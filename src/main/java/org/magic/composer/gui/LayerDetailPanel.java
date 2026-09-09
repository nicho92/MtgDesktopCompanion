package org.magic.composer.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.magic.composer.layer.IllustrationLayer;
import org.magic.composer.layer.Layer;
import org.magic.composer.layer.TextLayer;
import org.magic.composer.gui.listeners.LayerChangeListener;

public class LayerDetailPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JTextField nameField = new JTextField();
    private final JLabel typeValue = new JLabel("-");
    private final JSpinner xSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
    private final JSpinner ySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
    private final JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
    private final JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
    private final JCheckBox visibleCheckBox =  new JCheckBox("Visible");
  
    private Layer layer;
    private LayerChangeListener layerChangeListener;
    private boolean updating = false;

    public LayerDetailPanel() {

        setLayout(new BorderLayout());

        var content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        content.add(createSectionTitle("Layer"));
        content.add(createProperty("Name", nameField));
        content.add(createProperty("Type", typeValue));

        content.add(Box.createVerticalStrut(10));

        content.add(createSectionTitle("Transform"));
        content.add(createProperty("X", xSpinner));
        content.add(createProperty("Y", ySpinner));
        content.add(createProperty("Width", widthSpinner));
        content.add(createProperty("Height", heightSpinner));

        content.add(Box.createVerticalStrut(10));

        content.add(createSectionTitle("Properties"));
        	content.add(visibleCheckBox,BorderLayout.WEST);
       	
        content.add(Box.createVerticalStrut(10));

        content.add(createSectionTitle("Source"));

        var scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        add(scrollPane,BorderLayout.CENTER );

        /*
         * -------------------------------------------------------------
         * Listeners
         * -------------------------------------------------------------
         */

        installListeners();
      
        clear();
    }

    // =================================================================
    // UI
    // =================================================================

    private JLabel createSectionTitle(String title) {

        var label = new JLabel(title);

        label.setFont(
            label.getFont().deriveFont(Font.BOLD)
        );

        label.setBorder(
            BorderFactory.createEmptyBorder(
                5, 0, 5, 0
            )
        );

        label.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        return label;
    }

    private JPanel createProperty(
        String name,
        Component value
    ) {

        var panel =
            new JPanel(new BorderLayout(10, 0));

        panel.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        var nameLabel =
            new JLabel(name);

        nameLabel.setPreferredSize(
            new Dimension(70, 20)
        );

        panel.add(
            nameLabel,
            BorderLayout.WEST
        );

        panel.add(
            value,
            BorderLayout.CENTER
        );

        return panel;
    }

    // =================================================================
    // Listeners
    // =================================================================

    private void installListeners() {
        nameField.getDocument()
            .addDocumentListener(
                new DocumentListener() {

                    @Override
                    public void insertUpdate(
                        DocumentEvent e) {

                        updateName();
                    }

                    @Override
                    public void removeUpdate(
                        DocumentEvent e) {

                        updateName();
                    }

                    @Override
                    public void changedUpdate(
                        DocumentEvent e) {

                        updateName();
                    }
                }
            );

        xSpinner.addChangeListener(
            _ -> updateX()
        );

        ySpinner.addChangeListener(
            _ -> updateY()
        );

        widthSpinner.addChangeListener(
            _ -> updateWidth()
        );

        heightSpinner.addChangeListener(
            _ -> updateHeight()
        );

        visibleCheckBox.addActionListener(
            _ -> updateVisibility()
        );
       
    }
    
 
    private void updateName() {

        if (updating || layer == null) {
            return;
        }

        layer.setName(
            nameField.getText()
        );

        notifyLayerChanged();
    }

    private void updateX() {

        if (updating || layer == null) {
            return;
        }

        layer.setX(
            (Integer) xSpinner.getValue()
        );

        notifyLayerChanged();
    }

    private void updateY() {

        if (updating || layer == null) {
            return;
        }

        layer.setY(
            (Integer) ySpinner.getValue()
        );

        notifyLayerChanged();
    }

    private void updateWidth() {

        if (updating || layer == null) {
            return;
        }

        int width = (Integer) widthSpinner.getValue();
        
        
        if(layer instanceof IllustrationLayer illustration)
            illustration.setWidth(width);
        else if (layer instanceof TextLayer textLayer)
            textLayer.setWidth(width);

        notifyLayerChanged();
    }

    private void updateHeight() {

        if (updating || layer == null) {
            return;
        }

        int height =
            (Integer) heightSpinner.getValue();

        if(layer instanceof IllustrationLayer illustration)
            illustration.setHeight(height);
        else if (layer instanceof TextLayer textLayer)
            textLayer.setHeight(height);

        notifyLayerChanged();
    }

    private void updateVisibility() {

        if (updating || layer == null) {
            return;
        }

        layer.setVisible(
            visibleCheckBox.isSelected()
        );

        notifyLayerChanged();
    }

    private void notifyLayerChanged() {
        if (layerChangeListener != null) {
            layerChangeListener.layerChanged(layer);
        }
        repaint();
    }

    public void setLayerChangeListener(LayerChangeListener listener) {
        layerChangeListener = listener;
    }

    public Layer getLayer() {
        return layer;
    }

    public void refresh(Layer layer) {

        updating = true;
        
        
        try {

            this.layer = layer;

            if (layer == null) {
                clear();
                return;
            }

			nameField.setText(layer.getName());
			typeValue.setText(layer.getClass().getSimpleName());
			xSpinner.setValue(layer.getX());
			ySpinner.setValue(layer.getY());
			widthSpinner.setValue(layer.getWidth());
			heightSpinner.setValue(layer.getHeight());
			visibleCheckBox.setSelected(layer.isVisible());
           

        } finally {

            updating = false;
        }
    }

    public void clear() {

        updating = true;

        try {

            this.layer = null;

            nameField.setText("-");
            typeValue.setText("-");

            xSpinner.setValue(0);
            ySpinner.setValue(0);
            widthSpinner.setValue(1);
            heightSpinner.setValue(1);

            visibleCheckBox.setSelected(false);
          

        } finally {

            updating = false;
        }
    }

}
