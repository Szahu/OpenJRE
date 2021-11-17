package org.solar;

import org.joml.Vector3f;
import org.solar.engine.*;
import org.solar.engine.renderer.FloatArray;
import org.solar.engine.renderer.Renderer;
import org.solar.engine.renderer.Shader;
import org.solar.engine.renderer.Texture;
import org.solar.engine.renderer.VertexArray;
import org.solar.engine.renderer.VertexData;
import imgui.ImGui;
import java.io.IOException;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.glfw.GLFW.*;

public class testApp extends ApplicationTemplate {

	private Camera 		m_camera;
	private Shader 		m_testShader;
	private Transform 	m_testTransform;
	private Texture 	m_texture;
	private VertexArray m_testVertexArray;
	private float[] 	vertices = new float[] {
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
	private int[] indices = new int[]{
			// Front face
			0, 1, 3, 3, 1, 2,
			// Top Face
			8, 10, 11, 9, 8, 11,
			// Right face
			12, 13, 7, 5, 12, 7,
			// Left face
			14, 15, 6, 4, 14, 6,
			// Bottom face
			16, 18, 19, 17, 16, 19,
			// Back face
			4, 6, 7, 5, 4, 7,};
	//private float[] colours = new float[]{};
	private float[] positions = new float[]{
			// V0
			-0.5f, 0.5f, 0.5f,
			// V1
			-0.5f, -0.5f, 0.5f,
			// V2
			0.5f, -0.5f, 0.5f,
			// V3
			0.5f, 0.5f, 0.5f,
			// V4
			-0.5f, 0.5f, -0.5f,
			// V5
			0.5f, 0.5f, -0.5f,
			// V6
			-0.5f, -0.5f, -0.5f,
			// V7
			0.5f, -0.5f, -0.5f,
			// For text coords in top face
			// V8: V4 repeated
			-0.5f, 0.5f, -0.5f,
			// V9: V5 repeated
			0.5f, 0.5f, -0.5f,
			// V10: V0 repeated
			-0.5f, 0.5f, 0.5f,
			// V11: V3 repeated
			0.5f, 0.5f, 0.5f,
			// For text coords in right face
			// V12: V3 repeated
			0.5f, 0.5f, 0.5f,
			// V13: V2 repeated
			0.5f, -0.5f, 0.5f,
			// For text coords in left face
			// V14: V0 repeated
			-0.5f, 0.5f, 0.5f,
			// V15: V1 repeated
			-0.5f, -0.5f, 0.5f,
			// For text coords in bottom face
			// V16: V6 repeated
			-0.5f, -0.5f, -0.5f,
			// V17: V7 repeated
			0.5f, -0.5f, -0.5f,
			// V18: V1 repeated
			-0.5f, -0.5f, 0.5f,
			// V19: V2 repeated
			0.5f, -0.5f, 0.5f,};
	private float[] textCoords = new float[]{
			0.0f, 0.0f,
			0.0f, 0.5f,
			0.5f, 0.5f,
			0.5f, 0.0f,
			0.0f, 0.0f,
			0.5f, 0.0f,
			0.0f, 0.5f,
			0.5f, 0.5f,
			// For text coords in top face
			0.0f, 0.5f,
			0.5f, 0.5f,
			0.0f, 1.0f,
			0.5f, 1.0f,
			// For text coords in right face
			0.0f, 0.0f,
			0.0f, 0.5f,
			// For text coords in left face
			0.5f, 0.0f,
			0.5f, 0.5f,
			// For text coords in bottom face
			0.5f, 0.0f,
			1.0f, 0.0f,
			0.5f, 0.5f,
			1.0f, 0.5f
	};

	public testApp(){
		super();
	};

	public testApp( String inputObjFile ) throws IOException{
		vertices = Utils.getVertices( Utils.getWholeFileAsString( "src/main/resources/" + inputObjFile ) );
		indices  = Utils.getIndices( Utils.getWholeFileAsString( "src/main/resources/" + inputObjFile ) );
		Utils.LOG_INFO("input file: " + inputObjFile);
	}

	@Override
	public void initialise() throws IOException {

		//TODO make window class a singleton
		m_camera 			= new Camera(1024, 768);


		m_testShader 		= new Shader("testTextureShader.glsl"); //was - testUniformShader.glsl
		m_testShader		.bind();
		m_testShader		.setUniform("u_projectionMatrix", m_camera.getProjectionMatrix());
		m_testShader		.unbind();

		m_testTransform		= new Transform();
		//m_testVertexArray	= new VertexArray(indices, vertices, colours);

		m_testShader		.unbind();

		Event.addWindowResizeCallback((width, height)-> {
			m_testShader.bind();
			m_testShader.setUniform("u_projectionMatrix", m_camera.getProjectionMatrix());
			m_testShader.unbind();
		});

		m_testVertexArray = new VertexArray(indices, new VertexData(new FloatArray(3, positions),  new FloatArray(2, textCoords)));
		m_texture = new Texture("src/main/resources/texture_0001.png");

		Input.addKeyCallback(GLFW_KEY_SPACE, GLFW_PRESS, () -> {Utils.LOG("it works now");});
		Input.addKeyCallback(GLFW_KEY_ESCAPE, GLFW_RELEASE, Window::close);

	}

	@Override
	public void update() {
		ImGui.text("Hello world!");
		Renderer.setClearColor(new Vector3f(77f/255f, 200f/255f, 233f/255f));

		m_testShader.bind();
		m_testShader.setUniform("u_texture_sampler", 0);
		this.m_testShader.setUniform("u_viewMatrix", m_camera.getViewMatrix());
		this.m_testShader.setUniform("u_worldMatrix", m_testTransform.getTransformMatrix());
		// Activate first texture unit
		glActiveTexture(GL_TEXTURE0);
		// Bind the texture
		glBindTexture(GL_TEXTURE_2D, m_texture.getTextureId());
		Renderer.render(m_testVertexArray, m_testShader);
		m_testShader.unbind();

		m_camera.update();

		m_testTransform.debugGui("test Transform");
	}

	@Override
	public void terminate() {
		m_testVertexArray.cleanup();
		m_testShader.cleanup();
	}
}
