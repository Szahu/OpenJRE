package org.solar.appllication.terrain;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.CallbackI.J;
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

    Vector3f LinearInterp(Vector4f p1, Vector4f p2, float value)
    {
        if (compare(p1, p2))
        {
            Vector4f temp;
            temp = p1;
            p1 = p2;
            p2 = temp;    
        }

        Vector4f p;
        if((p1.w - p2.w) > 0.00001)
            p = p1.add(p2.sub(p1)).div((p2.w - p1.w)*(value - p1.w));
        else 
            p = p1;
        return p;
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

                    int[] indices = polygonise(cell, isolevel);

                    for(int x = 0;x < indices.length;x++) {

                        glVertices.add(CUBE_EDGE_VERTICES[3 * indices[x]] + i);
                        glVertices.add(CUBE_EDGE_VERTICES[3 * indices[x]+1] + j);
                        glVertices.add(CUBE_EDGE_VERTICES[3 * indices[x]+2] + k);
                        
                    }
    
                }
            }
        }
        for(int x = 0;x < glVertices.size()/3;x++) {
            glIndices.add(x);
        }
        m_vertexArray = new VertexArray(Utils.intListToArray(glIndices), new FloatArray(3, Utils.floatListToArray(glVertices)));
    }

    public int[] polygonise(GridCell grid, double isolevel) {

        int ntriang = 0;
        int cubeindex;
        Vector3f vertList[] = new Vector3f[12];

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

        /* Cube is entirely in/out of the surface */
        if (cubeindex == 0)
            return(new int[]{});

        return TRIANGULATE_TABLE[cubeindex];
    }

}