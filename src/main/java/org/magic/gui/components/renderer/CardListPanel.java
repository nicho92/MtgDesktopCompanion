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
import org.magic.gui.components.ManaPanel;
import org.magic.services.extra.IconSetProvider;

public class CardListPanel extends JPanel {

	private JLabel lblName;
	private JLabel lblType;
	private JLabel lblEdition;
	private ManaPanel manaPanel;
	private JLabel lblRarity;

	public CardListPanel() {

		initGUI();

	}

	private void initGUI() {
		setBorder(new LineBorder(new Color(0, 0, 0), 1, true));

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 137, 129, 0 };
		gridBagLayout.rowHeights = new int[] { 12, 12, 12, 12, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		lblName = new JLabel();
		lblName.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbclblName = new GridBagConstraints();
		gbclblName.fill = GridBagConstraints.VERTICAL;
		gbclblName.insets = new Insets(0, 0, 5, 5);
		gbclblName.anchor = GridBagConstraints.WEST;
		gbclblName.gridx = 1;
		gbclblName.gridy = 0;
		add(lblName, gbclblName);

		lblType = new JLabel();
		GridBagConstraints gbclblType = new GridBagConstraints();
		gbclblType.fill = GridBagConstraints.VERTICAL;
		gbclblType.gridwidth = 2;
		gbclblType.insets = new Insets(0, 0, 5, 0);
		gbclblType.anchor = GridBagConstraints.WEST;
		gbclblType.gridx = 1;
		gbclblType.gridy = 1;
		add(lblType, gbclblType);

		lblEdition = new JLabel();
		GridBagConstraints gbclblEdition = new GridBagConstraints();
		gbclblEdition.fill = GridBagConstraints.VERTICAL;
		gbclblEdition.insets = new Insets(0, 0, 5, 0);
		gbclblEdition.gridwidth = 2;
		gbclblEdition.anchor = GridBagConstraints.WEST;
		gbclblEdition.gridx = 1;
		gbclblEdition.gridy = 2;
		add(lblEdition, gbclblEdition);

		manaPanel = new ManaPanel();
		manaPanel.setBackground(this.getBackground());
		GridBagConstraints gbcmanaPanel = new GridBagConstraints();
		gbcmanaPanel.insets = new Insets(0, 0, 5, 0);
		gbcmanaPanel.anchor = GridBagConstraints.EAST;
		gbcmanaPanel.fill = GridBagConstraints.VERTICAL;
		gbcmanaPanel.gridx = 2;
		gbcmanaPanel.gridy = 0;
		add(manaPanel, gbcmanaPanel);

		lblRarity = new JLabel();
		lblRarity.setFont(new Font("Tahoma", Font.ITALIC, 11));
		GridBagConstraints gbclblRarity = new GridBagConstraints();
		gbclblRarity.fill = GridBagConstraints.VERTICAL;
		gbclblRarity.gridwidth = 2;
		gbclblRarity.anchor = GridBagConstraints.WEST;
		gbclblRarity.insets = new Insets(0, 0, 0, 5);
		gbclblRarity.gridx = 1;
		gbclblRarity.gridy = 3;
		add(lblRarity, gbclblRarity);
	}

	public void setMagicCard(MagicCard mc) {
		lblName.setText(mc.getName());
		lblType.setText(mc.getFullType());
		lblEdition.setText(mc.getCurrentSet().toString());
		lblEdition.setIcon(IconSetProvider.getInstance().get24(mc.getCurrentSet().getId()));
		lblRarity.setText(mc.getCurrentSet().getRarity());
		manaPanel.setManaCost(mc.getCost());
	}
}
