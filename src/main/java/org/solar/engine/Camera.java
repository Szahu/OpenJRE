package org.solar.engine;

import org.joml.Matrix4f;
import org.solar.engine.Event.WindowResizeCallback;

public class Camera {
    
    private static final float m_FOV = (float) Math.toRadians(60.0f);
    private static final float m_Z_NEAR = 0.01f;
    private static final float m_Z_FAR = 1000.f;
    private Matrix4f m_projectionMatrix;
    private Matrix4f m_transformMatrix;

    public Matrix4f getProjectionMatrix() {return m_projectionMatrix;}
    public Matrix4f getTransformMatrix() {return m_transformMatrix;}

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
          
    }



}
