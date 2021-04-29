package org.magic.gui.components.shops;

import static org.magic.tools.MTG.getPlugin;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.magic.api.beans.WebShopConfig;
import org.magic.api.interfaces.MTGServer;
import org.magic.gui.components.ServerStatePanel;
import org.magic.services.MTGControler;

public class WebShopConfigPanel extends JPanel {
	
	
	private JTextField txtSiteTitle;
	private JTextField txtBannerTitle;
	private JTextArea txtBannerText;
	private JTextArea txtAbout;
	
	public WebShopConfigPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{137, 86, 0};
		gridBagLayout.rowHeights = new int[]{31, 31, 58, 58, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		var lblTitleSite = new JLabel("SITE_TITLE");
		GridBagConstraints gbc_lblTitleSite = new GridBagConstraints();
		gbc_lblTitleSite.insets = new Insets(0, 0, 5, 5);
		gbc_lblTitleSite.gridx = 0;
		gbc_lblTitleSite.gridy = 0;
		add(lblTitleSite, gbc_lblTitleSite);
		
		txtSiteTitle = new JTextField();
		GridBagConstraints gbc_txtSiteTitle = new GridBagConstraints();
		gbc_txtSiteTitle.insets = new Insets(0, 0, 5, 0);
		gbc_txtSiteTitle.fill = GridBagConstraints.BOTH;
		gbc_txtSiteTitle.gridx = 1;
		gbc_txtSiteTitle.gridy = 0;
		add(txtSiteTitle, gbc_txtSiteTitle);
		
		JLabel lblBannerTitle = new JLabel("BANNER_TITLE");
		GridBagConstraints gbc_lblBannerTitle = new GridBagConstraints();
		gbc_lblBannerTitle.insets = new Insets(0, 0, 5, 5);
		gbc_lblBannerTitle.gridx = 0;
		gbc_lblBannerTitle.gridy = 1;
		add(lblBannerTitle, gbc_lblBannerTitle);
		
		txtBannerTitle = new JTextField();
		GridBagConstraints gbc_txtBannerTitle = new GridBagConstraints();
		gbc_txtBannerTitle.insets = new Insets(0, 0, 5, 0);
		gbc_txtBannerTitle.fill = GridBagConstraints.BOTH;
		gbc_txtBannerTitle.gridx = 1;
		gbc_txtBannerTitle.gridy = 1;
		add(txtBannerTitle, gbc_txtBannerTitle);
		txtBannerTitle.setColumns(10);
		
		JLabel lblBannerText = new JLabel("BANNER_TEXT");
		GridBagConstraints gbc_lblBannerText = new GridBagConstraints();
		gbc_lblBannerText.insets = new Insets(0, 0, 5, 5);
		gbc_lblBannerText.gridx = 0;
		gbc_lblBannerText.gridy = 2;
		add(lblBannerText, gbc_lblBannerText);
		
		txtBannerText = new JTextArea();
		GridBagConstraints gbc_txtBannerText = new GridBagConstraints();
		gbc_txtBannerText.insets = new Insets(0, 0, 5, 0);
		gbc_txtBannerText.fill = GridBagConstraints.BOTH;
		gbc_txtBannerText.gridx = 1;
		gbc_txtBannerText.gridy = 2;
		add(txtBannerText, gbc_txtBannerText);
		
		JLabel lblAbout = new JLabel("ABOUT");
		GridBagConstraints gbc_lblAbout = new GridBagConstraints();
		gbc_lblAbout.insets = new Insets(0, 0, 5, 5);
		gbc_lblAbout.gridx = 0;
		gbc_lblAbout.gridy = 3;
		add(lblAbout, gbc_lblAbout);
		
		txtAbout = new JTextArea();
		GridBagConstraints gbc_txtAbout = new GridBagConstraints();
		gbc_txtAbout.insets = new Insets(0, 0, 5, 0);
		gbc_txtAbout.fill = GridBagConstraints.BOTH;
		gbc_txtAbout.gridx = 1;
		gbc_txtAbout.gridy = 3;
		add(txtAbout, gbc_txtAbout);
		
		JButton btnSave = new JButton("Save");
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.insets = new Insets(0, 0, 5, 0);
		gbc_btnSave.gridwidth = 2;
		gbc_btnSave.gridx = 0;
		gbc_btnSave.gridy = 5;
		add(btnSave, gbc_btnSave);
		
		ServerStatePanel serverPanel = new ServerStatePanel(false,getPlugin("Shopping Server", MTGServer.class));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 6;
		add(serverPanel, gbc_panel);
		
		
		
		btnSave.addActionListener(al->{
			
			WebShopConfig bean = MTGControler.getInstance().getWebConfig();
			
			bean.setAboutText(txtAbout.getText());
			bean.setBannerText(txtBannerText.getText());
			bean.setBannerTitle(txtBannerTitle.getText());
			bean.setSiteTitle(txtSiteTitle.getText());
			
			
			MTGControler.getInstance().saveWebConfig(bean);
			
		});
		

	}

	public JTextField getTxtSiteTitle() {
		return txtSiteTitle;
	}
	public JTextField getTxtBannerTitle() {
		return txtBannerTitle;
	}
	public JTextArea getTextArea() {
		return txtBannerText;
	}
	public JTextArea getTextArea_1() {
		return txtAbout;
	}
}
