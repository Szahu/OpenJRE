package org.solar.engine;

import org.joml.Matrix4f;

public class CameraController extends Camera {
    public CameraController(int width, int height) {
        super(width, height);
    }

    @Override
    public void setTransformMatrix(Matrix4f newMatrix) {
        super.setTransformMatrix(newMatrix);
    }

    public void update() {
        
    }
}
