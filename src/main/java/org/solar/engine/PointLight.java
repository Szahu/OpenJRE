package org.solar.engine;

import org.joml.Vector3f;

import imgui.ImGui;

public class PointLight {
    public Vector3f position = new Vector3f(0,0,0);
    private float[] posArr = new float[]{position.x, position.y, position.z};
    public Vector3f color = new Vector3f(1,1,1);
    public float ambientStrength = 0.1f;
    public float specularStrength = 0.5f;
    public void debugGui(String title) {
        ImGui.dragFloat3(title + " positions", posArr);
    }

}
