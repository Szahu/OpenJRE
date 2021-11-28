package org.solar;

import org.joml.Math;
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
	private Texture m_normal_texture;
	private Transform m_lightTransform;

    @Override
    public void initialise() throws IOException, Exception {

		m_camera = new Camera(Window.getWidth(), Window.getHeight(), new DebugCameraController());
        Renderer.setActiveCamera(m_camera);

		m_testShader = new Shader("blinn.glsl");
		m_testShader.bind();
		m_testShader.setUniform("u_projectionMatrix", m_camera.getProjectionMatrix());
		m_testShader.unbind();

		Event.addWindowResizeCallback((width, height)-> {
			m_testShader.bind();
			m_testShader.setUniform("u_projectionMatrix", m_camera.getProjectionMatrix());
			m_testShader.unbind();
		});

		
		m_testTransform	= new Transform();
		m_testTransform.setScale(new Vector3f(0.1f, 0.1f, 0.1f));

		m_testVertexArray = ModelLoader.loadModel("assets/rock.fbx");
		
		m_texture = new Texture("assets/rock.png", false);
		m_normal_texture = new Texture("assets/rock_normal.png", false);

		Renderer.setClearColor(new Vector3f(41f/255f, 41f/255f, 41f/255f));
		m_lightTransform = new Transform();
		m_lightTransform.setPosition(new Vector3f(0,3,0));
	}


	float[] lightSpeed = {0.01f};

	float angle = 0;

	@Override
	public void update() {
		RenderUtils.renderGrid(m_camera);

		m_testShader.bind();
		m_testShader.setUniform("u_texture_sampler", 0);
		m_testShader.setUniform("u_normal_texture_sampler", 1);
		m_testShader.setUniform("u_cameraPosition", m_camera.getCameraController().getTransform().getPosition());
		m_testShader.setUniform("u_lightPosition", m_lightTransform.getPosition());


		m_texture.bind(0);
		m_normal_texture.bind(1);

		angle += lightSpeed[0];
		m_lightTransform.setPosition(new Vector3f((float)Math.sin(angle) * 10f, 3f, 10f * (float)Math.cos(angle)));

        Renderer.render(m_testVertexArray, m_testShader, m_testTransform);

		m_texture.unbind();
		m_normal_texture.unbind();

		m_testShader.unbind(); 

        m_camera.update();
			
        ImGui.text("FPS: " +  (int)(10f/Utils.getDeltaTime()));
		ImGui.dragFloat("speed", lightSpeed, 0.0001f);
		m_testTransform.debugGui("rock");
		//m_lightTransform.debugGui("light");
    }

    @Override
    public void terminate() {
        m_testVertexArray.cleanup();

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
