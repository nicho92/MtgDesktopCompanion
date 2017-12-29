package org.magic.gui.components.editor;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.japura.gui.model.DefaultListCheckModel;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.MagicCard;
import org.magic.gui.components.MagicTextPane;
import org.magic.gui.components.ManaPanel;
import org.magic.services.MTGControler;

public class MagicCardEditorPanel extends JPanel {

	private BindingGroup m_bindingGroup;
	private MagicCard magicCard = new MagicCard();
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
	private MagicTextPane textJEditorPane;
	private JTextField toughnessJTextField;
	private JCheckBox tranformableJCheckBox;
	private JTextField watermarksJTextField;
	private JPanel panel;
	private JLabel label;
	private JLabel lblType;
	private JPanel panel_1;
	private JCheckableListBox<String> cboSuperType;
	private JCheckableListBox<String> cboTypes;
	private JTextField txtSubTypes;
	private JPanel panel_2;
	private ManaPanel pan = new ManaPanel();
	private JCheckableListBox<String> cboSubtypes;
	
	public MagicCardEditorPanel(org.magic.api.beans.MagicCard newMagicCard) {
		setMagicCard(newMagicCard);
	}

	public MagicCardEditorPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 279, 122, 103, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 31, 28, 0, 56, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4 };
		setLayout(gridBagLayout);
		
				JLabel nameLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("NAME")+":");
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
		
				JLabel costLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_MANA")+":");
				GridBagConstraints labelGbc_2 = new GridBagConstraints();
				labelGbc_2.insets = new Insets(5, 5, 5, 5);
				labelGbc_2.gridx = 2;
				labelGbc_2.gridy = 0;
				add(costLabel, labelGbc_2);
		
				costJTextField = new JTextField();
				costJTextField.setEditable(false);
				costJTextField.addMouseListener(new MouseAdapter() {
					
					@Override
					public void mouseClicked(MouseEvent e) {
						final JDialog g = new JDialog();
						g.getContentPane().setLayout(new FlowLayout());
						g.setModal(true);
						
						String[] data = {"0","1","2","3","4","5","6","7","8","9","10"};
						final JComboBox<String> cboW = new JComboBox<String>(data);
						final JComboBox<String> cboU = new JComboBox<String>(data);
						final JComboBox<String> cboB = new JComboBox<String>(data);
						final JComboBox<String> cboR = new JComboBox<String>(data);
						final JComboBox<String> cboG = new JComboBox<String>(data);
						final JComboBox<String> cboC = new JComboBox<String>(data);
						final JComboBox<String> cboUn = new JComboBox<String>(data);
						cboUn.addItem("X");
						
						
						JButton btn = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SET_COST")+":");
						btn.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								String cost="";
								int cmc=0;
								HashSet<String> colors = new HashSet<String>();
								
								if(!cboUn.getSelectedItem().equals("X"))
								{
									if(cboUn.getSelectedIndex()>0)
									{
										cost+="{"+cboUn.getSelectedIndex()+"}";
										cmc+=cboUn.getSelectedIndex();
									}
								}
								else
								{
									cost+="{X}";
								}

								for(int i=0;i<cboC.getSelectedIndex();i++)
								{
									cost+="{C}";
									cmc+=1;
								}
								
								for(int i=0;i<cboW.getSelectedIndex();i++)
								{
									cost+="{W}";
									cmc+=1;
									colors.add("White");
								}
								
								for(int i=0;i<cboU.getSelectedIndex();i++)
								{
									cost+="{U}";
									cmc+=1;
									colors.add("Blue");
								}
								
								for(int i=0;i<cboB.getSelectedIndex();i++)
								{
									cost+="{B}";
									cmc+=1;
									colors.add("Black");
								}
								
								for(int i=0;i<cboR.getSelectedIndex();i++)
								{
									cost+="{R}";
									cmc+=1;
									colors.add("Red");
								}
								
								for(int i=0;i<cboG.getSelectedIndex();i++)
								{
									cost+="{G}";
									cmc+=1;
									colors.add("Green");
								}
								
								magicCard.setCmc(cmc);
								magicCard.setColors(new ArrayList<String>(colors));
								magicCard.setColorIdentity(new ArrayList<String>(colors));
								costJTextField.setText(cost);
								g.dispose();
								
							}
						});
						
						
						g.getContentPane().add(new JLabel(new ImageIcon(pan.getManaSymbol("1").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
						g.getContentPane().add(cboUn);
						g.getContentPane().add(new JLabel(new ImageIcon(pan.getManaSymbol("W").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
						g.getContentPane().add(cboW);
						g.getContentPane().add(new JLabel(new ImageIcon(pan.getManaSymbol("U").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
						g.getContentPane().add(cboU);
						g.getContentPane().add(new JLabel(new ImageIcon(pan.getManaSymbol("B").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
						g.getContentPane().add(cboB);
						g.getContentPane().add(new JLabel(new ImageIcon(pan.getManaSymbol("R").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
						g.getContentPane().add(cboR);
						g.getContentPane().add(new JLabel(new ImageIcon(pan.getManaSymbol("G").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
						g.getContentPane().add(cboG);
						g.getContentPane().add(new JLabel(new ImageIcon(pan.getManaSymbol("C").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
						g.getContentPane().add(cboC);
						g.getContentPane().add(btn);
						g.setLocationRelativeTo(null);
						g.pack();
						g.setVisible(true);
						
						
					}
					
				});
				
				GridBagConstraints componentGbc_2 = new GridBagConstraints();
				componentGbc_2.insets = new Insets(5, 0, 5, 0);
				componentGbc_2.fill = GridBagConstraints.HORIZONTAL;
				componentGbc_2.gridx = 3;
				componentGbc_2.gridy = 0;
				add(costJTextField, componentGbc_2);

				JLabel artistLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_ARTIST")+" :");
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
				
				JLabel rarityLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_RARITY")+" :");
				GridBagConstraints labelGbc_14 = new GridBagConstraints();
				labelGbc_14.insets = new Insets(5, 5, 5, 5);
				labelGbc_14.gridx = 2;
				labelGbc_14.gridy = 1;
				add(rarityLabel, labelGbc_14);
		
				rarityJComboBox = new JComboBox();
				rarityJComboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"Common", "Uncommon", "Rare", "Mythic Rare", "Special"}));
				GridBagConstraints componentGbc_14 = new GridBagConstraints();
				componentGbc_14.insets = new Insets(5, 0, 5, 0);
				componentGbc_14.fill = GridBagConstraints.HORIZONTAL;
				componentGbc_14.gridx = 3;
				componentGbc_14.gridy = 1;
				add(rarityJComboBox, componentGbc_14);
		
				lblType = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_TYPES")+" :");
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
				panel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
				
				cboSuperType = new JCheckableListBox<String>();
				DefaultListCheckModel modelSt = new DefaultListCheckModel();
				cboSuperType.setModel(modelSt);
				for (String t : new String[] {"", "Basic", "Elite", "Legendary", "Ongoing", "Snow", "World"}) { 
					modelSt.addElement(t); 
				} 
				panel_1.add(cboSuperType);
				
				
				cboTypes = new JCheckableListBox<String>();
				DefaultListCheckModel model = new DefaultListCheckModel();
				cboTypes.setModel(model);
				for (String t : new String[] {"", "Arcane", "Artifact", "Aura", "Basic", "Clue", "Conspiracy", "Continuous", "Contraption", "Creature", "Curse", "Elite", "Enchantment", "Equipment", "Fortification", "Global enchantment", "Hero", "Instant", "Interrupt", "Land", "Legendary", "Local", "Mana source", "Mono", "Ongoing", "Permanent", "Phenomenon", "Plane", "Planeswalker", "Poly", "Scheme", "Shrine", "Snow", "Sorcery", "Spell", "Summon", "Trap", "Tribal", "Vanguard", "Vehicle", "World"}) { 
				   model.addElement(t); 
				} 
			
				panel_1.add(cboTypes);
				
				cboTypes.setModel(model);
				
				cboSubtypes = new JCheckableListBox<String>();
				panel_1.add(cboSubtypes);
		
				txtSubTypes = new JTextField();
				txtSubTypes.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						cboSubtypes.addElement(txtSubTypes.getText(),true);
						txtSubTypes.setText("");
					}
				});
				panel_1.add(txtSubTypes);
				txtSubTypes.setColumns(10);
						
						panel_2 = new JPanel();
						FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
						flowLayout.setAlignment(FlowLayout.LEFT);
						GridBagConstraints gbc_panel_2 = new GridBagConstraints();
						gbc_panel_2.gridwidth = 2;
						gbc_panel_2.insets = new Insets(0, 0, 5, 5);
						gbc_panel_2.fill = GridBagConstraints.BOTH;
						gbc_panel_2.gridx = 1;
						gbc_panel_2.gridy = 3;
						add(panel_2, gbc_panel_2);
						
						String[] symbolcs = new String[]{"W","U","B","R","G","C","T","E"};
						for(String s : symbolcs)
						{
							final JButton btnG = new JButton();
							btnG.setToolTipText(s);
							btnG.setIcon(new ImageIcon(pan.getManaSymbol(s).getScaledInstance(15, 15, Image.SCALE_SMOOTH)));
							btnG.setForeground(btnG.getBackground());
							
							btnG.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									textJEditorPane.setText(textJEditorPane.getText()+ " {" + btnG.getToolTipText()+"}");
									
								}
							});
							panel_2.add(btnG);
								
						}
							JLabel textLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_TEXT")+":");
						GridBagConstraints labelGbc_16 = new GridBagConstraints();
						labelGbc_16.insets = new Insets(5, 5, 5, 5);
						labelGbc_16.gridx = 0;
						labelGbc_16.gridy = 4;
						add(textLabel, labelGbc_16);
		
				textJEditorPane = new MagicTextPane();
				GridBagConstraints componentGbc_16 = new GridBagConstraints();
				componentGbc_16.gridwidth = 3;
				componentGbc_16.gridheight = 2;
				componentGbc_16.insets = new Insets(5, 0, 5, 0);
				componentGbc_16.fill = GridBagConstraints.BOTH;
				componentGbc_16.gridx = 1;
				componentGbc_16.gridy = 4;
				add(textJEditorPane, componentGbc_16);

		JLabel flavorLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_FLAVOR")+":");
		GridBagConstraints labelGbc_3 = new GridBagConstraints();
		labelGbc_3.insets = new Insets(5, 5, 5, 5);
		labelGbc_3.gridx = 0;
		labelGbc_3.gridy = 6;
		add(flavorLabel, labelGbc_3);

		flavorJTextField = new JTextField();
		GridBagConstraints componentGbc_3 = new GridBagConstraints();
		componentGbc_3.gridwidth = 3;
		componentGbc_3.insets = new Insets(5, 0, 5, 0);
		componentGbc_3.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_3.gridx = 1;
		componentGbc_3.gridy = 6;
		add(flavorJTextField, componentGbc_3);
				
						JLabel layoutLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_LAYOUT")+":");
						GridBagConstraints labelGbc_6 = new GridBagConstraints();
						labelGbc_6.insets = new Insets(5, 5, 5, 5);
						labelGbc_6.gridx = 0;
						labelGbc_6.gridy = 7;
						add(layoutLabel, labelGbc_6);
				
						layoutJComboBox = new JComboBox();
						layoutJComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"normal", "split", "flip", "double-faced", "token", "plane", "scheme", "phenomenon", "leveler", "vanguard", "meld","token","aftermath"}));
						
						GridBagConstraints componentGbc_6 = new GridBagConstraints();
						componentGbc_6.insets = new Insets(5, 0, 5, 5);
						componentGbc_6.fill = GridBagConstraints.HORIZONTAL;
						componentGbc_6.gridx = 1;
						componentGbc_6.gridy = 7;
						add(layoutJComboBox, componentGbc_6);
						
								JLabel powerLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_POWER")+"/"+MTGControler.getInstance().getLangService().getCapitalize("CARD_TOUGHNESS")+":");
								GridBagConstraints labelGbc_13 = new GridBagConstraints();
								labelGbc_13.insets = new Insets(5, 5, 5, 5);
								labelGbc_13.gridx = 2;
								labelGbc_13.gridy = 7;
								add(powerLabel, labelGbc_13);
						
						panel = new JPanel();
						GridBagConstraints gbc_panel = new GridBagConstraints();
						gbc_panel.insets = new Insets(0, 0, 5, 0);
						gbc_panel.fill = GridBagConstraints.BOTH;
						gbc_panel.gridx = 3;
						gbc_panel.gridy = 7;
						add(panel, gbc_panel);
						
								powerJTextField = new JTextField();
								powerJTextField.setColumns(2);
								panel.add(powerJTextField);
										
										label = new JLabel("/");
										panel.add(label);
								
										toughnessJTextField = new JTextField();
										toughnessJTextField.setColumns(2);
										panel.add(toughnessJTextField);
												
														JLabel watermarksLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_WATERMARK")+" :");
														GridBagConstraints labelGbc_19 = new GridBagConstraints();
														labelGbc_19.insets = new Insets(5, 5, 5, 5);
														labelGbc_19.gridx = 0;
														labelGbc_19.gridy = 8;
														add(watermarksLabel, labelGbc_19);
												
														watermarksJTextField = new JTextField();
														GridBagConstraints componentGbc_19 = new GridBagConstraints();
														componentGbc_19.insets = new Insets(5, 0, 5, 5);
														componentGbc_19.fill = GridBagConstraints.HORIZONTAL;
														componentGbc_19.gridx = 1;
														componentGbc_19.gridy = 8;
														add(watermarksJTextField, componentGbc_19);
										
												JLabel loyaltyLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_LOYALTY"));
												GridBagConstraints labelGbc_7 = new GridBagConstraints();
												labelGbc_7.insets = new Insets(5, 5, 5, 5);
												labelGbc_7.gridx = 2;
												labelGbc_7.gridy = 8;
												add(loyaltyLabel, labelGbc_7);
								
										loyaltyJTextField = new JTextField();
										GridBagConstraints componentGbc_7 = new GridBagConstraints();
										componentGbc_7.insets = new Insets(5, 0, 5, 0);
										componentGbc_7.fill = GridBagConstraints.HORIZONTAL;
										componentGbc_7.gridx = 3;
										componentGbc_7.gridy = 8;
										add(loyaltyJTextField, componentGbc_7);
						
								JLabel numberLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_NUMBER"));
								GridBagConstraints labelGbc_11 = new GridBagConstraints();
								labelGbc_11.insets = new Insets(5, 5, 5, 5);
								labelGbc_11.gridx = 0;
								labelGbc_11.gridy = 9;
								add(numberLabel, labelGbc_11);
						
								numberJTextField = new JTextField();
								GridBagConstraints componentGbc_11 = new GridBagConstraints();
								componentGbc_11.insets = new Insets(5, 0, 5, 5);
								componentGbc_11.fill = GridBagConstraints.HORIZONTAL;
								componentGbc_11.gridx = 1;
								componentGbc_11.gridy = 9;
								add(numberJTextField, componentGbc_11);
				
						JLabel tranformableLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_TRANSFORMABLE")+" :");
						GridBagConstraints labelGbc_18 = new GridBagConstraints();
						labelGbc_18.insets = new Insets(5, 5, 5, 5);
						labelGbc_18.gridx = 2;
						labelGbc_18.gridy = 9;
						add(tranformableLabel, labelGbc_18);
		
				tranformableJCheckBox = new JCheckBox();
				GridBagConstraints componentGbc_18 = new GridBagConstraints();
				componentGbc_18.insets = new Insets(5, 0, 5, 0);
				componentGbc_18.fill = GridBagConstraints.HORIZONTAL;
				componentGbc_18.gridx = 3;
				componentGbc_18.gridy = 9;
				add(tranformableJCheckBox, componentGbc_18);
				
						JLabel mciNumberLabel = new JLabel("Mci:");
						GridBagConstraints labelGbc_8 = new GridBagConstraints();
						labelGbc_8.insets = new Insets(5, 5, 5, 5);
						labelGbc_8.gridx = 0;
						labelGbc_8.gridy = 10;
						add(mciNumberLabel, labelGbc_8);
				
						mciNumberJTextField = new JTextField();
						GridBagConstraints componentGbc_8 = new GridBagConstraints();
						componentGbc_8.insets = new Insets(5, 0, 5, 5);
						componentGbc_8.fill = GridBagConstraints.HORIZONTAL;
						componentGbc_8.gridx = 1;
						componentGbc_8.gridy = 10;
						add(mciNumberJTextField, componentGbc_8);
		
				JLabel flippableLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_FLIPPABLE")+" :");
				GridBagConstraints labelGbc_4 = new GridBagConstraints();
				labelGbc_4.insets = new Insets(5, 5, 5, 5);
				labelGbc_4.gridx = 2;
				labelGbc_4.gridy = 10;
				add(flippableLabel, labelGbc_4);
		
				flippableJCheckBox = new JCheckBox();
				GridBagConstraints componentGbc_4 = new GridBagConstraints();
				componentGbc_4.insets = new Insets(5, 0, 5, 0);
				componentGbc_4.fill = GridBagConstraints.HORIZONTAL;
				componentGbc_4.gridx = 3;
				componentGbc_4.gridy = 10;
				add(flippableJCheckBox, componentGbc_4);
								
										JLabel gathererCodeLabel = new JLabel("Gatherer ID:");
										GridBagConstraints labelGbc_5 = new GridBagConstraints();
										labelGbc_5.insets = new Insets(5, 5, 5, 5);
										labelGbc_5.gridx = 0;
										labelGbc_5.gridy = 11;
										add(gathererCodeLabel, labelGbc_5);
								
										gathererCodeJTextField = new JTextField();
										GridBagConstraints componentGbc_5 = new GridBagConstraints();
										componentGbc_5.insets = new Insets(5, 0, 5, 5);
										componentGbc_5.fill = GridBagConstraints.HORIZONTAL;
										componentGbc_5.gridx = 1;
										componentGbc_5.gridy = 11;
										add(gathererCodeJTextField, componentGbc_5);
						
								JLabel rotatedCardNameLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_TRANSFORMED_NAME")+" :");
								GridBagConstraints labelGbc_15 = new GridBagConstraints();
								labelGbc_15.insets = new Insets(5, 5, 5, 5);
								labelGbc_15.gridx = 2;
								labelGbc_15.gridy = 11;
								add(rotatedCardNameLabel, labelGbc_15);
						
								rotatedCardNameJTextField = new JTextField();
								GridBagConstraints componentGbc_15 = new GridBagConstraints();
								componentGbc_15.insets = new Insets(5, 0, 5, 0);
								componentGbc_15.fill = GridBagConstraints.HORIZONTAL;
								componentGbc_15.gridx = 3;
								componentGbc_15.gridy = 11;
								add(rotatedCardNameJTextField, componentGbc_15);

		if (magicCard != null) {
			m_bindingGroup = initDataBindings();
		}
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
			if (m_bindingGroup != null) {
				m_bindingGroup.unbind();
				m_bindingGroup = null;
			}
			if (magicCard != null) {
				m_bindingGroup = initDataBindings();
			}
		}
		
		cboSuperType.setSelectedElements(magicCard.getSupertypes());
		cboTypes.setSelectedElements(magicCard.getTypes());
		cboSubtypes.setSelectedElements(magicCard.getSubtypes());
		
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
		BeanProperty<MagicCard, Object> layoutProperty = BeanProperty.create("layout");
		BeanProperty<JComboBox, Object> selectedIndexProperty = BeanProperty.create("selectedItem");
		AutoBinding<MagicCard, Object, JComboBox, Object> autoBinding_6 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, layoutProperty, layoutJComboBox, selectedIndexProperty);
		autoBinding_6.bind();
		//
		BeanProperty<MagicCard, Object> loyaltyProperty = BeanProperty.create("loyalty");
		BeanProperty<JTextField, Object> valueProperty_1 = BeanProperty.create("value");
		AutoBinding<MagicCard, Object, JTextField, Object> autoBinding_7 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, loyaltyProperty, loyaltyJTextField, valueProperty_1);
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
		BeanProperty<MagicCard, Object> rarityProperty = BeanProperty.create("rarity");
		BeanProperty<JComboBox, Object> selectedIndexProperty_1 = BeanProperty.create("selectedItem");
		AutoBinding<MagicCard, Object, JComboBox, Object> autoBinding_14 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, rarityProperty, rarityJComboBox, selectedIndexProperty_1);
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
		/*BeanProperty<MagicCard, List<String>> magicCardBeanProperty = BeanProperty.create("types");
		BeanProperty<JCheckBox, Object> textProperty_11 = BeanProperty.create("selectedElements");
		AutoBinding<MagicCard, List<String>, JCheckBox, Object> autoBinding_1 = new JCheckBoxBinding(UpdateStrategy.READ_WRITE, magicCard, magicCardBeanProperty, cboTypes, textProperty_11,"");
		autoBinding_1.bind();*/
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
		//bindingGroup.addBinding(autoBinding_1);
		return bindingGroup;
	}
}
