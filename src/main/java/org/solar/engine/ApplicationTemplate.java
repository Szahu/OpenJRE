package org.solar.engine;

import java.io.IOException;

public abstract class ApplicationTemplate {
    public abstract void initialise() throws IOException;
    public abstract void update();
    public abstract void terminate();
}
