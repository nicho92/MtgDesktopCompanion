package org.magic.gui.components.shops;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getPlugin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.painter.MattePainter;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.technical.WebShopConfig;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGServer;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.dialog.importer.CardSearchImportDialog;
import org.magic.gui.components.dialog.importer.CardStockImportDialog;
import org.magic.gui.components.editor.JCheckableListBox;
import org.magic.gui.components.renderer.StockItemPanelRenderer;
import org.magic.gui.components.tech.ServerStatePanel;
import org.magic.gui.components.widgets.JLangLabel;
import org.magic.servers.impl.JSONHttpServer;
import org.magic.servers.impl.ShoppingServer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.TransactionService;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;

public class WebShopConfigPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JTextField txtSiteTitle;
	private JTextField txtBannerTitle;
	private JTextArea txtBannerText;
	private JTextArea txtAbout;
	private JTextField txtURLSlides;
	private DefaultListModel<String> listModel;
	private JList<String> listSlides;
	private JTextField txtAnalyticsGoogle;
	private JCheckableListBox<MTGCollection> cboCollections;
	private MTGCardStock topProduct;
	private JSlider maxLastProductSlide;
	private JSlider productPagination;
	private JCheckableListBox<MTGCollection> needCollection;
	private JSpinner spinnerReduction ;
	private JSpinner averageDeliverayDay ;
	private RSyntaxTextArea txtdeliveryRules ;
	private ContactSelectionPanel contactPanel;
	private JTextField txtPaypalClientId;
	private JTextField txtPaypalSendMoneyLink;
	private JTextField txtWebsiteUrl;
	private JCheckBox chkAutomaticValidation;
	private JCheckBox chkAutoProduct;
	private JCheckBox chkEnableStock;
	private JCheckBox chkEnableGed;
	private JTextField txtIban;
	private JTextField txtBic;
	private RSyntaxTextArea txtExtraCss ;


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
		contactPanel = new ContactSelectionPanel();
		var container = new JXTaskPaneContainer();
		container.setBackgroundPainter(new MattePainter(MTGConstants.PICTURE_PAINTER, true));



		var conf = MTGControler.getInstance().getWebConfig();
		var btnSave = new JButton("Save");



		var panelGeneral = createBoxPanel("GENERAL", MTGConstants.ICON_TAB_CONSTRUCT, new GridLayout(0, 2, 0, 0), false );

			var lblTitleSite = new JLangLabel("SITETITLE");
			panelGeneral.add(lblTitleSite);

			txtSiteTitle = new JTextField(conf.getSiteTitle());
			panelGeneral.add(txtSiteTitle);

			txtAnalyticsGoogle = new JTextField(conf.getGoogleAnalyticsId());
			txtAbout = new JTextArea(conf.getAboutText());
			txtBannerTitle = new JTextField(conf.getBannerTitle());
			txtBannerText = new JTextArea(conf.getBannerText());
			txtWebsiteUrl = new JTextField(conf.getWebsiteUrl());
			chkEnableGed = new JCheckBox();
			chkEnableGed.setSelected(conf.isEnableGed());
			panelGeneral.add(new JLangLabel("BANNERTITLE"));
			panelGeneral.add(txtBannerTitle);
			panelGeneral.add(new JLangLabel("BANNERTEXT"));
			panelGeneral.add(new JScrollPane(txtBannerText));
			panelGeneral.add(new JLangLabel("ABOUT"));
			panelGeneral.add(new JScrollPane(txtAbout));
			panelGeneral.add(new JLangLabel("WEBSITE_URL"));
			panelGeneral.add(txtWebsiteUrl);
			panelGeneral.add(new JLangLabel("GOOGLE_ID_ANALYTICS"));
			panelGeneral.add(txtAnalyticsGoogle);
			panelGeneral.add(new JLangLabel("GED_ENABLE"));
			panelGeneral.add(chkEnableGed);



		var panelSlides = createBoxPanel("SLIDES", MTGConstants.ICON_TAB_PICTURE, new BorderLayout(0, 0), true);

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



		var panelCss = createBoxPanel("CSS", MTGConstants.ICON_TAB_JSON, new BorderLayout(0, 0), true);
		txtExtraCss = new RSyntaxTextArea(conf.getExtraCss(),25,1);

		txtExtraCss.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSS);

		panelCss.setLayout(new BorderLayout());
		panelCss.add(new JScrollPane(txtExtraCss));


		var panelContact = createBoxPanel("CONTACT", MTGConstants.ICON_TAB_EVENTS, new GridLayout(0, 2, 0, 0), true);
		panelContact.setLayout(new BorderLayout());
		panelContact.add(contactPanel,BorderLayout.CENTER);
		contactPanel.setContact(conf.getContact());

		var panelServer = createBoxPanel("SERVER", MTGConstants.ICON_TAB_SERVER, new BorderLayout(), false);
		var serverStatPanel = new ServerStatePanel(false,getPlugin(new ShoppingServer().getName(), MTGServer.class));
		panelServer.add(serverStatPanel,BorderLayout.CENTER);
		var btnClearCache = new JButton("Clear Cache",MTGConstants.ICON_TAB_CACHE);
		btnClearCache.addActionListener(il->((JSONHttpServer)getPlugin(new JSONHttpServer().getName(), MTGServer.class)).clearCache());
		panelServer.add(btnClearCache,BorderLayout.SOUTH);

		var panelStock = createBoxPanel("STOCK",MTGConstants.ICON_TAB_STOCK, new GridLayout(0, 2, 0, 0),true);
		cboCollections = new JCheckableListBox<>();
		needCollection = new JCheckableListBox<>();
		chkEnableStock = new JCheckBox();
		chkEnableStock.setSelected(conf.isSealedEnabled());
		chkAutomaticValidation = new JCheckBox();
		chkAutomaticValidation.setSelected(conf.isAutomaticValidation());


		try {
			for(MTGCollection mc : MTG.getEnabledPlugin(MTGDao.class).listCollections())
			{
				cboCollections.addElement(mc, conf.getCollections().contains(mc));
				needCollection.addElement(mc, conf.getNeedcollections().contains(mc));
			}
		} catch (SQLException e1) {
			logger.error(e1);
		}

		panelStock.add(new JLangLabel("SELL_STOCK_IN_COLLECTION"));
		panelStock.add(cboCollections);

		panelStock.add(new JLangLabel("SEARCH_CARDS_IN_COLLECTION"));
		panelStock.add(needCollection);

		panelStock.add(new JLangLabel("AUTOMATIC_VALIDATION"));
		panelStock.add(chkAutomaticValidation);

		panelStock.add(new JLangLabel("ENABLE_SEALED_STOCK"));
		panelStock.add(chkEnableStock);





		var panelProduct = createBoxPanel("PRODUCT",MTGConstants.ICON_TAB_CARD, new GridLayout(0, 2),true);
		topProduct = conf.getTopProduct();
		var b = new JButton("Choose Top Product Card",MTGConstants.ICON_SEARCH);
		chkAutoProduct = new JCheckBox("Automatic Top Product");
		b.setEnabled(!chkAutoProduct.isSelected());

		spinnerReduction = new JSpinner(new SpinnerNumberModel(conf.getPercentReduction()*100,0,100,0.5));



		maxLastProductSlide = new JSlider(0, 16, conf.getMaxLastProduct());
		var lblMaxProductValue = new JLabel(String.valueOf(maxLastProductSlide.getValue()));
		maxLastProductSlide.addChangeListener(cl->lblMaxProductValue.setText(String.valueOf(maxLastProductSlide.getValue())));

		productPagination = new JSlider(0, 50, conf.getProductPagination());
		var lblProductPaginationValue = new JLabel(String.valueOf(productPagination.getValue()));
		productPagination.addChangeListener(cl->lblProductPaginationValue.setText(String.valueOf(productPagination.getValue())));



		var cardPanel = new StockItemPanelRenderer();

		if(topProduct!=null)
			cardPanel.setProduct(topProduct);

		var paneSlide = new JPanel();
		paneSlide.add(maxLastProductSlide);
		paneSlide.add(lblMaxProductValue);


		var paneSlide2 = new JPanel();
		paneSlide2.add(productPagination);
		paneSlide2.add(lblProductPaginationValue);



		b.addActionListener(il->{
							   var diag = new CardStockImportDialog();
								   diag.setVisible(true);
								   if(diag.getSelectedItem()!=null)
								   {
									   topProduct = diag.getSelectedItem();
									   cardPanel.setProduct(topProduct);
								   }
		});



		var sw = new SwingWorker<MTGCardStock , MTGCardStock >() {

			@Override
			protected MTGCardStock doInBackground() throws Exception {
				return TransactionService.getBestProduct();
			}

			@Override
			protected void done() {
				try {
					topProduct = get();
					cardPanel.setProduct(topProduct);
				} catch (InterruptedException|ExecutionException e) {
					Thread.currentThread().interrupt();
				} catch (NoSuchElementException e1) {
					logger.warn("No best product found");
				}
				catch (Exception e1) {
					logger.error(e1);
				}
			}
		};

		chkAutoProduct.addItemListener(il->{
				if(chkAutoProduct.isSelected())
				{
					ThreadManager.getInstance().runInEdt(sw, "Loading best product");
				}
				b.setEnabled(!chkAutoProduct.isSelected());
		});

		chkAutoProduct.setSelected(conf.isAutomaticProduct());


		var panelButton = new JPanel();
		panelButton.setLayout(new GridLayout(2, 1));
		panelButton.add(b);
		panelButton.add(chkAutoProduct);


		panelProduct.add(panelButton);
		panelProduct.add(cardPanel);
		panelProduct.add(new JLangLabel("X_LASTEST_PRODUCT"));
		panelProduct.add(paneSlide);
		panelProduct.add(new JLangLabel("PRODUCT_PAGINATION"));
		panelProduct.add(paneSlide2);





		panelProduct.add(new JLangLabel("PERCENT_REDUCTION_FOR_SELL"));
		panelProduct.add(spinnerReduction);

		JPanel panelDelivery = createBoxPanel("DELIVERY",MTGConstants.ICON_TAB_DELIVERY, new BorderLayout(),true);
		averageDeliverayDay = new JSpinner(new SpinnerNumberModel(conf.getAverageDeliveryTime(),0,8,1));
		txtdeliveryRules = new RSyntaxTextArea(10,1);
		txtdeliveryRules.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
		txtdeliveryRules.setText(conf.getShippingRules());
		var panelHaut = new JPanel();
			((FlowLayout)panelHaut.getLayout()).setAlignment(FlowLayout.LEFT);
			panelHaut.add(new JLangLabel("DELIVERY_DAY"));
			panelHaut.add(averageDeliverayDay);

		panelDelivery.add(panelHaut,BorderLayout.NORTH);
		panelDelivery.add(new JLangLabel("DELIVERY_RULES"),BorderLayout.WEST);
		panelDelivery.add(new JScrollPane(txtdeliveryRules), BorderLayout.CENTER);

		JPanel panelPayment = createBoxPanel("PAYMENT",MTGConstants.ICON_TAB_PRICES, new GridLayout(0, 2, 0, 0),true);

		txtPaypalClientId = new JTextField(conf.getPaypalClientId());
		panelPayment.add(new JLangLabel("PAYPAL_CLIENT_ID"));
		panelPayment.add(txtPaypalClientId);

		txtPaypalSendMoneyLink = new JTextField(conf.getSetPaypalSendMoneyUri().toString());
		panelPayment.add(new JLangLabel("PAYPAL_SEND_MONEY_LINK"));
		panelPayment.add(txtPaypalSendMoneyLink);

		txtIban = new JTextField(conf.getIban(),20);
		txtBic = new JTextField(conf.getBic(),10);
		var panelIbanBic  = new JPanel();

		((FlowLayout)panelIbanBic.getLayout()).setAlignment(FlowLayout.LEFT);

		panelIbanBic.add(txtIban);
		panelIbanBic.add(new JLangLabel("BIC"));
		panelIbanBic.add(txtBic);


		panelPayment.add(new JLangLabel("IBAN"));
		panelPayment.add(panelIbanBic);


		add(container,BorderLayout.CENTER);
		container.add(btnSave);
		container.add(panelGeneral);
		container.add(panelSlides);
		container.add(panelCss);
		container.add(panelContact);
		container.add(panelStock);
		container.add(panelProduct);
		container.add(panelDelivery);
		container.add(panelPayment);

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
				newBean.setSealedEnabled(chkEnableStock.isSelected());
				newBean.setMaxLastProduct(maxLastProductSlide.getValue());
				newBean.setProductPagination(productPagination.getValue());

				newBean.setGoogleAnalyticsId(txtAnalyticsGoogle.getText());
				newBean.setAverageDeliveryTime(Integer.parseInt(averageDeliverayDay.getValue().toString()));
				newBean.setShippingRules(txtdeliveryRules.getText());
				newBean.setPercentReduction(Double.parseDouble(spinnerReduction.getValue().toString())/100);
				newBean.setPaypalClientId(txtPaypalClientId.getText());
				newBean.setAutomaticValidation(chkAutomaticValidation.isSelected());
				newBean.setAutomaticProduct(chkAutoProduct.isSelected());
				newBean.setIban(txtIban.getText());
				newBean.setBic(txtBic.getText());
				newBean.setExtraCss(txtExtraCss.getText());
				newBean.setWebsiteUrl(txtWebsiteUrl.getText());
				newBean.setEnableGed(chkEnableGed.isSelected());
				try {
					newBean.setPaypalSendMoneyUri(new URI(txtPaypalSendMoneyLink.getText()));
				} catch (URISyntaxException e1) {
					MTGControler.getInstance().notify(e1);
				}

				newBean.getCollections().clear();
				newBean.getCollections().addAll(cboCollections.getSelectedElements());

				newBean.getNeedcollections().clear();
				newBean.getNeedcollections().addAll(needCollection.getSelectedElements());




			newBean.getSlidesLinksImage().clear();
			Iterator<String> it = listModel.elements().asIterator();
			while(it.hasNext())
				newBean.getSlidesLinksImage().add(it.next());


			newBean.setContact(contactPanel.getContact());



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
