package org.solar.engine;

import static org.lwjgl.glfw.GLFW.*;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.lang.Math;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector2i;

public class CameraController {

    private Consumer<Matrix4f> m_setTransformMatrixCallback;
    private Supplier<Matrix4f> m_getTransformMatrixCallback;
    private Transformation transformation;
    private Matrix4f transformMatrix;
    private Vector3f offset;
    private Vector3f rotation;
    private float scale = 1.0f;
    private int sensitivity = 30;

    private void setTransformMatrix(Matrix4f newMat) {
        m_setTransformMatrixCallback.accept(newMat);
    }

    private void handleInput() {
        Vector2i mousePosDelta = Input.getMousePosDelta();
        rotation.y -= mousePosDelta.get(0) * sensitivity * 0.005;
        rotation.x += mousePosDelta.get(1) * sensitivity * 0.005;
        if (Input.isKeyDown(265)) { // Up arrow
            rotation.x += 0.01f;
        }
        if (Input.isKeyDown(264)) { // Down arrow
            rotation.x -= 0.01f;
        }
        if (Input.isKeyDown(263)) { // Left arrow
            rotation.y += 0.01f;
        }
        if (Input.isKeyDown(262)) { // Right arrow
            rotation.y -= 0.01f;
        }
        if (Input.isKeyDown(68)) { // D key
            offset.x += 0.01f;
        }
        if (Input.isKeyDown(65)) { // A key
            offset.x -= 0.01f;
        }
        if (Input.isKeyDown(83)) { // S key
            offset.z += 0.01f;
        }
        if (Input.isKeyDown(87)) { // W key
            offset.z -= 0.01f;
        }
        if (Input.isKeyDown(32)) { // Space bar
            offset.y += 0.01f;
        }
        if (Input.isKeyDown(340)) { // Left shift
            offset.y -= 0.01f;
        }
        if (Input.isKeyDown(67)) { // C key
            centreCamera();
        }
        if (Input.isKeyDown(80)) { // P key
            startOffset();
        }
    }

    public void centreCamera() {
        rotation = new Vector3f(0,0,0);
    }
    public void startOffset() {
        offset = new Vector3f(0,0,1.0f);
    }

    public CameraController(Consumer<Matrix4f> setTransformMatrixCallback, Supplier<Matrix4f> getTransformMatrixCallback, Transformation transformation) {
        m_setTransformMatrixCallback = setTransformMatrixCallback;
        m_getTransformMatrixCallback = getTransformMatrixCallback;
        offset = new Vector3f(0,0,1.0f);
        rotation = new Vector3f(0,0,0);
        this.transformation = transformation;
    }

    public void update() {
        handleInput();
        setTransformMatrix(transformation.getTransformMatrix(offset, rotation, scale));
    }
}
