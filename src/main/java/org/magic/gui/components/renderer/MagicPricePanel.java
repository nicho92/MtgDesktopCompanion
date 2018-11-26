package org.magic.gui.components.renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;

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
		lblName.setFont(MTGConstants.FONT.deriveFont(Font.BOLD, 11));
		GridBagConstraints gbclblName = new GridBagConstraints();
		gbclblName.gridwidth = 2;
		gbclblName.fill = GridBagConstraints.BOTH;
		gbclblName.insets = new Insets(0, 0, 5, 0);
		gbclblName.gridx = 0;
		gbclblName.gridy = 0;
		add(lblName, gbclblName);

		JLabel lblPrice = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("PRICE") + " :");
		lblPrice.setFont(MTGConstants.FONT.deriveFont(Font.BOLD, 11));
		GridBagConstraints gbclblPrice = new GridBagConstraints();
		gbclblPrice.anchor = GridBagConstraints.WEST;
		gbclblPrice.insets = new Insets(0, 0, 5, 5);
		gbclblPrice.gridx = 0;
		gbclblPrice.gridy = 1;
		add(lblPrice, gbclblPrice);

		JLabel lblpriceValue = new JLabel(price.getValue() + " " +price.getCurrency());
		GridBagConstraints gbclblpriceValue = new GridBagConstraints();
		gbclblpriceValue.insets = new Insets(0, 0, 5, 0);
		gbclblpriceValue.fill = GridBagConstraints.VERTICAL;
		gbclblpriceValue.gridx = 1;
		gbclblpriceValue.gridy = 1;
		add(lblpriceValue, gbclblpriceValue);

		JLabel lblSeller = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("SELLER") + " :");
		lblSeller.setFont(MTGConstants.FONT.deriveFont(Font.BOLD, 11));
		GridBagConstraints gbclblSeller = new GridBagConstraints();
		gbclblSeller.anchor = GridBagConstraints.WEST;
		gbclblSeller.insets = new Insets(0, 0, 5, 5);
		gbclblSeller.gridx = 0;
		gbclblSeller.gridy = 2;
		add(lblSeller, gbclblSeller);

		JLabel lblSellerinfo = new JLabel(price.getSeller());
		GridBagConstraints gbclblSellerinfo = new GridBagConstraints();
		gbclblSellerinfo.insets = new Insets(0, 0, 5, 0);
		gbclblSellerinfo.gridx = 1;
		gbclblSellerinfo.gridy = 2;
		add(lblSellerinfo, gbclblSellerinfo);

		JLabel lblInfos = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("INFORMATIONS") + " :");
		lblInfos.setFont(MTGConstants.FONT.deriveFont(Font.BOLD, 11));
		GridBagConstraints gbclblInfos = new GridBagConstraints();
		gbclblInfos.anchor = GridBagConstraints.WEST;
		gbclblInfos.insets = new Insets(0, 0, 0, 5);
		gbclblInfos.gridx = 0;
		gbclblInfos.gridy = 3;
		add(lblInfos, gbclblInfos);

		JLabel lblNewLabel = new JLabel(price.getLanguage() + "/" + price.getQuality() + ""
				+ (price.isFoil() ? "/" + MTGControler.getInstance().getLangService().getCapitalize("FOIL") + "" : ""));
		GridBagConstraints gbclblNewLabel = new GridBagConstraints();
		gbclblNewLabel.gridx = 1;
		gbclblNewLabel.gridy = 3;
		add(lblNewLabel, gbclblNewLabel);
	}
}
