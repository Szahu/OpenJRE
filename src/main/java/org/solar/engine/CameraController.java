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
    private final float speed = 0.015f;

    private float rotX = 0;
    private float rotY = 0;
    private float rotZ = 0;

    public CameraController(Consumer<Matrix4f> setTransformMatrixCallback, Supplier<Matrix4f> getTransformMatrixCallback) {
        m_setTransformMatrixCallback = setTransformMatrixCallback;
        m_getTransformMatrixCallback = getTransformMatrixCallback;
    }

    private void moveLeftRight(Boolean dir) {
        offsetY -= Math.sin(rotZ)*Math.sin(rotX)*speed*(dir?1:-1);
        offsetX += Math.cos(rotZ)*Math.cos(rotX)*Math.cos(rotY)*speed*(dir?1:-1);
        offsetZ -= Math.cos(rotX)*Math.sin(rotY)*speed*(dir?1:-1);
    }

    private void moveUpDown(Boolean dir) {

    }

    private void moveForewardBackwadr(Boolean dir) {
        offsetY += Math.sin(rotX)*speed*(dir?1:-1) ; 
        offsetX -= Math.cos(rotX)*Math.sin(rotY)*speed*(dir?1:-1);
        offsetZ -= Math.cos(rotX)*Math.cos(rotY)*speed*(dir?1:-1);
    }

    public void update() {
        //Matrix4f newRot = new Matrix4f().identity().translate(new Vector3f(offsetX, offsetY, offsetZ));
        Matrix4f newTrans = new Matrix4f().identity().translate(new Vector3f(offsetX, offsetY, offsetZ)).rotateX(rotX).rotateY(rotY).rotateZ(rotZ).scale(1);
        setTransformMatrix(newTrans);
        if(Input.isKeyDown(GLFW_KEY_W)) {
            moveForewardBackwadr(true);
        }
        if(Input.isKeyDown(GLFW_KEY_A)) {
            moveLeftRight(false);
        }
        if(Input.isKeyDown(GLFW_KEY_S)) {
            moveForewardBackwadr(false); // false - move back
        }
        if(Input.isKeyDown(GLFW_KEY_D)) {
            moveLeftRight(true);
        }
        if(Input.isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
            offsetY += speed;
        }
        if(Input.isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
            offsetY -= speed;
        }
        if(Input.isKeyDown(GLFW_KEY_UP)) {
            rotX += Math.cos(rotY)*speed;
            rotZ -= Math.sin(rotY)*speed;
        }
        if(Input.isKeyDown(GLFW_KEY_LEFT)) {
            rotY += speed;
        }
        if(Input.isKeyDown(GLFW_KEY_RIGHT)) {
            rotY -= speed;
        }
        if(Input.isKeyDown(GLFW_KEY_DOWN)) {
            rotX -= Math.cos(rotY)*speed;
            rotZ += Math.sin(rotY)*speed;
        }
        if(Input.isKeyDown(GLFW_KEY_Q)) {
            rotZ += speed;
        }
        if(Input.isKeyDown(GLFW_KEY_E)) {
            rotZ -= speed;
        }
    }
}
