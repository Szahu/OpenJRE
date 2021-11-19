package org.solar.engine;

import java.util.function.Consumer;

import org.joml.Matrix4f;

public abstract class CameraControllerTemplate {

    public abstract void setTransformMatrixRefrence(Consumer<Matrix4f> callback);
    public abstract void update();

}
