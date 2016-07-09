package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.gui.components.CropImagePanel;
import org.magic.gui.components.ManaPanel;

public class CardBuilderPanelGUI extends JPanel {
	
	private MagicCard card;
	private JPanel cardsPicPanel;
	private CropImagePanel panelImage;
	private Image cardImage;

	private JTextPane textPane;
	private JTextField txtType;
	private JTextField txtPower;
	private JTextField txtToughness;
	private JTextField txtFlavor;
	private JTextField txtBottom;
	private JTextField txtCard1;
	private JTextField txtCard2;
	private JTextField txtName;
	
	private Integer[] data = {0,1,2,3,4,5,6,7,8,9,10};
	private JComboBox<Integer> cboW = new JComboBox<Integer>(data);
	private JComboBox<Integer> cboU = new JComboBox<Integer>(data);
	private JComboBox<Integer> cboB = new JComboBox<Integer>(data);
	private JComboBox<Integer> cboR = new JComboBox<Integer>(data);
	private JComboBox<Integer> cboG = new JComboBox<Integer>(data);
	private JComboBox<Integer> cboC = new JComboBox<Integer>(data);
	private JComboBox<Integer> cboUn = new JComboBox<Integer>(data);
	private JComboBox<String> cboColor;
	private JTextField txtAuthor;
	
	
	static final Logger logger = LogManager.getLogger(CardBuilderPanelGUI.class.getName());

	String url ="";
	
	
	private void init()
	{
		logger.debug("init cardsbuilder panel");
		card = new MagicCard();
		MagicEdition ed = new MagicEdition();
		card.getEditions().add(ed);
	}
	
	public void updateCard()//TODO use magiccardmaker
	{
		try {

		url = "http://www.mtgcardmaker.com/mcmaker/createcard.php?"
				+ "name="+URLEncoder.encode(txtName.getText(),"UTF-8")
				+ "&color="+cboColor.getSelectedItem()
				+ "&mana_r="+cboR.getSelectedItem()
				+ "&mana_u="+cboU.getSelectedItem()
				+ "&mana_g="+cboG.getSelectedItem()
				+ "&mana_b="+cboB.getSelectedItem()
				+ "&mana_w="+cboW.getSelectedItem()
				+ "&mana_colorless="+cboUn.getSelectedItem()
				+ "&picture="
				+ "&supertype="
				+ "&cardtype="+URLEncoder.encode(txtType.getText(),"UTF-8")
				+ "&subtype="
				+ "&expansion="
				+ "&rarity="
				+ "&cardtext="+URLEncoder.encode(textPane.getText(),"UTF-8")
				+ "&power="+txtPower.getText()
				+ "&toughness="+txtToughness.getText()
				+ "&artist="+URLEncoder.encode(txtAuthor.getText(),"UTF-8")
				+ "&bottom="+URLEncoder.encode(txtBottom.getText(),"UTF-8")
				+ "&set1="+txtCard1.getText()
				+ "&set2="+txtCard2.getText()
				+ "&setname=";
		
			cardImage = ImageIO.read(new URL(url));
			cardsPicPanel.repaint();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public CardBuilderPanelGUI() {
		
		init();
		
		setLayout(new BorderLayout(0, 0));
		
		cardsPicPanel = new JPanel() {
			
			@Override
			protected void paintComponent(Graphics g) {
				 super.paintComponent(g);
	             g.drawImage(cardImage, 0, 0, null);
	             
	           
					if(panelImage.getCroppedImage()!=null)
						g.drawImage(panelImage.getCroppedImage(), 35, 68, 329, 242, null);
				
			}
		};
		
		
		
		cardsPicPanel.setBackground(Color.WHITE);
		cardsPicPanel.setPreferredSize(new Dimension(400, 10));
		add(cardsPicPanel, BorderLayout.EAST);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{80, 96, 120, 0, 74, 41, 0};
		gbl_panel.rowHeights = new int[]{0, 28, 35, 29, 27, 28, 62, 28, 0, 0, 0, 30, 0, 30, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
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
		
		JLabel lblColor = new JLabel("Color :");
		GridBagConstraints gbc_lblColor = new GridBagConstraints();
		gbc_lblColor.anchor = GridBagConstraints.EAST;
		gbc_lblColor.fill = GridBagConstraints.VERTICAL;
		gbc_lblColor.insets = new Insets(0, 0, 5, 5);
		gbc_lblColor.gridx = 2;
		gbc_lblColor.gridy = 1;
		panel.add(lblColor, gbc_lblColor);
		
		cboColor = new JComboBox();
		cboColor.setModel(new DefaultComboBoxModel(new String[] {"White", "Blue", "Black", "Red", "Green", "Gold","Uncolor"}));
		GridBagConstraints gbc_cboColor = new GridBagConstraints();
		gbc_cboColor.gridwidth = 2;
		gbc_cboColor.fill = GridBagConstraints.BOTH;
		gbc_cboColor.insets = new Insets(0, 0, 5, 5);
		gbc_cboColor.gridx = 3;
		gbc_cboColor.gridy = 1;
		panel.add(cboColor, gbc_cboColor);
		
		JLabel lblName = new JLabel("Name: ");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.fill = GridBagConstraints.VERTICAL;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 2;
		panel.add(lblName, gbc_lblName);
		
		txtName = new JTextField();
		GridBagConstraints gbc_txtName = new GridBagConstraints();
		gbc_txtName.gridwidth = 4;
		gbc_txtName.insets = new Insets(0, 0, 5, 5);
		gbc_txtName.fill = GridBagConstraints.BOTH;
		gbc_txtName.gridx = 1;
		gbc_txtName.gridy = 2;
		panel.add(txtName, gbc_txtName);
		txtName.setColumns(10);
		
		JLabel lblCost = new JLabel("Cost :");
		GridBagConstraints gbc_lblCost = new GridBagConstraints();
		gbc_lblCost.insets = new Insets(0, 0, 5, 5);
		gbc_lblCost.gridx = 0;
		gbc_lblCost.gridy = 3;
		panel.add(lblCost, gbc_lblCost);
		
		JPanel panelcost = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panelcost.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panelcost = new GridBagConstraints();
		gbc_panelcost.anchor = GridBagConstraints.WEST;
		gbc_panelcost.gridwidth = 2;
		gbc_panelcost.insets = new Insets(0, 0, 5, 5);
		gbc_panelcost.fill = GridBagConstraints.VERTICAL;
		gbc_panelcost.gridx = 1;
		gbc_panelcost.gridy = 3;
		panel.add(panelcost, gbc_panelcost);
		gbc_panelcost.gridwidth = 2;
		gbc_panelcost.insets = new Insets(0, 0, 5, 5);
		gbc_panelcost.fill = GridBagConstraints.BOTH;
		gbc_panelcost.gridx = 1;
		gbc_panelcost.gridy = 5;
		
		
		final ManaPanel pan = new ManaPanel();
		
		panelcost.add(new JLabel(new ImageIcon(pan.getManaSymbol("W").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
		panelcost.add(cboW);
		panelcost.add(new JLabel(new ImageIcon(pan.getManaSymbol("U").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
		panelcost.add(cboU);
		panelcost.add(new JLabel(new ImageIcon(pan.getManaSymbol("B").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
		panelcost.add(cboB);
		panelcost.add(new JLabel(new ImageIcon(pan.getManaSymbol("R").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
		panelcost.add(cboR);
		panelcost.add(new JLabel(new ImageIcon(pan.getManaSymbol("G").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
		panelcost.add(cboG);
		panelcost.add(new JLabel(new ImageIcon(pan.getManaSymbol("1").getScaledInstance(10, 10, Image.SCALE_SMOOTH))));
		panelcost.add(cboUn);
		
		
		JLabel lblType = new JLabel("Type :");
		GridBagConstraints gbc_lblType = new GridBagConstraints();
		gbc_lblType.insets = new Insets(0, 0, 5, 5);
		gbc_lblType.gridx = 0;
		gbc_lblType.gridy = 4;
		panel.add(lblType, gbc_lblType);
		
		txtType = new JTextField();
		GridBagConstraints gbc_txtType = new GridBagConstraints();
		gbc_txtType.gridwidth = 2;
		gbc_txtType.insets = new Insets(0, 0, 5, 5);
		gbc_txtType.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtType.gridx = 1;
		gbc_txtType.gridy = 4;
		panel.add(txtType, gbc_txtType);
		txtType.setColumns(10);
		
		JLabel lblText = new JLabel("Text :");
		GridBagConstraints gbc_lblText = new GridBagConstraints();
		gbc_lblText.insets = new Insets(0, 0, 5, 5);
		gbc_lblText.gridx = 0;
		gbc_lblText.gridy = 5;
		panel.add(lblText, gbc_lblText);
		
		textPane = new JTextPane();
		GridBagConstraints gbc_textPane = new GridBagConstraints();
		gbc_textPane.gridheight = 2;
		gbc_textPane.gridwidth = 4;
		gbc_textPane.insets = new Insets(0, 0, 5, 5);
		gbc_textPane.fill = GridBagConstraints.BOTH;
		gbc_textPane.gridx = 1;
		gbc_textPane.gridy = 5;
		panel.add(textPane, gbc_textPane);
		
		JPanel panelPalette = new JPanel();
		GridBagConstraints gbc_panelPalette = new GridBagConstraints();
		gbc_panelPalette.gridwidth = 2;
		gbc_panelPalette.insets = new Insets(0, 0, 5, 5);
		gbc_panelPalette.fill = GridBagConstraints.BOTH;
		gbc_panelPalette.gridx = 1;
		gbc_panelPalette.gridy = 7;
		panel.add(panelPalette, gbc_panelPalette);
		
		
		String[] symbolcs = new String[]{"W","U","B","R","G","C","T"};
		
		for(String s : symbolcs)
		{
			final JButton btnG = new JButton();
			btnG.setToolTipText(s);
			btnG.setIcon(new ImageIcon(pan.getManaSymbol(s).getScaledInstance(15, 15, Image.SCALE_SMOOTH)));
			btnG.setForeground(btnG.getBackground());
			
			btnG.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					textPane.setText(textPane.getText()+ " {" + btnG.getToolTipText()+"}");
					
				}
			});
			panelPalette.add(btnG);
				
		}
		
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
		
		JLabel lblCardNumber = new JLabel("card number :");
		GridBagConstraints gbc_lblCardNumber = new GridBagConstraints();
		gbc_lblCardNumber.anchor = GridBagConstraints.EAST;
		gbc_lblCardNumber.insets = new Insets(0, 0, 5, 5);
		gbc_lblCardNumber.gridx = 2;
		gbc_lblCardNumber.gridy = 9;
		panel.add(lblCardNumber, gbc_lblCardNumber);
		
		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.gridwidth = 2;
		gbc_panel_2.insets = new Insets(0, 0, 5, 5);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 3;
		gbc_panel_2.gridy = 9;
		panel.add(panel_2, gbc_panel_2);
		
		txtCard1 = new JTextField();
		txtCard1.setColumns(3);
		panel_2.add(txtCard1);
		
		JLabel label = new JLabel("/");
		panel_2.add(label);
		
		txtCard2 = new JTextField();
		txtCard2.setColumns(3);
		panel_2.add(txtCard2);
		
		JLabel lblBottom = new JLabel("Bottom :");
		GridBagConstraints gbc_lblBottom = new GridBagConstraints();
		gbc_lblBottom.insets = new Insets(0, 0, 5, 5);
		gbc_lblBottom.gridx = 0;
		gbc_lblBottom.gridy = 10;
		panel.add(lblBottom, gbc_lblBottom);
		
		txtBottom = new JTextField();
		txtBottom.setText("\u2122 & \u00A9 1993-2016 Wizards of the Coast LLC");
		GridBagConstraints gbc_txtBottom = new GridBagConstraints();
		gbc_txtBottom.gridwidth = 2;
		gbc_txtBottom.insets = new Insets(0, 0, 5, 5);
		gbc_txtBottom.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtBottom.gridx = 1;
		gbc_txtBottom.gridy = 10;
		panel.add(txtBottom, gbc_txtBottom);
		txtBottom.setColumns(10);
		
		JLabel lblAuthor = new JLabel("Author :");
		GridBagConstraints gbc_lblAuthor = new GridBagConstraints();
		gbc_lblAuthor.insets = new Insets(0, 0, 5, 5);
		gbc_lblAuthor.gridx = 0;
		gbc_lblAuthor.gridy = 11;
		panel.add(lblAuthor, gbc_lblAuthor);
		
		txtAuthor = new JTextField();
		GridBagConstraints gbc_txtAuthor = new GridBagConstraints();
		gbc_txtAuthor.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtAuthor.gridwidth = 2;
		gbc_txtAuthor.insets = new Insets(0, 0, 5, 5);
		gbc_txtAuthor.gridx = 1;
		gbc_txtAuthor.gridy = 11;
		panel.add(txtAuthor, gbc_txtAuthor);
		txtAuthor.setColumns(10);
		
		JButton btnPicture = new JButton("Picture");
		GridBagConstraints gbc_btnPicture = new GridBagConstraints();
		gbc_btnPicture.anchor = GridBagConstraints.NORTH;
		gbc_btnPicture.insets = new Insets(0, 0, 5, 5);
		gbc_btnPicture.gridx = 0;
		gbc_btnPicture.gridy = 12;
		panel.add(btnPicture, gbc_btnPicture);
		
		btnPicture.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser choose = new JFileChooser();
				choose.showOpenDialog(null);
				File pics = choose.getSelectedFile();
				
				Image i = new ImageIcon(pics.getAbsolutePath()).getImage();
				panelImage.setImage(i.getScaledInstance(panelImage.getWidth(), panelImage.getHeight(), Image.SCALE_SMOOTH));
				
			}
		});
		
		panelImage = new CropImagePanel();
		panelImage.setBackground(Color.BLACK);
		GridBagConstraints gbc_panelImage = new GridBagConstraints();
		gbc_panelImage.gridwidth = 5;
		gbc_panelImage.gridheight = 5;
		gbc_panelImage.insets = new Insets(0, 0, 5, 0);
		gbc_panelImage.fill = GridBagConstraints.BOTH;
		gbc_panelImage.gridx = 1;
		gbc_panelImage.gridy = 12;
		panel.add(panelImage, gbc_panelImage);
		
		JButton btnGenerate = new JButton("Generate");
		GridBagConstraints gbc_btnGenerate = new GridBagConstraints();
		gbc_btnGenerate.insets = new Insets(0, 0, 5, 5);
		gbc_btnGenerate.gridx = 1;
		gbc_btnGenerate.gridy = 17;
		panel.add(btnGenerate, gbc_btnGenerate);
		
		JButton btnSave = new JButton("Save");
		
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.insets = new Insets(0, 0, 5, 5);
		gbc_btnSave.gridx = 2;
		gbc_btnSave.gridy = 17;
		panel.add(btnSave, gbc_btnSave);
		
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser choose = new JFileChooser();
				choose.showSaveDialog(null);
				File pics = choose.getSelectedFile();
				
				int w = cardsPicPanel.getWidth();
			    int h = 560;//cardsPicPanel.getHeight();
			    BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			    Graphics2D g = bi.createGraphics();
			    cardsPicPanel.paint(g);

				
				try {
					ImageIO.write(bi,"PNG",pics);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateCard();
				
			}
		});
	}

}
