package RenderingEngine.CoreComponents;

/**
 * The base class for Quaternions representing rotations in 3D space,
 * 
 * modified from <a href="https://github.com/BennyQBD/3DSoftwareRenderer/">BennyQBD's 3DSoftwareRenderer</a>
 * @author Eero Lempiö el415524@student.uta.fi
 */
public class Quaternion
{
    private float m_x, m_y, m_z, m_w;

    public Quaternion(float x, float y, float z, float w){
        m_x = x;
        m_y = y;
        m_z = z;
        m_w = w;
    }
    
    /**
    * Constructor that creates a Quaternion representing rotation around Vector4 axis by float angle in radians
    */
    public Quaternion(Vector4 axis, float angle){
        float sinHalfAngle = (float)Math.sin(angle / 2);
        float cosHalfAngle = (float)Math.cos(angle / 2);
        m_x = axis.getX() * sinHalfAngle;
        m_y = axis.getY() * sinHalfAngle;
        m_z = axis.getZ() * sinHalfAngle;
        m_w = cosHalfAngle;
    }
    
    /**
    * Constructor that creates a Quaternion representing Matrix4 rot
    */
    public Quaternion(Matrix4 rot){
        float trace = rot.get(0, 0) + rot.get(1, 1) + rot.get(2, 2);

        if(trace > 0)
        {
                float s = 0.5f / (float)Math.sqrt(trace+ 1.0f);
                m_w = 0.25f / s;
                m_x = (rot.get(1, 2) - rot.get(2, 1)) * s;
                m_y = (rot.get(2, 0) - rot.get(0, 2)) * s;
                m_z = (rot.get(0, 1) - rot.get(1, 0)) * s;
        }
        else
        {
                if(rot.get(0, 0) > rot.get(1, 1) && rot.get(0, 0) > rot.get(2, 2))
                {
                        float s = 2.0f * (float)Math.sqrt(1.0f + rot.get(0, 0) - rot.get(1, 1) - rot.get(2, 2));
                        m_w = (rot.get(1, 2) - rot.get(2, 1)) / s;
                        m_x = 0.25f * s;
                        m_y = (rot.get(1, 0) + rot.get(0, 1)) / s;
                        m_z = (rot.get(2, 0) + rot.get(0, 2)) / s;
                }
                else if(rot.get(1, 1) > rot.get(2, 2))
                {
                        float s = 2.0f * (float)Math.sqrt(1.0f + rot.get(1, 1) - rot.get(0, 0) - rot.get(2, 2));
                        m_w = (rot.get(2, 0) - rot.get(0, 2)) / s;
                        m_x = (rot.get(1, 0) + rot.get(0, 1)) / s;
                        m_y = 0.25f * s;
                        m_z = (rot.get(2, 1) + rot.get(1, 2)) / s;
                }
                else
                {
                        float s = 2.0f * (float)Math.sqrt(1.0f + rot.get(2, 2) - rot.get(0, 0) - rot.get(1, 1));
                        m_w = (rot.get(0, 1) - rot.get(1, 0) ) / s;
                        m_x = (rot.get(2, 0) + rot.get(0, 2) ) / s;
                        m_y = (rot.get(1, 2) + rot.get(2, 1) ) / s;
                        m_z = 0.25f * s;
                }
        }

        float length = (float)Math.sqrt(m_x * m_x + m_y * m_y + m_z * m_z + m_w * m_w);
        m_x /= length;
        m_y /= length;
        m_z /= length;
        m_w /= length;
    }

    public float getX(){return m_x;}
    public float getY(){return m_y;}
    public float getZ(){return m_z;}
    public float getW(){return m_w;}
    
    /**
    * Returns a new Vector4 representing this Quaternions forward direction
    */
    public Vector4 getForward(){return new Vector4(0,0,1,1).rotate(this);}
    
    /**
    * Returns a new Vector4 representing this Quaternions backward direction
    */
    public Vector4 getBack(){return new Vector4(0,0,-1,1).rotate(this);}
    
    /**
    * Returns a new Vector4 representing this Quaternions up direction
    */
    public Vector4 getUp(){return new Vector4(0,1,0,1).rotate(this);}
    
    /**
    * Returns a new Vector4 representing this Quaternions down direction
    */
    public Vector4 getDown(){return new Vector4(0,-1,0,1).rotate(this);}
    
    /**
    * Returns a new Vector4 representing this Quaternions right direction
    */
    public Vector4 getRight(){return new Vector4(1,0,0,1).rotate(this);}
    
    /**
    * Returns a new Vector4 representing this Quaternions left direction
    */
    public Vector4 getLeft(){return new Vector4(-1,0,0,1).rotate(this);}

    public boolean equals(Quaternion r){
        return m_x == r.getX() && m_y == r.getY() && m_z == r.getZ() && m_w == r.getW();
    }
    
    /**
    * Returns the length of this Quaternion
    */
    public float length(){
        return (float)Math.sqrt(m_x * m_x + m_y * m_y + m_z * m_z + m_w * m_w);
    }
    
    /**
    * Returns the dot product of this Quaternion and Quaternion r
    */
    public float dot(Quaternion r){
        return m_x * r.getX() + m_y * r.getY() + m_z * r.getZ() + m_w * r.getW();
    }
    
    /**
    * Returns a new Matrix4 initialized to a rotation matrix representing this Quaternion
    */
    public Matrix4 toRotationMatrix(){
        Vector4 forward =  new Vector4(2.0f * (m_x * m_z - m_w * m_y), 2.0f * (m_y * m_z + m_w * m_x), 1.0f - 2.0f * (m_x * m_x + m_y * m_y));
        Vector4 up = new Vector4(2.0f * (m_x * m_y + m_w * m_z), 1.0f - 2.0f * (m_x * m_x + m_z * m_z), 2.0f * (m_y * m_z - m_w * m_x));
        Vector4 right = new Vector4(1.0f - 2.0f * (m_y * m_y + m_z * m_z), 2.0f * (m_x * m_y - m_w * m_z), 2.0f * (m_x * m_z + m_w * m_y));

        return new Matrix4().initRotation(forward, up, right);
    }    

    /**
    * Returns a new Quaternion that is the normalized presentation of this Quaternion
    */
    public Quaternion normalized(){
        float length = length();
        return new Quaternion(m_x / length, m_y / length, m_z / length, m_w / length);
    }
    
    /**
    * Returns a new Quaternion representing this Quaternions components conjugation
    */
    public Quaternion conjugate(){
        return new Quaternion(-m_x, -m_y, -m_z, m_w);
    }
    
    /**
    * Returns a new Quaternion representing the product of this Quaternion's components that have been multiplied by float f
    */
    public Quaternion mul(float r){
        return new Quaternion(m_x * r, m_y * r, m_z * r, m_w * r);
    }
    
    /**
    * Returns a new Quaternion representing the multiplication of this Quaternion and Quaternion r
    */
    public Quaternion mul(Quaternion r){
        float w_ = m_w * r.getW() - m_x * r.getX() - m_y * r.getY() - m_z * r.getZ();
        float x_ = m_x * r.getW() + m_w * r.getX() + m_y * r.getZ() - m_z * r.getY();
        float y_ = m_y * r.getW() + m_w * r.getY() + m_z * r.getX() - m_x * r.getZ();
        float z_ = m_z * r.getW() + m_w * r.getZ() + m_x * r.getY() - m_y * r.getX();

        return new Quaternion(x_, y_, z_, w_);
    }
    
    /**
    * Returns a new Quaternion representing the multiplication of this Quaternion and Vector4 r
    */
    public Quaternion mul(Vector4 r){
        float w_ = -m_x * r.getX() - m_y * r.getY() - m_z * r.getZ();
        float x_ =  m_w * r.getX() + m_y * r.getZ() - m_z * r.getY();
        float y_ =  m_w * r.getY() + m_z * r.getX() - m_x * r.getZ();
        float z_ =  m_w * r.getZ() + m_x * r.getY() - m_y * r.getX();

        return new Quaternion(x_, y_, z_, w_);
    }
    
    /**
    * Returns a new Quaternion representing the sum of this Quaternion minus Quaternion r
    */
    public Quaternion sub(Quaternion r){
        return new Quaternion(m_x - r.getX(), m_y - r.getY(), m_z - r.getZ(), m_w - r.getW());
    }
    
    /**
    * Returns a new Quaternion representing the sum of this Quaternion and Quaternion r
    */
    public Quaternion add(Quaternion r){
        return new Quaternion(m_x + r.getX(), m_y + r.getY(), m_z + r.getZ(), m_w + r.getW());
    }
}