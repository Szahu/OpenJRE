package org.solar.appllication.terrain;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.system.CallbackI.J;
import org.lwjgl.system.CallbackI.Z;
import org.solar.engine.Utils;
import org.solar.engine.renderer.FloatArray;
import org.solar.engine.renderer.VertexArray;

import static org.solar.engine.Utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static org.solar.appllication.terrain.Consts.*;


public class Terrain {

    private VertexArray m_vertexArray;
    public VertexArray getVertexArray() {return m_vertexArray;}

  
    public void createMesh(double isolevel, Function<Vector3f, double[][][]> generator, Vector3f offset) {
        List<Integer> glIndices = new ArrayList<>();

        /* Chunk testChunk1 = new Chunk(new Vector3f(-Chunk.CHUNK_SIZE, 0, -Chunk.CHUNK_SIZE), generator, isolevel);
        float[] vertices1 = testChunk1.calculateVertices();
        Chunk testChunk2 = new Chunk(new Vector3f(0, 0, -Chunk.CHUNK_SIZE), generator, isolevel);
        float[] vertices2 = testChunk2.calculateVertices();

        Chunk testChunk3 = new Chunk(new Vector3f(-Chunk.CHUNK_SIZE, 0, 0), generator, isolevel);
        float[] vertices3 = testChunk3.calculateVertices();
        Chunk testChunk4 = new Chunk(new Vector3f(0, 0, 0), generator, isolevel);
        float[] vertices4 = testChunk4.calculateVertices();

        float[] res = concat(concat(concat(vertices1, vertices2), vertices3), vertices4); */
        Chunk testChunk = new Chunk(offset.mul(Chunk.CHUNK_SIZE), generator, isolevel);
        float[] res = testChunk.calculateVertices();
        for(int x = 0;x < res.length / 3;x++) {
            glIndices.add(x);
        }

        m_vertexArray = new VertexArray(Utils.intListToArray(glIndices), new FloatArray(3, res), new FloatArray(3, calculateNormals(res)));
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