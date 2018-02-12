package RenderingEngine.Constructs;

import RenderingEngine.CoreComponents.Matrix4;
import RenderingEngine.CoreComponents.Quaternion;
import RenderingEngine.CoreComponents.Vector4;

/**
 * Transform is a representation of position, rotation and scale in 3D space,
 * 
 * modified from <a href="https://github.com/BennyQBD/3DSoftwareRenderer/">BennyQBD's 3DSoftwareRenderer</a>
 * @author Eero Lempiö el415524@student.uta.fi
 */
public class Transform {
    private Vector4 m_position;
    private Quaternion m_rotation;
    private Vector4 m_scale;

    /**
    * Constructor that creates a new Transform with its position and rotation at 0 and scale of 1
    */
    public Transform() {this(new Vector4(0,0,0,0));}
    
    /**
    * Constructor that creates a new Transform with its position as Vector4 position, rotation at 0 and scale of 1
    */
    public Transform(Vector4 position) {
        this(position, new Quaternion(0f,0f,0f,1f), new Vector4(1,1,1,1));
    }
    
    /**
    * Constructor that creates a new Transform with its position as Vector4 position, rotation as Quaternion rotation and scale of 1
    */
    public Transform(Vector4 position, Quaternion rotation) {
        m_position = position;
        m_rotation = rotation;
        m_scale = new Vector4(1,1,1,1);
    }
    
    /**
    * Constructor that creates a new Transform with its position as Vector4 position, rotation as Quaternion rotation and scale as Vector4 scale
    */
    public Transform(Vector4 position, Quaternion rotation, Vector4 scale) {
        m_position = position;
        m_rotation = rotation;
        m_scale = scale;
    }
    
    public Vector4 getPosition() {return m_position;}
    public Quaternion getRotation() {return m_rotation;}
    public Vector4 getScale() {return m_scale;}
    
    /**
    * Returns a new Transform, with other fields remaining but position set to Vector4 position
    */
    public Transform setPosition(Vector4 position) {
        return new Transform(position, m_rotation, m_scale);
    }
    
    /**
    * Returns a new Transform, with other fields remaining but rotation multiplied by Quaternion rotation and then normalized
    */
    public Transform rotate(Quaternion rotation){
        return new Transform(m_position, rotation.mul(this.m_rotation).normalized(), m_scale);
    }
    
    /**
    * Returns a new Transform, with other fields remaining but rotation facing Vector4 point
    */
    public Transform lookAt(Vector4 point, Vector4 up){
        return rotate(getLookAtRotation(point, up));
    }
    
    /**
    * Returns a new Quaternion representing rotation from this Transforms rotation to Vector4 point
    */
    private Quaternion getLookAtRotation(Vector4 point, Vector4 up){
        return new Quaternion(new Matrix4().initRotation(point.sub(m_position).normalized(), up));
    }
    
    /**
    * Returns a new Matrix4 representing this Transform
    */
    public Matrix4 getTransformation(){
        Matrix4 translationMatrix = new Matrix4().initTranslation(m_position.getX(), m_position.getY(), m_position.getZ());
        Matrix4 rotationMatrix = m_rotation.toRotationMatrix();
        Matrix4 scaleMatrix = new Matrix4().initScale(m_scale.getX(), m_scale.getY(), m_scale.getZ());
        return translationMatrix.mul(rotationMatrix.mul(scaleMatrix));
    }
}
