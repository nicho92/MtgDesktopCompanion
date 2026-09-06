package org.beta.composer.gui;

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
import javax.swing.SwingConstants;

import org.beta.composer.layer.ImageLayer;
import org.beta.composer.layer.Layer;

public class LayerDetailPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final JLabel nameValue = new JLabel("-");
    private final JLabel typeValue = new JLabel("-");

    private final JLabel xValue = new JLabel("-");
    private final JLabel yValue = new JLabel("-");
    private final JLabel widthValue = new JLabel("-");
    private final JLabel heightValue = new JLabel("-");

    private final JCheckBox visibleCheckBox = new JCheckBox();

    private final JLabel sourceValue = new JLabel("-");

    private Layer layer;

    public LayerDetailPanel() {

        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.setBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        /*
         * Identity
         */
        content.add(createSectionTitle("Layer"));

        content.add(createProperty("Name", nameValue));
        content.add(createProperty("Type", typeValue));

        content.add(Box.createVerticalStrut(10));

        /*
         * Transform
         */
        content.add(createSectionTitle("Transform"));

        content.add(createProperty("X", xValue));
        content.add(createProperty("Y", yValue));
        content.add(createProperty("Width", widthValue));
        content.add(createProperty("Height", heightValue));

        content.add(Box.createVerticalStrut(10));

        /*
         * Layer properties
         */
        content.add(createSectionTitle("Properties"));

        JPanel visiblePanel = new JPanel(new BorderLayout(5, 0));
        visiblePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel visibleLabel = new JLabel("Visible");

        visiblePanel.add(visibleLabel, BorderLayout.WEST);
        visiblePanel.add(visibleCheckBox, BorderLayout.EAST);

        content.add(visiblePanel);

        content.add(Box.createVerticalStrut(10));

        /*
         * Source
         */
        content.add(createSectionTitle("Source"));

        sourceValue.setVerticalAlignment(SwingConstants.TOP);
        sourceValue.setToolTipText("");

        content.add(sourceValue);

        JScrollPane scrollPane = new JScrollPane(content);

        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);

        clear();
    }

    private JLabel createSectionTitle(String title) {

        JLabel label = new JLabel(title);

        label.setFont(
            label.getFont().deriveFont(Font.BOLD)
        );

        label.setBorder(
            BorderFactory.createEmptyBorder(5, 0, 5, 0)
        );

        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    private JPanel createProperty(
        String name,
        JLabel value
    ) {

        JPanel panel = new JPanel(new BorderLayout(10, 0));

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel(name);

        nameLabel.setPreferredSize(
            new Dimension(70, 20)
        );

        panel.add(nameLabel, BorderLayout.WEST);
        panel.add(value, BorderLayout.CENTER);

        return panel;
    }

    public Layer getLayer() {
        return layer;
    }

    public void refresh(Layer layer) {
	
	this.layer=layer;
	
        if (layer == null) {
            clear();
            return;
        }

        nameValue.setText(layer.getName());
        typeValue.setText(
            layer.getClass().getSimpleName()
        );

        xValue.setText(
            Integer.toString(layer.getX())
        );

        yValue.setText(
            Integer.toString(layer.getY())
        );

        widthValue.setText(
            Integer.toString(layer.getWidth())
        );

        heightValue.setText(
            Integer.toString(layer.getHeight())
        );

        visibleCheckBox.setSelected(
            layer.isVisible()
        );

        if (layer instanceof ImageLayer imageLayer) {

            sourceValue.setText(
                "<html>"
                + escapeHtml(
                    imageLayer.getSource().getAbsolutePath()
                )
                + "</html>"
            );

            sourceValue.setToolTipText(
                imageLayer.getSource().getAbsolutePath()
            );

        } else {

            sourceValue.setText("-");

            sourceValue.setToolTipText(null);
        }
    }

    public void clear() {

        nameValue.setText("-");
        typeValue.setText("-");

        xValue.setText("-");
        yValue.setText("-");
        widthValue.setText("-");
        heightValue.setText("-");

        visibleCheckBox.setSelected(false);

        sourceValue.setText("-");
        sourceValue.setToolTipText(null);
    }

    private String escapeHtml(String value) {

        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
    }
}