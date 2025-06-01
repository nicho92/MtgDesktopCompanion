package org.magic.gui.components.renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.magic.api.interfaces.MTGStockItem;
import org.magic.services.MTGControler;
import org.magic.services.providers.IconsProvider;
import org.magic.services.tools.UITools;

public class StockItemPanelRenderer extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel lblName;
	private JLabel lblType;
	private JLabel lblEdition;

	public StockItemPanelRenderer() {
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


	}

	public void setProduct(MTGStockItem mc) {

		if(mc==null || mc.getProduct()==null)
			return;

		lblName.setText(mc.getProduct().getName());
		lblEdition.setText(mc.getProduct().getEdition().getSet());
		lblEdition.setIcon(IconsProvider.getInstance().get24(mc.getProduct().getEdition().getId()));
		lblType.setText(mc.getProduct().getTypeProduct().name());
	}
}
