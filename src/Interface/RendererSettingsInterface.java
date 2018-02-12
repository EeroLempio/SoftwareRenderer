package Interface;


import java.awt.Color;




/**
 * RendererSettingsInterface is an interface for linking a RendererModel and a renderer settings editor together
 * 
 * @author Eero Lempiö el415524@student.uta.fi
 */
public interface RendererSettingsInterface {
    void setModel(RendererModel rendererModel);
    void setMode(int mode);
    void setViewParameters(int fov, float nearClip, float farClip);
    void setIlluminationParameters(Color zenithColor, Color ambientLightColor, float ambientLightIntensity, int lightMapResolution);
}
