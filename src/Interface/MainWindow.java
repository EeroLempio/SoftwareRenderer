package Interface;

import RenderingEngine.Constructs.Transform;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.UndoManager;


/**
 * The main window of the application
 * 
 * @author Eero Lempiö el415524@student.uta.fi
 */
public class MainWindow extends JFrame {
    private RendererModel m_model;
    private D3DViewController m_viewController;
    protected Thread m_renderThread;
    private JFileChooser m_objChooser;
    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        initRenderPanelControls();
        initModel();
        initEditOptions();
        initFileOptions();
        initRenderThread();
    }
    
    private void initRenderPanelControls() {
        renderPanel.setFocusable(true);
        m_viewController = new D3DViewController(87,83,65,68);
        JFrame thisFrame = this;
        renderPanel.addFocusListener(new FocusListener(){
            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
            new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank cursor");
            @Override
            public void focusGained(FocusEvent e) {
                Point p = renderPanel.getLocationOnScreen();
                m_viewController.setMouseLockPosition(p.x + renderPanel.getWidth()/2, p.y + renderPanel.getHeight()/2);
                renderPanel.addMouseMotionListener(m_viewController);
                renderPanel.setCursor(blankCursor);
            }

            @Override
            public void focusLost(FocusEvent e) {
                renderPanel.removeMouseMotionListener(m_viewController);
                renderPanel.setCursor(Cursor.getDefaultCursor());
            }
        });
        renderPanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(renderPanel.hasFocus())
                    thisFrame.requestFocusInWindow();
                else
                    renderPanel.requestFocusInWindow();
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        renderPanel.addKeyListener(m_viewController);
    }
    
    private void initModel(){
        m_model = new RendererModel(renderPanel, editorPanel, selectorPanel, rendererSettingsPanel);
    }
    
    private void initEditOptions(){
        editOption.addMenuListener(new MenuListener(){
            @Override
            public void menuSelected(MenuEvent e) {
                UndoManager undo = m_model.getUndoManager();
                if(undo.canUndo()){
                    undoOption.setEnabled(true);
                    undoOption.setText(undo.getUndoPresentationName());
                }
                else
                    undoOption.setEnabled(false);
                if(undo.canRedo()){
                    redoOption.setEnabled(true);
                    redoOption.setText(undo.getRedoPresentationName());
                }
                else
                    redoOption.setEnabled(false);
            }
            @Override
            public void menuDeselected(MenuEvent e) {}
            @Override
            public void menuCanceled(MenuEvent e) {}
        });
        
        undoOption.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(m_model.getUndoManager().canUndo())
                    m_model.getUndoManager().undo();
            }
        });
        undoOption.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
        
        redoOption.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(m_model.getUndoManager().canRedo())
                    m_model.getUndoManager().redo();
            }
        });
        redoOption.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
    }
    
    private void initFileOptions(){
        m_objChooser = new JFileChooser();
        m_objChooser.setFileFilter(new FileNameExtensionFilter("OBJ File","obj"));
        
        loadModelOption.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                m_objChooser.showOpenDialog(null);
                File file = m_objChooser.getSelectedFile();
                m_objChooser.setCurrentDirectory(m_objChooser.getCurrentDirectory());
                try {
                    m_model.loadEngineObject(file);
                }
                catch(IOException ex) {
                    JOptionPane.showMessageDialog(loadModelOption, "Could not read obj", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        createLightOption.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                m_model.createLightSource();
            }
        });
        loadDefaultSceneOption.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                m_model.loadDefaultScene();
            }
        });
        loadNewSceneOption.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                m_model.loadNewScene();
            }
        });
    }

    private void initRenderThread(){
        m_renderThread = new Thread(){
            @Override
            public void run(){
                long previousTime = System.nanoTime();
                while(true){
                    long currentTime = System.nanoTime();
                    float delta =(float)((currentTime - previousTime)/1000000000.0);
                    previousTime = currentTime;
                    if(renderPanel.hasFocus()){
                        Transform transform = m_viewController.update(delta, renderPanel.getCameraTransform());
                        renderPanel.setCameraTransform(transform);
                        renderPanel.render();
                    }
                }
            }
        };
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        renderPanel = new RenderingEngine.Rendering.RenderPanel();
        editorPanel = new Interface.ObjectEditorPanel();
        selectorPanel = new Interface.ObjectSelectorPanel();
        rendererSettingsPanel = new Interface.RendererSettingsPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        loadModelOption = new javax.swing.JMenuItem();
        createLightOption = new javax.swing.JMenuItem();
        loadDefaultSceneOption = new javax.swing.JMenuItem();
        loadNewSceneOption = new javax.swing.JMenuItem();
        editOption = new javax.swing.JMenu();
        undoOption = new javax.swing.JMenuItem();
        redoOption = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Software Renderer - TIEVA31 Project work by Eero Lempiö");

        javax.swing.GroupLayout renderPanelLayout = new javax.swing.GroupLayout(renderPanel);
        renderPanel.setLayout(renderPanelLayout);
        renderPanelLayout.setHorizontalGroup(
            renderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        renderPanelLayout.setVerticalGroup(
            renderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jMenu1.setText("File");

        loadModelOption.setText("Load Model");
        jMenu1.add(loadModelOption);

        createLightOption.setText("Create Light");
        jMenu1.add(createLightOption);

        loadDefaultSceneOption.setText("Load Default Scene");
        jMenu1.add(loadDefaultSceneOption);

        loadNewSceneOption.setText("Load New Scene");
        jMenu1.add(loadNewSceneOption);

        jMenuBar1.add(jMenu1);

        editOption.setText("Edit");

        undoOption.setText("Undo");
        editOption.add(undoOption);

        redoOption.setText("Redo");
        editOption.add(redoOption);

        jMenuBar1.add(editOption);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(renderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(rendererSettingsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(selectorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(renderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(editorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(rendererSettingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(selectorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        MainWindow window = new MainWindow();
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                window.setVisible(true);
                window.requestFocus();
            }
        });
        window.m_renderThread.start();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem createLightOption;
    private javax.swing.JMenu editOption;
    private Interface.ObjectEditorPanel editorPanel;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem loadDefaultSceneOption;
    private javax.swing.JMenuItem loadModelOption;
    private javax.swing.JMenuItem loadNewSceneOption;
    private javax.swing.JMenuItem redoOption;
    private RenderingEngine.Rendering.RenderPanel renderPanel;
    private Interface.RendererSettingsPanel rendererSettingsPanel;
    private Interface.ObjectSelectorPanel selectorPanel;
    private javax.swing.JMenuItem undoOption;
    // End of variables declaration//GEN-END:variables
}
