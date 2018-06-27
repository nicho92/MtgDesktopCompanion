package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.beta.BinderTagsMaker;
import org.magic.api.beans.MagicEdition;
import org.magic.gui.renderer.MagicEditionIconListRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.extra.BoosterPicturesProvider.LOGO;

public class BinderTagsEditor extends JDialog {
	
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private JPanel previewPanel;
	private BinderTagsMaker tagMaker;
	private BufferedImage img;
	private Color background;
	private JSpinner spinWidth;
	private JSpinner spinH;
	private JComboBox<LOGO> cboLogo;
	private JCheckBox chckbxBorder;
	private JCheckBox chckbxAddHeader;
	
	public void updateInfo() {
		
		if(chckbxAddHeader.isSelected())
			tagMaker.init(background, new Dimension((int)spinWidth.getValue(),(int)spinH.getValue()),chckbxBorder.isSelected(),(LOGO)cboLogo.getSelectedItem());
		else
			tagMaker.init(background, new Dimension((int)spinWidth.getValue(),(int)spinH.getValue()),chckbxBorder.isSelected(),null);
		try {
			img = tagMaker.generateFromId("4ED","MIR","VIS","WTH","MM3","MM2","IMA","DDU");
			previewPanel.revalidate();
		} catch (IOException e1) {
		
			e1.printStackTrace();
		}
		
		previewPanel.setPreferredSize(new Dimension((int)spinWidth.getValue(),(int)spinH.getValue()));
		previewPanel.revalidate();
		
	}
	
	
	public BinderTagsEditor() {
		tagMaker = new BinderTagsMaker();
		setTitle("Binder Tags Editor");
		
		getContentPane().setLayout(new BorderLayout(0, 0));
		JPanel editorPanel = new JPanel();
		getContentPane().add(editorPanel, BorderLayout.CENTER);
		GridBagLayout gbleditorPanel = new GridBagLayout();
		gbleditorPanel.columnWidths = new int[]{0, 275, 0, 0};
		gbleditorPanel.rowHeights = new int[]{0, 26, 20, 0, 0, 0, 0};
		gbleditorPanel.columnWeights = new double[]{0.0, 1.0, 1.0, Double.MIN_VALUE};
		gbleditorPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		editorPanel.setLayout(gbleditorPanel);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 0;
		editorPanel.add(panel, gbc_panel);
		
		List<MagicEdition> list = new ArrayList<>();
		try {
			list = MTGControler.getInstance().getEnabledCardsProviders().loadEditions();
		} catch (IOException e2) {
			logger.error(e2);
		}
		
		
		JComboBox cboEditions = new JComboBox(new DefaultComboBoxModel<>(list.toArray()));
		cboEditions.setRenderer(new MagicEditionIconListRenderer());
		panel.add(cboEditions);
		
		JButton btnAdd = new JButton("+");
		
		panel.add(btnAdd);
		
		JLabel lblWidth = new JLabel("Width :");
		GridBagConstraints gbclblWidth = new GridBagConstraints();
		gbclblWidth.anchor = GridBagConstraints.EAST;
		gbclblWidth.insets = new Insets(0, 0, 5, 5);
		gbclblWidth.gridx = 0;
		gbclblWidth.gridy = 1;
		editorPanel.add(lblWidth, gbclblWidth);
		
		spinWidth = new JSpinner();
		
		spinWidth.setModel(new SpinnerNumberModel(567,1, null, 1));
		GridBagConstraints gbcspinWidth = new GridBagConstraints();
		gbcspinWidth.fill = GridBagConstraints.HORIZONTAL;
		gbcspinWidth.insets = new Insets(0, 0, 5, 5);
		gbcspinWidth.gridx = 1;
		gbcspinWidth.gridy = 1;
		editorPanel.add(spinWidth, gbcspinWidth);
		
		JLabel lblHeight = new JLabel("Height :");
		GridBagConstraints gbclblHeight = new GridBagConstraints();
		gbclblHeight.anchor = GridBagConstraints.EAST;
		gbclblHeight.insets = new Insets(0, 0, 5, 5);
		gbclblHeight.gridx = 0;
		gbclblHeight.gridy = 2;
		editorPanel.add(lblHeight, gbclblHeight);
		
		spinH = new JSpinner();
		spinH.setModel(new SpinnerNumberModel(2173, 1, null, 1));
		GridBagConstraints gbcspinH = new GridBagConstraints();
		gbcspinH.fill = GridBagConstraints.HORIZONTAL;
		gbcspinH.insets = new Insets(0, 0, 5, 5);
		gbcspinH.gridx = 1;
		gbcspinH.gridy = 2;
		editorPanel.add(spinH, gbcspinH);
		
		chckbxAddHeader = new JCheckBox("add Header");
		GridBagConstraints gbcchckbxAddHeader = new GridBagConstraints();
		gbcchckbxAddHeader.anchor = GridBagConstraints.WEST;
		gbcchckbxAddHeader.insets = new Insets(0, 0, 5, 5);
		gbcchckbxAddHeader.gridx = 1;
		gbcchckbxAddHeader.gridy = 3;
		editorPanel.add(chckbxAddHeader, gbcchckbxAddHeader);
		
		cboLogo = new JComboBox<>(new DefaultComboBoxModel<>(LOGO.values()));
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 2;
		gbc_comboBox.gridy = 3;
		editorPanel.add(cboLogo, gbc_comboBox);
		
		chckbxBorder = new JCheckBox("Border");
		GridBagConstraints gbcchckbxBorder = new GridBagConstraints();
		gbcchckbxBorder.insets = new Insets(0, 0, 5, 5);
		gbcchckbxBorder.anchor = GridBagConstraints.WEST;
		gbcchckbxBorder.gridx = 1;
		gbcchckbxBorder.gridy = 4;
		editorPanel.add(chckbxBorder, gbcchckbxBorder);
		
		JButton btnBackgroundColor = new JButton(MTGConstants.ICON_GAME_COLOR);
		btnBackgroundColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Color selected = JColorChooser.showDialog(null, "Color Selection", Color.WHITE);
				if(selected!=null)
				{
					background=selected;
					updateInfo();
				}
				
				
				
			}
		});
		
		spinWidth.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				updateInfo();
			}

			
		});
		GridBagConstraints gbcbtnBackgroundColor = new GridBagConstraints();
		gbcbtnBackgroundColor.insets = new Insets(0, 0, 0, 5);
		gbcbtnBackgroundColor.gridx = 1;
		gbcbtnBackgroundColor.gridy = 5;
		editorPanel.add(btnBackgroundColor, gbcbtnBackgroundColor);
		
		previewPanel = new JPanel() {
			
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if(img!=null)
					g.drawImage(img, 0, 0, null);
					
				this.revalidate();
			}
			
		};
		previewPanel.setBackground(Color.white);
		getContentPane().add(new JScrollPane(previewPanel), BorderLayout.EAST);
		
		JPanel commandsPanel = new JPanel();
		getContentPane().add(commandsPanel, BorderLayout.SOUTH);
		
		JButton btnSave = new JButton(MTGConstants.ICON_SAVE);
		commandsPanel.add(btnSave);
		
		JButton btnExit = new JButton(MTGConstants.ICON_DELETE);
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				
			}
		});
		
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateInfo();
				
			}
		});
		
		
		commandsPanel.add(btnExit);
		pack();
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}
	

	

}
