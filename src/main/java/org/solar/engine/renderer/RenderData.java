package org.solar.engine.renderer;

import org.solar.engine.Material;

public class RenderData {
    private VertexArray m_vertexArray;
    private Shader m_shader;
    private Material m_material;

    public VertexArray getVertexArray() {return m_vertexArray;}
    public Shader getShader() {return m_shader;}
    public Material getMaterial() {return m_material;}

    public RenderData(VertexArray vao, Shader shader, Material material) {
        m_vertexArray = vao;
        m_shader = shader;
        m_material = material;
    }
    
}
