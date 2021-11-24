package org.solar.engine;

import java.io.IOException;

public abstract class ApplicationTemplate {
    public abstract void initialise() throws IOException;
    public abstract void update();
    public abstract void terminate();
    public void run() throws IOException {
		Engine m_engine = new Engine();
		m_engine.initialize();
		this.initialise();
		m_engine.mainLoop(() -> this.update());
		this.terminate();
		m_engine.terminate();
    }
}
