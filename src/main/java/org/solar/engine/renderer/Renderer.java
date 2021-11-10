package org.solar.engine.renderer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {
    
    private Renderer() {}

    public static void initialise() {

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

}
