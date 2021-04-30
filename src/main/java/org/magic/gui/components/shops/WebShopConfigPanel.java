package org.magic.gui.components.shops;

import static org.magic.tools.MTG.capitalize;
import static org.magic.tools.MTG.getPlugin;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.magic.api.beans.WebShopConfig;
import org.magic.api.interfaces.MTGServer;
import org.magic.gui.components.ServerStatePanel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class WebShopConfigPanel extends JXTaskPaneContainer {
	
	
	private JTextField txtSiteTitle;
	private JTextField txtBannerTitle;
	private JTextArea txtBannerText;
	private JTextArea txtAbout;
	private JTextField txtURLSlides;
	private DefaultListModel<String> listModel;
	private JList<String> listSlides;
	private JTextField txtContactName;
	private JTextField txtLastName;
	private JTextField txtEmail;
	private JTextField txtTelephone;
	private JTextField txtCountry;
	private JTextField txtAddress;
	private JTextField txtWebSite;
	
	private JPanel createBoxPanel(String keyName, Icon ic, LayoutManager layout,boolean collapsed)
	{
		var pane = new JXTaskPane();
		pane.setTitle(capitalize(keyName));
		pane.setIcon(ic);
		pane.setCollapsed(collapsed);
		pane.setLayout(layout);
		return pane;
	}
	
	
	public WebShopConfigPanel() {
		
		WebShopConfig conf = MTGControler.getInstance().getWebConfig();
		
		JPanel panelGeneral = createBoxPanel("GENERALE", MTGConstants.ICON_TAB_CONSTRUCT, new GridLayout(0, 2, 0, 0), true );
		
			var lblTitleSite = new JLabel("SITETITLE");
			panelGeneral.add(lblTitleSite);
			
			txtSiteTitle = new JTextField(conf.getSiteTitle());
			panelGeneral.add(txtSiteTitle);
			
			JLabel lblBannerTitle = new JLabel("BANNERTITLE");
			panelGeneral.add(lblBannerTitle);
			
			txtBannerTitle = new JTextField(conf.getBannerTitle());
			panelGeneral.add(txtBannerTitle);
			txtBannerTitle.setColumns(10);
			
			JLabel lblBannerText = new JLabel("BANNERTEXT");
			panelGeneral.add(lblBannerText);
			
			txtBannerText = new JTextArea(conf.getBannerText());
			panelGeneral.add(new JScrollPane(txtBannerText));
			
			JLabel lblAbout = new JLabel("ABOUT");
			panelGeneral.add(lblAbout);
			
			txtAbout = new JTextArea(conf.getAboutText());
			panelGeneral.add(new JScrollPane(txtAbout));
		
			
			
			
		JPanel panelSlides = createBoxPanel("SLIDES", MTGConstants.ICON_TAB_PICTURE, new BorderLayout(0, 0), true);
		
		JButton btnDeleteLink = new JButton(MTGConstants.ICON_SMALL_DELETE);
		listModel = new DefaultListModel<>();
		
		
		for(String s : conf.getSlidesLinksImage())
			listModel.addElement(s);
		
		txtURLSlides = new JTextField();
		txtURLSlides.addActionListener((ActionEvent e)->{
				listModel.addElement(txtURLSlides.getText());
				txtURLSlides.setText("");
		});
		panelSlides.add(txtURLSlides, BorderLayout.NORTH);
		txtURLSlides.setColumns(10);
		
		
		listSlides = new JList<>(listModel);
		listSlides.addListSelectionListener((ListSelectionEvent e)->{
				btnDeleteLink.setEnabled(listSlides.getSelectedIndex()>-1);

		});

		panelSlides.add(new JScrollPane(listSlides), BorderLayout.CENTER);

		
		JPanel deleteButtonLinkPanel = new JPanel();
		panelSlides.add(deleteButtonLinkPanel, BorderLayout.EAST);
		
		
		btnDeleteLink.addActionListener((ActionEvent e)->{
				listModel.removeElement(listSlides.getSelectedValue());
		});
		deleteButtonLinkPanel.add(btnDeleteLink);
		
		
		
		JPanel panelContact = createBoxPanel("CONTACT", MTGConstants.ICON_TAB_EVENTS, new GridLayout(0, 2, 0, 0), true);
		
		JLabel lblContactName = new JLabel("NAME");
		lblContactName.setHorizontalAlignment(SwingConstants.CENTER);
		panelContact.add(lblContactName);
		
		txtContactName = new JTextField(conf.getContact().getName());
		panelContact.add(txtContactName);
		
		JLabel lblLastName = new JLabel("LAST_NAME");
		lblLastName.setHorizontalAlignment(SwingConstants.CENTER);
		panelContact.add(lblLastName);
		
		txtLastName = new JTextField(conf.getContact().getLastName());
		panelContact.add(txtLastName);
		
		JLabel lblEmail = new JLabel("EMAIL");
		lblEmail.setHorizontalAlignment(SwingConstants.CENTER);
		panelContact.add(lblEmail);
		
		txtEmail = new JTextField(conf.getContact().getEmail());
		panelContact.add(txtEmail);
		
		JLabel lblTelephone = new JLabel("TELEPHONE");
		lblTelephone.setHorizontalAlignment(SwingConstants.CENTER);
		panelContact.add(lblTelephone);
		
		txtTelephone = new JTextField(conf.getContact().getTelephone());
		panelContact.add(txtTelephone);
		
		
		JLabel lblCountry = new JLabel("COUNTRY");
		lblCountry.setHorizontalAlignment(SwingConstants.CENTER);
		panelContact.add(lblCountry);
		
		txtCountry = new JTextField(conf.getContact().getCountry());
		panelContact.add(txtCountry);
		
		JLabel lblAddress = new JLabel("ADDRESS");
		lblAddress.setHorizontalAlignment(SwingConstants.CENTER);
		panelContact.add(lblAddress);
		
		txtAddress = new JTextField(conf.getContact().getAddress());
		panelContact.add(txtAddress);
		txtAddress.setColumns(10);
		
		JLabel lblWebSite = new JLabel("WEBSITE");
		lblWebSite.setHorizontalAlignment(SwingConstants.CENTER);
		panelContact.add(lblWebSite);
		
		txtWebSite = new JTextField(conf.getContact().getWebsite());
		panelContact.add(txtWebSite);
		txtWebSite.setColumns(10);
		
		JButton btnSave = new JButton("Save");
		GridBagConstraints gbcbtnSave = new GridBagConstraints();
		gbcbtnSave.insets = new Insets(0, 0, 5, 0);
		gbcbtnSave.gridwidth = 2;
		gbcbtnSave.gridx = 0;
		gbcbtnSave.gridy = 3;
		add(btnSave, gbcbtnSave);
		
		
		JPanel panelServer = createBoxPanel("SERVER", MTGConstants.ICON_TAB_SERVER, new BorderLayout(), true);
		ServerStatePanel serverStatPanel = new ServerStatePanel(false,getPlugin("Shopping Server", MTGServer.class));
		panelServer.add(serverStatPanel,BorderLayout.CENTER);
		
		
		
		add(panelGeneral);
		add(panelSlides);
		add(panelContact);
		add(panelServer);
		
		btnSave.addActionListener(al->{
			
			WebShopConfig bean = MTGControler.getInstance().getWebConfig();
			
			bean.setAboutText(txtAbout.getText());
			bean.setBannerText(txtBannerText.getText());
			bean.setBannerTitle(txtBannerTitle.getText());
			bean.setSiteTitle(txtSiteTitle.getText());
			
			bean.getSlidesLinksImage().clear();
			Iterator<String> it = listModel.elements().asIterator();
			while(it.hasNext())
				bean.getSlidesLinksImage().add(it.next());
			
			
			bean.getContact().setAddress(txtAddress.getText());
			bean.getContact().setCountry(txtCountry.getText());
			bean.getContact().setEmail(txtEmail.getText());
			bean.getContact().setLastName(txtLastName.getText());
			bean.getContact().setName(txtContactName.getText());
			bean.getContact().setTelephone(txtTelephone.getText());
			bean.getContact().setWebsite(txtWebSite.getText());
			
			
			
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
	public JTextArea getTextArea1() {
		return txtAbout;
	}
	public JList getListSlides() {
		return listSlides;
	}
	public JTextField getTxtContactName() {
		return txtContactName;
	}
	public JTextField getTxtLastName() {
		return txtLastName;
	}
	public JTextField getTxtEmail() {
		return txtEmail;
	}
	public JTextField getTxtTelephone() {
		return txtTelephone;
	}
	public JTextField getTxtCountry() {
		return txtCountry;
	}
	public JTextField getTxtAddress() {
		return txtAddress;
	}
	public JTextField getTxtWebSite() {
		return txtWebSite;
	}
}
