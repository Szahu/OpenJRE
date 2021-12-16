package org.solar.appllication.terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.system.CallbackI.I;
import org.solar.engine.Utils;
import org.solar.engine.renderer.FloatArray;
import org.solar.engine.renderer.VertexArray;
import org.solar.engine.renderer.VertexData;

import static org.solar.appllication.terrain.Consts.*;

public class Chunk {
    private Vector2i offset = new Vector2i();
    private double[][][] grid;
    private double isolevel;
    private VertexArray vertexArray;
    public static final int CHUNK_SIZE = 10;
    public static final int CHUNK_HEIGHT = 30;
    public static final int CELL_SIZE = 8;
    private VertexData vData;
    private Map<Vector2f, Float> m_worldCoordsHMap = new HashMap<>();

    public Chunk(Vector2i inOffset, Function<Vector3f, ChunkData> generator, double isolevel) {
        this.offset = inOffset;
        ChunkData chData = generator.apply(new Vector3f(this.offset.x * CHUNK_SIZE * CELL_SIZE, 0 , this.offset.y * CHUNK_SIZE * CELL_SIZE));
        this.grid = chData.getGrid();
        this.isolevel = isolevel;
    }

    public Map<Vector2f, Float> getHeightMap() {
        return m_worldCoordsHMap;
    }

    public VertexArray getVertexArray() {return vertexArray;}
    public Vector2i getOffset() {return offset;}

    public void generate() {
        float[] vertices = calculateVertices();
        float[] normals = calculateNormals(vertices);
        int[] indices = new int[vertices.length/3];
        for(int i = 0;i < indices.length;i++) {
            indices[i] = i;
        }

        vData = new VertexData(indices, new FloatArray(3, vertices), new FloatArray(3, normals));
    }

    public void createVertexArray() {
        vertexArray = new VertexArray(vData);

        Iterator<Map.Entry<Vector2f, Float>> it = m_worldCoordsHMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Vector2f, Float> item = it.next();
            if (item.getKey().x == (offset.x * CHUNK_SIZE * CELL_SIZE) + CHUNK_SIZE * CELL_SIZE || item.getKey().y == (offset.y * CHUNK_SIZE * CELL_SIZE) + CHUNK_SIZE * CELL_SIZE) {
                it.remove();
            }
        }
    }

    private Vector3f interpolateVerts(double isoLevel, Vector3f v1, Vector3f v2, double val1, double val2) {
        float t = (float)((isoLevel - val1) / (val2 - val1)) ;
        v2.sub(v1);

        v2.mul((float)t);

        v1.add(v2);

        return v1;
    }

    public float[] calculateVertices() {
        int i,j,k;
        List<Triangle> triangles = new ArrayList<>();

        for(k = 0;k < grid.length - 1 ;k++) {
            for(j = 0;j < grid[k].length - 1;j++) {
                for(i = 0;i < grid[k][j].length - 1;i++) {


                    GridCell cell = new GridCell(new double[]{
                        grid[k]  [j ] [i ],
                        grid[k ]  [j ] [i+1 ],
                        grid[k+1 ][j ] [i+1 ],
                        grid[k+1 ][j ] [i ],
                    
                        grid[k ]  [j+1 ][i ], 
                        grid[k ]  [j+1 ][i+1 ],
                        grid[k+1 ][j+1 ][i+1 ],
                        grid[k+1 ][j+1 ][i ],
                    });

                    cell.points = new Vector3f[]{
                        new Vector3f((0.0f + i) * CELL_SIZE, (0.0f + j)*CELL_SIZE, (0.0f + k) * CELL_SIZE),
                        new Vector3f((1.0f + i) * CELL_SIZE, (0.0f + j)*CELL_SIZE, (0.0f + k) * CELL_SIZE),
                        new Vector3f((1.0f + i) * CELL_SIZE, (0.0f + j)*CELL_SIZE, (1.0f + k) * CELL_SIZE),
                        new Vector3f((0.0f + i) * CELL_SIZE, (0.0f + j)*CELL_SIZE, (1.0f + k) * CELL_SIZE),
                        new Vector3f((0.0f + i) * CELL_SIZE, (1.0f + j)*CELL_SIZE, (0.0f + k) * CELL_SIZE),
                        new Vector3f((1.0f + i) * CELL_SIZE, (1.0f + j)*CELL_SIZE, (0.0f + k) * CELL_SIZE),
                        new Vector3f((1.0f + i) * CELL_SIZE, (1.0f + j)*CELL_SIZE, (1.0f + k) * CELL_SIZE),
                        new Vector3f((0.0f + i) * CELL_SIZE, (1.0f + j)*CELL_SIZE, (1.0f + k) * CELL_SIZE),
                    };

                    polygonise(cell, isolevel, triangles);
    
                }
            }
        }

        List<Float> glVertices = new ArrayList<>();
        
        for(Triangle tri : triangles) {
            tri.copyToRawList(glVertices);
        }

        return Utils.floatListToArray(glVertices);
    }

    public Vector2f snapToGrid(Vector2f vec) {
        Vector2f gridWise = new Vector2f((int)(vec.x / CELL_SIZE), (int)(vec.y / CELL_SIZE));
        return gridWise.mul(CELL_SIZE);
    }

    public void polygonise(GridCell grid, double isolevel, List<Triangle> triangles) {

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
            
            Vector3f p1t = interpolateVerts(isolevel, new Vector3f(grid.points[a0]), new Vector3f(grid.points[b0]), grid.levels[a0], grid.levels[b0]);
            Vector3f p2t = interpolateVerts(isolevel, new Vector3f(grid.points[a1]), new Vector3f(grid.points[b1]), grid.levels[a1], grid.levels[b1]);
            Vector3f p3t = interpolateVerts(isolevel, new Vector3f(grid.points[a2]), new Vector3f(grid.points[b2]), grid.levels[a2], grid.levels[b2]);

            Vector3f vertexOffset = new Vector3f(offset.x * CHUNK_SIZE * CELL_SIZE, 0, offset.y * CHUNK_SIZE * CELL_SIZE);

            Vector3f finalV1 = new Vector3f(p1t).add(vertexOffset);
            Vector3f finalV2 = new Vector3f(p2t).add(vertexOffset);
            Vector3f finalV3 = new Vector3f(p3t).add(vertexOffset);

            Vector2f val1 = snapToGrid(new Vector2f(finalV1.x, finalV1.z));
            Vector2f val2 = snapToGrid(new Vector2f(finalV2.x, finalV2.z));
            Vector2f val3 = snapToGrid(new Vector2f(finalV3.x, finalV3.z));

            m_worldCoordsHMap.put(val1, finalV1.y);
            m_worldCoordsHMap.put(val2, finalV2.y);
            m_worldCoordsHMap.put(val3, finalV3.y);

            triangles.add(new Triangle(finalV1,finalV2,finalV3));
        }
    }

    private float[] calculateNormals(float[] glVertices) {

        List<Float> glNormals = new ArrayList<>();

        for(int i = 0;i < glVertices.length;i+=9) {
            Vector3f p1 = new Vector3f(glVertices[i], glVertices[i+1],glVertices[i+2]);
            Vector3f p2 = new Vector3f(glVertices[i+3], glVertices[i+4],glVertices[i+5]);
            Vector3f p3 = new Vector3f(glVertices[i+6], glVertices[i+7],glVertices[i+8]);

            Vector3f U = new Vector3f();
            Vector3f V = new Vector3f();
            p2.sub(p1, U);
            p3.sub(p1, V);

            Vector3f normal = new Vector3f();
            V.cross(U, normal);
            normal.normalize();
            
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

        return Utils.floatListToArray(glNormals);
    }
}

