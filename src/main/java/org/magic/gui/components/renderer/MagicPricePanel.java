package org.magic.gui.components.renderer;

import static org.magic.tools.MTG.capitalize;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.SystemColor;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;
import org.magic.tools.UITools;
public class MagicPricePanel extends JPanel {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MagicPricePanel(MagicPrice price) {
		setBackground(SystemColor.inactiveCaptionBorder);

		setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 55, 108, 0 };
		gridBagLayout.rowHeights = new int[] { 28, 25, 0, 20, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblName = new JLabel(price.getSite());
		try{
			lblName.setIcon(PluginRegistry.inst().getPlugin(price.getSite(), MTGPricesProvider.class).getIcon());
		}
		catch(Exception e)
		{
			//do nothing
		}
		lblName.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		lblName.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD, 11));
		GridBagConstraints gbclblName = UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 0, 0);
		gbclblName.gridwidth = 2;
		add(lblName, gbclblName);

		JLabel lblPrice = new JLabel(capitalize("PRICE") + " :");
		lblPrice.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD, 11));
		add(lblPrice, UITools.createGridBagConstraints(GridBagConstraints.WEST, GridBagConstraints.BOTH, 0, 1));

		JLabel lblpriceValue = new JLabel(UITools.formatDouble(price.getValue()) + " " +price.getCurrency());
		add(lblpriceValue, UITools.createGridBagConstraints(null, GridBagConstraints.VERTICAL, 1, 1));

		JLabel lblSeller = new JLabel(capitalize("SELLER") + " :");
		lblSeller.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD, 11));
		add(lblSeller, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 0, 2));

		JLabel lblSellerinfo = new JLabel(price.getSeller());
		add(lblSellerinfo, UITools.createGridBagConstraints(null, null, 1, 2));

		JLabel lblInfos = new JLabel(capitalize("INFORMATIONS") + " :");
		lblInfos.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD, 11));
		add(lblInfos, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 0, 3));

		JLabel lblNewLabel = new JLabel(price.getLanguage() + "/" + price.getQuality() + ""
				+ (price.isFoil() ? "/" + capitalize("FOIL") + "" : ""));
		add(lblNewLabel, UITools.createGridBagConstraints(null, null, 1, 3));
	}
}
