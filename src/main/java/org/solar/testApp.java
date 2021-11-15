package org.solar;

import org.solar.engine.*;

import org.solar.engine.renderer.Renderer;
import org.solar.engine.renderer.Shader;
import org.solar.engine.renderer.VertexArray;
import static org.lwjgl.glfw.GLFW.*;

import imgui.ImGui;


public class testApp extends ApplicationTemplate {
    
    private Camera m_camera;
    private Shader m_testShader;
    private Transform m_testTransform;
    private VertexArray m_testVertexArray;

    @Override
    public void initialise() {
        
		//Our data to render
		float[] vertices = new float[] {
			// VO
			-0.5f,  0.5f,  0.5f,
			// V1
			-0.5f, -0.5f,  0.5f,
			// V2
			0.5f, -0.5f,  0.5f,
			// V3
			 0.5f,  0.5f,  0.5f,
			// V4
			-0.5f,  0.5f, -0.5f,
			// V5
			 0.5f,  0.5f, -0.5f,
			// V6
			-0.5f, -0.5f, -0.5f,
			// V7
			 0.5f, -0.5f, -0.5f,
		};

		int[] indices = new int[] {
			// Front face
			0, 1, 3, 3, 1, 2,
			// Top Face
			4, 0, 3, 5, 4, 3,
			// Right face
			3, 2, 7, 5, 3, 7,
			// Left face
			6, 1, 0, 6, 0, 4,
			// Bottom face
			2, 1, 6, 2, 6, 7,
			// Back face
			7, 6, 4, 7, 4, 5,
		};

		float[] colours = new float[]{
			0.5f, 0.0f, 0.0f,
			0.0f, 0.5f, 0.0f,
			0.0f, 0.0f, 0.5f,
			0.0f, 0.5f, 0.5f,
			0.5f, 0.0f, 0.0f,
			0.0f, 0.5f, 0.0f,
			0.0f, 0.0f, 0.5f,
			0.0f, 0.5f, 0.5f,
		};

        //TODO make window class a singleton
		m_camera = new Camera(1024, 768);

		m_testShader = new Shader("testUniformShader.glsl");
		m_testShader.setUniform("u_projectionMatrix", m_camera.getProjectionMatrix());

		m_testTransform = new Transform();

		Event.addWindowResizeCallback((width, height)-> {
			m_testShader.setUniform("u_projectionMatrix", m_camera.getProjectionMatrix());
		}); 

		m_testVertexArray = new VertexArray(indices, vertices, colours);

		Input.addKeyCallback(GLFW_KEY_SPACE, GLFW_PRESS, () -> {Utils.LOG("it works now");});
    }

    @Override
    public void update() {

        m_testShader.setUniform("u_viewMatrix", m_camera.getWorldMatrix());
        m_testShader.setUniform("u_worldMatrix", m_testTransform.getTransformMatrix());
        Renderer.render(m_testVertexArray, m_testShader);

        m_camera.update();
			
        ImGui.text("Hello world!");
        m_testTransform.debugGui();
    }

    @Override
    public void terminate() {
        m_testVertexArray.cleanup();
		m_testShader.cleanup();
    }
}
