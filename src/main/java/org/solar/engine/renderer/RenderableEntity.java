package org.solar.engine.renderer;

import java.util.ArrayList;
import java.util.List;

import org.solar.engine.Transform;

public class RenderableEntity {
    
    private Transform m_transform;
    private List<RenderData> m_renderData;

    public Transform getTransform() {return m_transform;}
    public List<RenderData> getRenderData(){return m_renderData;}

    public RenderableEntity(RenderData ...renderData) {
        m_transform = new Transform();
        m_renderData = new ArrayList<>();
        for(RenderData data : renderData) {
            m_renderData.add(data);
        }
    }



}
