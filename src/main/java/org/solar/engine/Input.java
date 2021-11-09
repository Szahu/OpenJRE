package org.solar.engine;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
    private Input() {

    }

    private static long m_windowHandle;

    public static void initialise(long windowHandle){
        m_windowHandle = windowHandle;
    }

    public static boolean isKeyDown(int keyCode) {
        return glfwGetKey(m_windowHandle, GLFW_KEY_V) == 1;
    }
}
