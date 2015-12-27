package org.magic.gui;

import java.awt.BorderLayout;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.swing.JPanel;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import javax.swing.JTabbedPane;
import org.magic.gui.components.CardsPicPanel;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import java.awt.FlowLayout;

public class CardBuilderPanelGUI extends JPanel {
	
	private MagicCard card;
	private JTextField txtName;
	CardsPicPanel cardsPicPanel;
	JComboBox cboColor;
	private JTextField txtType;
	JTextPane textPane;
	private JTextField txtPower;
	private JTextField txtToughness;
	private JTextField txtFlavor;
	
	private void init()
	{
		card = new MagicCard();
		MagicEdition ed = new MagicEdition();
		card.getEditions().add(ed);
		
				
	}
	
	public void updateCard()
	{
		try {
		String url = "http://www.mtgcardmaker.com/mcmaker/createcard.php?"
				+ "name="+URLEncoder.encode(txtName.getText(),"UTF-8")
				+ "&color="+cboColor.getSelectedItem()
				+ "&mana_r=0"
				+ "&mana_u=0"
				+ "&mana_g=0"
				+ "&mana_b=0"
				+ "&mana_w=0"
				+ "&mana_colorless=0"
				+ "&picture="
				+ "&supertype="
				+ "&cardtype="+URLEncoder.encode(txtType.getText(),"UTF-8")
				+ "&subtype="
				+ "&expansion="
				+ "&rarity=Common"
				+ "&cardtext="+URLEncoder.encode(textPane.getText(),"UTF-8")
				+ "&power="+txtPower.getText()
				+ "&toughness="+txtToughness.getText()
				+ "&artist="
				+ "&bottom=%E2%84%A2+%26+%C2%A9+1993-2013+Wizards+of+the+Coast+LLC&set1="
				+ "&set2=&setname=";
		
			cardsPicPanel.showPhoto(new URL(url));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public CardBuilderPanelGUI() {
		
		init();
		
		setLayout(new BorderLayout(0, 0));
		
		cardsPicPanel = new CardsPicPanel();
		cardsPicPanel.setMoveable(false);
		add(cardsPicPanel, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.WEST);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{80, 209, 144, 41, 0};
		gbl_panel.rowHeights = new int[]{0, 28, 35, 29, 27, 0, 97, 28, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblLayout = new JLabel("Layout :");
		GridBagConstraints gbc_lblLayout = new GridBagConstraints();
		gbc_lblLayout.insets = new Insets(0, 0, 5, 5);
		gbc_lblLayout.gridx = 0;
		gbc_lblLayout.gridy = 1;
		panel.add(lblLayout, gbc_lblLayout);
		
		JComboBox cboLayout = new JComboBox();
		cboLayout.setModel(new DefaultComboBoxModel(new String[] {"Normal", "Planeswalker"}));
		GridBagConstraints gbc_cboLayout = new GridBagConstraints();
		gbc_cboLayout.insets = new Insets(0, 0, 5, 5);
		gbc_cboLayout.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboLayout.gridx = 1;
		gbc_cboLayout.gridy = 1;
		panel.add(cboLayout, gbc_cboLayout);
		
		JLabel lblName = new JLabel("Name: ");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.fill = GridBagConstraints.VERTICAL;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 2;
		panel.add(lblName, gbc_lblName);
		
		txtName = new JTextField();
		GridBagConstraints gbc_txtName = new GridBagConstraints();
		gbc_txtName.insets = new Insets(0, 0, 5, 5);
		gbc_txtName.fill = GridBagConstraints.BOTH;
		gbc_txtName.gridx = 1;
		gbc_txtName.gridy = 2;
		panel.add(txtName, gbc_txtName);
		txtName.setColumns(10);
		
		JLabel lblColor = new JLabel("Color :");
		GridBagConstraints gbc_lblColor = new GridBagConstraints();
		gbc_lblColor.fill = GridBagConstraints.VERTICAL;
		gbc_lblColor.insets = new Insets(0, 0, 5, 5);
		gbc_lblColor.gridx = 0;
		gbc_lblColor.gridy = 3;
		panel.add(lblColor, gbc_lblColor);
		
		cboColor = new JComboBox();
		cboColor.setModel(new DefaultComboBoxModel(new String[] {"White", "Blue", "Black", "Red", "Green", "Gold","Uncolor"}));
		GridBagConstraints gbc_cboColor = new GridBagConstraints();
		gbc_cboColor.fill = GridBagConstraints.BOTH;
		gbc_cboColor.insets = new Insets(0, 0, 5, 5);
		gbc_cboColor.gridx = 1;
		gbc_cboColor.gridy = 3;
		panel.add(cboColor, gbc_cboColor);
		
		JLabel lblCost = new JLabel("Cost :");
		GridBagConstraints gbc_lblCost = new GridBagConstraints();
		gbc_lblCost.insets = new Insets(0, 0, 5, 5);
		gbc_lblCost.gridx = 0;
		gbc_lblCost.gridy = 4;
		panel.add(lblCost, gbc_lblCost);
		
		
		JLabel lblType = new JLabel("Type :");
		GridBagConstraints gbc_lblType = new GridBagConstraints();
		gbc_lblType.insets = new Insets(0, 0, 5, 5);
		gbc_lblType.gridx = 0;
		gbc_lblType.gridy = 5;
		panel.add(lblType, gbc_lblType);
		
		txtType = new JTextField();
		GridBagConstraints gbc_txtType = new GridBagConstraints();
		gbc_txtType.insets = new Insets(0, 0, 5, 5);
		gbc_txtType.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtType.gridx = 1;
		gbc_txtType.gridy = 5;
		panel.add(txtType, gbc_txtType);
		txtType.setColumns(10);
		
		JLabel lblText = new JLabel("Text :");
		GridBagConstraints gbc_lblText = new GridBagConstraints();
		gbc_lblText.insets = new Insets(0, 0, 5, 5);
		gbc_lblText.gridx = 0;
		gbc_lblText.gridy = 6;
		panel.add(lblText, gbc_lblText);
		
		textPane = new JTextPane();
		GridBagConstraints gbc_textPane = new GridBagConstraints();
		gbc_textPane.gridwidth = 2;
		gbc_textPane.insets = new Insets(0, 0, 5, 5);
		gbc_textPane.fill = GridBagConstraints.BOTH;
		gbc_textPane.gridx = 1;
		gbc_textPane.gridy = 6;
		panel.add(textPane, gbc_textPane);
		
		JPanel panelPalette = new JPanel();
		GridBagConstraints gbc_panelPalette = new GridBagConstraints();
		gbc_panelPalette.gridwidth = 2;
		gbc_panelPalette.insets = new Insets(0, 0, 5, 5);
		gbc_panelPalette.fill = GridBagConstraints.BOTH;
		gbc_panelPalette.gridx = 1;
		gbc_panelPalette.gridy = 7;
		panel.add(panelPalette, gbc_panelPalette);
		
		JButton btnW = new JButton("W");
		panelPalette.add(btnW);
		
		JButton btnU = new JButton("U");
		panelPalette.add(btnU);
		
		JButton btnB = new JButton("B");
		panelPalette.add(btnB);
		
		JButton btnR = new JButton("R");
		panelPalette.add(btnR);
		
		JButton btnG = new JButton("G");
		panelPalette.add(btnG);
		
		JLabel lblFlavor = new JLabel("Flavor :");
		GridBagConstraints gbc_lblFlavor = new GridBagConstraints();
		gbc_lblFlavor.insets = new Insets(0, 0, 5, 5);
		gbc_lblFlavor.gridx = 0;
		gbc_lblFlavor.gridy = 8;
		panel.add(lblFlavor, gbc_lblFlavor);
		
		txtFlavor = new JTextField();
		GridBagConstraints gbc_txtFlavor = new GridBagConstraints();
		gbc_txtFlavor.gridwidth = 2;
		gbc_txtFlavor.insets = new Insets(0, 0, 5, 5);
		gbc_txtFlavor.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFlavor.gridx = 1;
		gbc_txtFlavor.gridy = 8;
		panel.add(txtFlavor, gbc_txtFlavor);
		txtFlavor.setColumns(10);
		
		JLabel lblPower = new JLabel("Power :");
		GridBagConstraints gbc_lblPower = new GridBagConstraints();
		gbc_lblPower.insets = new Insets(0, 0, 5, 5);
		gbc_lblPower.gridx = 0;
		gbc_lblPower.gridy = 9;
		panel.add(lblPower, gbc_lblPower);
		
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 5);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 9;
		panel.add(panel_1, gbc_panel_1);
		
		txtPower = new JTextField();
		panel_1.add(txtPower);
		txtPower.setColumns(3);
		
		JLabel lblNewLabel = new JLabel("/");
		panel_1.add(lblNewLabel);
		
		txtToughness = new JTextField();
		panel_1.add(txtToughness);
		txtToughness.setColumns(3);
		
		JButton btnGenerate = new JButton("Generate");
		GridBagConstraints gbc_btnGenerate = new GridBagConstraints();
		gbc_btnGenerate.insets = new Insets(0, 0, 0, 5);
		gbc_btnGenerate.gridx = 1;
		gbc_btnGenerate.gridy = 13;
		panel.add(btnGenerate, gbc_btnGenerate);
		
		
		
		
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateCard();
			}
		});
	}

}
