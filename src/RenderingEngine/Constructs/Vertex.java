package RenderingEngine.Constructs;

import RenderingEngine.CoreComponents.Matrix4;
import RenderingEngine.CoreComponents.Vector4;

/**
 * Vertex represents a point in 3D space with location, normalized direction, and a texture coordinate for texturing purposes,
 * 
 * modified from <a href="https://github.com/BennyQBD/3DSoftwareRenderer/">BennyQBD's 3DSoftwareRenderer</a>
 * @author Eero Lempiö el415524@student.uta.fi
 */
public class Vertex {
    private Vector4 m_pos, m_UVcoord, m_normal;
    
    /**
    * Constructor that creates a new Vertex with location Vector4 pos and direction Vector4 normal. Texture coordinate initialized to a Zero Vector4
    */
    public Vertex(Vector4 pos, Vector4 normal) {
        m_pos = pos;
        m_UVcoord = new Vector4(0,0,0);
        m_normal = normal;
    }
    
    /**
    * Constructor that creates a new Vertex with location Vector4 pos, direction Vector4 normal and texture coordinate Vector4 UVcoord
    */
    public Vertex(Vector4 pos, Vector4 UVcoord, Vector4 normal) {
        m_pos = pos;
        m_UVcoord = UVcoord;
        m_normal = normal;
    }
    
    public Vector4 getPosition() {return m_pos;}
    
    /**
    * Returns this Vertex's texture coordinate
    */
    public Vector4 getUVcoord() {return m_UVcoord;}
    public Vector4 getNormal() {return m_normal;}
    
    /**
    * Returns a component from this Vertex's position. Index 0 gets X, 1 Y, 2 Z and 3 W
    */
    public float get(int index) {
        switch(index){
            case 0: return m_pos.getX();
            case 1: return m_pos.getY();
            case 2: return m_pos.getZ();
            case 3: return m_pos.getW();
            default: throw new IndexOutOfBoundsException();
        }
    }
    
    /**
    * Returns a new Vertex representing this Vertex with its position transformed by Matrix4 transform and its direction transformed by Matrix4 normalTransform.
    */
    public Vertex transform(Matrix4 transform, Matrix4 normalTransform){
        return new Vertex(transform.transform(m_pos), m_UVcoord, normalTransform.transform(m_normal).normalized());
    }
    
    /**
    * Returns a new Vertex representing this Vertex with its positions components except w divided by its positions w component
    */
    public Vertex perspectiveDivide(){
        return new Vertex(new Vector4(m_pos.getX()/m_pos.getW(), m_pos.getY()/m_pos.getW(), m_pos.getZ()/m_pos.getW(), m_pos.getW()), m_UVcoord, m_normal);
    }
    
    /**
    * Returns a new Vertex representing this Vertex with its positions components except w multiplied by its positions w component
    */
    public Vertex perspectiveUnDivide(){
        return new Vertex(new Vector4(m_pos.getX()*m_pos.getW(), m_pos.getY()*m_pos.getW(), m_pos.getZ()*m_pos.getW(), m_pos.getW()), m_UVcoord, m_normal);
    }
    
    /**
    * Returns the area denoted by this Vertex and Vetex b and Vertex c
    */
    public float triangleArea(Vertex b, Vertex c){
        float x = m_pos.getX();
        float y = m_pos.getY();
 
        return 
            ((b.m_pos.getX() - x) * (c.m_pos.getY() - y) - 
            (c.m_pos.getX() - x) * (b.m_pos.getY() - y))/2;
    }
    
    /**
    * Interpolates between this Vertex's position, direction and texture coordinate and Vertex other's position, direction and texture coordinate by lerpAmount
    */
    public Vertex lerp(Vertex other, float lerpAmount){
        return new Vertex(m_pos.lerp(other.getPosition(), lerpAmount), m_UVcoord.lerp(other.getUVcoord(), lerpAmount), m_normal.lerp(other.getNormal(), lerpAmount));
    }
    
    /**
    * Returns true if this Vertex's position is inside the view frustum denoted by this Vertex's positions w component.
    */
    public boolean isInsideViewFrustum(){
        return
            Math.abs(m_pos.getX()) <= Math.abs(m_pos.getW()) &&
            Math.abs(m_pos.getY()) <= Math.abs(m_pos.getW()) &&
            Math.abs(m_pos.getZ()) <= Math.abs(m_pos.getW());
    }
}
