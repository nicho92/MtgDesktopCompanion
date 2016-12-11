package org.magic.gui.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.AbstractListModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.JTextField;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.magic.api.beans.MagicCard;

public class MagicCardEditorPanel extends JPanel {

	private BindingGroup m_bindingGroup;
	private org.magic.api.beans.MagicCard magicCard = new org.magic.api.beans.MagicCard();
	private JTextField artistJTextField;
	private JTextField costJTextField;
	private JTextField flavorJTextField;
	private JCheckBox flippableJCheckBox;
	private JTextField gathererCodeJTextField;
	private JComboBox<String> layoutJComboBox;
	private JTextField loyaltyJTextField;
	private JTextField mciNumberJTextField;
	private JTextField nameJTextField;
	private JTextField numberJTextField;
	private JTextField powerJTextField;
	private JComboBox<String> rarityJComboBox;
	private JTextField rotatedCardNameJTextField;
	private JEditorPane textJEditorPane;
	private JTextField toughnessJTextField;
	private JCheckBox tranformableJCheckBox;
	private JTextField watermarksJTextField;
	private JPanel panel;
	private JLabel label;
	private JLabel lblType;
	private JPanel panel_1;
	private JComboBox<String> cboSuperType;
	private JList<? extends String> list;
	private JScrollPane scrollPane;
	private JTextField txtSubTypes;

	
	public MagicCardEditorPanel(org.magic.api.beans.MagicCard newMagicCard) {
		this();
		setMagicCard(newMagicCard);
	}

	public MagicCardEditorPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 192, 122, 103, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 31, 0, 56, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4 };
		setLayout(gridBagLayout);
		
				JLabel nameLabel = new JLabel("Name:");
				GridBagConstraints labelGbc_10 = new GridBagConstraints();
				labelGbc_10.insets = new Insets(5, 5, 5, 5);
				labelGbc_10.gridx = 0;
				labelGbc_10.gridy = 0;
				add(nameLabel, labelGbc_10);
		
				nameJTextField = new JTextField();
				GridBagConstraints componentGbc_10 = new GridBagConstraints();
				componentGbc_10.insets = new Insets(5, 0, 5, 5);
				componentGbc_10.fill = GridBagConstraints.HORIZONTAL;
				componentGbc_10.gridx = 1;
				componentGbc_10.gridy = 0;
				add(nameJTextField, componentGbc_10);
		
				JLabel costLabel = new JLabel("Cost:");
				GridBagConstraints labelGbc_2 = new GridBagConstraints();
				labelGbc_2.insets = new Insets(5, 5, 5, 5);
				labelGbc_2.gridx = 2;
				labelGbc_2.gridy = 0;
				add(costLabel, labelGbc_2);
		
				costJTextField = new JTextField();
				GridBagConstraints componentGbc_2 = new GridBagConstraints();
				componentGbc_2.insets = new Insets(5, 0, 5, 0);
				componentGbc_2.fill = GridBagConstraints.HORIZONTAL;
				componentGbc_2.gridx = 3;
				componentGbc_2.gridy = 0;
				add(costJTextField, componentGbc_2);

		JLabel artistLabel = new JLabel("Artist:");
		GridBagConstraints labelGbc_0 = new GridBagConstraints();
		labelGbc_0.insets = new Insets(5, 5, 5, 5);
		labelGbc_0.gridx = 0;
		labelGbc_0.gridy = 1;
		add(artistLabel, labelGbc_0);

		artistJTextField = new JTextField();
		GridBagConstraints componentGbc_0 = new GridBagConstraints();
		componentGbc_0.insets = new Insets(5, 0, 5, 5);
		componentGbc_0.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_0.gridx = 1;
		componentGbc_0.gridy = 1;
		add(artistJTextField, componentGbc_0);
				
						JLabel rarityLabel = new JLabel("Rarity:");
						GridBagConstraints labelGbc_14 = new GridBagConstraints();
						labelGbc_14.insets = new Insets(5, 5, 5, 5);
						labelGbc_14.gridx = 2;
						labelGbc_14.gridy = 1;
						add(rarityLabel, labelGbc_14);
				
						rarityJComboBox = new JComboBox();
						rarityJComboBox.setModel(new DefaultComboBoxModel(new String[] {"Common", "Uncommon", "Rare", "Mythic Rare", "Special"}));
						GridBagConstraints componentGbc_14 = new GridBagConstraints();
						componentGbc_14.insets = new Insets(5, 0, 5, 0);
						componentGbc_14.fill = GridBagConstraints.HORIZONTAL;
						componentGbc_14.gridx = 3;
						componentGbc_14.gridy = 1;
						add(rarityJComboBox, componentGbc_14);
				
				lblType = new JLabel("Type :");
				GridBagConstraints gbc_lblType = new GridBagConstraints();
				gbc_lblType.insets = new Insets(0, 0, 5, 5);
				gbc_lblType.gridx = 0;
				gbc_lblType.gridy = 2;
				add(lblType, gbc_lblType);
				
				panel_1 = new JPanel();
				GridBagConstraints gbc_panel_1 = new GridBagConstraints();
				gbc_panel_1.gridwidth = 3;
				gbc_panel_1.insets = new Insets(0, 0, 5, 0);
				gbc_panel_1.fill = GridBagConstraints.BOTH;
				gbc_panel_1.gridx = 1;
				gbc_panel_1.gridy = 2;
				add(panel_1, gbc_panel_1);
				GridBagLayout gbl_panel_1 = new GridBagLayout();
				gbl_panel_1.columnWidths = new int[]{82, 76, 63, 0, 0};
				gbl_panel_1.rowHeights = new int[]{20, 0};
				gbl_panel_1.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
				gbl_panel_1.rowWeights = new double[]{0.0, Double.MIN_VALUE};
				panel_1.setLayout(gbl_panel_1);
				
				cboSuperType = new JComboBox();
				cboSuperType.setModel(new DefaultComboBoxModel(new String[] {"", "Basic", "Elite", "Legendary", "Ongoing", "Snow", "World"}));
				GridBagConstraints gbc_cboSuperType = new GridBagConstraints();
				gbc_cboSuperType.anchor = GridBagConstraints.NORTHWEST;
				gbc_cboSuperType.insets = new Insets(0, 0, 0, 5);
				gbc_cboSuperType.gridx = 0;
				gbc_cboSuperType.gridy = 0;
				panel_1.add(cboSuperType, gbc_cboSuperType);
				
				scrollPane = new JScrollPane();
				GridBagConstraints gbc_scrollPane = new GridBagConstraints();
				gbc_scrollPane.fill = GridBagConstraints.VERTICAL;
				gbc_scrollPane.gridwidth = 2;
				gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
				gbc_scrollPane.anchor = GridBagConstraints.WEST;
				gbc_scrollPane.gridx = 1;
				gbc_scrollPane.gridy = 0;
				panel_1.add(scrollPane, gbc_scrollPane);
				
				list = new JList<String>();
				list.setVisibleRowCount(3);
				list.setModel(new AbstractListModel() {
					String[] values = new String[] {"", "Arcane", "Artifact", "Aura", "Basic", "Clue", "Conspiracy", "Continuous", "Contraption", "Creature", "Curse", "Elite", "Enchantment", "Equipment", "Fortification", "Global enchantment", "Hero", "Instant", "Interrupt", "Land", "Legendary", "Local", "Mana source", "Mono", "Ongoing", "Permanent", "Phenomenon", "Plane", "Planeswalker", "Poly", "Scheme", "Shrine", "Snow", "Sorcery", "Spell", "Summon", "Trap", "Tribal", "Vanguard", "Vehicle", "World"};
					public int getSize() {
						return values.length;
					}
					public Object getElementAt(int index) {
						return values[index];
					}
				});
				scrollPane.setViewportView(list);
				
				txtSubTypes = new JTextField();
				GridBagConstraints gbc_txtSubTypes = new GridBagConstraints();
				gbc_txtSubTypes.fill = GridBagConstraints.HORIZONTAL;
				gbc_txtSubTypes.gridx = 3;
				gbc_txtSubTypes.gridy = 0;
				panel_1.add(txtSubTypes, gbc_txtSubTypes);
				txtSubTypes.setColumns(10);
		
				JLabel textLabel = new JLabel("Text:");
				GridBagConstraints labelGbc_16 = new GridBagConstraints();
				labelGbc_16.insets = new Insets(5, 5, 5, 5);
				labelGbc_16.gridx = 0;
				labelGbc_16.gridy = 3;
				add(textLabel, labelGbc_16);
		
				textJEditorPane = new JEditorPane();
				GridBagConstraints componentGbc_16 = new GridBagConstraints();
				componentGbc_16.gridwidth = 3;
				componentGbc_16.gridheight = 2;
				componentGbc_16.insets = new Insets(5, 0, 5, 0);
				componentGbc_16.fill = GridBagConstraints.BOTH;
				componentGbc_16.gridx = 1;
				componentGbc_16.gridy = 3;
				add(textJEditorPane, componentGbc_16);

		JLabel flavorLabel = new JLabel("Flavor:");
		GridBagConstraints labelGbc_3 = new GridBagConstraints();
		labelGbc_3.insets = new Insets(5, 5, 5, 5);
		labelGbc_3.gridx = 0;
		labelGbc_3.gridy = 5;
		add(flavorLabel, labelGbc_3);

		flavorJTextField = new JTextField();
		GridBagConstraints componentGbc_3 = new GridBagConstraints();
		componentGbc_3.gridwidth = 3;
		componentGbc_3.insets = new Insets(5, 0, 5, 0);
		componentGbc_3.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_3.gridx = 1;
		componentGbc_3.gridy = 5;
		add(flavorJTextField, componentGbc_3);
				
						JLabel layoutLabel = new JLabel("Layout:");
						GridBagConstraints labelGbc_6 = new GridBagConstraints();
						labelGbc_6.insets = new Insets(5, 5, 5, 5);
						labelGbc_6.gridx = 0;
						labelGbc_6.gridy = 6;
						add(layoutLabel, labelGbc_6);
				
						layoutJComboBox = new JComboBox();
						GridBagConstraints componentGbc_6 = new GridBagConstraints();
						componentGbc_6.insets = new Insets(5, 0, 5, 5);
						componentGbc_6.fill = GridBagConstraints.HORIZONTAL;
						componentGbc_6.gridx = 1;
						componentGbc_6.gridy = 6;
						add(layoutJComboBox, componentGbc_6);
						
								JLabel powerLabel = new JLabel("Power / Toughness :");
								GridBagConstraints labelGbc_13 = new GridBagConstraints();
								labelGbc_13.insets = new Insets(5, 5, 5, 5);
								labelGbc_13.gridx = 2;
								labelGbc_13.gridy = 6;
								add(powerLabel, labelGbc_13);
						
						panel = new JPanel();
						GridBagConstraints gbc_panel = new GridBagConstraints();
						gbc_panel.insets = new Insets(0, 0, 5, 0);
						gbc_panel.fill = GridBagConstraints.BOTH;
						gbc_panel.gridx = 3;
						gbc_panel.gridy = 6;
						add(panel, gbc_panel);
						
								powerJTextField = new JTextField();
								powerJTextField.setColumns(2);
								panel.add(powerJTextField);
										
										label = new JLabel("/");
										panel.add(label);
								
										toughnessJTextField = new JTextField();
										toughnessJTextField.setColumns(2);
										panel.add(toughnessJTextField);
												
														JLabel watermarksLabel = new JLabel("Watermarks:");
														GridBagConstraints labelGbc_19 = new GridBagConstraints();
														labelGbc_19.insets = new Insets(5, 5, 5, 5);
														labelGbc_19.gridx = 0;
														labelGbc_19.gridy = 7;
														add(watermarksLabel, labelGbc_19);
												
														watermarksJTextField = new JTextField();
														GridBagConstraints componentGbc_19 = new GridBagConstraints();
														componentGbc_19.insets = new Insets(5, 0, 5, 5);
														componentGbc_19.fill = GridBagConstraints.HORIZONTAL;
														componentGbc_19.gridx = 1;
														componentGbc_19.gridy = 7;
														add(watermarksJTextField, componentGbc_19);
										
												JLabel loyaltyLabel = new JLabel("Loyalty:");
												GridBagConstraints labelGbc_7 = new GridBagConstraints();
												labelGbc_7.insets = new Insets(5, 5, 5, 5);
												labelGbc_7.gridx = 2;
												labelGbc_7.gridy = 7;
												add(loyaltyLabel, labelGbc_7);
								
										loyaltyJTextField = new JTextField();
										GridBagConstraints componentGbc_7 = new GridBagConstraints();
										componentGbc_7.insets = new Insets(5, 0, 5, 0);
										componentGbc_7.fill = GridBagConstraints.HORIZONTAL;
										componentGbc_7.gridx = 3;
										componentGbc_7.gridy = 7;
										add(loyaltyJTextField, componentGbc_7);
						
								JLabel numberLabel = new JLabel("Number:");
								GridBagConstraints labelGbc_11 = new GridBagConstraints();
								labelGbc_11.insets = new Insets(5, 5, 5, 5);
								labelGbc_11.gridx = 0;
								labelGbc_11.gridy = 8;
								add(numberLabel, labelGbc_11);
						
								numberJTextField = new JTextField();
								GridBagConstraints componentGbc_11 = new GridBagConstraints();
								componentGbc_11.insets = new Insets(5, 0, 5, 5);
								componentGbc_11.fill = GridBagConstraints.HORIZONTAL;
								componentGbc_11.gridx = 1;
								componentGbc_11.gridy = 8;
								add(numberJTextField, componentGbc_11);
				
						JLabel tranformableLabel = new JLabel("Tranformable:");
						GridBagConstraints labelGbc_18 = new GridBagConstraints();
						labelGbc_18.insets = new Insets(5, 5, 5, 5);
						labelGbc_18.gridx = 2;
						labelGbc_18.gridy = 8;
						add(tranformableLabel, labelGbc_18);
		
				tranformableJCheckBox = new JCheckBox();
				GridBagConstraints componentGbc_18 = new GridBagConstraints();
				componentGbc_18.insets = new Insets(5, 0, 5, 0);
				componentGbc_18.fill = GridBagConstraints.HORIZONTAL;
				componentGbc_18.gridx = 3;
				componentGbc_18.gridy = 8;
				add(tranformableJCheckBox, componentGbc_18);
				
						JLabel mciNumberLabel = new JLabel("MciNumber:");
						GridBagConstraints labelGbc_8 = new GridBagConstraints();
						labelGbc_8.insets = new Insets(5, 5, 5, 5);
						labelGbc_8.gridx = 0;
						labelGbc_8.gridy = 9;
						add(mciNumberLabel, labelGbc_8);
				
						mciNumberJTextField = new JTextField();
						GridBagConstraints componentGbc_8 = new GridBagConstraints();
						componentGbc_8.insets = new Insets(5, 0, 5, 5);
						componentGbc_8.fill = GridBagConstraints.HORIZONTAL;
						componentGbc_8.gridx = 1;
						componentGbc_8.gridy = 9;
						add(mciNumberJTextField, componentGbc_8);
		
				JLabel flippableLabel = new JLabel("Flippable:");
				GridBagConstraints labelGbc_4 = new GridBagConstraints();
				labelGbc_4.insets = new Insets(5, 5, 5, 5);
				labelGbc_4.gridx = 2;
				labelGbc_4.gridy = 9;
				add(flippableLabel, labelGbc_4);
		
				flippableJCheckBox = new JCheckBox();
				GridBagConstraints componentGbc_4 = new GridBagConstraints();
				componentGbc_4.insets = new Insets(5, 0, 5, 0);
				componentGbc_4.fill = GridBagConstraints.HORIZONTAL;
				componentGbc_4.gridx = 3;
				componentGbc_4.gridy = 9;
				add(flippableJCheckBox, componentGbc_4);
								
										JLabel gathererCodeLabel = new JLabel("GathererCode:");
										GridBagConstraints labelGbc_5 = new GridBagConstraints();
										labelGbc_5.insets = new Insets(5, 5, 5, 5);
										labelGbc_5.gridx = 0;
										labelGbc_5.gridy = 10;
										add(gathererCodeLabel, labelGbc_5);
								
										gathererCodeJTextField = new JTextField();
										GridBagConstraints componentGbc_5 = new GridBagConstraints();
										componentGbc_5.insets = new Insets(5, 0, 5, 5);
										componentGbc_5.fill = GridBagConstraints.HORIZONTAL;
										componentGbc_5.gridx = 1;
										componentGbc_5.gridy = 10;
										add(gathererCodeJTextField, componentGbc_5);
						
								JLabel rotatedCardNameLabel = new JLabel("RotatedCardName:");
								GridBagConstraints labelGbc_15 = new GridBagConstraints();
								labelGbc_15.insets = new Insets(5, 5, 5, 5);
								labelGbc_15.gridx = 2;
								labelGbc_15.gridy = 10;
								add(rotatedCardNameLabel, labelGbc_15);
						
								rotatedCardNameJTextField = new JTextField();
								GridBagConstraints componentGbc_15 = new GridBagConstraints();
								componentGbc_15.insets = new Insets(5, 0, 5, 0);
								componentGbc_15.fill = GridBagConstraints.HORIZONTAL;
								componentGbc_15.gridx = 3;
								componentGbc_15.gridy = 10;
								add(rotatedCardNameJTextField, componentGbc_15);

		if (magicCard != null) {
			m_bindingGroup = initDataBindings();
		}
	}

	public org.magic.api.beans.MagicCard getMagicCard() {
		return magicCard;
	}

	public void setMagicCard(org.magic.api.beans.MagicCard newMagicCard) {
		setMagicCard(newMagicCard, true);
	}

	public void setMagicCard(org.magic.api.beans.MagicCard newMagicCard, boolean update) {
		magicCard = newMagicCard;
		if (update) {
			if (m_bindingGroup != null) {
				m_bindingGroup.unbind();
				m_bindingGroup = null;
			}
			if (magicCard != null) {
				m_bindingGroup = initDataBindings();
			}
		}
	}
	protected BindingGroup initDataBindings() {
		BeanProperty<MagicCard, String> artistProperty = BeanProperty.create("artist");
		BeanProperty<JTextField, String> textProperty = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, artistProperty, artistJTextField, textProperty);
		autoBinding.bind();
		//
		BeanProperty<MagicCard, String> costProperty = BeanProperty.create("cost");
		BeanProperty<JTextField, String> textProperty_1 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, costProperty, costJTextField, textProperty_1);
		autoBinding_2.bind();
		//
		BeanProperty<MagicCard, String> flavorProperty = BeanProperty.create("flavor");
		BeanProperty<JTextField, String> textProperty_2 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, flavorProperty, flavorJTextField, textProperty_2);
		autoBinding_3.bind();
		//
		BeanProperty<MagicCard, Boolean> flippableProperty = BeanProperty.create("flippable");
		BeanProperty<JCheckBox, Boolean> selectedProperty = BeanProperty.create("selected");
		AutoBinding<MagicCard, Boolean, JCheckBox, Boolean> autoBinding_4 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, flippableProperty, flippableJCheckBox, selectedProperty);
		autoBinding_4.bind();
		//
		BeanProperty<MagicCard, String> gathererCodeProperty = BeanProperty.create("gathererCode");
		BeanProperty<JTextField, String> textProperty_3 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding_5 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, gathererCodeProperty, gathererCodeJTextField, textProperty_3);
		autoBinding_5.bind();
		//
		BeanProperty<MagicCard, String> layoutProperty = BeanProperty.create("layout");
		BeanProperty<JComboBox, String> selectedIndexProperty = BeanProperty.create("selectedItem");
		AutoBinding<MagicCard, String, JComboBox, String> autoBinding_6 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, layoutProperty, layoutJComboBox, selectedIndexProperty);
		autoBinding_6.bind();
		//
		BeanProperty<MagicCard, String> loyaltyProperty = BeanProperty.create("loyalty");
		BeanProperty<JTextField, String> valueProperty_1 = BeanProperty.create("value");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding_7 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, loyaltyProperty, loyaltyJTextField, valueProperty_1);
		autoBinding_7.bind();
		//
		BeanProperty<MagicCard, String> mciNumberProperty = BeanProperty.create("mciNumber");
		BeanProperty<JTextField, String> textProperty_4 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding_8 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, mciNumberProperty, mciNumberJTextField, textProperty_4);
		autoBinding_8.bind();
		//
		BeanProperty<MagicCard, String> nameProperty = BeanProperty.create("name");
		BeanProperty<JTextField, String> textProperty_5 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding_10 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, nameProperty, nameJTextField, textProperty_5);
		autoBinding_10.bind();
		//
		BeanProperty<MagicCard, String> numberProperty = BeanProperty.create("number");
		BeanProperty<JTextField, Object> valueProperty_3 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, Object> autoBinding_11 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, numberProperty, numberJTextField, valueProperty_3);
		autoBinding_11.bind();
		//
		BeanProperty<MagicCard, String> powerProperty = BeanProperty.create("power");
		BeanProperty<JTextField, Object> valueProperty_4 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, Object> autoBinding_13 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, powerProperty, powerJTextField, valueProperty_4);
		autoBinding_13.bind();
		//
		BeanProperty<MagicCard, String> rarityProperty = BeanProperty.create("rarity");
		BeanProperty<JComboBox, String> selectedIndexProperty_1 = BeanProperty.create("selectedItem");
		AutoBinding<MagicCard, String, JComboBox, String> autoBinding_14 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, rarityProperty, rarityJComboBox, selectedIndexProperty_1);
		autoBinding_14.bind();
		//
		BeanProperty<MagicCard, String> rotatedCardNameProperty = BeanProperty.create("rotatedCardName");
		BeanProperty<JTextField, String> textProperty_7 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding_15 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, rotatedCardNameProperty, rotatedCardNameJTextField, textProperty_7);
		autoBinding_15.bind();
		//
		BeanProperty<MagicCard, String> textProperty_8 = BeanProperty.create("text");
		BeanProperty<JEditorPane, String> textProperty_9 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JEditorPane, String> autoBinding_16 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, textProperty_8, textJEditorPane, textProperty_9);
		autoBinding_16.bind();
		//
		BeanProperty<MagicCard, String> toughnessProperty = BeanProperty.create("toughness");
		BeanProperty<JTextField, Object> valueProperty_5 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, Object> autoBinding_17 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, toughnessProperty, toughnessJTextField, valueProperty_5);
		autoBinding_17.bind();
		//
		BeanProperty<MagicCard, Boolean> tranformableProperty = BeanProperty.create("tranformable");
		BeanProperty<JCheckBox, Boolean> selectedProperty_1 = BeanProperty.create("selected");
		AutoBinding<MagicCard, Boolean, JCheckBox, Boolean> autoBinding_18 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, tranformableProperty, tranformableJCheckBox, selectedProperty_1);
		autoBinding_18.bind();
		//
		BeanProperty<MagicCard, String> watermarksProperty = BeanProperty.create("watermarks");
		BeanProperty<JTextField, String> textProperty_10 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding_19 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, watermarksProperty, watermarksJTextField, textProperty_10);
		autoBinding_19.bind();
		//
		BindingGroup bindingGroup = new BindingGroup();
		//
		bindingGroup.addBinding(autoBinding);
		bindingGroup.addBinding(autoBinding_2);
		bindingGroup.addBinding(autoBinding_3);
		bindingGroup.addBinding(autoBinding_4);
		bindingGroup.addBinding(autoBinding_5);
		bindingGroup.addBinding(autoBinding_6);
		bindingGroup.addBinding(autoBinding_7);
		bindingGroup.addBinding(autoBinding_8);
		bindingGroup.addBinding(autoBinding_10);
		bindingGroup.addBinding(autoBinding_11);
		bindingGroup.addBinding(autoBinding_13);
		bindingGroup.addBinding(autoBinding_14);
		bindingGroup.addBinding(autoBinding_15);
		bindingGroup.addBinding(autoBinding_16);
		bindingGroup.addBinding(autoBinding_17);
		bindingGroup.addBinding(autoBinding_18);
		bindingGroup.addBinding(autoBinding_19);
		return bindingGroup;
	}
}
