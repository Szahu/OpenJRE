package org.solar;

import org.lwjgl.*;
import org.solar.engine.Engine;
import java.io.IOException;
import org.solar.engine.Utils;

public class App {
	public void run() throws IOException, RuntimeException {
		Utils.LOG_SUCCESS("Hello LWJGL " + Version.getVersion() + "!");

		Engine m_engine = new Engine();

		m_engine.initialize();
		m_engine.mainLoop();
		m_engine.terminate();
	}

	public static void main(String[] args) throws IOException {
		new App().run();
	}
}
