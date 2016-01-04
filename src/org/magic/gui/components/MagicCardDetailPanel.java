package org.magic.gui.components;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicFormat;
import javax.swing.UIManager;

public class MagicCardDetailPanel extends JPanel {

	private BindingGroup m_bindingGroup;
	private org.magic.api.beans.MagicCard magicCard = new org.magic.api.beans.MagicCard();
	private JTextField cmcJTextField;
	private ManaPanel manaPanel;
	private JTextField fullTypeJTextField;
	private JTextField loyaltyJTextField;
	private JTextField nameJTextField;
	private JTextField powerJTextField;
	private JTextPane txtTexteArea;
	private JTextField toughnessJTextField;
	private JLabel lblFlavor;
	private JTextPane txtFlavorArea;
	private JLabel lblArtist;
	private JTextField txtArtist;
	private JLabel lblnumberInSet;
	private JPanel panelDetailCreature;
	private JLabel lblLayout;
	private JTextField txtLayoutField;
	private boolean thumbnail=false;
	private JLabel lblThumbnail;
	private JLabel lblLogoSet;
	private JLabel lblLegal;
	private JList<MagicFormat> lstFormats;
	private JScrollPane scrollLegality;
	private JLabel lblWatermark;
	private JTextField txtWatermark;
	
	public void enableThumbnail(boolean val)
	{
		thumbnail=val;
	}
	
	
	public MagicCardDetailPanel(org.magic.api.beans.MagicCard newMagicCard) {
		this();
		setMagicCard(newMagicCard);
	}

	public MagicCardDetailPanel() {
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 52, 224, 52, 0, 57, 32, 51, 16, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 44, 0, 65, 25, 21, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4 };
		setLayout(gridBagLayout);
						
		JLabel nameLabel = new JLabel("Name:");
		GridBagConstraints labelGbc_5 = new GridBagConstraints();
		labelGbc_5.insets = new Insets(5, 5, 5, 5);
		labelGbc_5.gridx = 0;
		labelGbc_5.gridy = 0;
		add(nameLabel, labelGbc_5);

		nameJTextField = new JTextField();
		nameJTextField.setEditable(false);
		GridBagConstraints componentGbc_5 = new GridBagConstraints();
		componentGbc_5.insets = new Insets(5, 0, 5, 5);
		componentGbc_5.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_5.gridx = 1;
		componentGbc_5.gridy = 0;
		add(nameJTextField, componentGbc_5);

		JLabel cmcLabel = new JLabel("Cmc:");
		GridBagConstraints labelGbc_0 = new GridBagConstraints();
		labelGbc_0.anchor = GridBagConstraints.EAST;
		labelGbc_0.insets = new Insets(5, 5, 5, 5);
		labelGbc_0.gridx = 2;
		labelGbc_0.gridy = 0;
		add(cmcLabel, labelGbc_0);

		cmcJTextField = new JTextField();
		cmcJTextField.setEditable(false);
		GridBagConstraints componentGbc_0 = new GridBagConstraints();
		componentGbc_0.insets = new Insets(5, 0, 5, 5);
		componentGbc_0.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_0.gridx = 4;
		componentGbc_0.gridy = 0;
		add(cmcJTextField, componentGbc_0);
				
				lblLogoSet = new JLabel("");
				GridBagConstraints gbc_lblLogoSet = new GridBagConstraints();
				gbc_lblLogoSet.gridwidth = 2;
				gbc_lblLogoSet.gridheight = 2;
				gbc_lblLogoSet.insets = new Insets(0, 0, 5, 5);
				gbc_lblLogoSet.gridx = 5;
				gbc_lblLogoSet.gridy = 0;
				add(lblLogoSet, gbc_lblLogoSet);
				
				
				lblThumbnail = new JLabel("");
				GridBagConstraints gbc_lblThumbnail = new GridBagConstraints();
				gbc_lblThumbnail.gridheight = 8;
				gbc_lblThumbnail.gridx = 7;
				gbc_lblThumbnail.gridy = 0;
				add(lblThumbnail, gbc_lblThumbnail);
		
				JLabel fullTypeLabel = new JLabel("Type:");
				GridBagConstraints labelGbc_2 = new GridBagConstraints();
				labelGbc_2.insets = new Insets(5, 5, 5, 5);
				labelGbc_2.gridx = 0;
				labelGbc_2.gridy = 1;
				add(fullTypeLabel, labelGbc_2);
		
				fullTypeJTextField = new JTextField();
				fullTypeJTextField.setEditable(false);
				GridBagConstraints componentGbc_2 = new GridBagConstraints();
				componentGbc_2.insets = new Insets(5, 0, 5, 5);
				componentGbc_2.fill = GridBagConstraints.HORIZONTAL;
				componentGbc_2.gridx = 1;
				componentGbc_2.gridy = 1;
				add(fullTypeJTextField, componentGbc_2);
						
				JLabel costLabel = new JLabel("Cost:");
				GridBagConstraints labelGbc_1 = new GridBagConstraints();
				labelGbc_1.anchor = GridBagConstraints.EAST;
				labelGbc_1.insets = new Insets(5, 5, 5, 5);
				labelGbc_1.gridx = 2;
				labelGbc_1.gridy = 1;
				add(costLabel, labelGbc_1);
		
				manaPanel = new ManaPanel();
				//costJTextField.setEditable(false);
				GridBagConstraints componentGbc_1 = new GridBagConstraints();
				componentGbc_1.insets = new Insets(5, 0, 5, 5);
				componentGbc_1.fill = GridBagConstraints.HORIZONTAL;
				componentGbc_1.gridx = 4;
				componentGbc_1.gridy = 1;
				add(manaPanel, componentGbc_1);
				
				JLabel loyaltyLabel = new JLabel("Loyalty:");
				GridBagConstraints gbc_loyaltyLabel = new GridBagConstraints();
				gbc_loyaltyLabel.insets = new Insets(0, 0, 5, 5);
				gbc_loyaltyLabel.gridx = 0;
				gbc_loyaltyLabel.gridy = 2;
				add(loyaltyLabel, gbc_loyaltyLabel);
				loyaltyJTextField = new JTextField();
				loyaltyJTextField.setEditable(false);
				loyaltyJTextField.setColumns(5);
				JLabel powerLabel = new JLabel("Power:");
				powerJTextField = new JTextField();
				powerJTextField.setEditable(false);
				powerJTextField.setColumns(5);
				JLabel toughnessLabel = new JLabel("/");
				toughnessJTextField = new JTextField();
				toughnessJTextField.setEditable(false);
				toughnessJTextField.setColumns(5);
								
				
				panelDetailCreature = new JPanel();
				FlowLayout flowLayout = (FlowLayout) panelDetailCreature.getLayout();
				flowLayout.setAlignment(FlowLayout.LEFT);
				GridBagConstraints gbc_panelDetailCreature = new GridBagConstraints();
				gbc_panelDetailCreature.gridwidth = 3;
				gbc_panelDetailCreature.insets = new Insets(0, 0, 5, 5);
				gbc_panelDetailCreature.fill = GridBagConstraints.BOTH;
				gbc_panelDetailCreature.gridx = 1;
				gbc_panelDetailCreature.gridy = 2;
				add(panelDetailCreature, gbc_panelDetailCreature);
				panelDetailCreature.add(loyaltyJTextField);
				panelDetailCreature.add(powerLabel);
				panelDetailCreature.add(powerJTextField);
				panelDetailCreature.add(toughnessLabel);
				panelDetailCreature.add(toughnessJTextField);
						
				lblnumberInSet = new JLabel("/");
				GridBagConstraints gbc_lblnumberInSet = new GridBagConstraints();
				gbc_lblnumberInSet.gridwidth = 2;
				gbc_lblnumberInSet.insets = new Insets(0, 0, 5, 5);
				gbc_lblnumberInSet.gridx = 5;
				gbc_lblnumberInSet.gridy = 2;
				add(lblnumberInSet, gbc_lblnumberInSet);
				
				lblLayout = new JLabel("Layout :");
				GridBagConstraints gbc_lblLayout = new GridBagConstraints();
				gbc_lblLayout.insets = new Insets(0, 0, 5, 5);
				gbc_lblLayout.gridx = 0;
				gbc_lblLayout.gridy = 3;
				add(lblLayout, gbc_lblLayout);
				
				txtLayoutField = new JTextField();
				txtLayoutField.setEditable(false);
				GridBagConstraints gbc_txtLayoutField = new GridBagConstraints();
				gbc_txtLayoutField.insets = new Insets(0, 0, 5, 5);
				gbc_txtLayoutField.fill = GridBagConstraints.HORIZONTAL;
				gbc_txtLayoutField.gridx = 1;
				gbc_txtLayoutField.gridy = 3;
				add(txtLayoutField, gbc_txtLayoutField);
				txtLayoutField.setColumns(10);
				
				lblLegal = new JLabel("Legal:");
				GridBagConstraints gbc_lblLegal = new GridBagConstraints();
				gbc_lblLegal.anchor = GridBagConstraints.EAST;
				gbc_lblLegal.insets = new Insets(0, 0, 5, 5);
				gbc_lblLegal.gridx = 2;
				gbc_lblLegal.gridy = 3;
				add(lblLegal, gbc_lblLegal);
				GridBagConstraints gbc_lstFormats = new GridBagConstraints();
				gbc_lstFormats.gridwidth = 3;
				gbc_lstFormats.insets = new Insets(0, 0, 5, 5);
				gbc_lstFormats.fill = GridBagConstraints.HORIZONTAL;
				gbc_lstFormats.gridx = 4;
				gbc_lstFormats.gridy = 3;
				
				lstFormats = new JList<MagicFormat>(new DefaultListModel<MagicFormat>());
				lstFormats.setVisibleRowCount(3);
				
				scrollLegality = new JScrollPane();
				GridBagConstraints gbc_scrollLegality = new GridBagConstraints();
				gbc_scrollLegality.gridwidth = 3;
				gbc_scrollLegality.insets = new Insets(0, 0, 5, 5);
				gbc_scrollLegality.fill = GridBagConstraints.BOTH;
				gbc_scrollLegality.gridx = 4;
				gbc_scrollLegality.gridy = 3;
				add(scrollLegality, gbc_scrollLegality);
				scrollLegality.setViewportView(lstFormats);

				JLabel textLabel = new JLabel("Text:");
				GridBagConstraints labelGbc_8 = new GridBagConstraints();
				labelGbc_8.insets = new Insets(5, 5, 5, 5);
				labelGbc_8.gridx = 0;
				labelGbc_8.gridy = 4;
				add(textLabel, labelGbc_8);
		
				txtTexteArea = new JTextPane();
				txtTexteArea.setBackground(Color.WHITE);
				txtTexteArea.setEditable(false);
				txtTexteArea.setFont(new Font("Arial", Font.PLAIN, 12));
//				txtTexteArea.setLineWrap(true);
//				txtTexteArea.setWrapStyleWord(true);
//				txtTexteArea.setRows(4);
				GridBagConstraints gbc_txtTexteArea = new GridBagConstraints();
				gbc_txtTexteArea.gridwidth = 6;
				gbc_txtTexteArea.gridheight = 2;
				gbc_txtTexteArea.insets = new Insets(5, 0, 5, 5);
				gbc_txtTexteArea.fill = GridBagConstraints.BOTH;
				gbc_txtTexteArea.gridx = 1;
				gbc_txtTexteArea.gridy = 4;
				add(txtTexteArea, gbc_txtTexteArea);
				
				
				lblFlavor = new JLabel("Flavor:");
				GridBagConstraints gbc_lblFlavor = new GridBagConstraints();
				gbc_lblFlavor.insets = new Insets(0, 0, 5, 5);
				gbc_lblFlavor.gridx = 0;
				gbc_lblFlavor.gridy = 6;
				add(lblFlavor, gbc_lblFlavor);
				
				txtFlavorArea = new JTextPane();
				txtFlavorArea.setEditable(false);
			//	txtFlavorArea.setLineWrap(true);
			//	txtFlavorArea.setWrapStyleWord(true);
				txtFlavorArea.setFont(new Font("Arial", Font.ITALIC, 11));
				GridBagConstraints gbc_txtFlavorArea = new GridBagConstraints();
				gbc_txtFlavorArea.gridwidth = 6;
				gbc_txtFlavorArea.insets = new Insets(0, 0, 5, 5);
				gbc_txtFlavorArea.fill = GridBagConstraints.BOTH;
				gbc_txtFlavorArea.gridx = 1;
				gbc_txtFlavorArea.gridy = 6;
				add(txtFlavorArea, gbc_txtFlavorArea);
				
				lblArtist = new JLabel("Artist:");
				GridBagConstraints gbc_lblArtist = new GridBagConstraints();
				gbc_lblArtist.insets = new Insets(0, 0, 0, 5);
				gbc_lblArtist.gridx = 0;
				gbc_lblArtist.gridy = 7;
				add(lblArtist, gbc_lblArtist);
				
				txtArtist = new JTextField();
				txtArtist.setEditable(false);
				GridBagConstraints gbc_txtArtist = new GridBagConstraints();
				gbc_txtArtist.insets = new Insets(0, 0, 0, 5);
				gbc_txtArtist.fill = GridBagConstraints.HORIZONTAL;
				gbc_txtArtist.gridx = 1;
				gbc_txtArtist.gridy = 7;
				add(txtArtist, gbc_txtArtist);
				txtArtist.setColumns(10);
				
				lblWatermark = new JLabel("watermark: ");
				GridBagConstraints gbc_lblWatermark = new GridBagConstraints();
				gbc_lblWatermark.anchor = GridBagConstraints.EAST;
				gbc_lblWatermark.insets = new Insets(0, 0, 0, 5);
				gbc_lblWatermark.gridx = 2;
				gbc_lblWatermark.gridy = 7;
				add(lblWatermark, gbc_lblWatermark);
				
				txtWatermark = new JTextField();
				txtWatermark.setEditable(false);
				GridBagConstraints gbc_txtWatermark = new GridBagConstraints();
				gbc_txtWatermark.gridwidth = 3;
				gbc_txtWatermark.insets = new Insets(0, 0, 0, 5);
				gbc_txtWatermark.fill = GridBagConstraints.HORIZONTAL;
				gbc_txtWatermark.gridx = 4;
				gbc_txtWatermark.gridy = 7;
				add(txtWatermark, gbc_txtWatermark);
				txtWatermark.setColumns(10);
						

		if (magicCard != null) {
			m_bindingGroup = initDataBindings();
		}
	}

	private void updateIcon() {
		String regex ="\\{(.*?)\\}";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(txtTexteArea.getText());
		
		String text = txtTexteArea.getText();
		 StyleContext context = new StyleContext();
		 StyledDocument document = new DefaultStyledDocument(context);
		 Style labelStyle = context.getStyle(StyleContext.DEFAULT_STYLE);
		 
		 int cumule=0;
		 try {
			document.insertString(0, text, null);
			while(m.find()) {
				 Image ic = manaPanel.getManaSymbol(m.group());
				 JLabel label = new JLabel(new ImageIcon(ic.getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
				 label.setAlignmentY(JLabel.TOP);
				 StyleConstants.setComponent(labelStyle, label);
				 document.remove(m.start()+cumule, (m.end()-m.start()));
				 document.insertString(m.start()+cumule, m.group(), labelStyle);
				 cumule += (m.end()-m.start())+1;
			}
			txtTexteArea.setDocument(document);
		 } 
		 catch (BadLocationException e) {
				txtTexteArea.setText(text);
		}
			
		
	}


	public MagicCard getMagicCard() {
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
	
	public JLabel getNumberInSetLabel()
	{
		return lblnumberInSet;
	}
	
	public void setMagicLogo(String set,String rarity) {
			try {
				String url = "http://gatherer.wizards.com/Handlers/Image.ashx?type=symbol&set="+set+"&size=medium&rarity="+rarity.substring(0,1);
				URL iconURL = new URL(url);
				lblLogoSet.setIcon(new ImageIcon(iconURL));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
	}
	
	
	
	protected BindingGroup initDataBindings() {
		BeanProperty<MagicCard, Integer> cmcProperty = BeanProperty.create("cmc");
		BeanProperty<JTextField, String> textProperty = BeanProperty.create("text");
		AutoBinding<MagicCard, Integer, JTextField, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, cmcProperty, cmcJTextField, textProperty);
		autoBinding.bind();
		//
		BeanProperty<MagicCard, String> costProperty = BeanProperty.create("cost");
		BeanProperty<ManaPanel, String> textProperty_1 = BeanProperty.create("manaCost");
		AutoBinding<MagicCard, String, ManaPanel, String> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, costProperty, manaPanel, textProperty_1);
		autoBinding_1.bind();
		//
		BeanProperty<MagicCard, String> fullTypeProperty = BeanProperty.create("fullType");
		BeanProperty<JTextField, String> textProperty_2 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, fullTypeProperty, fullTypeJTextField, textProperty_2);
		autoBinding_2.bind();
		//
		BeanProperty<MagicCard, Integer> loyaltyProperty = BeanProperty.create("loyalty");
		BeanProperty<JTextField, String> textProperty_4 = BeanProperty.create("text");
		AutoBinding<MagicCard, Integer, JTextField, String> autoBinding_4 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, loyaltyProperty, loyaltyJTextField, textProperty_4);
		autoBinding_4.bind();
		//
		BeanProperty<MagicCard, String> nameProperty = BeanProperty.create("name");
		BeanProperty<JTextField, String> textProperty_5 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding_5 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, nameProperty, nameJTextField, textProperty_5);
		autoBinding_5.bind();
		//
		BeanProperty<MagicCard, String> powerProperty = BeanProperty.create("power");
		BeanProperty<JTextField, String> textProperty_6 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding_6 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, powerProperty, powerJTextField, textProperty_6);
		autoBinding_6.bind();
		//
		BeanProperty<MagicCard, String> textProperty_8 = BeanProperty.create("text");
		BeanProperty<JTextPane, String> textProperty_9 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextPane, String> autoBinding_8 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, textProperty_8, txtTexteArea, textProperty_9);
		autoBinding_8.bind();
		//
		BeanProperty<MagicCard, String> toughnessProperty = BeanProperty.create("toughness");
		BeanProperty<JTextField, String> textProperty_10 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding_9 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, toughnessProperty, toughnessJTextField, textProperty_10);
		autoBinding_9.bind();
		
		
		BeanProperty<MagicCard, String> flavorProperty = BeanProperty.create("flavor");
		BeanProperty<JTextPane, String> textProperty_11 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextPane, String> autoBinding_10 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, flavorProperty, txtFlavorArea, textProperty_11);
		autoBinding_10.bind();
	
		
		BeanProperty<MagicCard, String> artistProperty = BeanProperty.create("artist");
		BeanProperty<JTextField, String> textProperty_12 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding_11 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, artistProperty, txtArtist, textProperty_12);
		autoBinding_11.bind();

		
		BeanProperty<MagicCard, String> layoutProperty = BeanProperty.create("layout");
		BeanProperty<JTextField, String> textProperty_13 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding_12 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, layoutProperty, txtLayoutField, textProperty_13);
		autoBinding_12.bind();
		
		BeanProperty<MagicCard, String> waterProperty = BeanProperty.create("watermarks");
		BeanProperty<JTextField, String> textProperty_14 = BeanProperty.create("text");
		AutoBinding<MagicCard, String, JTextField, String> autoBinding_13 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, magicCard, waterProperty, txtWatermark, textProperty_14);
		autoBinding_13.bind();
		
		
		updateIcon();
		
		if(thumbnail)
		{
			new Thread(new Runnable() {
				public void run() {
					ImageIcon icon;
					try {
						icon = new ImageIcon(new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+magicCard.getEditions().get(0).getMultiverse_id()+"&type=card"));
						Image img = icon.getImage();
						Image newimg = img.getScaledInstance(icon.getIconWidth()/2, icon.getIconHeight()/2,  java.awt.Image.SCALE_SMOOTH);
						lblThumbnail.setIcon( new ImageIcon(newimg));
					} catch (MalformedURLException e) {
						//e.printStackTrace();
					}
					
				}
			}).start();
		}
	
		if(magicCard.getEditions().size()>0)
		{ 
			
			new Thread(new Runnable() {
				public void run() {
						setMagicLogo(magicCard.getEditions().get(0).getId(),magicCard.getEditions().get(0).getRarity());
						getNumberInSetLabel().setText(magicCard.getNumber()+"/"+magicCard.getEditions().get(0).getCardCount());
				}
			}).start();
		}
		
		((DefaultListModel)lstFormats.getModel()).removeAllElements();
		
		
		for(MagicFormat mf : magicCard.getLegalities())
			((DefaultListModel)lstFormats.getModel()).addElement(mf);
		
		//
		BindingGroup bindingGroup = new BindingGroup();
		//
		bindingGroup.addBinding(autoBinding);
		bindingGroup.addBinding(autoBinding_1);
		bindingGroup.addBinding(autoBinding_2);
		bindingGroup.addBinding(autoBinding_4);
		bindingGroup.addBinding(autoBinding_5);
		bindingGroup.addBinding(autoBinding_6);
		bindingGroup.addBinding(autoBinding_8);
		bindingGroup.addBinding(autoBinding_9);
		bindingGroup.addBinding(autoBinding_10);
		bindingGroup.addBinding(autoBinding_11);
		bindingGroup.addBinding(autoBinding_12);
		bindingGroup.addBinding(autoBinding_13);
		return bindingGroup;
	}
}
