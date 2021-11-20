package org.solar.engine;

import org.joml.Matrix4f;

/**
 * Handles rendering calculations.
 */
public class Camera {
    
    private static final float m_FOV = (float) Math.toRadians(60.0f);
    private static final float m_Z_NEAR = 0.01f;
    private static final float m_Z_FAR = 1000.f;
    private Matrix4f m_projectionMatrix;
    private Matrix4f m_transformMatrix;

    private CameraControllerTemplate m_CameraController;

    /**
     * Returns current projectin matrix of the camera. 
     * @return
     */
    public Matrix4f getProjectionMatrix() {return m_projectionMatrix;}

    /**
     * Returns current view matrix of the camera. 
     * @return
     */
    public Matrix4f getViewMatrix() {return m_transformMatrix;}

    /**
     * Returns projectin matrix multiplied by view matrix of the camera. 
     * @return
     */
    public Matrix4f getViewProjectionMatrix() {return m_projectionMatrix.mul(m_transformMatrix);}

    /**
     * Set's value of camera's view matrix.
     * @param newMatrix New view matrix;
     */
    public void setViewMatrix(Matrix4f newMatrix) {
        m_transformMatrix = newMatrix;
    }

    private void recalculateProjection(float aspectRatio) {
        m_projectionMatrix = new Matrix4f().perspective(m_FOV, aspectRatio, m_Z_NEAR, m_Z_FAR);
    }

    /**
     * Initialises the camera class.
     * @param width Width of the screen.
     * @param height Height of the screen.
     * @param cameraController Camera controller to be attached.
     */
    public Camera(int width, int height, CameraControllerTemplate cameraController) {
        
        float aspectRatio = (float) width / height;
        recalculateProjection(aspectRatio);
        m_transformMatrix = new Matrix4f().identity();

        Event.addWindowResizeCallback((newWidth, newHeight) -> {
            recalculateProjection((float) newWidth / newHeight);
        });
        
        m_CameraController = cameraController;
        m_CameraController.setTransformMatrixRefrence((Matrix4f newMat) -> {m_transformMatrix = newMat;});
    }

    /**
     * To be called every frame. Updates attached CameraController class
     */
    public void update() {
        m_CameraController.update();
    }



}