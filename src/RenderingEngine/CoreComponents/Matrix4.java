package RenderingEngine.CoreComponents;

/**
 * The base class for 4x4 matrices and operations needed in 3D computing,
 * 
 * modified from <a href="https://github.com/BennyQBD/3DSoftwareRenderer/">BennyQBD's 3DSoftwareRenderer</a>
 * @author Eero Lempiö el415524@student.uta.fi
 */
public class Matrix4 {
    private float[][] m;
    
    /**
    * Creates the new 4x4 float array representing this Matrix4
    */
    public Matrix4(){m = new float[4][4];}
    
    /**
    * Returns the 4x4 float array representing this matrix
    */
    public float[][] getMatrix() {return m;}
    /**
    * Returns the value at position x,y in this matrix
    */
    public float get(int x, int y){return m[x][y];}
    
    /**
    * Sets the value at position x,y to float value
    */
    public void set(int x, int y, float value){m[x][y] = value;}
    
    /**
    * Initializes this Matrix4 to the identity matrix
    */
    public Matrix4 initIdentity(){
        m[0][0] = 1;
        m[1][1] = 1;
        m[2][2] = 1;
        m[3][3] = 1;
        return this;
    }
    
    /**
    * Initializes this Matrix4 to a translation matrix
    */
    public Matrix4 initTranslation(float x, float y, float z){
        initIdentity();
        m[0][3] = x;
        m[1][3] = y;
        m[2][3] = z;
        return this;
    }
    
    /**
    * Initializes this Matrix4 to a rotation matrix
    */
    public Matrix4 initRotation(float x, float y, float z, float angle){
        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);
        float omc = 1-cos;
        m[0][0] = cos+x*x*omc;      m[0][1] = x*y*omc-z*sin;    m[0][2] = x*z*omc+y*sin;
        m[1][0] = y*x*omc+z*sin;    m[1][1] = cos+y*y*omc;      m[1][2] = y*z*omc-x*sin;
        m[2][0] = z*x*omc-y*sin;    m[2][1] = z*y*omc+x*sin;    m[2][2] = cos+z*z*omc;
        m[3][3] = 1;
        return this;
    }
    
    /**
    * Initializes this Matrix4 to a translation matrix
    */
    public Matrix4 initRotation(float x, float y, float z){
        Matrix4 mx = new Matrix4();
        Matrix4 my = new Matrix4();
        Matrix4 mz = new Matrix4();
        float cosX = (float)Math.cos(x);
        float sinX = (float)Math.sin(x);
        mx.m[0][0] = 1;
        mx.m[1][1] = cosX;      mx.m[1][2] = -sinX;
        mx.m[2][1] = sinX;      mx.m[2][2] = cosX;
        mx.m[3][3] = 1;
        float cosY = (float)Math.cos(y);
        float sinY = (float)Math.sin(y);
        my.m[0][0] = cosY;  my.m[0][2] = -sinY;
        my.m[1][1] = 1;
        my.m[2][0] = sinY;  my.m[2][2] = cosY;
        my.m[3][3] = 1;
        float cosZ = (float)Math.cos(z);
        float sinZ = (float)Math.sin(z);
        mz.m[0][0] = cosZ;  mz.m[0][1] = -sinZ;
        mz.m[1][0] = sinZ;  mz.m[1][1] = cosZ;
        mz.m[2][2] = 1;
        mz.m[3][3] = 1;
        m = mz.mul(my.mul(mx)).getMatrix();
        return this;
    }
    
    /**
    * Initializes this Matrix4 to a translation matrix
    */
    public Matrix4 initRotation(Vector4 forward, Vector4 up){
        Vector4 f = forward.normalized();
        Vector4 r = up.normalized();
        r = r.cross(f);
        Vector4 u = f.cross(r);
        return initRotation(f,u,r);
    }
    
    /**
    * Initializes this Matrix4 to a translation matrix
    */
    public Matrix4 initRotation(Vector4 f, Vector4 u, Vector4 r){
        m[0][0] = r.getX();    m[0][1] = r.getY();  m[0][2] = r.getZ();
        m[1][0] = u.getX();    m[1][1] = u.getY();  m[1][2] = u.getZ();
        m[2][0] = f.getX();    m[2][1] = f.getY();  m[2][2] = f.getZ();
        m[3][3] = 1;
        return this;
    }
    
    /**
    * Initializes this Matrix4 to a scaling matrix
    */
    public Matrix4 initScale(float x, float y, float z){
        m[0][0] = x;
        m[1][1] = y;
        m[2][2] = z;
        m[3][3] = 1;
        return this;
    }
    
    /**
    * Initializes this Matrix4 to a screenspace transformation matrix
    */
    public Matrix4 initScreenSpaceTransform(float halfWidth, float halfHeight){
        m[0][0] = halfWidth;    m[0][3] = halfWidth - 0.5f;
	m[1][1] = -halfHeight;  m[1][3] = halfHeight - 0.5f;
	m[2][2] = 1;
	m[3][3] = 1;
        return this;
    }
    
    /**
    * Initializes this Matrix4 to an inverse screenspace transformation matrix
    */
    public Matrix4 initInverseScreenSpaceTransform(float halfWidth, float halfHeight){
        m[0][0] = 1/halfWidth;    m[0][3] = -(halfWidth - 0.5f)/halfWidth;
	m[1][1] = 1/-halfHeight;  m[1][3] = -(halfHeight - 0.5f)/-halfHeight;
	m[2][2] = 1;
	m[3][3] = 1;
        return this;
    }
    
    /**
    * Initializes this Matrix4 to a perspective matrix
    */
    public Matrix4 initPerspective(float fov, float aspectRatio, float zNear, float zFar){
        float tanHalfFOV = (float)Math.tan(fov / 2);
        float zRange = zNear - zFar;
        m[0][0] = 1.0f / (tanHalfFOV * aspectRatio);
        m[1][1] = 1.0f / tanHalfFOV;
        m[2][2] = (-zNear -zFar)/zRange;    m[2][3] = 2 * zFar * zNear / zRange;
        m[3][2] = 1;
        return this;
    }
    
    /**
    * Initializes this Matrix4 to an inverse perspective matrix
    */
    public Matrix4 initInversePerspective(float fov, float aspectRatio, float zNear, float zFar){
        float tanHalfFOV = (float)Math.tan(fov / 2);
        float zRange = zNear - zFar;
        m[0][0] = tanHalfFOV * aspectRatio;
        m[1][1] = tanHalfFOV;
        m[2][3] = 1;
        m[3][2] = zRange/(2 * zFar * zNear);    m[3][3] = -(-zNear - zFar)/(2 * zFar * zNear);
        return this;
    }
    
    /**
    * Returns a new Vector4 representing Vector4 v transformed by this Matrix4
    */
    public Vector4 transform(Vector4 v){
        return new Vector4(
                m[0][0]*v.getX() + m[0][1]*v.getY() + m[0][2]*v.getZ() + m[0][3]*v.getW(),
                m[1][0]*v.getX() + m[1][1]*v.getY() + m[1][2]*v.getZ() + m[1][3]*v.getW(),
                m[2][0]*v.getX() + m[2][1]*v.getY() + m[2][2]*v.getZ() + m[2][3]*v.getW(),
                m[3][0]*v.getX() + m[3][1]*v.getY() + m[3][2]*v.getZ() + m[3][3]*v.getW()
        );
    }
    
    /**
    * Returns a new Matrix4 representing this Matrxi4 multiplied by Matrix4 m
    */
    public Matrix4 mul(Matrix4 m){
        Matrix4 result = new Matrix4();
        for(int row = 0; row < 4; row++)
            for(int col = 0; col < 4; col++)
                result.set(row, col,
                    this.m[row][0] * m.get(0, col) +
                    this.m[row][1] * m.get(1, col) +
                    this.m[row][2] * m.get(2, col) +
                    this.m[row][3] * m.get(3, col)
                );
        return result;
    }
}
