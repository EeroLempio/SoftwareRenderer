package RenderingEngine.Rendering;

import RenderingEngine.Constructs.Edge;
import RenderingEngine.Constructs.EngineObject;
import RenderingEngine.Constructs.Gradients;
import RenderingEngine.Constructs.Vertex;
import RenderingEngine.CoreComponents.Bitmap;
import RenderingEngine.CoreComponents.Matrix4;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * EdgeCalculator
 * 
 * @author Eero Lempiö el415524@student.uta.fi
 */
public class EdgeCalculator {
    /**
    * Returns the Edges made up of EngineObjects o's Mesh's vertices transformed by Matrix4 viewProjection, Matrix4 screenSpaceTransform and Matrix4 normalTransform,
    * if they can be drawn
    */
    public static List<Edge> getEdges(Matrix4 viewProjection, Matrix4 screenSpaceTransform, Matrix4 normalTransform, EngineObject o){
        Vertex[] triangleVertices = o.getWorldVertices();
        List<Edge> edges = new ArrayList<>();
        Bitmap texture = o.getTexture();
        for(int i = 0; i < triangleVertices.length; i +=3){
            Vertex v1 = triangleVertices[i].transform(viewProjection, normalTransform);
            Vertex v2 = triangleVertices[i + 1].transform(viewProjection, normalTransform);
            Vertex v3 = triangleVertices[i + 2].transform(viewProjection, normalTransform);
            List<Vertex> vertices = EdgeCalculator.clip(v1, v2, v3);
            for(int j = 1; j < vertices.size()- 1; j++)
                edgesFromVertices(vertices.get(0),vertices.get(j),vertices.get(j + 1), screenSpaceTransform, normalTransform, edges, texture);
        }
        return edges;
    }
    
    private static void edgesFromVertices(Vertex v1, Vertex v2, Vertex v3, Matrix4 screenSpaceTransform, Matrix4 normalTransform, List<Edge> edges, Bitmap texture){
        Vertex minYvert = v1.transform(screenSpaceTransform, normalTransform).perspectiveDivide();
        Vertex midYvert = v2.transform(screenSpaceTransform, normalTransform).perspectiveDivide();
        Vertex maxYvert = v3.transform(screenSpaceTransform, normalTransform).perspectiveDivide();
        
        if(minYvert.triangleArea(maxYvert, midYvert) >= 0)
            return;
        if(maxYvert.getPosition().getY() < midYvert.getPosition().getY()){
            Vertex temp = maxYvert;
            maxYvert = midYvert;
            midYvert = temp;
        }
        if(midYvert.getPosition().getY() < minYvert.getPosition().getY()){
            Vertex temp = midYvert;
            midYvert = minYvert;
            minYvert = temp;
        }
        if(maxYvert.getPosition().getY() < midYvert.getPosition().getY()){
            Vertex temp = maxYvert;
            maxYvert = midYvert;
            midYvert = temp;
        }
        Gradients gradients = new Gradients(minYvert, midYvert, maxYvert);
        Edge topToBottom = new Edge(texture, gradients, minYvert, maxYvert, 0);
        Edge topToMiddle = new Edge(texture, gradients, minYvert, midYvert, 0);
        Edge middleToBottom = new Edge(texture, gradients, midYvert, maxYvert, 1);
        if(minYvert.triangleArea(maxYvert, midYvert) >= 0){
            edges.add(topToMiddle);
            edges.add(topToBottom);
            edges.add(topToMiddle);
            edges.add(middleToBottom);
            edges.add(topToBottom);
            edges.add(middleToBottom);
        }
        else{
            edges.add(topToBottom);
            edges.add(topToMiddle);
            edges.add(topToMiddle);
            edges.add(topToBottom);
            edges.add(middleToBottom);
            edges.add(middleToBottom);
        }
    }
    
    private static List<Vertex> clip (Vertex v1, Vertex v2, Vertex v3) {
        List<Vertex> vertices = new ArrayList<>();
        vertices.add(v1);
        vertices.add(v2);
        vertices.add(v3);
        if(v1.isInsideViewFrustum() && v2.isInsideViewFrustum() && v3.isInsideViewFrustum())
            return vertices;

        List<Vertex> auxilliary = new ArrayList<>();
        if(clipPolygonAxis(vertices, auxilliary, 0) && clipPolygonAxis(vertices, auxilliary, 1) && clipPolygonAxis(vertices, auxilliary, 2))
            return vertices;
        
        return new ArrayList<>();
    } 
    
    private static boolean clipPolygonAxis(List<Vertex> vertices, List<Vertex> auxilliary, int componentIndex){
        clipPolygonComponent(vertices, componentIndex, 1f, auxilliary);
        vertices.clear();
        if(auxilliary.isEmpty())
            return false;
        clipPolygonComponent(auxilliary, componentIndex, -1f, vertices);
        auxilliary.clear();
        return !vertices.isEmpty();
    }
    
    private static void clipPolygonComponent(List<Vertex> vertices, int componentIndex, float componentFactor, List<Vertex> result){
        Vertex previousVertex = vertices.get(vertices.size() - 1);
        float previousComponent = previousVertex.get(componentIndex) * componentFactor;
        boolean previousInside = previousComponent <= previousVertex.getPosition().getW();
        Iterator<Vertex> it = vertices.iterator();
        while(it.hasNext()){
            Vertex currentVertex = it.next();
            float currentComponent = currentVertex.get(componentIndex) * componentFactor;
            boolean currentInside = currentComponent <= currentVertex.getPosition().getW();
            if(currentInside ^ previousInside){
                float lerpAmount = (previousVertex.getPosition().getW() - previousComponent) /
                        ((previousVertex.getPosition().getW() - previousComponent) -
                        (currentVertex.getPosition().getW() - currentComponent));
                result.add(previousVertex.lerp(currentVertex, lerpAmount));
            }
            
            if(currentInside)
                result.add(currentVertex);
            
            previousVertex = currentVertex;
            previousComponent = currentComponent;
            previousInside = currentInside;
        }
    }
}
