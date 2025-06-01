package org.magic.gui.components.renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.magic.api.beans.MTGCard;
import org.magic.gui.components.card.ManaPanel;
import org.magic.services.MTGControler;
import org.magic.services.providers.IconsProvider;
import org.magic.services.tools.UITools;

public class CardListPanel extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
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

		var gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 137, 129, 0 };
		gridBagLayout.rowHeights = new int[] { 12, 12, 12, 12, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);
		lblName = new JLabel();
		lblName.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD, 11));
		add(lblName, UITools.createGridBagConstraints(GridBagConstraints.WEST, GridBagConstraints.VERTICAL, 1, 0));

		lblType = new JLabel();
		add(lblType, UITools.createGridBagConstraints(GridBagConstraints.WEST, GridBagConstraints.VERTICAL, 1, 1));

		lblEdition = new JLabel();
		add(lblEdition, UITools.createGridBagConstraints(GridBagConstraints.WEST, GridBagConstraints.VERTICAL, 1, 2));

		manaPanel = new ManaPanel();
		manaPanel.setBackground(this.getBackground());
		add(manaPanel, UITools.createGridBagConstraints(GridBagConstraints.EAST, GridBagConstraints.VERTICAL, 2, 0));

		lblRarity = new JLabel();
		lblRarity.setFont(MTGControler.getInstance().getFont().deriveFont(Font.ITALIC, 11));
		add(lblRarity, UITools.createGridBagConstraints(GridBagConstraints.WEST, GridBagConstraints.VERTICAL, 1, 3));
	}

	public void setMagicCard(MTGCard mc) {

		if(mc==null)
			return;

		lblName.setText(mc.getName());
		lblType.setText(mc.getFullType());
		lblEdition.setText(mc.getEdition().toString());
		lblEdition.setIcon(IconsProvider.getInstance().get24(mc.getEdition().getId()));
		if(mc.getRarity()!=null)
			lblRarity.setText(mc.getRarity().toPrettyString());

		manaPanel.setManaCost(mc.getCost());
	}
}
