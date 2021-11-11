package org.solar.engine;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/*
EVENT SYSTEM:
How to use: 
create Event with a label, then activate it each time you want to call all the callbacks, using the same label. 
Add callbacks using addCallbackFunction, example: 

Event.createEvent("windowResize");

Event.addEventCallback("windowResize", () -> {
    System.out.println("Window has been resized");
    return 0;
});

Event.activateEvent("windowResize");
     
*/

public class Event { 

    private static Map<String, List<Supplier<Integer>>> m_eventsList;

    @FunctionalInterface
    interface WindowResizeCallback<Int> {
        public void accept(int width, int height);
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
            m_eventsList = new HashMap<String, List<Supplier<Integer>>>();
        }
    }

    public static void createEvent(String eventLabel) {
    
        if(!m_eventsList.containsKey(eventLabel)) {
            m_eventsList.put(eventLabel, new ArrayList<>());
        } else {
            System.out.println("ERROR, SUCH EVENT ALREADY EXISTS: " + eventLabel);
        }
    }

    public static void activateEvent(String eventLabel) {
    
        if(m_eventsList.containsKey(eventLabel)) {
            List<Supplier<Integer>> callbacks = m_eventsList.get(eventLabel);
            for(int i = 0;i < callbacks.size();i++){
                callbacks.get(i).get();
            }
        } 
        else {
            System.out.println("NO SUCH EVENT AS: " + eventLabel);
        }
    }

    public static void activateWindowResizeEvent(int width, int height) {
        activateEvent("windowResize"); 
        
        for(int i = 0;i < m_ResizeCallbacks.size();i++){
            m_ResizeCallbacks.get(i).accept(width, height);
        }
    }

    public static void addEventCallback(String eventLabel, Supplier<Integer> func) {
        if(m_eventsList.containsKey(eventLabel)) {
            m_eventsList.get(eventLabel).add(func);
        }
        else {
            System.out.println("NO SUCH EVENT AS: " + eventLabel);
        }
    }


    public static void AddKeyCallback(long windowHandle, int keyCode, int actionCode, Supplier<Integer> func) {
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
			if ( key == keyCode && action == actionCode )
                func.get();
		});
    }



}
