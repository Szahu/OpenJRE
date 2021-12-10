package org.solar.engine;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import imgui.ImGui;

/**
 * Handles all transform calculations.
 */
public class Transform {

    private float[] m_position = new float[] {0,0,0};
    private float[] m_rotation = new float[] {0,0,0};
    private float[] m_scale = new float[] {1,1,1};
    private Matrix4f m_transformMatrix = new Matrix4f().identity();

    /**
     * Get position of the transform.
     * @return
     */
    public Vector3f getPosition() {return new Vector3f(m_position);}
    /**
     * Get rotation of the transform.
     * @return
     */
    public Vector3f getRotation() {return new Vector3f(m_rotation);}
    /**
     * Get scale of the transform.
     * @return
     */
    public Vector3f getScale() {return new Vector3f(m_scale);}

    //TODO optimise so that recalculate is called only once
    /**
     * Set position of the transform.
     * @param newPosition New position.
     */
    public void setPosition(Vector3f newPosition) {m_position = Utils.vec3fToArray(newPosition); recalculateMatrix();}
    /**
     * Set rotation of the transform.
     * @param newRotation New rotation.
     */
    public void setRotation(Vector3f newRotation) {m_rotation = Utils.vec3fToArray(newRotation); recalculateMatrix();}
    /**
     * Set scale of the transform.
     * @param newScale New scale.
     */
    public void setScale(Vector3f newScale) {m_scale = Utils.vec3fToArray(newScale); recalculateMatrix();}

    /**
     * Set position of the transform.
     * @param newPosition New position.
     */
    public void setPosition(float[] newPosition) {m_position = newPosition; recalculateMatrix();}
    /**
     * Set rotation of the transform.
     * @param newRotation New rotation.
     */
    public void setRotation(float[] newRotation) {m_rotation = newRotation; recalculateMatrix();}
    /**
     * Set scale of the transform.
     * @param newScale New scale.
     */
    public void setScale(float[] newScale) {m_scale = newScale; recalculateMatrix();}

    /**
     * Translate the transform by a provided vector.
     * @param vec Vector to translate the transform by.
     */
    public void translate(Vector3f vec) {m_position = Utils.vec3fToArray(new Vector3f(m_position).add(vec)); recalculateMatrix();}
    /**
     * Rotate the transform by a provided vector.
     * @param vec Vector to rotate the transform by.
     */
    public void rotate(Vector3f vec) {m_rotation = Utils.vec3fToArray(new Vector3f(m_rotation).add(vec)); recalculateMatrix();}
    /**
     * Scale the transform by a provided vector.
     * @param vec Vector to scale the transform by.
     */
    public void scale(Vector3f vec) {m_scale = Utils.vec3fToArray(new Vector3f(m_scale).mul(vec)); recalculateMatrix();}

    /**
     * Apply all calculations.
     */
    public void recalculateMatrix() {
        //TODO implement local/gloabl rotation
            
        m_transformMatrix = new Matrix4f().identity()
        .translate(new Vector3f(m_position))
        .rotate((float)Math.toRadians(m_rotation[0]), new Vector3f(1, 0, 0))
        .rotate((float)Math.toRadians(m_rotation[1]), new Vector3f(0, 1, 0))
        .rotate((float)Math.toRadians(m_rotation[2]), new Vector3f(0, 0, 1))
        .scale(new Vector3f(m_scale)); 

    }

    /**
     * Renders ImGui interface to edit values of the transform.
     * @param title Title of the gui, must be uniqe to other gui elements.
     */
    public void debugGui(String title) {
        ImGui.dragFloat3(title + ": Translation", m_position, 0.1f);
        ImGui.dragFloat3(title + ": Rotation", m_rotation, 0.1f);
        ImGui.dragFloat3(title + ": Scale", m_scale, 0.1f);
        recalculateMatrix();
    }

    /**
     * Get the final transform Matrix.
     * @return
     */
    public Matrix4f getTransformMatrix() {return m_transformMatrix;}
}
