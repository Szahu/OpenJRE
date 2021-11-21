package org.solar.engine.renderer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import org.joml.Vector3f;
import org.solar.engine.Camera;
import org.solar.engine.Transform;
import org.solar.engine.Utils;

import static org.lwjgl.opengl.GL20.*;

public class RenderUtils {

    private static VertexArray m_workspaceGridArray;
    private static int m_worspaceGridSize = 20;
    private static float m_worspaceGridElementSize = 20;
    private static Shader m_gridShader;
    private static Transform m_gridTransform = new Transform();

    public static void initialises() throws IOException {

        Vector<Float> gridVertices = new Vector<>();
        Vector<Integer> indices = new Vector<>();

        int i,j;
        for(j = 0;j <= m_worspaceGridSize;++j) {
            for(i = 0;i <= m_worspaceGridSize;++i) {
                float x = (float)i;
                float y = 0;
                float z = (float)j;
                gridVertices.add(x);
                gridVertices.add(y);
                gridVertices.add(z);
            }
        }

        for(j = 0;j < m_worspaceGridSize;++j) {
            for(i = 0;i < m_worspaceGridSize;++i) {

                int row1 = j * (m_worspaceGridSize+1);
                int row2 = (j+1) * (m_worspaceGridSize+1);
                
                //indices.push_back(glm::uvec4(row1+i, row1+i+1, row1+i+1, row2+i+1));
                indices.add(row1+i);
                indices.add(row1+i+1);
                indices.add(row1+i+1);
                indices.add(row2+i+1);
                //indices.push_back(glm::uvec4(row2+i+1, row2+i, row2+i, row1+i));
                indices.add(row2+i+1);
                indices.add(row2+i);
                indices.add(row2+i);
                indices.add(row1+i);
            }
        }


        m_workspaceGridArray = new VertexArray(Utils.intVectorToArray(indices), new FloatArray(3, Utils.floatVectorToArray(gridVertices)));
        m_gridShader = new Shader("gridShader.glsl");
        m_gridTransform.setPosition(new Vector3f(-1 * m_worspaceGridSize / 2, 0, -1 * m_worspaceGridSize / 2));
    }

    public static void renderGrid(Camera cam) {

        m_gridShader.bind();
        m_gridShader.setUniform(Shader.uniformProjectionMatrixToken, cam.getProjectionMatrix());
        m_gridShader.setUniform(Shader.uniformViewMatrixToken, cam.getViewMatrix());
        m_gridShader.setUniform(Shader.uniformTransformMatrixToken, m_gridTransform.getTransformMatrix());
        m_workspaceGridArray.bind();
        glEnableVertexAttribArray(0);

        glDrawElements(GL_LINES, m_workspaceGridArray.getIndexCount(), GL_UNSIGNED_INT, 0);

        m_workspaceGridArray.unbind();

    }
    
}
