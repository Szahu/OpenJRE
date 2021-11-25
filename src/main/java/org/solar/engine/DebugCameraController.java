package org.solar.engine;

import java.util.function.Consumer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.solar.engine.Input.*;
public class DebugCameraController extends CameraControllerTemplate {
    private Transform m_Transform;
    private Vector3f m_lookAtPoint = new Vector3f(0,0,0);
    private float m_yawAngle = 0.0f;
    private float m_pitchAngle = 30.0f;
    private float m_distanceFromCenter = 10.0f;
    private float m_inputSensitivity = 1.0f;
    private Vector3f m_cameraDirection = new Vector3f(0,0,0);
    private float m_moveSensitivity = 0.07f;
    private Vector3f m_positionOffset = new Vector3f(0,0,0);


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
    public Transform getTransform() {
        return m_Transform;
    }


    @Override
    public void setTransformMatrixRefrence(Consumer<Matrix4f> callback) {
        m_setTransformMatrixCallback = callback;
    }   

    @Override
    public void update() {

        if(Input.isMouseButtonDown(MOUSE_BUTTON_RIGHT)) {
            m_yawAngle -= Input.getMousePosDelta().x * m_inputSensitivity;
            m_pitchAngle -= Input.getMousePosDelta().y * m_inputSensitivity;
            if(m_pitchAngle >= 85) {m_pitchAngle = 85.0f;}
            if(m_pitchAngle <= -85) {m_pitchAngle = -85.0f;}
        }

        if(m_distanceFromCenter < 0)  {m_distanceFromCenter = 0.01f;}
        m_distanceFromCenter -= Input.getScrollInput();

        m_lookAtPoint.sub(m_Transform.getPosition(), m_cameraDirection);
        m_cameraDirection.normalize();

        Vector3f right = new Vector3f((float)Math.sin(Math.toRadians(m_yawAngle - 90f)), 0, (float)Math.cos(Math.toRadians(m_yawAngle - 90f)));
        right.mul(-1f);
        right.normalize();
        Vector3f up = new Vector3f(0,0,0);
        right.cross(m_cameraDirection, up);
        up.normalize();
        right.normalize();

        if(Input.isKeyDown(KEY_CODE_LEFT_SHIFT) && Input.isMouseButtonDown(MOUSE_BUTTON_LEFT)) {
            
            Vector3f deltaMouse = new Vector3f(Input.getMousePosDelta().x, Input.getMousePosDelta().y, 0);

            up.mul(deltaMouse.y * m_moveSensitivity);
            right.mul(deltaMouse.x * m_moveSensitivity);

            m_lookAtPoint.sub(up);
            m_lookAtPoint.sub(right);
            m_positionOffset.sub(up);
            m_positionOffset.sub(right);
        }


        float height = (float)Math.sin(Math.toRadians(m_pitchAngle)) * m_distanceFromCenter;
        float x = (float)Math.sin(Math.toRadians(m_yawAngle)) * m_distanceFromCenter * (float)Math.cos(Math.toRadians(m_pitchAngle));
        float z = (float)Math.cos(Math.toRadians(m_yawAngle)) * (float)Math.cos(Math.toRadians(m_pitchAngle)) * m_distanceFromCenter;
        Vector3f finalPosition = new Vector3f(0,0,0).add(m_positionOffset).add(new Vector3f(x, height, z));
        m_Transform.setPosition(finalPosition);

        updateCameraTransformMatrix(new Matrix4f().lookAt(m_Transform.getPosition(), m_lookAtPoint, new Vector3f(0,1,0)));

        
    }
}
