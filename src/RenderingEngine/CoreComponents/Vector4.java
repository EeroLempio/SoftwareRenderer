package RenderingEngine.CoreComponents;

/**
 * The base class denoting direction or position in 3D space,
 * 
 * modified from <a href="https://github.com/BennyQBD/3DSoftwareRenderer/">BennyQBD's 3DSoftwareRenderer</a>
 * @author Eero Lempiö el415524@student.uta.fi
 */
public class Vector4 {
    private float m_x,m_y,m_z,m_w;
    
    
    /**
    * Constructor that automatically assigns the imaginary component w as 1
    */
    public Vector4(float x, float y, float z) {this(x,y,z,1);}
    
    public Vector4(float x, float y, float z, float w) {
        m_x = x;
        m_y = y;
        m_z = z;
        m_w = w;
    }

    public float getX() {return m_x;}
    public float getY() {return m_y;}
    public float getZ() {return m_z;}
    public float getW() {return m_w;}
    
    /**
    * Returns a new Vector4 rotated along a Vector4 axis by an angle in radians
    */
    public Vector4 rotate(Vector4 axis, float angle){
        float sinAngle = (float)Math.sin(-angle);
        float cosAngle = (float)Math.cos(-angle);
        return this.cross(axis.mul(sinAngle)).add(
                (this.mul(cosAngle)).add(
                        axis.mul(this.dot(axis.mul(1 - cosAngle)))));
    }
    
    /**
    * Returns a new Vector4 rotated by Quaternion rotation
    */
    public Vector4 rotate(Quaternion rotation){
        Quaternion conjugate = rotation.conjugate();
        Quaternion _w = rotation.mul(this).mul(conjugate);
        return new Vector4(_w.getX(), _w.getY(), _w.getZ(), 1f);
    }
    
    /**
    * Returns the length of this Vector4
    */
    public float length(){
        return (float)Math.sqrt(m_x*m_x + m_y*m_y + m_z*m_z + m_w*m_w);
    }
    
    /**
    * Returns the max value of this vectors components
    */
    public float max(){
        return Math.max(Math.max(m_x, m_y), Math.max(m_z, m_w));
    }
    
    /**
    * Returns the dot product of this Vector4 and Vector4 v
    */
    public float dot(Vector4 v){
        return m_x*v.getX() + m_y*v.getY() + m_z*v.getZ() + m_w*v.getW();
    }
    
    /**
    * Returns a new Vector4 that is a cross product of this Vector4 and Vector4 v
    */
    public Vector4 cross(Vector4 v){
        float x_ = m_y*v.getZ() - m_z*v.getY();
        float y_ = m_z*v.getX() - m_x*v.getZ();
        float z_ = m_x*v.getY() - m_y*v.getX();
        return new Vector4(x_, y_, z_, 0);
    }
    
    /**
    * Returns a new Vector4 that is the normalized presentation of this Vector4
    */
    public Vector4 normalized(){
        float length = length();
        return new Vector4(m_x/length, m_y/length, m_z/length, m_w/length);
    }
    
    /**
    * Interpolates between this Vector4 and Vector4 dest by factor lerpFactor
    */
    public Vector4 lerp(Vector4 dest, float lerpFactor){
        return dest.sub(this).mul(lerpFactor).add(this);
    }
    
    /**
    * Returns a new Vector4 representing the sum of this Vector4 and Vector4 v
    */
    public Vector4 add(Vector4 v){
        return new Vector4(m_x+v.getX(), m_y+v.getY(), m_z+v.getZ(), m_w+v.getW());
    }
    
    /**
    * Returns a new Vector4 representing the sum of this Vector4's components that have been added to by float f
    */
    public Vector4 add(float f){
        return new Vector4(m_x+f, m_y+f, m_z+f, m_w+f);
    }
    
    /**
    * Returns a new Vector4 representing the sum of this Vector4 minus Vector4 v
    */
    public Vector4 sub(Vector4 v){
        return new Vector4(m_x-v.getX(), m_y-v.getY(), m_z-v.getZ(), m_w-v.getW());
    }
    
    /**
    * Returns a new Vector4 representing the sum of this Vector4's components that have been subtracted by float f
    */
    public Vector4 sub(float f){
        return new Vector4(m_x-f, m_y-f, m_z-f, m_w-f);
    }
    
    /**
    * Returns a new Vector4 representing the multiplication of this Vector4 and Vector4 v
    */
    public Vector4 mul(Vector4 v){
        return new Vector4(m_x*v.getX(), m_y*v.getY(), m_z*v.getZ(), m_w*v.getW());
    }
    
    /**
    * Returns a new Vector4 representing the product of this Vector4's components that have been multiplied by float f
    */
    public Vector4 mul(float f){
        return new Vector4(m_x*f, m_y*f, m_z*f, m_w*f);
    }
    
    /**
    * Returns a new Vector4 representing the division of this Vector4 and Vector4 v
    */
    public Vector4 div(Vector4 v){
        return new Vector4(m_x/v.getX(), m_y/v.getY(), m_z/v.getZ(), m_w/v.getW());
    }
    
    /**
    * Returns a new Vector4 representing the product of this Vector4's components that have been divided by float f
    */
    public Vector4 div(float f){
        return new Vector4(m_x/f, m_y/f, m_z/f, m_w/f);
    }
    
    /**
    * Returns a new Vector4 representing the absolute of this Vector
    */
    public Vector4 abs(){
        return new Vector4(Math.abs(m_x), Math.abs(m_y), Math.abs(m_z), Math.abs(m_w));
    }
}
