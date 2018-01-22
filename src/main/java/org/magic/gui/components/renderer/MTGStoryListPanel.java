package org.magic.gui.components.renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.magic.api.beans.MTGStory;

public class MTGStoryListPanel extends JPanel {

	JLabel lblicon;
	
	public MTGStoryListPanel(MTGStory value) {
		setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{91, 55, 73, 0};
		gridBagLayout.rowHeights = new int[]{14, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		lblicon = new JLabel(new ImageIcon(value.getIcon()));
		GridBagConstraints gbc_lblicon = new GridBagConstraints();
		gbc_lblicon.gridheight = 3;
		gbc_lblicon.insets = new Insets(0, 0, 0, 5);
		gbc_lblicon.gridx = 0;
		gbc_lblicon.gridy = 0;
		add(lblicon, gbc_lblicon);
		
		JLabel lblTitle = new JLabel(value.getTitle());
		lblTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_lblTitle = new GridBagConstraints();
		gbc_lblTitle.insets = new Insets(0, 0, 5, 5);
		gbc_lblTitle.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblTitle.gridx = 1;
		gbc_lblTitle.gridy = 0;
		add(lblTitle, gbc_lblTitle);
		
		JLabel lblDate = new JLabel(value.getDate());
		lblDate.setFont(new Font("Tahoma", Font.PLAIN, 11));
		GridBagConstraints gbc_lblDate = new GridBagConstraints();
		gbc_lblDate.anchor = GridBagConstraints.NORTH;
		gbc_lblDate.insets = new Insets(0, 0, 5, 0);
		gbc_lblDate.gridx = 2;
		gbc_lblDate.gridy = 0;
		add(lblDate, gbc_lblDate);
		
		JLabel lblAuthor = new JLabel(value.getAuthor());
		lblAuthor.setFont(new Font("Tahoma", Font.ITALIC, 11));
		GridBagConstraints gbc_lblAuthor = new GridBagConstraints();
		gbc_lblAuthor.anchor = GridBagConstraints.WEST;
		gbc_lblAuthor.insets = new Insets(0, 0, 5, 5);
		gbc_lblAuthor.gridx = 1;
		gbc_lblAuthor.gridy = 1;
		add(lblAuthor, gbc_lblAuthor);
		
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setOpaque(false);
		editorPane.setText(value.getDescription());
		GridBagConstraints gbc_editorPane = new GridBagConstraints();
		gbc_editorPane.gridwidth = 2;
		gbc_editorPane.insets = new Insets(0, 0, 0, 5);
		gbc_editorPane.fill = GridBagConstraints.BOTH;
		gbc_editorPane.gridx = 1;
		gbc_editorPane.gridy = 2;
		add(editorPane, gbc_editorPane);
	

	}

	
	
	
}
