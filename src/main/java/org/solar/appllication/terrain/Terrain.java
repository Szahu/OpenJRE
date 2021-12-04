package org.solar.appllication.terrain;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.CallbackI.J;
import org.lwjgl.system.CallbackI.Z;
import org.solar.engine.Utils;
import org.solar.engine.renderer.FloatArray;
import org.solar.engine.renderer.VertexArray;

import static org.solar.engine.Utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.solar.appllication.terrain.Consts.*;


public class Terrain {

    class Triangle {
        public Vector3f[] points;
    }

    private VertexArray m_vertexArray;
    public VertexArray getVertexArray() {return m_vertexArray;}


    private boolean compare(Vector4f p1, Vector4f p2) {
        if (p1.x > p2.x)
            return true;
        else if (p1.x < p2.x)
            return false;

        if (p1.y > p2.y)
            return true;
        else if (p1.y < p2.y)
            return false;

        if (p1.z > p2.z)
            return true;
        else if (p1.z < p2.z)
            return false;

        return false;
    }

    Vector3f interpolateVerts(double isoLevel, Vector3f v1, Vector3f v2, double val1, double val2) {
        float t = (float)((isoLevel - val1) / (val2 - val1)) ;
        v2.sub(v1);

        v2.mul((float)t);

        v1.add(v2);

        return v1;
    }

    public void createMesh(double[][][] grid, double isolevel) {
        List<Float> glVertices = new ArrayList<>();
        List<Integer> glIndices = new ArrayList<>();
        int i,j,k;
        for(k = 0;k < grid.length -1 ;k++) {
            for(j = 0;j < grid[k].length - 1;j++) {
                for(i = 0;i < grid[k][j].length - 1;i++) {


                    GridCell cell = new GridCell(new double[]{
                        grid[k]  [j] [i],
                        grid[k]  [j] [i+1],
                        grid[k+1][j] [i+1],
                        grid[k+1][j] [i],
                    
                        grid[k]  [j+1][i], 
                        grid[k]  [j+1][i+1],
                        grid[k+1][j+1][i+1],
                        grid[k+1][j+1][i],
                    });

                    cell.points = new Vector3f[]{
                        new Vector3f(0.0f + i, 0.0f + j, 0.0f + k),
                        new Vector3f(1.0f + i, 0.0f + j, 0.0f + k),
                        new Vector3f(1.0f + i, 0.0f + j, 1.0f + k),
                        new Vector3f(0.0f + i, 0.0f + j, 1.0f + k),
                        new Vector3f(0.0f + i, 1.0f + j, 0.0f + k),
                        new Vector3f(1.0f + i, 1.0f + j, 0.0f + k),
                        new Vector3f(1.0f + i, 1.0f + j, 1.0f + k),
                        new Vector3f(0.0f + i, 1.0f + j, 1.0f + k),
                    };

                    polygonise(cell, isolevel, glVertices);
    
                }
            }
        }
        for(int x = 0;x < glVertices.size()/3;x++) {
            glIndices.add(x);
        }

        List<Float> glNormals = new ArrayList<>();

        for(i = 0;i < glVertices.size();i+=9) {
            Vector3f p1 = new Vector3f(glVertices.get(i), glVertices.get(i+1),glVertices.get(i+2));
            Vector3f p2 = new Vector3f(glVertices.get(i+3), glVertices.get(i+4),glVertices.get(i+5));
            Vector3f p3 = new Vector3f(glVertices.get(i+6), glVertices.get(i+7),glVertices.get(i+8));

            Vector3f U = new Vector3f();
            Vector3f V = new Vector3f();
            p2.sub(p1, U);
            p3.sub(p1, V);

            Vector3f normal = new Vector3f();
            U.cross(V, normal);
            glNormals.add(normal.x);
            glNormals.add(normal.y);
            glNormals.add(normal.z);

            glNormals.add(normal.x);
            glNormals.add(normal.y);
            glNormals.add(normal.z);

            glNormals.add(normal.x);
            glNormals.add(normal.y);
            glNormals.add(normal.z);
        }

        m_vertexArray = new VertexArray(Utils.intListToArray(glIndices), new FloatArray(3, Utils.floatListToArray(glVertices)), new FloatArray(3, Utils.floatListToArray(glNormals)));
    }

    private Vector3f calcMiddle(Vector3f vec1, Vector3f vec2) {
        return new Vector3f(vec1.add(vec2)).mul(0.5f);
    }

    public void polygonise(GridCell grid, double isolevel, List<Float> glVertices) {

        int cubeindex;

        /*
        Determine the index into the edge table which
        tells us which vertices are inside of the surface.
        */
        cubeindex = 0;
        if (grid.levels[0] < isolevel) cubeindex |= 1;
        if (grid.levels[1] < isolevel) cubeindex |= 2;
        if (grid.levels[2] < isolevel) cubeindex |= 4;
        if (grid.levels[3] < isolevel) cubeindex |= 8;
        if (grid.levels[4] < isolevel) cubeindex |= 16;
        if (grid.levels[5] < isolevel) cubeindex |= 32;
        if (grid.levels[6] < isolevel) cubeindex |= 64;
        if (grid.levels[7] < isolevel) cubeindex |= 128;

        // Create triangles for current cube configuration
        for (int i = 0; i < TRIANGULATE_TABLE[cubeindex].length; i +=3) {
            // Get indices of corner points A and B for each of the three edges
            // of the cube that need to be joined to form the triangle.
            int a0 = cornerIndexAFromEdge[TRIANGULATE_TABLE[cubeindex][i]];
            int b0 = cornerIndexBFromEdge[TRIANGULATE_TABLE[cubeindex][i]];

            int a1 = cornerIndexAFromEdge[TRIANGULATE_TABLE[cubeindex][i+1]];
            int b1 = cornerIndexBFromEdge[TRIANGULATE_TABLE[cubeindex][i+1]];

            int a2 = cornerIndexAFromEdge[TRIANGULATE_TABLE[cubeindex][i+2]];
            int b2 = cornerIndexBFromEdge[TRIANGULATE_TABLE[cubeindex][i+2]];

            /* Vector3f p1t = calcMiddle(grid.points[a0], grid.points[b0]);
            Vector3f p2t = calcMiddle(grid.points[a1], grid.points[b1]);
            Vector3f p3t = calcMiddle(grid.points[a2], grid.points[b2]); */
            
            Vector3f p1t = interpolateVerts(isolevel, new Vector3f(grid.points[a0]), new Vector3f(grid.points[b0]), grid.levels[a0], grid.levels[b0]);
            Vector3f p2t = interpolateVerts(isolevel, new Vector3f(grid.points[a1]), new Vector3f(grid.points[b1]), grid.levels[a1], grid.levels[b1]);
            Vector3f p3t = interpolateVerts(isolevel, new Vector3f(grid.points[a2]), new Vector3f(grid.points[b2]), grid.levels[a2], grid.levels[b2]);

            glVertices.add(p1t.x);
            glVertices.add(p1t.y);
            glVertices.add(p1t.z);

            glVertices.add(p2t.x);
            glVertices.add(p2t.y);
            glVertices.add(p2t.z);

            glVertices.add(p3t.x);
            glVertices.add(p3t.y);
            glVertices.add(p3t.z);

        }
    }

}