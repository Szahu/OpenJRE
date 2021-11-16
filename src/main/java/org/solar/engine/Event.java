package org.solar.engine;

import static org.lwjgl.glfw.GLFW.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Event {

    private static Map<String, List<Runnable>> m_eventsList;

    @SuppressWarnings("unused")
    @FunctionalInterface
    public interface WindowResizeCallback<Int> {
        void accept(int width, int height);
    }

    private static List<WindowResizeCallback<Integer>> m_ResizeCallbacks;

    public static void addWindowResizeCallback(WindowResizeCallback<Integer> func) {
        if(m_ResizeCallbacks == null) {
            m_ResizeCallbacks = new ArrayList<>();
        }

        m_ResizeCallbacks.add(func);
    }

    private Event() {}

    public static void initialise() {
        if(m_ResizeCallbacks == null) {
            m_ResizeCallbacks = new ArrayList<>();
        }

        if(m_eventsList == null) {
            m_eventsList = new HashMap<>();
        }
    }

    public static void createEvent(String eventLabel) {
    
        if(!m_eventsList.containsKey(eventLabel)) {
            m_eventsList.put(eventLabel, new ArrayList<>());
        } else {
            Utils.LOG_ERROR("ERROR, SUCH EVENT ALREADY EXISTS: " + eventLabel);
        }
    }

    public static void activateEvent(String eventLabel) {
    
        if(m_eventsList.containsKey(eventLabel)) {
            List<Runnable> callbacks = m_eventsList.get(eventLabel);
            for (Runnable callback : callbacks) {
                callback.run();
            }
        } 
        else {
            Utils.LOG_ERROR("NO SUCH EVENT AS: " + eventLabel);
        }
    }

    public static void activateWindowResizeEvent(int width, int height) {
        activateEvent("windowResize");

        for (WindowResizeCallback<Integer> m_resizeCallback : m_ResizeCallbacks) {
            m_resizeCallback.accept(width, height);
        }
    }
    @SuppressWarnings("unused")
    public static void addEventCallback(String eventLabel, Runnable func) {
        if(m_eventsList.containsKey(eventLabel)) {
            m_eventsList.get(eventLabel).add(func);
        }
        else {
            Utils.LOG_ERROR("NO SUCH EVENT AS: " + eventLabel);
        }
    }

    @SuppressWarnings("unused")
    public static void AddKeyCallback(long windowHandle, int keyCode, int actionCode, Supplier<Integer> func) {
        // Set up a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
			if ( key == keyCode && action == actionCode )
                func.get();
		});
    }



}
