package org.solar.engine;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.List;
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


    static class EventObject {
        public String m_eventLabel;
        public List<Supplier<Integer>> m_callBacks;
        
        public EventObject(String label) {
            m_eventLabel = label;
            m_callBacks = new ArrayList<>();
        }
    } 

    private static List<EventObject> m_eventsList;
    //private static List<EventObject> m_eventsToDispatchQueue;

    private Event() {}

    //TODO check if event exists first
    public static void createEvent(String eventLabel) {
        if(m_eventsList == null) {
            m_eventsList = new ArrayList<>();
        }
        EventObject obj = new EventObject(eventLabel);
        m_eventsList.add(obj);
    }

    //To be optimised 
    //TODO provide some kind of error if error doesnt not exist
    public static void activateEvent(String eventLabel) {
        for(int i = 0; i < m_eventsList.size();i++) {
            EventObject obj = m_eventsList.get(i);
            if (obj.m_eventLabel == eventLabel) {
                for(int j = 0;j < obj.m_callBacks.size();j++) {
                    obj.m_callBacks.get(j).get();
                }
            }
        }
    }

    //To be optimised 
    //TODO provide some kind of error message if the event does not exists
    public static void addEventCallback(String eventLabel, Supplier<Integer> func) {
        for(int i = 0; i < m_eventsList.size();i++) {
            EventObject obj = m_eventsList.get(i);
            if (obj.m_eventLabel == eventLabel) {
                obj.m_callBacks.add(func);
            }
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
