package org.solar.engine;

import java.util.HashMap;

import org.solar.engine.renderer.Texture;

public class Material {

    public enum Property {
        shininess
    }

    private Texture m_albedoTexture;
    private Texture m_normalTexture;
    private HashMap<Property, Float> properties;

    public Material() {
        properties = new HashMap<>();
        properties.put(Property.shininess, 1.0f);
    }

    public void setTexture(Texture.TextureType type, Texture texture) {
        switch(type) {
            case Albedo:
            m_albedoTexture = texture;
                break;
            case Normal:
            m_normalTexture = texture;
                break;
            default:
                Utils.LOG_ERROR("No such texture type as: " + type.toString());
                break;
            
        }
    }

    public Texture getTexture(Texture.TextureType type) {
        switch(type) {
            case Albedo:
                return m_albedoTexture;
            case Normal:
                return m_normalTexture;
            default:
                Utils.LOG_ERROR("No such texture type as: " + type.toString());
                return m_albedoTexture;            
        }
    }

    public void setProperty(Property prop, float value) {
        if(properties.containsKey(prop)) {
            properties.put(prop, value);
        } else {
            Utils.LOG_ERROR("No such property type as: " + prop.toString());
        }
    }

    public Float getProperty(Property prop) {
        if(properties.containsKey(prop)) {
            return properties.get(prop);
        } else {
            Utils.LOG_ERROR("No such property type as: " + prop.toString());
            return 0f;
        }
    }


}
