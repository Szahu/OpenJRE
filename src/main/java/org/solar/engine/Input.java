package org.solar.engine;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector2i;

public class Input {
    private Input() {}

    private static long m_windowHandle;

    private static int m_xpos = 0;
    private static int m_ypos = 0;

    private static Vector2i m_lastMousePos;
    private static Vector2i m_deltaMousePos;

    public static Vector2i getMousePosDelta() {
        return m_deltaMousePos;
    }

    public static void initialise(long windowHandle){
        m_windowHandle = windowHandle;
        m_lastMousePos = new Vector2i(0,0);
        m_deltaMousePos = new Vector2i(0,0);

        glfwSetCursorPosCallback(m_windowHandle, (window, xpos, ypos) -> {
            m_xpos = (int)xpos;
            m_ypos = (int)ypos;
        });
    }

    public static boolean isKeyDown(int keyCode) {
        return glfwGetKey(m_windowHandle, keyCode) == 1;
    }

    public static Vector2i getMousePosition() {
        return new Vector2i(m_xpos, m_ypos);
    }

    public static void update() {

        Vector2i mousePos = getMousePosition();
        m_deltaMousePos = m_lastMousePos.sub(mousePos);
        // To prevent huge jumps when the mouse leaves the application and re-enters at a different point
        if (Math.abs(m_deltaMousePos.get(0)) > 150 || Math.abs(m_deltaMousePos.get(1)) > 150) {
            m_deltaMousePos.zero();
        }
        m_deltaMousePos.mul(-1, 1);
        m_lastMousePos = mousePos;
    }
}
