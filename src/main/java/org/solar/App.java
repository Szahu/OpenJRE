package org.solar;

import org.lwjgl.*;
import org.solar.engine.ApplicationTemplate;
import org.solar.engine.Engine;
import org.solar.engine.Utils;
import java.io.IOException;

//########################
//APPLICATION ENTRY POINT
//########################

public class App {

	private ApplicationTemplate m_application;

	public App(ApplicationTemplate appToRun) {
		m_application = appToRun;
	}

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
		if( args.length != 0 ) new App( new testApp( args ) ).run();
		else new App( new testApp() ).run();
	}

}
