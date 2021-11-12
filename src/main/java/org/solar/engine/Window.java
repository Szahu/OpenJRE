package org.solar.engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.*;
import org.lwjgl.opengl.*;

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
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
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
    }

    public void terminate(){
        //Cleanup
        glfwDestroyWindow(m_handle);
    }

    public void close() {
        m_shouldClose = true;
    }
}
