package org.magic.composer.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Dialog allowing the user to configure the RenderingHints
 * used by a Graphics2D renderer.
 */
public class RenderingHintsDialog extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final Map<RenderingHints.Key, JComboBox<Option>> controls =
            new LinkedHashMap<>();

    private RenderingHints result;

    public RenderingHintsDialog(Window owner, RenderingHints initialHints) {
        super(owner, "Rendering Hints", ModalityType.APPLICATION_MODAL);

        buildUI(initialHints);

        setMinimumSize(new Dimension(520, 500));
        setSize(620, 650);
        setLocationRelativeTo(owner);
    }

    private void buildUI(RenderingHints initialHints) {

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel hintsPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Rendering
        row = addHint(
                hintsPanel,
                gbc,
                row,
                "Rendering",
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_DEFAULT,
                RenderingHints.VALUE_RENDER_SPEED,
                RenderingHints.VALUE_RENDER_QUALITY
        );

        // Antialiasing
        row = addHint(
                hintsPanel,
                gbc,
                row,
                "Antialiasing",
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_DEFAULT,
                RenderingHints.VALUE_ANTIALIAS_OFF,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Text antialiasing
        row = addHint(
                hintsPanel,
                gbc,
                row,
                "Text Antialiasing",
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT,
                RenderingHints.VALUE_TEXT_ANTIALIAS_OFF,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR
        );

        // Fractional metrics
        row = addHint(
                hintsPanel,
                gbc,
                row,
                "Fractional Metrics",
                RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT,
                RenderingHints.VALUE_FRACTIONALMETRICS_OFF,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON
        );

        // Dithering
        row = addHint(
                hintsPanel,
                gbc,
                row,
                "Dithering",
                RenderingHints.KEY_DITHERING,
                RenderingHints.VALUE_DITHER_DEFAULT,
                RenderingHints.VALUE_DITHER_DISABLE,
                RenderingHints.VALUE_DITHER_ENABLE
        );

        // Alpha interpolation
        row = addHint(
                hintsPanel,
                gbc,
                row,
                "Alpha Interpolation",
                RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY
        );

        // Color rendering
        row = addHint(
                hintsPanel,
                gbc,
                row,
                "Color Rendering",
                RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_DEFAULT,
                RenderingHints.VALUE_COLOR_RENDER_SPEED,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY
        );

        // Stroke control
        row = addHint(
                hintsPanel,
                gbc,
                row,
                "Stroke Control",
                RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_DEFAULT,
                RenderingHints.VALUE_STROKE_NORMALIZE,
                RenderingHints.VALUE_STROKE_PURE
        );

        // Interpolation
        row = addHint(
                hintsPanel,
                gbc,
                row,
                "Interpolation",
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );

        // Normalize
        row = addHint(
                hintsPanel,
                gbc,
                row,
                "Resolution Variant",
                RenderingHints.KEY_RESOLUTION_VARIANT,
                RenderingHints.VALUE_RESOLUTION_VARIANT_DEFAULT,
                RenderingHints.VALUE_RESOLUTION_VARIANT_BASE,
                RenderingHints.VALUE_RESOLUTION_VARIANT_SIZE_FIT,
                RenderingHints.VALUE_RESOLUTION_VARIANT_DPI_FIT
        );

        // Spacer
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        hintsPanel.add(Box.createVerticalGlue(), gbc);

        mainPanel.add(new JScrollPane(hintsPanel), BorderLayout.CENTER);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton resetButton = new JButton("Reset");
        JButton cancelButton = new JButton("Cancel");
        JButton okButton = new JButton("Apply");

        resetButton.addActionListener(e -> reset());

        cancelButton.addActionListener(e -> {
            result = null;
            dispose();
        });

        okButton.addActionListener(_ -> {
            result = createRenderingHints();
            dispose();
        });

        buttons.add(resetButton);
        buttons.add(cancelButton);
        buttons.add(okButton);

        mainPanel.add(buttons, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        applyInitialHints(initialHints);
    }

    private int addHint(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            RenderingHints.Key key,
            Object... values) {

        gbc.gridwidth = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = row;

        panel.add(new JLabel(label + ":"), gbc);

        JComboBox<Option> combo = new JComboBox<>();

        for (Object value : values) {
            combo.addItem(new Option(value));
        }

        controls.put(key, combo);

        gbc.gridx = 1;
        gbc.weightx = 1.0;

        panel.add(combo, gbc);

        return row + 1;
    }

    private void applyInitialHints(RenderingHints hints) {

        if (hints == null) {
            reset();
            return;
        }

        controls.forEach((key, combo) -> {

            Object value = hints.get(key);

            if (value == null) {
                return;
            }

            for (int i = 0; i < combo.getItemCount(); i++) {

                Option option = combo.getItemAt(i);

                if (option.value().equals(value)) {
                    combo.setSelectedIndex(i);
                    break;
                }
            }
        });
    }

    private RenderingHints createRenderingHints() {

        RenderingHints hints = new RenderingHints(null);

        controls.forEach((key, combo) -> {

            Option option = (Option) combo.getSelectedItem();

            if (option != null) {
                hints.put(key, option.value());
            }
        });

        return hints;
    }

    private void reset() {

        controls.forEach((key, combo) -> {

            if (combo.getItemCount() > 0) {
                combo.setSelectedIndex(0);
            }
        });
    }

    /**
     * Returns the configured RenderingHints.
     * Returns null when the dialog was cancelled.
     */
    public RenderingHints getRenderingHints() {
        return result;
    }

    /**
     * Small wrapper used to provide readable values in JComboBox.
     */
    private record Option(Object value) {

        @Override
        public String toString() {
            	return String.valueOf(value);
        }
    }
}
