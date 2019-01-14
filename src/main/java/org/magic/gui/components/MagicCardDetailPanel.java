package org.magic.gui.components;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
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
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;

import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicFormat;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public class MagicCardDetailPanel extends JPanel implements Observer {

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
	private JLabel lblFlavor;
	private JTextPane txtFlavorArea;
	private JLabel lblArtist;
	private JTextField txtArtist;
	private JLabel lblnumberInSet;
	private JPanel panelDetailCreature;
	private JLabel lblLayout;
	private JTextField txtLayoutField;
	private boolean thumbnail = false;
	private JLabel lblThumbnail;
	private JLabel lblLogoSet;
	private JLabel lblLegal;
	private JList<MagicFormat> lstFormats;
	private JList<MagicCollection> listCollection;
	private JScrollPane scrollLegality;
	private JLabel lblWatermark;
	private JTextField txtWatermark;
	private JScrollPane scrollCollections;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private JTextField rarityJTextField;
	private GridBagLayout gridBagLayout;
	private JButton btnAlert;
	private JCheckBox chckbxReserved;
	private boolean enableCollectionLookup = true;
	private DefaultListModel<MagicCollection> listModelCollection;
	private JPanel panelSwitchLangage;
	
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

		gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 52, 382, 76, 0, 57, 32, 51, 0, 77, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 44, 0, 65, 25, 21, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4 };
		setLayout(gridBagLayout);

		JLabel nameLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("NAME") + " :");
		GridBagConstraints labelgbc5 = new GridBagConstraints();
		labelgbc5.insets = new Insets(5, 5, 5, 5);
		labelgbc5.gridx = 0;
		labelgbc5.gridy = 0;
		add(nameLabel, labelgbc5);

		nameJTextField = new JTextField();

		GridBagConstraints componentgbc5 = new GridBagConstraints();
		componentgbc5.insets = new Insets(5, 0, 5, 5);
		componentgbc5.fill = GridBagConstraints.HORIZONTAL;
		componentgbc5.gridx = 1;
		componentgbc5.gridy = 0;
		add(nameJTextField, componentgbc5);

		JLabel cmcLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_COST") + " :");
		GridBagConstraints labelgbc0 = new GridBagConstraints();
		labelgbc0.anchor = GridBagConstraints.EAST;
		labelgbc0.insets = new Insets(5, 5, 5, 5);
		labelgbc0.gridx = 2;
		labelgbc0.gridy = 0;
		add(cmcLabel, labelgbc0);

		cmcJTextField = new JTextField();
		GridBagConstraints componentgbc0 = new GridBagConstraints();
		componentgbc0.insets = new Insets(5, 0, 5, 5);
		componentgbc0.fill = GridBagConstraints.HORIZONTAL;
		componentgbc0.gridx = 4;
		componentgbc0.gridy = 0;
		add(cmcJTextField, componentgbc0);

		lblLogoSet = new JLabel("");
		GridBagConstraints gbclblLogoSet = new GridBagConstraints();
		gbclblLogoSet.gridwidth = 2;
		gbclblLogoSet.gridheight = 2;
		gbclblLogoSet.insets = new Insets(0, 0, 5, 5);
		gbclblLogoSet.gridx = 5;
		gbclblLogoSet.gridy = 0;
		add(lblLogoSet, gbclblLogoSet);

		btnAlert = new JButton("");
		btnAlert.setEnabled(false);
		Image b = MTGConstants.ICON_ALERT.getImage();
		btnAlert.setIcon(new ImageIcon(b));
		btnAlert.addActionListener(ae -> {

			MagicCardAlert alert = new MagicCardAlert();
			alert.setCard(magicCard);
			String price = JOptionPane.showInputDialog(null,
					MTGControler.getInstance().getLangService().getCapitalize("SELECT_MAXIMUM_PRICE"),
					MTGControler.getInstance().getLangService().getCapitalize("ADD_ALERT_FOR", magicCard),
					JOptionPane.QUESTION_MESSAGE);
			alert.setPrice(Double.parseDouble(price));

			try {
				MTGControler.getInstance().getEnabled(MTGDao.class).saveAlert(alert);
			} catch (Exception e) {
				logger.error(e);
				MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),e));
			}
		});

		GridBagConstraints gbcbtnAlert = new GridBagConstraints();
		gbcbtnAlert.insets = new Insets(0, 0, 5, 0);
		gbcbtnAlert.gridx = 8;
		gbcbtnAlert.gridy = 0;
		add(btnAlert, gbcbtnAlert);

		lblThumbnail = new JLabel("");
		GridBagConstraints gbclblThumbnail = new GridBagConstraints();
		gbclblThumbnail.insets = new Insets(0, 0, 5, 0);
		gbclblThumbnail.fill = GridBagConstraints.HORIZONTAL;
		gbclblThumbnail.gridwidth = 2;
		gbclblThumbnail.gridheight = 9;
		gbclblThumbnail.gridx = 7;
		gbclblThumbnail.gridy = 1;
		add(lblThumbnail, gbclblThumbnail);

		JLabel fullTypeLabel = new JLabel(
				MTGControler.getInstance().getLangService().getCapitalize("CARD_TYPES") + " :");
		GridBagConstraints labelgbc2 = new GridBagConstraints();
		labelgbc2.insets = new Insets(5, 5, 5, 5);
		labelgbc2.gridx = 0;
		labelgbc2.gridy = 1;
		add(fullTypeLabel, labelgbc2);

		fullTypeJTextField = new JTextField();
		GridBagConstraints componentgbc2 = new GridBagConstraints();
		componentgbc2.insets = new Insets(5, 0, 5, 5);
		componentgbc2.fill = GridBagConstraints.HORIZONTAL;
		componentgbc2.gridx = 1;
		componentgbc2.gridy = 1;
		add(fullTypeJTextField, componentgbc2);

		JLabel costLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_MANA") + " :");
		GridBagConstraints labelgbc1 = new GridBagConstraints();
		labelgbc1.anchor = GridBagConstraints.EAST;
		labelgbc1.insets = new Insets(5, 5, 5, 5);
		labelgbc1.gridx = 2;
		labelgbc1.gridy = 1;
		add(costLabel, labelgbc1);

		manaPanel = new ManaPanel();
		GridBagConstraints componentgbc1 = new GridBagConstraints();
		componentgbc1.insets = new Insets(5, 0, 5, 5);
		componentgbc1.fill = GridBagConstraints.HORIZONTAL;
		componentgbc1.gridx = 4;
		componentgbc1.gridy = 1;
		add(manaPanel, componentgbc1);

		JLabel loyaltyLabel = new JLabel(
				MTGControler.getInstance().getLangService().getCapitalize("CARD_LOYALTY") + " :");
		GridBagConstraints gbcloyaltyLabel = new GridBagConstraints();
		gbcloyaltyLabel.insets = new Insets(0, 0, 5, 5);
		gbcloyaltyLabel.gridx = 0;
		gbcloyaltyLabel.gridy = 2;
		add(loyaltyLabel, gbcloyaltyLabel);
		loyaltyJTextField = new JTextField();
		loyaltyJTextField.setColumns(5);
		JLabel powerLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_POWER") + " :");
		powerJTextField = new JTextField();
		powerJTextField.setColumns(5);
		JLabel toughnessLabel = new JLabel("/");
		toughnessJTextField = new JTextField();
		toughnessJTextField.setColumns(5);

		panelDetailCreature = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelDetailCreature.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbcpanelDetailCreature = new GridBagConstraints();
		gbcpanelDetailCreature.gridwidth = 3;
		gbcpanelDetailCreature.insets = new Insets(0, 0, 5, 5);
		gbcpanelDetailCreature.fill = GridBagConstraints.BOTH;
		gbcpanelDetailCreature.gridx = 1;
		gbcpanelDetailCreature.gridy = 2;
		add(panelDetailCreature, gbcpanelDetailCreature);
		panelDetailCreature.add(loyaltyJTextField);
		panelDetailCreature.add(powerLabel);
		panelDetailCreature.add(powerJTextField);
		panelDetailCreature.add(toughnessLabel);
		panelDetailCreature.add(toughnessJTextField);

		lblnumberInSet = new JLabel("/");
		GridBagConstraints gbclblnumberInSet = new GridBagConstraints();
		gbclblnumberInSet.gridwidth = 2;
		gbclblnumberInSet.insets = new Insets(0, 0, 5, 5);
		gbclblnumberInSet.gridx = 5;
		gbclblnumberInSet.gridy = 2;
		add(lblnumberInSet, gbclblnumberInSet);

		lblLayout = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_LAYOUT") + " :");
		GridBagConstraints gbclblLayout = new GridBagConstraints();
		gbclblLayout.insets = new Insets(0, 0, 5, 5);
		gbclblLayout.gridx = 0;
		gbclblLayout.gridy = 3;
		add(lblLayout, gbclblLayout);

		txtLayoutField = new JTextField();

		GridBagConstraints gbctxtLayoutField = new GridBagConstraints();
		gbctxtLayoutField.insets = new Insets(0, 0, 5, 5);
		gbctxtLayoutField.fill = GridBagConstraints.HORIZONTAL;
		gbctxtLayoutField.gridx = 1;
		gbctxtLayoutField.gridy = 3;
		add(txtLayoutField, gbctxtLayoutField);
		txtLayoutField.setColumns(10);
		GridBagConstraints gbclstFormats = new GridBagConstraints();
		gbclstFormats.gridwidth = 3;
		gbclstFormats.insets = new Insets(0, 0, 5, 5);
		gbclstFormats.fill = GridBagConstraints.HORIZONTAL;
		gbclstFormats.gridx = 4;
		gbclstFormats.gridy = 3;

		chckbxReserved = new JCheckBox("(R)");
		GridBagConstraints gbcchckbxReserved = new GridBagConstraints();
		gbcchckbxReserved.insets = new Insets(0, 0, 5, 5);
		gbcchckbxReserved.gridx = 2;
		gbcchckbxReserved.gridy = 3;
		add(chckbxReserved, gbcchckbxReserved);

		rarityJTextField = new JTextField();
		GridBagConstraints gbcrarityJTextField = new GridBagConstraints();
		gbcrarityJTextField.gridwidth = 3;
		gbcrarityJTextField.insets = new Insets(0, 0, 5, 5);
		gbcrarityJTextField.fill = GridBagConstraints.HORIZONTAL;
		gbcrarityJTextField.gridx = 3;
		gbcrarityJTextField.gridy = 3;
		add(rarityJTextField, gbcrarityJTextField);
		rarityJTextField.setColumns(10);

		lstFormats = new JList<>(new DefaultListModel<MagicFormat>());
		lstFormats.setVisibleRowCount(4);

		scrollLegality = new JScrollPane();
		GridBagConstraints gbcscrollLegality = new GridBagConstraints();
		gbcscrollLegality.gridheight = 2;
		gbcscrollLegality.gridwidth = 2;
		gbcscrollLegality.insets = new Insets(0, 0, 5, 5);
		gbcscrollLegality.fill = GridBagConstraints.BOTH;
		gbcscrollLegality.gridx = 1;
		gbcscrollLegality.gridy = 8;
		add(scrollLegality, gbcscrollLegality);
		scrollLegality.setViewportView(lstFormats);

		JLabel textLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_TEXT") + " :");
		GridBagConstraints labelgbc8 = new GridBagConstraints();
		labelgbc8.gridheight = 2;
		labelgbc8.insets = new Insets(5, 5, 5, 5);
		labelgbc8.gridx = 0;
		labelgbc8.gridy = 4;
		add(textLabel, labelgbc8);

		txtTextPane = new MagicTextPane();
		txtTextPane.setBorder(new LineBorder(Color.GRAY));
		txtTextPane.setBackground(Color.WHITE);

		
		GridBagConstraints gbctxtTextPane = new GridBagConstraints();
		gbctxtTextPane.gridwidth = 6;
		gbctxtTextPane.gridheight = 2;
		gbctxtTextPane.insets = new Insets(5, 0, 5, 5);
		gbctxtTextPane.fill = GridBagConstraints.BOTH;
		gbctxtTextPane.gridx = 1;
		gbctxtTextPane.gridy = 4;
		add(txtTextPane, gbctxtTextPane);

		lblFlavor = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_FLAVOR") + " :");
		GridBagConstraints gbclblFlavor = new GridBagConstraints();
		gbclblFlavor.insets = new Insets(0, 0, 5, 5);
		gbclblFlavor.gridx = 0;
		gbclblFlavor.gridy = 6;
		add(lblFlavor, gbclblFlavor);

		txtFlavorArea = new JTextPane();
		GridBagConstraints gbctxtFlavorArea = new GridBagConstraints();
		gbctxtFlavorArea.gridwidth = 6;
		gbctxtFlavorArea.insets = new Insets(0, 0, 5, 5);
		gbctxtFlavorArea.fill = GridBagConstraints.BOTH;
		gbctxtFlavorArea.gridx = 1;
		gbctxtFlavorArea.gridy = 6;
		add(txtFlavorArea, gbctxtFlavorArea);

		lblArtist = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_ARTIST") + " :");
		GridBagConstraints gbclblArtist = new GridBagConstraints();
		gbclblArtist.insets = new Insets(0, 0, 5, 5);
		gbclblArtist.gridx = 0;
		gbclblArtist.gridy = 7;
		add(lblArtist, gbclblArtist);

		txtArtist = new JTextField();
		GridBagConstraints gbctxtArtist = new GridBagConstraints();
		gbctxtArtist.insets = new Insets(0, 0, 5, 5);
		gbctxtArtist.fill = GridBagConstraints.HORIZONTAL;
		gbctxtArtist.gridx = 1;
		gbctxtArtist.gridy = 7;
		add(txtArtist, gbctxtArtist);
		txtArtist.setColumns(10);

		lblWatermark = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_WATERMARK") + " :");
		GridBagConstraints gbclblWatermark = new GridBagConstraints();
		gbclblWatermark.anchor = GridBagConstraints.EAST;
		gbclblWatermark.insets = new Insets(0, 0, 5, 5);
		gbclblWatermark.gridx = 2;
		gbclblWatermark.gridy = 7;
		add(lblWatermark, gbclblWatermark);

		txtWatermark = new JTextField();

		GridBagConstraints gbctxtWatermark = new GridBagConstraints();
		gbctxtWatermark.gridwidth = 3;
		gbctxtWatermark.insets = new Insets(0, 0, 5, 5);
		gbctxtWatermark.fill = GridBagConstraints.HORIZONTAL;
		gbctxtWatermark.gridx = 4;
		gbctxtWatermark.gridy = 7;
		add(txtWatermark, gbctxtWatermark);
		txtWatermark.setColumns(10);

		lblLegal = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_LEGALITIES") + " :");
		GridBagConstraints gbclblLegal = new GridBagConstraints();
		gbclblLegal.insets = new Insets(0, 0, 5, 5);
		gbclblLegal.gridx = 0;
		gbclblLegal.gridy = 8;
		add(lblLegal, gbclblLegal);

		scrollCollections = new JScrollPane();
		GridBagConstraints gbcscrollCollections = new GridBagConstraints();
		gbcscrollCollections.gridheight = 2;
		gbcscrollCollections.gridwidth = 3;
		gbcscrollCollections.insets = new Insets(0, 0, 5, 5);
		gbcscrollCollections.fill = GridBagConstraints.BOTH;
		gbcscrollCollections.gridx = 4;
		gbcscrollCollections.gridy = 8;
		add(scrollCollections, gbcscrollCollections);

		listModelCollection = new DefaultListModel<>();
		
		listCollection = new JList<>(listModelCollection);
		scrollCollections.setViewportView(listCollection);
		
		panelSwitchLangage = new JPanel();
		FlowLayout flowLayout1 = (FlowLayout) panelSwitchLangage.getLayout();
		flowLayout1.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbcpanelSwitchLangage = new GridBagConstraints();
		gbcpanelSwitchLangage.gridwidth = 9;
		gbcpanelSwitchLangage.insets = new Insets(0, 0, 0, 5);
		gbcpanelSwitchLangage.fill = GridBagConstraints.BOTH;
		gbcpanelSwitchLangage.gridx = 0;
		gbcpanelSwitchLangage.gridy = 10;
		add(panelSwitchLangage, gbcpanelSwitchLangage);
		
		if (magicCard != null) {
			mBindingGroup = initDataBindings();
		}

		setEditable(false);

	}

	public MagicCard getMagicCard() {
		return magicCard;
	}

	public void setMagicCard(MagicCard newMagicCard) {
		setMagicCard(newMagicCard, true);
	}

	public void setMagicCard(MagicCard newMagicCard, boolean update) {
		magicCard = newMagicCard;
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

	public void setMagicLogo(final String set, final String rarity) {
		ThreadManager.getInstance().execute(() -> {
			try {
				lblLogoSet.setIcon(
						new ImageIcon(MTGControler.getInstance().getEnabled(MTGPictureProvider.class).getSetLogo(set, rarity)));
			} catch (Exception e) {
				lblLogoSet.setIcon(null);
			}
		}, "retrieve logo " + set + " " + rarity);

	}

	protected BindingGroup initDataBindings() {
		BeanProperty<MagicCard, Integer> cmcProperty = BeanProperty.create("cmc");
		BeanProperty<JTextField, String> textProperty = BeanProperty.create("text");
		AutoBinding<MagicCard, Integer, JTextField, String> autoBinding = Bindings
				.createAutoBinding(UpdateStrategy.READ, magicCard, cmcProperty, cmcJTextField, textProperty);
		autoBinding.bind();
		//
		BeanProperty<MagicCard, String> costProperty = BeanProperty.create("cost");
		BeanProperty<ManaPanel, String> textProperty1 = BeanProperty.create("manaCost");
		AutoBinding<MagicCard, String, ManaPanel, String> autoBinding1 = Bindings
				.createAutoBinding(UpdateStrategy.READ, magicCard, costProperty, manaPanel, textProperty1);
		autoBinding1.bind();
		//
		BeanProperty<MagicCard, String> fullTypeProperty = BeanProperty.create("fullType");
		BeanProperty<JTextField, String> textProperty2 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding2 = Bindings.createAutoBinding(
				UpdateStrategy.READ, magicCard, fullTypeProperty, fullTypeJTextField, textProperty2);
		autoBinding2.bind();
		//
		BeanProperty<MagicCard, Integer> loyaltyProperty = BeanProperty.create("loyalty");
		BeanProperty<JTextField, String> textProperty4 = BeanProperty.create("text");
		AutoBinding<MagicCard, Integer, JTextField, String> autoBinding4 = Bindings.createAutoBinding(
				UpdateStrategy.READ, magicCard, loyaltyProperty, loyaltyJTextField, textProperty4);
		autoBinding4.bind();
		//
		BeanProperty<MagicCard, String> nameProperty = BeanProperty.create("name");
		BeanProperty<JTextField, String> textProperty5 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding5 = Bindings
				.createAutoBinding(UpdateStrategy.READ, magicCard, nameProperty, nameJTextField, textProperty5);
		autoBinding5.bind();
		//
		BeanProperty<MagicCard, String> powerProperty = BeanProperty.create("power");
		BeanProperty<JTextField, String> textProperty6 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding6 = Bindings
				.createAutoBinding(UpdateStrategy.READ, magicCard, powerProperty, powerJTextField, textProperty6);
		autoBinding6.bind();
		//
		BeanProperty<MagicCard, String> textProperty8 = BeanProperty.create("text");
		BeanProperty<MagicTextPane, String> textProperty9 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, MagicTextPane, String> autoBinding8 = Bindings
				.createAutoBinding(UpdateStrategy.READ, magicCard, textProperty8, txtTextPane, textProperty9);
		autoBinding8.bind();
		//
		BeanProperty<MagicCard, String> toughnessProperty = BeanProperty.create("toughness");
		BeanProperty<JTextField, String> textProperty10 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding9 = Bindings.createAutoBinding(
				UpdateStrategy.READ, magicCard, toughnessProperty, toughnessJTextField, textProperty10);
		autoBinding9.bind();

		BeanProperty<MagicCard, String> flavorProperty = BeanProperty.create("flavor");
		BeanProperty<JTextPane, String> textProperty11 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextPane, String> autoBinding10 = Bindings
				.createAutoBinding(UpdateStrategy.READ, magicCard, flavorProperty, txtFlavorArea, textProperty11);
		autoBinding10.bind();

		BeanProperty<MagicCard, String> artistProperty = BeanProperty.create("artist");
		BeanProperty<JTextField, String> textProperty12 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding11 = Bindings
				.createAutoBinding(UpdateStrategy.READ, magicCard, artistProperty, txtArtist, textProperty12);
		autoBinding11.bind();

		BeanProperty<MagicCard, String> layoutProperty = BeanProperty.create("layout");
		BeanProperty<JTextField, String> textProperty13 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding12 = Bindings.createAutoBinding(
				UpdateStrategy.READ, magicCard, layoutProperty, txtLayoutField, textProperty13);
		autoBinding12.bind();

		BeanProperty<MagicCard, String> waterProperty = BeanProperty.create("watermarks");
		BeanProperty<JTextField, String> textProperty14 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding13 = Bindings
				.createAutoBinding(UpdateStrategy.READ, magicCard, waterProperty, txtWatermark, textProperty14);
		autoBinding13.bind();

		BeanProperty<MagicCard, Boolean> reservedProperty = BeanProperty.create("reserved");
		BeanProperty<JCheckBox, Boolean> chkProperty15 = BeanProperty.create("selected");
		AutoBinding<MagicCard, Boolean, JCheckBox, Boolean> autoBinding15 = Bindings.createAutoBinding(
				UpdateStrategy.READ, magicCard, reservedProperty, chckbxReserved, chkProperty15);
		autoBinding15.bind();

		try {
			if (magicCard != null)
				rarityJTextField.setText(magicCard.getCurrentSet().getRarity());
		} catch (Exception e) {
			rarityJTextField.setText("");
		}

		txtTextPane.updateTextWithIcons();

		if (thumbnail && magicCard != null)
		{
			ThreadManager.getInstance().execute(() -> loadPics(magicCard),"load pics");
		}

		if (magicCard != null && !magicCard.getEditions().isEmpty()) {

			ThreadManager.getInstance().execute(() -> {
				setMagicLogo(magicCard.getCurrentSet().getId(), magicCard.getCurrentSet().getRarity());
				
				int showCount = magicCard.getCurrentSet().getCardCountOfficial();
				if(showCount==0)
					showCount=magicCard.getCurrentSet().getCardCount();
				
				lblnumberInSet.setText(magicCard.getCurrentSet().getNumber() + "/"+ showCount);
			}, "loadLogo");
		}

		if (magicCard != null && enableCollectionLookup && !magicCard.getEditions().isEmpty())
			ThreadManager.getInstance().execute(() -> {
				try {
					listModelCollection.removeAllElements();
					MTGControler.getInstance().getEnabled(MTGDao.class).listCollectionFromCards(magicCard).forEach(col->listModelCollection.addElement(col));
					
				} catch (Exception e) {
					logger.error(e);
				}
			}, "loadCollections");

		if (magicCard != null && enableCollectionLookup)
			ThreadManager.getInstance().execute(() -> {
				if (MTGControler.getInstance().getEnabled(MTGDao.class).hasAlert(magicCard)) {
					btnAlert.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("HAD_ALERT"));
					btnAlert.setEnabled(false);
				} else {
					btnAlert.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("ADD_ALERT_FOR",
							magicCard.getName()));
					btnAlert.setEnabled(true);
				}
			}, "Get alerts for " + magicCard);

		((DefaultListModel) lstFormats.getModel()).removeAllElements();

		if (magicCard != null)
			for (MagicFormat mf : magicCard.getLegalities())
				((DefaultListModel) lstFormats.getModel()).addElement(mf);

		ButtonGroup group = new ButtonGroup();
	
		
		if(magicCard!=null)
		{
			
				panelSwitchLangage.removeAll();
				panelSwitchLangage.revalidate();
			
				SwingWorker<Void, MagicCardNames> sw = new SwingWorker<Void, MagicCardNames>(){

					@Override
					protected void process(List<MagicCardNames> chunks) {
						
						chunks.forEach(fn->{
							JToggleButton tglLangButton = new JToggleButton(fn.getLanguage());
							tglLangButton.setActionCommand(fn.getLanguage());
							AbstractAction act = new AbstractAction() {
								private static final long serialVersionUID = 1L;
								@Override
								public void actionPerformed(ActionEvent ae) {
									txtTextPane.setText(fn.getText());
									txtTextPane.updateTextWithIcons();
									nameJTextField.setText(fn.getName());
									fullTypeJTextField.setText(fn.getType());
									txtFlavorArea.setText(fn.getFlavor());
									if (thumbnail)
									{
										ThreadManager.getInstance().execute(() -> loadPics(fn,magicCard),"load pics");
									}
									
									
									
								}
							};
							act.putValue(Action.NAME, fn.getLanguage());
							
							tglLangButton.setActionCommand(fn.getLanguage());
							tglLangButton.setAction(act);
							group.add(tglLangButton);
							panelSwitchLangage.add(tglLangButton);
							
							if(fn.getGathererId()>0 && fn.getLanguage().equalsIgnoreCase(MTGControler.getInstance().get("langage")))
								tglLangButton.doClick();
							
						});
					
					
					
					}

					@Override
					protected Void doInBackground() throws Exception {
						publish(magicCard.getForeignNames().toArray(new MagicCardNames[magicCard.getForeignNames().size()]));
						return null;
					}
				};
				
				ThreadManager.getInstance().runInEdt(sw);
			
		}
		

		
		
		
		//
		BindingGroup bindingGroup = new BindingGroup();
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
		bindingGroup.addBinding(autoBinding12);
		bindingGroup.addBinding(autoBinding13);
		bindingGroup.addBinding(autoBinding15);
		return bindingGroup;
	}

	protected void loadPics(MagicCard mc) {
		ImageIcon icon;
		try {
			icon = new ImageIcon(MTGControler.getInstance().getEnabled(MTGPictureProvider.class).getPicture(mc, null));
		} catch (Exception e) {
			icon = new ImageIcon(MTGControler.getInstance().getEnabled(MTGPictureProvider.class).getBackPicture());
			logger.error("Error loading pics for" + mc, e);
		}
		lblThumbnail.setIcon(icon);
		repaint();
	}
	
	protected void loadPics(MagicCardNames fn,MagicCard mc) {
		ImageIcon icon;
		try {
			icon = new ImageIcon(MTGControler.getInstance().getEnabled(MTGPictureProvider.class).getForeignNamePicture(fn, mc));
		} catch (Exception e) {
			icon = new ImageIcon(MTGControler.getInstance().getEnabled(MTGPictureProvider.class).getBackPicture());
			logger.error("Error loading pics for" + mc, e);
		}
		lblThumbnail.setIcon(icon);
		repaint();
	}
	
	
	@Override
	public void update(Observable o, Object ob) {
		setMagicCard((MagicCard) ob);

	}

	public void enableCollectionLookup(boolean b) {
		enableCollectionLookup = b;

	}

}
