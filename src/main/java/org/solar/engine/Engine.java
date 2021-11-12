package org.solar.engine;

import org.lwjgl.glfw.*;
import org.lwjgl.system.*;

import org.solar.engine.renderer.Mesh;
import org.solar.engine.renderer.Renderer;
import org.solar.engine.renderer.Shader;
import org.solar.engine.renderer.VertexArray;

import imgui.ImGui;
import imgui.app.Application;
import imgui.app.Configuration;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.opengl.GL20.*;

public class Engine {

    private static Window m_window;
	private static Camera m_camera;

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
		m_window.initialize();

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
		float[] vertices = new float[]{
			-0.5f,  0.5f, -1.05f,
			 0.5f,  0.5f, -1.05f,
			 0.5f, -0.5f, -1.05f,
			-0.5f, -0.5f, -1.05f
		};

		int[] indices = new int[]{
			0, 3, 1, 1, 2, 3
		};

		float[] colours = new float[]{
			0.5f, 0.0f, 0.0f,
			0.0f, 0.5f, 0.0f,
			0.0f, 0.0f, 0.5f,
			0.0f, 0.5f, 0.5f,
		};

		Shader testColorShader = new Shader();
		Shader testUniformShader = new Shader("testUniformShader.glsl");
		testColorShader.load("testColorShader.glsl");

		VertexArray testVertexArray = new VertexArray(indices, vertices, colours);

		//TEST CODE END

		while (!this.getWindow().getShouldClose()) {

			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			//START CODE HERE
			
			testUniformShader.setUniform("u_projectionMatrix", m_camera.getProjectionMatrix());
			testUniformShader.setUniform("u_worldMatrix", m_camera.getWorldMatrix());
			Renderer.render(testVertexArray, testUniformShader);


			Utils.updateDeltaTime();
			Input.update();
			m_camera.update();
			m_imGui.update();
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
