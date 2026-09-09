package org.magic.composer.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import org.magic.composer.layer.TextLayer;

/**
 * Inspector properties that are specific to a {@link TextLayer}.
 */
public class TextLayerDetailPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final float MIN_FONT_SIZE = 1.0f;
    private static final float MAX_FONT_SIZE = 500.0f;

    private final JSpinner fontSizeSpinner = new JSpinner(
        new SpinnerNumberModel(MIN_FONT_SIZE, MIN_FONT_SIZE, MAX_FONT_SIZE, 1.0f)
    );

    private TextLayer layer;
    private Runnable changeListener = () -> { };
    private boolean updating;

    public TextLayerDetailPanel() {
        setLayout(new BorderLayout(10, 0));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        var label = new JLabel("Font size");
        label.setPreferredSize(new Dimension(70, 20));
        add(label, BorderLayout.WEST);
        add(fontSizeSpinner, BorderLayout.CENTER);

        ChangeListener listener = _ -> updateFontSize();
        fontSizeSpinner.addChangeListener(listener);

        setVisible(false);
    }

    public void setChangeListener(Runnable changeListener) {
        this.changeListener = changeListener == null ? () -> { } : changeListener;
    }

    public void refresh(TextLayer textLayer) {
        updating = true;
        try {
            layer = textLayer;
            setVisible(textLayer != null);
            if (textLayer != null) {
                fontSizeSpinner.setValue(textLayer.getFont().getSize2D());
            }
        } finally {
            updating = false;
        }
    }

    private void updateFontSize() {
        if (updating || layer == null) {
            return;
        }

        layer.setFontSize(((Number) fontSizeSpinner.getValue()).floatValue());
        changeListener.run();
    }
}
