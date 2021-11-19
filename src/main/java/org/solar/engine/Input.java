package org.solar.engine;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    // TODO create own codes
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

    public static Vector2i getMousePosition() {
        return new Vector2i(m_xpos, m_ypos);
    }

    public static void update() {
        Vector2i mousePos = getMousePosition();
        m_deltaMousePos = m_lastMousePos.sub(mousePos);
        m_deltaMousePos.mul(-1, 1);
        m_lastMousePos = mousePos; 
    }
}
