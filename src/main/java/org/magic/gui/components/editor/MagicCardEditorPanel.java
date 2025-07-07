package org.magic.gui.components.editor;

import static org.magic.services.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.LineBorder;
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
import org.magic.api.interfaces.MTGPictureEditor;
import org.magic.api.interfaces.MTGPictureEditor.MOD;
import org.magic.api.interfaces.MTGTextGenerator;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.card.MagicTextPane;
import org.magic.gui.components.dialog.importer.WallPaperChooseDialog;
import org.magic.gui.components.widgets.JLangLabel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.network.URLTools;
import org.magic.services.providers.IconsProvider;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;


public class MagicCardEditorPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private transient BindingGroup mbindingGroup;
	private MTGCard magicCard = new MTGCard();
	private JTextField artistJTextField;
	private JTextField costJTextField;
	private JTextField flavorJTextField;
	private JCheckBox chkFoil;
	private JComboBox<EnumFrameEffects> layoutJComboBox;
	private JTextField loyaltyJTextField;
	private JTextField nameJTextField;
	private JTextField numberJTextField;
	private JTextField powerJTextField;
	private JComboBox<EnumRarity> rarityJComboBox;
	private MagicTextPane textJEditorPane;
	private JTextField toughnessJTextField;
	private JTextField watermarksJTextField;
	private JCheckableListBox<String> cboSuperType;
	private JCheckableListBox<String> cboTypes;
	private JTextField txtSubTypes;
	private JCheckableListBox<String> cboSubtypes;
	private JSpinner spinner;
	private JCheckBox chkColorIndicator;
	private JButton btnImage;
	private JButton btnUrl;
	private JButton btnWallpaper;
	private CropImagePanel imagePanel;
	private JSlider sldZoom;
	private JSlider sldX;
	private JSlider sldY;
	private JCheckBox chkWhiteText;

	@Override
	public String getTitle() {
		return "CARD_EDITOR";
	}
	
	
	@Override
	public void onVisible() {
		btnUrl.setEnabled(MTG.getEnabledPlugin(MTGPictureEditor.class).getMode()==MOD.URI);
		btnImage.setEnabled(MTG.getEnabledPlugin(MTGPictureEditor.class).getMode()!=MOD.URI);
	}
	

	public MagicCardEditorPanel() {
		var gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 279, 122, 103, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 31, 28, 0, 56, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 0.0, 0.0, 1.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,1.0E-4 };
		setLayout(gridBagLayout);

		add(new JLangLabel("NAME",true), UITools.createGridBagConstraints(null, null, 0, 0));
		add(new JLangLabel("CARD_MANA",true), UITools.createGridBagConstraints(null, null, 2, 0));
		add(new JLangLabel("CARD_ARTIST",true), UITools.createGridBagConstraints(null, null, 0, 1));
		add(new JLangLabel("CARD_RARITY",true), UITools.createGridBagConstraints(null, null, 2, 1));
		add(new JLangLabel("CARD_TYPES",true), UITools.createGridBagConstraints(null, null, 0, 2));
		add(new JLangLabel("CARD_TEXT",true), UITools.createGridBagConstraints(null, null, 0, 4));
		add(new JLangLabel("CARD_FLAVOR",true), UITools.createGridBagConstraints(null, null, 0, 7));
		add(new JLangLabel("CARD_LAYOUT",true), UITools.createGridBagConstraints(null, null, 0, 8));
		add(new JLabel(capitalize("CARD_POWER") + "/" + capitalize("CARD_TOUGHNESS") + ":"), UITools.createGridBagConstraints(null, null, 2, 8));		
		add(new JLangLabel("CARD_WATERMARK",true), UITools.createGridBagConstraints(null, null, 0, 9));
		add(new JLangLabel("CARD_LOYALTY",true), UITools.createGridBagConstraints(null, null, 2, 9));
		add(new JLangLabel("CARD_NUMBER",true), UITools.createGridBagConstraints(null, null, 0, 10));
		add(new JLangLabel("FOIL",true), UITools.createGridBagConstraints(null, null, 2, 10));
		add(new JLabel("Color Indicator :"), UITools.createGridBagConstraints(null, null, 2, 12));
		add(new JLabel("Picture :"), UITools.createGridBagConstraints(null, null, 2, 14));
		add(new JLabel("Text Size :"), UITools.createGridBagConstraints(null, null, 2, 11));
			
		nameJTextField = new JTextField();
		add(nameJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 0));

		costJTextField = new JTextField();
		costJTextField.setEditable(false);
		costJTextField.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				final var g = new JDialog();
				g.setIconImage(MTGConstants.ICON_GAME_COLOR.getImage());
				g.getContentPane().setLayout(new FlowLayout());
				g.setModal(true);

				var data = new String[]{ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
				final JComboBox<String> cboW = new JComboBox<>(data);
				final JComboBox<String> cboU = new JComboBox<>(data);
				final JComboBox<String> cboB = new JComboBox<>(data);
				final JComboBox<String> cboR = new JComboBox<>(data);
				final JComboBox<String> cboG = new JComboBox<>(data);
				final JComboBox<String> cboC = new JComboBox<>(data);
				final JComboBox<String> cboUn = new JComboBox<>(data);
				final JComboBox<String> cboS = new JComboBox<>(data);
				cboUn.addItem("X");

				var btn = new JButton(capitalize("SET_COST") + ":");
				btn.addActionListener(_ -> {
					var cost = new StringBuilder();
					var cmc = 0;
					Set<EnumColors> colors = new LinkedHashSet<>();

					if (!cboUn.getSelectedItem().equals("X")) {
						if (cboUn.getSelectedIndex() > 0) {
							cost.append("{").append(cboUn.getSelectedItem()).append("}");
							cmc += cboUn.getSelectedIndex();
						}
					} else {
						cost.append("{X}");
					}

					for (var i = 0; i < cboC.getSelectedIndex(); i++) {
						cost.append("{C}");
						cmc += 1;
					}

					for (var i = 0; i < cboW.getSelectedIndex(); i++) {
						cost.append(EnumColors.WHITE.toManaCode());
						cmc += 1;
						colors.add(EnumColors.WHITE);
					}

					for (var i = 0; i < cboU.getSelectedIndex(); i++) {
						cost.append(EnumColors.BLUE.toManaCode());
						cmc += 1;
						colors.add(EnumColors.BLUE);
					}

					for (var i = 0; i < cboB.getSelectedIndex(); i++) {
						cost.append(EnumColors.BLACK.toManaCode());
						cmc += 1;
						colors.add(EnumColors.BLACK);
					}

					for (var i = 0; i < cboR.getSelectedIndex(); i++) {
						cost.append(EnumColors.RED.toManaCode());
						cmc += 1;
						colors.add(EnumColors.RED);
					}

					for (var i = 0; i < cboG.getSelectedIndex(); i++) {
						cost.append(EnumColors.GREEN.toManaCode());
						cmc += 1;
						colors.add(EnumColors.GREEN);
					}

					for (var i = 0; i < cboS.getSelectedIndex(); i++) {
						cost.append(EnumColors.SNOW.toManaCode());
						cmc += 1;
						colors.add(EnumColors.SNOW);
					}
					
					magicCard.setCmc(cmc);
					magicCard.setColors(new ArrayList<>(colors));
					magicCard.setColorIdentity(new ArrayList<>(colors));
					costJTextField.setText(cost.toString());
					g.dispose();
				});

				g.getContentPane().add(new JLabel(new ImageIcon(IconsProvider.getInstance().getManaSymbol("1").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
				g.getContentPane().add(cboUn);
				g.getContentPane().add(new JLabel(new ImageIcon(IconsProvider.getInstance().getManaSymbol("W").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
				g.getContentPane().add(cboW);
				g.getContentPane().add(new JLabel(new ImageIcon(IconsProvider.getInstance().getManaSymbol("U").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
				g.getContentPane().add(cboU);
				g.getContentPane().add(new JLabel(new ImageIcon(IconsProvider.getInstance().getManaSymbol("B").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
				g.getContentPane().add(cboB);
				g.getContentPane().add(new JLabel(new ImageIcon(IconsProvider.getInstance().getManaSymbol("R").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
				g.getContentPane().add(cboR);
				g.getContentPane().add(new JLabel(new ImageIcon(IconsProvider.getInstance().getManaSymbol("G").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
				g.getContentPane().add(cboG);
				g.getContentPane().add(new JLabel(new ImageIcon(IconsProvider.getInstance().getManaSymbol("C").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
				g.getContentPane().add(cboC);
				g.getContentPane().add(new JLabel(new ImageIcon(IconsProvider.getInstance().getManaSymbol("S").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
				g.getContentPane().add(cboS);
				g.getContentPane().add(btn);
				g.setLocationRelativeTo(null);
				g.pack();
				g.setVisible(true);

			}

		});
		add(costJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 3, 0));

		artistJTextField = new JTextField();
		add(artistJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 1));

		rarityJComboBox = UITools.createCombobox(EnumRarity.values());
		add(rarityJComboBox, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 3, 1));

		cboSuperType = new JCheckableListBox<>();
		var modelSt = new DefaultListCheckModel();
		cboSuperType.setModel(modelSt);
		
		List.of("", "Basic", "Legendary","Ongoing", "Snow", "World" ).forEach(modelSt::addElement);

		cboTypes = new JCheckableListBox<>();
		var model = new DefaultListCheckModel();
		cboTypes.setModel(model);
		List.of("", "Arcane", "Artifact", "Aura", "Clue", "Conspiracy", "Continuous","Contraption", "Creature", "Curse", "Elite", "Enchantment", "Equipment", "Fortification","Global enchantment", "Hero", "Instant", "Interrupt", "Land", "Local", "Mana source","Mono", "Ongoing", "Permanent", "Phenomenon", "Plane", "Planeswalker", "Poly", "Scheme", "Shrine","Snow", "Sorcery", "Spell", "Summon", "Trap", "Tribal", "Vanguard", "Vehicle", "World").forEach(model::addElement);
		
		cboSubtypes = new JCheckableListBox<>();
		
		txtSubTypes = new JTextField(10);
		txtSubTypes.addActionListener(_ -> {
			cboSubtypes.addElement(txtSubTypes.getText(), true);
			txtSubTypes.setText("");
		});

		
		var gbcpanelType = UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 2,3,null);
		add(UITools.createFlowPanel(cboSuperType,cboTypes,cboSubtypes,txtSubTypes), gbcpanelType);
		
		var panelButton = new JPanel();
		FlowLayout flpanelButton = (FlowLayout) panelButton.getLayout();
		flpanelButton.setAlignment(FlowLayout.LEFT);
		
		var gbcpanelButton = UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 3,2,null);
		add(panelButton, gbcpanelButton);

		for (var s : new String[] { "W", "U", "B", "R", "G", "C", "T", "E" }) 
		{
			final var btnG = new JButton();
			btnG.setToolTipText(s);
			btnG.setIcon(new ImageIcon(IconsProvider.getInstance().getManaSymbol(s).getScaledInstance(15, 15, Image.SCALE_SMOOTH)));
			btnG.setForeground(btnG.getBackground());

			btnG.addActionListener(
					_ -> {
						try {
							textJEditorPane.getDocument().insertString(textJEditorPane.getCaretPosition(), " {" + btnG.getToolTipText() + "}", null);
						} catch (BadLocationException e1) {
							textJEditorPane.setText(textJEditorPane.getText()+" {" + btnG.getToolTipText() + "}");
							logger.error(e1);
						}
					});

			panelButton.add(btnG);

		}
		
		var panelEditor = new JPanel();
		panelEditor.setLayout(new BorderLayout());
		textJEditorPane = new MagicTextPane(false);

		panelEditor.add(new JScrollPane(textJEditorPane),BorderLayout.CENTER);
		var scrollPane = new JScrollPane(new JSuggestedPanel(textJEditorPane,MTG.getEnabledPlugin(MTGTextGenerator.class)));
		scrollPane.setPreferredSize(new Dimension(panelEditor.getWidth(), 60));
		panelEditor.add(scrollPane,BorderLayout.SOUTH);
		panelEditor.setPreferredSize(new Dimension(textJEditorPane.getWidth(), 150));

		add(panelEditor, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 4,3,3));


		flavorJTextField = new JTextField();
		add(flavorJTextField,  UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 7,3,null));

	
		layoutJComboBox = new JComboBox<>();
		for(var eff : EnumFrameEffects.values())
			layoutJComboBox.addItem(eff);
		
		add(layoutJComboBox, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 8));
	
		powerJTextField = new JTextField(2);
		toughnessJTextField = new JTextField(2);
		add(UITools.createFlowPanel(powerJTextField,new JLabel("/"),toughnessJTextField), UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 3, 8));
		
		watermarksJTextField = new JTextField();
		add(watermarksJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 9));

		loyaltyJTextField = new JTextField();
		add(loyaltyJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 3, 9));

		numberJTextField = new JTextField();
		add(numberJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 10));


		chkFoil = new JCheckBox();
		add(chkFoil, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 3, 10));

		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(32, 18, 38, 1));
		add(spinner, UITools.createGridBagConstraints(null, null, 3, 11));

		var panelImageButtons = new JPanel();
		add(panelImageButtons, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 0, 12,null,4));
		
		
		
		var gblpanelImageButtons = new GridBagLayout();
		gblpanelImageButtons.columnWidths = new int[]{63, 0};
		gblpanelImageButtons.rowHeights = new int[]{23, 0, 0};
		gblpanelImageButtons.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gblpanelImageButtons.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panelImageButtons.setLayout(gblpanelImageButtons);

		btnImage = new JButton("Image");
		btnImage.addActionListener(_ -> {

			var choose = new JFileChooser();
			choose.showOpenDialog(null);
			var pics = choose.getSelectedFile();
			if(pics!=null) {
			magicCard.setUrl(pics.getAbsolutePath());
			showCrop();
			}
		});
		panelImageButtons.add(btnImage,  UITools.createGridBagConstraints(GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 0, 0));

		btnUrl = new JButton("URL");
		panelImageButtons.add(btnUrl, UITools.createGridBagConstraints(GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 0, 1));
		btnUrl.addActionListener(_->{
					magicCard.setUrl(JOptionPane.showInputDialog("URL"));
					showCrop();
		});
		
		btnWallpaper = new JButton("WallPaper");
		panelImageButtons.add(btnWallpaper, UITools.createGridBagConstraints(GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 0, 2));
		btnWallpaper.addActionListener(_->{
		
					var wallChooser = new WallPaperChooseDialog();
					wallChooser.setVisible(true);
					
					if(wallChooser.hasSelected())
					{
						
						if(MTG.getEnabledPlugin(MTGPictureEditor.class).getMode()==MOD.FILE)
						{
							var f = new File(MTGConstants.MTG_WALLPAPER_DIRECTORY,wallChooser.getSelectedItem().getName()+"."+wallChooser.getSelectedItem().getFormat());
							
							try {
								URLTools.download(wallChooser.getSelectedItem().getUrl().toASCIIString(), f);
								magicCard.setUrl(f.getAbsolutePath());	
							} catch (IOException e1) {
								MTGControler.getInstance().notify(e1);
							}
						}
						else
						{
							magicCard.setUrl(wallChooser.getSelectedItem().getUrl().toASCIIString());	
						}
						
						magicCard.setArtist(wallChooser.getSelectedItem().getAuthor());
						artistJTextField.setText(wallChooser.getSelectedItem().getAuthor());
						imagePanel.setImage(wallChooser.getSelectedItem().getPicture());
					}
					showCrop();
		
		});
		
		imagePanel = new CropImagePanel();
		imagePanel.setBorder(new LineBorder(Color.BLACK));
		add(imagePanel, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 12,null,4));

		chkColorIndicator = new JCheckBox("");
		add(chkColorIndicator, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 3, 12));
		
		
		
		sldZoom = new JSlider(100,400);
		sldX = new JSlider(-300,300);
		sldY = new JSlider(-300,300);

		sldZoom.setValue(100);
		sldX.setValue(0);
		sldY.setValue(0);
		chkWhiteText = new JCheckBox();
		var pan = new JPanel();
		pan.setLayout(new GridLayout(4, 1));
		
		pan.add(UITools.createFlowPanel(new JLabel("White text :"),chkWhiteText));
		pan.add(UITools.createFlowPanel(new JLabel("Z:"), sldZoom));
		pan.add(UITools.createFlowPanel(new JLabel("X:"), sldX));
		pan.add(UITools.createFlowPanel(new JLabel("Y:"), sldY));
		
		add(pan,  UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 3, 14));
		
		
		mbindingGroup = initDataBindings();
		
	}


	private void showCrop() {

		BufferedImage buff;
		try {
			if(magicCard.getUrl().startsWith("http"))
				buff = URLTools.extractAsImage(magicCard.getUrl());
			else
				buff = ImageTools.read(new File(magicCard.getUrl()));

			if(buff!=null) {
				var i = new ImageIcon(buff).getImage();
				imagePanel.setImage(i.getScaledInstance(imagePanel.getWidth(), imagePanel.getHeight(), Image.SCALE_SMOOTH));
				imagePanel.revalidate();
				imagePanel.repaint();
			}

		} catch (Exception e) {
			logger.error("error cropping",e.getMessage());
		}

	}

	public MTGCard getMagicCard() {
		magicCard.setTypes(cboTypes.getSelectedElements());
		magicCard.setSupertypes(cboSuperType.getSelectedElements());
		magicCard.setSubtypes(cboSubtypes.getSelectedElements());
		magicCard.setText(textJEditorPane.getText());
		magicCard.setFrameEffects(List.of((EnumFrameEffects)layoutJComboBox.getSelectedItem()));
		
		magicCard.getCustomMetadata().put(EnumExtraCardMetaData.CROP_H, String.valueOf(imagePanel.getCroppedDimension().getHeight()));
		magicCard.getCustomMetadata().put(EnumExtraCardMetaData.CROP_W, String.valueOf(imagePanel.getCroppedDimension().getWidth()));
		magicCard.getCustomMetadata().put(EnumExtraCardMetaData.CROP_X, String.valueOf(imagePanel.getCroppedDimension().getX()));
		magicCard.getCustomMetadata().put(EnumExtraCardMetaData.CROP_Y, String.valueOf(imagePanel.getCroppedDimension().getY()));
		
		return magicCard;
	}

	public void setMagicCard(MTGCard newMagicCard) {
		magicCard = newMagicCard;
		cboSuperType.unselectAll();
		cboTypes.unselectAll();
		cboSubtypes.unselectAll();
		cboSubtypes.getModel().clear();
		imagePanel.setImage(null);
		
		if (mbindingGroup != null) {
				mbindingGroup.unbind();
				mbindingGroup = null;
			}
			if (magicCard != null) {
				mbindingGroup = initDataBindings();
			}

		if (magicCard != null) {
			cboSuperType.setSelectedElements(magicCard.getSupertypes());
			cboTypes.setSelectedElements(magicCard.getTypes());
			magicCard.getSubtypes().forEach(s->cboSubtypes.addElement(s, true));
			
			if(!magicCard.getFrameEffects().isEmpty())
				layoutJComboBox.setSelectedItem(magicCard.getFrameEffects().get(0));
			else
				layoutJComboBox.setSelectedItem(EnumFrameEffects.NONE);
		}

	}

	public CropImagePanel getImagePanel() {
		return imagePanel;
	}


	protected BindingGroup initDataBindings() {
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
		AutoBinding<MTGCard, Object, JComboBox<EnumRarity>, Object> autoBinding14 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, rarityProperty, rarityJComboBox, selectedIndexProperty1);
		autoBinding14.bind();
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
		//
		BeanProperty<MTGCard, String> watermarksProperty = BeanProperty.create("watermarks");
		BeanProperty<JTextField, String> textProperty10 = BeanProperty.create("text");
		AutoBinding<MTGCard, String, JTextField, String> autoBinding19 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, watermarksProperty, watermarksJTextField, textProperty10);
		autoBinding19.bind();

		BeanProperty<MTGCard, String> loyaltyProperty = BeanProperty.create("loyalty");
		BeanProperty<JTextField, String> textProperty11 = BeanProperty.create("text");
		AutoBinding<MTGCard, String, JTextField, String> autoBinding20 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, loyaltyProperty, loyaltyJTextField, textProperty11);
		autoBinding20.bind();
			
		BeanProperty<MTGCard, String> numberProperty = BeanProperty.create("number");
		BeanProperty<JTextField, String> numberTxtProperty = BeanProperty.create("text");
		AutoBinding<MTGCard, String, JTextField, String> autoBinding22 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, numberProperty, numberJTextField, numberTxtProperty);
		autoBinding22.bind();
		
		
		spinner.setValue(magicCard.getCustomMetadata().get(EnumExtraCardMetaData.SIZE)!=null? Integer.parseInt(magicCard.getCustomMetadata().get(EnumExtraCardMetaData.SIZE)):30);
		chkFoil.setSelected(Boolean.parseBoolean(magicCard.getCustomMetadata().get(EnumExtraCardMetaData.FOIL)));
		chkColorIndicator.setSelected(Boolean.parseBoolean(magicCard.getCustomMetadata().get(EnumExtraCardMetaData.INDICATOR)));
		
		chkWhiteText.setSelected(magicCard.getCustomMetadata().get(EnumExtraCardMetaData.TEXT_COLOR)!=null && magicCard.getCustomMetadata().get(EnumExtraCardMetaData.TEXT_COLOR).equals("#ffffff"));
		
		
		
		if(magicCard.getCustomMetadata().get(EnumExtraCardMetaData.ZOOM)!=null)
			sldZoom.setValue(Integer.parseInt(magicCard.getCustomMetadata().get(EnumExtraCardMetaData.ZOOM)));
		
		
		spinner.addChangeListener(_->magicCard.getCustomMetadata().put(EnumExtraCardMetaData.SIZE, spinner.getValue().toString() ));
		chkFoil.addItemListener(_->magicCard.getCustomMetadata().put(EnumExtraCardMetaData.FOIL, String.valueOf(chkFoil.isSelected()) ));
		chkColorIndicator.addItemListener(_->magicCard.getCustomMetadata().put(EnumExtraCardMetaData.INDICATOR, String.valueOf(chkColorIndicator.isSelected()) ));
		sldZoom.addChangeListener(_->magicCard.getCustomMetadata().put(EnumExtraCardMetaData.ZOOM, String.valueOf(sldZoom.getValue()) ));
		sldX.addChangeListener(_->magicCard.getCustomMetadata().put(EnumExtraCardMetaData.X, String.valueOf(sldX.getValue()) ));
		sldY.addChangeListener(_->magicCard.getCustomMetadata().put(EnumExtraCardMetaData.Y, String.valueOf(sldY.getValue()) ));
		chkWhiteText.addItemListener(_->magicCard.getCustomMetadata().put(EnumExtraCardMetaData.TEXT_COLOR, chkWhiteText.isSelected()?"#ffffff":"#000000"));
		
		//
		var bindingGroup = new BindingGroup();
		//
		bindingGroup.addBinding(autoBinding);
		bindingGroup.addBinding(autoBinding2);
		bindingGroup.addBinding(autoBinding3);
		bindingGroup.addBinding(autoBinding10);
		bindingGroup.addBinding(autoBinding13);
		bindingGroup.addBinding(autoBinding14);
		bindingGroup.addBinding(autoBinding16);
		bindingGroup.addBinding(autoBinding17);
		bindingGroup.addBinding(autoBinding19);
		bindingGroup.addBinding(autoBinding20);
		return bindingGroup;
	}



}
