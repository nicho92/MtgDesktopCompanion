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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel lblicon;
	private String useFonts = "Tahoma";

	public MTGStoryListPanel(MTGStory value) {
		setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 91, 55, 73, 0 };
		gridBagLayout.rowHeights = new int[] { 14, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		lblicon = new JLabel(new ImageIcon(value.getIcon()));
		GridBagConstraints gbclblicon = new GridBagConstraints();
		gbclblicon.gridheight = 3;
		gbclblicon.insets = new Insets(0, 0, 0, 5);
		gbclblicon.gridx = 0;
		gbclblicon.gridy = 0;
		add(lblicon, gbclblicon);

		JLabel lblTitle = new JLabel(value.getTitle());
		lblTitle.setFont(new Font(useFonts, Font.BOLD, 14));
		GridBagConstraints gbclblTitle = new GridBagConstraints();
		gbclblTitle.insets = new Insets(0, 0, 5, 5);
		gbclblTitle.anchor = GridBagConstraints.NORTHWEST;
		gbclblTitle.gridx = 1;
		gbclblTitle.gridy = 0;
		add(lblTitle, gbclblTitle);

		JLabel lblDate = new JLabel(value.getDate());
		lblDate.setFont(new Font(useFonts, Font.PLAIN, 11));
		GridBagConstraints gbclblDate = new GridBagConstraints();
		gbclblDate.anchor = GridBagConstraints.NORTH;
		gbclblDate.insets = new Insets(0, 0, 5, 0);
		gbclblDate.gridx = 2;
		gbclblDate.gridy = 0;
		add(lblDate, gbclblDate);

		JLabel lblAuthor = new JLabel(value.getAuthor());
		lblAuthor.setFont(new Font(useFonts, Font.ITALIC, 11));
		GridBagConstraints gbclblAuthor = new GridBagConstraints();
		gbclblAuthor.anchor = GridBagConstraints.WEST;
		gbclblAuthor.insets = new Insets(0, 0, 5, 5);
		gbclblAuthor.gridx = 1;
		gbclblAuthor.gridy = 1;
		add(lblAuthor, gbclblAuthor);

		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setOpaque(false);
		editorPane.setText(value.getDescription());
		GridBagConstraints gbceditorPane = new GridBagConstraints();
		gbceditorPane.gridwidth = 2;
		gbceditorPane.insets = new Insets(0, 0, 0, 5);
		gbceditorPane.fill = GridBagConstraints.BOTH;
		gbceditorPane.gridx = 1;
		gbceditorPane.gridy = 2;
		add(editorPane, gbceditorPane);

	}

}
