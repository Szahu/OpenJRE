package org.solar.engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.system.MemoryStack.*;

public class Window {
    private long m_handle;
    private boolean m_shouldClose = false;
    private int m_width = 1024;
    private int m_height = 768;

    public int getWidth() {return m_width;}
    public int getHeight() {return m_height;}
    public long getHandle() {return m_handle;}
    public boolean getShouldClose() {return m_shouldClose;}

    public void initialize(Runnable glInitCallback){
                
        // Configure GLFW
		glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		this.m_handle = glfwCreateWindow(this.m_width, this.m_height, "Hello World!", NULL, NULL);
		if ( this.m_handle == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

        //Create current contex 
		glfwMakeContextCurrent(m_handle);
        //Initialise OpenGL
		glInitCallback.run();

        Event.createEvent("windowResize");

        //resize callback
        glfwSetFramebufferSizeCallback(m_handle, (window, width, height) -> {
            Event.activateWindowResizeEvent(width, height);
            this.m_width = width;
            this.m_height = height;
            glViewport(0,0, width, height);
        });

        //making sure the window will close upon closing
        glfwSetWindowCloseCallback(m_handle, (window) -> {this.m_shouldClose = true;});

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(m_handle, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				m_handle,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);

		} // the stack frame is popped automatically
    }

    public void terminate(){
        //Cleanup
        glfwDestroyWindow(m_handle);
    }

    public void close() {
        m_shouldClose = true;
    }
}
