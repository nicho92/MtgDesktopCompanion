package org.magic.gui.components;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

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
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileFilter;

import org.magic.api.beans.MTGEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.sealedprovider.impl.MTGCompanionSealedProvider.LOGO;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.decorators.JListFilterDecorator;
import org.magic.gui.renderer.MagicEditionIconListRenderer;
import org.magic.services.BinderTagsManager;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.tools.ImageTools;
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

	private JList<MTGEdition> listEditions;
	private JList<MTGEdition> listSelected;
	private DefaultListModel<MTGEdition> model;
	private DefaultListModel<MTGEdition> modelSelect;
	private JButton btnRefresh;
	private JPanel panneauBas;
	private JPanel panel;

	public void updateInfo() {
		img = ImageTools.trimAlpha(tagMaker.generate());
		previewPanel.revalidate();
		previewPanel.repaint();
	}

	private void init()
	{
		var d = new Dimension(567,2173);
		double mmW = ImageTools.toMM(d.getWidth());
 		double mmH = ImageTools.toMM(d.getHeight());
 		String res = JOptionPane.showInputDialog("Dimension in mm", mmW+"x"+mmH);
 		String[] result = res.split("x");

 		d = new Dimension((int)ImageTools.toPX(Double.parseDouble(result[0])), (int)ImageTools.toPX(Double.parseDouble(result[1])));
		tagMaker = new BinderTagsManager(d);
	}


	@Override
	public String getTitle() {
		return capitalize("BINDER_TAG_EDITOR");
	}


	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_BINDERS;
	}




	public BinderTagsEditorComponent() {

		init();

		leftPanel = new JPanel();
		var editorPanel = new JPanel();
		model = new DefaultListModel<>();
		modelSelect = new DefaultListModel<>();
		listSelected = new JList<>(modelSelect);
		listSelected.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		setLayout(new BorderLayout(0, 0));

		try {

			List<MTGEdition> lst = getEnabledPlugin(MTGCardsProvider.class).listEditions();
			Collections.sort(lst);
			for(MTGEdition ed : lst)
				model.addElement(ed);

		} catch (IOException e2) {
			logger.error(e2);
		}

		previewPanel = new JPanel() {
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


		var scrollPane = new JScrollPane(previewPanel);
		scrollPane.setPreferredSize(new Dimension(250, 250));
		add(scrollPane, BorderLayout.CENTER);
		add(leftPanel, BorderLayout.WEST);
		leftPanel.setLayout(new BorderLayout(0, 0));
		leftPanel.add(editorPanel, BorderLayout.CENTER);
		editorPanel.setLayout(new BorderLayout(0, 0));

		panneauBas = new JPanel();
		editorPanel.add(panneauBas, BorderLayout.SOUTH);
		panneauBas.setLayout(new BoxLayout(panneauBas, BoxLayout.Y_AXIS));



		panel1 = new JPanel();
		panneauBas.add(panel1);
		var flowLayout = (FlowLayout) panel1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);

		chckbxAddHeader = new JCheckBox("add Header");
		panel1.add(chckbxAddHeader);

		cboLogo = new JComboBox<>(new DefaultComboBoxModel<>(LOGO.values()));
		panel1.add(cboLogo);

		listSelected.setCellRenderer(new MagicEditionIconListRenderer());

		panelInterspace = new JPanel();
		panneauBas.add(panelInterspace);
		var flowLayout1 = (FlowLayout) panelInterspace.getLayout();
		flowLayout1.setAlignment(FlowLayout.LEFT);

		lblInterSpace = new JLabel("Space :");
		panelInterspace.add(lblInterSpace);

		spinSpace = new JSpinner();

		panelInterspace.add(spinSpace);

		btnBackgroundColor = new JButton(MTGConstants.ICON_GAME_COLOR);
		panelInterspace.add(btnBackgroundColor);

		chckbxBorder = new JCheckBox("Border");
		panelInterspace.add(chckbxBorder);

		panel = new JPanel();
		editorPanel.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		listEditions = new JList<>(model);
		listEditions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		var deco = JListFilterDecorator.decorate(listEditions,(MTGEdition t, String u)->t.getSet().toLowerCase().contains(u.toLowerCase()));


		listEditions.setCellRenderer(new MagicEditionIconListRenderer());
		panel.add(new JScrollPane(deco.getContentPanel()));

		panel.add(new JLabel("\u2193"));

		panel.add(new JScrollPane(listSelected));
		var commandsPanel = new JPanel();
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

		btnRefresh.addActionListener(_-> {
			tagMaker.setEditions(Collections.list(modelSelect.elements()));
			updateInfo();
		});


		btnBackgroundColor.addActionListener(_->{
			var selected = JColorChooser.showDialog(null, "Color Selection", Color.WHITE);
			tagMaker.setBackColor(selected);
			updateInfo();
		});

		chckbxAddHeader.addActionListener(_->{
			if(!chckbxAddHeader.isSelected())
			{
				tagMaker.setLogo(null);
			}
			else
			{
				tagMaker.setLogo((LOGO)cboLogo.getSelectedItem());
			}

		});

		cboLogo.addItemListener(_-> {
				if(chckbxAddHeader.isSelected())
					tagMaker.setLogo((LOGO)cboLogo.getSelectedItem());

				updateInfo();
		});

		chckbxBorder.addActionListener(_->{
			tagMaker.setBorder(chckbxBorder.isSelected());
			updateInfo();
		});

		btnNew.addActionListener(_->{
			tagMaker.clear();
			updateInfo();
		});

		spinSpace.addChangeListener(_ ->{
			tagMaker.setSpace((int)spinSpace.getValue());
			updateInfo();
		});

		btnSave.addActionListener(_->{
			var choose = new JFileChooser(MTGConstants.DATA_DIR);

			choose.setFileFilter(new FileFilter() {

				@Override
				public String getDescription() {
					return "*.png,*.PNG";
				}

				@Override
				public boolean accept(File f) {
					return f.getName().toLowerCase().endsWith(".png");
				}
			});



			int res= choose.showSaveDialog(null);

			if(res==JFileChooser.APPROVE_OPTION) {
				var f = choose.getSelectedFile();
				try {
					ImageTools.saveImageInPng(img, f);
				} catch (Exception e1) {
					logger.error("error",e1);
					MTGControler.getInstance().notify(e1);
				}
			}
		});

		listEditions.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2)
				{
					modelSelect.addElement(listEditions.getSelectedValue());
				}


			}
		});

		listSelected.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2)
				{
					modelSelect.removeElementAt(listSelected.getSelectedIndex());
				}
			}
		});

	}


}
