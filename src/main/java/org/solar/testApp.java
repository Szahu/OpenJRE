package org.solar;

import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.solar.appllication.terrain.Chunk;
import org.solar.appllication.terrain.OpenSimplexNoise;
import org.solar.appllication.terrain.Terrain;
import org.solar.engine.*;
import org.solar.engine.renderer.RenderUtils;
import org.solar.engine.renderer.Renderer;
import org.solar.engine.renderer.Shader;
import org.solar.engine.renderer.VertexArray;

import imgui.ImGui;
import imgui.type.ImInt;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;


public class testApp extends ApplicationTemplate {

    private Camera m_camera;
    private Shader m_testShader;
	public Terrain m_terrain;
	private Transform m_transform;
	private Transform m_lightTransform;
	private boolean m_drawLines = false;

	private Vector2i gridLocation = new Vector2i();

    @Override
    public void initialise() throws IOException, Exception {

		m_camera = new Camera(Window.getWidth(), Window.getHeight(), new gameLikeCameraController());
        Renderer.setActiveCamera(m_camera);

		m_testShader = new Shader("terrainLight.glsl");
		m_testShader.bind();
		m_testShader.setUniform("u_projectionMatrix", m_camera.getProjectionMatrix());
		m_testShader.unbind();

		Event.addWindowResizeCallback((width, height)-> {
			m_testShader.bind();
			m_testShader.setUniform("u_projectionMatrix", m_camera.getProjectionMatrix());
			m_testShader.unbind();
		});

		gridLocation.x = (int)(m_camera.getCameraController().getTransform().getPosition().x / Chunk.CHUNK_SIZE);
		gridLocation.y = (int)(m_camera.getCameraController().getTransform().getPosition().z / Chunk.CHUNK_SIZE);
	
		Renderer.setClearColor(new Vector3f(41f/255f, 41f/255f, 41f/255f));

		m_terrain = new Terrain();
		m_lightTransform = new Transform();
		m_lightTransform.setPosition(new Vector3f(0,  10, 0));

		Input.addKeyCallback(Input.KEY_CODE_P, Input.ACTION_RELEASE, () -> {
			m_drawLines = !m_drawLines;
		});

		for(int i = 0;i < 10;i++) {
			for(int j = 0;j < 10;j++) {
				m_terrain.addNewChunk(new Vector2i(i,j));
			}
		}
;
	}

	ImInt seed = new ImInt();
	float[] level = new float[]{0};	
	float old_level = level[0];
	float[] xoffset = new float[]{0};

	@Override
	public void update() {
		RenderUtils.renderGrid(m_camera);
		

		m_testShader.bind();
		m_testShader.setUniform("u_lightPosition", m_lightTransform.getPosition());
		m_testShader.setUniform(Shader.uniformViewMatrixToken, m_camera.getViewMatrix());

		Renderer.setDrawLines(m_drawLines);
		for (Map.Entry<Vector2i, VertexArray> entry : m_terrain.getVertexArrays().entrySet()) {
			if(entry.getKey().distance(gridLocation) < 15) {
				Renderer.drawVertexArray(entry.getValue());
			}
		}
		Renderer.setDrawLines(false);

		Vector2i newGridLoc= new Vector2i();
		newGridLoc.x = (int)Math.floor(-m_camera.getCameraController().getTransform().getPosition().x / Chunk.CHUNK_SIZE);
		newGridLoc.y = (int)Math.floor(-m_camera.getCameraController().getTransform().getPosition().z / Chunk.CHUNK_SIZE); 

		if(!gridLocation.equals(newGridLoc)) {
			gridLocation = newGridLoc;
			m_terrain.addNewChunk(gridLocation);
		} 

        m_camera.update();
			
		//IMGUI:
        ImGui.text("FPS: " +  (int)(10f/Utils.getDeltaTime()));
		m_lightTransform.debugGui("light");
    }

    @Override
    public void terminate() {
		m_testShader.cleanup();
	}

	public void run() throws Exception {
		super.run();
	}

	public static void main(String[] args) throws Exception {
		ApplicationTemplate app = new testApp();
		app.run();
	}
}
