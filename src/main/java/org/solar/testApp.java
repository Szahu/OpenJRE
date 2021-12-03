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
import imgui.type.ImFloat;
import imgui.type.ImInt;

import java.io.IOException;

import javax.print.DocFlavor.READER;

public class testApp extends ApplicationTemplate {

    private Camera m_camera;
    private Shader m_testShader;
	private Terrain m_terrain;
	private Transform m_transform;
	private OpenSimplexNoise m_noise;
	private PointLight m_testLight;
	private boolean m_drawLines = false;

    @Override
    public void initialise() throws IOException, Exception {

		m_camera = new Camera(Window.getWidth(), Window.getHeight(), new DebugCameraController());
        Renderer.setActiveCamera(m_camera);

		m_testShader = new Shader("terrain.glsl");
		m_testShader.bind();
		m_testShader.setUniform("u_projectionMatrix", m_camera.getProjectionMatrix());
		m_testShader.unbind();

		Event.addWindowResizeCallback((width, height)-> {
			m_testShader.bind();
			m_testShader.setUniform("u_projectionMatrix", m_camera.getProjectionMatrix());
			m_testShader.unbind();
		});

		Renderer.setClearColor(new Vector3f(41f/255f, 41f/255f, 41f/255f));

		m_testLight = new PointLight();
		m_terrain = new Terrain();
		m_noise = new OpenSimplexNoise(276587923645987l);


		int size = 10;
		double[][][] grid = new double[size][size][size];

		for(int x = 0;x < size;x++){
			for(int y = 0;y < size;y++){
				for(int z = 0;z < size;z++){
					grid[x][y][z] = m_noise.noise3_Classic(x, y, z);
				}
			}
		}

		m_terrain.createMesh(grid, 0.5);
		m_transform = new Transform();
		m_transform.setPosition(new Vector3f(-size/2f, 0, -size/2f));

		Input.addKeyCallback(Input.KEY_CODE_A, Input.KEY_RELEASE, () -> {
			m_drawLines = !m_drawLines;
		});
	}

	ImInt seed = new ImInt();
	float[] level = new float[]{0};	
	float old_level = level[0];

	@Override
	public void update() {
		RenderUtils.renderGrid(m_camera);
		

		m_testShader.bind();
		m_testShader.setUniform(Shader.uniformTransformMatrixToken, m_transform.getTransformMatrix());

		Renderer.setDrawLines(m_drawLines);
		Renderer.render(m_terrain.getVertexArray(), m_testShader);
		Renderer.setDrawLines(false);

        m_camera.update();
			
		//IMGUI:
		ImGui.inputInt("seed", seed);
		if(ImGui.button("regenerate!")) {
			m_noise = new OpenSimplexNoise((long)seed.get());
			int size = 10;
			double[][][] grid = new double[size][size][size];

			for(int x = 0;x < size;x++){
				for(int y = 0;y < size;y++){
					for(int z = 0;z < size;z++){
						grid[x][y][z] = m_noise.noise3_Classic(x, y, z);
					}
				}
			}
			m_terrain.createMesh(grid, 0.5);
		}
		ImGui.sliderFloat("iso level", level, -2, 2);
		if(level[0] != old_level) {
			old_level = level[0];
			int size = 10;
			double[][][] grid = new double[size][size][size];

			for(int x = 0;x < size;x++){
				for(int y = 0;y < size;y++){
					for(int z = 0;z < size;z++){
						grid[x][y][z] = m_noise.noise3_Classic(x, y, z);
					}
				}
			}
			m_terrain.createMesh(grid, level[0]);
		}
        ImGui.text("FPS: " +  (int)(10f/Utils.getDeltaTime()));
		m_testLight.debugGui("light");
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
