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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGPictureEditor;
import org.magic.api.pictures.impl.PersonalSetPicturesProvider;
import org.magic.api.pictureseditor.impl.MTGCardMakerPicturesProvider;
import org.magic.api.pictureseditor.impl.MTGDesignPicturesProvider;
import org.magic.api.providers.impl.PrivateMTGSetProvider;
import org.magic.gui.components.JSONPanel;
import org.magic.gui.components.MagicEditionDetailPanel;
import org.magic.gui.components.dialog.CardSearchImportDialog;
import org.magic.gui.components.editor.MagicCardEditorPanel;
import org.magic.gui.models.MagicCardNamesTableModel;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.gui.models.MagicEditionsTableModel;
import org.magic.gui.renderer.MagicCardNameEditor;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.tools.ImageUtils;

public class CardBuilder2GUI extends JPanel {

	private static final long serialVersionUID = 1L;
	private JXTable editionsTable;
	private MagicEditionDetailPanel magicEditionDetailPanel;
	private MagicCardEditorPanel magicCardEditorPanel;
	private MagicEditionsTableModel editionModel;

	private JComboBox<MagicEdition> cboSets;
	private transient MTGPictureEditor picProvider;
	private transient Image cardImage;
	private JPanel panelPictures;
	private JXTable cardsTable;
	private MagicCardTableModel cardsModel;
	private JSONPanel jsonPanel;
	private JTabbedPane tabbedPane;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private JSpinner spinCommon;
	private JSpinner spinRare;
	private JSpinner spinUnco;
	private JPanel foreignNamesEditorPanel;
	private transient PersonalSetPicturesProvider picturesProvider;
	private transient PrivateMTGSetProvider provider;

	private JButton btnRefresh;
	private JTable listNames;
	private MagicCardNamesTableModel namesModel;

	public CardBuilder2GUI() {
		try {

			logger.info("init Builder GUI");

			//////////////////////////////////////////////////// INIT LOCAL COMPONENTS
			JPanel panelEditionHaut = new JPanel();
			JPanel panelSets = new JPanel();
			JButton btnSaveEdition = new JButton("");
			JButton btnNewSet = new JButton("");
			JButton btnRemoveEdition = new JButton("");
			JPanel buttonsForeignNamesPanel = new JPanel();
			JButton btnRemoveName = new JButton("Remove");
			JSplitPane splitcardEdPanel = new JSplitPane();
			JScrollPane scrollTableEdition = new JScrollPane();
			JPanel panelCards = new JPanel();
			JPanel panelCardsHaut = new JPanel();
			JButton btnImport = new JButton("");
			JScrollPane scrollTableCards = new JScrollPane();
			JButton btnSaveCard = new JButton("");
			JButton btnAddName = new JButton("add Languages");
			JTabbedPane tabbedResult = new JTabbedPane(JTabbedPane.TOP);
			JButton btnRemoveCard = new JButton("");
			JButton btnNewCard = new JButton("");
			JPanel panelBooster = new JPanel();
			JLabel lblCommon = new JLabel("Common :");
			JLabel lblUncommon = new JLabel("Uncommon :");
			JLabel lblRareMythic = new JLabel("Rare/Mythic :");

			JTabbedPane tabbedCards = new JTabbedPane(JTabbedPane.TOP);
			JPanel panelMisc = new JPanel();
			JPanel panelCardEditions = new JPanel();
			JPanel legalitiesPanel = new JPanel();
			legalitiesPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "",TitledBorder.LEADING, TitledBorder.TOP, null, null));

			JToggleButton tglStd = new JToggleButton("STD");
			JToggleButton tglMdn = new JToggleButton("MDN");
			JToggleButton tglVin = new JToggleButton("VIN");
			JToggleButton tglLeg = new JToggleButton("LEG");

			//////////////////////////////////////////////////// INIT GLOBAL COMPONENTS
			editionModel = new MagicEditionsTableModel();
			provider = new PrivateMTGSetProvider();
			btnRefresh = new JButton("");
			picturesProvider = new PersonalSetPicturesProvider();
			spinCommon = new JSpinner();
			spinRare = new JSpinner();
			spinUnco = new JSpinner();
			//picProvider = new MTGCardMakerPicturesProvider();
			picProvider = new MTGDesignPicturesProvider();
			cardsModel = new MagicCardTableModel();
			jsonPanel = new JSONPanel();
			jsonPanel.setMaximumSize(new Dimension(400, 10));
			editionsTable = new JXTable();
			cardsTable = new JXTable();
			tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			cboSets = new JComboBox<>();
			namesModel = new MagicCardNamesTableModel();
			panelPictures = new JPanel() {
				private static final long serialVersionUID = 1L;
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(cardImage, 0, 0, null);
					if (magicCardEditorPanel.getImagePanel().getCroppedImage() != null)
						g.drawImage(magicCardEditorPanel.getImagePanel().getCroppedImage(), 35, 68, 329, 242, null);
					revalidate();
				}
			};

			foreignNamesEditorPanel = new JPanel();
			listNames = new JTable();
			magicCardEditorPanel = new MagicCardEditorPanel();
			magicEditionDetailPanel = new MagicEditionDetailPanel(false, false);

			//////////////////////////////////////////////////// MODELS INIT
			editionsTable.setModel(editionModel);
			cardsTable.setModel(cardsModel);
			listNames.setModel(namesModel);

			spinCommon.setModel(new SpinnerNumberModel(0, 0, null, 1));
			spinUnco.setModel(new SpinnerNumberModel(0, 0, null, 1));
			spinRare.setModel(new SpinnerNumberModel(0, 0, null, 1));

			List<MagicEdition> eds = provider.loadEditions();
			cboSets.setModel(new DefaultComboBoxModel<MagicEdition>(eds.toArray(new MagicEdition[eds.size()])));
			
			//////////////////////////////////////////////////// LAYOUT CONFIGURATION
			setLayout(new BorderLayout(0, 0));
			panelSets.setLayout(new BorderLayout(0, 0));
			panelCards.setLayout(new BorderLayout(0, 0));
			panelMisc.setLayout(new BorderLayout(0, 0));

			GridBagLayout gblPanelBooster = new GridBagLayout();
			gblPanelBooster.columnWidths = new int[] { 218, 218, 0 };
			gblPanelBooster.rowHeights = new int[] { 38, 41, 37, 0 };
			gblPanelBooster.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
			gblPanelBooster.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
			GridBagConstraints gbcLblCommon = new GridBagConstraints();
			gbcLblCommon.fill = GridBagConstraints.BOTH;
			gbcLblCommon.insets = new Insets(0, 0, 5, 5);
			gbcLblCommon.gridx = 0;
			gbcLblCommon.gridy = 0;

			GridBagConstraints gbcSpinCommon = new GridBagConstraints();
			gbcSpinCommon.fill = GridBagConstraints.BOTH;
			gbcSpinCommon.insets = new Insets(0, 0, 5, 0);
			gbcSpinCommon.gridx = 1;
			gbcSpinCommon.gridy = 0;

			GridBagConstraints gbcLblUncommon = new GridBagConstraints();
			gbcLblUncommon.fill = GridBagConstraints.BOTH;
			gbcLblUncommon.insets = new Insets(0, 0, 5, 5);
			gbcLblUncommon.gridx = 0;
			gbcLblUncommon.gridy = 1;

			GridBagConstraints gbcSpinUnco = new GridBagConstraints();
			gbcSpinUnco.fill = GridBagConstraints.BOTH;
			gbcSpinUnco.insets = new Insets(0, 0, 5, 0);
			gbcSpinUnco.gridx = 1;
			gbcSpinUnco.gridy = 1;

			GridBagConstraints gbcLblRareMythic = new GridBagConstraints();
			gbcLblRareMythic.fill = GridBagConstraints.BOTH;
			gbcLblRareMythic.insets = new Insets(0, 0, 0, 5);
			gbcLblRareMythic.gridx = 0;
			gbcLblRareMythic.gridy = 2;
			GridBagConstraints gbcSpinRare = new GridBagConstraints();
			gbcSpinRare.fill = GridBagConstraints.BOTH;
			gbcSpinRare.gridx = 1;
			gbcSpinRare.gridy = 2;

			GridBagLayout gridBagLayout = (GridBagLayout) magicCardEditorPanel.getLayout();
			gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 33, 0, 0};
			gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
			gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0 };

			panelBooster.setLayout(gblPanelBooster);

			//////////////////////////////////////////////////// PANEL ADDS
			add(tabbedPane);
			panelCards.add(panelCardsHaut, BorderLayout.NORTH);
			panelSets.add(panelEditionHaut, BorderLayout.NORTH);
			panelEditionHaut.add(btnNewSet);
			panelEditionHaut.add(btnSaveEdition);
			panelEditionHaut.add(btnRemoveEdition);
			panelSets.add(splitcardEdPanel, BorderLayout.CENTER);
			panelCardsHaut.add(cboSets);
			panelCardsHaut.add(btnNewCard);
			panelCardsHaut.add(btnImport);
			panelCardsHaut.add(btnSaveCard);
			panelCardsHaut.add(btnRefresh);
			panelCards.add(tabbedResult, BorderLayout.EAST);
			panelCardsHaut.add(btnRemoveCard);
			tabbedPane.addTab("Set", MTGConstants.ICON_BACK, panelSets, null);
			tabbedPane.addTab("Cards", MTGConstants.ICON_TAB_DECK, panelCards, null);
			tabbedResult.addTab("Pictures", MTGConstants.ICON_TAB_PICTURE, panelPictures, null);
			tabbedResult.addTab("JSON",MTGConstants.ICON_TAB_JSON, jsonPanel,null);
			panelBooster.add(lblCommon, gbcLblCommon);
			panelBooster.add(spinCommon, gbcSpinCommon);
			panelBooster.add(lblUncommon, gbcLblUncommon);
			panelBooster.add(spinUnco, gbcSpinUnco);
			panelBooster.add(lblRareMythic, gbcLblRareMythic);
			panelBooster.add(spinRare, gbcSpinRare);
			panelCards.add(tabbedCards, BorderLayout.CENTER);
			tabbedCards.addTab("Details", MTGConstants.ICON_TAB_DETAILS, magicCardEditorPanel, null);
			tabbedCards.addTab("Editions", MTGConstants.ICON_BACK, panelCardEditions, null);
			tabbedCards.addTab("Misc", MTGConstants.ICON_TAB_ADMIN, panelMisc, null);
			panelMisc.add(legalitiesPanel, BorderLayout.SOUTH);
			legalitiesPanel.add(tglStd);
			legalitiesPanel.add(tglMdn);
			legalitiesPanel.add(tglLeg);
			legalitiesPanel.add(tglVin);
			splitcardEdPanel.setLeftComponent(scrollTableEdition);
			scrollTableEdition.setViewportView(editionsTable);
			splitcardEdPanel.setRightComponent(scrollTableCards);
			scrollTableCards.setViewportView(cardsTable);
			panelSets.add(magicEditionDetailPanel, BorderLayout.EAST);
			panelMisc.add(foreignNamesEditorPanel);
			foreignNamesEditorPanel.setLayout(new BorderLayout(0, 0));
			foreignNamesEditorPanel.add(new JScrollPane(listNames), BorderLayout.CENTER);
			foreignNamesEditorPanel.add(buttonsForeignNamesPanel, BorderLayout.NORTH);
			buttonsForeignNamesPanel.add(btnAddName);
			buttonsForeignNamesPanel.add(btnRemoveName);

			
			//////////////////////////////////////////////////// COMPONENT CONFIG
			editionModel.init(provider.loadEditions());
			editionModel.fireTableDataChanged();
			splitcardEdPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
			
			btnSaveEdition.setIcon(MTGConstants.ICON_SAVE);
			btnNewSet.setIcon(MTGConstants.ICON_NEW);
			btnRemoveEdition.setIcon(MTGConstants.ICON_DELETE);

			btnSaveEdition.setToolTipText("Save the set");
			btnNewSet.setToolTipText("New set");
			btnRemoveEdition.setToolTipText("Delete Set");
			btnImport.setToolTipText("Import existing card");
			btnSaveCard.setToolTipText("Save the card");
			btnRefresh.setToolTipText("Refresh");
			btnNewCard.setToolTipText("New Card");
			btnRemoveCard.setToolTipText("Delete the card");

			magicEditionDetailPanel.setEditable(true);

			btnImport.setIcon(MTGConstants.ICON_IMPORT);
			btnSaveCard.setIcon(MTGConstants.ICON_SAVE);
			btnRefresh.setIcon(MTGConstants.ICON_REFRESH);
			btnRemoveCard.setIcon(MTGConstants.ICON_DELETE);
			btnNewCard.setIcon(MTGConstants.ICON_NEW);
			cardsTable.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
			panelPictures.setBackground(Color.WHITE);
			panelPictures.setPreferredSize(new Dimension(400, 10));
			listNames.getColumnModel().getColumn(0).setCellEditor(new MagicCardNameEditor());
			buttonsForeignNamesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

			//////////////////////////////////////////////////// ACTION LISTENER
			
			magicCardEditorPanel.getSizeSpinner().addChangeListener(ce->picProvider.setTextSize((Integer)magicCardEditorPanel.getSizeSpinner().getValue()));
			magicCardEditorPanel.getColorIndicatorJCheckBox().addActionListener(ae->picProvider.setColorIndicator(magicCardEditorPanel.getColorIndicatorJCheckBox().isSelected()));
			magicCardEditorPanel.getChboxFoil().addActionListener(ae->picProvider.setFoil(magicCardEditorPanel.getChboxFoil().isSelected()));
			magicCardEditorPanel.getCboColorAccent().addItemListener(ie-> picProvider.setColorAccentuation(magicCardEditorPanel.getCboColorAccent().getSelectedItem().toString()));
			magicCardEditorPanel.getBtnUrl().addActionListener(ae->{
																		
																	try {
																			String urlImage = JOptionPane.showInputDialog("URL");
																			picProvider.setImage(new URI(urlImage));
																		} catch (Exception e1) {
																			logger.error("Error with url ",e1);
																			MTGControler.getInstance().notify(new MTGNotification("ERROR", e1));
																		}
																		
																	});
			
			btnRemoveName.addActionListener(e -> {
				int row = listNames.getSelectedRow();
				namesModel.removeRow(row);
				namesModel.fireTableDataChanged();
			});
			
			btnAddName.addActionListener(e -> {

				MagicCardNames name = new MagicCardNames();
				name.setLanguage("");
				name.setName("");
				magicCardEditorPanel.getMagicCard().getForeignNames().add(name);
				namesModel.init(magicCardEditorPanel.getMagicCard());
			});

			btnNewCard.addActionListener(e -> {
				MagicCard mc = new MagicCard();
				try {
					mc.setNumber(
							String.valueOf(provider.getCards((MagicEdition) cboSets.getSelectedItem()).size() + 1));
					logger.debug("create new card for " + cboSets.getSelectedItem() + " num = " + mc.getNumber());
				} catch (IOException e1) {
					logger.error(e1);
				}
				initCard(mc);
			});

			btnRemoveCard.addActionListener(e -> {
				try {
					int res = JOptionPane.showConfirmDialog(null, MTGControler.getInstance().getLangService()
							.get("CONFIRM_DELETE", magicCardEditorPanel.getMagicCard()));
					if (res == JOptionPane.YES_OPTION) {
						provider.removeCard((MagicEdition) cboSets.getSelectedItem(),
								magicCardEditorPanel.getMagicCard());
						picturesProvider.removePicture((MagicEdition) cboSets.getSelectedItem(),
								magicCardEditorPanel.getMagicCard());
						initCard(new MagicCard());
					}
				} catch (IOException ex) {
					MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),ex));
				}
			});

			btnSaveEdition.addActionListener(e -> {
				try {
					MagicEdition ed = magicEditionDetailPanel.getMagicEdition();
					List<Object> boos = new ArrayList<>();
					for (int i = 0; i < (Integer) spinCommon.getValue(); i++)
						boos.add("common");
					for (int i = 0; i < (Integer) spinUnco.getValue(); i++)
						boos.add("uncommon");
					for (int i = 0; i < (Integer) spinRare.getValue(); i++)
						boos.add(new String[] { "rare", "mythic rare" });

					ed.setBooster(boos);
					provider.saveEdition(ed);

					cboSets.removeAllItems();
					cboSets.setModel(new DefaultComboBoxModel<MagicEdition>(
							provider.loadEditions().toArray(new MagicEdition[provider.loadEditions().size()])));

					editionModel.init(provider.loadEditions());
					editionModel.fireTableDataChanged();
				} catch (Exception ex) {
					MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),ex));
				}
			});

			btnImport.addActionListener(e -> {
				CardSearchImportDialog l = new CardSearchImportDialog();
				l.setVisible(true);
				if (l.getSelected() != null)
					initCard(l.getSelected());

			});

			btnNewSet.addActionListener(e -> magicEditionDetailPanel.setMagicEdition(new MagicEdition(), true));

			btnRemoveEdition.addActionListener(e -> {

				int viewRow = editionsTable.getSelectedRow();
				int modelRow = editionsTable.convertRowIndexToModel(viewRow);
				MagicEdition ed = (MagicEdition) editionsTable.getModel().getValueAt(modelRow, 1);

				int res = JOptionPane.showConfirmDialog(null,
						MTGControler.getInstance().getLangService().get("CONFIRM_DELETE", ed),
						MTGControler.getInstance().getLangService().get("DELETE"), JOptionPane.YES_NO_OPTION);
				if (res == JOptionPane.YES_OPTION) {
					provider.removeEdition(ed);
					try {
						editionModel.init(provider.loadEditions());
						editionModel.fireTableDataChanged();
					} catch (Exception ex) {
						MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),ex));
					}
				}

			});

			cardsTable.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					MagicCard ed = (MagicCard) cardsTable.getValueAt(cardsTable.getSelectedRow(), 0);
					
					
					if (arg0.getClickCount() == 2) {
						initCard(ed);
						tabbedPane.setSelectedIndex(1);
					}

				}
			});
			editionsTable.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					MagicEdition ed = (MagicEdition) editionsTable.getValueAt(editionsTable.getSelectedRow(), 1);
					try {
						initEdition(ed);
						cardsModel.init(provider.getCards(ed));
						cardsModel.fireTableDataChanged();
					} catch (IOException e) {
						MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),e));
					}
				}
			});
			btnSaveCard.addActionListener(e -> {
				MagicEdition me = (MagicEdition) cboSets.getSelectedItem();
				MagicCard mc = magicCardEditorPanel.getMagicCard();
				me.setNumber(mc.getNumber());
				me.setRarity(mc.getRarity());
				me.setArtist(mc.getArtist());
				me.setFlavor(mc.getFlavor());

				if (mc.getId() == null)
					mc.setId(DigestUtils.sha1Hex(me.getSet() + mc.getId() + mc.getName()));

				if ((mc.getCurrentSet()!=me))
					mc.getEditions().add(0, me);
				try {
					provider.addCard(me, mc);
					BufferedImage bi = new BufferedImage(panelPictures.getSize().width, 560,
							BufferedImage.TYPE_INT_ARGB);
					Graphics2D g = bi.createGraphics();
					panelPictures.paint(g);
					g.dispose();
					picturesProvider.savePicture(bi, mc, me);
				} catch (IOException ex) {
					MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),ex));
				}
			});

			btnRefresh.addActionListener(e -> {
				try {
					BufferedImage img = picProvider.getPicture(magicCardEditorPanel.getMagicCard(),null);
					
					if(img!=null)
					{
						cardImage = ImageUtils.scaleResize(img,panelPictures.getWidth());
					}
					panelPictures.revalidate();
					panelPictures.repaint();
					jsonPanel.show(magicCardEditorPanel.getMagicCard());

				} catch (Exception ex) {
					logger.error("error painting",ex);
					MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),ex));
				}

			});

		} catch (Exception e) {
			MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),e));
		}
	}

	protected void initCard(MagicCard mc) {
		magicCardEditorPanel.setMagicCard(mc);
		//
		namesModel.init(mc);
		try {
			cardImage = picturesProvider.getPicture(mc, mc.getCurrentSet());
		} catch (Exception e) {
			btnRefresh.doClick();
			logger.error(e);
		}
		finally {
			panelPictures.repaint();
		}

	}

	protected void initEdition(MagicEdition ed) {
		magicEditionDetailPanel.setMagicEdition(ed);

		spinCommon.setValue(0);
		spinUnco.setValue(0);
		spinRare.setValue(0);

		for (Object o : ed.getBooster()) {
			if (o.equals("common"))
				spinCommon.setValue((int) spinCommon.getValue() + 1);
			else if (o.equals("uncommon"))
				spinUnco.setValue((int) spinUnco.getValue() + 1);
			else
				spinRare.setValue((int) spinRare.getValue() + 1);
		}

	}

}
