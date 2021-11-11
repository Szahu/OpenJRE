package org.solar.engine.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {

    private float[] m_vertices;
    private int[] m_indices;

    private int m_vertexArrayId;
    private int m_vertexBufferId;
    private int m_indexBufferId;
    private boolean m_initialised = false;

    public int getVertexArrayId() {return m_vertexArrayId;}

    private void glInit() {
        if (!m_initialised) {

            m_vertexArrayId = glGenVertexArrays();
            glBindVertexArray(m_vertexArrayId);


            FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(m_vertices.length);
            verticesBuffer.put(m_vertices).flip();
            m_vertexBufferId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, m_vertexBufferId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);            
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            memFree(verticesBuffer);

            m_indexBufferId = glGenBuffers();
            IntBuffer indicesBuffer = MemoryUtil.memAllocInt(m_indices.length);
		    indicesBuffer.put(m_indices).flip();
		    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_indexBufferId);
		    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
		    memFree(indicesBuffer);

            m_initialised = true;
        }

        else {
            System.out.println("Mesh already initialised!");
        }
        
    }

    public Mesh(float[] verticse, int[] indices) {
        m_vertices = verticse;
        m_indices = indices;

        glInit();
    }

    //Empty constructor in case somebodies wants to lead in a certain point
    public Mesh() { }

    public void load(float[] verticse, int[] indices) {
        m_vertices = verticse;
        m_indices = indices;

        glInit();
    }

    public void cleanup() {
        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(m_vertexBufferId);
        glDeleteBuffers(m_indexBufferId);

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(m_vertexArrayId);
    }
    
}
