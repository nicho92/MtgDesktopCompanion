package org.magic.composer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.magic.composer.gui.listeners.LayerChangeListener;
import org.magic.composer.layer.BorderLayer;
import org.magic.composer.layer.FrameLayer;
import org.magic.composer.layer.IllustrationLayer;
import org.magic.composer.layer.Layer;
import org.magic.composer.layer.ManaLayer;
import org.magic.composer.layer.TextLayer;

/** Displays common layer properties and the properties specific to its concrete type. */
public class LayerDetailPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int MAX_DIMENSION = 10000;

    private final JTextField nameField = new JTextField();
    private final JLabel typeValue = new JLabel("-");
    private final JSpinner xSpinner = integerSpinner(0, 0, MAX_DIMENSION);
    private final JSpinner ySpinner = integerSpinner(0, 0, MAX_DIMENSION);
    private final JSpinner widthSpinner = integerSpinner(1, 1, MAX_DIMENSION);
    private final JSpinner heightSpinner = integerSpinner(1, 1, MAX_DIMENSION);
    private final JSpinner scaleSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 10.0, 0.1));
    private final JCheckBox visibleCheckBox = new JCheckBox("Visible");
    private final JPanel propertiesPanel = new JPanel();

    private Layer layer;
    private LayerChangeListener layerChangeListener;
    private boolean updating;
    private double previousScale = 1.0;

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
        content.add(createProperty("Scale", scaleSpinner));
        content.add(Box.createVerticalStrut(10));
        content.add(createSectionTitle("Properties"));
        visibleCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(visibleCheckBox);
        propertiesPanel.setLayout(new BoxLayout(propertiesPanel, BoxLayout.Y_AXIS));
        propertiesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(propertiesPanel);

        var scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
        installCommonListeners();
        clear();
    }

    private static JSpinner integerSpinner(int value, int minimum, int maximum) {
        return new JSpinner(new SpinnerNumberModel(value, minimum, maximum, 1));
    }

    private JLabel createSectionTitle(String title) {
        var label = new JLabel(title);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createProperty(String name, Component value) {
        var panel = new JPanel(new BorderLayout(10, 0));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        var nameLabel = new JLabel(name);
        nameLabel.setPreferredSize(new Dimension(70, 20));
        panel.add(nameLabel, BorderLayout.WEST);
        panel.add(value, BorderLayout.CENTER);
        return panel;
    }

    private void installCommonListeners() {
        nameField.getDocument().addDocumentListener(documentListener(this::updateName));
        xSpinner.addChangeListener(_ -> update(() -> layer.setX((Integer) xSpinner.getValue())));
        ySpinner.addChangeListener(_ -> update(() -> layer.setY((Integer) ySpinner.getValue())));
        widthSpinner.addChangeListener(_ -> update(() -> layer.setWidth((Integer) widthSpinner.getValue())));
        heightSpinner.addChangeListener(_ -> update(() -> layer.setHeight((Integer) heightSpinner.getValue())));
        visibleCheckBox.addActionListener(_ -> update(() -> layer.setVisible(visibleCheckBox.isSelected())));
        scaleSpinner.addChangeListener(_ -> updateScale());
    }

    private DocumentListener documentListener(Runnable action) {
        return new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent event) { action.run(); }
            @Override public void removeUpdate(DocumentEvent event) { action.run(); }
            @Override public void changedUpdate(DocumentEvent event) { action.run(); }
        };
    }

    private void updateName() {
        update(() -> layer.setName(nameField.getText()));
    }

    private void updateScale() {
        update(() -> {
            double newScale = ((Number) scaleSpinner.getValue()).doubleValue();
            layer.scale(newScale / previousScale);
            previousScale = newScale;
        });
    }

    private void update(Runnable change) {
        if (updating || layer == null) {
            return;
        }
        change.run();
        notifyLayerChanged();
    }

    private void rebuildProperties() {
        propertiesPanel.removeAll();
        if (layer instanceof IllustrationLayer illustrationLayer) {
            addIllustrationProperties(illustrationLayer);
        } else if (layer instanceof ManaLayer manaLayer) {
            addManaProperties(manaLayer);
        } else if (layer instanceof TextLayer textLayer) {
            addTextProperties(textLayer);
        } else if (layer instanceof BorderLayer borderLayer) {
            addBorderProperties(borderLayer);
        } else if (layer instanceof FrameLayer frameLayer) {
            addFrameProperties(frameLayer);
        }
        propertiesPanel.revalidate();
        propertiesPanel.repaint();
    }

    private void addIllustrationProperties(IllustrationLayer illustrationLayer) {
        var urlField = new JTextField(illustrationLayer.getSource().toString());
        urlField.addActionListener(_ -> updateIllustrationSource(illustrationLayer, urlField.getText()));
        propertiesPanel.add(createProperty("URL", urlField));
    }

    private void updateIllustrationSource(IllustrationLayer illustrationLayer, String value) {
        try {
            URL source = new URL(value);
            update(() -> illustrationLayer.setSource(source));
        } catch (MalformedURLException exception) {
            // Keep the current image and restore its valid URL in the editor.
            refresh(illustrationLayer);
        }
    }

    private void addManaProperties(ManaLayer manaLayer) {
        var costField = new JTextField(manaLayer.getCost());
        costField.addActionListener(_ -> update(() -> manaLayer.setCost(costField.getText())));
        propertiesPanel.add(createProperty("Cost", costField));
    }

    private void addTextProperties(TextLayer textLayer) {
        var textArea = new JTextArea(textLayer.getText(), 4, 12);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.getDocument().addDocumentListener(documentListener(() -> update(() -> textLayer.setText(textArea.getText()))));
        propertiesPanel.add(createProperty("Text", new JScrollPane(textArea)));

        var fontSize = new JSpinner(new SpinnerNumberModel((double) textLayer.getFont().getSize2D(), 1.0, 500.0, 1.0));
        fontSize.addChangeListener(_ -> update(() -> textLayer.setFontSize(((Number) fontSize.getValue()).floatValue())));
        propertiesPanel.add(createProperty("Size", fontSize));
        propertiesPanel.add(colorProperty("Color", textLayer.getColor(), textLayer::setColor));
    }

    private void addBorderProperties(BorderLayer borderLayer) {
        propertiesPanel.add(colorProperty("Color", borderLayer.getColor(), borderLayer::setColor));
        var radius = new JSpinner(new SpinnerNumberModel(borderLayer.getRadius(), 0.0, 10000.0, 1.0));
        radius.addChangeListener(_ -> update(() -> borderLayer.setRadius(((Number) radius.getValue()).doubleValue())));
        propertiesPanel.add(createProperty("Radius", radius));
        addDoubleProperty("Image scale", borderLayer.getImageScale(), 0.01, borderLayer::setImageScale);
        addDoubleProperty("Image X", borderLayer.getImageOffsetX(), -MAX_DIMENSION, borderLayer::setImageOffsetX);
        addDoubleProperty("Image Y", borderLayer.getImageOffsetY(), -MAX_DIMENSION, borderLayer::setImageOffsetY);
    }

    private void addDoubleProperty(String name, double value, double minimum, java.util.function.DoubleConsumer setter) {
        var spinner = new JSpinner(new SpinnerNumberModel(value, minimum, MAX_DIMENSION, 0.1));
        spinner.addChangeListener(_ -> update(() -> setter.accept(((Number) spinner.getValue()).doubleValue())));
        propertiesPanel.add(createProperty(name, spinner));
    }

    private JPanel colorProperty(String name, Color color, java.util.function.Consumer<Color> setter) {
        var button = new JButton("Choose…");
        button.setBackground(color);
        button.addActionListener(_ -> {
            Color selected = JColorChooser.showDialog(this, name, color);
            if (selected != null) {
                update(() -> setter.accept(selected));
                button.setBackground(selected);
            }
        });
        return createProperty(name, button);
    }

    private void addFrameProperties(FrameLayer frameLayer) {
        var sourceField = new JTextField(frameLayer.getSource().getPath());
        sourceField.setEditable(false);
        var browseButton = new JButton("Browse…");
        browseButton.addActionListener(_ -> chooseFrameSource(frameLayer));
        var sourcePanel = new JPanel(new BorderLayout(5, 0));
        sourcePanel.add(sourceField, BorderLayout.CENTER);
        sourcePanel.add(browseButton, BorderLayout.EAST);
        propertiesPanel.add(createProperty("File", sourcePanel));
    }

    private void chooseFrameSource(FrameLayer frameLayer) {
        var chooser = new JFileChooser(frameLayer.getSource().getParentFile());
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            update(() -> frameLayer.setSource(chooser.getSelectedFile()));
            refresh(frameLayer);
        }
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
        previousScale = 1.0;
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
            scaleSpinner.setValue(1.0);
            visibleCheckBox.setSelected(layer.isVisible());
            rebuildProperties();
        } finally {
            updating = false;
        }
    }

    public void clear() {
        updating = true;
        try {
            layer = null;
            nameField.setText("-");
            typeValue.setText("-");
            previousScale = 1.0;
            scaleSpinner.setValue(1.0);
            xSpinner.setValue(0);
            ySpinner.setValue(0);
            widthSpinner.setValue(1);
            heightSpinner.setValue(1);
            visibleCheckBox.setSelected(false);
            rebuildProperties();
        } finally {
            updating = false;
        }
    }
}
