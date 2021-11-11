package org.solar.engine;

import org.lwjgl.glfw.*;
import org.lwjgl.system.*;
import org.solar.engine.renderer.Mesh;
import org.solar.engine.renderer.Renderer;
import org.solar.engine.renderer.Shader;
import org.solar.engine.renderer.VertexArray;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.opengl.GL20.*;

public class Engine {

    private static Window m_window;

    public Window getWindow() {
        return m_window;
    }

    public static Integer closeWindow() {
        m_window.close();
        return 0;
    }

    public void initialize() {

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
		Input.initialise(m_window.handle);
        Event.AddKeyCallback(m_window.handle, GLFW_KEY_ESCAPE, GLFW_RELEASE, Engine::closeWindow);

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(m_window.handle, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				m_window.handle,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);

		} // the stack frame is popped automatically
		
		// Make the OpenGL context current
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(m_window.handle);
    }
	public void mainLoop(){

		//TEST CODE
		float[] vertices = new float[]{
			-0.5f,  0.5f, 0.0f,
			 0.5f,  0.5f, 0.0f,
			 0.5f, -0.5f, 0.0f,
			-0.5f, -0.5f, 0.0f
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

		Shader shader = new Shader();

		//shader.load("VertexFragmentShader.glsl");
		shader.load("testColorShader.glsl");

		Mesh testMesh = new Mesh(vertices, indices);

		VertexArray testVertexArray = new VertexArray(indices, vertices, colours);
		//TEST CODE END

		while (!this.getWindow().shouldClose) {

			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			//Renderer.render(testMesh, shader);

			Renderer.render(testVertexArray, shader);

			glfwSwapBuffers(this.getWindow().handle); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}

		//test code here
		testMesh.cleanup();
		testVertexArray.cleanup();
		shader.cleanup();
		//test code end here

	}

    public void terminate() {
        // Free the window callbacks and destroy the window
		glfwFreeCallbacks(m_window.handle);
		glfwDestroyWindow(m_window.handle);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
    }
}
