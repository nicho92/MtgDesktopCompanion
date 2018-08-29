package org.magic.gui.components.editor;

import java.awt.Color;
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

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.LineBorder;

import org.apache.log4j.Logger;
import org.japura.gui.model.DefaultListCheckModel;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.magic.api.beans.MagicCard;
import org.magic.gui.components.MagicTextPane;
import org.magic.gui.components.ManaPanel;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.tools.URLTools;

public class MagicCardEditorPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient BindingGroup mbindingGroup;
	private MagicCard magicCard = new MagicCard();
	private JTextField artistJTextField;
	private JTextField costJTextField;
	private JTextField flavorJTextField;
	private JCheckBox chboxFoil;
	private JTextField gathererCodeJTextField;
	private JComboBox<String> layoutJComboBox;
	private JTextField loyaltyJTextField;
	private JTextField nameJTextField;
	private JTextField numberJTextField;
	private JTextField powerJTextField;
	private JComboBox<String> rarityJComboBox;
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
	private ManaPanel pan = new ManaPanel();
	private JCheckableListBox<String> cboSubtypes;
	private JLabel lblTxtSize;
	private JSpinner spinner;
	private JLabel lblColorIndicator;
	private JCheckBox chkColorIndicator;
	private JLabel lblColorOrientation;
	private JComboBox cboColorAccent;
	private JPanel panelImageButtons;
	private JButton btnImage;
	private JButton btnUrl;
	private CropImagePanel imagePanel;	
	private transient Logger logger = MTGLogger.getLogger(this.getClass());


	public MagicCardEditorPanel(org.magic.api.beans.MagicCard newMagicCard) {
		setMagicCard(newMagicCard);
	}

	public MagicCardEditorPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 279, 122, 103, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 31, 28, 0, 56, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, 0.0, 1.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0,
				1.0E-4 };
		setLayout(gridBagLayout);

		JLabel nameLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("NAME") + ":");
		GridBagConstraints labelgbc10 = new GridBagConstraints();
		labelgbc10.insets = new Insets(5, 5, 5, 5);
		labelgbc10.gridx = 0;
		labelgbc10.gridy = 0;
		add(nameLabel, labelgbc10);

		nameJTextField = new JTextField();
		GridBagConstraints componentgbc10 = new GridBagConstraints();
		componentgbc10.insets = new Insets(5, 0, 5, 5);
		componentgbc10.fill = GridBagConstraints.HORIZONTAL;
		componentgbc10.gridx = 1;
		componentgbc10.gridy = 0;
		add(nameJTextField, componentgbc10);

		JLabel costLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_MANA") + ":");
		GridBagConstraints labelgbc2 = new GridBagConstraints();
		labelgbc2.insets = new Insets(5, 5, 5, 5);
		labelgbc2.gridx = 2;
		labelgbc2.gridy = 0;
		add(costLabel, labelgbc2);

		costJTextField = new JTextField();
		costJTextField.setEditable(false);
		costJTextField.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				final JDialog g = new JDialog();
				g.getContentPane().setLayout(new FlowLayout());
				g.setModal(true);

				String[] data = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
				final JComboBox<String> cboW = new JComboBox<>(data);
				final JComboBox<String> cboU = new JComboBox<>(data);
				final JComboBox<String> cboB = new JComboBox<>(data);
				final JComboBox<String> cboR = new JComboBox<>(data);
				final JComboBox<String> cboG = new JComboBox<>(data);
				final JComboBox<String> cboC = new JComboBox<>(data);
				final JComboBox<String> cboUn = new JComboBox<>(data);
				cboUn.addItem("X");

				JButton btn = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SET_COST") + ":");
				btn.addActionListener(ev -> {
					StringBuilder cost = new StringBuilder();
					int cmc = 0;
					Set<String> colors = new LinkedHashSet<>();

					if (!cboUn.getSelectedItem().equals("X")) {
						if (cboUn.getSelectedIndex() > 0) {
							cost.append("{").append(cboUn.getSelectedIndex()).append("}");
							cmc += cboUn.getSelectedIndex();
						}
					} else {
						cost.append("{X}");
					}

					for (int i = 0; i < cboC.getSelectedIndex(); i++) {
						cost.append("{C}");
						cmc += 1;
					}

					for (int i = 0; i < cboW.getSelectedIndex(); i++) {
						cost.append("{W}");
						cmc += 1;
						colors.add("White");
					}

					for (int i = 0; i < cboU.getSelectedIndex(); i++) {
						cost.append("{U}");
						cmc += 1;
						colors.add("Blue");
					}

					for (int i = 0; i < cboB.getSelectedIndex(); i++) {
						cost.append("{B}");
						cmc += 1;
						colors.add("Black");
					}

					for (int i = 0; i < cboR.getSelectedIndex(); i++) {
						cost.append("{R}");
						cmc += 1;
						colors.add("Red");
					}

					for (int i = 0; i < cboG.getSelectedIndex(); i++) {
						cost.append("{G}");
						cmc += 1;
						colors.add("Green");
					}

					magicCard.setCmc(cmc);
					magicCard.setColors(new ArrayList(colors));
					magicCard.setColorIdentity(new ArrayList(colors));
					costJTextField.setText(cost.toString());
					g.dispose();
				});

				g.getContentPane().add(new JLabel(
						new ImageIcon(pan.getManaSymbol("1").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
				g.getContentPane().add(cboUn);
				g.getContentPane().add(new JLabel(
						new ImageIcon(pan.getManaSymbol("W").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
				g.getContentPane().add(cboW);
				g.getContentPane().add(new JLabel(
						new ImageIcon(pan.getManaSymbol("U").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
				g.getContentPane().add(cboU);
				g.getContentPane().add(new JLabel(
						new ImageIcon(pan.getManaSymbol("B").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
				g.getContentPane().add(cboB);
				g.getContentPane().add(new JLabel(
						new ImageIcon(pan.getManaSymbol("R").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
				g.getContentPane().add(cboR);
				g.getContentPane().add(new JLabel(
						new ImageIcon(pan.getManaSymbol("G").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
				g.getContentPane().add(cboG);
				g.getContentPane().add(new JLabel(
						new ImageIcon(pan.getManaSymbol("C").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
				g.getContentPane().add(cboC);
				g.getContentPane().add(btn);
				g.setLocationRelativeTo(null);
				g.pack();
				g.setVisible(true);

			}

		});

		GridBagConstraints componentgbc2 = new GridBagConstraints();
		componentgbc2.insets = new Insets(5, 0, 5, 0);
		componentgbc2.fill = GridBagConstraints.HORIZONTAL;
		componentgbc2.gridx = 3;
		componentgbc2.gridy = 0;
		add(costJTextField, componentgbc2);

		JLabel artistLabel = new JLabel(
				MTGControler.getInstance().getLangService().getCapitalize("CARD_ARTIST") + " :");
		GridBagConstraints labelgbc0 = new GridBagConstraints();
		labelgbc0.insets = new Insets(5, 5, 5, 5);
		labelgbc0.gridx = 0;
		labelgbc0.gridy = 1;
		add(artistLabel, labelgbc0);

		artistJTextField = new JTextField();
		GridBagConstraints componentgbc0 = new GridBagConstraints();
		componentgbc0.insets = new Insets(5, 0, 5, 5);
		componentgbc0.fill = GridBagConstraints.HORIZONTAL;
		componentgbc0.gridx = 1;
		componentgbc0.gridy = 1;
		add(artistJTextField, componentgbc0);

		JLabel rarityLabel = new JLabel(
				MTGControler.getInstance().getLangService().getCapitalize("CARD_RARITY") + " :");
		GridBagConstraints labelgbc14 = new GridBagConstraints();
		labelgbc14.insets = new Insets(5, 5, 5, 5);
		labelgbc14.gridx = 2;
		labelgbc14.gridy = 1;
		add(rarityLabel, labelgbc14);

		rarityJComboBox = new JComboBox();
		rarityJComboBox.setModel(new DefaultComboBoxModel<String>(
				new String[] { "Common", "Uncommon", "Rare", "Mythic Rare", "Special" }));
		GridBagConstraints componentgbc14 = new GridBagConstraints();
		componentgbc14.insets = new Insets(5, 0, 5, 0);
		componentgbc14.fill = GridBagConstraints.HORIZONTAL;
		componentgbc14.gridx = 3;
		componentgbc14.gridy = 1;
		add(rarityJComboBox, componentgbc14);

		lblType = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_TYPES") + " :");
		GridBagConstraints gbclblType = new GridBagConstraints();
		gbclblType.insets = new Insets(0, 0, 5, 5);
		gbclblType.gridx = 0;
		gbclblType.gridy = 2;
		add(lblType, gbclblType);

		panelType = new JPanel();
		GridBagConstraints gbcpanelType = new GridBagConstraints();
		gbcpanelType.gridwidth = 3;
		gbcpanelType.insets = new Insets(0, 0, 5, 0);
		gbcpanelType.fill = GridBagConstraints.BOTH;
		gbcpanelType.gridx = 1;
		gbcpanelType.gridy = 2;
		add(panelType, gbcpanelType);
		panelType.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		cboSuperType = new JCheckableListBox<>();
		DefaultListCheckModel modelSt = new DefaultListCheckModel();
		cboSuperType.setModel(modelSt);
		for (String t : new String[] { "", "Basic", "Elite", "Legendary", "Ongoing", "Snow", "World" }) {
			modelSt.addElement(t);
		}
		panelType.add(cboSuperType);

		cboTypes = new JCheckableListBox<>();
		DefaultListCheckModel model = new DefaultListCheckModel();
		cboTypes.setModel(model);
		for (String t : new String[] { "", "Arcane", "Artifact", "Aura", "Basic", "Clue", "Conspiracy", "Continuous",
				"Contraption", "Creature", "Curse", "Elite", "Enchantment", "Equipment", "Fortification",
				"Global enchantment", "Hero", "Instant", "Interrupt", "Land", "Legendary", "Local", "Mana source",
				"Mono", "Ongoing", "Permanent", "Phenomenon", "Plane", "Planeswalker", "Poly", "Scheme", "Shrine",
				"Snow", "Sorcery", "Spell", "Summon", "Trap", "Tribal", "Vanguard", "Vehicle", "World" }) {
			model.addElement(t);
		}

		panelType.add(cboTypes);

		cboTypes.setModel(model);

		cboSubtypes = new JCheckableListBox<>();
		panelType.add(cboSubtypes);

		txtSubTypes = new JTextField();
		txtSubTypes.addActionListener(ae -> {
			cboSubtypes.addElement(txtSubTypes.getText(), true);
			txtSubTypes.setText("");
		});

		panelType.add(txtSubTypes);
		txtSubTypes.setColumns(10);

		panelButton = new JPanel();
		FlowLayout flpanelButton = (FlowLayout) panelButton.getLayout();
		flpanelButton.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbcpanelButton = new GridBagConstraints();
		gbcpanelButton.gridwidth = 2;
		gbcpanelButton.insets = new Insets(0, 0, 5, 5);
		gbcpanelButton.fill = GridBagConstraints.BOTH;
		gbcpanelButton.gridx = 1;
		gbcpanelButton.gridy = 3;
		add(panelButton, gbcpanelButton);

		String[] symbolcs = new String[] { "W", "U", "B", "R", "G", "C", "T", "E" };
		for (String s : symbolcs) {
			final JButton btnG = new JButton();
			btnG.setToolTipText(s);
			btnG.setIcon(new ImageIcon(pan.getManaSymbol(s).getScaledInstance(15, 15, Image.SCALE_SMOOTH)));
			btnG.setForeground(btnG.getBackground());

			btnG.addActionListener(
					e -> textJEditorPane.setText(textJEditorPane.getText() + " {" + btnG.getToolTipText() + "}"));

			panelButton.add(btnG);

		}
		JLabel textLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_TEXT") + ":");
		GridBagConstraints labelgbc16 = new GridBagConstraints();
		labelgbc16.insets = new Insets(5, 5, 5, 5);
		labelgbc16.gridx = 0;
		labelgbc16.gridy = 4;
		add(textLabel, labelgbc16);

		textJEditorPane = new MagicTextPane();
		GridBagConstraints componentgbc16 = new GridBagConstraints();
		componentgbc16.gridwidth = 3;
		componentgbc16.gridheight = 2;
		componentgbc16.insets = new Insets(5, 0, 5, 0);
		componentgbc16.fill = GridBagConstraints.BOTH;
		componentgbc16.gridx = 1;
		componentgbc16.gridy = 4;
		add(textJEditorPane, componentgbc16);

		JLabel flavorLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_FLAVOR") + ":");
		GridBagConstraints labelgbc3 = new GridBagConstraints();
		labelgbc3.insets = new Insets(5, 5, 5, 5);
		labelgbc3.gridx = 0;
		labelgbc3.gridy = 6;
		add(flavorLabel, labelgbc3);

		flavorJTextField = new JTextField();
		GridBagConstraints componentgbc3 = new GridBagConstraints();
		componentgbc3.gridwidth = 3;
		componentgbc3.insets = new Insets(5, 0, 5, 0);
		componentgbc3.fill = GridBagConstraints.HORIZONTAL;
		componentgbc3.gridx = 1;
		componentgbc3.gridy = 6;
		add(flavorJTextField, componentgbc3);

		JLabel layoutLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_LAYOUT") + ":");
		GridBagConstraints labelgbc6 = new GridBagConstraints();
		labelgbc6.insets = new Insets(5, 5, 5, 5);
		labelgbc6.gridx = 0;
		labelgbc6.gridy = 7;
		add(layoutLabel, labelgbc6);

		layoutJComboBox = new JComboBox();
		layoutJComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "normal", "split", "flip", "double-faced",
				"token", "plane", "scheme", "phenomenon", "leveler", "vanguard", "meld", "token", "aftermath" }));

		GridBagConstraints componentgbc6 = new GridBagConstraints();
		componentgbc6.insets = new Insets(5, 0, 5, 5);
		componentgbc6.fill = GridBagConstraints.HORIZONTAL;
		componentgbc6.gridx = 1;
		componentgbc6.gridy = 7;
		add(layoutJComboBox, componentgbc6);

		JLabel powerLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_POWER") + "/"
				+ MTGControler.getInstance().getLangService().getCapitalize("CARD_TOUGHNESS") + ":");
		GridBagConstraints labelgbc13 = new GridBagConstraints();
		labelgbc13.insets = new Insets(5, 5, 5, 5);
		labelgbc13.gridx = 2;
		labelgbc13.gridy = 7;
		add(powerLabel, labelgbc13);

		panelPT = new JPanel();
		GridBagConstraints gbcpanelPT = new GridBagConstraints();
		gbcpanelPT.insets = new Insets(0, 0, 5, 0);
		gbcpanelPT.fill = GridBagConstraints.BOTH;
		gbcpanelPT.gridx = 3;
		gbcpanelPT.gridy = 7;
		add(panelPT, gbcpanelPT);

		powerJTextField = new JTextField();
		powerJTextField.setColumns(2);
		panelPT.add(powerJTextField);

		label = new JLabel("/");
		panelPT.add(label);

		toughnessJTextField = new JTextField();
		toughnessJTextField.setColumns(2);
		panelPT.add(toughnessJTextField);

		JLabel watermarksLabel = new JLabel(
				MTGControler.getInstance().getLangService().getCapitalize("CARD_WATERMARK") + " :");
		GridBagConstraints labelgbc19 = new GridBagConstraints();
		labelgbc19.insets = new Insets(5, 5, 5, 5);
		labelgbc19.gridx = 0;
		labelgbc19.gridy = 8;
		add(watermarksLabel, labelgbc19);

		watermarksJTextField = new JTextField();
		GridBagConstraints componentgbc19 = new GridBagConstraints();
		componentgbc19.insets = new Insets(5, 0, 5, 5);
		componentgbc19.fill = GridBagConstraints.HORIZONTAL;
		componentgbc19.gridx = 1;
		componentgbc19.gridy = 8;
		add(watermarksJTextField, componentgbc19);

		JLabel loyaltyLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_LOYALTY"));
		GridBagConstraints labelgbc7 = new GridBagConstraints();
		labelgbc7.insets = new Insets(5, 5, 5, 5);
		labelgbc7.gridx = 2;
		labelgbc7.gridy = 8;
		add(loyaltyLabel, labelgbc7);

		loyaltyJTextField = new JTextField();
		GridBagConstraints componentgbc7 = new GridBagConstraints();
		componentgbc7.insets = new Insets(5, 0, 5, 0);
		componentgbc7.fill = GridBagConstraints.HORIZONTAL;
		componentgbc7.gridx = 3;
		componentgbc7.gridy = 8;
		add(loyaltyJTextField, componentgbc7);

		JLabel numberLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_NUMBER"));
		GridBagConstraints labelgbc11 = new GridBagConstraints();
		labelgbc11.insets = new Insets(5, 5, 5, 5);
		labelgbc11.gridx = 0;
		labelgbc11.gridy = 9;
		add(numberLabel, labelgbc11);

		numberJTextField = new JTextField();
		GridBagConstraints componentgbc11 = new GridBagConstraints();
		componentgbc11.insets = new Insets(5, 0, 5, 5);
		componentgbc11.fill = GridBagConstraints.HORIZONTAL;
		componentgbc11.gridx = 1;
		componentgbc11.gridy = 9;
		add(numberJTextField, componentgbc11);
		
				JLabel lblFoil = new JLabel(
						"Foil :");
				GridBagConstraints gbclblFoil = new GridBagConstraints();
				gbclblFoil.insets = new Insets(5, 5, 5, 5);
				gbclblFoil.gridx = 2;
				gbclblFoil.gridy = 9;
				add(lblFoil, gbclblFoil);
		
				chboxFoil = new JCheckBox();
				GridBagConstraints gbcchboxFoil = new GridBagConstraints();
				gbcchboxFoil.insets = new Insets(5, 0, 5, 0);
				gbcchboxFoil.gridx = 3;
				gbcchboxFoil.gridy = 9;
				add(chboxFoil, gbcchboxFoil);
		
				JLabel gathererCodeLabel = new JLabel("Gatherer ID:");
				GridBagConstraints labelgbc5 = new GridBagConstraints();
				labelgbc5.insets = new Insets(5, 5, 5, 5);
				labelgbc5.gridx = 0;
				labelgbc5.gridy = 10;
				add(gathererCodeLabel, labelgbc5);
		
				gathererCodeJTextField = new JTextField();
				GridBagConstraints componentgbc5 = new GridBagConstraints();
				componentgbc5.insets = new Insets(5, 0, 5, 5);
				componentgbc5.fill = GridBagConstraints.HORIZONTAL;
				componentgbc5.gridx = 1;
				componentgbc5.gridy = 10;
				add(gathererCodeJTextField, componentgbc5);
		
		lblTxtSize = new JLabel("Text Size :");
		GridBagConstraints gbclblTxtSize = new GridBagConstraints();
		gbclblTxtSize.insets = new Insets(0, 0, 5, 5);
		gbclblTxtSize.gridx = 2;
		gbclblTxtSize.gridy = 10;
		add(lblTxtSize, gbclblTxtSize);
		
		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(32, 18, 38, 1));
		GridBagConstraints gbcspinner = new GridBagConstraints();
		gbcspinner.insets = new Insets(0, 0, 5, 0);
		gbcspinner.gridx = 3;
		gbcspinner.gridy = 10;
		add(spinner, gbcspinner);
		
		panelImageButtons = new JPanel();
		GridBagConstraints gbcpanelImageButtons = new GridBagConstraints();
		gbcpanelImageButtons.gridheight = 3;
		gbcpanelImageButtons.insets = new Insets(0, 0, 0, 5);
		gbcpanelImageButtons.fill = GridBagConstraints.BOTH;
		gbcpanelImageButtons.gridx = 0;
		gbcpanelImageButtons.gridy = 11;
		add(panelImageButtons, gbcpanelImageButtons);
		GridBagLayout gblpanelImageButtons = new GridBagLayout();
		gblpanelImageButtons.columnWidths = new int[]{63, 0};
		gblpanelImageButtons.rowHeights = new int[]{23, 0, 0};
		gblpanelImageButtons.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gblpanelImageButtons.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panelImageButtons.setLayout(gblpanelImageButtons);
		
		btnImage = new JButton("Image");
		btnImage.addActionListener(e -> {

			JFileChooser choose = new JFileChooser();
			choose.showOpenDialog(null);
			File pics = choose.getSelectedFile();
			magicCard.setImageName(pics.getAbsolutePath());
			showCrop();
		});
		GridBagConstraints gbcbtnImage = new GridBagConstraints();
		gbcbtnImage.fill = GridBagConstraints.HORIZONTAL;
		gbcbtnImage.anchor = GridBagConstraints.NORTH;
		gbcbtnImage.insets = new Insets(0, 0, 5, 0);
		gbcbtnImage.gridx = 0;
		gbcbtnImage.gridy = 0;
		panelImageButtons.add(btnImage, gbcbtnImage);
		
		btnUrl = new JButton("URL");
		GridBagConstraints gbcbtnUrl = new GridBagConstraints();
		gbcbtnUrl.fill = GridBagConstraints.HORIZONTAL;
		gbcbtnUrl.anchor = GridBagConstraints.NORTH;
		gbcbtnUrl.gridx = 0;
		gbcbtnUrl.gridy = 1;
		panelImageButtons.add(btnUrl, gbcbtnUrl);
		btnUrl.addActionListener(ae->{
					String urlImage = JOptionPane.showInputDialog("URL");
					magicCard.setImageName(urlImage);
					showCrop();
		});
		
		imagePanel = new CropImagePanel();
		imagePanel.setBorder(new LineBorder(Color.BLACK));
		GridBagConstraints gbcimagePanel = new GridBagConstraints();
		gbcimagePanel.gridheight = 3;
		gbcimagePanel.insets = new Insets(0, 0, 5, 5);
		gbcimagePanel.fill = GridBagConstraints.BOTH;
		gbcimagePanel.gridx = 1;
		gbcimagePanel.gridy = 11;
		add(imagePanel, gbcimagePanel);
		
		lblColorIndicator = new JLabel("Color Indicator");
		GridBagConstraints gbclblColorIndicator = new GridBagConstraints();
		gbclblColorIndicator.insets = new Insets(0, 0, 5, 5);
		gbclblColorIndicator.gridx = 2;
		gbclblColorIndicator.gridy = 11;
		add(lblColorIndicator, gbclblColorIndicator);
		
		chkColorIndicator = new JCheckBox("");
		GridBagConstraints gbcchkColorIndicator = new GridBagConstraints();
		gbcchkColorIndicator.insets = new Insets(0, 0, 5, 0);
		gbcchkColorIndicator.gridx = 3;
		gbcchkColorIndicator.gridy = 11;
		add(chkColorIndicator, gbcchkColorIndicator);
		
		lblColorOrientation = new JLabel("Color Orientation :");
		GridBagConstraints gbclblColorOrientation = new GridBagConstraints();
		gbclblColorOrientation.anchor = GridBagConstraints.NORTH;
		gbclblColorOrientation.insets = new Insets(0, 0, 5, 5);
		gbclblColorOrientation.gridx = 2;
		gbclblColorOrientation.gridy = 12;
		add(lblColorOrientation, gbclblColorOrientation);
		
		cboColorAccent = new JComboBox<>(new DefaultComboBoxModel<String>(new String[] {"", "C", "G", "W", "WU", "WB", "U", "UB", "UR", "C", "B", "BR", "BG", "R", "RG", "TW", "G", "GW", "GU"}));
		
		GridBagConstraints gbccomboBox = new GridBagConstraints();
		gbccomboBox.anchor = GridBagConstraints.NORTH;
		gbccomboBox.insets = new Insets(0, 0, 5, 0);
		gbccomboBox.fill = GridBagConstraints.HORIZONTAL;
		gbccomboBox.gridx = 3;
		gbccomboBox.gridy = 12;
		add(cboColorAccent, gbccomboBox);

		if (magicCard != null) {
			mbindingGroup = initDataBindings();
		}
	}


	private void showCrop() {
	
		BufferedImage buff;
		try {
			if(magicCard.getImageName().startsWith("http"))
				buff = URLTools.extractImage(magicCard.getImageName());
			else
				buff = ImageIO.read(new File(magicCard.getImageName()));
			
			Image i = new ImageIcon(buff).getImage();
			imagePanel.setImage(i.getScaledInstance(imagePanel.getWidth(), imagePanel.getHeight(), Image.SCALE_SMOOTH));
			imagePanel.revalidate();
			imagePanel.repaint();
			
		} catch (Exception e) {
			logger.error(e);
		}
		
	}

	public JCheckBox getChboxFoil() {
		return chboxFoil;
	}

	public MagicCard getMagicCard() {
		magicCard.setTypes(cboTypes.getSelectedElements());
		magicCard.setSupertypes(cboSuperType.getSelectedElements());
		magicCard.setSubtypes(cboSubtypes.getSelectedElements());
		return magicCard;
	}

	public void setMagicCard(MagicCard newMagicCard) {
		setMagicCard(newMagicCard, true);
	}

	public void setMagicCard(MagicCard newMagicCard, boolean update) {
		magicCard = newMagicCard;
		cboSuperType.unselectAll();
		cboTypes.unselectAll();
		cboSubtypes.unselectAll();

		if (update) {
			if (mbindingGroup != null) {
				mbindingGroup.unbind();
				mbindingGroup = null;
			}
			if (magicCard != null) {
				mbindingGroup = initDataBindings();
			}
		}
		if (magicCard != null) {
			cboSuperType.setSelectedElements(magicCard.getSupertypes());
			cboTypes.setSelectedElements(magicCard.getTypes());
			cboSubtypes.setSelectedElements(magicCard.getSubtypes());
		}
		
	}
	
	public JCheckBox getColorIndicatorJCheckBox() {
		return chkColorIndicator;
	}
	protected BindingGroup initDataBindings() {
		BeanProperty<MagicCard, String> artistProperty = BeanProperty.create("artist");
		BeanProperty<JTextField, String> textProperty = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, artistProperty, artistJTextField, textProperty);
		autoBinding.bind();
		//
		BeanProperty<MagicCard, String> costProperty = BeanProperty.create("cost");
		BeanProperty<JTextField, String> textProperty1 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding2 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, costProperty, costJTextField, textProperty1);
		autoBinding2.bind();
		//
		BeanProperty<MagicCard, String> flavorProperty = BeanProperty.create("flavor");
		BeanProperty<JTextField, String> textProperty2 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding3 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, flavorProperty, flavorJTextField, textProperty2);
		autoBinding3.bind();
		//
		BeanProperty<MagicCard, Boolean> flippableProperty = BeanProperty.create("flippable");
		BeanProperty<JCheckBox, Boolean> selectedProperty = BeanProperty.create("selected");
		AutoBinding<MagicCard, Boolean, JCheckBox, Boolean> autoBinding4 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, flippableProperty, chboxFoil, selectedProperty);
		autoBinding4.bind();
		//
		BeanProperty<MagicCard, String> gathererCodeProperty = BeanProperty.create("gathererCode");
		BeanProperty<JTextField, String> textProperty3 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding5 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, gathererCodeProperty, gathererCodeJTextField, textProperty3);
		autoBinding5.bind();
		//
		BeanProperty<MagicCard, Object> layoutProperty = BeanProperty.create("layout");
		BeanProperty<JComboBox, Object> selectedIndexProperty = BeanProperty.create("selectedItem");
		AutoBinding<MagicCard, Object, JComboBox, Object> autoBinding6 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, layoutProperty, layoutJComboBox, selectedIndexProperty);
		autoBinding6.bind();
		//
		BeanProperty<MagicCard, Object> loyaltyProperty = BeanProperty.create("loyalty");
		BeanProperty<JTextField, Object> valueProperty1 = BeanProperty.create("value");
		AutoBinding<MagicCard, Object, JTextField, Object> autoBinding7 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, loyaltyProperty, loyaltyJTextField, valueProperty1);
		autoBinding7.bind();
		//
		BeanProperty<MagicCard, String> nameProperty = BeanProperty.create("name");
		BeanProperty<JTextField, String> textProperty5 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding10 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, nameProperty, nameJTextField, textProperty5);
		autoBinding10.bind();
		//
		BeanProperty<MagicCard, String> numberProperty = BeanProperty.create("number");
		BeanProperty<JTextField, Object> valueProperty3 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, Object> autoBinding11 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, numberProperty, numberJTextField, valueProperty3);
		autoBinding11.bind();
		//
		BeanProperty<MagicCard, String> powerProperty = BeanProperty.create("power");
		BeanProperty<JTextField, Object> valueProperty4 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, Object> autoBinding13 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, powerProperty, powerJTextField, valueProperty4);
		autoBinding13.bind();
		//
		BeanProperty<MagicCard, Object> rarityProperty = BeanProperty.create("rarity");
		BeanProperty<JComboBox, Object> selectedIndexProperty1 = BeanProperty.create("selectedItem");
		AutoBinding<MagicCard, Object, JComboBox, Object> autoBinding14 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, rarityProperty, rarityJComboBox, selectedIndexProperty1);
		autoBinding14.bind();
		//
		BeanProperty<MagicCard, String> textProperty8 = BeanProperty.create("text");
		BeanProperty<JEditorPane, String> textProperty9 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JEditorPane, String> autoBinding16 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, textProperty8, textJEditorPane, textProperty9);
		autoBinding16.bind();
		//
		BeanProperty<MagicCard, String> toughnessProperty = BeanProperty.create("toughness");
		BeanProperty<JTextField, Object> valueProperty5 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, Object> autoBinding17 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, toughnessProperty, toughnessJTextField, valueProperty5);
		autoBinding17.bind();
		//
		BeanProperty<MagicCard, String> watermarksProperty = BeanProperty.create("watermarks");
		BeanProperty<JTextField, String> textProperty10 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding19 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, watermarksProperty, watermarksJTextField, textProperty10);
		autoBinding19.bind();
		//
		BindingGroup bindingGroup = new BindingGroup();
		//
		bindingGroup.addBinding(autoBinding);
		bindingGroup.addBinding(autoBinding2);
		bindingGroup.addBinding(autoBinding3);
		bindingGroup.addBinding(autoBinding4);
		bindingGroup.addBinding(autoBinding5);
		bindingGroup.addBinding(autoBinding6);
		bindingGroup.addBinding(autoBinding7);
		bindingGroup.addBinding(autoBinding10);
		bindingGroup.addBinding(autoBinding11);
		bindingGroup.addBinding(autoBinding13);
		bindingGroup.addBinding(autoBinding14);
		bindingGroup.addBinding(autoBinding16);
		bindingGroup.addBinding(autoBinding17);
		bindingGroup.addBinding(autoBinding19);
		return bindingGroup;
	}
	public JSpinner getSizeSpinner() {
		return spinner;
	}
	public JComboBox getCboColorAccent() {
		return cboColorAccent;
	}
	public CropImagePanel getImagePanel() {
		return imagePanel;
	}
	
}
