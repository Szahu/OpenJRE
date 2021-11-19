package org.solar.engine;

import org.joml.Matrix4f;

public class Camera {
    
    private static final float m_FOV = (float) Math.toRadians(60.0f);
    private static final float m_Z_NEAR = 0.01f;
    private static final float m_Z_FAR = 1000.f;
    private Matrix4f m_projectionMatrix;
    private Matrix4f m_transformMatrix;

    private CameraController m_CameraController;

    public Matrix4f getProjectionMatrix() {return m_projectionMatrix;}
    public Matrix4f getViewMatrix() {return m_transformMatrix.invert();}

    public Matrix4f getViewProjectionMatrix() {return m_projectionMatrix.mul(m_transformMatrix.invert());}

    public void setTransformMatrix(Matrix4f newMatrix) {
        m_transformMatrix = newMatrix;
    }
    public Matrix4f getTramsformMatrix(){ return this.m_transformMatrix; }

    private void recalculateProjection(float aspectRatio) {
        m_projectionMatrix = new Matrix4f().perspective(m_FOV, aspectRatio, m_Z_NEAR, m_Z_FAR);
    }

    public Camera(int width, int height) {
        
        float aspectRatio = (float) width / height;
        recalculateProjection(aspectRatio);
        m_transformMatrix = new Matrix4f().identity();

        Event.addWindowResizeCallback((newWidth, newHeight) -> {
            recalculateProjection((float) newWidth / newHeight);
        });
        
        m_CameraController = new CameraController((newMatrix) -> {
            m_transformMatrix = newMatrix;
        });
    }

    public void update() {
        m_CameraController.update();
    }



}
