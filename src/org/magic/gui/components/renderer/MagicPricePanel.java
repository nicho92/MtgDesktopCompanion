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

public class MagicPricePanel extends JPanel {

	MagicPrice price;

	public MagicPricePanel(MagicPrice price) {
		setBackground(SystemColor.inactiveCaptionBorder);
		
		setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		this.price=price;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{55, 108, 0};
		gridBagLayout.rowHeights = new int[]{28, 25, 0, 20, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblName = new JLabel(price.getSite());
		lblName.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.WEST;
		gbc_lblName.gridwidth = 2;
		gbc_lblName.fill = GridBagConstraints.VERTICAL;
		gbc_lblName.insets = new Insets(0, 0, 5, 0);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		add(lblName, gbc_lblName);
		
		JLabel lblPrice = new JLabel("Price :");
		lblPrice.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblPrice = new GridBagConstraints();
		gbc_lblPrice.anchor = GridBagConstraints.WEST;
		gbc_lblPrice.insets = new Insets(0, 0, 5, 5);
		gbc_lblPrice.gridx = 0;
		gbc_lblPrice.gridy = 1;
		add(lblPrice, gbc_lblPrice);
		
		JLabel lblpriceValue = new JLabel(price.getValue()+price.getCurrency());
		GridBagConstraints gbc_lblpriceValue = new GridBagConstraints();
		gbc_lblpriceValue.insets = new Insets(0, 0, 5, 0);
		gbc_lblpriceValue.fill = GridBagConstraints.VERTICAL;
		gbc_lblpriceValue.gridx = 1;
		gbc_lblpriceValue.gridy = 1;
		add(lblpriceValue, gbc_lblpriceValue);
		
		JLabel lblSeller = new JLabel("Seller :");
		lblSeller.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblSeller = new GridBagConstraints();
		gbc_lblSeller.anchor = GridBagConstraints.WEST;
		gbc_lblSeller.insets = new Insets(0, 0, 5, 5);
		gbc_lblSeller.gridx = 0;
		gbc_lblSeller.gridy = 2;
		add(lblSeller, gbc_lblSeller);
		
		JLabel lblSellerinfo = new JLabel(price.getSeller());
		GridBagConstraints gbc_lblSellerinfo = new GridBagConstraints();
		gbc_lblSellerinfo.insets = new Insets(0, 0, 5, 0);
		gbc_lblSellerinfo.gridx = 1;
		gbc_lblSellerinfo.gridy = 2;
		add(lblSellerinfo, gbc_lblSellerinfo);
		
		JLabel lblInfos = new JLabel("Infos :");
		lblInfos.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblInfos = new GridBagConstraints();
		gbc_lblInfos.anchor = GridBagConstraints.WEST;
		gbc_lblInfos.insets = new Insets(0, 0, 0, 5);
		gbc_lblInfos.gridx = 0;
		gbc_lblInfos.gridy = 3;
		add(lblInfos, gbc_lblInfos);
		
		JLabel lblNewLabel = new JLabel(price.getLanguage()+"/"+price.getQuality()+""+(price.isFoil()?"/Foil":""));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 3;
		add(lblNewLabel, gbc_lblNewLabel);
	}
}
