package org.solar.engine;

import java.util.HashMap;

import org.solar.engine.renderer.Texture;

public class Material {

    public enum Property {
        shininess
    }

    private Texture m_diffuseTexture;
    private Texture m_normalTexture;
    private HashMap<Property, Float> properties;

    public Material(Texture ...textures) {
        properties = new HashMap<>();
        properties.put(Property.shininess, 1.0f);
        for(Texture tex : textures) {
            switch(tex.getType()) {
                case Diffuse:
                    m_diffuseTexture = tex;
                    break;
                case Normal:
                    m_normalTexture = tex;
                    break;
            }
        }
    }

    public void setTexture(Texture.TextureType type, Texture texture) {
        switch(type) {
            case Diffuse:
            m_diffuseTexture = texture;
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
            case Diffuse:
                return m_diffuseTexture;
            case Normal:
                return m_normalTexture;
            default:
                Utils.LOG_ERROR("No such texture type as: " + type.toString());
                return m_diffuseTexture;            
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
