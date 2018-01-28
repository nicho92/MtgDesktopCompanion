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
		
		JLabel lblName = new JLabel(mc.getName());
		lblName.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbclblName = new GridBagConstraints();
		gbclblName.insets = new Insets(0, 0, 5, 5);
		gbclblName.anchor = GridBagConstraints.NORTHWEST;
		gbclblName.gridx = 1;
		gbclblName.gridy = 0;
		add(lblName, gbclblName);
		
		JLabel lblType = new JLabel(mc.getFullType());
		GridBagConstraints gbclblType = new GridBagConstraints();
		gbclblType.gridwidth = 2;
		gbclblType.insets = new Insets(0, 0, 5, 0);
		gbclblType.anchor = GridBagConstraints.WEST;
		gbclblType.gridx = 1;
		gbclblType.gridy = 1;
		add(lblType, gbclblType);
		
		JLabel lblEdition = new JLabel(mc.getEditions().get(0).toString());
		GridBagConstraints gbclblEdition = new GridBagConstraints();
		gbclblEdition.insets = new Insets(0, 0, 5, 0);
		gbclblEdition.gridwidth = 2;
		gbclblEdition.anchor = GridBagConstraints.WEST;
		gbclblEdition.gridx = 1;
		gbclblEdition.gridy = 2;
		add(lblEdition, gbclblEdition);
		
		lblEdition.setIcon(IconSetProvider.getInstance().get24(mc.getEditions().get(0).getId()));
	
	
		
		ManaPanel manaPanel = new ManaPanel();
		manaPanel.setBackground(Color.WHITE);
		manaPanel.setManaCost(mc.getCost());
		GridBagConstraints gbcmanaPanel = new GridBagConstraints();
		gbcmanaPanel.insets = new Insets(0, 0, 5, 0);
		gbcmanaPanel.anchor = GridBagConstraints.EAST;
		gbcmanaPanel.fill = GridBagConstraints.VERTICAL;
		gbcmanaPanel.gridx = 2;
		gbcmanaPanel.gridy = 0;
		add(manaPanel, gbcmanaPanel);
		
		JLabel lblRarity = new JLabel(mc.getEditions().get(0).getRarity());
		lblRarity.setFont(new Font("Tahoma", Font.ITALIC, 11));
		GridBagConstraints gbclblRarity = new GridBagConstraints();
		gbclblRarity.gridwidth = 2;
		gbclblRarity.anchor = GridBagConstraints.WEST;
		gbclblRarity.insets = new Insets(0, 0, 0, 5);
		gbclblRarity.gridx = 1;
		gbclblRarity.gridy = 3;
		add(lblRarity, gbclblRarity);
	}	
	
	
}
