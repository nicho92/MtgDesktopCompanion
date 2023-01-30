package org.magic.gui.components;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getEnabledPlugin;
import static org.magic.services.tools.MTG.getPlugin;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.widgets.JLangLabel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.UITools;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;
public class MagicCardDetailPanel extends MTGUIComponent implements Observer {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private transient BindingGroup mBindingGroup;
	private MagicCard magicCard;
	private JTextField cmcJTextField;
	private ManaPanel manaPanel;
	private JTextField fullTypeJTextField;
	private JTextField loyaltyJTextField;
	private JTextField nameJTextField;
	private JTextField powerJTextField;
	private MagicTextPane txtTextPane;
	private JTextField toughnessJTextField;
	private JTextPane txtFlavorArea;
	private JTextField txtArtist;
	private JLabel lblnumberInSet;
	private JPanel panelDetailCreature;
	private JTextField txtLayoutField;
	private boolean thumbnail = false;
	private JLabel lblThumbnail;
	private JLabel lblLogoSet;
	private JList<MTGFormat> lstFormats;
	private JList<MagicCollection> listCollection;
	private JTextField txtWatermark;
	private JTextField rarityJTextField;
	private GridBagLayout gridBagLayout;
	private JButton btnAlert;
	private JButton btnCopy;
	private JButton btnStock;
	private JCheckBox chckbxReserved;
	private boolean enableCollectionLookup = true;
	private DefaultListModel<MagicCollection> listModelCollection;
	private JPanel panelSwitchLangage;
	private transient Observable obs;


	public void setEditable(boolean b) {
		txtWatermark.setEditable(b);
		txtArtist.setEditable(b);
		txtFlavorArea.setEditable(b);
		txtTextPane.setEditable(b);
		rarityJTextField.setEditable(b);
		txtLayoutField.setEditable(b);
		toughnessJTextField.setEditable(b);
		powerJTextField.setEditable(b);
		loyaltyJTextField.setEditable(b);
		fullTypeJTextField.setEditable(b);
		nameJTextField.setEditable(b);
		cmcJTextField.setEditable(b);
		chckbxReserved.setEnabled(b);

	}

	public void enableThumbnail(boolean val) {
		thumbnail = val;
	}



	public MagicCardDetailPanel() {

		obs = new Observable();
		gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 52, 382, 76, 0, 57, 32, 51, 0, 77, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 44, 0, 65, 25, 21, 0, 0, 34, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4 };
		setLayout(gridBagLayout);

		add(new JLangLabel("NAME",true), UITools.createGridBagConstraints(null, null, 0, 0));
		add(new JLangLabel("CARD_COST",true), UITools.createGridBagConstraints(null, null, 2, 0));
		add(new JLangLabel("CARD_TYPES",true), UITools.createGridBagConstraints(null, null, 0, 1));
		add(new JLangLabel("CARD_MANA",true),  UITools.createGridBagConstraints(null, null, 2, 1));
		add(new JLangLabel("CARD_LOYALTY",true), UITools.createGridBagConstraints(null, null, 0, 2));
		add(new JLangLabel("CARD_LAYOUT",true), UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 0, 3));
		add(new JLangLabel("CARD_TEXT",true),UITools.createGridBagConstraints(null, null, 0, 4,null,2));
		add(new JLangLabel("CARD_FLAVOR",true), UITools.createGridBagConstraints(null, null, 0, 6));
		add(new JLangLabel("CARD_ARTIST",true), UITools.createGridBagConstraints(null, null, 0, 7));
		add(new JLangLabel("CARD_LEGALITIES",true), UITools.createGridBagConstraints(null, null, 0, 8));
		add(new JLangLabel("CARD_WATERMARK",true), UITools.createGridBagConstraints(null, null, 2, 7));



		nameJTextField = new JTextField();
		add(nameJTextField, UITools.createGridBagConstraints(null,GridBagConstraints.HORIZONTAL,1,0));

		cmcJTextField = new JTextField();
		add(cmcJTextField, UITools.createGridBagConstraints(null,GridBagConstraints.HORIZONTAL,4,0));

		lblLogoSet = new JLabel("");
		add(lblLogoSet, UITools.createGridBagConstraints(null, null, 5, 0,2,2));

		lblThumbnail = new JLabel("");
		add(lblThumbnail, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 7, 1,2,9));

		fullTypeJTextField = new JTextField();
		add(fullTypeJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 1));

		manaPanel = new ManaPanel();
		add(manaPanel, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 4, 1));

		lblnumberInSet = new JLabel("/");
		add(lblnumberInSet, UITools.createGridBagConstraints(null, null, 5, 2,2,null));

		txtLayoutField = new JTextField(10);
		add(txtLayoutField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 3));

		chckbxReserved = new JCheckBox("(R)");
		add(chckbxReserved, UITools.createGridBagConstraints(null, null, 2, 3));

		rarityJTextField = new JTextField(10);
		add(rarityJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 3, 3,3,null));

		txtArtist = new JTextField(10);
		add(txtArtist, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 7));

		txtWatermark = new JTextField(10);
		add(txtWatermark, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 4, 7,3,null));

		var p = new JPanel();

		btnCopy = new JButton(MTGConstants.ICON_COPY);
		btnCopy.setEnabled(false);
		btnCopy.setToolTipText("Copy to clipboard");
		btnCopy.addActionListener(ae -> {
			try {
				getPlugin(MTGConstants.DEFAULT_CLIPBOARD_NAME,MTGCardsExport.class).exportDeck(MagicDeck.toDeck(Arrays.asList(getMagicCard())),null);

			} catch (Exception e) {
				logger.error(e);
				MTGControler.getInstance().notify(e);
			}
		});



		btnAlert = new JButton(MTGConstants.ICON_ALERT);
		btnAlert.setEnabled(false);
		btnAlert.addActionListener(ae -> {
			var alert = new MagicCardAlert();
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

		btnStock = new JButton(MTGConstants.ICON_STOCK);
		btnStock.setEnabled(false);
		btnStock.setToolTipText(capitalize("ADD_CARDS_STOCKS"));
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



		p.add(btnCopy);
		p.add(btnAlert);
		p.add(btnStock);

		add(p, UITools.createGridBagConstraints(null, null, 8, 0));




		panelDetailCreature = new JPanel();
		loyaltyJTextField = new JTextField(5);

		powerJTextField = new JTextField(2);
		toughnessJTextField = new JTextField(2);
		var flowLayout = (FlowLayout) panelDetailCreature.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panelDetailCreature.add(loyaltyJTextField);
		panelDetailCreature.add(new JLangLabel("CARD_POWER",true));
		panelDetailCreature.add(powerJTextField);
		panelDetailCreature.add(new JLabel("/"));
		panelDetailCreature.add(toughnessJTextField);
		add(panelDetailCreature,UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 2,3,null));


		lstFormats = new JList<>(new DefaultListModel<>());
		lstFormats.setCellRenderer((JList<? extends MTGFormat> list, MTGFormat obj, int arg2,boolean arg3, boolean arg4)->{
			var l = new JLabel(obj.getFormat());

					if(obj.getFormatLegality()!=null)
					{

						switch (obj.getFormatLegality()) {
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
		add(new JScrollPane(lstFormats), UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 8,2,2));



		txtTextPane = new MagicTextPane();
		txtTextPane.setBorder(new LineBorder(Color.GRAY));
		txtTextPane.setBackground(Color.WHITE);
		add(txtTextPane, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 4,6,2));


		txtFlavorArea = new JTextPane();
		txtFlavorArea.setFont(txtFlavorArea.getFont().deriveFont(Font.ITALIC));
		add(txtFlavorArea, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 6,6,null));

		listModelCollection = new DefaultListModel<>();
		listCollection = new JList<>(listModelCollection);
		listCollection.setCellRenderer((JList<? extends MagicCollection> list, MagicCollection obj, int arg2,boolean arg3, boolean arg4)->new JLabel(obj.getName(),MTGConstants.ICON_COLLECTION,SwingConstants.LEFT));

		add(new JScrollPane(listCollection), UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 4, 8,3,2));


		panelSwitchLangage = new JPanel();
		var flowLayout1 = (FlowLayout) panelSwitchLangage.getLayout();
		flowLayout1.setAlignment(FlowLayout.LEFT);

		add(panelSwitchLangage, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 0, 10,9,null));

		if (magicCard != null) {
			mBindingGroup = initDataBindings();
		}

		setEditable(false);

	}

	public MagicCard getMagicCard() {
		return magicCard;
	}

	public void init(MagicCard newMagicCard) {
		init(newMagicCard, true);
	}

	public void init(MagicCard newMagicCard, boolean update) {
		magicCard = newMagicCard;

		btnStock.setEnabled(magicCard!=null);
		btnCopy.setEnabled(magicCard!=null);

		if (update) {
			if (mBindingGroup != null) {
				mBindingGroup.unbind();
				mBindingGroup = null;
			}
			if (magicCard != null) {
				mBindingGroup = initDataBindings();
			}
		}
	}

	private BindingGroup initDataBindings() {
		BeanProperty<MagicCard, Integer> cmcProperty = BeanProperty.create("cmc");
		BeanProperty<JTextField, String> textProperty = BeanProperty.create("text");
		AutoBinding<MagicCard, Integer, JTextField, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ, magicCard, cmcProperty, cmcJTextField, textProperty);
		autoBinding.bind();
		//
		BeanProperty<MagicCard, String> costProperty = BeanProperty.create("cost");
		BeanProperty<ManaPanel, String> textProperty1 = BeanProperty.create("manaCost");
		AutoBinding<MagicCard, String, ManaPanel, String> autoBinding1 = Bindings.createAutoBinding(UpdateStrategy.READ, magicCard, costProperty, manaPanel, textProperty1);
		autoBinding1.bind();
		//
		BeanProperty<MagicCard, String> fullTypeProperty = BeanProperty.create("fullType");
		BeanProperty<JTextField, String> textProperty2 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding2 = Bindings.createAutoBinding(UpdateStrategy.READ, magicCard, fullTypeProperty, fullTypeJTextField, textProperty2);
		autoBinding2.bind();
		//
		BeanProperty<MagicCard, Integer> loyaltyProperty = BeanProperty.create("loyalty");
		BeanProperty<JTextField, String> textProperty4 = BeanProperty.create("text");
		AutoBinding<MagicCard, Integer, JTextField, String> autoBinding4 = Bindings.createAutoBinding(UpdateStrategy.READ, magicCard, loyaltyProperty, loyaltyJTextField, textProperty4);
		autoBinding4.bind();
		//
		BeanProperty<MagicCard, String> nameProperty = BeanProperty.create("name");
		BeanProperty<JTextField, String> textProperty5 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding5 = Bindings.createAutoBinding(UpdateStrategy.READ, magicCard, nameProperty, nameJTextField, textProperty5);
		autoBinding5.bind();
		//
		BeanProperty<MagicCard, String> powerProperty = BeanProperty.create("power");
		BeanProperty<JTextField, String> textProperty6 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding6 = Bindings.createAutoBinding(UpdateStrategy.READ, magicCard, powerProperty, powerJTextField, textProperty6);
		autoBinding6.bind();
		//
		BeanProperty<MagicCard, String> textProperty8 = BeanProperty.create("text");
		BeanProperty<MagicTextPane, String> textProperty9 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, MagicTextPane, String> autoBinding8 = Bindings.createAutoBinding(UpdateStrategy.READ, magicCard, textProperty8, txtTextPane, textProperty9);
		autoBinding8.bind();
		//
		BeanProperty<MagicCard, String> toughnessProperty = BeanProperty.create("toughness");
		BeanProperty<JTextField, String> textProperty10 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding9 = Bindings.createAutoBinding(UpdateStrategy.READ, magicCard, toughnessProperty, toughnessJTextField, textProperty10);
		autoBinding9.bind();

		BeanProperty<MagicCard, String> flavorProperty = BeanProperty.create("flavor");
		BeanProperty<JTextPane, String> textProperty11 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextPane, String> autoBinding10 = Bindings.createAutoBinding(UpdateStrategy.READ, magicCard, flavorProperty, txtFlavorArea, textProperty11);
		autoBinding10.bind();

		BeanProperty<MagicCard, String> artistProperty = BeanProperty.create("artist");
		BeanProperty<JTextField, String> textProperty12 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding11 = Bindings.createAutoBinding(UpdateStrategy.READ, magicCard, artistProperty, txtArtist, textProperty12);
		autoBinding11.bind();

		BeanProperty<MagicCard, String> waterProperty = BeanProperty.create("watermarks");
		BeanProperty<JTextField, String> textProperty14 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding13 = Bindings.createAutoBinding(UpdateStrategy.READ, magicCard, waterProperty, txtWatermark, textProperty14);
		autoBinding13.bind();

		BeanProperty<MagicCard, Boolean> reservedProperty = BeanProperty.create("reserved");
		BeanProperty<JCheckBox, Boolean> chkProperty15 = BeanProperty.create("selected");
		AutoBinding<MagicCard, Boolean, JCheckBox, Boolean> autoBinding15 = Bindings.createAutoBinding(UpdateStrategy.READ, magicCard, reservedProperty, chckbxReserved, chkProperty15);
		autoBinding15.bind();

		try {
			if (magicCard != null)
				rarityJTextField.setText(magicCard.getRarity().toPrettyString());
		} catch (Exception e) {
			rarityJTextField.setText("");
		}

		try {
			if (magicCard != null)
				txtLayoutField.setText(magicCard.getLayout().toPrettyString());
		} catch (Exception e) {
			txtLayoutField.setText("");
		}


		txtTextPane.updateTextWithIcons();

		if (thumbnail && magicCard != null)
		{
			loadPics(magicCard,null);
		}

		if (magicCard != null && !magicCard.getEditions().isEmpty())
		{
			int showCount = magicCard.getCurrentSet().getCardCountOfficial();
			if(showCount==0)
				showCount=magicCard.getCurrentSet().getCardCount();

			lblnumberInSet.setText(magicCard.getCurrentSet().getNumber() + "/"+ showCount);
		}

		if (magicCard != null && enableCollectionLookup && !magicCard.getEditions().isEmpty())
		{
			var sw = new SwingWorker<List<MagicCollection>, Void>()
					{
							@Override
							protected List<MagicCollection> doInBackground() throws Exception {
								return getEnabledPlugin(MTGDao.class).listCollectionFromCards(magicCard);
							}

							@Override
							protected void done() {
								listModelCollection.removeAllElements();
								try {
									get().forEach(col->listModelCollection.addElement(col));
								} catch (InterruptedException e) {
									Thread.currentThread().interrupt();
								} catch (Exception e) {
									logger.error(e);
								}
							}

					};
			ThreadManager.getInstance().runInEdt(sw, "loadCollections");
		}

		if (magicCard != null && enableCollectionLookup) {

			var sw = new SwingWorker<Boolean, Void>()
					{

						@Override
						protected Boolean doInBackground() throws Exception {
							return getEnabledPlugin(MTGDao.class).hasAlert(magicCard)!=null;
						}

						@Override
						protected void done() {

							try {
								if (get().booleanValue()) {
									btnAlert.setToolTipText(capitalize("HAD_ALERT"));
									btnAlert.setEnabled(false);
								} else {
									btnAlert.setToolTipText(capitalize("ADD_ALERT_FOR",magicCard.getName()));
									btnAlert.setEnabled(true);
								}
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
							} catch (Exception e) {
								logger.error(e);
							}

						}

					};



			ThreadManager.getInstance().runInEdt(sw, "Get alerts for " + magicCard);
		}



		((DefaultListModel<MTGFormat>) lstFormats.getModel()).removeAllElements();

		if (magicCard != null)
			for (MTGFormat mf : magicCard.getLegalities())
				((DefaultListModel<MTGFormat>) lstFormats.getModel()).addElement(mf);



		var groupLanguagesButtons = new ButtonGroup();

		if(magicCard!=null)
		{

				panelSwitchLangage.removeAll();
				panelSwitchLangage.revalidate();

				if(enableCollectionLookup)
				{

				SwingWorker<Void, MagicCardNames> sw = new SwingWorker<>(){

					@Override
					protected void process(List<MagicCardNames> chunks) {

						chunks.forEach(fn->{
							var tglLangButton = new JToggleButton(fn.getLanguage());
							tglLangButton.setContentAreaFilled(false);
							tglLangButton.setActionCommand(fn.getLanguage());
							tglLangButton.setFont(tglLangButton.getFont().deriveFont(tglLangButton.getFont().getSize()-2));
							AbstractAction act = new AbstractAction() {
								private static final long serialVersionUID = 1L;

								@Override
								public void actionPerformed(ActionEvent e) {
									obs.setChanged();
									obs.notifyObservers(fn);
									txtTextPane.setText(fn.getText());
									txtTextPane.updateTextWithIcons();
									nameJTextField.setText(fn.getName());
									fullTypeJTextField.setText(fn.getType());
									txtFlavorArea.setText(fn.getFlavor());
									if (thumbnail)
										loadPics(magicCard,fn);

								}
							};
							act.putValue(Action.NAME, fn.getLanguage());

							tglLangButton.setActionCommand(fn.getLanguage());
							tglLangButton.setAction(act);
							groupLanguagesButtons.add(tglLangButton);
							panelSwitchLangage.add(tglLangButton);

							if(!MTGControler.getInstance().get("langage").equalsIgnoreCase("english") && fn.getGathererId()>0 && fn.getLanguage().equalsIgnoreCase(MTGControler.getInstance().get("langage")))
								tglLangButton.doClick();

						});



					}

					@Override
					protected Void doInBackground() throws Exception {
						publish(magicCard.getForeignNames().toArray(new MagicCardNames[magicCard.getForeignNames().size()]));
						return null;
					}
				};
				ThreadManager.getInstance().runInEdt(sw,"loading " + magicCard + " languages");
				}
		}
		//
		var bindingGroup = new BindingGroup();
		//
		bindingGroup.addBinding(autoBinding);
		bindingGroup.addBinding(autoBinding1);
		bindingGroup.addBinding(autoBinding2);
		bindingGroup.addBinding(autoBinding4);
		bindingGroup.addBinding(autoBinding5);
		bindingGroup.addBinding(autoBinding6);
		bindingGroup.addBinding(autoBinding8);
		bindingGroup.addBinding(autoBinding9);
		bindingGroup.addBinding(autoBinding10);
		bindingGroup.addBinding(autoBinding11);
		bindingGroup.addBinding(autoBinding13);
		bindingGroup.addBinding(autoBinding15);
		return bindingGroup;
	}

	protected void loadPics(MagicCard mc,MagicCardNames fn) {

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
					if(get().getIconHeight()>369)
						lblThumbnail.setIcon(ImageTools.resize(get(),266,369));
					else
						lblThumbnail.setIcon(get());
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


	@Override
	public String getTitle() {
		return "DETAILS";
	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_DETAILS;
	}
	
	@Override
	public void update(Observable o, Object ob) {
		init((MagicCard) ob);
	}

	public void enableCollectionLookup(boolean b) {
		enableCollectionLookup = b;
	}

	public void addObserver(Observer o) {
		obs.addObserver(o);
	}

}
