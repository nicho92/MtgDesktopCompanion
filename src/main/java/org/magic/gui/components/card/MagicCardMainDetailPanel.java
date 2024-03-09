package org.magic.gui.components.card;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGAlert;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardNames;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.MTG;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;
import javax.swing.UIManager;



public class MagicCardMainDetailPanel extends JPanel  implements Observer {
	
	
	
	private static final long serialVersionUID = 1L;
	private JTextField txtName;
	private JTextField txtTypes;
	private JTextField txtPower;
	private JTextArea txtFlavor;
	private JTextField txtRarity;
	private JCheckBox chkReserved;
	private JCheckBox chkBorderless;
	private JCheckBox chkShowcase;
	private JCheckBox chkExtended;
	private MagicTextPane txtText;
	private ManaPanel manaCostPanel;
	private JPanel panelActions;
	private JButton btnAlert;
	private JButton btnCopy;
	private JButton btnStock;
	private JLabel lblThumbnail;
	private JLabel lblNumber;
	private boolean thumbnail = false;
	private JList<MTGFormat> lstFormats;
	private transient Observable obs;
	private boolean enableCollectionLookup=true;
	private JList<MTGCollection> lstCollections;
	private transient Logger logger = MTGLogger.getLogger(MagicCardMainDetailPanel.class);
	private MTGCard magicCard;
	private JCheckBox chkRetro;
	private JLabel lblAuthor;
	
	public String getTitle() {
		return "DETAILS";
	}
	
	
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_DETAILS;
	}
	
	
	
	public void enableThumbnail(boolean val) {
		thumbnail = val;
	}
	
	private void setEditable(boolean b) {
		
		txtName.setEditable(b);
		txtTypes.setEditable(b);
		txtPower.setEditable(b);
		txtFlavor.setEditable(b);
		txtRarity.setEditable(b);
		
		
		txtText.setEditable(b);
	}
	
	public void init(MTGCard mc)
	{
		
		if(mc==null)
			return;
		
		
		this.magicCard=mc;
		
		txtName.setText(mc.getName());
		txtTypes.setText(mc.getFullType());
		txtFlavor.setText(mc.getFlavor());
		txtRarity.setText(mc.getRarity().toPrettyString());
		txtText.setText(mc.getText());
		lblAuthor.setText(mc.getArtist());
		chkReserved.setSelected(mc.isReserved());
		chkBorderless.setSelected(mc.isBorderLess());
		chkExtended.setSelected(mc.isExtendedArt());
		chkShowcase.setSelected(mc.isShowCase());
		chkRetro.setSelected(mc.isRetro());
		
		if(mc.isCreature())
			txtPower.setText(mc.getPower()+"/"+mc.getToughness());
		else if (mc.isPlaneswalker())
			txtPower.setText(""+mc.getLoyalty());
		else if (mc.isSiege())
			txtPower.setText(""+mc.getDefense());
		else
			txtPower.setText("");
		
		manaCostPanel.setManaCost(mc.getCost());
		
		if (!mc.getEditions().isEmpty())
		{
			int showCount = mc.getEdition().getCardCountOfficial();
			if(showCount<=0)
				showCount=mc.getEdition().getCardCount();

			lblNumber.setText(mc.getNumber() + "/"+ showCount);
		}
		
		txtText.updateTextWithIcons();
		
		
		((DefaultListModel<MTGFormat>) lstFormats.getModel()).removeAllElements();
		mc.getLegalities().forEach(((DefaultListModel<MTGFormat>) lstFormats.getModel())::addElement);
		
		if(thumbnail)
			loadPics(mc, null);
		
		
		if (enableCollectionLookup && !mc.getEditions().isEmpty())
		{
			var sw = new SwingWorker<List<MTGCollection>, Void>()
					{
							@Override
							protected List<MTGCollection> doInBackground() throws Exception {
								return getEnabledPlugin(MTGDao.class).listCollectionFromCards(mc);
							}

							@Override
							protected void done() {
								((DefaultListModel<MTGCollection>)lstCollections.getModel()).removeAllElements();
								try {
									get().forEach(((DefaultListModel<MTGCollection>)lstCollections.getModel())::addElement);
								} catch (InterruptedException e) {
									Thread.currentThread().interrupt();
								} catch (Exception e) {
									logger.error(e);
								}
							}

					};
			ThreadManager.getInstance().runInEdt(sw, "loadCollections");
		}

		if (enableCollectionLookup) {

			var sw = new SwingWorker<Boolean, Void>()
					{

						@Override
						protected Boolean doInBackground() throws Exception {
							return getEnabledPlugin(MTGDao.class).hasAlert(mc)!=null;
						}

						@Override
						protected void done() {

							try {
								if (get().booleanValue()) {
									btnAlert.setToolTipText(capitalize("HAD_ALERT"));
									btnAlert.setEnabled(false);
								} else {
									btnAlert.setToolTipText(capitalize("ADD_ALERT_FOR",mc.getName()));
									btnAlert.setEnabled(true);
								}
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
							} catch (Exception e) {
								logger.error(e);
							}

						}

					};



			ThreadManager.getInstance().runInEdt(sw, "Get alerts for " + mc);
		}
		
	}
	
	
	protected void loadPics(MTGCard mc,MTGCardNames fn) {

		SwingWorker<ImageIcon, Void> sw = new SwingWorker<>()
		{
			@Override
			protected ImageIcon doInBackground() throws Exception {
				ImageIcon icon;
				try {
					if(fn==null)
						icon = new ImageIcon(getEnabledPlugin(MTGPictureProvider.class).getPicture(mc));
					else
						icon = new ImageIcon(getEnabledPlugin(MTGPictureProvider.class).getForeignNamePicture(fn, mc));
				} catch (Exception e) {
					icon = new ImageIcon(getEnabledPlugin(MTGPictureProvider.class).getBackPicture(mc));
					logger.error("Error loading pics for {}", mc, e);
				}
				return icon;

			}

			@Override
			protected void done() {
				try {
						lblThumbnail.setIcon(ImageTools.resize(get(),MTGControler.getInstance().getPictureProviderDimension()));
				}
				catch(InterruptedException ex)
				{
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					logger.error(e);
				}

			}
		};

		ThreadManager.getInstance().runInEdt(sw,"loading " + mc + " picture");
	}

	
	
	public MagicCardMainDetailPanel() {
		
		obs = new Observable();
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 121, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 156, 0, 40, 90, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		txtName = new JTextField();
		txtName.setToolTipText("Name");
		GridBagConstraints gbctxtName = new GridBagConstraints();
		gbctxtName.insets = new Insets(0, 0, 5, 5);
		gbctxtName.fill = GridBagConstraints.HORIZONTAL;
		gbctxtName.gridx = 0;
		gbctxtName.gridy = 0;
		add(txtName, gbctxtName);
		txtName.setColumns(10);
		
		manaCostPanel = new ManaPanel();
		manaCostPanel.setToolTipText("Mana");
		GridBagConstraints gbcmanaCostPanel = new GridBagConstraints();
		gbcmanaCostPanel.insets = new Insets(0, 0, 5, 5);
		gbcmanaCostPanel.gridx = 1;
		gbcmanaCostPanel.gridy = 0;
		add(manaCostPanel, gbcmanaCostPanel);
		
		panelActions = new JPanel();
		GridBagConstraints gbcpanelActions = new GridBagConstraints();
		gbcpanelActions.insets = new Insets(0, 0, 5, 0);
		gbcpanelActions.fill = GridBagConstraints.BOTH;
		gbcpanelActions.gridx = 2;
		gbcpanelActions.gridy = 0;
		add(panelActions, gbcpanelActions);
		
		btnAlert = new JButton(MTGConstants.ICON_ALERT);
		panelActions.add(btnAlert);
		
		btnCopy = new JButton(MTGConstants.ICON_COPY);
		panelActions.add(btnCopy);
		
		btnStock = new JButton(MTGConstants.ICON_STOCK);
		panelActions.add(btnStock);
		
		txtTypes = new JTextField();
		txtTypes.setToolTipText("Type");
		GridBagConstraints gbctxtTypes = new GridBagConstraints();
		gbctxtTypes.insets = new Insets(0, 0, 5, 5);
		gbctxtTypes.fill = GridBagConstraints.HORIZONTAL;
		gbctxtTypes.gridx = 0;
		gbctxtTypes.gridy = 1;
		add(txtTypes, gbctxtTypes);
		txtTypes.setColumns(10);
		
		txtRarity = new JTextField();
		txtRarity.setToolTipText("Rarity");
		txtRarity.setColumns(10);
		GridBagConstraints gbctxtRarity = new GridBagConstraints();
		gbctxtRarity.insets = new Insets(0, 0, 5, 5);
		gbctxtRarity.fill = GridBagConstraints.HORIZONTAL;
		gbctxtRarity.gridx = 1;
		gbctxtRarity.gridy = 1;
		add(txtRarity, gbctxtRarity);
		
		lblThumbnail = new JLabel("");
		GridBagConstraints gbclblThumbnail = new GridBagConstraints();
		gbclblThumbnail.insets = new Insets(0, 0, 5, 0);
		gbclblThumbnail.gridheight = 5;
		gbclblThumbnail.gridx = 2;
		gbclblThumbnail.gridy = 1;
		add(lblThumbnail, gbclblThumbnail);
		
		txtText = new MagicTextPane();
		GridBagConstraints gbctxtCardText = new GridBagConstraints();
		gbctxtCardText.gridwidth = 2;
		gbctxtCardText.insets = new Insets(0, 0, 5, 5);
		gbctxtCardText.fill = GridBagConstraints.BOTH;
		gbctxtCardText.gridx = 0;
		gbctxtCardText.gridy = 2;
		add(txtText, gbctxtCardText);
		
		txtFlavor = new JTextArea();
		txtFlavor.setFont(txtFlavor.getFont().deriveFont(Font.ITALIC));
		txtFlavor.setLineWrap(true);
		GridBagConstraints gbctxtFlavour = new GridBagConstraints();
		gbctxtFlavour.insets = new Insets(0, 0, 5, 5);
		gbctxtFlavour.fill = GridBagConstraints.HORIZONTAL;
		gbctxtFlavour.gridx = 0;
		gbctxtFlavour.gridy = 3;
		add(txtFlavor, gbctxtFlavour);
		
		txtPower = new JTextField();
		txtPower.setHorizontalAlignment(SwingConstants.CENTER);
		txtPower.setToolTipText("Power");
		GridBagConstraints gbctxtPower = new GridBagConstraints();
		gbctxtPower.fill = GridBagConstraints.HORIZONTAL;
		gbctxtPower.insets = new Insets(0, 0, 5, 5);
		gbctxtPower.gridx = 1;
		gbctxtPower.gridy = 3;
		add(txtPower, gbctxtPower);
		txtPower.setColumns(10);
		
		
		chkReserved = new JCheckBox("(Reserved)");
		chkReserved.setToolTipText("Reserved");
		chkBorderless = new JCheckBox("(Borderless)");
		chkBorderless.setToolTipText("Borderless");
		chkShowcase = new JCheckBox("(Showcase)");
		chkShowcase.setToolTipText("Showcase");
		chkExtended= new JCheckBox("(Extended)");
		chkExtended.setToolTipText("Extended");
		chkRetro = new JCheckBox("(Retro)");
		chkRetro.setToolTipText("Retro");
		
		var panelDetails = new JPanel();
		panelDetails.setBorder(UIManager.getBorder("TextField.border"));
			 panelDetails.add(chkReserved);
			 panelDetails.add(new JSeparator());
			 panelDetails.add(chkBorderless);
			 panelDetails.add(chkShowcase);
			 panelDetails.add(chkExtended);
			 
		GridBagConstraints gbcchkReserved = new GridBagConstraints();
		gbcchkReserved.anchor = GridBagConstraints.WEST;
		gbcchkReserved.insets = new Insets(0, 0, 0, 5);
		gbcchkReserved.gridx = 0;
		gbcchkReserved.gridy = 6;
		add(panelDetails, gbcchkReserved);
		

		panelDetails.add(chkRetro);
		
		lblNumber = new JLabel("");
		GridBagConstraints gbclblNumber = new GridBagConstraints();
		gbclblNumber.insets = new Insets(0, 0, 5, 5);
		gbclblNumber.gridx = 1;
		gbclblNumber.gridy = 4;
		add(lblNumber, gbclblNumber);
		
		lstFormats = new JList<>(new DefaultListModel<>());
		lstFormats.setVisibleRowCount(4);
		lstFormats.setCellRenderer((JList<? extends MTGFormat> list, MTGFormat obj, int arg2,boolean arg3, boolean arg4)->{
		var l = new JLabel(obj.getFormat());
					if(obj.getFormatLegality()!=null)
					{
						switch (obj.getFormatLegality()) 
						{
							case BANNED: l.setIcon(MTGConstants.ICON_SMALL_DELETE);break;
							case LEGAL:l.setIcon(MTGConstants.ICON_SMALL_CHECK);break;
							case NOT_LEGAL:l.setIcon(MTGConstants.ICON_SMALL_DELETE);break;
							case RESTRICTED:l.setIcon(MTGConstants.ICON_SMALL_EQUALS);break;
							default: break;
						}
						l.setToolTipText(obj.getFormat() + ":" + obj.getFormatLegality().name());
					}
				return l;
		});
		
		
		
		
		
		GridBagConstraints gbclstFormats = new GridBagConstraints();
		gbclstFormats.gridheight = 2;
		gbclstFormats.insets = new Insets(0, 0, 5, 5);
		gbclstFormats.fill = GridBagConstraints.BOTH;
		gbclstFormats.gridx = 0;
		gbclstFormats.gridy = 4;
		add(new JScrollPane(lstFormats), gbclstFormats);
		
		lstCollections = new JList<>(new DefaultListModel<>());
		lstCollections.setCellRenderer((JList<? extends MTGCollection> list, MTGCollection obj, int arg2,boolean arg3, boolean arg4)->new JLabel(obj.getName(),MTGConstants.ICON_COLLECTION,SwingConstants.LEFT));

		GridBagConstraints gbclstCollections = new GridBagConstraints();
		gbclstCollections.insets = new Insets(0, 0, 5, 5);
		gbclstCollections.fill = GridBagConstraints.BOTH;
		gbclstCollections.gridx = 1;
		gbclstCollections.gridy = 5;
		add(new JScrollPane(lstCollections), gbclstCollections);
		
		
		txtText.setBorder(txtName.getBorder());
		
		lblAuthor = new JLabel();
		lblAuthor.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbclblAuthor = new GridBagConstraints();
		gbclblAuthor.fill = GridBagConstraints.HORIZONTAL;
		gbclblAuthor.gridx = 2;
		gbclblAuthor.gridy = 6;
		add(lblAuthor, gbclblAuthor);
		
		
		btnAlert.addActionListener(ae -> {
			var alert = new MTGAlert();
			alert.setCard(magicCard);
			String price = JOptionPane.showInputDialog(null,
					capitalize("SELECT_MAXIMUM_PRICE"),
					capitalize("ADD_ALERT_FOR", magicCard),
					JOptionPane.QUESTION_MESSAGE);
			alert.setPrice(Double.parseDouble(price));

			try {
				getEnabledPlugin(MTGDao.class).saveAlert(alert);
			} catch (Exception e) {
				logger.error(e);
				MTGControler.getInstance().notify(e);
			}
		});
		
		btnCopy.addActionListener(ae -> {
			try {
				MTG.getPlugin(MTGConstants.DEFAULT_CLIPBOARD_NAME,MTGCardsExport.class).exportDeck(MTGDeck.toDeck(Arrays.asList(magicCard)),null);
				
			} catch (Exception e) {
				logger.error(e);
				MTGControler.getInstance().notify(e);
			}
		});
		
		btnStock.addActionListener(ae -> {
			var st = MTGControler.getInstance().getDefaultStock();
			st.setProduct(magicCard);
			try {
				getEnabledPlugin(MTGDao.class).saveOrUpdateCardStock(st);
				MTGControler.getInstance().notify(new MTGNotification("Stock", "Added", MESSAGE_TYPE.INFO));
			} catch (Exception e) {
				logger.error(e);
				MTGControler.getInstance().notify(e);
			}
		});
		
		
		setEditable(false);
	}
	

	@Override
	public void update(Observable o, Object ob) {
		init((MTGCard) ob);
	}

	public void enableCollectionLookup(boolean b) {
		enableCollectionLookup = b;
	}
	
	public void addObserver(Observer o) {
		obs.addObserver(o);
	}

}

