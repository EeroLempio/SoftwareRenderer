package Interface;





import RenderingEngine.Constructs.BaseObject;


/**
 * ObjectEditorInterface is an interface for linking an RendererModel and an object editor together
 * 
 * @author Eero Lempiö el415524@student.uta.fi
 */
public interface ObjectEditorInterface {
    void setModel(RendererModel rendererModel);
    void reset(BaseObject baseObject);
    void refreshTransform(BaseObject baseObject);
    void refreshColor(BaseObject baseObject);
    void refreshLight(BaseObject baseObject);
}
