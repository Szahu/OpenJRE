package org.solar.engine;

import java.util.Vector;
import java.util.function.Consumer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class DebugCameraController extends CameraControllerTemplate {
    private Transform m_Transform;

    private Consumer<Matrix4f> m_setTransformMatrixCallback;
    private void updateCameraTransformMatrix(Matrix4f mat) {
        m_setTransformMatrixCallback.accept(mat);
    }

    public DebugCameraController() {
        m_Transform = new Transform();
        m_Transform.setPosition(new float[]{0,0,3});
    }

    @Override
    public void setTransformMatrixRefrence(Consumer<Matrix4f> callback) {
        m_setTransformMatrixCallback = callback;
    }   

    @Override
    public void update() {
        m_Transform.debugGui("Camera Controller");
        Matrix4f trans = new Matrix4f().lookAt(m_Transform.getPosition(), new Vector3f(0,0,0), new Vector3f(0,1,0));
        updateCameraTransformMatrix(trans);
    }
}
