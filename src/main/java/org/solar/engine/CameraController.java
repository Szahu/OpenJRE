package org.solar.engine;

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.joml.Matrix4f;

public class CameraController {

    private Consumer<Matrix4f>  m_setTransformMatrixCallback;
    private Supplier<Matrix4f>  m_getTransformMatrixCallback;
    private Transform m_Transform   = new Transform();
    private float     offset        = 10;

    private void setTransformMatrix(Matrix4f newMat) { m_setTransformMatrixCallback.accept(newMat); }

    public CameraController(Consumer<Matrix4f> setTransformMatrixCallback) {
        m_setTransformMatrixCallback = setTransformMatrixCallback;
        m_Transform.setPosition(new float[] {1,1,4});
    }

    public void update() {
        m_Transform.debugGui("Camera Controller");
        setTransformMatrix(m_Transform.getTransformMatrix());
    }
}
