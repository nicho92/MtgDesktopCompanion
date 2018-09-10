package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.magic.api.beans.MagicEdition;
import org.magic.gui.models.EditionsShakerTableModel;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;

public class MagicEditionDetailPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient BindingGroup mBindingGroup;
	private org.magic.api.beans.MagicEdition magicEdition = new org.magic.api.beans.MagicEdition();
	private JTextField borderJTextField;
	private JTextField cardCountTextField;
	private JTextField releaseDateJTextField;
	private JTextField setJTextField;
	private JTextField typeJTextField;

	private JTextField blockJTextField;
	private JTextField idJtextField;
	private EditionsShakerTableModel mod;
	private boolean showPrices;
	private JCheckBox chkOnline;
	private BoosterPicsPanel lblBoosterPic;
	private boolean openBooster;

	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public MagicEditionDetailPanel(boolean showTablePrice, boolean openBooster) {
		this.showPrices = showTablePrice;
		this.openBooster = openBooster;

		initGUI();
	}

	public MagicEditionDetailPanel() {
		showPrices = true;
		openBooster = true;
		initGUI();
	}

	public void initGUI() {

		JSplitPane splitPane;
		JPanel panneauBooster;
		JButton btnOpenBooster;
		JLabel lblOnlineSet;
		JPanel panneauHaut;
		JLabel lblBlock;
		JLabel lblId;
		JTable table;
		splitPane = new JSplitPane();
		panneauHaut = new JPanel();

		
		setLayout(new BorderLayout(0, 0));

		
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

		this.add(splitPane);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 104, 333, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4 };
		panneauHaut.setLayout(gridBagLayout);

		JLabel setLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("EDITION") + " :");
		GridBagConstraints labelgbc8 = new GridBagConstraints();
		labelgbc8.insets = new Insets(5, 5, 5, 5);
		labelgbc8.gridx = 0;
		labelgbc8.gridy = 0;
		panneauHaut.add(setLabel, labelgbc8);

		setJTextField = new JTextField();

		GridBagConstraints componentGbc8 = new GridBagConstraints();
		componentGbc8.insets = new Insets(5, 0, 5, 5);
		componentGbc8.fill = GridBagConstraints.HORIZONTAL;
		componentGbc8.gridx = 1;
		componentGbc8.gridy = 0;
		panneauHaut.add(setJTextField, componentGbc8);

		JLabel typeLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("EDITION_TYPE") + " :");
		GridBagConstraints labelgbc11 = new GridBagConstraints();
		labelgbc11.insets = new Insets(5, 5, 5, 5);
		labelgbc11.gridx = 0;
		labelgbc11.gridy = 1;
		panneauHaut.add(typeLabel, labelgbc11);

		typeJTextField = new JTextField();
		GridBagConstraints componentGbc11 = new GridBagConstraints();
		componentGbc11.insets = new Insets(5, 0, 5, 5);
		componentGbc11.fill = GridBagConstraints.HORIZONTAL;
		componentGbc11.gridx = 1;
		componentGbc11.gridy = 1;
		panneauHaut.add(typeJTextField, componentGbc11);

		JLabel releaseDateLabel = new JLabel(
				MTGControler.getInstance().getLangService().getCapitalize("DATE_RELEASE") + " :");
		GridBagConstraints labelgbc7 = new GridBagConstraints();
		labelgbc7.insets = new Insets(5, 5, 5, 5);
		labelgbc7.gridx = 0;
		labelgbc7.gridy = 2;
		panneauHaut.add(releaseDateLabel, labelgbc7);

		releaseDateJTextField = new JTextField();

		GridBagConstraints componentGbc7 = new GridBagConstraints();
		componentGbc7.insets = new Insets(5, 0, 5, 5);
		componentGbc7.fill = GridBagConstraints.HORIZONTAL;
		componentGbc7.gridx = 1;
		componentGbc7.gridy = 2;
		panneauHaut.add(releaseDateJTextField, componentGbc7);

		JLabel borderLabel = new JLabel(
				MTGControler.getInstance().getLangService().getCapitalize("EDITION_BORDER") + " :");
		GridBagConstraints labelgbc2 = new GridBagConstraints();
		labelgbc2.insets = new Insets(5, 5, 5, 5);
		labelgbc2.gridx = 0;
		labelgbc2.gridy = 3;
		panneauHaut.add(borderLabel, labelgbc2);

		borderJTextField = new JTextField();
		GridBagConstraints componentGbc2 = new GridBagConstraints();
		componentGbc2.fill = GridBagConstraints.HORIZONTAL;
		componentGbc2.insets = new Insets(5, 0, 5, 5);
		componentGbc2.gridx = 1;
		componentGbc2.gridy = 3;
		panneauHaut.add(borderJTextField, componentGbc2);

		JLabel cardCountLabel = new JLabel(
				MTGControler.getInstance().getLangService().getCapitalize("EDITION_CARD_COUNT") + " :");
		GridBagConstraints labelgbc3 = new GridBagConstraints();
		labelgbc3.insets = new Insets(5, 5, 5, 5);
		labelgbc3.gridx = 0;
		labelgbc3.gridy = 4;
		panneauHaut.add(cardCountLabel, labelgbc3);

		cardCountTextField = new JTextField();

		GridBagConstraints componentGbc3 = new GridBagConstraints();
		componentGbc3.insets = new Insets(5, 0, 5, 5);
		componentGbc3.fill = GridBagConstraints.HORIZONTAL;
		componentGbc3.gridx = 1;
		componentGbc3.gridy = 4;
		panneauHaut.add(cardCountTextField, componentGbc3);

		lblBlock = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("EDITION_BLOCK") + " :");
		GridBagConstraints gbclblBlock = new GridBagConstraints();
		gbclblBlock.insets = new Insets(0, 0, 5, 5);
		gbclblBlock.gridx = 0;
		gbclblBlock.gridy = 5;
		panneauHaut.add(lblBlock, gbclblBlock);

		blockJTextField = new JTextField();
		GridBagConstraints gbcblockJTextField = new GridBagConstraints();
		gbcblockJTextField.insets = new Insets(0, 0, 5, 5);
		gbcblockJTextField.fill = GridBagConstraints.HORIZONTAL;
		gbcblockJTextField.gridx = 1;
		gbcblockJTextField.gridy = 5;
		panneauHaut.add(blockJTextField, gbcblockJTextField);
		blockJTextField.setColumns(10);

		lblId = new JLabel("ID :");
		GridBagConstraints gbclblId = new GridBagConstraints();
		gbclblId.insets = new Insets(0, 0, 5, 5);
		gbclblId.gridx = 0;
		gbclblId.gridy = 6;
		panneauHaut.add(lblId, gbclblId);

		idJtextField = new JTextField();

		GridBagConstraints gbctxtID = new GridBagConstraints();
		gbctxtID.insets = new Insets(0, 0, 5, 5);
		gbctxtID.fill = GridBagConstraints.HORIZONTAL;
		gbctxtID.gridx = 1;
		gbctxtID.gridy = 6;
		panneauHaut.add(idJtextField, gbctxtID);
		idJtextField.setColumns(10);

		splitPane.setLeftComponent(panneauHaut);

		lblOnlineSet = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("EDITION_ONLINE") + " :");
		GridBagConstraints gbclblOnlineSet = new GridBagConstraints();
		gbclblOnlineSet.insets = new Insets(0, 0, 5, 5);
		gbclblOnlineSet.gridx = 0;
		gbclblOnlineSet.gridy = 7;
		panneauHaut.add(lblOnlineSet, gbclblOnlineSet);

		chkOnline = new JCheckBox("");
		GridBagConstraints gbcchkOnline = new GridBagConstraints();
		gbcchkOnline.anchor = GridBagConstraints.WEST;
		gbcchkOnline.insets = new Insets(0, 0, 5, 5);
		gbcchkOnline.gridx = 1;
		gbcchkOnline.gridy = 7;
		panneauHaut.add(chkOnline, gbcchkOnline);

		panneauBooster = new JPanel();
		add(panneauBooster, BorderLayout.EAST);
		panneauBooster.setLayout(new BorderLayout(0, 0));

		if (openBooster) {
			btnOpenBooster = new JButton(
					MTGControler.getInstance().getLangService().getCapitalize("OPEN_BOOSTER") + " :");
			panneauBooster.add(btnOpenBooster, BorderLayout.NORTH);
			btnOpenBooster.addActionListener(ae -> {
				try {
					CardSearchPanel.getInstance().thumbnail(
							MTGControler.getInstance().getEnabledCardsProviders().generateBooster(magicEdition).getCards());
				} catch (Exception e) {
					logger.error("Error loading booster for " + magicEdition, e);
				}
			});

		}

		lblBoosterPic = new BoosterPicsPanel();
		panneauBooster.add(lblBoosterPic);

		if (showPrices) {
			mod = new EditionsShakerTableModel();
			table = new JTable(mod);
			table.setRowSorter(new TableRowSorter(mod));
			splitPane.setRightComponent(new JScrollPane(table));
		} else {
			splitPane.setRightComponent(null);
		}

		if (magicEdition != null) {
			mBindingGroup = initDataBindings();
		}

		setEditable(false);
	}

	public void setEditable(boolean b) {
		idJtextField.setEditable(b);
		blockJTextField.setEditable(b);
		borderJTextField.setEditable(b);
		cardCountTextField.setEditable(b);
		releaseDateJTextField.setEditable(b);
		typeJTextField.setEditable(b);
		setJTextField.setEditable(b);
		chkOnline.setEnabled(b);
	}

	public org.magic.api.beans.MagicEdition getMagicEdition() {
		return magicEdition;
	}

	public void setMagicEdition(MagicEdition newMagicEdition) {
		setMagicEdition(newMagicEdition, true);
	}

	public void setMagicEdition(MagicEdition newMagicEdition, boolean update) {
		magicEdition = newMagicEdition;
		if (update) {
			if (mBindingGroup != null) {
				mBindingGroup.unbind();
				mBindingGroup = null;
			}
			if (magicEdition != null) {
				mBindingGroup = initDataBindings();
			}
		}

		if (showPrices) {
			ThreadManager.getInstance().execute(() -> {
				mod.init(magicEdition);
				mod.fireTableDataChanged();
			}, "load prices for" + magicEdition);
		}

		lblBoosterPic.setEdition(magicEdition);

	}

	protected BindingGroup initDataBindings() {
		BeanProperty<MagicEdition, String> borderProperty = BeanProperty.create("border");
		BeanProperty<JTextField, String> textProperty2 = BeanProperty.create("text");
		AutoBinding<MagicEdition, String, JTextField, String> autoBinding2 = Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE, magicEdition, borderProperty, borderJTextField, textProperty2);
		autoBinding2.bind();
		//
		BeanProperty<MagicEdition, Integer> cardCountProperty = BeanProperty.create("cardCount");
		BeanProperty<JTextField, String> valueProperty = BeanProperty.create("text");
		AutoBinding<MagicEdition, Integer, JTextField, String> autoBinding3 = Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE, magicEdition, cardCountProperty, cardCountTextField, valueProperty);
		autoBinding3.bind();
		//
		BeanProperty<MagicEdition, String> releaseDateProperty = BeanProperty.create("releaseDate");
		BeanProperty<JTextField, String> textProperty6 = BeanProperty.create("text");
		AutoBinding<MagicEdition, String, JTextField, String> autoBinding7 = Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE, magicEdition, releaseDateProperty, releaseDateJTextField, textProperty6);
		autoBinding7.bind();
		//
		BeanProperty<MagicEdition, String> setProperty = BeanProperty.create("set");
		BeanProperty<JTextField, String> textProperty7 = BeanProperty.create("text");
		AutoBinding<MagicEdition, String, JTextField, String> autoBinding8 = Bindings
				.createAutoBinding(UpdateStrategy.READ_WRITE, magicEdition, setProperty, setJTextField, textProperty7);
		autoBinding8.bind();
		//
		BeanProperty<MagicEdition, String> typeProperty = BeanProperty.create("type");
		BeanProperty<JTextField, String> textProperty10 = BeanProperty.create("text");
		AutoBinding<MagicEdition, String, JTextField, String> autoBinding11 = Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE, magicEdition, typeProperty, typeJTextField, textProperty10);
		autoBinding11.bind();

		BeanProperty<MagicEdition, String> blockProperty = BeanProperty.create("block");
		BeanProperty<JTextField, String> textProperty11 = BeanProperty.create("text");
		AutoBinding<MagicEdition, String, JTextField, String> autoBinding12 = Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE, magicEdition, blockProperty, blockJTextField, textProperty11);
		autoBinding12.bind();

		BeanProperty<MagicEdition, String> idProperty = BeanProperty.create("id");
		BeanProperty<JTextField, String> textProperty12 = BeanProperty.create("text");
		AutoBinding<MagicEdition, String, JTextField, String> autoBinding13 = Bindings
				.createAutoBinding(UpdateStrategy.READ_WRITE, magicEdition, idProperty, idJtextField, textProperty12);
		autoBinding13.bind();

		BeanProperty<MagicEdition, Boolean> onlineProperty = BeanProperty.create("onlineOnly");
		BeanProperty<JCheckBox, Boolean> chkProperty13 = BeanProperty.create("selected");
		AutoBinding<MagicEdition, Boolean, JCheckBox, Boolean> autoBinding14 = Bindings
				.createAutoBinding(UpdateStrategy.READ_WRITE, magicEdition, onlineProperty, chkOnline, chkProperty13);
		autoBinding14.bind();

		//
		BindingGroup bindingGroup = new BindingGroup();
		//
		bindingGroup.addBinding(autoBinding2);
		bindingGroup.addBinding(autoBinding3);
		bindingGroup.addBinding(autoBinding7);
		bindingGroup.addBinding(autoBinding8);
		bindingGroup.addBinding(autoBinding11);
		bindingGroup.addBinding(autoBinding12);
		bindingGroup.addBinding(autoBinding13);
		bindingGroup.addBinding(autoBinding14);
		return bindingGroup;
	}
}
