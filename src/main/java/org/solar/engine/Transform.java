package org.solar.engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform {

    private Vector3f m_position = new Vector3f(0,0,0);
    private Vector3f m_rotation = new Vector3f(0,0,0);
    private Vector3f m_scale = new Vector3f(1,1,1);

    private Matrix4f m_transformMatrix = new Matrix4f().identity();

    public Vector3f getPosition() {return m_position;}
    public Vector3f getRotation() {return m_rotation;}
    public Vector3f getScale() {return m_scale;}

    //TODO optimise so that recalculate is called only once
    public void setPosition(Vector3f newPosition) {m_position = newPosition; recalculateMatrix();}
    public void setRotation(Vector3f newRotation) {m_rotation = newRotation; recalculateMatrix();}
    public void setScale(Vector3f newScale) {m_scale = newScale; recalculateMatrix();}

    public void setPosition(float[] newPosition) {m_position = new Vector3f(newPosition); recalculateMatrix();}
    public void setRotation(float[] newRotation) {m_rotation = new Vector3f(newRotation); recalculateMatrix();}
    public void setScale(float[] newScale) {m_scale = new Vector3f(newScale); recalculateMatrix();}

    public void translate(Vector3f vec) {m_position.add(vec); recalculateMatrix();}
    public void rotate(Vector3f vec) {m_rotation.add(vec); recalculateMatrix();}
    public void scale(Vector3f vec) {m_scale.mul(vec); recalculateMatrix();}

    private void recalculateMatrix() {
        //TODO implement local/gloabl rotation
        m_transformMatrix = new Matrix4f().identity()
        .translate(m_position)
        .rotate((float) Math.toRadians(m_rotation.x), new Vector3f(1,0,0))
        .rotate((float) Math.toRadians(m_rotation.y), new Vector3f(0,1,0))
        .rotate((float) Math.toRadians(m_rotation.z), new Vector3f(0,0,1))
        .scale(m_scale);
    }

    public Matrix4f getTransformMatrix() {return m_transformMatrix;}
}
