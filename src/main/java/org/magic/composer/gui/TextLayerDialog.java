package org.magic.composer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

import org.magic.composer.gui.listeners.SimpleDocumentListener;
import org.magic.composer.layer.TextLayer;

public class TextLayerDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JTextArea textArea = new JTextArea();

	private final JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(24, 1, 500, 1));

	private final JPanel previewPanel = new JPanel(new BorderLayout());

	private boolean confirmed = false;

	private final TextLayer layer;

	private float size;

	public TextLayerDialog(Window owner, TextLayer layer) {
		super(owner, "Text Layer", ModalityType.APPLICATION_MODAL);
		this.layer = layer;

		initialize();
		textArea.setText(layer.getText());

		sizeSpinner.setValue(layer.getFont().getSize());
		updatePreview();
		
		pack();
		setMinimumSize(new Dimension(500, 550));
		setLocationRelativeTo(owner);
	}

	private void initialize() {

		getContentPane().setLayout(new BorderLayout(10, 10));

		var content = new JPanel();
		var buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		var cancelButton = new JButton("Cancel");
		var okButton = new JButton("OK");
		var btnColor = new JButton("Color");
		
		
		
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setRows(4);

		content.add(new JScrollPane(textArea));
		content.add(Box.createVerticalStrut(10));
		content.add(sizeSpinner);
		content.add(Box.createVerticalStrut(10));
		content.add(btnColor);
		content.add(Box.createVerticalStrut(10));
		
		previewPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		previewPanel.setPreferredSize(new Dimension(450, 150));
		content.add(previewPanel);

		getContentPane().add(content, BorderLayout.CENTER);

	
		cancelButton.addActionListener(_ -> cancel());
		okButton.addActionListener(_ -> confirm());
		btnColor.addActionListener(_->{
		    
		    Color color = JColorChooser.showDialog(this, "Text Color", layer.getColor());
		    
		    layer.setColor(color);
		    
		    updatePreview();
		    
		});
		buttons.add(cancelButton);
		buttons.add(okButton);

		getContentPane().add(buttons, BorderLayout.SOUTH);

		textArea.getDocument().addDocumentListener(new SimpleDocumentListener(this::updatePreview));
		sizeSpinner.addChangeListener(_ -> updatePreview());
	}

	private void updatePreview() {

		size = (Integer) sizeSpinner.getValue();
		layer.setFontSize(size);
		var preview = new JLabel("<html>" + escapeHtml(textArea.getText()).replace("\n", "<br>") + "</html>");
		preview.setFont(layer.getFont());
		preview.setForeground(layer.getColor());
		preview.setHorizontalAlignment(layer.getRole().getAlignement());

		previewPanel.removeAll();

		previewPanel.add(preview, BorderLayout.CENTER);

		previewPanel.revalidate();
		previewPanel.repaint();
	}

	private void confirm() {

		layer.setText(textArea.getText());
		layer.setFontSize( (float) size);
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

	private String escapeHtml(String text) {

		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
}