package RenderingEngine.Constructs;

import RenderingEngine.CoreComponents.Matrix4;
import RenderingEngine.CoreComponents.Vector4;

/**
 * Camera contains the necessary projectionMatrices for rendering.
 * 
 * @author Eero Lempiö el415524@student.uta.fi
 */
public class Camera extends BaseObject{
    protected Matrix4
            m_projection, m_inverseProjection,
            m_viewProjection, m_inverseViewProjection;
    
    
    public Camera(String name, Transform transform, float fov, float aspectRatio, float zNear, float zFar) {
        super(name, transform);
        setProjection(fov, aspectRatio, zNear, zFar);
        viewProjection();
    }
    
    /**
    * Sets this Camera's transform to Transform transform and recalculates its view projection
    */
    @Override
    public void setTransform(Transform transform) {super.setTransform(transform); viewProjection();}
    
    /**
    * Sets this Camera's projection based on float fov, float aspectRatio, float zNear (minimum distance), float zFar (maximum distance)
    * and then also recalculates its view projection
    */
    public void setProjection(float fov, float aspectRatio, float zNear, float zFar) {
        m_projection = new Matrix4().initPerspective((float)Math.toRadians(fov), aspectRatio, zNear, zFar);
        m_inverseProjection = new Matrix4().initInversePerspective((float)Math.toRadians(fov), aspectRatio, zNear, zFar);
        viewProjection();
    }

    /**
    * Returns this Camera's view projection
    */
    public Matrix4 getViewProjection() {return m_viewProjection;}
    
    /**
    * Returns this Camera's view projections inverse
    */
    public Matrix4 getInverseViewProjection() {return m_inverseViewProjection;}
    
    /**
    * Calculates this Camera's view projection and inverse view projection
    */
    private void viewProjection() {
        Matrix4 cameraRotation = m_transform.getRotation().conjugate().toRotationMatrix();
        Vector4 cameraPosition = m_transform.getPosition();
        Matrix4 cameraTranslation = new Matrix4().initTranslation(-cameraPosition.getX(), -cameraPosition.getY(), -cameraPosition.getZ());
        m_viewProjection = m_projection.mul(cameraRotation.mul(cameraTranslation));
        cameraRotation = m_transform.getRotation().toRotationMatrix();
        cameraTranslation = new Matrix4().initTranslation(cameraPosition.getX(), cameraPosition.getY(), cameraPosition.getZ());
        m_inverseViewProjection = cameraTranslation.mul(cameraRotation).mul(m_inverseProjection);
    }
}
