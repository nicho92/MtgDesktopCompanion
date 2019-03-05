package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;

import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.renderer.MagicEditionIconListRenderer;
import org.magic.services.BinderTagsManager;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.extra.PackagesProvider.LOGO;
import org.magic.tools.ImageTools;

public class BinderTagsEditorComponent extends MTGUIComponent {
	
	private static final long serialVersionUID = 1L;
	private JPanel previewPanel;
	private transient BinderTagsManager tagMaker;
	private transient BufferedImage img;
	private JComboBox<LOGO> cboLogo;
	private JCheckBox chckbxBorder;
	private JCheckBox chckbxAddHeader;
	private JButton btnBackgroundColor ;
	private JButton btnSave;
	private JPanel panel1;
	private JPanel leftPanel;
	private JButton btnNew;
	private JPanel panelInterspace;
	private JLabel lblInterSpace;
	private JSpinner spinSpace;
	private JScrollPane scrollListEdition;
	private JList<MagicEdition> listEditions;
	private DefaultListModel<MagicEdition> model;
	private JButton btnRefresh;
	private JPanel panneauBas;

	public void updateInfo() {
		img = ImageTools.trimAlpha(tagMaker.generate());
		previewPanel.revalidate();
		previewPanel.repaint();
	}
	
	
	
	
	private void init()
	{
		Dimension d = new Dimension(567,2173);
		double mmW = ImageTools.toMM(d.getWidth());
 		double mmH = ImageTools.toMM(d.getHeight());
 		String res = JOptionPane.showInputDialog("Dimension in mm", mmW+"x"+mmH);
 		String[] result = res.split("x");
 		
 		d = new Dimension((int)ImageTools.toPX(Double.parseDouble(result[0])), (int)ImageTools.toPX(Double.parseDouble(result[1])));
		tagMaker = new BinderTagsManager(d);
	}
	

	@Override
	public String getTitle() {
		return MTGControler.getInstance().getLangService().getCapitalize("BINDER_TAG_EDITOR");
	}
	

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_BINDERS;
	}
	

	
	
	public BinderTagsEditorComponent() {
		
		init();
		
		leftPanel = new JPanel();
		JPanel editorPanel = new JPanel();
		model = new DefaultListModel<>();
		listEditions = new JList<>(model);
		listEditions.setCellRenderer(new MagicEditionIconListRenderer());
		
		setLayout(new BorderLayout(0, 0));
		
		try {
			for(MagicEdition ed : MTGControler.getInstance().getEnabled(MTGCardsProvider.class).loadEditions())
				model.addElement(ed);
				
		} catch (IOException e2) {
			logger.error(e2);
		}
		
		previewPanel = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if(img!=null)
					g.drawImage(img, 0, 0, null);
					
				this.revalidate();
			}
		};
		previewPanel.setPreferredSize(new Dimension(576, 2173));
		
		previewPanel.setBackground(Color.white);
		
		
		JScrollPane scrollPane = new JScrollPane(previewPanel);
		scrollPane.setPreferredSize(new Dimension(250, 250));
		add(scrollPane, BorderLayout.CENTER);
		add(leftPanel, BorderLayout.WEST);
		leftPanel.setLayout(new BorderLayout(0, 0));
		leftPanel.add(editorPanel, BorderLayout.CENTER);
		editorPanel.setLayout(new BorderLayout(0, 0));
		
		scrollListEdition = new JScrollPane(listEditions);
		editorPanel.add(scrollListEdition, BorderLayout.CENTER);
		
		panneauBas = new JPanel();
		editorPanel.add(panneauBas, BorderLayout.SOUTH);
		panneauBas.setLayout(new BoxLayout(panneauBas, BoxLayout.Y_AXIS));
		
		
		
		panel1 = new JPanel();
		panneauBas.add(panel1);
		FlowLayout flowLayout = (FlowLayout) panel1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		
		chckbxAddHeader = new JCheckBox("add Header");
		panel1.add(chckbxAddHeader);
		
		cboLogo = new JComboBox<>(new DefaultComboBoxModel<>(LOGO.values()));
		panel1.add(cboLogo);
		
		panelInterspace = new JPanel();
		panneauBas.add(panelInterspace);
		FlowLayout flowLayout1 = (FlowLayout) panelInterspace.getLayout();
		flowLayout1.setAlignment(FlowLayout.LEFT);
		
		lblInterSpace = new JLabel("Space :");
		panelInterspace.add(lblInterSpace);
		
		spinSpace = new JSpinner();
		
		panelInterspace.add(spinSpace);
		
		btnBackgroundColor = new JButton(MTGConstants.ICON_GAME_COLOR);
		panelInterspace.add(btnBackgroundColor);
		
		chckbxBorder = new JCheckBox("Border");
		panelInterspace.add(chckbxBorder);
		JPanel commandsPanel = new JPanel();
		leftPanel.add(commandsPanel, BorderLayout.SOUTH);
		
		btnNew = new JButton(MTGConstants.ICON_NEW);
		
		commandsPanel.add(btnNew);
		
		btnRefresh = new JButton(MTGConstants.ICON_REFRESH);
		
		commandsPanel.add(btnRefresh);
		
		btnSave = new JButton(MTGConstants.ICON_SAVE);
		commandsPanel.add(btnSave);
		
		initActions();

	}


	private void initActions() {
		
		btnRefresh.addActionListener(ae-> {
			tagMaker.setEditions(listEditions.getSelectedValuesList());
			updateInfo();
		});
		
		
		btnBackgroundColor.addActionListener(ae->{
			Color selected = JColorChooser.showDialog(null, "Color Selection", Color.WHITE);
			tagMaker.setBackColor(selected);
			updateInfo();
		});
	
		chckbxAddHeader.addActionListener(ae->{
			if(!chckbxAddHeader.isSelected())
			{
				tagMaker.setLogo(null);
			}
			else
			{
				tagMaker.setLogo((LOGO)cboLogo.getSelectedItem());
			}

		});
		
		cboLogo.addItemListener(e-> {
				if(chckbxAddHeader.isSelected())
					tagMaker.setLogo((LOGO)cboLogo.getSelectedItem());
				
				updateInfo();
		});
		
		chckbxBorder.addActionListener(e->{
			tagMaker.setBorder(chckbxBorder.isSelected());
			updateInfo();	
		});
		
		btnNew.addActionListener(e->{
			tagMaker.clear();
			updateInfo();
		});
		
		spinSpace.addChangeListener(ce ->{
			tagMaker.setSpace((int)spinSpace.getValue());
			updateInfo();
		});
		
		btnSave.addActionListener(e->{
			JFileChooser choose = new JFileChooser();
						 choose.showSaveDialog(null);
			File f = choose.getSelectedFile();
			try {
				ImageTools.saveImageInPng(img, f);
			} catch (IOException e1) {
				logger.error("Error saving image", e1);
			}
						 
		});
		
		
	}


}
