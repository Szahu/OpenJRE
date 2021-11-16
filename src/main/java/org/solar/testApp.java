package org.solar;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.solar.engine.*;
import org.solar.engine.renderer.Renderer;
import org.solar.engine.renderer.Shader;
import org.solar.engine.renderer.VertexArray;
import imgui.ImGui;
import java.io.IOException;
import static org.lwjgl.glfw.GLFW.*;

public class testApp extends ApplicationTemplate {

	private Camera 		m_camera;
	private Shader 		m_testShader;
	private Transform 	m_testTransform;
	private VertexArray m_testVertexArray;
	private float[] vertices = new float[] {
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
	private int[] indices = new int[] {
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
	private float[] colours = new float[]{};

	public testApp( String inputObjFile ) throws IOException{
		super();
		vertices = Utils.getVertices( Utils.getStringFromFile( "src/main/resources/" + inputObjFile ) );
		indices  = Utils.getIndices( Utils.getStringFromFile( "src/main/resources/" + inputObjFile ) );
		Utils.LOG_INFO("input file: " + inputObjFile);
	}

	@Override
	public void initialise() throws IOException {

		//TODO make window class a singleton
		m_camera 			= new Camera(1024, 768);
		m_testShader 		= new Shader("testUniformShader.glsl");
		m_testTransform		= new Transform();
		m_testVertexArray	= new VertexArray(indices, vertices, colours);
		m_testShader		.setUniform("u_projectionMatrix", m_camera.getProjectionMatrix());

		Event.addWindowResizeCallback((width, height)-> {
			m_testShader.setUniform("u_projectionMatrix", m_camera.getProjectionMatrix());
		});

		Input.addKeyCallback(GLFW_KEY_SPACE, GLFW_PRESS, () -> {Utils.LOG("it works now");});
		Input.addKeyCallback(GLFW_KEY_ESCAPE, GLFW_RELEASE, Window::close);

	}

	@Override
	public void update() {

		this.m_testTransform.rotate(new Vector3f(0f,1f,0f));
		//this.m_testTransform.debugGui();
		this.m_camera.update();
		this.m_testShader.setUniform("u_viewMatrix", m_camera.getWorldMatrix());
		this.m_testShader.setUniform("u_worldMatrix", m_testTransform.getTransformMatrix());
		Renderer.render(m_testVertexArray, m_testShader);

	}

	@Override
	public void terminate() {
		m_testVertexArray.cleanup();
		m_testShader.cleanup();
	}
}
