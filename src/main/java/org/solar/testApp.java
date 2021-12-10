package org.solar;

import org.joml.Math;
import org.joml.Vector2f;
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
import java.util.function.Function;


public class testApp extends ApplicationTemplate {

    private Camera m_camera;
    private Shader m_testShader;
	private Terrain m_terrain;
	private Transform m_transform;
	private Transform m_lightTransform;
	private OpenSimplexNoise m_noise;
	private boolean m_drawLines = false;
	private VertexArray m_cube;

	private int gridSizeX = 40;
	private int gridSizeY = 20;
	private int gridSizeZ = 40;
	private double isoLevel = 0.0;

	private double frequency = 0.025f;

	private Vector3f noiseOffse = new Vector3f();
	private Vector2f gridLocation = new Vector2f();

	private Transform terrainCursor = new Transform();

	private double[][][] grid = new double[Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE];

	private void regenerate() {

		Function<Vector3f, double[][][]> generator = (offset) -> {
			double[][][] res = new double[Chunk.CHUNK_SIZE+1][Chunk.CHUNK_SIZE+1][Chunk.CHUNK_SIZE+1];
			for(int x = 0;x <= Chunk.CHUNK_SIZE;x++){
				for(int y = 0;y <= Chunk.CHUNK_SIZE;y++){
					for(int z = 0;z <= Chunk.CHUNK_SIZE;z++){
						
						float density = -y - offset.y;
						Vector2f location = new Vector2f(x + offset.z + 1, z + offset.x + 1);
						density += Math.clamp((m_noise.noise2(frequency * location.x, frequency *  location.y) + 1) * 6, 0, 1);
						res[x][y][z] = density;
	
					}	
				}
			}
			return res;
		};

		

		m_terrain.createMesh(isoLevel, generator, new Vector3f(gridLocation.x,0,gridLocation.y));
	}

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

		m_cube = ModelLoader.loadModel("assets/cube.obj");

		m_terrain = new Terrain();
		m_noise = new OpenSimplexNoise(276587923645987l);
		m_lightTransform = new Transform();
		m_lightTransform.setPosition(new Vector3f(0, gridSizeY + 1, 0));

		regenerate();

		m_transform = new Transform();
		m_transform.setPosition(new Vector3f(-gridSizeX/2f, 0, -gridSizeZ/2f));

		Input.addKeyCallback(Input.KEY_CODE_P, Input.KEY_RELEASE, () -> {
			m_drawLines = !m_drawLines;
		});
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

		Renderer.setDrawLines(m_drawLines);
		Renderer.render(m_terrain.getVertexArray(), m_testShader);
		Renderer.setDrawLines(false);

		Vector2f newGridLoc= new Vector2f();
		newGridLoc.x = Math.floor(-m_camera.getCameraController().getTransform().getPosition().x / Chunk.CHUNK_SIZE);
		newGridLoc.y = Math.floor(-m_camera.getCameraController().getTransform().getPosition().z / Chunk.CHUNK_SIZE);

		if(!gridLocation.equals(newGridLoc, 0)) {
			gridLocation = newGridLoc;
			regenerate();
		}

        m_camera.update();
			
		//IMGUI:
		ImGui.inputInt("seed", seed);
		if(ImGui.button("regenerate!")) {
			m_noise = new OpenSimplexNoise((long)seed.get());
			regenerate();
		}
		ImGui.sliderFloat("iso level", level, 0, 1);
		if(level[0] != old_level) {
			old_level = level[0];
			isoLevel = level[0];
			regenerate();
		}
		ImGui.dragFloat("offset", xoffset, 0.001f);
		xoffset[0] += 0.01f;
		if(noiseOffse.x != xoffset[0]) {
			noiseOffse.x = xoffset[0];
			//regenerate();
		}
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
