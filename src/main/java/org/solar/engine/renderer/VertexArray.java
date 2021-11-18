package org.solar.engine.renderer;

import org.lwjgl.system.MemoryUtil;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

public class VertexArray {

    private int             m_indexBufferId;
    private boolean         m_initialised           = false;
    private int             m_indexCount            = 0;
    private List<Integer>   m_floatBuffersIds       = new ArrayList<>();
    private static int      m_numberOfAttributes    = 0;
    private static int      m_vertexArrayId;

    public static int getNumberOfAttributes() { return m_numberOfAttributes; }

    public void initialise(int[] indices, float[] ...floatArrays) {

        if (!m_initialised) {
            m_vertexArrayId = glGenVertexArrays();
            glBindVertexArray(m_vertexArrayId);

            for(float[] array: floatArrays) {

                FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(array.length);
                verticesBuffer.put(array).flip();
                int vertexBufferId = glGenBuffers();
                glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
                glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);            
                glVertexAttribPointer(m_numberOfAttributes, 3, GL_FLOAT, false, 0, 0);
                glBindBuffer(GL_ARRAY_BUFFER, 0);
                memFree(verticesBuffer);

                m_floatBuffersIds.add(vertexBufferId);
                m_numberOfAttributes++;
            }

            m_indexCount = indices.length;
            m_indexBufferId = glGenBuffers();
            IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_indexBufferId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
            memFree(indicesBuffer);    

            m_initialised = true;
        }
    }

    public void initialise(int[] indices, VertexData vData) {

        m_floatBuffersIds = new ArrayList<>();

        if (!m_initialised) {
            m_vertexArrayId = glGenVertexArrays();
            glBindVertexArray(m_vertexArrayId);

            FloatBuffer floatData = MemoryUtil.memAllocFloat(vData.rawData.length);
            floatData.put(vData.rawData).flip();
            int vertexBufferId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
            glBufferData(GL_ARRAY_BUFFER, floatData, GL_STATIC_DRAW);        

            int stride = 0;
            for(FloatArray arr: vData.arrays) {
                glVertexAttribPointer(m_numberOfAttributes, arr.step, GL_FLOAT, false, vData.getSumStep() * Float.BYTES, (long) stride * Float.BYTES);
                stride += arr.step;
                m_numberOfAttributes++;
            }

            m_floatBuffersIds.add(vertexBufferId);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            memFree(floatData); 

            m_indexCount = indices.length;
            m_indexBufferId = glGenBuffers();
            IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_indexBufferId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
            memFree(indicesBuffer);    

            glBindVertexArray(0);

            m_initialised = true;
        }
    }

    public VertexArray(int[] indices, float[] ...floatArrays) {
        initialise(indices, floatArrays);
    }

    public VertexArray(int[] indices, VertexData vData) {
        initialise(indices, vData);
    }

    public void cleanup() {

        glDeleteBuffers(m_indexBufferId);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        for (Integer m_floatBuffersId : m_floatBuffersIds) {
            // Delete the VBOs
            glDeleteBuffers(m_floatBuffersId);
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(m_vertexArrayId);
    }

    public static void bind()           { glBindVertexArray(m_vertexArrayId); }
    public static void unbind()         { glBindVertexArray(0); }
    public         int getIndexCount()  { return m_indexCount; }

}
