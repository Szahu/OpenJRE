package org.solar.engine.renderer;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;


import org.lwjgl.opengl.GL;
import org.solar.engine.Event;
import org.solar.engine.Utils;
import org.solar.engine.Window;

import java.io.IOException;

public class FrameBuffer {

    private int m_frameBufferId = -1;
    private int m_textureId = -1;
    private static Shader m_shader;
    private int m_depthStencilBufferId = -1;

    private static final float[] virtalScreenVertices = {
        -1.0f, -1.0f,
        -1.0f,  1.0f,
         1.0f,  1.0f,
         1.0f, -1.0f
    };

    private static final float[] virtualScreenTextureCoordinates = {
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f
    };

    private static final int[] virtualScreenIndices = {0,1,2,2,0,3};

    private static final VertexArray m_vertexarray = new VertexArray(virtualScreenIndices, new FloatArray(2, virtalScreenVertices), new FloatArray(2, virtualScreenTextureCoordinates));

    public static VertexArray getVertexArray() {return m_vertexarray;}
    public static Shader getShader() {return m_shader;}


    public void bindTexture() {
        glBindTexture(GL_TEXTURE_2D, m_textureId);
        glActiveTexture(GL_TEXTURE0);
    }

    public void unBindTexture() {
        glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE0);
    }

    public FrameBuffer() throws IOException {
        load();

        m_shader = new Shader("frameBufferShader.glsl");

        Event.addWindowResizeCallback((widht, height) -> {
            glViewport(0, 0, widht, height);
            load();
        });

    }

    private void load() {
        m_frameBufferId = glGenFramebuffersEXT();
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, m_frameBufferId);

        m_depthStencilBufferId = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, m_depthStencilBufferId);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, Window.getWidth(), Window.getHeight());
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, m_depthStencilBufferId);  

        m_textureId = glGenTextures();
	    glBindTexture(GL_TEXTURE_2D, m_textureId);
	    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, Window.getWidth(), Window.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, (java.nio.ByteBuffer)null);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, m_textureId, 0);

        if(glCheckFramebufferStatusEXT(GL_FRAMEBUFFER_EXT) != GL_FRAMEBUFFER_COMPLETE_EXT){
			Utils.LOG_ERROR("ERROR couldn't create a frameBuffer");
		}

        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    }

    public void bind() {
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, m_frameBufferId);
    }

    public void unbind() {
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    } 

    public void cleanup() {
        glDeleteBuffers(m_frameBufferId);
    }

}
