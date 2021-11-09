package org.solar.engine;

import static org.lwjgl.system.MemoryUtil.*;

import static org.lwjgl.glfw.GLFW.*;

public class Window {
    public long handle;

    public boolean shouldClose = false;

    public int width = 1024;
    public int height = 768;

    public void initialize(){
                
        // Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		this.handle = glfwCreateWindow(this.width, this.height, "Hello World!", NULL, NULL);
		if ( this.handle == NULL )
			throw new RuntimeException("Failed to create the GLFW window");


        //resize callback
        glfwSetFramebufferSizeCallback(handle, (window, width, height) -> {
            this.width = width;
            this.height = height;
        });

        //making sure the window will close upon closing
        glfwSetWindowCloseCallback(handle, (window) -> {this.shouldClose = true;});
    }

    public void terminate(){
        glfwDestroyWindow(handle);
    }

    public void close() {
        shouldClose = true;
    }
}
