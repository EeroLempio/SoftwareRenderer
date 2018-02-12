package RenderingEngine.Constructs;

import RenderingEngine.CoreComponents.Matrix4;
import java.util.List;

/**
 * Mesh is a 3D model with its geometry represented by a List of Vertex vertices
 * 
 * @author Eero Lempiö el415524@student.uta.fi
 */
public class Mesh {
    private final List<Vertex> m_vertices;
    private final List<Integer> m_indices;

    /**
    * Creates a new Mesh with its vertices set to List vertices and indices to List indices. Indices are Integers, representing which 3 vertices form a triangle
    */
    public Mesh(List<Vertex> vertices, List<Integer> indices) {
        m_vertices = vertices;
        m_indices = indices;
    }

    /**
    * Returns an array of vertices, representing the triangles making up this Mesh, transformed by Matrix4 transform and Matrix4 normalTransform
    */
    public Vertex[] getTriangleVertices(Matrix4 transform, Matrix4 normalTransform){
        Vertex[] vertices = new Vertex[m_indices.size()];
        for(int i = 0; i < m_indices.size(); i++)
            vertices[i] = m_vertices.get(m_indices.get(i)).transform(transform, normalTransform);
        return vertices;
    }
}
