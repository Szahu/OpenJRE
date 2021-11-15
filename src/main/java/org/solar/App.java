package org.solar;

import org.lwjgl.*;
import org.solar.engine.ApplicationTemplate;
import org.solar.engine.Engine;
import org.solar.engine.Utils;

//########################
//APPLICATION ENTRY POINT
//########################

public class App {

	private Engine m_engine;
	private final ApplicationTemplate m_application;

	public App(ApplicationTemplate appToRun) {
		m_application = appToRun;
	}

	public void run() {
		Utils.LOG_SUCCESS("Hello LWJGL " + Version.getVersion() + "!");
		
		m_engine = new Engine();

		m_engine.initialize();
		m_application.initialise();
		m_engine.mainLoop(m_application::update);
		m_application.terminate();
		m_engine.terminate();
	}

	public static void main(String[] args) {
		new App(new testApp()).run();
	}

}
