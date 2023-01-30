package org.magic.gui;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.apache.commons.codec.digest.DigestUtils;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGPictureEditor;
import org.magic.api.interfaces.MTGPictureEditor.MOD;
import org.magic.api.pictures.impl.PersonalSetPicturesProvider;
import org.magic.api.providers.impl.PrivateMTGSetProvider;
import org.magic.api.sorters.CardsEditionSorter;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.MagicEditionDetailPanel;
import org.magic.gui.components.ObjectViewerPanel;
import org.magic.gui.components.dialog.CardSearchImportDialog;
import org.magic.gui.components.editor.MagicCardEditorPanel;
import org.magic.gui.editor.MagicCardNameEditor;
import org.magic.gui.models.MagicCardNamesTableModel;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.gui.models.MagicEditionsTableModel;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.UITools;
public class CardBuilder2GUI extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JXTable editionsTable;
	private MagicEditionDetailPanel magicEditionDetailPanel;
	private MagicCardEditorPanel magicCardEditorPanel;
	private MagicEditionsTableModel editionModel;
	private JComboBox<MagicEdition> cboSets;
	private transient Image cardImage;
	private JPanel panelPictures;
	private JXTable cardsTable;
	private MagicCardTableModel cardsModel;
	private ObjectViewerPanel jsonPanel;
	private JTabbedPane tabbedPane;
	private JSpinner spinCommon;
	private JSpinner spinRare;
	private JSpinner spinUnco;
	private JPanel foreignNamesEditorPanel;
	private transient PersonalSetPicturesProvider picturesProvider;
	private transient PrivateMTGSetProvider provider;
	private JButton btnRefresh;
	private JXTable listNames;
	private MagicCardNamesTableModel namesModel;


	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_BUILDER;
	}

	@Override
	public String getTitle() {
		return capitalize("BUILDER_MODULE");
	}


	@Override
	public void onFirstShowing() {
		try {
			editionModel.init(provider.listEditions());
			editionModel.fireTableDataChanged();
		} catch (IOException e) {
			logger.error(e);
		}

	}

	public CardBuilder2GUI() {
		try {
			//////////////////////////////////////////////////// INIT LOCAL COMPONENTS
			var panelEditionHaut = new JPanel();
			var panelSets = new JPanel();
			var btnSaveEdition = new JButton("");
			var btnNewSet = new JButton("");
			var btnRemoveEdition = new JButton("");
			var buttonsForeignNamesPanel = new JPanel();
			var btnRemoveName = new JButton("Remove");
			var splitcardEdPanel = new JSplitPane();
			var panelCards = new JPanel();
			var panelCardsHaut = new JPanel();
			var btnImport = new JButton(MTGConstants.ICON_IMPORT);
			var btnRefreshSet = new JButton(MTGConstants.ICON_REFRESH);

			var btnSaveCard = new JButton(MTGConstants.ICON_SAVE);
			var btnAddName = new JButton("add Languages");
			var tabbedResult = new JTabbedPane(SwingConstants.TOP);
			var btnRemoveCard = new JButton("");
			var btnNewCard = new JButton("");
			var panelBooster = new JPanel();
			var lblCommon = new JLabel("Common :");
			var lblUncommon = new JLabel("Uncommon :");
			var lblRareMythic = new JLabel("Rare/Mythic :");

			var tabbedCards = new JTabbedPane(SwingConstants.TOP);
			var panelMisc = new JPanel();
			var panelCardEditions = new JPanel();
			var legalitiesPanel = new JPanel();
			legalitiesPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "",TitledBorder.LEADING, TitledBorder.TOP, null, null));

			var tglStd = new JToggleButton("STD");
			var tglMdn = new JToggleButton("MDN");
			var tglVin = new JToggleButton("VIN");
			var tglLeg = new JToggleButton("LEG");

			//////////////////////////////////////////////////// INIT GLOBAL COMPONENTS
			editionModel = new MagicEditionsTableModel();
			provider = new PrivateMTGSetProvider();
			provider.init();
			btnRefresh = new JButton("");
			picturesProvider = new PersonalSetPicturesProvider();
			spinCommon = new JSpinner();
			spinRare = new JSpinner();
			spinUnco = new JSpinner();
			cardsModel = new MagicCardTableModel();
			jsonPanel = new ObjectViewerPanel();
			jsonPanel.setMaximumSize(new Dimension(400, 10));
			editionsTable = UITools.createNewTable(null);
			cardsTable = UITools.createNewTable(null);
			tabbedPane = new JTabbedPane(SwingConstants.TOP);
			cboSets = new JComboBox<>();
			namesModel = new MagicCardNamesTableModel();
			panelPictures = new JPanel() {
				private static final long serialVersionUID = 1L;
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(cardImage, 0, 0, null);


					if (magicCardEditorPanel.getImagePanel().getCroppedImage() != null && getEnabledPlugin(MTGPictureEditor.class).getMode()==MOD.LOCAL)
						g.drawImage(magicCardEditorPanel.getImagePanel().getCroppedImage(), 35, 68, 329, 242, null);


					revalidate();
				}
			};

			foreignNamesEditorPanel = new JPanel();
			listNames = UITools.createNewTable(null);
			magicCardEditorPanel = new MagicCardEditorPanel();
			magicEditionDetailPanel = new MagicEditionDetailPanel(false);

			//////////////////////////////////////////////////// MODELS INIT
			editionsTable.setModel(editionModel);
			cardsTable.setModel(cardsModel);
			listNames.setModel(namesModel);

			spinCommon.setModel(new SpinnerNumberModel(0, 0, null, 1));
			spinUnco.setModel(new SpinnerNumberModel(0, 0, null, 1));
			spinRare.setModel(new SpinnerNumberModel(0, 0, null, 1));

			List<MagicEdition> eds = provider.listEditions();
			cboSets.setModel(new DefaultComboBoxModel<>(eds.toArray(new MagicEdition[eds.size()])));


			//////////////////////////////////////////////////// LAYOUT CONFIGURATION
			setLayout(new BorderLayout(0, 0));
			panelSets.setLayout(new BorderLayout(0, 0));
			panelCards.setLayout(new BorderLayout(0, 0));
			panelMisc.setLayout(new BorderLayout(0, 0));

			var gblPanelBooster = new GridBagLayout();
			gblPanelBooster.columnWidths = new int[] { 218, 218, 0 };
			gblPanelBooster.rowHeights = new int[] { 38, 41, 37, 0 };
			gblPanelBooster.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
			gblPanelBooster.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
			var gbcLblCommon = UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 0, 0);
			var gbcSpinCommon = UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 0);
			var gbcLblUncommon = UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 0, 1);
			var gbcSpinUnco = UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 1);
			var gbcLblRareMythic = UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 0, 2);
			var gbcSpinRare = UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 2);

			var gridBagLayout = (GridBagLayout) magicCardEditorPanel.getLayout();
			gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 33, 0, 0};
			gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
			gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0 };

			panelBooster.setLayout(gblPanelBooster);
			splitcardEdPanel.setDividerLocation(0.5);
			splitcardEdPanel.setResizeWeight(0.5);


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
			tabbedResult.addTab("Object",MTGConstants.ICON_TAB_JSON, jsonPanel,null);
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
			splitcardEdPanel.setLeftComponent(new JScrollPane(editionsTable));

			splitcardEdPanel.setRightComponent( new JScrollPane(cardsTable));
			panelSets.add(magicEditionDetailPanel, BorderLayout.EAST);
			panelMisc.add(foreignNamesEditorPanel);
			foreignNamesEditorPanel.setLayout(new BorderLayout(0, 0));
			foreignNamesEditorPanel.add(new JScrollPane(listNames), BorderLayout.CENTER);
			foreignNamesEditorPanel.add(buttonsForeignNamesPanel, BorderLayout.NORTH);
			buttonsForeignNamesPanel.add(btnAddName);
			buttonsForeignNamesPanel.add(btnRemoveName);


			//////////////////////////////////////////////////// COMPONENT CONFIG

			splitcardEdPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);

			btnSaveEdition.setIcon(MTGConstants.ICON_SAVE);
			btnNewSet.setIcon(MTGConstants.ICON_NEW);
			btnRemoveEdition.setIcon(MTGConstants.ICON_DELETE);

			btnSaveEdition.setToolTipText("Save the set");
			btnNewSet.setToolTipText("New set");
			btnRemoveEdition.setToolTipText("Delete Set");

			panelEditionHaut.add(btnRefreshSet);
			btnImport.setToolTipText("Import existing card");
			btnSaveCard.setToolTipText("Save the card");
			btnRefresh.setToolTipText("Refresh");
			btnNewCard.setToolTipText("New Card");
			btnRemoveCard.setToolTipText("Delete the card");

			magicEditionDetailPanel.setEditable(true);



			btnRefresh.setIcon(MTGConstants.ICON_REFRESH);
			btnRemoveCard.setIcon(MTGConstants.ICON_DELETE);
			btnNewCard.setIcon(MTGConstants.ICON_NEW);
			cardsTable.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
			panelPictures.setBackground(Color.WHITE);
			panelPictures.setPreferredSize(new Dimension(400, 10));
			listNames.getColumnModel().getColumn(0).setCellEditor(new MagicCardNameEditor());
			buttonsForeignNamesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

			//////////////////////////////////////////////////// ACTION LISTENER

			magicCardEditorPanel.getSizeSpinner().addChangeListener(ce->getEnabledPlugin(MTGPictureEditor.class).setTextSize((Integer)magicCardEditorPanel.getSizeSpinner().getValue()));
			magicCardEditorPanel.getColorIndicatorJCheckBox().addActionListener(ae->getEnabledPlugin(MTGPictureEditor.class).setColorIndicator(magicCardEditorPanel.getColorIndicatorJCheckBox().isSelected()));
			magicCardEditorPanel.getChboxFoil().addActionListener(ae->getEnabledPlugin(MTGPictureEditor.class).setFoil(magicCardEditorPanel.getChboxFoil().isSelected()));
			magicCardEditorPanel.getCboColorAccent().addItemListener(ie-> getEnabledPlugin(MTGPictureEditor.class).setColorAccentuation(magicCardEditorPanel.getCboColorAccent().getSelectedItem().toString()));


			btnRefreshSet.addActionListener(e->{

				MagicEdition ed = (MagicEdition) editionsTable.getValueAt(editionsTable.getSelectedRow(), 1);
				try {
					List<MagicCard> cards = provider.getCards(ed);
					ed.setCardCount(cards.size());

					cards.forEach(mc->mc.getCurrentSet().setNumber(null));
					Collections.sort(cards,new CardsEditionSorter());
					for(var i=0;i<cards.size();i++)
						{
							cards.get(i).getCurrentSet().setNumber(String.valueOf((i+1)));
						}
					provider.saveEdition(ed,cards);






				} catch (IOException e1) {
					logger.error(e1);
				}




			});

			btnRemoveName.addActionListener(e -> {
				int row = listNames.getSelectedRow();
				namesModel.removeRow(row);
				namesModel.fireTableDataChanged();
			});

			btnAddName.addActionListener(e -> {

				var name = new MagicCardNames();
				name.setLanguage("");
				name.setName("");
				magicCardEditorPanel.getMagicCard().getForeignNames().add(name);
				namesModel.init(magicCardEditorPanel.getMagicCard());
			});

			btnNewCard.addActionListener(e -> {
				var mc = new MagicCard();
				mc.getEditions().add((MagicEdition)cboSets.getSelectedItem());
				try {
					mc.getCurrentSet().setNumber(
							String.valueOf(provider.getCards((MagicEdition) cboSets.getSelectedItem()).size() + 1));
				} catch (IOException e1) {
					logger.error(e1);
				}
				initCard(mc);
			});

			btnRemoveCard.addActionListener(e -> {
				try {
					int res = JOptionPane.showConfirmDialog(null, MTGControler.getInstance().getLangService().get("CONFIRM_DELETE", magicCardEditorPanel.getMagicCard()));
					if (res == JOptionPane.YES_OPTION) {
						provider.removeCard((MagicEdition) cboSets.getSelectedItem(),
								magicCardEditorPanel.getMagicCard());
						picturesProvider.removePicture((MagicEdition) cboSets.getSelectedItem(),
								magicCardEditorPanel.getMagicCard());
						initCard(new MagicCard());
					}
				} catch (IOException ex) {
					MTGControler.getInstance().notify(ex);
				}
			});

			btnSaveEdition.addActionListener(e -> {
				try {
					var ed = magicEditionDetailPanel.getMagicEdition();
					List<Object> boos = new ArrayList<>();
					for (var i = 0; i < (Integer) spinCommon.getValue(); i++)
						boos.add("common");
					for (var i = 0; i < (Integer) spinUnco.getValue(); i++)
						boos.add("uncommon");
					for (var i = 0; i < (Integer) spinRare.getValue(); i++)
						boos.add(new String[] { "rare", "mythic rare" });

					ed.setBooster(boos);
					provider.saveEdition(ed);

					cboSets.removeAllItems();
					cboSets.setModel(new DefaultComboBoxModel<>(
							provider.listEditions().toArray(new MagicEdition[provider.listEditions().size()])));

					editionModel.init(provider.listEditions());
					editionModel.fireTableDataChanged();
				} catch (Exception ex) {
					MTGControler.getInstance().notify(ex);
				}
			});

			btnImport.addActionListener(e -> {
				var l = new CardSearchImportDialog();
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
						editionModel.init(provider.listEditions());
						editionModel.fireTableDataChanged();
					} catch (Exception ex) {
						MTGControler.getInstance().notify(ex);
					}
				}

			});

			cardsTable.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent me) {

					if(UITools.getTableSelections(cardsTable, 1).isEmpty())
						return;

					MagicCard ed = UITools.getTableSelection(cardsTable, 0);
					if (me.getClickCount() == 2) {
						initCard(ed);
						tabbedPane.setSelectedIndex(1);
					}

				}
			});
			editionsTable.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent me) {

					if(UITools.getTableSelections(editionsTable, 1).isEmpty())
						return;

					MagicEdition ed =UITools.getTableSelection(editionsTable, 1);
					try {
						initEdition(ed);
						cardsModel.init(provider.getCards(ed));
						cardsModel.fireTableDataChanged();
					} catch (IOException e) {
						MTGControler.getInstance().notify(e);
					}
				}
			});


			btnSaveCard.addActionListener(e -> {


				btnRefresh.doClick();

				MagicEdition me = (MagicEdition) cboSets.getSelectedItem();
				var mc = magicCardEditorPanel.getMagicCard();
				me.setNumber(mc.getCurrentSet().getNumber());

				if (mc.getId() == null)
					mc.setId(DigestUtils.sha256Hex(me.getSet() + mc.getId() + mc.getName()));

				if ((mc.getCurrentSet()!=me))
					mc.getEditions().add(0, me);
				try {
					provider.addCard(me, mc);
					var bi = new BufferedImage(panelPictures.getSize().width, 560,BufferedImage.TYPE_INT_ARGB);
					Graphics2D g = bi.createGraphics();
					panelPictures.paint(g);
					g.dispose();
					picturesProvider.savePicture(bi, mc, me);
				} catch (IOException ex) {
					MTGControler.getInstance().notify(ex);
				}
			});

			btnRefresh.addActionListener(e -> {
				try {
					BufferedImage img = getEnabledPlugin(MTGPictureEditor.class).getPicture(magicCardEditorPanel.getMagicCard(),(MagicEdition) cboSets.getSelectedItem());

					if(img!=null)
					{
						cardImage = ImageTools.scaleResize(img,panelPictures.getWidth());
					}
					panelPictures.revalidate();
					panelPictures.repaint();
					jsonPanel.init(magicCardEditorPanel.getMagicCard());

				} catch (Exception ex) {
					logger.error("error painting",ex);
					MTGControler.getInstance().notify(ex);
				}

			});

		} catch (Exception e) {
			MTGControler.getInstance().notify(e);
		}
	}

	protected void initCard(MagicCard mc) {
		magicCardEditorPanel.setMagicCard(mc);
		jsonPanel.init(mc);
		namesModel.init(mc);
		try {
			cardImage = picturesProvider.getPicture(mc);
			panelPictures.repaint();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	protected void initEdition(MagicEdition ed) {
		magicEditionDetailPanel.init(ed);

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
