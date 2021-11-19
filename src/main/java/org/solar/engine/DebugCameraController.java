package org.solar.engine;

import java.util.function.Consumer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class DebugCameraController extends CameraControllerTemplate {
    private Transform m_Transform;
    private Vector3f m_lookAtPoint = new Vector3f(0,0,0);
    private float m_yawAngle = 0.0f;
    private float m_pitchAngle = 0.0f;
    private float m_rollAngle = 0.0f;
    private float m_distanceFromCenter = 10.0f;
    private float m_height = 5.0f;

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
        //Matrix4f trans = new Matrix4f().lookAt(m_Transform.getPosition(), new Vector3f(0,0,0), new Vector3f(0,1,0));
        m_Transform.setPosition(new Vector3f(0.0f, m_height, (float)Math.sqrt(m_distanceFromCenter * m_distanceFromCenter - m_height * m_height)));
        updateCameraTransformMatrix(new Matrix4f().lookAt(m_Transform.getPosition(), m_lookAtPoint, new Vector3f(0,1,0)));
        m_Transform.debugGui("Camera Controller");
    }
}
