package org.solar.engine.renderer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import org.solar.engine.Camera;
import org.solar.engine.Utils;

public class Renderer {

    private static Camera m_CameraRefrence;

    private Renderer() {}

    public static void initialise() {
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS); 
        Utils.LOG_INFO("OpenGL version: " + glGetString(GL_VERSION));
    }   

    public static void setCameraRefrence(Camera cam) {m_CameraRefrence = cam;}

    public static void setClearColor(Vector3f color) {
        glClearColor(color.x, color.y, color.z, 1.0f);
    }

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

}
