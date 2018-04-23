package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.magic.api.beans.Booster;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGComparator;
import org.magic.game.gui.components.BoosterPanel;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GraveyardPanel;
import org.magic.game.model.PositionEnum;
import org.magic.gui.components.charts.CmcChartPanel;
import org.magic.gui.components.charts.ManaRepartitionPanel;
import org.magic.gui.components.charts.TypeRepartitionPanel;
import org.magic.gui.models.SealedPackTableModel;
import org.magic.gui.renderer.IntegerCellEditor;
import org.magic.gui.renderer.MagicEditionListRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGDeckManager;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;
import org.magic.sorters.CmcSorter;
import org.magic.sorters.ColorSorter;
import org.magic.sorters.TypesSorter;

public class SealedPanel extends JPanel {
	private JLabel lblLoading;
	private SealedPackTableModel model;
	private BoosterPanel panelOpenedBooster;
	private JComboBox<MagicEdition> cboEditions;
	private JButton btnOpen;
	private CmcChartPanel cmcChartPanel;
	private ManaRepartitionPanel manaRepartitionPanel;
	private TypeRepartitionPanel typeRepartitionPanel;
	private MagicDeck deck;
	private List<MagicCard> list;
	private boolean analyseDeck = false;
	private JTextField txtNumberLand;
	private JComboBox<String> cboLands;
	private GraveyardPanel panelDeck;
	private MagicCardDetailPanel panelDetail;
	private JProgressBar progressBar;
	private transient MTGDeckManager deckManager;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public SealedPanel() {
		deckManager = new MTGDeckManager();
		initGUI();
	}

	private void initGUI() {

		JPanel panelWest;
		JButton btnSaveDeck;
		JSplitPane panelCenter;
		JButton btnAddBoosters;
		JScrollPane scrollTablePack;
		JTable table;
		JPanel panelControl;
		JPanel panelAnalyse;
		JPanel panelSorters;
		JRadioButton rdioCmcSortButton;
		JRadioButton rdiocolorSort;
		JRadioButton rdiotypeSort;
		JPanel panel;
		JPanel panelEast;

		JPanel panelAnalyseChooser;
		JRadioButton rdioDeckAnalyse;
		JRadioButton rdioBoosterAnalyse;
		JPanel panelLands;

		setLayout(new BorderLayout(0, 0));
		panelOpenedBooster = new BoosterPanel();
		model = new SealedPackTableModel();
		panelDetail = new MagicCardDetailPanel();
		panelDetail.enableThumbnail(true);
		panelDetail.enableCollectionLookup(false);

		List<MagicEdition> li;
		try {
			li = MTGControler.getInstance().getEnabledProviders().loadEditions();
		} catch (IOException e1) {
			li = new ArrayList<>();
			logger.error(e1);
		}
		panelWest = new JPanel();
		panelWest.setPreferredSize(new Dimension(300, 10));

		add(panelWest, BorderLayout.WEST);
		panelWest.setLayout(new BorderLayout(0, 0));

		panelControl = new JPanel();
		panelWest.add(panelControl, BorderLayout.NORTH);
		table = new JTable(model);
		table.getColumnModel().getColumn(1).setCellEditor(new IntegerCellEditor());
		panelControl.setLayout(new BorderLayout(0, 0));

		panel = new JPanel();
		panelControl.add(panel, BorderLayout.NORTH);
		GridBagLayout gblpanel = new GridBagLayout();
		gblpanel.columnWidths = new int[] { 105, 65, 0, 0, 0 };
		gblpanel.rowHeights = new int[] { 41, 0, 0 };
		gblpanel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gblpanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gblpanel);
		cboEditions = new JComboBox<>();
		GridBagConstraints gbccboEditions = new GridBagConstraints();
		gbccboEditions.fill = GridBagConstraints.HORIZONTAL;
		gbccboEditions.gridwidth = 4;
		gbccboEditions.insets = new Insets(0, 0, 5, 0);
		gbccboEditions.gridx = 0;
		gbccboEditions.gridy = 0;
		panel.add(cboEditions, gbccboEditions);
		cboEditions.setRenderer(new MagicEditionListRenderer());
		cboEditions.setModel(new DefaultComboBoxModel<MagicEdition>(li.toArray(new MagicEdition[li.size()])));

		btnAddBoosters = new JButton(MTGConstants.ICON_NEW);
		GridBagConstraints gbcbtnAddBoosters = new GridBagConstraints();
		gbcbtnAddBoosters.anchor = GridBagConstraints.NORTH;
		gbcbtnAddBoosters.insets = new Insets(0, 0, 0, 5);
		gbcbtnAddBoosters.gridx = 0;
		gbcbtnAddBoosters.gridy = 1;
		panel.add(btnAddBoosters, gbcbtnAddBoosters);

		btnOpen = new JButton(MTGConstants.ICON_OPEN);
		GridBagConstraints gbcbtnOpen = new GridBagConstraints();
		gbcbtnOpen.insets = new Insets(0, 0, 0, 5);
		gbcbtnOpen.anchor = GridBagConstraints.NORTH;
		gbcbtnOpen.gridx = 1;
		gbcbtnOpen.gridy = 1;
		panel.add(btnOpen, gbcbtnOpen);
		btnOpen.setEnabled(false);

		btnSaveDeck = new JButton(MTGConstants.ICON_SAVE);
		GridBagConstraints gbcbtnSaveDeck = new GridBagConstraints();
		gbcbtnSaveDeck.insets = new Insets(0, 0, 0, 5);
		gbcbtnSaveDeck.gridx = 2;
		gbcbtnSaveDeck.gridy = 1;
		panel.add(btnSaveDeck, gbcbtnSaveDeck);

		lblLoading = new JLabel(MTGConstants.ICON_LOADING);
		lblLoading.setVisible(false);
		GridBagConstraints gbclblLoading = new GridBagConstraints();
		gbclblLoading.gridx = 3;
		gbclblLoading.gridy = 1;
		panel.add(lblLoading, gbclblLoading);
		btnSaveDeck.addActionListener(e -> save());
		btnOpen.addActionListener(ae -> open());
		btnAddBoosters.addActionListener(ae -> addBooster());

		scrollTablePack = new JScrollPane();
		scrollTablePack.setPreferredSize(new Dimension(2, 100));
		panelControl.add(scrollTablePack);
		scrollTablePack.setViewportView(table);

		panelAnalyse = new JPanel();
		panelWest.add(panelAnalyse, BorderLayout.CENTER);
		panelAnalyse.setLayout(new GridLayout(5, 1, 0, 0));

		panelSorters = new JPanel();
		panelAnalyse.add(panelSorters);
		panelSorters.setLayout(new GridLayout(0, 1, 0, 0));

		rdioCmcSortButton = new JRadioButton(
				MTGControler.getInstance().getLangService().getCapitalize("SORT_BY", "cmc"));
		rdioCmcSortButton.addActionListener(ae -> sort(new CmcSorter()));

		panelSorters.add(rdioCmcSortButton);

		rdiocolorSort = new JRadioButton(MTGControler.getInstance().getLangService().getCapitalize("SORT_BY", "color"));
		rdiocolorSort.addActionListener(ae -> sort(new ColorSorter()));

		panelSorters.add(rdiocolorSort);

		rdiotypeSort = new JRadioButton(MTGControler.getInstance().getLangService().getCapitalize("SORT_BY", "type"));
		rdiotypeSort.addActionListener(ae -> sort(new TypesSorter()));

		panelSorters.add(rdiotypeSort);

		ButtonGroup groupSorter = new ButtonGroup();
		groupSorter.add(rdioCmcSortButton);
		groupSorter.add(rdiocolorSort);
		groupSorter.add(rdiotypeSort);

		panelAnalyseChooser = new JPanel();
		panelSorters.add(panelAnalyseChooser);
		FlowLayout flowLayout = (FlowLayout) panelAnalyseChooser.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);

		rdioBoosterAnalyse = new JRadioButton("Booster");
		rdioBoosterAnalyse.setSelected(true);
		rdioBoosterAnalyse.addActionListener(e -> analyseDeck(false));
		panelAnalyseChooser.add(rdioBoosterAnalyse);

		rdioDeckAnalyse = new JRadioButton("Deck");
		rdioDeckAnalyse.addActionListener(e -> analyseDeck(true));

		panelAnalyseChooser.add(rdioDeckAnalyse);

		ButtonGroup groupAnalyser = new ButtonGroup();
		groupAnalyser.add(rdioBoosterAnalyse);
		groupAnalyser.add(rdioDeckAnalyse);

		progressBar = new JProgressBar();
		progressBar.setMaximum(40);
		progressBar.setStringPainted(true);
		panelSorters.add(progressBar);

		cmcChartPanel = new CmcChartPanel();
		panelAnalyse.add(cmcChartPanel);

		manaRepartitionPanel = new ManaRepartitionPanel();
		panelAnalyse.add(manaRepartitionPanel);

		typeRepartitionPanel = new TypeRepartitionPanel();
		panelAnalyse.add(typeRepartitionPanel);
		panelCenter = new JSplitPane();
		panelCenter.setResizeWeight(0.5);
		panelCenter.setOrientation(JSplitPane.VERTICAL_SPLIT);

		JScrollPane paneBooster = new JScrollPane();
		paneBooster.setViewportView(panelOpenedBooster);
		panelCenter.setLeftComponent(paneBooster);
		panelCenter.setRightComponent(panelDetail);
		add(panelCenter, BorderLayout.CENTER);

		panelEast = new JPanel();
		add(panelEast, BorderLayout.EAST);
		panelEast.setLayout(new BorderLayout(0, 0));

		panelDeck = new GraveyardPanel() {
			@Override
			public PositionEnum getOrigine() {
				return PositionEnum.DECK;

			}

			@Override
			public void moveCard(DisplayableCard mc, PositionEnum to) {
				if (to == PositionEnum.BOOSTER) {
					deck.remove(mc.getMagicCard());
					list.add(mc.getMagicCard());
					refreshStats();
				}
			}

			@Override
			public void addComponent(DisplayableCard i) {
				super.addComponent(i);
				deck.add(i.getMagicCard());
				refreshStats();
			}

		};

		JScrollPane scrollDeck = new JScrollPane();
		scrollDeck.setViewportView(panelDeck);
		panelEast.add(scrollDeck);
		panelDeck.setPreferredSize(new Dimension((int) MTGControler.getInstance().getCardsDimension().getWidth() + 5,
				(int) (MTGControler.getInstance().getCardsDimension().getHeight() * 30)));

		panelEast.add(new JLabel(MTGControler.getInstance().getLangService().getCapitalize("DROP_HERE")),
				BorderLayout.NORTH);

		panelLands = new JPanel();
		panelEast.add(panelLands, BorderLayout.SOUTH);

		txtNumberLand = new JTextField();
		panelLands.add(txtNumberLand);
		txtNumberLand.setColumns(2);

		cboLands = new JComboBox<>(
				new DefaultComboBoxModel<>(new String[] { "Plains", "Island", "Swamp", "Mountain", "Forest" }));
		panelLands.add(cboLands);

		JButton btnAddLands = new JButton("+");
		btnAddLands.addActionListener(ae -> addLands());
		panelLands.add(btnAddLands);

	}

	private void addLands() {
		int qte = Integer.parseInt(txtNumberLand.getText());
		String land = cboLands.getSelectedItem().toString();

		MagicEdition ed = new MagicEdition();
		ed.setId(MTGControler.getInstance().get("default-land-deck"));
		try {
			MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", land, ed, true)
					.get(0);

			for (int i = 0; i < qte; i++) {
				deck.add(mc);
				DisplayableCard c = createCard(mc);
				panelDeck.addComponent(c);
				panelDeck.postTreatment(c);
			}

			refreshStats();
		} catch (IOException e) {
			logger.error(e);
		}

	}

	private void analyseDeck(boolean b) {
		this.analyseDeck = b;
	}

	private void addBooster() {
		model.add((MagicEdition) cboEditions.getSelectedItem(), 6);
		btnOpen.setEnabled(model.getSealedPack().size() > 0);
	}

	protected void open() {
		deck = new MagicDeck();
		
		deck.setDescription("Sealed from " + model.getSealedPack());
		deck.setName("sealed from " + model.getSealedPack().toList().size() + " boosters");

		panelOpenedBooster.clear();
		panelDeck.removeAll();
		panelDeck.revalidate();
		panelDeck.repaint();
		ThreadManager.getInstance().execute(() -> {
			int column = 0;
			list = new ArrayList<>();
			lblLoading.setVisible(true);
			for (Entry<MagicEdition, Integer> ed : model.getSealedPack().getEntries()) {

				try {
					for (int i = 0; i < ed.getValue(); i++) {

						Booster b = MTGControler.getInstance().getEnabledProviders().generateBooster(ed.getKey());
						column++;
						for (MagicCard mc : b.getCards()) {
							list.add(mc);
							DisplayableCard c = createCard(mc);

							panelOpenedBooster.addComponent(c, column);
						}

					}
				} catch (IOException e) {
					logger.error(e);
					lblLoading.setVisible(false);
				}

			}
			panelOpenedBooster.setList(list);
			refreshStats();
			lblLoading.setVisible(false);
		});
	}

	private DisplayableCard createCard(MagicCard mc) {
		DisplayableCard c = new DisplayableCard(mc, MTGControler.getInstance().getCardsDimension(), true, false);
		c.addObserver(panelDetail);
		return c;

	}

	public void sort(MTGComparator<MagicCard> sorter) {
		Collections.sort(list, sorter);
		panelOpenedBooster.clear();
		for (MagicCard mc : list) {
			DisplayableCard c = createCard(mc);
			panelOpenedBooster.addComponent(c, sorter.getWeight(mc));
		}

	}

	private void refreshStats() {
		txtNumberLand.setText(String.valueOf(40 - deck.getAsList().size()));
		progressBar.setValue(deck.getAsList().size());
		progressBar.setString(deck.getAsList().size() + "/" + progressBar.getMaximum());
		if (analyseDeck) {
			cmcChartPanel.init(deck.getAsList());
			typeRepartitionPanel.init(deck.getAsList());
			manaRepartitionPanel.init(deck.getAsList());
		} else {
			cmcChartPanel.init(list);
			typeRepartitionPanel.init(list);
			manaRepartitionPanel.init(list);
		}

	}

	protected void save() {

		try {
			String name = JOptionPane.showInputDialog(
					MTGControler.getInstance().getLangService().getCapitalize("DECK_NAME") + " ?", deck.getName());
			deck.setName(name);
			deckManager.saveDeck(deck);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, ex, MTGControler.getInstance().getLangService().getError(),
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
