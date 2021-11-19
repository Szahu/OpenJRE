package org.solar.engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import imgui.ImGui;

public class Transform {

    private float[] m_position = new float[] {0,0,0};
    private float[] m_rotation = new float[] {0,0,0};
    private float[] m_scale = new float[] {1,1,1};
    private Matrix4f m_transformMatrix = new Matrix4f().identity();

    public Vector3f getPosition() {return new Vector3f(m_position);}
    public Vector3f getRotation() {return new Vector3f(m_rotation);}
    public Vector3f getScale() {return new Vector3f(m_scale);}

    //TODO optimise so that recalculate is called only once
    public void setPosition(Vector3f newPosition) {m_position = Utils.vec3fToArray(newPosition); recalculateMatrix();}
    public void setRotation(Vector3f newRotation) {m_rotation = Utils.vec3fToArray(newRotation); recalculateMatrix();}
    public void setScale(Vector3f newScale) {m_scale = Utils.vec3fToArray(newScale); recalculateMatrix();}

    public void setPosition(float[] newPosition) {m_position = newPosition; recalculateMatrix();}
    public void setRotation(float[] newRotation) {m_rotation = newRotation; recalculateMatrix();}
    public void setScale(float[] newScale) {m_scale = newScale; recalculateMatrix();}

    public void translate(Vector3f vec) {m_position = Utils.vec3fToArray(new Vector3f(m_position).add(vec)); recalculateMatrix();}
    public void rotate(Vector3f vec) {m_rotation = Utils.vec3fToArray(new Vector3f(m_rotation).add(vec)); recalculateMatrix();}
    public void scale(Vector3f vec) {m_scale = Utils.vec3fToArray(new Vector3f(m_scale).mul(vec)); recalculateMatrix();}

    public void recalculateMatrix() {
        //TODO implement local/gloabl rotation

        Matrix4f rotation = new Matrix4f();
        rotation.rotate((float)Math.toRadians(m_rotation[0]), 0, 1, 0);
        rotation.rotate((float)Math.toRadians(m_rotation[0]), 1, 0, 0);
        rotation.rotate((float)Math.toRadians(m_rotation[0]), 0, 0, 1);
        
        m_transformMatrix = new Matrix4f().identity()
        .translate(new Vector3f(m_position))
        .mul(rotation)
        .scale(new Vector3f(m_scale)); 

    }

    public void debugGui(String title) {
        ImGui.dragFloat3(title + ": Translation", m_position, 0.1f);
        ImGui.dragFloat3(title + ": Rotation", m_rotation, 0.1f);
        ImGui.dragFloat3(title + ": Scale", m_scale, 0.1f);
        recalculateMatrix();
    }

    public Matrix4f getTransformMatrix() {return m_transformMatrix;}
}
