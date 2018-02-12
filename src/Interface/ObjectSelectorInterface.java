package Interface;




import RenderingEngine.Constructs.BaseObject;


/**
 * ObjectSelectorInterface is an interface for linking a RendererModel and an object selector together
 * 
 * @author Eero Lempiö el415524@student.uta.fi
 */
public interface ObjectSelectorInterface {
    void setModel(RendererModel rendererModel);
    void setActiveObject(BaseObject baseObject);
    void removeObject(BaseObject baseObject);
    void reset();
}
