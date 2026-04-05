package org.magic.gui.components.renderer;

import static org.magic.services.tools.MTG.capitalize;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.magic.api.beans.MTGPrice;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.gui.components.widgets.JLangLabel;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;
import org.magic.services.tools.UITools;
public class MagicPricePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel lblName;
	private JLabel lblpriceValue;
	private JLangLabel lblSeller;
	private JLabel lblSellerinfo;
	private JLangLabel lblInfos;

	public void init(MTGPrice price)
	{
		lblName.setText(price.getSite());
		try{
			lblName.setIcon(PluginRegistry.inst().getPlugin(price.getSite(), MTGPricesProvider.class).getIcon());
		}
		catch(Exception _)
		{
			//do nothing
		}
		lblpriceValue.setText(UITools.formatDouble(price.getValue()) + " " +price.getCurrency());
		lblSellerinfo.setText(price.getSeller());
		lblInfos.setText(price.getLanguage() + "/" + price.getQuality() + ""+ (price.isFoil() ? "/" + capitalize("FOIL") + "" : ""));
	}
	
	
	public MagicPricePanel() {

		setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		var gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 55, 108, 0 };
		gridBagLayout.rowHeights = new int[] { 28, 25, 0, 20, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		lblName = new JLabel();
		lblName.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		lblName.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD, 11));
		add(lblName, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 0, 0,2,null));

		var lblPrice = new JLangLabel("PRICE",true);
		lblPrice.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD, 11));
		add(lblPrice, UITools.createGridBagConstraints(GridBagConstraints.WEST, GridBagConstraints.BOTH, 0, 1));

		lblpriceValue = new JLabel();
		add(lblpriceValue, UITools.createGridBagConstraints(null, GridBagConstraints.VERTICAL, 1, 1));

		lblSeller = new JLangLabel("SELLER",true);
		lblSeller.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD, 11));
		add(lblSeller, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 0, 2));

		lblSellerinfo = new JLabel();
		add(lblSellerinfo, UITools.createGridBagConstraints(null, null, 1, 2));

		lblInfos = new JLangLabel("INFORMATIONS",true);
		lblInfos.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD, 11));
		add(lblInfos, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 0, 3));

		var lblNewLabel = new JLabel();
		add(lblNewLabel, UITools.createGridBagConstraints(null, null, 1, 3));
	}
}
