package org.magic.gui.components.renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.magic.api.beans.MagicCard;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.gui.components.ManaPanel;
import org.magic.services.IconSetProvider;

public class CardListPanel extends JPanel{
	
	MagicCard card;
	DisplayableCard lblNewLabel;
	
	
	public CardListPanel(MagicCard mc) {
		this.card=mc;
		setBackground(Color.WHITE);
		setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		
		
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 230, 106, 0};
		gridBagLayout.rowHeights = new int[]{14, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		/*
		lblNewLabel = new DisplayableCard(card,new Dimension(77, 107),false);
				GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
				gbc_lblNewLabel.gridheight = 4;
				gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
				gbc_lblNewLabel.gridx = 0;
				gbc_lblNewLabel.gridy = 0;
				add(lblNewLabel, gbc_lblNewLabel);
		*/
		JLabel lblName = new JLabel(mc.getName());
		lblName.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblName.gridx = 1;
		gbc_lblName.gridy = 0;
		add(lblName, gbc_lblName);
		
		JLabel lblType = new JLabel(mc.getFullType());
		GridBagConstraints gbc_lblType = new GridBagConstraints();
		gbc_lblType.gridwidth = 2;
		gbc_lblType.insets = new Insets(0, 0, 5, 0);
		gbc_lblType.anchor = GridBagConstraints.WEST;
		gbc_lblType.gridx = 1;
		gbc_lblType.gridy = 1;
		add(lblType, gbc_lblType);
		
		JLabel lblEdition = new JLabel(mc.getEditions().get(0).toString());
		GridBagConstraints gbc_lblEdition = new GridBagConstraints();
		gbc_lblEdition.insets = new Insets(0, 0, 5, 0);
		gbc_lblEdition.gridwidth = 2;
		gbc_lblEdition.anchor = GridBagConstraints.WEST;
		gbc_lblEdition.gridx = 1;
		gbc_lblEdition.gridy = 2;
		add(lblEdition, gbc_lblEdition);
		
		lblEdition.setIcon(IconSetProvider.getInstance().get24(mc.getEditions().get(0).getId()));
	
	
		
		ManaPanel manaPanel = new ManaPanel();
		manaPanel.setBackground(Color.WHITE);
		manaPanel.setManaCost(mc.getCost());
		GridBagConstraints gbc_manaPanel = new GridBagConstraints();
		gbc_manaPanel.insets = new Insets(0, 0, 5, 0);
		gbc_manaPanel.anchor = GridBagConstraints.EAST;
		gbc_manaPanel.fill = GridBagConstraints.VERTICAL;
		gbc_manaPanel.gridx = 2;
		gbc_manaPanel.gridy = 0;
		add(manaPanel, gbc_manaPanel);
	}	
	
	
}
