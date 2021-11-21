package org.solar.engine.renderer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import org.solar.engine.Camera;
import org.solar.engine.Utils;
import org.solar.engine.Window;

import java.io.IOException;

public class Renderer {

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

    private static VertexArray m_screenVertexArray; 
    private static Camera m_CameraRefrence;
    private static FrameBuffer m_multiSampleFrameBuffer;
    private static FrameBuffer m_frameBuffer;
    private static Vector3f m_clearColor;
    private static Shader m_screenShader;

    private Renderer() {}

    public static FrameBuffer getFrameBuffer() {return m_frameBuffer;}

    public static void initialise() throws IOException {
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS); 
        Utils.LOG_INFO("OpenGL version: " + glGetString(GL_VERSION));
        m_frameBuffer = new FrameBuffer();
        m_multiSampleFrameBuffer = new MultiSampledFrameBuffer(8);
        m_screenVertexArray = new VertexArray(virtualScreenIndices, new FloatArray(2, virtalScreenVertices), new FloatArray(2, virtualScreenTextureCoordinates));
        m_screenShader = new Shader("screenFrameBufferShader.glsl");
        m_clearColor = new Vector3f(0,0,0);
    }   

    public static void setCameraRefrence(Camera cam) {m_CameraRefrence = cam;}

    public static void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); 
    }

    public static void setClearColor(Vector3f newColor) {m_clearColor = newColor;}

    public static void render(VertexArray vao, Shader shader) {
        
        shader.bind();

        shader.setUniform("u_viewMatrix", m_CameraRefrence.getViewMatrix());

        vao.bind();

        for(int i = 0;i < vao.getNumberOfAttributes(); i++) {
            glEnableVertexAttribArray(i);
        }

        glDrawElements(GL_TRIANGLES, vao.getIndexCount(), GL_UNSIGNED_INT, 0);

        // Restore state
        vao.unbind();

        shader.unbind();
    }

    public static void render(VertexArray vao, Shader shader, org.solar.engine.Transform transform) {
        
        shader.bind();

        shader.setUniform(Shader.uniformViewMatrixToken, m_CameraRefrence.getViewMatrix());
        shader.setUniform(Shader.uniformTransformMatrixToken, transform.getTransformMatrix());

        vao.bind();

        for(int i = 0;i < vao.getNumberOfAttributes(); i++) {
            glEnableVertexAttribArray(i);
        }

        glDrawElements(GL_TRIANGLES, vao.getIndexCount(), GL_UNSIGNED_INT, 0);

        // Restore state
        vao.unbind();

        shader.unbind();
    }

    public static void renderToScreen(Runnable drawScene) {
        
        m_multiSampleFrameBuffer.bind();
        glClearColor(m_clearColor.x, m_clearColor.y, m_clearColor.z, 1.0f);
        clear();
        glEnable(GL_DEPTH_TEST);
        
        drawScene.run();

        glBindFramebuffer(GL_READ_FRAMEBUFFER, m_multiSampleFrameBuffer.getId());
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, m_frameBuffer.getId());
        glBlitFramebuffer(0, 0, Window.getWidth(), Window.getHeight(), 0, 0, Window.getWidth(),  Window.getHeight(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
        
        m_multiSampleFrameBuffer.unbind();
        glClearColor(m_clearColor.x, m_clearColor.y, m_clearColor.z, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);

        m_screenVertexArray.bind();
        m_screenShader.bind();
        m_screenShader.setUniform("u_texture_sampler", 0);
        m_frameBuffer.bindTexture();

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawElements(GL_TRIANGLES,  m_screenVertexArray.getIndexCount(), GL_UNSIGNED_INT, 0);
        m_screenShader.unbind();
        m_screenVertexArray.unbind();

    }

}
