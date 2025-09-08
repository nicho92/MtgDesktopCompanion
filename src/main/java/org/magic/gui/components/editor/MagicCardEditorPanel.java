package org.magic.gui.components.editor;

import static org.magic.services.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.BadLocationException;

import org.japura.gui.model.DefaultListCheckModel;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.enums.EnumExtraCardMetaData;
import org.magic.api.beans.enums.EnumFrameEffects;
import org.magic.api.beans.enums.EnumRarity;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.providers.impl.PrivateMTGSetProvider;
import org.magic.api.sorters.CardNameSorter;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.card.MagicTextPane;
import org.magic.gui.components.dialog.importer.ManaCostDialog;
import org.magic.gui.components.dialog.importer.WallPaperChooseDialog;
import org.magic.gui.components.widgets.JLangLabel;
import org.magic.services.MTGConstants;
import org.magic.services.providers.IconsProvider;
import org.magic.services.tools.ImagePoster;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;


public class MagicCardEditorPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private transient BindingGroup mbindingGroup;
	private MTGCard magicCard = new MTGCard();
	
	private JTextField artistJTextField;
	private JTextField costJTextField;
	private JTextField flavorJTextField;
	private JTextField loyaltyJTextField;
	private JTextField nameJTextField;
	private JTextField numberJTextField;
	private JTextField powerJTextField;
	private JTextField txtSubTypes;
	private JTextField toughnessJTextField;
	private MagicTextPane textJEditorPane;
	private JSpinner spinnerTextSize;
	private JButton btnImage;
	private JButton btnUrl;
	private JButton btnWallpaper;
	
	private JSlider sldZoom;
	private JSlider sldX;
	private JSlider sldY;
	
	private JCheckBox chkWhiteText;
	private JCheckBox chkMatureContent;

	private JComboBox<String> cboColorAccent;
	private JComboBox<EnumFrameEffects> cboFrameEffects;
	private JComboBox<String> cboSide;
	private JComboBox<EnumRarity> cboRarity;
	private JComboBox<MTGCard> cboReversedCards;
	private JCheckableListBox<String> cboSuperType;
	private JCheckableListBox<String> cboTypes;
	private JCheckableListBox<String> cboSubtypes;

	
	
	@Override
	public String getTitle() {
		return "CARD_EDITOR";
	}
	
	public MagicCardEditorPanel() {
		var gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 279, 122, 103, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 0.0, 0.0, 1.0, 1.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,0.0 };
		setLayout(gridBagLayout);

				
		nameJTextField = new JTextField();
		costJTextField = new JTextField();
		artistJTextField = new JTextField();
		cboRarity = UITools.createCombobox(EnumRarity.values());
		cboSuperType = new JCheckableListBox<>();
		cboTypes = new JCheckableListBox<>();
		cboSubtypes = new JCheckableListBox<>();
		txtSubTypes = new JTextField(10);
		var panelButton = new JPanel();
		var textEditorPanel = new JPanel();
		var pictureEditorPanel = new JPanel();
		textJEditorPane = new MagicTextPane(false);
		flavorJTextField = new JTextField();
		cboFrameEffects = UITools.createCombobox(EnumFrameEffects.values());
		cboReversedCards = new JComboBox<>();
		loyaltyJTextField = new JTextField();
		numberJTextField = new JTextField();
		cboSide = UITools.createCombobox((new String[] {"a","b"}));
		chkMatureContent = new JCheckBox("Mature");
		spinnerTextSize = new JSpinner(new SpinnerNumberModel(32, 18, 38, 1));
		btnUrl = new JButton("URL",MTGConstants.ICON_WEBSITE);
		btnImage = new JButton("File",MTGConstants.ICON_NEW);
		btnWallpaper = new JButton("WallPaper",MTGConstants.ICON_WALLPAPER);
		cboColorAccent = new JComboBox<>(new DefaultComboBoxModel<>(new String[] {"","C", "A", "W", "WU", "WB", "U", "UB", "UR", "B", "BR", "BG", "R", "RG", "RW", "G", "GW", "GU"}));
		
		
		
		
		var scrollPane = new JScrollPane(new JSuggestedPanel(textJEditorPane));
		var modelSt = new DefaultListCheckModel();
		var model = new DefaultListCheckModel();
		powerJTextField = new JTextField(2);
		toughnessJTextField = new JTextField(2);
		sldZoom = new JSlider(100,400);
		sldX = new JSlider(-300,300);
		sldY = new JSlider(-300,300);
		chkWhiteText = new JCheckBox("White text");
	
		
		cboSuperType.setModel(modelSt);
		costJTextField.setEditable(false);
		cboTypes.setModel(model);
		List.of("", "Basic", "Legendary","Ongoing", "Snow", "World" ).forEach(modelSt::addElement);
		List.of("", "Artifact","Battle","Conspiracy","Creature","Enchantment","Instant","Land","Phenomenon","Plane","Planeswalker","Scheme","Sorcery","Tribal","Token").forEach(model::addElement);
		FlowLayout flpanelButton = (FlowLayout) panelButton.getLayout();
		flpanelButton.setAlignment(FlowLayout.LEFT);
		textEditorPanel.setLayout(new BorderLayout());
		cboReversedCards.setVisible(false);
		scrollPane.setPreferredSize(new Dimension(textEditorPanel.getWidth(), 60));
		textEditorPanel.setPreferredSize(new Dimension(textJEditorPane.getWidth(), 150));
		sldZoom.setValue(100);
		sldX.setValue(0);
		sldY.setValue(0);
		pictureEditorPanel.setLayout(new GridLayout(6, 1));
		
		
		add(new JLangLabel("NAME",true), UITools.createGridBagConstraints(null, null, 0, 0));
		add(new JLangLabel("CARD_MANA",true), UITools.createGridBagConstraints(null, null, 2, 0));
		add(new JLangLabel("CARD_ARTIST",true), UITools.createGridBagConstraints(null, null, 0, 1));
		add(new JLangLabel("CARD_RARITY",true), UITools.createGridBagConstraints(null, null, 2, 1));
		add(new JLangLabel("CARD_TYPES",true), UITools.createGridBagConstraints(null, null, 0, 2));
		add(new JLangLabel("CARD_TEXT",true), UITools.createGridBagConstraints(null, null, 0, 4));
		add(new JLangLabel("CARD_FLAVOR",true), UITools.createGridBagConstraints(null, null, 0, 7));
		add(new JLangLabel("CARD_LAYOUT",true), UITools.createGridBagConstraints(null, null, 0, 8));
		add(new JLabel(capitalize("CARD_POWER") + "/" + capitalize("CARD_TOUGHNESS") + ":"), UITools.createGridBagConstraints(null, null, 2, 8));		
		add(new JLangLabel("CARD_NUMBER",true), UITools.createGridBagConstraints(null, null, 0, 9));
		add(new JLangLabel("PICTURE",true), UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 0, 11));
		add(new JLangLabel("COLOR_INDICATOR",true), UITools.createGridBagConstraints(null, null, 2, 10));
		
		add(nameJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 0));
		add(costJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 3, 0));
		add(artistJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 1));
		add(cboRarity, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 3, 1));
		add(UITools.createFlowPanel(cboSuperType,cboTypes,cboSubtypes,txtSubTypes), UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 2,3,null));
		add(panelButton, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 3,2,null));
		add(flavorJTextField,  UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 7,3,null));
		add(UITools.createFlowPanel(cboFrameEffects,cboReversedCards), UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 8));
		add(UITools.createFlowPanel(powerJTextField,new JLabel("/"),toughnessJTextField, new JLangLabel("CARD_LOYALTY",true),loyaltyJTextField), UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 3, 8));
		add(UITools.createFlowPanel(numberJTextField,cboSide), UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 9));
		add(chkMatureContent, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 10));
		add(pictureEditorPanel,  UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 11));
		
		
		add(cboColorAccent, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 3, 10));
		add(textEditorPanel, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 4,3,3));

		
		
		textEditorPanel.add(new JScrollPane(textJEditorPane),BorderLayout.CENTER);
		textEditorPanel.add(scrollPane,BorderLayout.SOUTH);
		pictureEditorPanel.add(UITools.createFlowPanel(btnUrl,btnImage,btnWallpaper));
		pictureEditorPanel.add(UITools.createFlowPanel(new JLangLabel("SIZE",true), spinnerTextSize));
		pictureEditorPanel.add(chkWhiteText);
		pictureEditorPanel.add(UITools.createFlowPanel(new JLabel("Z:"), sldZoom));
		pictureEditorPanel.add(UITools.createFlowPanel(new JLabel("X:"), sldX));
		pictureEditorPanel.add(UITools.createFlowPanel(new JLabel("Y:"), sldY));
	
		
		
	
		for (var s : new String[] { "W", "U", "B", "R", "G", "C", "T", "E" }) 
		{
			final var btnG = new JButton();
			btnG.setToolTipText(s);
			btnG.setIcon(new ImageIcon(IconsProvider.getInstance().getManaSymbol(s).getScaledInstance(15, 15, Image.SCALE_SMOOTH)));
			btnG.setForeground(btnG.getBackground());

			btnG.addActionListener(_ -> {
						try {
							textJEditorPane.getDocument().insertString(textJEditorPane.getCaretPosition(), " {" + btnG.getToolTipText() + "}", null);
						} catch (BadLocationException e1) {
							textJEditorPane.setText(textJEditorPane.getText()+" {" + btnG.getToolTipText() + "}");
							logger.error(e1);
						}
			});
			panelButton.add(btnG);
		}
		
		costJTextField.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				var g = new ManaCostDialog();
				g.setVisible(true);
				
				if(g.hasSelected())
				{
					costJTextField.setText(g.getSelectedItem());
				}
				else
				{
					costJTextField.setText("");
				}
			}
		});
	
		txtSubTypes.addActionListener(_ -> {
			cboSubtypes.addElement(txtSubTypes.getText(), true);
			txtSubTypes.setText("");
		});
		
		btnImage.addActionListener(_ -> {
			var choose = new JFileChooser();
			choose.showOpenDialog(null);
			var pics = choose.getSelectedFile();
			if(pics!=null) {
				magicCard.setUrl(pics.getAbsolutePath());
			}
		});
		
		btnUrl.addActionListener(_->{
			magicCard.setUrl(JOptionPane.showInputDialog("URL"));
		});
		
		btnWallpaper.addActionListener(_->{
		
					var wallChooser = new WallPaperChooseDialog();
					wallChooser.setVisible(true);
					
					if(wallChooser.hasSelected())
					{

						magicCard.setUrl(wallChooser.getSelectedItem().getUrl().toASCIIString());	
						
						//protected webhoster.
						if(wallChooser.getSelectedItem().getUserAgent()!=null)
						{
							try {
								magicCard.setUrl(new ImagePoster().upload(wallChooser.getSelectedItem().getUrl().toASCIIString()));
							} catch (IOException e1) {
								logger.error(e1);
							}
						}
						magicCard.setArtist(wallChooser.getSelectedItem().getAuthor());
						magicCard.setHasContentWarning(wallChooser.getSelectedItem().isMature());
						artistJTextField.setText(wallChooser.getSelectedItem().getAuthor());
					}
		});
		
		cboReversedCards.addItemListener(event->{
			if(event.getStateChange() == ItemEvent.SELECTED)
				magicCard.setRotatedCard(cboReversedCards.getItemAt(cboReversedCards.getSelectedIndex()));
		});

		cboFrameEffects.addItemListener(event->{
			if(event.getStateChange() == ItemEvent.SELECTED)
			{
				cboReversedCards.setVisible(cboFrameEffects.getSelectedItem().toString().contains("DFC"));
				cboReversedCards.removeAllItems();
				cboReversedCards.addItem(null);
				try {
					MTG.getPlugin(PrivateMTGSetProvider.PERSONNAL_DATA_SET_PROVIDER,MTGCardsProvider.class).searchCardByEdition(magicCard.getEdition()).stream().sorted(new CardNameSorter()).toList().forEach(cboReversedCards::addItem);
				} catch (Exception e) {
					logger.error(e);
				}
			}
			
			if(magicCard.isDoubleFaced())
				cboReversedCards.setSelectedItem(magicCard.getRotatedCard());
			
		});
		
		mbindingGroup = initDataBindings();
		
	}

	public MTGCard getMagicCard() {
		magicCard.setTypes(cboTypes.getSelectedElements());
		magicCard.setSupertypes(cboSuperType.getSelectedElements());
		magicCard.setSubtypes(cboSubtypes.getSelectedElements());
		magicCard.setText(textJEditorPane.getText());
		magicCard.setFrameEffects(List.of(cboFrameEffects.getItemAt(cboFrameEffects.getSelectedIndex())));
		magicCard.setColors(EnumColors.parseColorsFromText(magicCard.getCost()));
		magicCard.setColorIdentity(EnumColors.parseColorsFromText(magicCard.getCost()));
		magicCard.setCmc(MTGCard.calculateManaCost(magicCard.getCost()));
		return magicCard;
	}

	public void setMagicCard(MTGCard newMagicCard) {
		magicCard = newMagicCard;
		cboSuperType.unselectAll();
		cboTypes.unselectAll();
		cboSubtypes.unselectAll();
		cboSubtypes.getModel().clear();
		
		if (mbindingGroup != null) {
				mbindingGroup.unbind();
				mbindingGroup = null;
			}
			if (magicCard != null) {
				mbindingGroup = initDataBindings();
			}
	}
	
	protected BindingGroup initDataBindings() {
			
		spinnerTextSize.setValue(magicCard.getCustomMetadata().get(EnumExtraCardMetaData.SIZE)!=null? Integer.parseInt(magicCard.getCustomMetadata().get(EnumExtraCardMetaData.SIZE)):30);
		cboColorAccent.setSelectedItem(magicCard.getCustomMetadata().get(EnumExtraCardMetaData.ACCENT)!=null?magicCard.getCustomMetadata().get(EnumExtraCardMetaData.ACCENT):"");
		chkWhiteText.setSelected(magicCard.getCustomMetadata().get(EnumExtraCardMetaData.TEXT_COLOR)!=null && magicCard.getCustomMetadata().get(EnumExtraCardMetaData.TEXT_COLOR).equals("#ffffff"));
		chkMatureContent.setSelected(magicCard.isHasContentWarning());
		cboSuperType.setSelectedElements(magicCard.getSupertypes());
		cboTypes.setSelectedElements(magicCard.getTypes());
		magicCard.getSubtypes().forEach(s->cboSubtypes.addElement(s, true));
		cboReversedCards.setSelectedItem(magicCard.getRotatedCard());	
		
		if(!magicCard.getFrameEffects().isEmpty())
			cboFrameEffects.setSelectedItem(magicCard.getFrameEffects().get(0));
		else
			cboFrameEffects.setSelectedItem(EnumFrameEffects.NONE);

		
		if(magicCard.getCustomMetadata().get(EnumExtraCardMetaData.ZOOM)!=null)
			sldZoom.setValue(Integer.parseInt(magicCard.getCustomMetadata().get(EnumExtraCardMetaData.ZOOM)));
		
		
		spinnerTextSize.addChangeListener(_->magicCard.getCustomMetadata().put(EnumExtraCardMetaData.SIZE, spinnerTextSize.getValue().toString() ));
		cboColorAccent.addItemListener(_-> magicCard.getCustomMetadata().put(EnumExtraCardMetaData.ACCENT, (cboColorAccent.getSelectedItem().toString()) ));
		sldZoom.addChangeListener(_->magicCard.getCustomMetadata().put(EnumExtraCardMetaData.ZOOM, String.valueOf(sldZoom.getValue()) ));
		sldX.addChangeListener(_->magicCard.getCustomMetadata().put(EnumExtraCardMetaData.X, String.valueOf(sldX.getValue()) ));
		sldY.addChangeListener(_->magicCard.getCustomMetadata().put(EnumExtraCardMetaData.Y, String.valueOf(sldY.getValue()) ));
		chkWhiteText.addItemListener(_->magicCard.getCustomMetadata().put(EnumExtraCardMetaData.TEXT_COLOR, chkWhiteText.isSelected()?"#ffffff":"#000000"));
		chkMatureContent.addItemListener(_->magicCard.setHasContentWarning(chkMatureContent.isSelected()));
		
		BeanProperty<MTGCard, String> artistProperty = BeanProperty.create("artist");
		BeanProperty<JTextField, String> textProperty = BeanProperty.create("text");
		AutoBinding<MTGCard, String, JTextField, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, artistProperty, artistJTextField, textProperty);
				
		autoBinding.bind();
		//
		BeanProperty<MTGCard, String> costProperty = BeanProperty.create("cost");
		BeanProperty<JTextField, String> textProperty1 = BeanProperty.create("text");
		AutoBinding<MTGCard, String, JTextField, String> autoBinding2 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, costProperty, costJTextField, textProperty1);
		autoBinding2.bind();
		//
		BeanProperty<MTGCard, String> flavorProperty = BeanProperty.create("flavor");
		BeanProperty<JTextField, String> textProperty2 = BeanProperty.create("text");
		AutoBinding<MTGCard, String, JTextField, String> autoBinding3 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, flavorProperty, flavorJTextField, textProperty2);
		autoBinding3.bind();
		//
		BeanProperty<MTGCard, String> nameProperty = BeanProperty.create("name");
		BeanProperty<JTextField, String> textProperty5 = BeanProperty.create("text");
		AutoBinding<MTGCard, String, JTextField, String> autoBinding10 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, nameProperty, nameJTextField, textProperty5);
		autoBinding10.bind();
		//
		BeanProperty<MTGCard, String> powerProperty = BeanProperty.create("power");
		BeanProperty<JTextField, Object> valueProperty4 = BeanProperty.create("text");
		AutoBinding<MTGCard, String, JTextField, Object> autoBinding13 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, powerProperty, powerJTextField, valueProperty4);
		autoBinding13.bind();
		//
		BeanProperty<MTGCard, Object> rarityProperty = BeanProperty.create("rarity");
		BeanProperty<JComboBox<EnumRarity>, Object> selectedIndexProperty1 = BeanProperty.create("selectedItem");
		AutoBinding<MTGCard, Object, JComboBox<EnumRarity>, Object> autoBinding14 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, rarityProperty, cboRarity, selectedIndexProperty1);
		autoBinding14.bind();
		//
		BeanProperty<MTGCard, String> sideProperty = BeanProperty.create("side");
		BeanProperty<JComboBox<String>, String> selectedSideProperty = BeanProperty.create("selectedItem");
		AutoBinding<MTGCard, String, JComboBox<String>, String> autoBinding15 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, sideProperty, cboSide, selectedSideProperty);
		autoBinding15.bind();
		//
		BeanProperty<MTGCard, String> textProperty8 = BeanProperty.create("text");
		BeanProperty<MagicTextPane, String> textProperty9 = BeanProperty.create("text");
		AutoBinding<MTGCard, String, MagicTextPane, String> autoBinding16 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, textProperty8, textJEditorPane, textProperty9);
		autoBinding16.bind();
		//
		BeanProperty<MTGCard, String> toughnessProperty = BeanProperty.create("toughness");
		BeanProperty<JTextField, Object> valueProperty5 = BeanProperty.create("text");
		AutoBinding<MTGCard, String, JTextField, Object> autoBinding17 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, toughnessProperty, toughnessJTextField, valueProperty5);
		autoBinding17.bind();
	
		BeanProperty<MTGCard, String> loyaltyProperty = BeanProperty.create("loyalty");
		BeanProperty<JTextField, String> textProperty11 = BeanProperty.create("text");
		AutoBinding<MTGCard, String, JTextField, String> autoBinding20 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, loyaltyProperty, loyaltyJTextField, textProperty11);
		autoBinding20.bind();
		
		BeanProperty<MTGCard, String> numberProperty = BeanProperty.create("number");
		BeanProperty<JTextField, String> numberTxtProperty = BeanProperty.create("text");
		AutoBinding<MTGCard, String, JTextField, String> autoBinding22 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, numberProperty, numberJTextField, numberTxtProperty);
		autoBinding22.bind();
		
		
		var bindingGroup = new BindingGroup();
				bindingGroup.addBinding(autoBinding);
				bindingGroup.addBinding(autoBinding2);
				bindingGroup.addBinding(autoBinding3);
				bindingGroup.addBinding(autoBinding10);
				bindingGroup.addBinding(autoBinding13);
				bindingGroup.addBinding(autoBinding14);
				bindingGroup.addBinding(autoBinding15);
				bindingGroup.addBinding(autoBinding16);
				bindingGroup.addBinding(autoBinding17);
				bindingGroup.addBinding(autoBinding20);
				bindingGroup.addBinding(autoBinding22);

		return bindingGroup;
	}
}
