package org.solar.engine;

import org.joml.Vector3f;
import org.joml.Matrix4f;

public class Camera {
    
    
    private static final float m_FOV = (float) Math.toRadians(60.0f);
    private static final float m_Z_NEAR = 0.01f;
    private static final float m_Z_FAR = 1000.f;
    private Matrix4f m_projectionMatrix;
    private Matrix4f m_transformMatrix;
    private final Vector3f position;
    private final Vector3f rotation;

    private CameraController m_CameraController;

    public Matrix4f getProjectionMatrix() {return m_projectionMatrix;}
    public Matrix4f getWorldMatrix() {return m_transformMatrix.invert();}

    public void setTransformMatrix(Matrix4f newMatrix) {
        m_transformMatrix = newMatrix;
    }

    private void recalculateProjection(float aspectRatio) {
        m_projectionMatrix = new Matrix4f().perspective(m_FOV, aspectRatio, m_Z_NEAR, m_Z_FAR);
    }

    public Camera(int width, int height) {
        
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);

        float aspectRatio = (float) width / height;
        recalculateProjection(aspectRatio);
        m_transformMatrix = new Matrix4f().identity();

        Event.addWindowResizeCallback((newWidth, newHeight) -> {
            recalculateProjection((float) newWidth / newHeight);
        });
        
        m_CameraController = new CameraController((newMatrix) -> {
            m_transformMatrix = newMatrix;
        }, () -> {return m_transformMatrix;});
    }

    public Vector3f getPosition() { return position; }
    
    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if ( offsetZ != 0 ) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            position.z += (float)Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }
        if ( offsetX != 0) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            position.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }
        position.y += offsetY;
    }

    public Vector3f getRotation() { return rotation; }

    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;
    }

    public void update() {
        m_CameraController.update();
    }



}
