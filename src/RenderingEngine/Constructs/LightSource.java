package RenderingEngine.Constructs;

import java.awt.Color;

/**
 * LightSource represents a point lightSource, that works in the same way as Camera and can be used to light a scene and generate shadows based on what it sees.
 * 
 * @author Eero Lempiö el415524@student.uta.fi
 */
public class LightSource extends Camera{
    private float m_angle;
    private float m_distance;
    private float m_intensity;
    private Color m_color;
    
    private float[] m_zBuffer;
    
    public LightSource(String name, Transform transform, float angle, float distance, Color color, float intensity) {
        super(name, transform, angle, 1, 0.1f, distance);
        m_angle = angle;
        m_distance = distance;
        m_color = color;
        m_intensity = intensity;
    }
    
    /**
    * Returns a float array of size 3 representing this light's color in BGR
    */
    public float[] getColorValues(){
        float[] color = m_color.getColorComponents(m_color.getColorSpace(), null);
        return new float[]{color[2], color[1], color[0]};
    }
    
    /**
    * Returns a float array of size 3 representing this light's angle, distance and intensity
    */
    public float[] getLightParameters(){
            return new float[]{m_angle, m_distance, m_intensity};
    }
    
    /**
    * Returns this Light's color
    */
    public Color getColor(){return m_color;}
    
    /**
    * Returns this Light's angle
    */
    public float getAngle() {return m_angle;}
    
    /**
    * Returns this Light's distance
    */
    public float getDistance() {return m_distance;}
    
    /**
    * Returns this Light's intensity
    */
    public float getIntensity(){return m_intensity;}
    
    /**
    * Returns this Light's zBuffer, which is a float array representation of what this LigthSource sees
    */
    public float[] getzBuffer(){return m_zBuffer;};
    
    /**
    * Sets this Light's color to Color color
    */
    public void setColor(Color color){m_color = color;}
    
    /**
    * Sets this Light's angle to float angle, distance to float distance and intensity to float intensity
    */
    public void setLightParameters(float angle, float distance, float intensity){
        m_angle = angle;
        m_distance = distance;
        m_intensity = intensity;
        setProjection(m_angle, 1, 0.1f, m_distance);
    }
    
    /**
    * Sets this Light's intensity to float intensity
    */
    public void setIntensity(float intensity){m_intensity = intensity;}
    
    /**
    * Sets this Light's angle to float angle
    */
    public void setAngle(float angle) {
        m_angle = angle;
        setProjection(m_angle, 1, 0.1f, m_distance);
    }
    
    /**
    * Sets this Light's distance to float distance
    */
    public void setDistance(float distance) {m_distance = distance; setProjection(m_angle, 1, 0.1f, m_distance);}

    /**
    * Sets this Light's zBuffer to float[] zBuffer
    */
    public void setzBuffer(float[] zBuffer){m_zBuffer = zBuffer;};
}