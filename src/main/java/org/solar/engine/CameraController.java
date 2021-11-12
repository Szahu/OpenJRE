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

    private float offsetX = 0;
    private float offsetY = 0;
    private float offsetZ = 1;
    private final float speed = 0.01f;

    public CameraController(Consumer<Matrix4f> setTransformMatrixCallback, Supplier<Matrix4f> getTransformMatrixCallback) {
        m_setTransformMatrixCallback = setTransformMatrixCallback;
        m_getTransformMatrixCallback = getTransformMatrixCallback;
    }

    public void update() {

        Matrix4f newTrans = new Matrix4f().identity().translate(new Vector3f(offsetX, offsetY, offsetZ));

        setTransformMatrix(newTrans);
        if(Input.isKeyDown(GLFW_KEY_W)) {
            offsetZ += speed;
        }
        if(Input.isKeyDown(GLFW_KEY_A)) {
            offsetX -= speed;
        }
        if(Input.isKeyDown(GLFW_KEY_S)) {
            offsetZ -= speed;
        }
        if(Input.isKeyDown(GLFW_KEY_D)) {
            offsetX += speed;
        }
        if(Input.isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
            offsetY += speed;
        }
        if(Input.isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
            offsetY -= speed;
        }
    }
}
