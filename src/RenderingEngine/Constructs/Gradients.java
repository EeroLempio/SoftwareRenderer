package RenderingEngine.Constructs;

/**
 * Gradients represents a Triangle made of three vertices, and is used to interpolate values on the surface of this triangle based on these three vertices,
 * 
 * modified from <a href="https://github.com/BennyQBD/3DSoftwareRenderer/">BennyQBD's 3DSoftwareRenderer</a>
 * @author Eero Lempiö el415524@student.uta.fi
 */
public class Gradients {
    /*depth, oneOverZ, UVX, UVY, NormalX, NormalY, NormalZ, W*/
    private float[][] m_values;

    /*depthX, depthY, oneOverZX, oneOverZY, UVXX, UVXY, UVYX, UVYY,
    NormalXX, NormalXY, NormalYX, NormalYY, NormalZX, NormalZY, WX, WY*/    
    private float[] m_steps;

    /**
    * Creates a new Gradients object, representing a triangle made of vertices Vertex minYvert, Vertex midYvert, Vertex maxYvert
    */
    public Gradients(Vertex minYvert, Vertex midYvert, Vertex maxYvert) {
        m_values = new float[8][3];
        m_steps = new float[8 * 2];
        float oneOverdX = 1f / 
                (((midYvert.getPosition().getX() - maxYvert.getPosition().getX()) *
                (minYvert.getPosition().getY() - maxYvert.getPosition().getY())) -
                ((minYvert.getPosition().getX() - maxYvert.getPosition().getX()) *
                (midYvert.getPosition().getY() - maxYvert.getPosition().getY())));
        
        Vertex[] verts = {minYvert, midYvert, maxYvert};
        
        for(int j = 0; j < 3; j++){
            m_values[0][j] = verts[j].getPosition().getZ();
            m_values[1][j] = 1f/verts[j].getPosition().getW();
            m_values[2][j] = verts[j].getUVcoord().getX() * m_values[1][j];
            m_values[3][j] = verts[j].getUVcoord().getY() * m_values[1][j];
            m_values[4][j] = verts[j].getNormal().getX();
            m_values[5][j] = verts[j].getNormal().getY();
            m_values[6][j] = verts[j].getNormal().getZ();
            m_values[7][j] = verts[j].getPosition().getW();
        }
        for(int i = 0; i < m_values.length; i++){
            m_steps[i * 2] = calcXStep(m_values[i], minYvert, midYvert, maxYvert, oneOverdX);
            m_steps[i * 2 + 1] = calcYStep(m_values[i], minYvert, midYvert, maxYvert, -oneOverdX);
        }
    }    
    
    /**
    * Returns a value representing either Depth, oneOverZ, UVX, UVY, NormalX, NormalY, NormalZ or W of based on int i of Vertex j, 
    * in order Vertex minYvert, Vertex midYvert, Vertex maxYvert
    */
    public float getValue(int i, int j){return m_values[i][j];}
    
    /**
    * Returns a value representing how much to step either Depth, oneOverZ, UVX, UVY, NormalX, NormalY, NormalZ or W and in which direction, X or Y, based on int i.
    * Steps are depthX, depthY, oneOverZX, oneOverZY, UVXX, UVXY, UVYX, UVYY, NormalXX, NormalXY, NormalYX, NormalYY, NormalZX, NormalZY, WX, WY
    */
    public float getStep(int i){return m_steps[i];}
    
    /**
    * Calculates the X step of float[] values, based on Vertex minYvert, Vertex midYvert, Vertex maxYvert, float oneOverdX
    */
    private float calcXStep(float[] values, Vertex minYvert, Vertex midYvert, Vertex maxYvert, float oneOverdX){
        return (((values[1] - (values[2])) *
                (minYvert.getPosition().getY() - maxYvert.getPosition().getY())) -
                ((values[0] - (values[2])) *
                (midYvert.getPosition().getY() - maxYvert.getPosition().getY()))) *
                oneOverdX;
    }
    
    /**
    * Calculates the Y step of float[] values, based on Vertex minYvert, Vertex midYvert, Vertex maxYvert, float oneOverdX
    */
    private float calcYStep(float[] values, Vertex minYvert, Vertex midYvert, Vertex maxYvert, float oneOverdY){
        return (((values[1] - (values[2])) *
                (minYvert.getPosition().getX() - maxYvert.getPosition().getX())) -
                ((values[0] - (values[2])) *
                (midYvert.getPosition().getX() - maxYvert.getPosition().getX()))) *
                oneOverdY;
    }
}