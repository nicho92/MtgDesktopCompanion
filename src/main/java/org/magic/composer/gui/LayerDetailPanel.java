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
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.magic.composer.layer.FrameLayer;
import org.magic.composer.layer.IllustrationLayer;
import org.magic.composer.layer.Layer;

public class LayerDetailPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JTextField nameField = new JTextField();

    private final JLabel typeValue = new JLabel("-");

    private final JSpinner xSpinner =
        new JSpinner(new SpinnerNumberModel(0, -10000, 10000, 1));

    private final JSpinner ySpinner =
        new JSpinner(new SpinnerNumberModel(0, -10000, 10000, 1));

    private final JSpinner widthSpinner =
        new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));

    private final JSpinner heightSpinner =
        new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));

    private final JCheckBox visibleCheckBox =
        new JCheckBox();

    private Layer layer;

    /*
     * Permet d'éviter que les listeners soient déclenchés
     * pendant un refresh().
     */
    private boolean updating = false;

    public LayerDetailPanel() {

        setLayout(new BorderLayout());

        var content = new JPanel();

        content.setLayout(
            new BoxLayout(content, BoxLayout.Y_AXIS)
        );

        content.setBorder(
            BorderFactory.createEmptyBorder(
                10, 10, 10, 10
            )
        );

        /*
         * -------------------------------------------------------------
         * Layer
         * -------------------------------------------------------------
         */

        content.add(createSectionTitle("Layer"));

        content.add(
            createProperty("Name", nameField)
        );

        content.add(
            createProperty("Type", typeValue)
        );

        content.add(Box.createVerticalStrut(10));

        /*
         * -------------------------------------------------------------
         * Transform
         * -------------------------------------------------------------
         */

        content.add(createSectionTitle("Transform"));

        content.add(
            createProperty("X", xSpinner)
        );

        content.add(
            createProperty("Y", ySpinner)
        );

        content.add(
            createProperty("Width", widthSpinner)
        );

        content.add(
            createProperty("Height", heightSpinner)
        );

        content.add(Box.createVerticalStrut(10));

        /*
         * -------------------------------------------------------------
         * Properties
         * -------------------------------------------------------------
         */

        content.add(createSectionTitle("Properties"));

        JPanel visiblePanel =
            new JPanel(new BorderLayout(5, 0));

        visiblePanel.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        JLabel visibleLabel =
            new JLabel("Visible");

        visiblePanel.add(
            visibleLabel,
            BorderLayout.WEST
        );

        visiblePanel.add(
            visibleCheckBox,
            BorderLayout.EAST
        );

        content.add(visiblePanel);

        content.add(Box.createVerticalStrut(10));

        /*
         * -------------------------------------------------------------
         * Source
         * -------------------------------------------------------------
         */

        content.add(createSectionTitle("Source"));

        var scrollPane =
            new JScrollPane(content);

        scrollPane.setBorder(null);

        add(
            scrollPane,
            BorderLayout.CENTER
        );

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

        /*
         * Name
         */

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

        /*
         * Position / dimensions
         */

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

        /*
         * Visibility
         */

        visibleCheckBox.addActionListener(
            _ -> updateVisibility()
        );
    }

    // =================================================================
    // Editing
    // =================================================================

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

    // =================================================================
    // Layer notification
    // =================================================================

    private void notifyLayerChanged() {
        repaint();
    }

    // =================================================================
    // Layer
    // =================================================================

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

            /*
             * Identity
             */

            nameField.setText(
                layer.getName()
            );

            typeValue.setText(
                layer.getClass().getSimpleName()
            );

            /*
             * Transform
             */

            xSpinner.setValue(
                layer.getX()
            );

            ySpinner.setValue(
                layer.getY()
            );

            widthSpinner.setValue(
                layer.getWidth()
            );

            heightSpinner.setValue(
                layer.getHeight()
            );

            /*
             * Properties
             */

            visibleCheckBox.setSelected(
                layer.isVisible()
            );

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
            widthSpinner.setValue(0);
            heightSpinner.setValue(0);

            visibleCheckBox.setSelected(false);

        } finally {

            updating = false;
        }
    }

}