package RenderingEngine.Constructs;

import RenderingEngine.CoreComponents.Bitmap;

/**
 * EngineObject is a 3D model representation of a mesh transformed by a Transform and textured with a Bitmap texture
 * 
 * @author Eero Lempiö el415524@student.uta.fi
 */
public class EngineObject extends BaseObject{
    private Mesh m_mesh;
    private Bitmap m_texture;
    private Vertex[] m_worldVertices;

    public EngineObject(String name, Transform transform, Mesh mesh, Bitmap texture) {
        super(name, transform);
        m_mesh = mesh;
        m_transform = transform;
        m_texture = texture;
        calculateWorldVertices();
    }
    
    /**
    * Returns this EngineObjects Mesh
    */
    public Mesh getMesh() {return m_mesh;}
    
    /**
    * Returns this EngineObjects texture
    */
    public Bitmap getTexture() {return m_texture;}
    
    /**
    * Returns the vertices of this EngineObjects Mesh, transformed by this EngineObjects tranform to world space
    */
    public Vertex[] getWorldVertices(){return m_worldVertices;}
    
    /**
    * Sets this EngineObjects transform to Transform transform and recalculates its world space vertices
    */
    @Override
    public void setTransform(Transform transform) {super.setTransform(transform); calculateWorldVertices();}
    
    /**
    * Sets this EngineObjects texture to Bitmap texture
    */
    public void setTexture(Bitmap texture) {m_texture = texture;}
    
    /**
    * Calculates this EngineObjects world space vertices
    */
    private void calculateWorldVertices(){
        m_worldVertices = m_mesh.getTriangleVertices(m_transform.getTransformation(), m_transform.getTransformation());
    }
}
