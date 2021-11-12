package org.solar.engine;

import static org.lwjgl.glfw.GLFW.*;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.joml.Matrix4f;
import org.joml.Vector3f;


public class CameraController {

    private Consumer<Matrix4f> m_setTransformMatrixCallback;
    private Supplier<Matrix4f> m_getTransformMatrixCallback;

    private void setTransformMatrix(Matrix4f newMat) {
        m_setTransformMatrixCallback.accept(newMat);
    }

    private float offset = 0;

    public CameraController(Consumer<Matrix4f> setTransformMatrixCallback, Supplier<Matrix4f> getTransformMatrixCallback) {
        m_setTransformMatrixCallback = setTransformMatrixCallback;
        m_getTransformMatrixCallback = getTransformMatrixCallback;
    }

    public void update() {
        Matrix4f newTrans = new Matrix4f().identity().translate(new Vector3f(0, 0, offset));
        setTransformMatrix(newTrans);
        offset += 0.01f;
    }
}
