package org.solar.engine.renderer;

import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.system.MemoryUtil.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class VertexArray {
    
    private boolean m_initialised = false;

    private int m_vertexArrayId;
    private int m_indexBufferId;
    private List<Integer> m_floatBuffersIds;
    private int m_numberOfAttributes = 0;

    private int m_indexCount = 0;
    public int getIndexCount() {return m_indexCount;}

    public int getNumberOfAttributes() { return m_numberOfAttributes; }

    //public VertexArray() {}

    /* public VertexArray(int[] indices, float[] ...floatArrays) {
        initialise(indices, floatArrays);
    } */

    public VertexArray(int[] indices, VertexData vData) {
        initialise(indices, vData);
    }

    public VertexArray(int[] indices, FloatArray ...floatArrays) {
        initialise(indices, new VertexData(floatArrays));
    }

    public void bind()  {
        glBindVertexArray(m_vertexArrayId);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public void cleanup() {

        glDeleteBuffers(m_indexBufferId);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        for(int i = 0;i < m_floatBuffersIds.size();i++) {
            // Delete the VBOs
        glDeleteBuffers(m_floatBuffersIds.get(i));        
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(m_vertexArrayId);
    }

    
    public void initialise(int[] indices, float[] ...floatArrays) {

        m_floatBuffersIds = new ArrayList<>();

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
                glVertexAttribPointer(m_numberOfAttributes, arr.step, GL_FLOAT, false, vData.getSumStep() * Float.BYTES, stride * Float.BYTES);
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


}
