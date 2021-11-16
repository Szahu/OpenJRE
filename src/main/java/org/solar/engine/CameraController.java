package org.solar.engine;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.joml.Matrix4f;
import org.joml.Vector3f;


public class CameraController {

    private Consumer<Matrix4f> m_setTransformMatrixCallback;
    private Supplier<Matrix4f> m_getTransformMatrixCallback;
    private final static float yLimit   = 1.0f;
    private final static float delta    = 0.005f;
    private float offset                = 3;
    private float fl                    = 0;
    private boolean yDirection          = false;

    private void setTransformMatrix(Matrix4f newMat) {
        m_setTransformMatrixCallback.accept(newMat);
    }

    public CameraController(Consumer<Matrix4f> setTransformMatrixCallback, Supplier<Matrix4f> getTransformMatrixCallback) {
        m_setTransformMatrixCallback = setTransformMatrixCallback;
        m_getTransformMatrixCallback = getTransformMatrixCallback;
    }

    public void update() {
        if( isyDirection() )    goUp();
        else                    goDown();
        Matrix4f newTrans = new Matrix4f().identity().translate(new Vector3f(0, fl, offset));
        setTransformMatrix(newTrans);
        //offset += 0.01f;
    }


    public boolean isyDirection() {
        return yDirection;
    }

    public void setyDirection(boolean yDirection) {
        this.yDirection = yDirection;
    }

    private void goDown(){
        if( fl > - yLimit ) fl -= delta;
        else setyDirection(true);
    }

    private void goUp(){
        if( fl < yLimit) fl += delta;
        else setyDirection(false);
    }

}
