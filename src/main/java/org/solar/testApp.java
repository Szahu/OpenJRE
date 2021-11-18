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
import static org.solar.engine.Utils.*;

public class testApp extends ApplicationTemplate {

	private Camera 		m_camera;
	private Shader 		m_testShader;
	private Transform 	m_testTransform;
	private Texture 	m_texture;
	private VertexArray m_testVertexArray;
	private String		textureFile		= "defaultTexture.png";
	private String		objFile			= "cube.obj";
	private float[] 	vertices 		= new float[] {};
	private int[] 		verticesIndices = new int[]{};
	private float[] 	texels 			= new float[]{};
	private int[] 		texelsIndices 	= new int[]{};

	public testApp(){
		super();
	}

	public testApp( String[] inputFiles ) {
		if(inputFiles.length >= 1) {
			objFile = inputFiles[0];
			if( inputFiles.length > 1)
			textureFile = inputFiles[1];
		}
	}

	@Override
	public void initialise() throws IOException {

		LOG_INFO("OBJ file: " + objFile);
		String inputObjContent = getWholeFileAsString( "src/main/resources/" + objFile );
		vertices 		  = getVertices( inputObjContent ); // non-indexed!
		verticesIndices   = getIndices ( inputObjContent, VERTICES_IDX);
		texels	  		  = getTexels  ( inputObjContent ); // non-indexed!
		texelsIndices	  = getIndices ( inputObjContent, TEXELS_IDX);

		//TODO make window class a singleton
		m_camera 			= new Camera(1024, 768);

		m_testShader 		= new Shader("testTextureShader.glsl"); //was - testUniformShader.glsl
		m_testShader		.bind();
		m_testShader		.setUniform("u_projectionMatrix", m_camera.getProjectionMatrix());
		m_testShader		.unbind();

		m_testTransform		= new Transform();

		Event.addWindowResizeCallback((width, height)-> {
			m_testShader.bind();
			m_testShader.setUniform("u_projectionMatrix", m_camera.getProjectionMatrix());
			m_testShader.unbind();
		});

		FloatArray vertexes = new FloatArray(3, vertices, verticesIndices);
		int[] plainIndices = new int[verticesIndices.length];
		for(int i = 0; i < verticesIndices.length; i++) plainIndices[i] = i;
		m_testVertexArray = new VertexArray(plainIndices, new VertexData(vertexes,  new FloatArray(2, texels, texelsIndices)));
		m_texture = new Texture("src/main/resources/" + textureFile);

		Input.addKeyCallback(GLFW_KEY_SPACE, GLFW_PRESS, () -> LOG("it works now"));
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
