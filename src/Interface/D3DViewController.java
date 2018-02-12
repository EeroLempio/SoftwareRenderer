package Interface;


import RenderingEngine.Constructs.Transform;
import RenderingEngine.CoreComponents.Quaternion;
import RenderingEngine.CoreComponents.Vector4;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;


/**
 * D3DViewController is a key and mouse listener which allows for traversing a 3D scene by keyboard commands and mouse
 * 
 * @author Eero Lempiö el415524@student.uta.fi
 */
public class D3DViewController implements  KeyListener, MouseMotionListener{
    private final int[] m_keys;
    private final boolean[] m_actions;
    
    private int m_mouseXLockPosition;
    private int m_mouseYLockPosition;
    private int m_mouseXMoveAmount;
    private int m_mouseYMoveAmount;
    private int m_mouseMultiplier;
    
    /**
    * Creates a new D3DViewController with keycodes int forward, int back, int left, int right
    */
    public D3DViewController(int forward, int back, int left, int right) {
        m_keys = new int[4];
        m_keys[0] = forward;
        m_keys[1] = back;
        m_keys[2] = left;
        m_keys[3] = right;
        m_actions = new boolean[4];
        setMouseLockPosition(0,0);
    }
    
    /**
    * Sets the locking point of the mouse for the Robot class
    */
    public void setMouseLockPosition(int x, int y){
        m_mouseXLockPosition = x;
        m_mouseYLockPosition = y;
        m_mouseXMoveAmount = 0;
        m_mouseYMoveAmount = 0;
        m_mouseMultiplier = 1;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        for(int i = 0; i < 4; i++)
            if(keyCode == m_keys[i]){
                m_actions[i] = true;
                break;
            }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        for(int i = 0; i < 4; i++)
            if(keyCode == m_keys[i]){
                m_actions[i] = false;
                break;
            }
    }

    @Override
    public void mouseDragged(MouseEvent e) {mouseMoved(e);}
    @Override
    public void mouseMoved(MouseEvent e) {
        try{
            int mouseXPosition = e.getXOnScreen();
            int mouseYPosition = e.getYOnScreen();
            m_mouseXMoveAmount += mouseXPosition - m_mouseXLockPosition;
            m_mouseYMoveAmount += mouseYPosition - m_mouseYLockPosition;
            m_mouseMultiplier ++;
            Robot r = new Robot();
            r.mouseMove(m_mouseXLockPosition, m_mouseYLockPosition);
        }
        catch (AWTException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
    * Returns a new Transform transformed by delta, keys pressed and the amount the mouse moved in different directions
    */
    public Transform update(float delta, Transform transform){
        final float sensitivityX = 2.66f * delta;
        final float sensitivityY = 2.0f * delta;
        final float movAmt = 5.0f * delta;
        if(m_actions[0])
                transform = move(transform, transform.getRotation().getForward(), movAmt);
        if(m_actions[1])
                transform = move(transform, transform.getRotation().getForward(), -movAmt);
        if(m_actions[2])
                transform = move(transform, transform.getRotation().getLeft(), movAmt);
        if(m_actions[3])
                transform = move(transform, transform.getRotation().getRight(), movAmt);
        
        transform = rotate(transform, new Vector4(0f,1f,0f), m_mouseXMoveAmount / m_mouseMultiplier * delta * 0.2f);
        transform = rotate(transform, transform.getRotation().getRight(), m_mouseYMoveAmount / m_mouseMultiplier * delta * 0.2f);
        m_mouseXMoveAmount = 0;
        m_mouseYMoveAmount = 0;
        m_mouseMultiplier = 1;
        return transform;
    }
    
    private Transform move(Transform transform, Vector4 dir, float amt){
        return(transform.setPosition(transform.getPosition().add(dir.mul(amt))));
    }

    private Transform rotate(Transform transform, Vector4 axis, float angle){
        return(transform.rotate(new Quaternion(axis, angle)));
    }
}
