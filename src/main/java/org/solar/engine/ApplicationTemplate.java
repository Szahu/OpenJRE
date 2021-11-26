package org.solar.engine;

public abstract class ApplicationTemplate {
    public abstract void initialise() throws Exception;
    public abstract void update();
    public abstract void terminate();
    public void run() throws Exception {
		Engine m_engine = new Engine();
		m_engine.initialize();
		this.initialise();
		m_engine.mainLoop(() -> this.update());
		this.terminate();
		m_engine.terminate();
    }
}
