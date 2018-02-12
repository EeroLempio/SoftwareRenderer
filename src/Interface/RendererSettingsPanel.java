package Interface;


import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeListener;


/**
 * RendererSettingsPanel is a panel for editin different rendering settings and parameters
 * 
 * @author Eero Lempiö el415524@student.uta.fi
 */
public class RendererSettingsPanel extends javax.swing.JPanel implements RendererSettingsInterface{
    private RendererModel m_model;
    private final JRadioButton[] m_buttons;
    private ItemListener m_renderModeListener;
    private ChangeListener m_cameraParamsListener;
    private ChangeListener m_lightingParamsListener;
    private MouseListener m_colorListener;
    
    /**
     * Creates a new RendererSettingsPanel
     */
    public RendererSettingsPanel() {
        initComponents();
        m_buttons = new JRadioButton[]{depth, wire, normal, diffuse, staticLight, dynamicLight};
        initListeners();
    }
    
    /**
     * Sets the RendererModel of this RendererSettingsPanel to RendererModel rendererModel
     */
    @Override
    public void setModel(RendererModel rendererModel) {
        m_model = rendererModel;
    }
    
    /**
     * Sets the displayed rendering mode of this RendererSettingsPanel to int mode
     */
    @Override
    public void setMode(int mode){
        for(JRadioButton button : m_buttons)
            button.removeItemListener(m_renderModeListener);
        m_buttons[mode].setSelected(true);
        for(JRadioButton button : m_buttons)
            button.addItemListener(m_renderModeListener);
    }
    
    /**
     * Sets the displayed view parameters of this RendererSettingsPanel to int fov, float nearClip, float farClip
     */
    @Override
    public void setViewParameters(int fov, float nearClip, float farClip){
        this.fov.removeChangeListener(m_cameraParamsListener);
        this.nearClip.removeChangeListener(m_cameraParamsListener);
        this.farClip.removeChangeListener(m_cameraParamsListener);
        this.fov.setValue(fov);
        fovDisplay.setText(Integer.toString(fov));
        this.nearClip.setValue(nearClip);
        this.farClip.setValue(farClip);
        this.fov.addChangeListener(m_cameraParamsListener);
        this.nearClip.addChangeListener(m_cameraParamsListener);
        this.farClip.addChangeListener(m_cameraParamsListener);
    }
    
    /**
     * Sets the displayed illumination parameters of this RendererSettingsPanel to Color zenithColor, Color ambientLightColor, float ambientLightIntensity, int lightMapResolution
     */
    @Override
    public void setIlluminationParameters(Color zenithColor, Color ambientLightColor, float ambientLightIntensity, int lightMapResolution){
        ambientStrength.removeChangeListener(m_lightingParamsListener);
        this.lightMapResolution.removeChangeListener(m_lightingParamsListener);
        zenithColorLabel.removeMouseListener(m_colorListener);
        ambientColorLabel.removeMouseListener(m_colorListener);
        this.ambientColorLabel.setBackground(ambientLightColor);
        this.zenithColorLabel.setBackground(zenithColor);
        this.ambientStrength.setValue(ambientLightIntensity);
        this.lightMapResolution.setValue(lightMapResolution);
        ambientStrength.addChangeListener(m_lightingParamsListener);
        this.lightMapResolution.addChangeListener(m_lightingParamsListener);
        zenithColorLabel.addMouseListener(m_colorListener);
        ambientColorLabel.addMouseListener(m_colorListener);
    }
    
    private void initListeners() {
        m_renderModeListener = new java.awt.event.ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED && m_model != null){
                    for(int i = 0; i < m_buttons.length; i++)
                        if(e.getSource().equals(m_buttons[i])){
                            m_model.setRenderMode(i);
                            break;
                        }
                }
            }
        };
        for(JRadioButton button : m_buttons)
            button.removeItemListener(m_renderModeListener);
        
        m_cameraParamsListener = new ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                if(m_model != null)
                    m_model.setViewParameters((float)fov.getValue(), (float)nearClip.getValue(), (float)farClip.getValue());
                if(evt.getSource().equals(fov))   
                    fovDisplay.setText(Integer.toString((int)fov.getValue()));
            }
        };
        this.fov.addChangeListener(m_cameraParamsListener);
        this.nearClip.addChangeListener(m_cameraParamsListener);
        this.farClip.addChangeListener(m_cameraParamsListener);
        
        
        m_lightingParamsListener = new ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                m_model.setIlluminationParameters(
                        zenithColorLabel.getBackground(), ambientColorLabel.getBackground(),
                        (float)ambientStrength.getValue(), (int)lightMapResolution.getValue());  
            }
        };
        ambientStrength.addChangeListener(m_lightingParamsListener);
        this.lightMapResolution.addChangeListener(m_lightingParamsListener);
        
        m_colorListener= new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JLabel label = (JLabel)evt.getSource();
                Color c = JColorChooser.showDialog(null, "Choose a Color", label.getBackground());
                if(c != null){
                    c = new Color(c.getRed(), c.getGreen(), c.getBlue());
                    label.setBackground(c);
                    if(m_model != null)
                        m_model.setIlluminationParameters(
                                zenithColorLabel.getBackground(), ambientColorLabel.getBackground(),
                                (float)ambientStrength.getValue(), (int)lightMapResolution.getValue());
                }
            }
        };
        zenithColorLabel.addMouseListener(m_colorListener);
        ambientColorLabel.addMouseListener(m_colorListener);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        RenderModeGroup = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        dynamicLight = new javax.swing.JRadioButton();
        staticLight = new javax.swing.JRadioButton();
        diffuse = new javax.swing.JRadioButton();
        wire = new javax.swing.JRadioButton();
        normal = new javax.swing.JRadioButton();
        depth = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        fov = new javax.swing.JSlider();
        jLabel5 = new javax.swing.JLabel();
        nearClip = new javax.swing.JSpinner();
        farClip = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        fovDisplay = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        ambientColorLabel = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        zenithColorLabel = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        ambientStrength = new javax.swing.JSpinner();
        lightMapResolution = new javax.swing.JSpinner();

        setBackground(new java.awt.Color(114, 114, 114));
        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Render Settings");

        jPanel1.setBackground(new java.awt.Color(114, 114, 114));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Render Mode");

        RenderModeGroup.add(dynamicLight);
        dynamicLight.setSelected(true);
        dynamicLight.setText("Lighted Dynamic");
        dynamicLight.setContentAreaFilled(false);

        RenderModeGroup.add(staticLight);
        staticLight.setText("Lighted Static");
        staticLight.setContentAreaFilled(false);

        RenderModeGroup.add(diffuse);
        diffuse.setText("Diffuse");
        diffuse.setContentAreaFilled(false);

        RenderModeGroup.add(wire);
        wire.setText("WireFrame");
        wire.setContentAreaFilled(false);

        RenderModeGroup.add(normal);
        normal.setText("Normal");
        normal.setContentAreaFilled(false);

        RenderModeGroup.add(depth);
        depth.setText("Depth");
        depth.setContentAreaFilled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dynamicLight)
                            .addComponent(diffuse)
                            .addComponent(wire))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(depth)
                            .addComponent(normal)
                            .addComponent(staticLight))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dynamicLight)
                    .addComponent(staticLight))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(diffuse)
                    .addComponent(normal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wire)
                    .addComponent(depth))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(114, 114, 114));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setText("View Parameters");

        jLabel4.setText("Field of View");

        fov.setMajorTickSpacing(30);
        fov.setMaximum(180);
        fov.setPaintLabels(true);
        fov.setPaintTicks(true);
        fov.setValue(90);
        fov.setOpaque(false);

        jLabel5.setText("Near Clip Plane");

        nearClip.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.1f), Float.valueOf(0.1f), Float.valueOf(1000.0f), Float.valueOf(0.1f)));
        nearClip.setMinimumSize(new java.awt.Dimension(70, 22));
        nearClip.setPreferredSize(new java.awt.Dimension(70, 22));

        farClip.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(100.0f), Float.valueOf(0.1f), Float.valueOf(1000.0f), Float.valueOf(0.1f)));
        farClip.setMinimumSize(new java.awt.Dimension(70, 22));
        farClip.setPreferredSize(new java.awt.Dimension(70, 22));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel6.setText("Far Clip Plane");

        fovDisplay.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        fovDisplay.setText("90");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nearClip, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(farClip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(fov, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(fovDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(fovDisplay))
                .addGap(3, 3, 3)
                .addComponent(fov, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nearClip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(farClip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(114, 114, 114));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel7.setText("Lighting Parameters");

        jLabel9.setText("Ambient Color");

        ambientColorLabel.setOpaque(true);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel11.setText("Zenith Color");

        zenithColorLabel.setOpaque(true);

        jLabel13.setText("Ambient Intensity");

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel14.setText("LightMap Resolution");

        ambientStrength.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.1f), Float.valueOf(0.0f), Float.valueOf(1.0f), Float.valueOf(0.01f)));
        ambientStrength.setMinimumSize(new java.awt.Dimension(70, 22));
        ambientStrength.setPreferredSize(new java.awt.Dimension(70, 22));

        lightMapResolution.setModel(new javax.swing.SpinnerNumberModel(800, 0, 4000, 1));
        lightMapResolution.setMinimumSize(new java.awt.Dimension(70, 22));
        lightMapResolution.setPreferredSize(new java.awt.Dimension(70, 22));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(ambientColorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ambientStrength, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(lightMapResolution, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(zenithColorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(zenithColorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel11))
                        .addGap(0, 0, 0)
                        .addComponent(ambientColorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ambientStrength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lightMapResolution, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel1)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup RenderModeGroup;
    private javax.swing.JLabel ambientColorLabel;
    private javax.swing.JSpinner ambientStrength;
    private javax.swing.JRadioButton depth;
    private javax.swing.JRadioButton diffuse;
    private javax.swing.JRadioButton dynamicLight;
    private javax.swing.JSpinner farClip;
    private javax.swing.JSlider fov;
    private javax.swing.JLabel fovDisplay;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSpinner lightMapResolution;
    private javax.swing.JSpinner nearClip;
    private javax.swing.JRadioButton normal;
    private javax.swing.JRadioButton staticLight;
    private javax.swing.JRadioButton wire;
    private javax.swing.JLabel zenithColorLabel;
    // End of variables declaration//GEN-END:variables
}
