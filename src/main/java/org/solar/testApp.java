package org.solar;

import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.solar.appllication.terrain.Chunk;
import org.solar.appllication.terrain.OpenSimplexNoise;
import org.solar.appllication.terrain.Terrain;
import org.solar.engine.*;
import org.solar.engine.renderer.FloatArray;
import org.solar.engine.renderer.InstancedVertexArray;
import org.solar.engine.renderer.RenderUtils;
import org.solar.engine.renderer.Renderer;
import org.solar.engine.renderer.Shader;
import org.solar.engine.renderer.VertexArray;
import org.solar.engine.renderer.VertexData;

import imgui.ImGui;
import imgui.type.ImInt;

import java.io.IOException;
import java.util.Arrays;
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

	private InstancedVertexArray testCube;
	private Shader instanceShader;

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

		gridLocation.x = (int)(m_camera.getCameraController().getTransform().getPosition().x / (Chunk.CHUNK_SIZE * Chunk.CELL_SIZE));
		gridLocation.y = (int)(m_camera.getCameraController().getTransform().getPosition().z / (Chunk.CHUNK_SIZE * Chunk.CELL_SIZE));
	
		Renderer.setClearColor(new Vector3f(135f/255f, 206f/255f, 235f/255f));

		m_terrain = new Terrain();
		m_lightTransform = new Transform();
		m_lightTransform.setPosition(new Vector3f(0, 30, 0));

		Input.addKeyCallback(Input.KEY_CODE_P, Input.ACTION_RELEASE, () -> {
			m_drawLines = !m_drawLines;
		});


		m_terrain.addNewChunk(new Vector2i(0,0));

		//m_terrain.multiThreadedLoading();

		//m_terrain.initAllChunksInQueue();

		float[] offsets = new float[100];
		for(int i = 0;i < 97;i+=3) {
			offsets[i] = (float)i/3 * 3;
			offsets[i+1] = 0;
			offsets[i+2] = 0;
		}

		VertexData testCubeData = ModelLoader.loadModel("assets/cube.obj");
		testCube = new InstancedVertexArray(Arrays.copyOf(testCubeData.indices, testCubeData.indices.length),
		new FloatArray(3, Arrays.copyOf(testCubeData.arrays[0].data, testCubeData.arrays[0].data.length)), 
		new FloatArray(3, Arrays.copyOf(testCubeData.arrays[3].data, testCubeData.arrays[3].data.length)),
		new FloatArray(3, Arrays.copyOf(offsets, offsets.length)));
		
		instanceShader = new Shader("instanced.glsl");
		instanceShader.bind();
		instanceShader.setUniform("u_projectionMatrix", m_camera.getProjectionMatrix());
		instanceShader.unbind(); 
	}

	float[] lightVec = new float[]{-3,1,0};

	@Override
	public void update() {
		RenderUtils.renderGrid(m_camera);
		m_testShader.bind();
		m_testShader.setUniform("u_lightDirection", new Vector3f(lightVec[0], lightVec[1], lightVec[2]));
		m_testShader.setUniform(Shader.uniformViewMatrixToken, m_camera.getViewMatrix());


		Renderer.setDrawLines(m_drawLines);
		for (Map.Entry<Vector2i, Chunk> entry : m_terrain.getChunks().entrySet()) {
			if(entry.getKey().distance(gridLocation) < Terrain.RENDERING_DSTSNCE) {
				Renderer.drawVertexArray(entry.getValue().getVertexArray());
			}
		}

		Transform t = new Transform();
		instanceShader.bind();
		instanceShader.setUniform("u_worldMatrix", t.getTransformMatrix());
		instanceShader.setUniform("u_lightDirection", new Vector3f(lightVec[0], lightVec[1], lightVec[2]));
		instanceShader.unbind();

		Renderer.renderInstanced(testCube, instanceShader, 10);
		instanceShader.unbind();

		Renderer.setDrawLines(false);

		Vector2i newGridLoc= new Vector2i();
		newGridLoc.x = (int)Math.floor(-m_camera.getCameraController().getTransform().getPosition().x / (Chunk.CHUNK_SIZE * Chunk.CELL_SIZE));
		newGridLoc.y = (int)Math.floor(-m_camera.getCameraController().getTransform().getPosition().z / (Chunk.CHUNK_SIZE * Chunk.CELL_SIZE)); 

		if(!gridLocation.equals(newGridLoc)) {
			Vector2i change = new Vector2i();
			newGridLoc.sub(gridLocation, change);
			Vector2i changePer = new Vector2i(change.y, change.x);
			gridLocation = newGridLoc;
			Vector2i loc = new Vector2i(gridLocation);
			for(int i = 0;i < 3;i++) {
				m_terrain.addNewChunk(new Vector2i(loc));
				m_terrain.addNewChunk(new Vector2i().add(loc).add(changePer));
				m_terrain.addNewChunk(new Vector2i().add(loc).sub(changePer));
				loc.add(change);
			} 
		} 

		m_terrain.initOneChunkInQueue();


        m_camera.update();
			
		//IMGUI:
        ImGui.text("FPS: " +  (int)(10f/Utils.getDeltaTime()));
		ImGui.dragFloat3("light Vec", lightVec, 0.05f);
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
