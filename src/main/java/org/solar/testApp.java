package org.solar;

import org.joml.Vector3f;
import org.solar.appllication.terrain.GridCell;
import org.solar.appllication.terrain.OpenSimplexNoise;
import org.solar.appllication.terrain.Terrain;
import org.solar.engine.*;
import org.solar.engine.renderer.RenderUtils;
import org.solar.engine.renderer.Renderer;
import org.solar.engine.renderer.Shader;

import imgui.ImGui;
import imgui.type.ImInt;

import java.io.IOException;


public class testApp extends ApplicationTemplate {

    private Camera m_camera;
    private Shader m_testShader;
	private Terrain m_terrain;
	private Transform m_transform;
	private Transform m_lightTransform;
	private OpenSimplexNoise m_noise;
	private boolean m_drawLines = false;

	private int gridSize = 20;
	private double isoLevel = 0.0;

	private double factor = 0.1f;

	private Vector3f noiseOffse = new Vector3f();

	private void regenerate() {
		int size = gridSize;
		double[][][] grid = new double[size][size][size];

		for(int x = 0;x < size;x++){
			for(int y = 0;y < size;y++){
				for(int z = 0;z < size;z++){
					grid[x][y][z] = m_noise.noise3_Classic(factor*x, factor*y, factor*z - noiseOffse.x) * 2;
				}
			}
		}

		m_terrain.createMesh(grid, isoLevel);
	}

    @Override
    public void initialise() throws IOException, Exception {

		m_camera = new Camera(Window.getWidth(), Window.getHeight(), new DebugCameraController());
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

		Renderer.setClearColor(new Vector3f(41f/255f, 41f/255f, 41f/255f));

		m_terrain = new Terrain();
		m_noise = new OpenSimplexNoise(276587923645987l);
		m_lightTransform = new Transform();
		m_lightTransform.setPosition(new Vector3f(0, gridSize + 1, 0));

		regenerate();

		/* double[][][] grid = new double[][][]{
			{
				{-1,1},
				{1,1},
			}, 
			{
				{1,1},
				{1,1},
			}, 
		}; */

		m_transform = new Transform();
		m_transform.setPosition(new Vector3f(-gridSize/2f, 0, -gridSize/2f));

		Input.addKeyCallback(Input.KEY_CODE_A, Input.KEY_RELEASE, () -> {
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
		m_testShader.setUniform(Shader.uniformTransformMatrixToken, m_transform.getTransformMatrix());
		m_testShader.setUniform("u_lightPosition", m_lightTransform.getPosition());

		Renderer.setDrawLines(m_drawLines);
		Renderer.render(m_terrain.getVertexArray(), m_testShader);
		Renderer.setDrawLines(false);

        m_camera.update();
			
		//IMGUI:
		ImGui.inputInt("seed", seed);
		if(ImGui.button("regenerate!")) {
			m_noise = new OpenSimplexNoise((long)seed.get());
			regenerate();
		}
		ImGui.sliderFloat("iso level", level, -2, 2);
		if(level[0] != old_level) {
			old_level = level[0];
			isoLevel = level[0];
			regenerate();
		}
		ImGui.dragFloat("offset", xoffset, 0.001f);
		if(noiseOffse.x != xoffset[0]) {
			noiseOffse.x = xoffset[0];
			regenerate();
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
