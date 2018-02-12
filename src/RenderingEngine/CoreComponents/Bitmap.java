package RenderingEngine.CoreComponents;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * The base class for representing and manipulating images stored as a byte array,
 * 
 * modified from <a href="https://github.com/BennyQBD/3DSoftwareRenderer/">BennyQBD's 3DSoftwareRenderer</a>
 * @author Eero Lempiö el415524@student.uta.fi
 */
public class Bitmap {
    private final int m_width, m_height;
    private final byte[] m_components;
    private final BufferedImage m_sourceImage;
    
    /**
    * Constructor that creates a Bitmap int widht pixels wide and int height pixels tall
    */
    public Bitmap(int width, int height) {
        m_width = width;
        m_height = height;
        m_components = new byte[width * height * 4];
        m_sourceImage = null;
    }
    
    /**
    * Constructor that creates a Bitmap from BufferedImage image
    */
    public Bitmap(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();
        byte[] components = new byte[width * height * 4];
        int imgPixels[] = new int[width * height];
        image.getRGB(0, 0, width, height, imgPixels, 0, width);
        
        for(int i = 0; i < width * height; i++){
            int pixel = imgPixels[i];
            components[i * 4    ] = (byte)((pixel >> 24) & 0xFF);
            components[i * 4 + 1] = (byte)((pixel      ) & 0xFF);
            components[i * 4 + 2] = (byte)((pixel >> 8 ) & 0xFF);
            components[i * 4 + 3] = (byte)((pixel >> 16) & 0xFF);
        }
        m_width = width;
        m_height = height;
        m_components = components;
        m_sourceImage = image;
    }
    
    /**
    * Returns the source BufferedImage this Bitmap was created with. Returns null if this Bitmap was not created from a BufferedImage
    */
    public BufferedImage getSourceImage(){return m_sourceImage;}
    public int getWidth() {return m_width;}
    public int getHeight() {return m_height;}
    
    /**
    * Returns a byte representing a single color value at int index location. Each pixel has four color values.
    */
    public byte getComponent(int index) {return m_components[index];}
    
    /**
    * Returns a byte array of size 4 representing the aBGR values of the pixel (int x, int y)
    */
    public byte[] getPixelValues(int x, int y) {
        int index = (x + y * m_width) * 4;
        byte[] pixel = {
        m_components[index],
        m_components[index + 1],
        m_components[index + 2],
        m_components[index + 3]};
        return pixel;
    }
    
    /**
    * Sets the values of pixel (int x, int y) to byte a, byte b, byte g, byte r
    */
    public void drawPixel(int x, int y, byte a, byte b, byte g, byte r){
        int index = (x + y * m_width) * 4;
        m_components[index] = a;
        m_components[index + 1] = b;
        m_components[index + 2] = g;
        m_components[index + 3] = r;
    }
    
    /**
    * Clears this Bitmap to byte[] color
    */
    public void clear(byte[] color){
        for(int i = 0; i < m_components.length - 4; i += 4){
            m_components[i] = (byte)0x00;
            m_components[i + 1] = color[0];
            m_components[i + 2] = color[1];
            m_components[i + 3] = color[2];
        }
    }
    
    /**
    * Copies this Bitmaps byte array to byte[] destination, where each pixel has three values, BGR
    */
    public void copyToByteArray(byte[] destination){
        try{
            for(int i = 0; i < m_width * m_height; i++){
                destination[i * 3    ] = m_components[i * 4 + 1];
                destination[i * 3 + 1] = m_components[i * 4 + 2];
                destination[i * 3 + 2] = m_components[i * 4 + 3];
            }
        }
        catch(ArrayIndexOutOfBoundsException e){}
    }
}
