package org.solar.engine.renderer;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;

import org.solar.engine.Event;
import org.solar.engine.Utils;
import org.solar.engine.Window;

/**
 * Wrapper for an OpenGL frame buffer with a multisampled texture.
 * @author Stanislaw Solarewicz
 */
public class MultiSampledFrameBuffer extends FrameBuffer {
    
    private int m_frameBufferId = -1;
    private int m_renderBufferId = -1;
    private int m_textureId = -1;

    @Override
    public int getId() {return m_frameBufferId;}

    public MultiSampledFrameBuffer(int samples) throws IOException {
        load();


        Event.addWindowResizeCallback((widht, height) -> {
            glViewport(0, 0, widht, height);
            load();
        });
    }

    private void load() {
        m_frameBufferId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, m_frameBufferId);

        // create a multisampled color attachment texture
        m_textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, m_textureId);
        glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, 4, GL_RGB, Window.getWidth(), Window.getHeight(), true);
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, m_textureId, 0);
        // create a (also multisampled) renderbuffer object for depth and stencil attachments
        m_renderBufferId = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, m_renderBufferId);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, 4, GL_DEPTH24_STENCIL8, Window.getWidth(), Window.getHeight());
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, m_renderBufferId);
    
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            Utils.LOG_ERROR("ERROR::FRAMEBUFFER:: Framebuffer is not complete!");
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

    }

    @Override
    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, m_frameBufferId);
    }

    @Override
    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }


}
