package org.solar.engine.renderer;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import org.solar.engine.Event;
import org.solar.engine.Utils;
import org.solar.engine.Window;

import java.io.IOException;

/**
 * An OpenGL frame buffer wrapper class. 
 * @author Stanislaw Solarewicz 
 */
public class FrameBuffer {

    private int m_frameBufferId = -1;
    private int m_textureId = -1;
    private int m_depthStencilBufferId = -1;

    /**
     * Returns the od the frame buffer.
     * @return Id of the frame buffer.
     */
    public int getId() {return m_frameBufferId;}

    /**
     * Binds the texture of the frame buffer and sets active texture to 0.
     */
    public void bindTexture() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, m_textureId);
    }

    /**
     * Unbinds the texture of the frame buffer.
     */
    public void unbindTexture() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Creates the frame buffer. 
     * @throws IOException
     */
    public FrameBuffer() throws IOException {
        load();

        Event.addWindowResizeCallback((widht, height) -> {
            glViewport(0, 0, widht, height);
            load();
        });

    }

    private void load() {
        m_frameBufferId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, m_frameBufferId);

        m_depthStencilBufferId = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, m_depthStencilBufferId);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, Window.getWidth(), Window.getHeight());
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, m_depthStencilBufferId);  

        m_textureId = glGenTextures();
	    glBindTexture(GL_TEXTURE_2D, m_textureId);
	    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, Window.getWidth(), Window.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, (java.nio.ByteBuffer)null);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        glFramebufferTexture2D(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, m_textureId, 0);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
			Utils.LOG_ERROR("ERROR couldn't create a frameBuffer");
		}

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    /**
     * Binds the frame buffer.
     */
    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, m_frameBufferId);
    }

    /**
     * Unbinds the frame buffer. 
     */
    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    } 

    /**
     * Delets the frame buffer. 
     */
    public void cleanup() {
        glDeleteBuffers(m_frameBufferId);
    }

}
