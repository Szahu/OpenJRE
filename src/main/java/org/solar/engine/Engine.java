package org.solar.engine;

import org.joml.Vector3f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.*;

import org.solar.engine.renderer.Renderer;
import org.solar.engine.renderer.Shader;
import org.solar.engine.renderer.VertexArray;

import imgui.ImGui;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.opengl.GL20.*;

public class Engine {

	//Our window object
    private static Window m_window;
	//Our camera object
	private static Camera m_camera;


	//One more change
	//Maybe one more

    public Window getWindow() {
        return m_window;
    }

    public static Integer closeWindow() {
        m_window.close();
        return 0;
    }

    public void initialize() {

		Event.initialise();

        // Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		//Creating and initialising window
		m_window = new Window();
		m_window.initialize(()->{
			GL.createCapabilities();
        	glEnable(GL_DEPTH_TEST);
        	glDepthFunc(GL_LESS); 
			Utils.LOG_INFO("OpenGL version: " + glGetString(GL_VERSION));
		});

		//Initialising Input object so we can use it as a singleton
		Input.initialise(m_window.getHandle());
        Event.AddKeyCallback(m_window.getHandle(), GLFW_KEY_ESCAPE, GLFW_RELEASE, Engine::closeWindow);

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(m_window.getHandle(), pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				m_window.getHandle(),
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);

		} // the stack frame is popped automatically
		
		// Make the OpenGL context current
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(m_window.getHandle());

		m_camera = new Camera(m_window.getWidth(), m_window.getHeight());

    }
	public void mainLoop(){

		//TEST CODE
		float[] vertices = new float[] {
			// VO
			-0.5f,  0.5f,  0.5f,
			// V1
			-0.5f, -0.5f,  0.5f,
			// V2
			0.5f, -0.5f,  0.5f,
			// V3
			 0.5f,  0.5f,  0.5f,
			// V4
			-0.5f,  0.5f, -0.5f,
			// V5
			 0.5f,  0.5f, -0.5f,
			// V6
			-0.5f, -0.5f, -0.5f,
			// V7
			 0.5f, -0.5f, -0.5f,
		};

		int[] indices = new int[] {
			// Front face
			0, 1, 3, 3, 1, 2,
			// Top Face
			4, 0, 3, 5, 4, 3,
			// Right face
			3, 2, 7, 5, 3, 7,
			// Left face
			6, 1, 0, 6, 0, 4,
			// Bottom face
			2, 1, 6, 2, 6, 7,
			// Back face
			7, 6, 4, 7, 4, 5,
		};

		float[] colours = new float[]{
			0.5f, 0.0f, 0.0f,
			0.0f, 0.5f, 0.0f,
			0.0f, 0.0f, 0.5f,
			0.0f, 0.5f, 0.5f,
			0.5f, 0.0f, 0.0f,
			0.0f, 0.5f, 0.0f,
			0.0f, 0.0f, 0.5f,
			0.0f, 0.5f, 0.5f,
		};

		Shader testColorShader = new Shader();
		testColorShader.load("testColorShader.glsl");

		Shader testUniformShader = new Shader("testUniformShader.glsl");
		testUniformShader.setUniform("u_projectionMatrix", m_camera.getProjectionMatrix());
		Transform testTransform = new Transform();

		Event.addWindowResizeCallback((width, height)-> {
			testUniformShader.setUniform("u_projectionMatrix", m_camera.getProjectionMatrix());
		});

		VertexArray testVertexArray = new VertexArray(indices, vertices, colours);
		ImGuiLayer m_guiLayer = new ImGuiLayer(m_window.getHandle());
		m_guiLayer.initImGui();
		//TEST CODE END

		while (!this.getWindow().getShouldClose()) {

			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			//START CODE HERE
			
			testUniformShader.setUniform("u_viewMatrix", m_camera.getWorldMatrix());
			testUniformShader.setUniform("u_worldMatrix", testTransform.getTransformMatrix());
			Renderer.render(testVertexArray, testUniformShader);

			Utils.updateDeltaTime();
			Input.update();
			m_camera.update();
			
			m_guiLayer.update(Utils.getDeltaTime(), m_window, () -> {
				ImGui.text("Hello world");
				testTransform.debugGui();
			});
			//END CODE HERER
			

			glfwSwapBuffers(this.getWindow().getHandle()); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}

		//test code here
		testVertexArray.cleanup();
		testColorShader.cleanup();
		testUniformShader.cleanup();
		//test code end here

	}

    public void terminate() {
        // Free the window callbacks and destroy the window
		glfwFreeCallbacks(m_window.getHandle());
		glfwDestroyWindow(m_window.getHandle());

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
    }
}
