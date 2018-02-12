package RenderingEngine.Rendering;

import RenderingEngine.Constructs.Edge;
import RenderingEngine.Constructs.LightSource;
import RenderingEngine.Constructs.Transform;
import RenderingEngine.Constructs.Vertex;
import RenderingEngine.CoreComponents.Bitmap;
import RenderingEngine.CoreComponents.Matrix4;
import RenderingEngine.CoreComponents.Vector4;
import java.util.Arrays;
import java.util.List;

/**
 * EdgeDrawer contains static classes which draw triangles on a Bitmap based on a List of Edges
 * 
 * @author Eero Lempiö el415524@student.uta.fi
 */
public class EdgeDrawer {
    public static float[] drawzBuffer(List<Edge> edges, int resolution){
        float[] zBuffer = getNewzBuffer(resolution, resolution);
        for(int i = 0; i < edges.size() ; i += 3){
            for(int j = edges.get(i + 2).getyStart(); j < edges.get(i + 2).getyEnd(); j++){
                Edge left = edges.get(i);
                Edge right = edges.get(i + 1);
                float[] vals = getCoreValuesAndSteps(left, right);
                for(int k = (int)vals[0]; k < (int)vals[1]; k++){
                    int index = k + j * resolution;
                    if(vals[5] < zBuffer[index])
                        zBuffer[index] = vals[5];
                    vals[5] += vals[4];
                }
                left.step();
                right.step();
            }
        }
        return zBuffer;
    }
    
    public static void drawDepth(Bitmap frame, List<Edge> edges){
        int width = frame.getWidth();
        int height = frame.getHeight();
        float[] zBuffer = getNewzBuffer(width, height);
        for(int i = 0; i < edges.size() ; i += 3){
            for(int j = edges.get(i + 2).getyStart(); j < edges.get(i + 2).getyEnd(); j++){
                Edge left = edges.get(i);
                Edge right = edges.get(i + 1);
                float[] vals = getCoreValuesAndSteps(left, right);
                for(int k = (int)vals[0]; k < (int)vals[1]; k++){
                    int index = k + j * width;
                    if(vals[5] < zBuffer[index]){
                        zBuffer[index] = vals[5];
                        int shade = (int)(-vals[5] * 255) +100;
                        frame.drawPixel(k, j, (byte)shade, (byte)shade, (byte)shade, (byte)shade);
                    }
                    vals[5] += vals[4];
                }
                left.step();
                right.step();
            }
        }
    }
    
    public static void drawWireFrame(Bitmap frame, List<Edge> edges){
        int width = frame.getWidth();
        int height = frame.getHeight();
        float[] zBuffer = getNewzBuffer(width, height);
        for(int i = 0; i < edges.size() ; i += 3){
            for(int j = edges.get(i + 2).getyStart(); j < edges.get(i + 2).getyEnd(); j++){
                Edge left = edges.get(i);
                Edge right = edges.get(i + 1);
                float[] vals = getCoreValuesAndSteps(left, right);
                for(int k = (int)vals[0]; k < (int)vals[1]; k++){
                    int index = k + j * width;
                    if(vals[5] < zBuffer[index] && (k == (int)vals[0] || k == (int)vals[1])){
                        zBuffer[index] = vals[5];
                        frame.drawPixel(k, j, (byte)255, (byte)255, (byte)255, (byte)255);
                    }
                    vals[5] += vals[4];
                }
                left.step();
                right.step();
            }
        }
    }
    
    public static void drawNormal(Bitmap frame, List<Edge> edges){
        int width = frame.getWidth();
        int height = frame.getHeight();
        float[] zBuffer = getNewzBuffer(width, height);
        for(int i = 0; i < edges.size() ; i += 3){
            for(int j = edges.get(i + 2).getyStart(); j < edges.get(i + 2).getyEnd(); j++){
                Edge left = edges.get(i);
                Edge right = edges.get(i + 1);
                float[] vals = getCoreValuesAndSteps(left, right);
                float[] normalVals = getNormalValuesAndSteps(left, right, vals);
                for(int k = (int)vals[0]; k < (int)vals[1]; k++){
                    int index = k + j * width;
                    if(vals[5] < zBuffer[index]){
                        zBuffer[index] = vals[5];
                        frame.drawPixel(k, j, (byte)255, (byte)((normalVals[5] + 1) * 127.5f), (byte)((normalVals[3] + 1) * 127.5f), (byte)((normalVals[1] + 1) * 127.5f));
                    }
                    vals[5] += vals[4];
                    normalVals[1] += normalVals[0];
                    normalVals[3] += normalVals[2];
                    normalVals[5] += normalVals[4];
                }
                left.step();
                right.step();
            }
        }
    }
    
    public static void drawDiffuse(Bitmap frame, List<Edge> edges){
        int width = frame.getWidth();
        int height = frame.getHeight();
        float[] zBuffer = getNewzBuffer(width, height);
        for(int i = 0; i < edges.size() ; i += 3){
            for(int j = edges.get(i + 2).getyStart(); j < edges.get(i + 2).getyEnd(); j++){
                Edge left = edges.get(i);
                Edge right = edges.get(i + 1);
                Bitmap texture = left.getTexture();
                int textureWidth = texture.getWidth();
                int textureHeight = texture.getHeight();
                float[] vals = getCoreValuesAndSteps(left, right);
                float[] UVVals = getUVValuesAndSteps(left, right, vals);
                for(int k = (int)vals[0]; k < (int)vals[1]; k++){
                    int index = k + j * width;
                    if(vals[5] < zBuffer[index]){
                        zBuffer[index] = vals[5];
                        float z = 1f / UVVals[5];
                        int srcX = (int)((UVVals[1] * z) * (textureWidth - 1) + 0.5f);
                        int srcY = (int)((UVVals[3] * z) * (textureHeight - 1) + 0.5f);
                        int srcIndex = (srcX + srcY * textureWidth) * 4;
                        frame.drawPixel(k, j, 
                                texture.getComponent(srcIndex),
                                texture.getComponent(srcIndex + 1),
                                texture.getComponent(srcIndex + 2),
                                texture.getComponent(srcIndex + 3));
                    }
                    vals[5] += vals[4];
                    UVVals[1] += UVVals[0];
                    UVVals[3] += UVVals[2];
                    UVVals[5] += UVVals[4];
                }
                left.step();
                right.step();
            }
        }
    }
    
    public static void drawLighted(
            Bitmap frame, List<Edge> edges, List<LightSource> lightSources,
            Matrix4 inverseScreenSpaceTransform, Matrix4 lightScreenSpaceTransform, Matrix4 inverseViewProjection,
            Matrix4 identity, int resolution, float[] ambientColor, float ambientIntensity){
        int width = frame.getWidth();
        int height = frame.getHeight();
        float[] zBuffer = getNewzBuffer(width, height);
        float[] wBuffer = new float[width * height];
        Vector4[] normalBuffer = new Vector4[width * height];
        for(int i = 0; i < edges.size() ; i += 3){
            for(int j = edges.get(i + 2).getyStart(); j < edges.get(i + 2).getyEnd(); j++){
                Edge left = edges.get(i);
                Edge right = edges.get(i + 1);
                Bitmap texture = left.getTexture();
                int textureWidth = texture.getWidth();
                int textureHeight = texture.getHeight();
                float[] vals = getCoreValuesAndSteps(left, right);
                float[] normalVals = getNormalValuesAndSteps(left, right, vals);
                float[] UVVals = getUVValuesAndSteps(left, right, vals);
                float wXStep = (right.getW() - left.getW())/vals[3];
                float w = left.getW() + wXStep * vals[2];
                for(int k = (int)vals[0]; k < (int)vals[1]; k++){
                    int index = k + j * width;
                    if(vals[5] < zBuffer[index]){
                        zBuffer[index] = vals[5];
                        wBuffer[index] = w;
                        normalBuffer[index] = new Vector4(normalVals[1], normalVals[3], normalVals[5]);
                        float z = 1f / UVVals[5];
                        int srcX = (int)((UVVals[1] * z) * (textureWidth - 1) + 0.5f);
                        int srcY = (int)((UVVals[3] * z) * (textureHeight - 1) + 0.5f);
                        int srcIndex = (srcX + srcY * textureWidth) * 4;
                        frame.drawPixel(k, j, 
                                texture.getComponent(srcIndex),
                                texture.getComponent(srcIndex + 1),
                                texture.getComponent(srcIndex + 2),
                                texture.getComponent(srcIndex + 3));
                    }
                    vals[5] += vals[4];
                    normalVals[1] += normalVals[0];
                    normalVals[3] += normalVals[2];
                    normalVals[5] += normalVals[4];
                    UVVals[1] += UVVals[0];
                    UVVals[3] += UVVals[2];
                    UVVals[5] += UVVals[4];
                }
                left.step();
                right.step();
            }
        }
        
        int lightSourcesAmnt = lightSources.size();
        
        float[][] lightColors = new float[lightSourcesAmnt][3];
        float[] lightIntensities = new float[lightSourcesAmnt];
        float[][] lightzBuffers = new float[lightSourcesAmnt][];
        Matrix4[] lightViewProjections = new Matrix4[lightSourcesAmnt];
        Transform[] lightTransforms = new Transform[lightSourcesAmnt];
        
        for(int i = 0; i < lightSourcesAmnt; i++){
            lightColors[i] = lightSources.get(i).getColorValues();
            lightIntensities[i] = lightSources.get(i).getIntensity();
            lightViewProjections[i] = lightSources.get(i).getViewProjection();
            lightzBuffers[i] = lightSources.get(i).getzBuffer();
            lightTransforms[i] = lightSources.get(i).getTransform();
        }

        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                if(zBuffer[j*width + i] != Float.MAX_VALUE){
                    float[] pixelLightIntensity = new float[lightSourcesAmnt];
                    for(int m = 0; m < lightSourcesAmnt; m++){
                        Vertex v = new Vertex(new Vector4(i, j, zBuffer[j*width + i], wBuffer[j*width + i]), new Vector4(0f,0f,0f), normalBuffer[j*width + i]);
                        v = v.perspectiveUnDivide().transform(inverseScreenSpaceTransform, identity).transform(inverseViewProjection, identity);
                        v = v.transform(lightViewProjections[m], identity);
                        float intensity = 0;
                        if(v.isInsideViewFrustum()){
                            v = v.transform(lightScreenSpaceTransform, identity).perspectiveDivide();
                            if(lightzBuffers[m][(int)v.getPosition().getY() * resolution + (int)v.getPosition().getX()] > v.getPosition().getZ() - 0.001){
                                intensity = lightTransforms[m].getRotation().getDown().dot(v.getNormal());
                                if(intensity < 0)
                                    intensity = 0;
                                if(intensity > 1)
                                    intensity = 1;
                                intensity *= lightIntensities[m];
                            }
                        }
                        pixelLightIntensity[m] = intensity;
                    }
                    float[] pixelLightColors = new float[3];
                    for(int m = 0; m < lightSourcesAmnt; m++){
                        for(int k = 0; k < 3; k++)
                            pixelLightColors[k] += lightColors[m][k] * pixelLightIntensity[m];
                            
                    }
                    for(int k = 0; k < 3; k++){
                        pixelLightColors[k] += ambientColor[k] * ambientIntensity;
                        if(pixelLightColors[k] > 1)
                                pixelLightColors[k] = 1;
                    }
                    byte[] pixel = frame.getPixelValues(i, j);
                    float[] pixelValues = new float[3];
                    for(int m = 0; m < 3; m++){
                        pixelValues[m] = (float)(pixel[m + 1] & 0XFF) / 255 * pixelLightColors[m];
                        if(pixelValues[m] > 1)
                            pixelValues[m] = 1;
                    }
                    frame.drawPixel(i, j, (byte)0, (byte)(int)(pixelValues[0] * 255), (byte)(int)(pixelValues[1] * 255), (byte)(int)(pixelValues[2] * 255));
                }
            }
        }
    }
    
    private static float[] getNewzBuffer(int width, int height){
        float[] zBuffer = new float[width * height];
        Arrays.fill(zBuffer, Float.MAX_VALUE);
        return zBuffer;
    }
    
    private static float[] getCoreValuesAndSteps(Edge left, Edge right){
        float[] vals = new float[6];
        vals[0] = (int)Math.ceil(left.getX());
        vals[1] = (int)Math.ceil(right.getX());
        vals[2] = vals[0] - left.getX();
        vals[3] = right.getX() - left.getX();
        vals[4] = (right.getDepth() - left.getDepth())/vals[3];
        vals[5] = left.getDepth() + vals[4] * vals[2];
        
        return vals;
    }
    
    private static float[] getNormalValuesAndSteps(Edge left, Edge right, float[] vals){
        float[] normalVals = new float[6];
        normalVals[0] = (right.getNormalX() - left.getNormalX())/vals[3];
        normalVals[1] = left.getNormalX() + normalVals[0] * vals[2];
        normalVals[2] = (right.getNormalY() - left.getNormalY())/vals[3];
        normalVals[3] = left.getNormalY() + normalVals[2] * vals[2];
        normalVals[4] = (right.getNormalZ() - left.getNormalZ())/vals[3];
        normalVals[5] = left.getNormalZ() + normalVals[4] * vals[2];
        return normalVals;
    }
    
    private static float[] getUVValuesAndSteps(Edge left, Edge right, float[] vals){
        float[] UVVals = new float[6];
        UVVals[0] = (right.getUVcoordX() - left.getUVcoordX())/vals[3];
        UVVals[1] = left.getUVcoordX() + UVVals[0] * vals[2];
        UVVals[2] = (right.getUVcoordY() - left.getUVcoordY())/vals[3];
        UVVals[3] = left.getUVcoordY() + UVVals[2] * vals[2];
        UVVals[4] = (right.getOneOverZ() - left.getOneOverZ())/vals[3];
        UVVals[5] = left.getOneOverZ() + UVVals[4] * vals[2];
        return UVVals;
    }
}
