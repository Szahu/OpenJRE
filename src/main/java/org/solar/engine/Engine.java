package org.solar.engine;

import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.solar.engine.renderer.RenderUtils;
import org.solar.engine.renderer.Renderer;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.solar.engine.Input.*;

public class Engine {

	//Our camera object
	private ImGuiLayer m_guiLayer;


    public void initialize() throws Exception {

		Utils.LOG_SUCCESS("Hello LWJGL " + Version.getVersion() + "!");

		//Initialise Event static class
		Event.initialise();

        // Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		//Creating and initialising window
		Window.initialize();

		//Initialise all the render and OpenGL stuff
		Renderer.initialise();
		RenderUtils.initialises();

		//Initialise imgui layer
		m_guiLayer = new ImGuiLayer(Window.getHandle());
		m_guiLayer.initImGui();

		//Initialising Input object, so we can use it as a singleton
		Input.initialise(Window.getHandle());
		
		// Make the window visible
		glfwShowWindow(Window.getHandle());

		Input.addKeyCallback(KEY_CODE_ESCAPE, KEY_RELEASE, Window::close);

    }
	
	public void mainLoop(Runnable appUpdate){

		while (!Window.getShouldClose()) {
			// clear the framebuffer

			Utils.updateDeltaTime();

			Input.update();

			m_guiLayer.startFrame(Utils.getDeltaTime());
			//glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // we're not using the stencil buffer now

			//Renderer.getFrameBuffer().bind();
			//appUpdate.run();
			//Renderer.getFrameBuffer().unbind();
			Renderer.renderToScreen(appUpdate);
			
			m_guiLayer.endFrame();

			//END CODE HERER
			
			glfwSwapBuffers(Window.getHandle()); // swap the color buffers

			// Poll for window events
			glfwPollEvents();
		}
	}


    public void terminate() {
        
		m_guiLayer.destroyImGui();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(Window.getHandle());
		glfwDestroyWindow(Window.getHandle());

		// Terminate GLFW and free the error callback
		glfwTerminate();

		Objects.requireNonNull(glfwSetErrorCallback(null)).free();
	}

}
