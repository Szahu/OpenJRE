package org.solar.engine.renderer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import org.solar.engine.Camera;
import org.solar.engine.Utils;

public class Renderer {

    private static Camera m_CameraInstance;

    private Renderer() {}

    public static void initialise() {
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS); 
        Utils.LOG_INFO("OpenGL version: " + glGetString(GL_VERSION));
    }   

    public static void setCameraRefrence(Camera cam) {m_CameraInstance = cam;}

    public static void setClearColor(Vector3f color) {
        glClearColor(color.x, color.y, color.z, 1.0f);
    }

    public static void render(Mesh mesh, Shader shader) {
        
        shader.bind();

        glBindVertexArray(mesh.getVertexArrayId());
        glEnableVertexAttribArray(0);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

        // Restore state
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        shader.unbind();

    }

    public static void render(VertexArray vao, Shader shader) {
        
        shader.bind();

        VertexArray.bind();

        for(int i = 0;i < VertexArray.getNumberOfAttributes(); i++) {
            glEnableVertexAttribArray(i);
        }

        glDrawElements(GL_TRIANGLES, vao.getIndexCount(), GL_UNSIGNED_INT, 0);

        for(int i = 0;i < VertexArray.getNumberOfAttributes(); i++) {
            glEnableVertexAttribArray(i);
        }

        // Restore state
        VertexArray.unbind();

        shader.unbind();
    }

}
