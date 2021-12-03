package org.solar;

import org.joml.Vector3f;
import org.solar.appllication.terrain.GridCell;
import org.solar.appllication.terrain.Terrain;
import org.solar.engine.*;
import org.solar.engine.renderer.RenderUtils;
import org.solar.engine.renderer.Renderer;
import org.solar.engine.renderer.Shader;

import imgui.ImGui;
import java.io.IOException;

import javax.print.DocFlavor.READER;

public class testApp extends ApplicationTemplate {

    private Camera m_camera;
    private Shader m_testShader;
	private Terrain m_terrain;
	private Transform m_transform;

	private PointLight m_testLight;

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

		double[][][] grid = new double[][][]{
			{
				{1,1,1},
				{1,1,1},
				{1,1,1},
			},
			{
				{1,1,1},
				{1,0,1},
				{1,1,1},
			},
			{
				{1,1,1},
				{1,1,1},
				{1,1,1},
			}
		}; 
 
		/* double[][][] grid = new double[][][]{
			{{0,0},{0,0},},
			{{0,0},{0,1},}
		};   */

		m_terrain.createMesh(grid, 0.5);
		m_transform = new Transform();
	}

	@Override
	public void update() {
		RenderUtils.renderGrid(m_camera);


		m_testShader.bind();
		m_testShader.setUniform(Shader.uniformTransformMatrixToken, m_transform.getTransformMatrix());

		Renderer.toggleLines(true);
		Renderer.render(m_terrain.getVertexArray(), m_testShader);
		Renderer.toggleLines(false);


        m_camera.update();
			
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
