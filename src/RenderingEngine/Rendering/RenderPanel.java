package RenderingEngine.Rendering;

import RenderingEngine.Constructs.LightSource;
import RenderingEngine.Constructs.EngineObject;
import RenderingEngine.Constructs.Camera;
import RenderingEngine.Constructs.Edge;
import RenderingEngine.Constructs.Transform;
import RenderingEngine.CoreComponents.Bitmap;
import RenderingEngine.CoreComponents.Matrix4;
import java.awt.Color;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;


/**
 * RenderPanel is the actual rendering engine that combines all the other classes in the RenderingEngine package to a usable visual representation
 * 
 * @author Eero Lempiö el415524@student.uta.fi
 */
public class RenderPanel extends JPanel{
    private Mode m_mode;
    
    /**
    * Mode enumeration denotes the available rendering modes
    */
    public static enum Mode{
        DEPTH, WIREFRAME, NORMAL, DIFFUSE, LIGHTED_STATIC, LIGHTED_DYNAMIC
    }
    
    private final Camera m_camera;
    private final Matrix4 m_identity;
    private Matrix4 m_screenSpaceTransform, m_inverseScreenSpaceTransform;
    
    private List<LightSource> m_lightSources;
    private Matrix4 m_lightScreenSpaceTransform;
    private int m_lightMapResolution;
    
    private List<EngineObject> m_renderObjects;
     
    private float m_fov;
    private float m_nearClip;
    private float m_farClip;
    
    private int m_width;
    private int m_height;
    
    private BufferedImage m_displayImage;
    private byte[] m_displayComponents;
    
    private byte[] m_zenithColor;
    private float[] m_ambientLightColor;
    private float m_ambientLightIntensity;
    
    /**
    * Creates a new RenderPanel, with the camera at zero position and lightMap resolution set to 2000, 
    * and initializes the Lists for EngineObjects and LightSources
    */
    public RenderPanel(){
        m_camera = new Camera(
                        "Camera",
                        new Transform(),
                        m_fov = 90,
                        (float)getWidth()/(float)getHeight(),
                        m_nearClip = 0.1f,
                        m_farClip = 100);
        m_lightSources = new ArrayList<>();
        setLightMapResolution(2000);
        
        m_renderObjects = new ArrayList<>();
        
        m_identity = new Matrix4().initIdentity();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {resize();}
        });
        setMode(Mode.LIGHTED_DYNAMIC);
        
        m_zenithColor = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00};
        m_ambientLightColor = new float[]{0,0,0};
        m_ambientLightIntensity = 0;
    }
    
    /**
    * Returns this RenderPanels Cameras Transform
    */
    public Transform getCameraTransform() {return m_camera.getTransform();}
    
    /**
    * Returns this RenderPanels current rendering mode as int
    */
    public int getMode(){return m_mode.ordinal();}
    
    /**
    * Returns a new float array of size 3, containing the field of view, near- and far - clipping distances of this RenderPanel and Camera
    */
    public float[] getProjectionParameters(){
        return new float[]{m_fov, m_nearClip, m_farClip};
    }
    
    /**
    * Returns a new Color representing the ambient light color of this RenderPanel
    */
    public Color getAmbientLightColor (){
        return new Color((int)(m_ambientLightColor[2]*255), (int)(m_ambientLightColor[1]*255), (int)(m_ambientLightColor[0]*255));
    }
    
    /**
    * Returns a new Color representing the zenith (background) color of this RenderPanel
    */
    public Color getZenithColor(){
        return new Color((int)(m_zenithColor[2] & 0XFF), (int)(m_zenithColor[1] & 0XFF), (int)(m_zenithColor[0] & 0XFF));
    }
    
    /**
    * Returns the ambient light intensity of this RenderPanel
    */
    public float getAmbientLightIntensity(){return m_ambientLightIntensity;}
    
    /**
    * Returns the ambient light map resolution of this RenderPanel
    */
    public int getLightMapResolution(){return m_lightMapResolution;}

    /**
    * Sets the transform of this RenderPanels Camera to Tranform transform
    */
    public void setCameraTransform(Transform camera) {m_camera.setTransform(camera);}
    
    /**
    * Sets this RenderPanels rendering mode to Mode mode
    */
    public void setMode(Mode mode){m_mode = mode;}
    
    /**
    * Sets the List of LightSources of this RenderPanel to List LightSources and checks if their zBuffers need to be calculated
    */
    public void setLightSources(List<LightSource> lightSources){m_lightSources = lightSources; checkLightBufferRefresh();}
    
    /**
    * Sets the light map resolution of this RenderPanel to int resolution
    */
    public void setLightMapResolution(int resolution){
        m_lightMapResolution = resolution;
        m_lightScreenSpaceTransform = new Matrix4().initScreenSpaceTransform(resolution/2, resolution/2);
        checkLightBufferRefresh();
    }
    
    /**
    * Sets the List of EngineObjects of this RenderPanel to List EngineObjects and checks if the LghtSources zBuffers need to be calculated
    */
    public void setRenderObjects(List<EngineObject> renderObjects) {m_renderObjects = renderObjects; checkLightBufferRefresh();}
    
    /**
    * Sets the the projection of this RenderPanels Camera using float fov, float nearClip, float farClip
    */
    public void setProjection(float fov, float nearClip, float farClip){
        m_camera.setProjection(m_fov = fov, (float)getWidth()/(float)getHeight(), m_nearClip = nearClip, m_farClip = farClip);
    };
    
    /**
    * Sets the the illumination parameters of this RenderPanel to Color zenithColor, Color ambientLightColor, float ambientLightIntensity, int lightMapResolution
    */
    public void setIllumination(Color zenithColor, Color ambientLightColor, float ambientLightIntensity, int lightMapResolution){
        byte[] zC = new byte[]{(byte)zenithColor.getBlue(), (byte)zenithColor.getGreen(), (byte)zenithColor.getRed()};
        float[] aC = new float[]{ambientLightColor.getBlue()/255, ambientLightColor.getGreen()/255, ambientLightColor.getRed()/255};
        m_zenithColor = zC;
        m_ambientLightColor = aC;
        m_ambientLightIntensity = ambientLightIntensity;
        setLightMapResolution(lightMapResolution);
    }
    
    /**
    * Renders an image to this RenderPanel
    */
    public void render(){
        drawFrame();
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics.drawImage(m_displayImage, 0, 0, getWidth(), getHeight(), null);
    }
    
    private void checkLightBufferRefresh(){
        if(m_mode == Mode.LIGHTED_STATIC)
            refreshLightBuffers();
    }
  
    private void resize(){
        int width = getWidth();
        int height = getHeight();
        m_width = width > 0? width : 1;
        m_height = height > 0? height : 1;
        
        m_screenSpaceTransform = new Matrix4().initScreenSpaceTransform(m_width/2, m_height/2);
        m_inverseScreenSpaceTransform = new Matrix4().initInverseScreenSpaceTransform(m_width/2, m_height/2);
        
        m_displayImage = new  BufferedImage(m_width, m_height, BufferedImage.TYPE_3BYTE_BGR);
        m_displayComponents = ((DataBufferByte)m_displayImage.getRaster().getDataBuffer()).getData();
        setProjection(m_fov, m_nearClip, m_farClip);
        render();
    }
    
    private void drawFrame(){
        int width = m_width;
        int height = m_height;
        List<Edge> edges = getRenderObjectsEdges(m_camera.getViewProjection(), m_screenSpaceTransform);
        Bitmap frame = getNewFrame(width, height);
        switch(m_mode){
            case DEPTH:
                EdgeDrawer.drawDepth(frame, edges);
                break;
            case WIREFRAME:
                EdgeDrawer.drawWireFrame(frame, edges);
                break;
            case NORMAL:
                EdgeDrawer.drawNormal(frame, edges);
                break;
            case DIFFUSE:
                EdgeDrawer.drawDiffuse(frame, edges);
                break;
            case LIGHTED_STATIC:
                EdgeDrawer.drawLighted(
                        frame, edges, m_lightSources,
                        m_inverseScreenSpaceTransform, m_lightScreenSpaceTransform, m_camera.getInverseViewProjection(),
                        m_identity, m_lightMapResolution, m_ambientLightColor,m_ambientLightIntensity);
                break;
            default:
                refreshLightBuffers();
                EdgeDrawer.drawLighted(
                        frame, edges, m_lightSources,
                        m_inverseScreenSpaceTransform, m_lightScreenSpaceTransform, m_camera.getInverseViewProjection(),
                        m_identity, m_lightMapResolution, m_ambientLightColor,m_ambientLightIntensity);
        }
        if(width == m_width && height == m_height)
            frame.copyToByteArray(m_displayComponents);
    }
    
    private void refreshLightBuffers(){
        for(LightSource ls: m_lightSources){
            List<Edge> edges = getRenderObjectsEdges(ls.getViewProjection(), m_lightScreenSpaceTransform);
            ls.setzBuffer(EdgeDrawer.drawzBuffer(edges, m_lightMapResolution));
        }
    }
    
    private List<Edge> getRenderObjectsEdges(Matrix4 viewProjection, Matrix4 screenSpaceTransform){
        List<Edge> edges = new ArrayList<>();
        for(EngineObject o : m_renderObjects)
            edges.addAll(EdgeCalculator.getEdges(viewProjection, screenSpaceTransform, m_identity, o));
        return edges;
    }
    
    private Bitmap getNewFrame(int width, int height){
        Bitmap frame = new Bitmap(width, height);
        frame.clear(m_zenithColor);
        return frame;
    }
}
