package org.solar;

import org.joml.Math;
import org.joml.Vector3f;
import org.solar.engine.*;
import org.solar.engine.renderer.RenderData;
import org.solar.engine.renderer.RenderUtils;
import org.solar.engine.renderer.RenderableEntity;
import org.solar.engine.renderer.Renderer;
import org.solar.engine.renderer.Shader;
import org.solar.engine.renderer.Texture;
import org.solar.engine.renderer.VertexArray;
import org.solar.engine.renderer.Texture.TextureType;

import imgui.ImGui;
import java.io.IOException;

public class testApp extends ApplicationTemplate {

    private Camera m_camera;
    private Shader m_testShader;
    private Transform m_testTransform;
    private VertexArray m_testVertexArray;
	private Transform m_lightTransform;

	private Material m_rockMaterial;
	private RenderableEntity m_testRenderableEntity;

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

		m_rockMaterial = new Material();
		m_rockMaterial.setTexture(TextureType.Albedo, new Texture("assets/rock.png", Texture.TextureType.Albedo));
		m_rockMaterial.setTexture(TextureType.Normal, new Texture("assets/rock_normal.png", Texture.TextureType.Normal));

		Renderer.setClearColor(new Vector3f(41f/255f, 41f/255f, 41f/255f));
		m_lightTransform = new Transform();
		m_lightTransform.setPosition(new Vector3f(0,3,0));

		m_testRenderableEntity = new RenderableEntity(new RenderData(m_testVertexArray, m_testShader, m_rockMaterial));
		m_testRenderableEntity.getTransform().setScale(new Vector3f(0.1f, 0.1f, 0.1f));
	}


	float[] lightSpeed = {0.01f};

	float angle = 0;

	@Override
	public void update() {
		RenderUtils.renderGrid(m_camera);

		m_testShader.bind();
		m_testRenderableEntity.getRenderData().forEach(data -> {
			data.getShader().setUniform("u_cameraPosition", m_camera.getCameraController().getTransform().getPosition());
		});
		m_testShader.setUniform("u_lightPosition", m_lightTransform.getPosition());

		Renderer.render(m_testRenderableEntity);

		m_testShader.unbind(); 

        m_camera.update();
			
        ImGui.text("FPS: " +  (int)(10f/Utils.getDeltaTime()));
		ImGui.dragFloat("speed", lightSpeed, 0.0001f);
		m_testRenderableEntity.getTransform().debugGui("trans");
		angle += lightSpeed[0];
		m_lightTransform.setPosition(new Vector3f((float)Math.sin(angle) * 10f, 3f, 10f * (float)Math.cos(angle)));
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
