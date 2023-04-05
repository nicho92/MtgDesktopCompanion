package org.magic.gui.components.renderer;

import java.awt.BorderLayout;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.magic.api.beans.JsonMessage;
import org.magic.services.MTGControler;
import org.magic.services.tools.ImageTools;
import org.ocpsoft.prettytime.PrettyTime;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.FlowLayout;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class JsonMessagePanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel lblTime;
	private JTextArea textArea;

	
	
	public JsonMessagePanel(JsonMessage value) {
		setBorder(new LineBorder(value.getColor(),2,true));
		
		int iconSize=25;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{10, 42, 436, 0};
		gridBagLayout.rowHeights = new int[]{24, 71, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
	
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 0, 5, 5);
		gbc.gridx = 1;
		gbc.gridy = 0;
		JLabel label_1 = new JLabel(new ImageIcon(ImageTools.resize(value.getAuthor().getAvatar(), iconSize, iconSize)));
		add(label_1, gbc);
		
		
	
		
		var separator = new JPanel();
		separator.setBackground(value.getColor());
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.gridheight = 3;
		gbc_separator.anchor = GridBagConstraints.WEST;
		gbc_separator.fill = GridBagConstraints.VERTICAL;
		gbc_separator.insets = new Insets(0, 0, 0, 5);
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 0;
		add(separator, gbc_separator);
		GridBagConstraints gbc_1 = new GridBagConstraints();
		gbc_1.anchor = GridBagConstraints.WEST;
		gbc_1.insets = new Insets(0, 0, 5, 0);
		gbc_1.gridx = 2;
		gbc_1.gridy = 0;
		JLabel label = new JLabel(value.getAuthor().getName());
		add(label, gbc_1);
		
		
		
		textArea = new JTextArea(value.getMessage());
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.anchor = GridBagConstraints.NORTH;
		gbc_textArea.gridwidth = 2;
		gbc_textArea.fill = GridBagConstraints.HORIZONTAL;
		gbc_textArea.insets = new Insets(0, 0, 5, 0);
		gbc_textArea.gridx = 1;
		gbc_textArea.gridy = 1;
		add(textArea, gbc_textArea);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setFont(MTGControler.getInstance().getFont());
		textArea.setOpaque(false);
		
		lblTime = new JLabel("("+new PrettyTime(MTGControler.getInstance().getLocale()).format(new Date(value.getTimeStamp()))+")",SwingConstants.RIGHT);
		lblTime.setFont(MTGControler.getInstance().getFont().deriveFont(Font.ITALIC));
		GridBagConstraints gbc_lblTime = new GridBagConstraints();
		gbc_lblTime.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblTime.gridwidth = 2;
		gbc_lblTime.anchor = GridBagConstraints.NORTH;
		gbc_lblTime.gridx = 1;
		gbc_lblTime.gridy = 2;
		add(lblTime, gbc_lblTime);
	}
	
}
