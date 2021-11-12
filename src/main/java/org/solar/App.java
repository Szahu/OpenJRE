package org.solar;

import org.lwjgl.*;

import org.solar.engine.Engine;
import org.solar.engine.Utils;

public class App {

	private Engine m_engine;

	public void run() {
		Utils.LOG_SUCCESS("Hello LWJGL " + Version.getVersion() + "!");
		
		m_engine = new Engine();

		m_engine.initialize();
		m_engine.mainLoop();
		m_engine.terminate();
	}

	public static void main(String[] args) {
		new App().run();
	}

}
