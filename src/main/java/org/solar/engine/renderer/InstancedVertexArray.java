package org.solar.engine.renderer;

import org.lwjgl.system.MemoryUtil;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.*;

public class InstancedVertexArray {

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

  /*   public InstancedVertexArray(VertexData vData) {
        initialise(vData.indices, vData);
    } */

    public InstancedVertexArray(int[] indices, FloatArray ...floatArrays) {

        FloatArray[] proeprArrs = new FloatArray[floatArrays.length - 1];
        System.arraycopy(floatArrays, 0, proeprArrs, 0, floatArrays.length - 1);
        initialise(indices, new FloatArray(3, floatArrays[floatArrays.length - 1].data), new VertexData(proeprArrs));
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

    public void updateData(float[] data, int offset) {
        glBindVertexArray(m_vertexArrayId);
        FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(data.length);
        verticesBuffer.put(data).flip();
        glBufferSubData(GL_ARRAY_BUFFER, offset, data);
        glBindVertexArray(0);
    }

    /* public void initialise(int[] indices, float[] ...floatArrays) {

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
    } */

    public void initialise(int[] indices, FloatArray instanced, VertexData vData) {

        m_floatBuffersIds = new ArrayList<>();

        if (!m_initialised) {


            //create and bind vertex array
            m_vertexArrayId = glGenVertexArrays();
            glBindVertexArray(m_vertexArrayId);


            //Deal with normal data works fine
            FloatBuffer floatData = MemoryUtil.memAllocFloat(vData.rawData.length);
            floatData.put(vData.rawData).flip();
            int vertexBufferId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
            glBufferData(GL_ARRAY_BUFFER, floatData, GL_STATIC_DRAW);        

            int stride = 0;
            for(FloatArray arr: vData.arrays) {
                glEnableVertexAttribArray(m_numberOfAttributes);
                glVertexAttribPointer(m_numberOfAttributes, arr.step, GL_FLOAT, false, vData.getSumStep() * Float.BYTES, (long) stride * Float.BYTES);
                stride += arr.step;
                m_numberOfAttributes++;
            }

            m_floatBuffersIds.add(vertexBufferId);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            memFree(floatData); 

            //THIS IS PROBLEMATIC
            FloatBuffer instancedData = MemoryUtil.memAllocFloat(instanced.data.length);
            instancedData.put(instanced.data).flip();
            int instancedDataId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, instancedDataId);
            glBufferData(GL_ARRAY_BUFFER, instancedData, GL_STATIC_DRAW);  
            glEnableVertexAttribArray(m_numberOfAttributes);
            
            glVertexAttribPointer(m_numberOfAttributes, instanced.step, GL_FLOAT, false, instanced.step * Float.BYTES, 0);

            glVertexAttribDivisor(m_numberOfAttributes, 1);  
            m_numberOfAttributes++;

            m_floatBuffersIds.add(instancedDataId);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            memFree(instancedData); 

            
            //Index buffer works fine
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
