package org.solar.engine;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;

public class Engine {

	//Our camera object
	//private ImGuiLayer m_guiLayer;


    public void initialize() {

		Event.initialise();

        // Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		//Creating and initialising window
		Window.initialize(()->{
			GL.createCapabilities();
        	glEnable(GL_DEPTH_TEST);
        	glDepthFunc(GL_LESS); 
			Utils.LOG_INFO("OpenGL version: " + glGetString(GL_VERSION));
		});

		//Initialising Input object so we can use it as a singleton
		Input.initialise(Window.getHandle());
        //Event.AddKeyCallback(m_window.getHandle(), GLFW_KEY_ESCAPE, GLFW_RELEASE, Engine::closeWindow/* );

		// Get the thread stack and push a new frame
		/* try ( MemoryStack stack = stackPush() ) {
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

		} */ // the stack frame is popped automatically */
		
		// Make the OpenGL context current
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(Window.getHandle());

		m_guiLayer = new ImGuiLayer(Window.getHandle());
		m_guiLayer.initImGui();
    }
	
	public void mainLoop(Runnable appUpdate){


		//TEST CODE END

		while (!Window.getShouldClose()) {

			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			//START CODE HERE

			Utils.updateDeltaTime();
			Input.update();

			//m_guiLayer.startFrame(Utils.getDeltaTime());

			appUpdate.run();
			
			//m_guiLayer.endFrame();

			//END CODE HERER
			
			glfwSwapBuffers(Window.getHandle()); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}

	}

    public void terminate() {
        
		//m_guiLayer.destroyImGui();
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(Window.getHandle());
		glfwDestroyWindow(Window.getHandle());

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
    }
}
