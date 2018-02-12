package Interface;


import RenderingEngine.Constructs.BaseObject;
import RenderingEngine.Constructs.EngineObject;
import RenderingEngine.Constructs.LightSource;
import RenderingEngine.Constructs.Transform;
import RenderingEngine.CoreComponents.Bitmap;
import RenderingEngine.CoreComponents.Vector4;
import RenderingEngine.Rendering.RenderPanel;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoManager;


/**
 * RendererModel links the RenderingPanel rendering engine to an interface consisting of ObjectEditorInterface, ObjectSelectorInterface, RendererSettingsInterface,
 * and manages the undoable edits of the application, in addition to the actual LightSources and EngineObjects rendered inside the RenderPanel
 * 
 * @author Eero Lempiö el415524@student.uta.fi
 */
public class RendererModel {
    private final static int DEFAULT_RENDER_MODE = 5;
    private final static float DEFAULT_FOV = 90;
    private final static float DEFAULT_ZNEAR = 0.1f;
    private final static float DEFAULT_ZFAR = 100;
    private final static Color DEFAULT_ZENITH_COLOR = Color.PINK;
    private final static Color DEFAULT_AMBIENTLIGHT_COLOR  = Color.WHITE;
    private final static float DEFAULT_AMBIENTLIGHT_INTENSITY = 0.2f;
    private final static int DEFAULT_LIGHTMAP_RESOLUTION = 1024;
    private final static Transform DEFAULT_CAMERA = new Transform(new Vector4(0,1.5f,-4));
    
    private final RenderPanel m_renderPanel;
    
    private ObjectEditorInterface m_objectEditor;
    private ObjectSelectorInterface m_objectSelector;
    private RendererSettingsInterface m_renderSettings;

    
    private UndoManager m_undoManager;
    private List<LightSource> m_lightSources;
    private List<EngineObject> m_engineObjects;
    private BaseObject m_activeObject;
    
    /**
    * Creates a new RendererModel and loads the deault scene
    */
    public RendererModel(RenderPanel renderPanel, ObjectEditorInterface objectEditor, ObjectSelectorInterface objectSelector, RendererSettingsInterface renderSettings) {
        m_renderPanel = renderPanel;
        m_objectEditor = objectEditor;
        m_objectEditor.setModel(this);
        m_objectSelector = objectSelector;
        m_objectSelector.setModel(this);
        m_renderSettings = renderSettings;
        m_renderSettings.setModel(this);
        loadDefaultScene();
    }
    
    /**
    * Loads a new empty scene and resets all interface parameters clearing the UndoManager
    */
    public void loadNewScene(){
        m_objectSelector.reset();
        m_undoManager = new UndoManager();
        m_lightSources = new ArrayList<>();
        m_engineObjects = new ArrayList<>();
        m_renderPanel.setLightSources(m_lightSources);
        m_renderPanel.setRenderObjects(m_engineObjects);
        m_activeObject = null;
        resetAllObjectParams();
        resetRenderParams();
    }
    
    /**
    * Loads the default scene and resets all interface parameters clearing the UndoManager
    */
    public void loadDefaultScene(){
        loadNewScene();
        try{
            loadEngineObject(getResourceAsFile("Interface/Resources/Statue.obj"));
            setCurrentObjectTexture(ImageIO.read(getResourceAsFile("Interface/Resources/Statue.jpg")));
            loadEngineObject(getResourceAsFile("Interface/Resources/Plane.obj"));
            setCurrentObjectTexture(ImageIO.read(getResourceAsFile("Interface/Resources/Marble.jpg")));
            
            createLightSource();
            setCurrentObjectTransform(new Transform(new Vector4(20,8,3)).lookAt(new Vector4(0,0,0), new Vector4(0,1,0)));
            setCurrentObjectColor(Color.WHITE);
            createLightSource();
            setCurrentObjectTransform(new Transform(new Vector4(0,3,5)).lookAt(new Vector4(0,0,0), new Vector4(0,1,0)));
            setCurrentObjectColor(Color.GRAY);
            m_undoManager.discardAllEdits();
        }
        catch (IOException ex) {

        }        
    }
    
    /**
    * Returns this RendererModels UndoManager
    */
    public UndoManager getUndoManager(){return m_undoManager;}
    
    /**
    * Sets the rendering mode to int mode and saves an edit
    */
    public void setRenderMode(int mode){
        m_undoManager.addEdit(new AbstractUndoableEdit() {
            RenderPanel.Mode[] modes = RenderPanel.Mode.values();
            int oldMode = m_renderPanel.getMode();
            int newMode = mode;
            @Override
            public void undo() {
                super.undo();
                m_renderPanel.setMode(modes[oldMode]);
                refreshMode(oldMode);
            }
            @Override
            public void redo() {
                super.redo();
                m_renderPanel.setMode(modes[newMode]);
                refreshMode(newMode);
            }
            public String getPresentationName(){
               return "Change Mode"; 
            }
        });
        RenderPanel.Mode[] modes = RenderPanel.Mode.values();
        m_renderPanel.setMode(modes[mode]);
        m_renderPanel.render();
    }
    
    /**
    * Sets the view parameters to float fov, float zNear, float zFar and saves an edit
    */
    public void setViewParameters(float fov, float zNear, float zFar) {
        m_undoManager.addEdit(new AbstractUndoableEdit() {
            float[] oldParams = m_renderPanel.getProjectionParameters();
            float[] newParams = new float[]{fov, zNear, zFar};
            @Override
            public void undo() {
                super.undo();
                m_renderPanel.setProjection(oldParams[0], oldParams[1], oldParams[2]);
                refreshViewParameters(oldParams[0], oldParams[1], oldParams[2]);
            }
            @Override
            public void redo() {
                super.redo();
                m_renderPanel.setProjection(newParams[0], newParams[1], newParams[2]);
                refreshViewParameters(newParams[0], newParams[1], newParams[2]);
            }
            public String getPresentationName(){
               return "Change Camera Parameters"; 
            }
        });
        m_renderPanel.setProjection(fov, zNear, zFar);
        m_renderPanel.render();
    }
    
    /**
    * Sets the illumination parameters to Color zenithColor, Color ambientLightColor, float ambientLightIntensity, int lightMapResolution and saves an edit
    */
    public void setIlluminationParameters(Color zenithColor, Color ambientLightColor, float ambientLightIntensity, int lightMapResolution){
        m_undoManager.addEdit(new AbstractUndoableEdit() {
            Color[] oldColors = new Color[]{m_renderPanel.getZenithColor(), m_renderPanel.getAmbientLightColor()};
            float oldIntensity = m_renderPanel.getAmbientLightIntensity();
            int oldResolution = m_renderPanel.getLightMapResolution();
            Color[] newColors = new Color[]{zenithColor, ambientLightColor};
            float newIntensity = ambientLightIntensity;
            int newResolution = lightMapResolution;
            @Override
            public void undo() {
                super.undo();
                m_renderPanel.setIllumination(oldColors[0], oldColors[1], oldIntensity, oldResolution);
                refreshIlluminationParameters(oldColors[0], oldColors[1], oldIntensity, oldResolution);
            }
            @Override
            public void redo() {
                super.redo();
                m_renderPanel.setIllumination(newColors[0], newColors[1], newIntensity, newResolution);
                refreshIlluminationParameters(newColors[0], newColors[1], newIntensity, newResolution);
            }
            public String getPresentationName(){
               return "Change Lighting Parameters"; 
            }
        });
        m_renderPanel.setIllumination(zenithColor, ambientLightColor, ambientLightIntensity, lightMapResolution);
        m_renderPanel.render();
    }
    
    /**
    * Loads an OBJ file, adds it to the EngineObjects List, sets it as the current object and saves an edit
    */
    public void loadEngineObject(File file) throws IOException{
        EngineObject loadedObject = new EngineObject(
                file.getName(),
                new Transform(),
                OBJModelLoader.meshFromObjFile(file),
                new Bitmap(ImageIO.read(getResourceAsFile("Interface/Resources/Base.jpg")))
        );
        m_undoManager.addEdit(new AbstractUndoableEdit() {
            BaseObject oldObject = m_activeObject;
            EngineObject targetObject = loadedObject;
            @Override
            public void undo() {
                super.undo();
                m_engineObjects.remove(targetObject);
                m_objectSelector.removeObject(targetObject);
                m_activeObject = oldObject;
                resetAllObjectParams();
            }
            @Override
            public void redo() {
                super.redo();
                m_engineObjects.add(targetObject);
                m_activeObject = targetObject;
                resetAllObjectParams();
            }
            public String getPresentationName(){
               return "Load Model"; 
            }
        });
        m_engineObjects.add(loadedObject);
        m_activeObject = loadedObject;
        resetAllObjectParams();
    }
    /**
    * Converts a resource to a temporary file
    */
    private File getResourceAsFile(String resourcePath) throws IOException {
        InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
        if (in == null)
            return null; 
        File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
        tempFile.deleteOnExit();
        FileOutputStream out = new FileOutputStream(tempFile);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        return tempFile;
    }
    
    /**
    * Creates a LightSource, adds it to the LightSource List, sets it as the current object and saves an edit
    */
    public void createLightSource(){
        LightSource createdLight = new LightSource(
                "New Light source",
                new Transform(new Vector4(0,3,0)).lookAt(new Vector4(0,0,0), new Vector4(0,1,0)),
                90, 100, Color.WHITE, 0.9f);
        m_undoManager.addEdit(new AbstractUndoableEdit() {
            BaseObject oldObject = m_activeObject;
            LightSource targetLight = createdLight;
            @Override
            public void undo() {
                super.undo();
                m_lightSources.remove(targetLight);
                m_objectSelector.removeObject(targetLight);
                m_activeObject = oldObject;
                resetAllObjectParams();
            }
            @Override
            public void redo() {
                super.redo();
                m_lightSources.add(targetLight);
                m_activeObject = targetLight;
                resetAllObjectParams();
            }
            public String getPresentationName(){
               return "Create Lightsource"; 
            }
        });
        m_lightSources.add(createdLight);
        m_activeObject = createdLight;
        resetAllObjectParams();
    }
    
    /**
    * Deletes the current object, removes it from the appropriate List, sets the current object to null and saves an edit
    */
    public void delete(){
        m_undoManager.addEdit(new AbstractUndoableEdit() {
            BaseObject deleted = m_activeObject;
            @Override
            public void undo() {
                super.undo();
                if(deleted instanceof LightSource)
                    m_lightSources.add((LightSource)deleted);
                else
                    m_engineObjects.add((EngineObject)deleted);
                m_activeObject = deleted;
                resetAllObjectParams();
            }
            @Override
            public void redo() {
                super.redo();
                if(deleted instanceof LightSource)
                    m_lightSources.remove((LightSource)deleted);
                else
                    m_engineObjects.remove((EngineObject)deleted);
                m_objectSelector.removeObject(m_activeObject);
                m_activeObject = null;
                resetAllObjectParams();
            }
            public String getPresentationName(){
               return "Delete"; 
            }
        });
        if(m_activeObject instanceof LightSource){
            m_lightSources.remove((LightSource)m_activeObject);
        }
        else{
            m_engineObjects.remove((EngineObject)m_activeObject);
        }
        m_objectSelector.removeObject(m_activeObject);
        m_activeObject = null;
        resetAllObjectParams();
    }
    
    /**
    * Duplicates the current object, adds it to the appropriate List, sets it as the current object and saves an edit
    */
    public void duplicate(){
        BaseObject copy;
        if(m_activeObject instanceof LightSource){
            LightSource originalLight = (LightSource)m_activeObject;
            copy = new LightSource(
                    originalLight.toString(), originalLight.getTransform(), originalLight.getAngle(),
                    originalLight.getDistance(), originalLight.getColor(), originalLight.getIntensity());
            m_lightSources.add((LightSource)copy);
        }
        else{
            EngineObject originalObject = (EngineObject)m_activeObject;
            copy = new EngineObject(originalObject.toString() + "(copy)", originalObject.getTransform(), originalObject.getMesh(), originalObject.getTexture());
            m_engineObjects.add((EngineObject)copy);
        }
        m_undoManager.addEdit(new AbstractUndoableEdit() {
            BaseObject oldObject = m_activeObject;
            BaseObject newObject = copy;
            @Override
            public void undo() {
                super.undo();
                if(newObject instanceof LightSource)
                    m_lightSources.remove((LightSource)newObject);
                else
                    m_engineObjects.remove((EngineObject)newObject);
                m_activeObject = oldObject;
                resetAllObjectParams();
            }
            @Override
            public void redo() {
                super.redo();
                if(newObject instanceof LightSource)
                    m_lightSources.add((LightSource)newObject);
                else
                    m_engineObjects.add((EngineObject)newObject);
                m_activeObject = newObject;
                resetAllObjectParams();
            }
            public String getPresentationName(){
               return "Duplicate"; 
            }
        });
        m_activeObject = copy;
        resetAllObjectParams();
    }
    
    /**
    * Sets BaseObject activeObject as the current object and saves an edit
    */
    public void setactiveObject(BaseObject activeObject){
        m_undoManager.addEdit(new AbstractUndoableEdit() {
            BaseObject oldObject = m_activeObject;
            BaseObject newObject = activeObject;
            @Override
            public void undo() {
                super.undo();
                m_activeObject = oldObject;
                resetAllObjectParams();
            }
            @Override
            public void redo() {
                super.redo();
                m_activeObject = newObject;
                resetAllObjectParams();
            }
            public String getPresentationName(){
               return "Selection"; 
            }
        });
        m_activeObject = activeObject;
        resetAllObjectParams();
    }
    
    /**
    * Sets the transform of the current object to Transform transform and saves an edit
    */
    public void setCurrentObjectTransform(Transform transform){
        m_undoManager.addEdit(new AbstractUndoableEdit() {
            Transform oldTransform = m_activeObject.getTransform();
            Transform newTransform = transform;
            @Override
            public void undo() {
                super.undo();
                m_activeObject.setTransform(oldTransform);
                refreshTransform();
            }
            @Override
            public void redo() {
                super.redo();
                m_activeObject.setTransform(newTransform);
                refreshTransform();
            }
            public String getPresentationName(){
               return "Edit Transform"; 
            }
        });
        m_activeObject.setTransform(transform);
        m_renderPanel.render();
    }
    
    /**
    * Sets the color of the current LightSource to Color color and saves an edit
    */
    public void setCurrentObjectColor(Color color){
        m_undoManager.addEdit(new AbstractUndoableEdit() {
            Color oldColor = ((LightSource)m_activeObject).getColor();
            Color newColor = color;
            @Override
            public void undo() {
                super.undo();
                ((LightSource)m_activeObject).setColor(oldColor);
                refreshColor();
            }
            @Override
            public void redo() {
                super.redo();
                ((LightSource)m_activeObject).setColor(newColor);
                refreshColor();
            }
            public String getPresentationName(){
               return "Change Color"; 
            }
        });
        ((LightSource)m_activeObject).setColor(color);
        m_renderPanel.render();
    }
    
    /**
    * Sets the texture of the current EngineObject to BufferedImage sourceImage and saves an edit
    */
    public void setCurrentObjectTexture(BufferedImage sourceImage){
        Bitmap texture = new Bitmap(sourceImage);
        m_undoManager.addEdit(new AbstractUndoableEdit() {
            Bitmap oldTexture = ((EngineObject)m_activeObject).getTexture();
            Bitmap newTexture = texture;
            @Override
            public void undo() {
                super.undo();
                ((EngineObject)m_activeObject).setTexture(oldTexture);
                refreshColor();
            }
            @Override
            public void redo() {
                super.redo();
                ((EngineObject)m_activeObject).setTexture(newTexture);
                refreshColor();
            }
            public String getPresentationName(){
               return "Change Texture"; 
            }
        });
        ((EngineObject)m_activeObject).setTexture(texture);
        m_renderPanel.render();
    }
    
    /**
    * Sets the light parameters of the current LightSource to float angle, float distance, float intensity and saves an edit
    */
    public void setCurrentObjectLightParameters(float angle, float distance, float intensity){
        m_undoManager.addEdit(new AbstractUndoableEdit() {
            float[] oldParams = ((LightSource)m_activeObject).getLightParameters();
            float[] newParams = new float[]{angle, distance, intensity};
            @Override
            public void undo() {
                super.undo();
                ((LightSource)m_activeObject).setLightParameters(oldParams[0], oldParams[1], oldParams[2]);
                refreshLightParams();
            }
            @Override
            public void redo() {
                super.redo();
                ((LightSource)m_activeObject).setLightParameters(newParams[0], newParams[1], newParams[2]);
                refreshLightParams();
            }
            public String getPresentationName(){
               return "Edit Light"; 
            }
        });
        ((LightSource)m_activeObject).setLightParameters(angle, distance, intensity);
        m_renderPanel.render();
    }
    
    /**
    * Resets the object editor and selection to the current object
    */
    public void resetAllObjectParams(){
        m_objectEditor.reset(m_activeObject);
        m_objectSelector.setActiveObject(m_activeObject);
        m_renderPanel.render();
    }
    
    /**
    * Resets the object editor transform to the current object
    */
    public void refreshTransform(){
        m_objectEditor.refreshTransform(m_activeObject);
        m_renderPanel.render();
    }
    
    /**
    * Resets the object editor color to the current object
    */
    public void refreshColor(){
        m_objectEditor.refreshColor(m_activeObject);
        m_renderPanel.render();
    }
    
    /**
    * Resets the object editor light parameters to the current object
    */
    public void refreshLightParams(){
        m_objectEditor.refreshLight(m_activeObject);
        m_renderPanel.render();
    }

    /**
    * Resets the rendering parameters
    */
    public void resetRenderParams() {
        refreshMode(DEFAULT_RENDER_MODE);
        RenderPanel.Mode[] modes = RenderPanel.Mode.values();
        m_renderPanel.setMode(modes[DEFAULT_RENDER_MODE]);
        
        refreshViewParameters(DEFAULT_FOV, DEFAULT_ZNEAR, DEFAULT_ZFAR);
        m_renderPanel.setProjection(DEFAULT_FOV, DEFAULT_ZNEAR, DEFAULT_ZFAR);
        
        refreshIlluminationParameters(
                DEFAULT_ZENITH_COLOR, DEFAULT_AMBIENTLIGHT_COLOR,
                DEFAULT_AMBIENTLIGHT_INTENSITY, DEFAULT_LIGHTMAP_RESOLUTION);
        m_renderPanel.setIllumination(DEFAULT_ZENITH_COLOR, DEFAULT_AMBIENTLIGHT_COLOR,
                DEFAULT_AMBIENTLIGHT_INTENSITY, DEFAULT_LIGHTMAP_RESOLUTION);
        m_renderPanel.setCameraTransform(DEFAULT_CAMERA);
    }
    
    /**
    * Sets the rendering settings interface mode to int mode
    */
    public void refreshMode(int mode){
        m_renderSettings.setMode(mode);
        m_renderPanel.render();
    }
    
    /**
    * Sets the rendering settings interface view parameters to float fov, float zNear, float zFar
    */
    public void refreshViewParameters(float fov, float zNear, float zFar){
        m_renderSettings.setViewParameters((int)fov, zNear, zFar);
        m_renderPanel.render();
    }
    
    /**
    * Sets the rendering settings interface illumination parameters to Color zenithColor, Color ambientLightColor, float ambientLightIntensity, int lightMapResolution
    */
    public void refreshIlluminationParameters(Color zenithColor, Color ambientLightColor, float ambientLightIntensity, int lightMapResolution){
        m_renderSettings.setIlluminationParameters(zenithColor, ambientLightColor, ambientLightIntensity, lightMapResolution);
        m_renderPanel.render();
    }
}
