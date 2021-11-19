package org.solar.engine;

import java.util.function.Consumer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import imgui.ImGui;

public class DebugCameraController extends CameraControllerTemplate {
    private Transform m_Transform;
    private Vector3f m_lookAtPoint = new Vector3f(0,0,0);
    private float[] m_yawAngle = {0.0f};
    private float[] m_pitchAngle = {30.0f};
    private float m_distanceFromCenter = 10.0f;

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
    }

    @Override
    public void setTransformMatrixRefrence(Consumer<Matrix4f> callback) {
        m_setTransformMatrixCallback = callback;
    }   

    @Override
    public void update() {
        float height = (float)Math.sin(Math.toRadians(m_pitchAngle[0])) * m_distanceFromCenter;
        float x = (float)Math.sin(Math.toRadians(m_yawAngle[0])) * m_distanceFromCenter * (float)Math.cos(Math.toRadians(m_pitchAngle[0]));
        float z = (float)Math.cos(Math.toRadians(m_yawAngle[0])) * (float)Math.cos(Math.toRadians(m_pitchAngle[0])) * m_distanceFromCenter;
        m_Transform.setPosition(new Vector3f(x, height, z));
        updateCameraTransformMatrix(new Matrix4f().lookAt(m_Transform.getPosition(), m_lookAtPoint, new Vector3f(0,1,0)));

        ImGui.dragFloat("Pitch", m_pitchAngle);
        ImGui.dragFloat("Yaw", m_yawAngle);
    }
}
