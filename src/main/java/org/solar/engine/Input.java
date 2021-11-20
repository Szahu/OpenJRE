package org.solar.engine;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.joml.Vector2i;

/**
 * Handles all user input. 
 */
public class Input {
    private Input() {}

    private static long m_windowHandle;

    private static int m_xpos = 0;
    private static int m_ypos = 0;

    private static Vector2i m_lastMousePos;
    private static Vector2i m_deltaMousePos;

    private static float m_scrollInput = 0;

    /**
     * Returns change in mouse position since last frame. 
     * @return 
     */
    public static Vector2i getMousePosDelta() {
        return m_deltaMousePos;
    }

    /**
     * Returns scroll input from a mouse wheel.
     * @return
     */
    public static float getScrollInput() {float res = m_scrollInput; m_scrollInput = 0; return res;}

    /**
     * Initialises the input class, handled by the engine. The user shouldn't need to call this class. 
     * @param windowHandle Current glfw window pointer.
     */
    public static void initialise(long windowHandle){
        m_windowHandle = windowHandle;
        m_lastMousePos = new Vector2i(0,0);
        m_deltaMousePos = new Vector2i(0,0);

        glfwSetCursorPosCallback(m_windowHandle, (window, xpos, ypos) -> {
            m_xpos = (int)xpos;
            m_ypos = (int)ypos;
        });

        glfwSetScrollCallback(m_windowHandle, (window, xoffset, yoffset) -> {
            m_scrollInput = (float)yoffset;
        });
    }

    /**
     * Checks if a certain key is pressed.
     * @param keyCode Key code of the key.
     * @return
     */
    public static boolean isKeyDown(int keyCode) {
        return glfwGetKey(m_windowHandle, keyCode) == 1;
    }

    /**
     * Checks if mouse button is pressed.
     * @param mouseButtonCode Key code of the mouse button.
     * @return
     */
    public static boolean isMouseButtonDown(int mouseButtonCode) {
        return glfwGetMouseButton(m_windowHandle, mouseButtonCode) == 1;
    }

    class actionData {
        public actionData(int key, int act) {
            keyCode = key;
            action = act;
            hashCode = Objects.hash(keyCode, act);
        }
        public int keyCode;
        public int action;
        private int hashCode;

        public void print() {Utils.LOG("KEYCODE: " + keyCode + " action: " + action);}

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            actionData that = (actionData) o;
            return keyCode == that.keyCode && action == that.action;
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }
    }

    private static Map<actionData, List<Runnable>> m_keyCallbacks = new HashMap<actionData, List<Runnable>>();

    private static void udpateKeyCallback() {
        glfwSetKeyCallback(Window.getHandle(), (window, key, scancode, action, mods) -> {
            Input outer = new Input();
			Input.actionData actionKey = outer.new actionData(key, action); 

            //actionKey.print();

            if(m_keyCallbacks.containsKey(actionKey)) {
                List<Runnable> cbs = m_keyCallbacks.get(actionKey);
                for(Runnable cb: cbs) {cb.run();} 
            }
		}); 
    }

    /**
     * Adds key callback that will be executed when a certain conditions are met.
     * @param keyCode Key code of the key that must be pressed.
     * @param act Action that must happen (KEY_PRESS/KEY_RELEASE/KEY_REPEAT).
     * @param callback Callback that shall be executed.
     */
    public static void addKeyCallback(int keyCode, int act, Runnable callback) {
        Input outer = new Input();
        actionData key = outer.new actionData(keyCode, act);
        if(m_keyCallbacks.containsKey(key)) {
            m_keyCallbacks.get(outer.new actionData(keyCode, act)).add(callback);
        } else {
            List<Runnable> newList = new ArrayList<>();
            newList.add(callback);
            m_keyCallbacks.put(key, newList);
        }
        udpateKeyCallback();
    }

    /**
     * Returns current mouse position in pixels.
     * @return
     */
    public static Vector2i getMousePosition() {
        return new Vector2i(m_xpos, m_ypos);
    }

    public static void update() {
        Vector2i mousePos = getMousePosition();
        m_deltaMousePos = m_lastMousePos.sub(mousePos);
        m_deltaMousePos.mul(-1, 1);
        m_lastMousePos = mousePos; 
    }

    public static final int KEY_RELEASE = GLFW_RELEASE;
    public static final int KEY_PRESS = GLFW_PRESS;
    public static final int KEY_REPEAT = GLFW_REPEAT;

    public static final int MOUSE_BUTTON_LEFT = GLFW_MOUSE_BUTTON_LEFT;
    public static final int MOUSE_BUTTON_RIGHT = GLFW_MOUSE_BUTTON_RIGHT;
    public static final int MOUSE_BUTTON_MIDDLE = GLFW_MOUSE_BUTTON_MIDDLE;

    public static final int KEY_CODE_Q = GLFW_KEY_Q;
    public static final int KEY_CODE_W = GLFW_KEY_W;
    public static final int KEY_CODE_E = GLFW_KEY_E;
    public static final int KEY_CODE_R = GLFW_KEY_R;
    public static final int KEY_CODE_T = GLFW_KEY_T;
    public static final int KEY_CODE_Y = GLFW_KEY_Y;
    public static final int KEY_CODE_U = GLFW_KEY_U;
    public static final int KEY_CODE_I = GLFW_KEY_I;
    public static final int KEY_CODE_O = GLFW_KEY_O;
    public static final int KEY_CODE_P = GLFW_KEY_P;
    public static final int KEY_CODE_A = GLFW_KEY_A;
    public static final int KEY_CODE_S = GLFW_KEY_S;
    public static final int KEY_CODE_D = GLFW_KEY_D;
    public static final int KEY_CODE_F = GLFW_KEY_F;
    public static final int KEY_CODE_G = GLFW_KEY_G;
    public static final int KEY_CODE_H = GLFW_KEY_H;
    public static final int KEY_CODE_J = GLFW_KEY_J;
    public static final int KEY_CODE_K = GLFW_KEY_K;
    public static final int KEY_CODE_L = GLFW_KEY_L;
    public static final int KEY_CODE_Z = GLFW_KEY_Z;
    public static final int KEY_CODE_X = GLFW_KEY_X;
    public static final int KEY_CODE_C = GLFW_KEY_C;
    public static final int KEY_CODE_V = GLFW_KEY_V;
    public static final int KEY_CODE_B = GLFW_KEY_B;
    public static final int KEY_CODE_N = GLFW_KEY_N;
    public static final int KEY_CODE_M = GLFW_KEY_M;

    public static final int KEY_CODE_ESCAPE = GLFW_KEY_ESCAPE;
    public static final int KEY_CODE_ENTER = GLFW_KEY_ENTER;
    public static final int KEY_CODE_LEFT_SHIFT = GLFW_KEY_LEFT_SHIFT;
    public static final int KEY_CODE_RIGHT_SHIFT = GLFW_KEY_RIGHT_SHIFT;
    public static final int KEY_CODE_LEFT_ALT = GLFW_KEY_LEFT_ALT;
    public static final int KEY_CODE_RIGHT_ALT = GLFW_KEY_RIGHT_ALT;

}
