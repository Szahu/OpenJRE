package org.solar;

import org.joml.Vector3f;
import org.solar.engine.*;
import org.solar.engine.renderer.RenderUtils;
import org.solar.engine.renderer.Renderer;
import org.solar.engine.renderer.Shader;
import org.solar.engine.renderer.Texture;
import org.solar.engine.renderer.VertexArray;
import imgui.ImGui;
import java.io.IOException;

public class testApp extends ApplicationTemplate {

    private Camera m_camera;
    private Shader m_testShader;
    private Transform m_testTransform;
    private VertexArray m_testVertexArray;
	private Texture m_texture;

    @Override
    public void initialise() throws IOException {

		m_camera = new Camera(Window.getWidth(), Window.getHeight(), new DebugCameraController());
        Renderer.setActiveCamera(m_camera);

		m_testShader = new Shader("testTextureShader.glsl");
		m_testShader.bind();
		m_testShader.setUniform("u_projectionMatrix", m_camera.getProjectionMatrix());
		m_testShader.unbind();

		m_testTransform	= new Transform();

		Event.addWindowResizeCallback((width, height)-> {
			m_testShader.bind();
			m_testShader.setUniform("u_projectionMatrix", m_camera.getProjectionMatrix());
			m_testShader.unbind();
		});

		m_testVertexArray = ModelLoader.loadModel("assets/barn.obj");
		
		m_texture = new Texture("assets/block.png", true);

		Renderer.setClearColor(new Vector3f(77f/255f, 200f/255f, 233f/255f));
	}

	@Override
	public void update() {
		RenderUtils.renderGrid(m_camera);

		m_testShader.bind();
		m_testShader.setUniform("u_texture_sampler", 0);

		m_texture.bind();

        Renderer.render(m_testVertexArray, m_testShader, m_testTransform);

		m_testShader.unbind(); 

        m_camera.update();
			
        ImGui.text("FPS: " +  (int)(10f/Utils.getDeltaTime()));
        m_testTransform.debugGui("test Transform");
    }

    @Override
    public void terminate() {
        m_testVertexArray.cleanup();

		m_testShader.cleanup();
	}

	public void run() throws IOException {
		super.run();
	}

	public static void main(String[] args) throws IOException {
		ApplicationTemplate app = new testApp();
		app.run();
	}
}
