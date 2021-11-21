package org.solar.engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.system.MemoryStack.*;

/**
 * Handles all operation related to the application window.
 * It is a static class.
 */
public class Window {
    private static long m_handle;
    private static boolean m_shouldClose = false;
    private static int m_width = 1024;
    private static int m_height = 768;
    //private static boolean m_enableVsync = true;

    //public static void setVsync(boolean newSetting) {m_enableVsync = newSetting;}
    
    /**
     * Returns width of the screen in pixels.
     * @return
     */
    public static int getWidth() {return m_width;}
    /**
     * Returns Height of the screen in pixels.
     * @return
     */
    public static int getHeight() {return m_height;}
    /**
     * Return glfw window pointer to the window.
     * @return
     */
    public static long getHandle() {return m_handle;}
    /**
     * Return private shouldClose boolean.
     * @return
     */
    public static boolean getShouldClose() {return m_shouldClose;}
 
    /**
     * Initialises the window, is called by an Engine itself.
     */
    public static void initialize(){
                
        // Configure GLFW
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		m_handle = glfwCreateWindow(m_width, m_height, "Hello World!", NULL, NULL);
		if (m_handle == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

        Event.createEvent("windowResize");

        //resize callback
        glfwSetWindowSizeCallback(m_handle, (window, width, height) -> {
            Event.activateWindowResizeEvent(width, height);
            m_width = width;
            m_height = height;
            //glViewport(0,0, width, height);
        });

        glfwSetWindowMaximizeCallback(m_handle, (window, maximised) -> {
            IntBuffer w = BufferUtils.createIntBuffer(1);
            IntBuffer h = BufferUtils.createIntBuffer(1);
            glfwGetWindowSize(window, w, h);
            int width = w.get(0);
            int height = h.get(0);
            Event.activateWindowResizeEvent(width, height);
            m_width = width;
            m_height = height;
        });

        //making sure the window will close upon closing
        glfwSetWindowCloseCallback(m_handle, (window) -> {m_shouldClose = true;});

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
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

        //Create current context
        glfwMakeContextCurrent(m_handle);

        // Enable v-sync
		glfwSwapInterval(1);
    }

    /**
     * Cleanup of the window class.
     */
    public static void terminate(){
        //Cleanup
        glfwDestroyWindow(m_handle);
    }

    /**
     * Closes the window (breaks the main while loops).
     */
    public static void close() {
        m_shouldClose = true;
    }
}
