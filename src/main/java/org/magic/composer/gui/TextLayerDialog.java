package org.magic.composer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import org.magic.composer.gui.listeners.SimpleDocumentListener;
import org.magic.composer.layer.TextLayer;

public class TextLayerDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JTextArea textArea = new JTextArea();

	private final JComboBox<String> styleComboBox = new JComboBox<>(
			new String[] { "Normal", "Bold", "Italic", "Bold Italic" });

	private final JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(24, 1, 500, 1));

	private final JComboBox<String> alignmentComboBox = new JComboBox<>(new String[] { "Left", "Center", "Right" });

	private final JButton colorButton = new JButton(" ");

	private final JLabel fontLabel = new JLabel("-");

	private final JPanel previewPanel = new JPanel(new BorderLayout());

	private Color textColor = Color.BLACK;

	private Font selectedFont;

	private boolean confirmed = false;

	private final TextLayer layer;

	public TextLayerDialog(Window owner, TextLayer layer) {
		super(owner, "Text Layer", ModalityType.APPLICATION_MODAL);
		this.layer = layer;

		initialize();
		loadLayer();
		pack();
		setMinimumSize(new Dimension(500, 550));
		setLocationRelativeTo(owner);
	}

	private void initialize() {

		setLayout(new BorderLayout(10, 10));

		JPanel content = new JPanel();

		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		/*
		 * Text
		 */

		content.add(createLabel("Text"));

		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);

		textArea.setRows(4);

		content.add(new JScrollPane(textArea));

		content.add(Box.createVerticalStrut(10));

		/*
		 * Font
		 */

		content.add(createLabel("Font"));

		JPanel fontPanel = new JPanel(new BorderLayout(5, 0));

		fontPanel.add(fontLabel, BorderLayout.CENTER);

		JButton chooseFontButton = new JButton("Choose...");

		chooseFontButton.addActionListener(this::chooseFont);

		fontPanel.add(chooseFontButton, BorderLayout.EAST);

		content.add(fontPanel);

		content.add(Box.createVerticalStrut(10));

		/*
		 * Size
		 */

		content.add(createLabel("Size"));

		content.add(sizeSpinner);

		content.add(Box.createVerticalStrut(10));

		/*
		 * Style
		 */

		content.add(createLabel("Style"));

		content.add(styleComboBox);

		content.add(Box.createVerticalStrut(10));

		/*
		 * Alignment
		 */

		content.add(createLabel("Alignment"));

		content.add(alignmentComboBox);

		content.add(Box.createVerticalStrut(10));

		/*
		 * Color
		 */

		content.add(createLabel("Color"));

		colorButton.setPreferredSize(new Dimension(60, 30));

		colorButton.addActionListener(_ -> chooseColor());

		content.add(colorButton);

		content.add(Box.createVerticalStrut(15));

		/*
		 * Preview
		 */

		content.add(createLabel("Preview"));

		previewPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		previewPanel.setPreferredSize(new Dimension(450, 150));

		content.add(previewPanel);

		add(content, BorderLayout.CENTER);

		/*
		 * Buttons
		 */

		var buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		var cancelButton = new JButton("Cancel");

		var okButton = new JButton("OK");

		cancelButton.addActionListener(_ -> cancel());

		okButton.addActionListener(_ -> confirm());

		buttons.add(cancelButton);
		buttons.add(okButton);

		add(buttons, BorderLayout.SOUTH);

		/*
		 * Live preview
		 */

		textArea.getDocument().addDocumentListener(new SimpleDocumentListener(this::updatePreview));

		sizeSpinner.addChangeListener(_ -> updatePreview());

		styleComboBox.addActionListener(_ -> updatePreview());

		alignmentComboBox.addActionListener(_ -> updatePreview());
	}

	private JLabel createLabel(String text) {

		JLabel label = new JLabel(text);

		label.setFont(label.getFont().deriveFont(Font.BOLD));

		label.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));

		return label;
	}

	private void loadLayer() {

		textArea.setText(layer.getText());

		selectedFont = layer.getFont();

		if (selectedFont != null) {

			fontLabel.setText(selectedFont.getFontName());

			sizeSpinner.setValue(selectedFont.getSize());

			styleComboBox.setSelectedIndex(getStyleIndex(selectedFont.getStyle()));
		}

		textColor = layer.getColor();

		updateColorButton();

		alignmentComboBox.setSelectedIndex(getAlignmentIndex(layer.getAlignment()));

		updatePreview();
	}

	private void chooseFont(ActionEvent event) {

		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();

		String[] fonts = environment.getAvailableFontFamilyNames();

		String current = selectedFont != null ? selectedFont.getFamily() : fonts[0];

		String selected = (String) JOptionPane.showInputDialog(this, "Select font:", "Font", JOptionPane.PLAIN_MESSAGE,	null, fonts, current);

		if (selected == null) {
			return;
		}

		int style = getSelectedStyle();

		int size = (Integer) sizeSpinner.getValue();

		selectedFont = new Font(selected, style, size);

		fontLabel.setText(selectedFont.getFontName());

		updatePreview();
	}

	private void chooseColor() {

		Color color = JColorChooser.showDialog(this, "Text Color", textColor);

		if (color == null) {
			return;
		}

		textColor = color;

		updateColorButton();

		updatePreview();
	}

	private void updateColorButton() {

		colorButton.setBackground(textColor);

		colorButton.setOpaque(true);
	}

	private void updatePreview() {

		if (selectedFont == null) {
			return;
		}

		int size = (Integer) sizeSpinner.getValue();

		int style = getSelectedStyle();

		selectedFont = selectedFont.deriveFont(style, (float) size);

		var preview = new JLabel("<html>" + escapeHtml(textArea.getText()).replace("\n", "<br>") + "</html>");

		preview.setFont(selectedFont);
		preview.setForeground(textColor);

		preview.setHorizontalAlignment(getSelectedAlignment());

		previewPanel.removeAll();

		previewPanel.add(preview, BorderLayout.CENTER);

		previewPanel.revalidate();
		previewPanel.repaint();
	}

	private void confirm() {

		layer.setText(textArea.getText());
		layer.setFont(selectedFont);
		layer.setColor(textColor);
		layer.setAlignment(getSelectedAlignment());

		confirmed = true;

		dispose();
	}

	private void cancel() {

		confirmed = false;

		dispose();
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	private int getSelectedStyle() {

		return switch (styleComboBox.getSelectedIndex()) {
		case 1 -> Font.BOLD;
		case 2 -> Font.ITALIC;
		case 3 -> Font.BOLD | Font.ITALIC;
		default -> Font.PLAIN;
		};
	}

	private int getStyleIndex(int style) {

		return switch (style) {
		case Font.BOLD -> 1;
		case Font.ITALIC -> 2;
		case Font.BOLD | Font.ITALIC -> 3;
		default -> 0;
		};
	}

	private int getSelectedAlignment() {

		return switch (alignmentComboBox.getSelectedIndex()) {
		case 1 -> SwingConstants.CENTER;
		case 2 -> SwingConstants.RIGHT;
		default -> SwingConstants.LEFT;
		};
	}

	private int getAlignmentIndex(int alignment) {

		return switch (alignment) {
		case SwingConstants.CENTER -> 1;
		case SwingConstants.RIGHT -> 2;
		default -> 0;
		};
	}

	private String escapeHtml(String text) {

		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
}