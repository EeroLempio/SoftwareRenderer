package RenderingEngine.Constructs;

/**
 * BaseObject is the base class for Camera, LightSource and EngineObject
 * 
 * @author Eero Lempiö el415524@student.uta.fi
 */
public class BaseObject {
    private String m_name;
    protected Transform m_transform;

    /**
    * Creates a BaseObject with an empty name and Transform transform
    */
    public BaseObject(Transform transform) {
        m_name = "";
        m_transform = transform;
    }
    
    /**
    * Creates a BaseObject with a name String name and Transform transform
    */
    public BaseObject(String name, Transform transform) {
        m_name = name;
        m_transform = transform;
    }
    
    /**
    * Sets this BaseObjects name to String name
    */  
    public void setName(String name){
        m_name = name;
    }
    
    /**
    * Returns this BaseObjects name
    */    
    @Override
    public String toString(){
        return m_name;
    }
    
    /**
    * Returns this BaseObjects transform
    */    
    public Transform getTransform(){return m_transform;}    
    
    /**
    * Sets this BaseObjects transform to Transform transform
    */
    public void setTransform(Transform transform) {m_transform = transform;}
}
