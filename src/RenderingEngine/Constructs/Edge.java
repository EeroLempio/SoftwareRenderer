package RenderingEngine.Constructs;

import RenderingEngine.CoreComponents.Bitmap;

/**
 * Edge represents a line between two vertices and is used to interpolate values on this line based on these two vertices and a Gradients object these vertices belong to,
 * 
 * modified from <a href="https://github.com/BennyQBD/3DSoftwareRenderer/">BennyQBD's 3DSoftwareRenderer</a>
 * @author Eero Lempiö el415524@student.uta.fi
 */
public class Edge {
    private Bitmap m_texture;
    
    private int m_yStart, m_yEnd;
    private float m_x, m_xStep;
    
    /*depth, oneOverZ, UVX, UVY, NormalX, NormalY, NormalZ, W*/
    private float[] m_values;

    /*depthStep, oneOverZStep, UVXStep, UVYStep, NormalXStep, NormalYStep, NormalZStep, WStep*/    
    private float[] m_steps;

    /**
    * Creates a new Edge object, representing a line made of vertices Vertex start and Vertex end, that must belong to Gradients gradients.
    * Bitmap texture is also stored here for ease of use, and it represents the texture that will be applied to these vertices.
    */
    public Edge(Bitmap texture, Gradients gradients, Vertex start, Vertex end, int index){
        m_texture = texture;
        
        m_values = new float[8];
        m_steps = new float[8];
        
        m_yStart = (int)Math.ceil(start.getPosition().getY());
        m_yEnd = (int)Math.ceil(end.getPosition().getY());
        float yPreStep = m_yStart - start.getPosition().getY();
        
        m_xStep = 
                (end.getPosition().getX() - start.getPosition().getX())/
                (end.getPosition().getY() - start.getPosition().getY());
        m_x = start.getPosition().getX() + yPreStep * m_xStep;
        float xPreStep = m_x - start.getPosition().getX();
        
        for(int i = 0; i < m_values.length; i++){
            m_values[i] = gradients.getValue(i, index) +
                    gradients.getStep(i*2) * xPreStep +
                    gradients.getStep(i*2 + 1) * yPreStep;
            m_steps[i] = gradients.getStep(i*2 + 1) +
                gradients.getStep(i*2) * m_xStep;
        }
    }
    public Bitmap getTexture(){return m_texture;};
    public int getyStart() {return m_yStart;}
    public int getyEnd() {return m_yEnd;}
    public float getX() {return m_x;}
    public float getDepth() {return m_values[0];}
    public float getOneOverZ() {return m_values[1];}
    public float getUVcoordX() {return m_values[2];}
    public float getUVcoordY() {return m_values[3];}
    public float getNormalX() {return m_values[4];}
    public float getNormalY() {return m_values[5];}
    public float getNormalZ() {return m_values[6];}
    public float getW() {return m_values[7];}
    
    /**
    * Steps this Edge forward
    */
    public void step(){
        m_x += m_xStep;
        for(int i = 0; i < m_values.length; i++)
            m_values[i] += m_steps[i];
    }
}
