package org.solar;

import org.lwjgl.*;
import org.solar.engine.ApplicationTemplate;
import org.solar.engine.Engine;
import org.solar.engine.Utils;
import java.io.IOException;

/**
 * Entry point of the application.
 */
public class App {

	private ApplicationTemplate m_application;

	/**
	 * Takes in an application to be executed by the engine.
	 * @param appToRun Application instance to be ran.
	 */
	public App(ApplicationTemplate appToRun) {
		m_application = appToRun;
	}

	/**
	 * Main entry point of the whole system.
	 * @throws IOException
	 */
	public void run() throws IOException{
		Utils.LOG_SUCCESS("Hello LWJGL " + Version.getVersion() + "!");
		Engine m_engine = new Engine();
		m_engine.initialize();
		m_application.initialise();
		m_engine.mainLoop(() -> m_application.update());
		m_application.terminate();
		m_engine.terminate();
	}

	public static void main(String[] args) throws IOException {
		new App(new testApp()).run();
	}

}
