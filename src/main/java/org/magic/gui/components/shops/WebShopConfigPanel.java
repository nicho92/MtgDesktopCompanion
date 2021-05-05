package org.magic.gui.components.shops;

import static org.magic.tools.MTG.capitalize;
import static org.magic.tools.MTG.getPlugin;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.painter.MattePainter;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.WebShopConfig;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGServer;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.ServerStatePanel;
import org.magic.gui.components.dialog.CardSearchImportDialog;
import org.magic.gui.components.editor.JCheckableListBox;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.tools.MTG;
import org.magic.tools.UITools;

public class WebShopConfigPanel extends MTGUIComponent {
	
	
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
	private JCheckableListBox<MagicCollection> cboCollections;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private MagicCard topProduct;
	private JSlider maxLastProductSlide;
	private JCheckableListBox<MagicCollection> needCollection;
	
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
		
		setLayout(new BorderLayout());
		
		JXTaskPaneContainer container = new JXTaskPaneContainer();
		container.setBackgroundPainter(new MattePainter(MTGConstants.PICTURE_PAINTER, true));
		
		
		
		WebShopConfig conf = MTGControler.getInstance().getWebConfig();
		var btnSave = new JButton("Save");
		
		
		
		JPanel panelGeneral = createBoxPanel("GENERAL", MTGConstants.ICON_TAB_CONSTRUCT, new GridLayout(0, 2, 0, 0), false );
		
			var lblTitleSite = new JLabel("SITETITLE");
			panelGeneral.add(lblTitleSite);
			
			txtSiteTitle = new JTextField(conf.getSiteTitle());
			panelGeneral.add(txtSiteTitle);
			
			panelGeneral.add(new JLabel("BANNERTITLE"));
			
			txtBannerTitle = new JTextField(conf.getBannerTitle());
			panelGeneral.add(txtBannerTitle);
			panelGeneral.add(new JLabel("BANNERTEXT"));
			txtBannerText = new JTextArea(conf.getBannerText());
			panelGeneral.add(new JScrollPane(txtBannerText));
			panelGeneral.add(new JLabel("ABOUT"));
			
			txtAbout = new JTextArea(conf.getAboutText());
			panelGeneral.add(new JScrollPane(txtAbout));
		
			
			
			
		JPanel panelSlides = createBoxPanel("SLIDES", MTGConstants.ICON_TAB_PICTURE, new BorderLayout(0, 0), true);
		
		var btnDeleteLink = new JButton(MTGConstants.ICON_SMALL_DELETE);
		btnDeleteLink.setEnabled(false);
		listModel = new DefaultListModel<>();
		
		
		for(String s : conf.getSlidesLinksImage())
			listModel.addElement(s);
		
		txtURLSlides = new JTextField();
		txtURLSlides.addActionListener((ActionEvent e)->{
				listModel.addElement(txtURLSlides.getText());
				txtURLSlides.setText("");
		});
		panelSlides.add(txtURLSlides, BorderLayout.NORTH);
		
		listSlides = new JList<>(listModel);
		listSlides.setVisibleRowCount(4);
		listSlides.setFixedCellHeight(25);
		panelSlides.add(new JScrollPane(listSlides), BorderLayout.CENTER);

		
		var deleteButtonLinkPanel = new JPanel();
		panelSlides.add(deleteButtonLinkPanel, BorderLayout.EAST);
		
		deleteButtonLinkPanel.add(btnDeleteLink);
		
		
		
		JPanel panelContact = createBoxPanel("CONTACT", MTGConstants.ICON_TAB_EVENTS, new GridLayout(0, 2, 0, 0), true);

		panelContact.add(new JLabel("NAME"));
		txtContactName = new JTextField(conf.getContact().getName());
		panelContact.add(txtContactName);
		panelContact.add(new JLabel("LAST_NAME"));
		
		txtLastName = new JTextField(conf.getContact().getLastName());
		panelContact.add(txtLastName);
		panelContact.add(new JLabel("EMAIL"));
		txtEmail = new JTextField(conf.getContact().getEmail());
		panelContact.add(txtEmail);
		panelContact.add(new JLabel("TELEPHONE"));
		txtTelephone = new JTextField(conf.getContact().getTelephone());
		panelContact.add(txtTelephone);
		
		panelContact.add(new JLabel("COUNTRY"));
		
		txtCountry = new JTextField(conf.getContact().getCountry());
		panelContact.add(txtCountry);
		
		panelContact.add(new JLabel("ADDRESS"));
		
		txtAddress = new JTextField(conf.getContact().getAddress());
		panelContact.add(txtAddress);
		txtAddress.setColumns(10);
		
		panelContact.add(new JLabel("WEBSITE"));
		
		txtWebSite = new JTextField(conf.getContact().getWebsite());
		panelContact.add(txtWebSite);
		txtWebSite.setColumns(10);
		
		
		
		
		
		JPanel panelServer = createBoxPanel("SERVER", MTGConstants.ICON_TAB_SERVER, new BorderLayout(), false);
		var serverStatPanel = new ServerStatePanel(false,getPlugin("Shopping Server", MTGServer.class));
		panelServer.add(serverStatPanel,BorderLayout.CENTER);
		
		
		
		JPanel panelStock = createBoxPanel("STOCK",MTGConstants.ICON_TAB_STOCK, new GridLayout(0, 2, 0, 0),true);
		cboCollections = new JCheckableListBox<>();
		needCollection = new JCheckableListBox<>();
		
		try {
			for(MagicCollection mc : MTG.getEnabledPlugin(MTGDao.class).listCollections())
			{
				cboCollections.addElement(mc, conf.getCollections().contains(mc));
				needCollection.addElement(mc, conf.getNeedcollections().contains(mc));
			}
		} catch (SQLException e1) {
			logger.error(e1);
		}
		
		panelStock.add(new JLabel("SELL_STOCK_IN_COLLECTION"));
		panelStock.add(cboCollections);

		panelStock.add(new JLabel("SEARCH_CARDS_IN_COLLECTION"));
		panelStock.add(needCollection);
		
		
		
		
		JPanel panelProduct = createBoxPanel("PRODUCT",MTGConstants.ICON_TAB_CARD, new GridLayout(0, 2, 0, 0),true);
		topProduct = conf.getTopProduct();
		var b = new JButton("Choose Top Product Card",MTGConstants.ICON_SEARCH);
		var l = new JLabel(String.valueOf(topProduct));
		
		var paneSlide = new JPanel();
		maxLastProductSlide = new JSlider(0, 16, conf.getMaxLastProduct());
		var valueLbl = new JLabel(String.valueOf(maxLastProductSlide.getValue()));
		
		maxLastProductSlide.addChangeListener(cl->valueLbl.setText(String.valueOf(maxLastProductSlide.getValue())));
		
		paneSlide.add(maxLastProductSlide);
		paneSlide.add(valueLbl);
		
		b.addActionListener(il->{
							   var diag = new CardSearchImportDialog();
								   diag.setVisible(true); 
								   topProduct= diag.getSelected();
								   if(topProduct!=null)
									   l.setText(topProduct.getName());
		});
		
		panelProduct.add(b);
		panelProduct.add(l);
		panelProduct.add(new JLabel("X_LASTEST_PRODUCT"));
		panelProduct.add(paneSlide);
		
		
		add(container,BorderLayout.CENTER);
		
		container.add(btnSave);
		container.add(panelGeneral);
		container.add(panelSlides);
		container.add(panelContact);
		container.add(panelStock);
		container.add(panelProduct);
		container.add(panelServer);
		
		
		listSlides.addListSelectionListener((ListSelectionEvent e)->btnDeleteLink.setEnabled(listSlides.getSelectedIndex()>-1));
		btnDeleteLink.addActionListener((ActionEvent e)->listModel.removeElement(listSlides.getSelectedValue()));
		btnSave.addActionListener(al->{
			
			WebShopConfig newBean = MTGControler.getInstance().getWebConfig();
			
			newBean.setAboutText(txtAbout.getText());
			newBean.setBannerText(txtBannerText.getText());
			newBean.setBannerTitle(txtBannerTitle.getText());
			newBean.setSiteTitle(txtSiteTitle.getText());
			newBean.setTopProduct(topProduct);
			newBean.setMaxLastProduct(maxLastProductSlide.getValue());
			
			newBean.getCollections().clear();
			newBean.getCollections().addAll(cboCollections.getSelectedElements());
			
			newBean.getNeedcollections().clear();
			newBean.getNeedcollections().addAll(needCollection.getSelectedElements());
			
			
			
			
			newBean.getSlidesLinksImage().clear();
			Iterator<String> it = listModel.elements().asIterator();
			while(it.hasNext())
				newBean.getSlidesLinksImage().add(it.next());
			
			
			newBean.getContact().setAddress(txtAddress.getText());
			newBean.getContact().setCountry(txtCountry.getText());
			newBean.getContact().setEmail(txtEmail.getText());
			newBean.getContact().setLastName(txtLastName.getText());
			newBean.getContact().setName(txtContactName.getText());
			newBean.getContact().setTelephone(txtTelephone.getText());
			newBean.getContact().setWebsite(txtWebSite.getText());
			
			
			
			MTGControler.getInstance().saveWebConfig(newBean);
			
		});
		

	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_ADMIN;
	}


	@Override
	public String getTitle() {
		return "WebShop Config";
	}

}
