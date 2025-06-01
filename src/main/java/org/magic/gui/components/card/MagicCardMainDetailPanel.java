package org.magic.gui.components.card;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import org.magic.services.tools.UITools;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;



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
	
	
	boolean face=false;
	public void init(MTGCard mc)
	{
		lblThumbnail.setCursor(Cursor.getDefaultCursor());
	
		for(var l : lblThumbnail.getMouseListeners())
			lblThumbnail.removeMouseListener(l);
		
		
		if(mc==null)
			return;
		
		
		this.magicCard=mc;
		
		fillField(mc);
		
		((DefaultListModel<MTGFormat>) lstFormats.getModel()).removeAllElements();
		mc.getLegalities().forEach(((DefaultListModel<MTGFormat>) lstFormats.getModel())::addElement);
		
		if(thumbnail)
		{
			loadPics(mc);
			lblAuthor.setText(mc.getArtist());
			
			
			if(mc.isDoubleFaced()) 
			{
					lblThumbnail.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					lblThumbnail.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							if(face)
							{
								loadPics(mc);
								fillField(mc);
							}
							else
							{
								loadPics(mc.getRotatedCard());
								fillField(mc.getRotatedCard());
							}
							face=!face;
						}
						
					});
			}
		}
		
		
		if (enableCollectionLookup && !mc.getEditions().isEmpty()){
			var sw = new SwingWorker<List<MTGCollection>, Void>(){
							@Override
							protected List<MTGCollection> doInBackground() throws Exception {
								return getEnabledPlugin(MTGDao.class).listCollectionFromCards(mc);
							}

							@Override
							protected void done() {
								((DefaultListModel<MTGCollection>)lstCollections.getModel()).removeAllElements();
								try {
									get().forEach(((DefaultListModel<MTGCollection>)lstCollections.getModel())::addElement);
								} catch (InterruptedException _) {
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
							} catch (InterruptedException _) {
								Thread.currentThread().interrupt();
							} catch (Exception e) {
								logger.error(e);
							}

						}

					};
			ThreadManager.getInstance().runInEdt(sw, "Get alerts for " + mc);
		}
	}
	
	
	
	
	
	private void fillField(MTGCard mc) {
		txtName.setText(mc.getName());
		txtTypes.setText(mc.getFullType());
		txtFlavor.setText(mc.getFlavor());
		txtRarity.setText(mc.getRarity().toPrettyString());
		txtText.setText(mc.getText());
		
		chkReserved.setSelected(mc.isReserved());
		chkBorderless.setSelected(mc.isBorderLess());
		chkExtended.setSelected(mc.isExtendedArt());
		chkShowcase.setSelected(mc.isShowCase());
		chkRetro.setSelected(mc.isRetro());
		
		
		if(mc.isCreature() || mc.isVehicule())
			txtPower.setText(mc.getPower()+"/"+mc.getToughness());
		else if (mc.isPlaneswalker())
			txtPower.setText(""+mc.getLoyalty());
		else if (mc.isSiege())
			txtPower.setText(""+mc.getDefense());
		else
			txtPower.setText("");
		
		manaCostPanel.setManaCost(mc.getCost());
		
		int showCount = mc.getEdition().getCardCountOfficial();
		if(showCount<=0)
				showCount=mc.getEdition().getCardCount();

		lblNumber.setText(mc.getNumber() + "/"+ showCount);
		txtText.updateTextWithIcons();

	}


	protected void loadPics(MTGCard mc) {

		var d = MTGControler.getInstance().getPictureProviderDimension().getDimension();
		
		
		var sw = new SwingWorker<ImageIcon, Void>()
		{
			@Override
			protected ImageIcon doInBackground() throws Exception {
				return new ImageIcon(getEnabledPlugin(MTGPictureProvider.class).getPicture(mc));
			}

			@Override
			protected void done() {
				try {
						lblThumbnail.setIcon(ImageTools.resize(get(),d));
				}
				catch(InterruptedException _)
				{
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					lblThumbnail.setIcon(ImageTools.resize(new ImageIcon(getEnabledPlugin(MTGPictureProvider.class).getBackPicture(mc)),d));
					logger.error(e);
				}

			}
		};

		ThreadManager.getInstance().runInEdt(sw,"loading " + mc + " picture");
	}

	
	
	public MagicCardMainDetailPanel() {
		
		obs = new Observable();
	
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{50, 127, 159, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 156, 0, 40, 90, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		txtName = new JTextField();
		txtName.setToolTipText("Name");
		var gbctxtName = new GridBagConstraints();
		gbctxtName.insets = new Insets(0, 0, 5, 5);
		gbctxtName.fill = GridBagConstraints.HORIZONTAL;
		gbctxtName.gridx = 0;
		gbctxtName.gridy = 0;
		add(txtName, gbctxtName);
		txtName.setColumns(10);
		
		manaCostPanel = new ManaPanel();
		manaCostPanel.setToolTipText("Mana");
		var gbcmanaCostPanel = new GridBagConstraints();
		gbcmanaCostPanel.insets = new Insets(0, 0, 5, 5);
		gbcmanaCostPanel.gridx = 1;
		gbcmanaCostPanel.gridy = 0;
		add(manaCostPanel, gbcmanaCostPanel);

		btnAlert = new JButton(MTGConstants.ICON_ALERT);
		btnCopy = new JButton(MTGConstants.ICON_COPY);
		btnStock = new JButton(MTGConstants.ICON_STOCK);
		
		
		var gbcpanelActions = new GridBagConstraints();
		gbcpanelActions.insets = new Insets(0, 0, 5, 5);
		gbcpanelActions.fill = GridBagConstraints.BOTH;
		gbcpanelActions.gridx = 2;
		gbcpanelActions.gridy = 0;
		add(UITools.createFlowCenterPanel(btnAlert,btnCopy,btnStock),gbcpanelActions);
				
		txtTypes = new JTextField();
		txtTypes.setToolTipText("Type");
		var gbctxtTypes = new GridBagConstraints();
		gbctxtTypes.insets = new Insets(0, 0, 5, 5);
		gbctxtTypes.fill = GridBagConstraints.HORIZONTAL;
		gbctxtTypes.gridx = 0;
		gbctxtTypes.gridy = 1;
		add(txtTypes, gbctxtTypes);
		txtTypes.setColumns(10);
		
		txtRarity = new JTextField();
		txtRarity.setToolTipText("Rarity");
		txtRarity.setColumns(10);
		var gbctxtRarity = new GridBagConstraints();
		gbctxtRarity.insets = new Insets(0, 0, 5, 5);
		gbctxtRarity.fill = GridBagConstraints.HORIZONTAL;
		gbctxtRarity.gridx = 1;
		gbctxtRarity.gridy = 1;
		add(txtRarity, gbctxtRarity);
		
		lblThumbnail = new JLabel("");
		var gbclblThumbnail = new GridBagConstraints();
		gbclblThumbnail.fill = GridBagConstraints.HORIZONTAL;
		gbclblThumbnail.insets = new Insets(0, 0, 5, 0);
		gbclblThumbnail.gridheight = 5;
		gbclblThumbnail.gridx = 2;
		gbclblThumbnail.gridy = 1;
		add(lblThumbnail, gbclblThumbnail);
		
		txtText = new MagicTextPane();
		var gbctxtCardText = new GridBagConstraints();
		gbctxtCardText.gridwidth = 2;
		gbctxtCardText.insets = new Insets(0, 0, 5, 5);
		gbctxtCardText.fill = GridBagConstraints.BOTH;
		gbctxtCardText.gridx = 0;
		gbctxtCardText.gridy = 2;
		add(txtText, gbctxtCardText);
		
		txtFlavor = new JTextArea();
		txtFlavor.setFont(txtFlavor.getFont().deriveFont(Font.ITALIC));
		txtFlavor.setLineWrap(true);
		var gbctxtFlavour = new GridBagConstraints();
		gbctxtFlavour.insets = new Insets(0, 0, 5, 5);
		gbctxtFlavour.fill = GridBagConstraints.HORIZONTAL;
		gbctxtFlavour.gridx = 0;
		gbctxtFlavour.gridy = 3;
		add(txtFlavor, gbctxtFlavour);
		
		txtPower = new JTextField(10);
		txtPower.setHorizontalAlignment(SwingConstants.CENTER);
		txtPower.setToolTipText("Power");
		var gbctxtPower = new GridBagConstraints();
		gbctxtPower.fill = GridBagConstraints.HORIZONTAL;
		gbctxtPower.insets = new Insets(0, 0, 5, 5);
		gbctxtPower.gridx = 1;
		gbctxtPower.gridy = 3;
		add(txtPower, gbctxtPower);
		
		
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
		
			 
		var gbcchkReserved = new GridBagConstraints();
		gbcchkReserved.anchor = GridBagConstraints.WEST;
		gbcchkReserved.insets = new Insets(0, 0, 0, 5);
		gbcchkReserved.gridx = 0;
		gbcchkReserved.gridy = 6;
		add(UITools.createFlowPanel(chkReserved,new JSeparator(),chkBorderless,chkShowcase,chkExtended), gbcchkReserved);
		
		
		lblNumber = new JLabel("");
		GridBagConstraints gbclblNumber = new GridBagConstraints();
		gbclblNumber.insets = new Insets(0, 0, 5, 5);
		gbclblNumber.gridx = 1;
		gbclblNumber.gridy = 4;
		add(lblNumber, gbclblNumber);
		
		lstFormats = new JList<>(new DefaultListModel<>());
		lstFormats.setVisibleRowCount(4);
		lstFormats.setCellRenderer((JList<? extends MTGFormat> _, MTGFormat obj, int _,boolean _, boolean _)->{
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
		
		
		
		
		
		var gbclstFormats = new GridBagConstraints();
		gbclstFormats.gridheight = 2;
		gbclstFormats.insets = new Insets(0, 0, 5, 5);
		gbclstFormats.fill = GridBagConstraints.BOTH;
		gbclstFormats.gridx = 0;
		gbclstFormats.gridy = 4;
		add(new JScrollPane(lstFormats), gbclstFormats);
		
		lstCollections = new JList<>(new DefaultListModel<>());
		lstCollections.setCellRenderer((JList<? extends MTGCollection> _, MTGCollection obj, int _,boolean _, boolean _)->new JLabel(obj.getName(),MTGConstants.ICON_COLLECTION,SwingConstants.LEFT));

		var gbclstCollections = new GridBagConstraints();
		gbclstCollections.insets = new Insets(0, 0, 5, 5);
		gbclstCollections.fill = GridBagConstraints.BOTH;
		gbclstCollections.gridx = 1;
		gbclstCollections.gridy = 5;
		add(new JScrollPane(lstCollections), gbclstCollections);
		
		
		txtText.setBorder(txtName.getBorder());
		
		lblAuthor = new JLabel();
		lblAuthor.setHorizontalAlignment(SwingConstants.CENTER);
		var gbclblAuthor = new GridBagConstraints();
		gbclblAuthor.fill = GridBagConstraints.HORIZONTAL;
		gbclblAuthor.gridx = 2;
		gbclblAuthor.gridy = 6;
		add(lblAuthor, gbclblAuthor);
		
		
		btnAlert.addActionListener(_ -> {
			var alert = new MTGAlert();
			alert.setCard(magicCard);
			String price = JOptionPane.showInputDialog(null,
					capitalize("SELECT_MAXIMUM_PRICE"),
					capitalize("ADD_ALERT_FOR", magicCard),
					JOptionPane.QUESTION_MESSAGE);
			alert.setPrice(UITools.parseDouble(price));

			try {
				getEnabledPlugin(MTGDao.class).saveAlert(alert);
				btnAlert.setEnabled(false);
			} catch (Exception e) {
				logger.error(e);
				MTGControler.getInstance().notify(e);
			}
		});
		
		btnCopy.addActionListener(_ -> {
			try {
				MTG.getPlugin(MTGConstants.DEFAULT_CLIPBOARD_NAME,MTGCardsExport.class).exportDeck(MTGDeck.toDeck(Arrays.asList(magicCard)),null);
				
			} catch (Exception e) {
				logger.error(e);
				MTGControler.getInstance().notify(e);
			}
		});
		
		btnStock.addActionListener(_ -> {
			if(magicCard==null)
				return ;
			
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
		lstCollections.setVisible(b);
		
		
	}
	
}

