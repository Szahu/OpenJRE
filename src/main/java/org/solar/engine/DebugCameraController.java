package org.solar.engine;

import java.util.function.Consumer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;

public class DebugCameraController extends CameraControllerTemplate {
    private Transform m_Transform;
    private Vector3f m_lookAtPoint = new Vector3f(0,0,0);
    private float m_yawAngle = 0.0f;
    private float m_pitchAngle = 30.0f;
    private float m_distanceFromCenter = 10.0f;
    private float m_inputSensitivity = 1.0f;


    private Consumer<Matrix4f> m_setTransformMatrixCallback;
    private void updateCameraTransformMatrix(Matrix4f mat) {
        m_setTransformMatrixCallback.accept(mat);
    }

    @Override
    public boolean invertMatrix() {
        return false;
    }

    public DebugCameraController() {
        m_Transform = new Transform();
        m_Transform.setPosition(new Vector3f(0,0,3));
        Window.setVsync(false);
    }

    @Override
    public void setTransformMatrixRefrence(Consumer<Matrix4f> callback) {
        m_setTransformMatrixCallback = callback;
    }   

    @Override
    public void update() {

        if(Input.isMouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
            m_yawAngle -= Input.getMousePosDelta().x * m_inputSensitivity;
            m_pitchAngle -= Input.getMousePosDelta().y * m_inputSensitivity;
            if(m_pitchAngle >= 85) {m_pitchAngle = 85.0f;}
            if(m_pitchAngle <= -85) {m_pitchAngle = -85.0f;}
        }
        float height = (float)Math.sin(Math.toRadians(m_pitchAngle)) * m_distanceFromCenter;
        float x = (float)Math.sin(Math.toRadians(m_yawAngle)) * m_distanceFromCenter * (float)Math.cos(Math.toRadians(m_pitchAngle));
        float z = (float)Math.cos(Math.toRadians(m_yawAngle)) * (float)Math.cos(Math.toRadians(m_pitchAngle)) * m_distanceFromCenter;
        m_Transform.setPosition(new Vector3f(x, height, z));
        updateCameraTransformMatrix(new Matrix4f().lookAt(m_Transform.getPosition(), m_lookAtPoint, new Vector3f(0,1,0)));
    }
}
