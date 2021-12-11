package org.solar.engine;

import java.util.function.Consumer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class gameLikeCameraController implements CameraControllerTemplate {
 
    private Transform m_transform = new Transform();
    
    private Vector2f m_angles = new Vector2f();
    private Vector3f m_position = new Vector3f();

    private float m_moveSpeed = 10.0f;

    private Consumer<Matrix4f> m_setTransformMatrixCallback;
    private void updateCameraTransformMatrix(Matrix4f mat) {
        m_setTransformMatrixCallback.accept(mat);
    }

    public gameLikeCameraController() {
        Input.addMouseCallback(Input.MOUSE_BUTTON_RIGHT, Input.ACTION_PRESS, () -> {
            Input.setCursorMode(Input.CURSOR_MODE_DISABLED);
        });

        Input.addMouseCallback(Input.MOUSE_BUTTON_RIGHT, Input.ACTION_RELEASE, () -> {
            Input.setCursorMode(Input.CURSOR_MODE_NORMAL);
        });
    }

    public void update() {
        //Input.setCursorMode(Input.CURSOR_MODE_NORMAL);
        if (Input.isMouseButtonDown(Input.MOUSE_BUTTON_RIGHT)) {
            m_angles.x -= Input.getMousePosDelta().x * 0.1f;
            m_angles.y += Input.getMousePosDelta().y * 0.1f;
        }

        Vector3f offset = new Vector3f();
        if(Input.isKeyDown(Input.KEY_CODE_A)) {
            offset.x += m_moveSpeed * Utils.getDeltaTime();
        }
        if(Input.isKeyDown(Input.KEY_CODE_S)) {
            offset.z += m_moveSpeed * Utils.getDeltaTime();
        }
        if(Input.isKeyDown(Input.KEY_CODE_D)) {
            offset.x -= m_moveSpeed * Utils.getDeltaTime();
        }
        if(Input.isKeyDown(Input.KEY_CODE_W)) {
            offset.z -= m_moveSpeed * Utils.getDeltaTime();
        }
        if(Input.isKeyDown(Input.KEY_CODE_LEFT_SHIFT)) {
            offset.y -= m_moveSpeed * Utils.getDeltaTime();
        }
        if(Input.isKeyDown(Input.KEY_CODE_SPACE)) {
            offset.y += m_moveSpeed * Utils.getDeltaTime();
        }

        offset.mul(-1);

        m_position.x += (float)Math.sin(Math.toRadians(m_angles.x)) * offset.z;
        m_position.z += (float)Math.cos(Math.toRadians(m_angles.x)) * offset.z;
        

        m_position.x += (float)Math.sin(Math.toRadians(m_angles.x - 90)) * offset.x;
        m_position.z += (float)Math.cos(Math.toRadians(m_angles.x - 90)) * offset.x;

        m_position.y += offset.y;

        m_transform.setPosition(m_position);

        Matrix4f mat = new Matrix4f().identity();
        mat.rotateX((float)Math.toRadians(-m_angles.y)).
        rotateY((float)Math.toRadians(-m_angles.x))
        .translate(m_position);

        updateCameraTransformMatrix(mat);

    }

    public void setTransformMatrixRefrence(Consumer<Matrix4f> ref) {
        m_setTransformMatrixCallback = ref;
    }

    public Transform getTransform() {
        return m_transform;
    }

    public boolean invertMatrix() {
        return false;
    }

}
