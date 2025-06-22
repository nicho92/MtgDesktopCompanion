package org.magic.gui.components.editor;

import static org.magic.services.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
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
import org.magic.api.beans.enums.EnumRarity;
import org.magic.api.interfaces.MTGPictureEditor;
import org.magic.api.interfaces.MTGPictureEditor.MOD;
import org.magic.api.interfaces.MTGTextGenerator;
import org.magic.api.interfaces.abstracts.AbstractPicturesEditorProvider;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.card.MagicTextPane;
import org.magic.gui.components.dialog.importer.WallPaperChooseDialog;
import org.magic.gui.components.widgets.JLangLabel;
import org.magic.services.MTGConstants;
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
	private JComboBox<String> layoutJComboBox;
	private JTextField loyaltyJTextField;
	private JTextField nameJTextField;
	private JTextField numberJTextField;
	private JTextField powerJTextField;
	private JComboBox<EnumRarity> rarityJComboBox;
	private MagicTextPane textJEditorPane;
	private JTextField toughnessJTextField;
	private JTextField watermarksJTextField;
	private JPanel panelPT;
	private JLabel label;
	private JLabel lblType;
	private JPanel panelType;
	private JCheckableListBox<String> cboSuperType;
	private JCheckableListBox<String> cboTypes;
	private JTextField txtSubTypes;
	private JPanel panelButton;
	private JCheckableListBox<String> cboSubtypes;
	private JLabel lblTxtSize;
	private JSpinner spinner;
	private JLabel lblColorIndicator;
	private JCheckBox chkColorIndicator;
	private JPanel panelImageButtons;
	private JButton btnImage;
	private JButton btnUrl;
	private JButton btnWallpaper;
	private CropImagePanel imagePanel;
	private JLabel lblPromo;
	private JCheckBox chkPromo;

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

		var nameLabel = new JLangLabel("NAME",true);
		var labelgbc10 = new GridBagConstraints();
		labelgbc10.insets = new Insets(5, 5, 5, 5);
		labelgbc10.gridx = 0;
		labelgbc10.gridy = 0;
		add(nameLabel, labelgbc10);

		nameJTextField = new JTextField();
		var componentgbc10 = new GridBagConstraints();
		componentgbc10.insets = new Insets(5, 0, 5, 5);
		componentgbc10.fill = GridBagConstraints.HORIZONTAL;
		componentgbc10.gridx = 1;
		componentgbc10.gridy = 0;
		add(nameJTextField, componentgbc10);

		var costLabel = new JLangLabel("CARD_MANA",true);
		var labelgbc2 = new GridBagConstraints();
		labelgbc2.insets = new Insets(5, 5, 5, 5);
		labelgbc2.gridx = 2;
		labelgbc2.gridy = 0;
		add(costLabel, labelgbc2);

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

					magicCard.setCmc(cmc);
					magicCard.setColors(new ArrayList<>(colors));
					magicCard.setColorIdentity(new ArrayList<>(colors));
					costJTextField.setText(cost.toString());
					g.dispose();
				});

				g.getContentPane().add(new JLabel(
						new ImageIcon(IconsProvider.getInstance().getManaSymbol("1").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
				g.getContentPane().add(cboUn);
				g.getContentPane().add(new JLabel(
						new ImageIcon(IconsProvider.getInstance().getManaSymbol("W").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
				g.getContentPane().add(cboW);
				g.getContentPane().add(new JLabel(
						new ImageIcon(IconsProvider.getInstance().getManaSymbol("U").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
				g.getContentPane().add(cboU);
				g.getContentPane().add(new JLabel(
						new ImageIcon(IconsProvider.getInstance().getManaSymbol("B").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
				g.getContentPane().add(cboB);
				g.getContentPane().add(new JLabel(
						new ImageIcon(IconsProvider.getInstance().getManaSymbol("R").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
				g.getContentPane().add(cboR);
				g.getContentPane().add(new JLabel(
						new ImageIcon(IconsProvider.getInstance().getManaSymbol("G").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
				g.getContentPane().add(cboG);
				g.getContentPane().add(new JLabel(
						new ImageIcon(IconsProvider.getInstance().getManaSymbol("C").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
				g.getContentPane().add(cboC);
				g.getContentPane().add(btn);
				g.setLocationRelativeTo(null);
				g.pack();
				g.setVisible(true);

			}

		});

		var componentgbc2 = new GridBagConstraints();
		componentgbc2.insets = new Insets(5, 0, 5, 0);
		componentgbc2.fill = GridBagConstraints.HORIZONTAL;
		componentgbc2.gridx = 3;
		componentgbc2.gridy = 0;
		add(costJTextField, componentgbc2);

		var artistLabel = new JLabel(
				capitalize("CARD_ARTIST") + " :");
		var labelgbc0 = new GridBagConstraints();
		labelgbc0.insets = new Insets(5, 5, 5, 5);
		labelgbc0.gridx = 0;
		labelgbc0.gridy = 1;
		add(artistLabel, labelgbc0);

		artistJTextField = new JTextField();
		var componentgbc0 = new GridBagConstraints();
		componentgbc0.insets = new Insets(5, 0, 5, 5);
		componentgbc0.fill = GridBagConstraints.HORIZONTAL;
		componentgbc0.gridx = 1;
		componentgbc0.gridy = 1;
		add(artistJTextField, componentgbc0);

		var rarityLabel = new JLabel(
				capitalize("CARD_RARITY") + " :");
		var labelgbc14 = new GridBagConstraints();
		labelgbc14.insets = new Insets(5, 5, 5, 5);
		labelgbc14.gridx = 2;
		labelgbc14.gridy = 1;
		add(rarityLabel, labelgbc14);

		rarityJComboBox = UITools.createCombobox(EnumRarity.values());

		var componentgbc14 = new GridBagConstraints();
		componentgbc14.insets = new Insets(5, 0, 5, 0);
		componentgbc14.fill = GridBagConstraints.HORIZONTAL;
		componentgbc14.gridx = 3;
		componentgbc14.gridy = 1;
		add(rarityJComboBox, componentgbc14);

		lblType = new JLangLabel("CARD_TYPES",true);
		var gbclblType = new GridBagConstraints();
		gbclblType.insets = new Insets(0, 0, 5, 5);
		gbclblType.gridx = 0;
		gbclblType.gridy = 2;
		add(lblType, gbclblType);

		panelType = new JPanel();
		var gbcpanelType = new GridBagConstraints();
		gbcpanelType.gridwidth = 3;
		gbcpanelType.insets = new Insets(0, 0, 5, 0);
		gbcpanelType.fill = GridBagConstraints.BOTH;
		gbcpanelType.gridx = 1;
		gbcpanelType.gridy = 2;
		add(panelType, gbcpanelType);
		panelType.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		cboSuperType = new JCheckableListBox<>();
		var modelSt = new DefaultListCheckModel();
		cboSuperType.setModel(modelSt);
		for (String t : new String[] { "", "Basic", "Elite", "Legendary", "Ongoing", "Snow", "World" }) {
			modelSt.addElement(t);
		}
		panelType.add(cboSuperType);

		cboTypes = new JCheckableListBox<>();
		var model = new DefaultListCheckModel();
		cboTypes.setModel(model);
		for (String t : new String[] { "", "Arcane", "Artifact", "Aura", "Clue", "Conspiracy", "Continuous",
				"Contraption", "Creature", "Curse", "Elite", "Enchantment", "Equipment", "Fortification",
				"Global enchantment", "Hero", "Instant", "Interrupt", "Land", "Local", "Mana source",
				"Mono", "Ongoing", "Permanent", "Phenomenon", "Plane", "Planeswalker", "Poly", "Scheme", "Shrine",
				"Snow", "Sorcery", "Spell", "Summon", "Trap", "Tribal", "Vanguard", "Vehicle", "World" }) {
			model.addElement(t);
		}

		panelType.add(cboTypes);

		cboTypes.setModel(model);

		cboSubtypes = new JCheckableListBox<>();
		panelType.add(cboSubtypes);

		txtSubTypes = new JTextField();
		txtSubTypes.addActionListener(_ -> {
			cboSubtypes.addElement(txtSubTypes.getText(), true);
			txtSubTypes.setText("");
		});

		panelType.add(txtSubTypes);
		txtSubTypes.setColumns(10);

		panelButton = new JPanel();
		FlowLayout flpanelButton = (FlowLayout) panelButton.getLayout();
		flpanelButton.setAlignment(FlowLayout.LEFT);
		var gbcpanelButton = new GridBagConstraints();
		gbcpanelButton.gridwidth = 2;
		gbcpanelButton.insets = new Insets(0, 0, 5, 5);
		gbcpanelButton.fill = GridBagConstraints.BOTH;
		gbcpanelButton.gridx = 1;
		gbcpanelButton.gridy = 3;
		add(panelButton, gbcpanelButton);

		var symbolcs = new String[] { "W", "U", "B", "R", "G", "C", "T", "E" };
		for (String s : symbolcs) {
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
		var textLabel = new JLangLabel("CARD_TEXT",true);
		var labelgbc16 = new GridBagConstraints();
		labelgbc16.insets = new Insets(5, 5, 5, 5);
		labelgbc16.gridx = 0;
		labelgbc16.gridy = 4;
		add(textLabel, labelgbc16);
		var panelEditor = new JPanel();
		panelEditor.setLayout(new BorderLayout());


		textJEditorPane = new MagicTextPane(false);
		var componentgbc16 = new GridBagConstraints();
		componentgbc16.gridwidth = 3;
		componentgbc16.gridheight = 3;
		componentgbc16.insets = new Insets(5, 0, 5, 0);
		componentgbc16.fill = GridBagConstraints.BOTH;
		componentgbc16.gridx = 1;
		componentgbc16.gridy = 4;

		panelEditor.add(new JScrollPane(textJEditorPane),BorderLayout.CENTER);
		var scrollPane = new JScrollPane(new JSuggestedPanel(textJEditorPane,MTG.getEnabledPlugin(MTGTextGenerator.class)));
		scrollPane.setPreferredSize(new Dimension(panelEditor.getWidth(), 60));
		panelEditor.add(scrollPane,BorderLayout.SOUTH);
		panelEditor.setPreferredSize(new Dimension(textJEditorPane.getWidth(), 150));

		add(panelEditor, componentgbc16);

		var flavorLabel = new JLangLabel("CARD_FLAVOR",true);
		var labelgbc3 = new GridBagConstraints();
		labelgbc3.insets = new Insets(5, 5, 5, 5);
		labelgbc3.gridx = 0;
		labelgbc3.gridy = 7;
		add(flavorLabel, labelgbc3);

		flavorJTextField = new JTextField();
		var componentgbc3 = new GridBagConstraints();
		componentgbc3.gridwidth = 3;
		componentgbc3.insets = new Insets(5, 0, 5, 0);
		componentgbc3.fill = GridBagConstraints.HORIZONTAL;
		componentgbc3.gridx = 1;
		componentgbc3.gridy = 7;
		add(flavorJTextField, componentgbc3);

		var layoutLabel = new JLangLabel("CARD_LAYOUT",true);
		var labelgbc6 = new GridBagConstraints();
		labelgbc6.insets = new Insets(5, 5, 5, 5);
		labelgbc6.gridx = 0;
		labelgbc6.gridy = 8;
		add(layoutLabel, labelgbc6);

		layoutJComboBox = new JComboBox<>();
		layoutJComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "normal", "split", "flip", "double-faced",
				"token", "plane", "scheme", "phenomenon", "leveler", "vanguard", "meld", "token", "aftermath" }));

		var componentgbc6 = new GridBagConstraints();
		componentgbc6.insets = new Insets(5, 0, 5, 5);
		componentgbc6.fill = GridBagConstraints.HORIZONTAL;
		componentgbc6.gridx = 1;
		componentgbc6.gridy = 8;
		add(layoutJComboBox, componentgbc6);

		var powerLabel = new JLabel(capitalize("CARD_POWER") + "/" + capitalize("CARD_TOUGHNESS") + ":");
		var labelgbc13 = new GridBagConstraints();
		labelgbc13.insets = new Insets(5, 5, 5, 5);
		labelgbc13.gridx = 2;
		labelgbc13.gridy = 8;
		add(powerLabel, labelgbc13);

		panelPT = new JPanel();
		var gbcpanelPT = new GridBagConstraints();
		gbcpanelPT.insets = new Insets(0, 0, 5, 0);
		gbcpanelPT.fill = GridBagConstraints.BOTH;
		gbcpanelPT.gridx = 3;
		gbcpanelPT.gridy = 8;
		add(panelPT, gbcpanelPT);

		powerJTextField = new JTextField();
		powerJTextField.setColumns(2);
		panelPT.add(powerJTextField);

		label = new JLabel("/");
		panelPT.add(label);

		toughnessJTextField = new JTextField();
		toughnessJTextField.setColumns(2);
		panelPT.add(toughnessJTextField);

		var watermarksLabel = new JLabel(
				capitalize("CARD_WATERMARK") + " :");
		var labelgbc19 = new GridBagConstraints();
		labelgbc19.insets = new Insets(5, 5, 5, 5);
		labelgbc19.gridx = 0;
		labelgbc19.gridy = 9;
		add(watermarksLabel, labelgbc19);

		watermarksJTextField = new JTextField();
		var componentgbc19 = new GridBagConstraints();
		componentgbc19.insets = new Insets(5, 0, 5, 5);
		componentgbc19.fill = GridBagConstraints.HORIZONTAL;
		componentgbc19.gridx = 1;
		componentgbc19.gridy = 9;
		add(watermarksJTextField, componentgbc19);

		var loyaltyLabel = new JLangLabel("CARD_LOYALTY",true);
		var labelgbc7 = new GridBagConstraints();
		labelgbc7.insets = new Insets(5, 5, 5, 5);
		labelgbc7.gridx = 2;
		labelgbc7.gridy = 9;
		add(loyaltyLabel, labelgbc7);

		loyaltyJTextField = new JTextField();
		var componentgbc7 = new GridBagConstraints();
		componentgbc7.insets = new Insets(5, 0, 5, 0);
		componentgbc7.fill = GridBagConstraints.HORIZONTAL;
		componentgbc7.gridx = 3;
		componentgbc7.gridy = 9;
		add(loyaltyJTextField, componentgbc7);

		var numberLabel = new JLangLabel("CARD_NUMBER",true);
		var labelgbc11 = new GridBagConstraints();
		labelgbc11.insets = new Insets(5, 5, 5, 5);
		labelgbc11.gridx = 0;
		labelgbc11.gridy = 10;
		add(numberLabel, labelgbc11);

		numberJTextField = new JTextField();
		var componentgbc11 = new GridBagConstraints();
		componentgbc11.insets = new Insets(5, 0, 5, 5);
		componentgbc11.fill = GridBagConstraints.HORIZONTAL;
		componentgbc11.gridx = 1;
		componentgbc11.gridy = 10;
		add(numberJTextField, componentgbc11);

		var lblFoil = new JLabel(
						"Foil :");
				var gbclblFoil = new GridBagConstraints();
				gbclblFoil.insets = new Insets(5, 5, 5, 5);
				gbclblFoil.gridx = 2;
				gbclblFoil.gridy = 10;
				add(lblFoil, gbclblFoil);

				chkFoil = new JCheckBox();
				var gbcchboxFoil = new GridBagConstraints();
				gbcchboxFoil.insets = new Insets(5, 0, 5, 0);
				gbcchboxFoil.gridx = 3;
				gbcchboxFoil.gridy = 10;
				add(chkFoil, gbcchboxFoil);


		lblTxtSize = new JLabel("Text Size :");
		var gbclblTxtSize = new GridBagConstraints();
		gbclblTxtSize.insets = new Insets(0, 0, 5, 5);
		gbclblTxtSize.gridx = 2;
		gbclblTxtSize.gridy = 11;
		add(lblTxtSize, gbclblTxtSize);

		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(32, 18, 38, 1));
		var gbcspinner = new GridBagConstraints();
		gbcspinner.insets = new Insets(0, 0, 5, 0);
		gbcspinner.gridx = 3;
		gbcspinner.gridy = 11;
		add(spinner, gbcspinner);

		panelImageButtons = new JPanel();
		var gbcpanelImageButtons = new GridBagConstraints();
		gbcpanelImageButtons.gridheight = 4;
		gbcpanelImageButtons.insets = new Insets(0, 0, 0, 5);
		gbcpanelImageButtons.fill = GridBagConstraints.BOTH;
		gbcpanelImageButtons.gridx = 0;
		gbcpanelImageButtons.gridy = 12;
		add(panelImageButtons, gbcpanelImageButtons);
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
		var gbcbtnImage = new GridBagConstraints();
		gbcbtnImage.fill = GridBagConstraints.HORIZONTAL;
		gbcbtnImage.anchor = GridBagConstraints.NORTH;
		gbcbtnImage.insets = new Insets(0, 0, 5, 0);
		gbcbtnImage.gridx = 0;
		gbcbtnImage.gridy = 0;
		panelImageButtons.add(btnImage, gbcbtnImage);

		btnUrl = new JButton("URL");
		var gbcbtnUrl = new GridBagConstraints();
		gbcbtnUrl.fill = GridBagConstraints.HORIZONTAL;
		gbcbtnUrl.anchor = GridBagConstraints.NORTH;
		gbcbtnUrl.gridx = 0;
		gbcbtnUrl.gridy = 1;
		panelImageButtons.add(btnUrl, gbcbtnUrl);
		btnUrl.addActionListener(_->{
					String urlImage = JOptionPane.showInputDialog("URL");
					magicCard.setUrl(urlImage);
					showCrop();
		});
		
		btnWallpaper = new JButton("WallPaper");
		var gbcbtnWallpaperl = new GridBagConstraints();
		gbcbtnWallpaperl.fill = GridBagConstraints.HORIZONTAL;
		gbcbtnWallpaperl.anchor = GridBagConstraints.NORTH;
		gbcbtnWallpaperl.gridx = 0;
		gbcbtnWallpaperl.gridy = 2;
		panelImageButtons.add(btnWallpaper, gbcbtnWallpaperl);
		btnWallpaper.addActionListener(_->{
		
					var wallChooser = new WallPaperChooseDialog();
					wallChooser.setVisible(true);
					
					if(wallChooser.hasSelected())
					{
						magicCard.setUrl(wallChooser.getSelectedItem().getUrl().toASCIIString());
						magicCard.setArtist(wallChooser.getSelectedItem().getAuthor());
						artistJTextField.setText(wallChooser.getSelectedItem().getAuthor());
						imagePanel.setImage(wallChooser.getSelectedItem().getPicture());
					}
					showCrop();
		
		});
		
		
		imagePanel = new CropImagePanel();
		imagePanel.setBorder(new LineBorder(Color.BLACK));
		var gbcimagePanel = new GridBagConstraints();
		gbcimagePanel.gridheight = 4;
		gbcimagePanel.insets = new Insets(0, 0, 0, 5);
		gbcimagePanel.fill = GridBagConstraints.BOTH;
		gbcimagePanel.gridx = 1;
		gbcimagePanel.gridy = 12;
		add(imagePanel, gbcimagePanel);

		lblColorIndicator = new JLabel("Color Indicator");
		var gbclblColorIndicator = new GridBagConstraints();
		gbclblColorIndicator.insets = new Insets(0, 0, 5, 5);
		gbclblColorIndicator.gridx = 2;
		gbclblColorIndicator.gridy = 12;
		add(lblColorIndicator, gbclblColorIndicator);

		chkColorIndicator = new JCheckBox("");
		var gbcchkColorIndicator = new GridBagConstraints();
		gbcchkColorIndicator.insets = new Insets(0, 0, 5, 0);
		gbcchkColorIndicator.gridx = 3;
		gbcchkColorIndicator.gridy = 12;
		add(chkColorIndicator, gbcchkColorIndicator);

		lblPromo = new JLabel("Promo :");
		GridBagConstraints gbclblFrame = new GridBagConstraints();
		gbclblFrame.insets = new Insets(0, 0, 5, 5);
		gbclblFrame.gridx = 2;
		gbclblFrame.gridy = 14;
		add(lblPromo, gbclblFrame);
		
		chkPromo = new JCheckBox();
		GridBagConstraints gbcchkPromo = new GridBagConstraints();
		gbcchkPromo.insets = new Insets(0, 0, 5, 0);
		gbcchkPromo.fill = GridBagConstraints.HORIZONTAL;
		gbcchkPromo.gridx = 3;
		gbcchkPromo.gridy = 14;
		add(chkPromo, gbcchkPromo);
		
				
		
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
		return magicCard;
	}

	public void setMagicCard(MTGCard newMagicCard) {
		magicCard = newMagicCard;
		cboSuperType.unselectAll();
		cboTypes.unselectAll();
		cboSubtypes.unselectAll();
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
		
		BeanProperty<MTGCard, Boolean> promoProperty = BeanProperty.create("promoCard");
		BeanProperty<JCheckBox, Boolean> promoChkProperty = BeanProperty.create("selected");
		AutoBinding<MTGCard, Boolean, JCheckBox, Boolean> autoBinding21 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, promoProperty, chkPromo, promoChkProperty);
		autoBinding21.bind();
		
		BeanProperty<MTGCard, String> numberProperty = BeanProperty.create("number");
		BeanProperty<JTextField, String> numberTxtProperty = BeanProperty.create("text");
		AutoBinding<MTGCard, String, JTextField, String> autoBinding22 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, numberProperty, numberJTextField, numberTxtProperty);
		autoBinding22.bind();
		
		
		spinner.setValue(magicCard.getCustomMetadata().get(AbstractPicturesEditorProvider.SIZE)!=null? Integer.parseInt(magicCard.getCustomMetadata().get(AbstractPicturesEditorProvider.SIZE)):30);
		chkFoil.setSelected(Boolean.parseBoolean(magicCard.getCustomMetadata().get(AbstractPicturesEditorProvider.FOIL)));
		chkColorIndicator.setSelected(Boolean.parseBoolean(magicCard.getCustomMetadata().get(AbstractPicturesEditorProvider.INDICATOR)));
	
		spinner.addChangeListener(_->magicCard.getCustomMetadata().put(AbstractPicturesEditorProvider.SIZE, spinner.getValue().toString() ));
		chkFoil.addItemListener(_->magicCard.getCustomMetadata().put(AbstractPicturesEditorProvider.FOIL, String.valueOf(chkFoil.isSelected()) ));
		chkColorIndicator.addItemListener(_->magicCard.getCustomMetadata().put(AbstractPicturesEditorProvider.INDICATOR, String.valueOf(chkColorIndicator.isSelected()) ));
		
		
		
		
		
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
		bindingGroup.addBinding(autoBinding21);
		return bindingGroup;
	}



}
