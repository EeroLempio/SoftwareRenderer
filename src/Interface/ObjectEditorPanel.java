package Interface;


import RenderingEngine.Constructs.BaseObject;
import RenderingEngine.Constructs.EngineObject;
import RenderingEngine.Constructs.LightSource;
import RenderingEngine.Constructs.Transform;
import RenderingEngine.CoreComponents.Quaternion;
import RenderingEngine.CoreComponents.Vector4;
import java.awt.Color;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;


/**
 * ObjectEditorPanel is a panel for editing EngineObjects and LightSources
 * 
 * @author Eero Lempiö el415524@student.uta.fi
 */
public class ObjectEditorPanel extends javax.swing.JPanel implements ObjectEditorInterface {
    private final JSpinner[] m_spinners;
    
    private RendererModel m_model;
    
    private JColorChooser m_colorChooser;
    private JFileChooser m_fileChooser;
    
    private ChangeListener m_spinnerListener;
    private MouseListener m_lightColorListener;
    private MouseListener m_objectTextureListener;


    /**
     * Creates new form ObjectEditorPanel
     */
    public ObjectEditorPanel() {
        initComponents();
        initChoosers();
        initListeners();
        toggleTransformParameters(false);
        toggleLightParameters(false);
        toggleColorPanel(false);
        m_spinners = new JSpinner[]{
            locationX, locationY, locationZ, rotationX, rotationY, rotationZ, rotationW, scale, angle, distance, intensity};
    }
    
    /**
     * Sets the RendererModel of this ObjectEditorPanel to RendererModel rendererModel
     */
    @Override
    public void setModel(RendererModel rendererModel) {
        m_model = rendererModel;
    }

    /**
     * Resets the displayed values of this ObjectEditorPanel to BaseObject baseObject
     */
    @Override
    public void reset(BaseObject baseObject) {
        toggleTransformParameters(false);
        toggleLightParameters(false);
        toggleColorPanel(false);
        if(baseObject != null){
            refreshTransform(baseObject);
            refreshColor(baseObject);
            if(baseObject instanceof LightSource)
                refreshLight(baseObject);
            objectName.setText(baseObject.toString());
        }
        else{
            for(JSpinner spinner : m_spinners)
                spinner.setValue(0.0f);
            colorDisplay.setBackground(Color.BLACK);
            colorDisplay.setIcon(null);
        }
    }
    
    /**
     * Sets the displayed values of this ObjectEditorPanels transform heading to BaseObject baseObject
     */
    @Override
    public void refreshTransform(BaseObject baseObject) {
        toggleTransformParameters(false);
        Transform transform = baseObject.getTransform();
        Vector4 position = transform.getPosition();
        Quaternion rotation = transform.getRotation();
        locationX.setValue(position.getX());
        locationY.setValue(position.getY());
        locationZ.setValue(position.getZ());
        rotationX.setValue(rotation.getX());
        rotationY.setValue(rotation.getY());
        rotationZ.setValue(rotation.getZ());
        rotationW.setValue(rotation.getW());
        scale.setValue(transform.getScale().getX());
        toggleTransformParameters(true);
    }

    /**
     * Sets the displayed values of this ObjectEditorPanels color heading to BaseObject baseObject
     */
    @Override
    public void refreshColor(BaseObject baseObject) {
        colorDisplay.removeMouseListener(m_lightColorListener);
        colorDisplay.removeMouseListener(m_objectTextureListener);
        if(baseObject instanceof EngineObject){
            ImageIcon imageIcon = new ImageIcon(
                    ((EngineObject)baseObject).getTexture().getSourceImage().getScaledInstance(
                            colorDisplay.getWidth(), colorDisplay.getHeight(), Image.SCALE_DEFAULT));
            colorDisplay.setIcon(imageIcon);
            colorDisplay.addMouseListener(m_objectTextureListener);
        }
        else{
            colorDisplay.setBackground(((LightSource)baseObject).getColor());
            colorDisplay.setIcon(null);
            colorDisplay.addMouseListener(m_lightColorListener);
        }
        toggleColorPanel(true);
    }

    /**
     * Sets the displayed values of this ObjectEditorPanels light heading to BaseObject baseObject
     */
    @Override
    public void refreshLight(BaseObject baseObject) {
        toggleLightParameters(false);
        if(baseObject instanceof LightSource){
            LightSource lightSource = (LightSource)baseObject;
            angle.setValue(lightSource.getAngle());
            distance.setValue(lightSource.getDistance());
            intensity.setValue(lightSource.getIntensity());
            toggleLightParameters(true);
        }
    }
    
    private void initChoosers() {
        m_colorChooser = new JColorChooser();
        m_fileChooser = new JFileChooser();
        m_fileChooser.setFileFilter(
                new FileNameExtensionFilter("JPG & GIF Images", "jpg", "gif"));
    }
    
    private void initListeners() {
         m_spinnerListener = new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                int spinnerId = 0;
                JSpinner spinner = (JSpinner)e.getSource();
                for(int i = 0; i < m_spinners.length; i++)
                    if(m_spinners[i].equals(spinner)){
                        spinnerId = i;
                        break;
                    }
                if(m_model != null){
                    if(spinnerId < 8){
                        m_model.setCurrentObjectTransform(new Transform(
                            new Vector4((float)locationX.getValue(), (float)locationY.getValue(), (float)locationZ.getValue()),
                            new Quaternion((float)rotationX.getValue(), (float)rotationY.getValue(), (float)rotationZ.getValue(), (float)rotationW.getValue()),
                            new Vector4((float)scale.getValue(), (float)scale.getValue(), (float)scale.getValue())));
                    }
                    else
                        m_model.setCurrentObjectLightParameters(
                            (float)angle.getValue(),
                            (float)distance.getValue(),
                            (float)intensity.getValue());
                }
            }
        };
        
        m_lightColorListener = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                    Color c = m_colorChooser.showDialog(null, "Choose a Color", colorDisplay.getBackground());
                    if(c != null){
                        c = new Color(c.getRed(), c.getGreen(), c.getBlue());
                        if(m_model != null)
                            m_model.setCurrentObjectColor(c);
                        colorDisplay.setBackground(c);
                    }
            }
        };
        m_objectTextureListener = new java.awt.event.MouseAdapter() {        
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                m_fileChooser.showOpenDialog(null);
                File file = m_fileChooser.getSelectedFile();
                m_fileChooser.setCurrentDirectory(m_fileChooser.getCurrentDirectory());
                try {
                    BufferedImage img = ImageIO.read(file);
                    if(m_model != null)
                        m_model.setCurrentObjectTexture(img);
                    ImageIcon imageIcon = new ImageIcon(img.getScaledInstance(colorDisplay.getWidth(), colorDisplay.getHeight(), Image.SCALE_DEFAULT));
                    colorDisplay.setIcon(imageIcon);
                }
                catch(IOException e) {
                    JOptionPane.showMessageDialog(colorDisplay, "Could not read image", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        ActionListener buttonListener = new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(duplicate))
                    m_model.duplicate();
                else
                    m_model.delete();
                JFrame frame = (JFrame) SwingUtilities.getRoot((Component)e.getSource());
                frame.requestFocus();
            }
        };
        duplicate.addActionListener(buttonListener);
        delete.addActionListener(buttonListener);
    }
    
    private void toggleLightParameters(boolean state){
        Component[] components = lightParameters.getComponents();
        for(Component c : components){
            c.setEnabled(state);
            if(c instanceof JSpinner){
                if(state)
                    ((JSpinner)c).addChangeListener(m_spinnerListener);
                else
                    ((JSpinner)c).removeChangeListener(m_spinnerListener);
            }
        }
    }
    
    private void toggleTransformParameters(boolean state){
        Component[] components = transformParameters.getComponents();
        for(Component c : components){
            c.setEnabled(state);
            if(c instanceof JSpinner){
                if(state)
                    ((JSpinner)c).addChangeListener(m_spinnerListener);
                else
                    ((JSpinner)c).removeChangeListener(m_spinnerListener);
            }
        }
        duplicate.setEnabled(state);
        delete.setEnabled(state);
    }
    
    private void toggleColorPanel(boolean state){
        Component[] components = colorPanel.getComponents();
        for(Component c : components)
            c.setEnabled(state);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        objectName = new javax.swing.JLabel();
        transformParameters = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        locationX = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        locationY = new javax.swing.JSpinner();
        locationZ = new javax.swing.JSpinner();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        rotationX = new javax.swing.JSpinner();
        rotationY = new javax.swing.JSpinner();
        rotationZ = new javax.swing.JSpinner();
        jLabel19 = new javax.swing.JLabel();
        rotationW = new javax.swing.JSpinner();
        jLabel18 = new javax.swing.JLabel();
        scale = new javax.swing.JSpinner();
        colorPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        colorDisplay = new javax.swing.JLabel();
        lightParameters = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        angle = new javax.swing.JSpinner();
        intensity = new javax.swing.JSpinner();
        jLabel10 = new javax.swing.JLabel();
        distance = new javax.swing.JSpinner();
        duplicate = new javax.swing.JButton();
        delete = new javax.swing.JButton();

        setBackground(new java.awt.Color(114, 114, 114));
        setPreferredSize(new java.awt.Dimension(736, 147));

        jPanel2.setBackground(new java.awt.Color(114, 114, 114));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Current Object:");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Editor");

        objectName.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        objectName.setText("OBJECTNAME");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(objectName, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                .addGap(255, 255, 255))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(jLabel2)
                .addComponent(objectName))
        );

        transformParameters.setBackground(new java.awt.Color(114, 114, 114));
        transformParameters.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setText("Location");

        jLabel4.setText("X");

        locationX.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(-1000.0f), Float.valueOf(1000.0f), Float.valueOf(0.001f)));
        locationX.setPreferredSize(new java.awt.Dimension(70, 22));

        jLabel6.setText("Y");

        jLabel7.setText("Z");

        locationY.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(-1000.0f), Float.valueOf(1000.0f), Float.valueOf(0.001f)));
        locationY.setPreferredSize(new java.awt.Dimension(70, 22));

        locationZ.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(-1000.0f), Float.valueOf(1000.0f), Float.valueOf(0.001f)));
        locationZ.setPreferredSize(new java.awt.Dimension(70, 22));

        jLabel14.setText("Rotation");

        jLabel15.setText("X");

        jLabel16.setText("Y");

        jLabel17.setText("Z");

        rotationX.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(-1.0f), Float.valueOf(1.0f), Float.valueOf(0.001f)));
        rotationX.setPreferredSize(new java.awt.Dimension(70, 22));

        rotationY.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(-1.0f), Float.valueOf(1.0f), Float.valueOf(0.001f)));
        rotationY.setPreferredSize(new java.awt.Dimension(70, 22));

        rotationZ.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(-1.0f), Float.valueOf(1.0f), Float.valueOf(0.001f)));
        rotationZ.setPreferredSize(new java.awt.Dimension(70, 22));

        jLabel19.setText("W");

        rotationW.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(-1.0f), Float.valueOf(1.0f), Float.valueOf(0.001f)));
        rotationW.setPreferredSize(new java.awt.Dimension(70, 22));

        jLabel18.setText("Scale");

        scale.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(-1000.0f), Float.valueOf(1000.0f), Float.valueOf(0.001f)));
        scale.setPreferredSize(new java.awt.Dimension(70, 22));

        javax.swing.GroupLayout transformParametersLayout = new javax.swing.GroupLayout(transformParameters);
        transformParameters.setLayout(transformParametersLayout);
        transformParametersLayout.setHorizontalGroup(
            transformParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transformParametersLayout.createSequentialGroup()
                .addGroup(transformParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(transformParametersLayout.createSequentialGroup()
                        .addGroup(transformParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(transformParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(transformParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(locationZ, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(locationY, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(locationX, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(4, 4, 4)
                .addGroup(transformParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(transformParametersLayout.createSequentialGroup()
                        .addGroup(transformParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(transformParametersLayout.createSequentialGroup()
                                .addGroup(transformParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabel19))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGroup(transformParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(rotationZ, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rotationY, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rotationX, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rotationW, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(transformParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(transformParametersLayout.createSequentialGroup()
                        .addComponent(scale, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        transformParametersLayout.setVerticalGroup(
            transformParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transformParametersLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(transformParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, transformParametersLayout.createSequentialGroup()
                        .addGroup(transformParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(jLabel18))
                        .addGap(0, 0, 0)
                        .addGroup(transformParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(rotationX, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(scale, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(transformParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(rotationY, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(transformParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(rotationZ, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(transformParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rotationW, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, transformParametersLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, 0)
                        .addGroup(transformParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(locationX, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(transformParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(locationY, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(transformParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(locationZ, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        colorPanel.setBackground(new java.awt.Color(114, 114, 114));
        colorPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel8.setText("Color");

        colorDisplay.setOpaque(true);

        javax.swing.GroupLayout colorPanelLayout = new javax.swing.GroupLayout(colorPanel);
        colorPanel.setLayout(colorPanelLayout);
        colorPanelLayout.setHorizontalGroup(
            colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(colorPanelLayout.createSequentialGroup()
                .addComponent(jLabel8)
                .addContainerGap(75, Short.MAX_VALUE))
            .addComponent(colorDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        colorPanelLayout.setVerticalGroup(
            colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(colorPanelLayout.createSequentialGroup()
                .addComponent(jLabel8)
                .addGap(0, 0, 0)
                .addComponent(colorDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        lightParameters.setBackground(new java.awt.Color(114, 114, 114));
        lightParameters.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel9.setText("Light Parameters");

        jLabel11.setText("Distance");

        jLabel12.setText("Angle");

        angle.setModel(new javax.swing.SpinnerNumberModel(0.0f, null, null, 0.001f));
        angle.setPreferredSize(new java.awt.Dimension(70, 22));

        intensity.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(1.0f), Float.valueOf(0.001f)));
        intensity.setPreferredSize(new java.awt.Dimension(70, 22));

        jLabel10.setText("Intensity");

        distance.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(2000.0f), Float.valueOf(0.001f)));
        distance.setPreferredSize(new java.awt.Dimension(70, 22));

        javax.swing.GroupLayout lightParametersLayout = new javax.swing.GroupLayout(lightParameters);
        lightParameters.setLayout(lightParametersLayout);
        lightParametersLayout.setHorizontalGroup(
            lightParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel9)
            .addGroup(lightParametersLayout.createSequentialGroup()
                .addGroup(lightParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(lightParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(intensity, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(distance, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(angle, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        lightParametersLayout.setVerticalGroup(
            lightParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lightParametersLayout.createSequentialGroup()
                .addComponent(jLabel9)
                .addGap(0, 0, 0)
                .addGroup(lightParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(angle, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(lightParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(distance, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(lightParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(intensity, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        duplicate.setText("Duplicate");

        delete.setText("Delete");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(transformParameters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(colorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(lightParameters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(duplicate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(delete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(transformParameters, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(colorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lightParameters, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(duplicate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(delete)
                        .addContainerGap())))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner angle;
    private javax.swing.JLabel colorDisplay;
    private javax.swing.JPanel colorPanel;
    private javax.swing.JButton delete;
    private javax.swing.JSpinner distance;
    private javax.swing.JButton duplicate;
    private javax.swing.JSpinner intensity;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel lightParameters;
    private javax.swing.JSpinner locationX;
    private javax.swing.JSpinner locationY;
    private javax.swing.JSpinner locationZ;
    private javax.swing.JLabel objectName;
    private javax.swing.JSpinner rotationW;
    private javax.swing.JSpinner rotationX;
    private javax.swing.JSpinner rotationY;
    private javax.swing.JSpinner rotationZ;
    private javax.swing.JSpinner scale;
    private javax.swing.JPanel transformParameters;
    // End of variables declaration//GEN-END:variables
}
