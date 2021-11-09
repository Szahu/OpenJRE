package org.solar.engine;

import static org.lwjgl.glfw.GLFW.*;

import java.util.function.Supplier;

public class Event {
    public static void AddKeyCallback(long windowHandle, int keyCode, int actionCode, Supplier<Integer> func) {
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
			if ( key == keyCode && action == actionCode )
                func.get();
		});
    }
}
