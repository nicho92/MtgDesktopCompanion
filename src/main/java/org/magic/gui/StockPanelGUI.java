package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.SystemColor;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.abstracts.AbstractCardExport.MODS;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.gui.components.dialog.CardSearchImportDialog;
import org.magic.gui.models.CardStockTableModel;
import org.magic.gui.renderer.EnumConditionEditor;
import org.magic.gui.renderer.IntegerCellEditor;
import org.magic.gui.renderer.MagicEditionListEditor;
import org.magic.gui.renderer.MagicEditionRenderer;
import org.magic.gui.renderer.StockTableRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

public class StockPanelGUI extends JPanel {
	private JXTable table;
	private CardStockTableModel model;
	private JButton btnDelete = new JButton();
	private JButton btnSave = new JButton();
	private boolean multiselection = false;

	private MagicCardDetailPanel magicCardDetailPanel;

	private JButton btnReload;

	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private JLabel lblLoading;
	private JPanel rightPanel;
	private JSpinner spinner;
	private JComboBox<String> cboLanguages;
	private JTextPane textPane;
	private JComboBox<Boolean> cboFoil;
	private JComboBox<Boolean> cboSigned;
	private JComboBox<Boolean> cboAltered;
	private JButton btnshowMassPanel;
	private JButton btnApplyModification;

	private static Boolean[] values = { null, true, false };
	private JComboBox<EnumCondition> cboQuality;
	private JButton btnImport;
	private JComboBox<MagicCollection> cboCollection;
	private JButton btnExport;
	private JButton btnGeneratePrice;
	private JLabel lblCount;

	private JComboBox<String> cboSelections;
	private String[] selections = new String[] { "", MTGControler.getInstance().getLangService().get("NEW"),
			MTGControler.getInstance().getLangService().get("UPDATED") };
	private File f;

	public StockPanelGUI() {
		logger.info("init StockManagment GUI");

		initGUI();

		btnSave.addActionListener(e ->

		ThreadManager.getInstance().execute(() -> {
			for (MagicCardStock ms : model.getList())
				if (ms.isUpdate())
					try {
						lblLoading.setVisible(true);
						MTGControler.getInstance().getEnabledDAO().saveOrUpdateStock(ms);
						ms.setUpdate(false);
						lblLoading.setVisible(false);
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(),
								MTGControler.getInstance().getLangService().getError() + " : " + ms,
								JOptionPane.ERROR_MESSAGE);
						lblLoading.setVisible(false);
					}

			model.fireTableDataChanged();
		}, "Batch stock save"));

		table.getSelectionModel().addListSelectionListener(event -> {

			if (!multiselection && !event.getValueIsAdjusting()) {
				int viewRow = table.getSelectedRow();
				if (viewRow > -1) {
					int modelRow = table.convertRowIndexToModel(viewRow);
					MagicCardStock selectedStock = (MagicCardStock) table.getModel().getValueAt(modelRow, 0);
					btnDelete.setEnabled(true);
					magicCardDetailPanel.setMagicCard(selectedStock.getMagicCard());
				}
			}
		});

		btnDelete.addActionListener(event -> {
			int res = JOptionPane.showConfirmDialog(null,
					MTGControler.getInstance().getLangService().getCapitalize("CONFIRM_DELETE",
							table.getSelectedRows().length + " item(s)"),
					MTGControler.getInstance().getLangService().getCapitalize("DELETE") + " ?",
					JOptionPane.YES_NO_OPTION);
			if (res == JOptionPane.YES_OPTION) {
				ThreadManager.getInstance().execute(() -> {
					try {
						int[] selected = table.getSelectedRows();
						lblLoading.setVisible(true);
						List<MagicCardStock> stocks = extract(selected);
						model.removeRows(stocks);
						updateCount();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, e.getMessage(),
								MTGControler.getInstance().getLangService().getError(), JOptionPane.ERROR_MESSAGE);
						lblLoading.setVisible(false);
					}
					lblLoading.setVisible(false);
					updateCount();

				}, "delete stock");

			}
		});

		btnReload.addActionListener(event -> {
			int res = JOptionPane.showConfirmDialog(null,
					MTGControler.getInstance().getLangService().getCapitalize("CANCEL_CHANGES"),
					MTGControler.getInstance().getLangService().getCapitalize("CONFIRM_UNDO"),
					JOptionPane.YES_NO_OPTION);
			if (res == JOptionPane.YES_OPTION) {
				logger.debug("reload collection");
				ThreadManager.getInstance().execute(() -> {
					try {
						lblLoading.setVisible(true);
						model.init();
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(),
								MTGControler.getInstance().getLangService().getError(), JOptionPane.ERROR_MESSAGE);
					}
					lblLoading.setVisible(false);
					updateCount();
				}, "reload stock");

			}

		});

		btnshowMassPanel.addActionListener(event -> rightPanel.setVisible(!rightPanel.isVisible()));

		btnImport.addActionListener(ae -> {
			JPopupMenu menu = new JPopupMenu();

			JMenuItem mnuImportSearch = new JMenuItem(MTGControler.getInstance().getLangService()
					.getCapitalize("IMPORT_FROM", MTGControler.getInstance().getLangService().get("SEARCH_MODULE")));
			mnuImportSearch.setIcon(MTGConstants.ICON_SEARCH);

			mnuImportSearch.addActionListener(importAE -> {
				CardSearchImportDialog cdSearch = new CardSearchImportDialog();
				cdSearch.setVisible(true);
				if (cdSearch.getSelection() != null) {
					for (MagicCard mc : cdSearch.getSelection())
						addCard(mc);
				}
			});
			menu.add(mnuImportSearch);

			for (final MTGCardsExport exp : MTGControler.getInstance().getEnabledDeckExports()) {
				if (exp.getMods() == MODS.BOTH || exp.getMods() == MODS.IMPORT) {

					JMenuItem it = new JMenuItem();
					it.setIcon(exp.getIcon());
					it.setText(exp.getName());
					it.addActionListener(itemEvent -> {
						JFileChooser jf = new JFileChooser(".");
						jf.setFileFilter(new FileFilter() {

							@Override
							public String getDescription() {
								return exp.getName();
							}

							@Override
							public boolean accept(File f) {
								if (f.isDirectory())
									return true;
								return f.getName().endsWith(exp.getFileExtension());
							}
						});

						int res = -1;
						f = new File("");

						if (!exp.needDialogGUI()) {
							res = jf.showOpenDialog(null);
							f = jf.getSelectedFile();
						} else {
							res = JFileChooser.APPROVE_OPTION;

						}

						if (res == JFileChooser.APPROVE_OPTION)
							ThreadManager.getInstance().execute(() -> {
								try {
									lblLoading.setVisible(true);
									List<MagicCardStock> list = exp.importStock(f);
									for (MagicCardStock mc : list) {
										addStock(mc);
									}
									model.fireTableDataChanged();
									updateCount();
									lblLoading.setVisible(false);
									JOptionPane.showMessageDialog(null,
											MTGControler.getInstance().getLangService().combine("IMPORT", "FINISHED"),
											exp.getName() + " "
													+ MTGControler.getInstance().getLangService()
															.getCapitalize("FINISHED"),
											JOptionPane.INFORMATION_MESSAGE);

								} catch (Exception e) {
									logger.error("ERROR", e);
									lblLoading.setVisible(false);
									JOptionPane.showMessageDialog(null, e,
											MTGControler.getInstance().getLangService().getError(),
											JOptionPane.ERROR_MESSAGE);
								}

							}, "import " + exp);
					});

					menu.add(it);
				}
			}

			Component b = (Component) ae.getSource();
			Point p = b.getLocationOnScreen();
			menu.show(b, 0, 0);
			menu.setLocation(p.x, p.y + b.getHeight());
		});

		btnExport.addActionListener(event -> {
			JPopupMenu menu = new JPopupMenu();

			for (final MTGCardsExport exp : MTGControler.getInstance().getEnabledDeckExports()) {
				if (exp.getMods() == MODS.BOTH || exp.getMods() == MODS.EXPORT) {

					JMenuItem it = new JMenuItem();
					it.setIcon(exp.getIcon());
					it.setText(exp.getName());
					it.addActionListener(itEvent -> {
						JFileChooser jf = new JFileChooser(".");
						jf.setFileFilter(new FileFilter() {

							@Override
							public String getDescription() {
								return exp.getName();
							}

							@Override
							public boolean accept(File f) {
								return (f.isDirectory() || (f.getName().endsWith(exp.getFileExtension())));
							}
						});
						int res = jf.showSaveDialog(null);
						final File f = jf.getSelectedFile();

						if (res == JFileChooser.APPROVE_OPTION)
							ThreadManager.getInstance().execute(() -> {
								try {
									lblLoading.setVisible(true);

									exp.exportStock(model.getList(), f);

									lblLoading.setVisible(false);
									JOptionPane.showMessageDialog(null,
											MTGControler.getInstance().getLangService().combine("EXPORT", "FINISHED"),
											exp.getName() + " "
													+ MTGControler.getInstance().getLangService()
															.getCapitalize("FINISHED"),
											JOptionPane.INFORMATION_MESSAGE);

								} catch (Exception e) {
									logger.error(e);
									lblLoading.setVisible(false);
									JOptionPane.showMessageDialog(null, e,
											MTGControler.getInstance().getLangService().getError(),
											JOptionPane.ERROR_MESSAGE);
								}
							}, "export " + exp);

					});
					menu.add(it);
				}
			}

			Component b = (Component) event.getSource();
			Point p = b.getLocationOnScreen();
			menu.show(b, 0, 0);
			menu.setLocation(p.x, p.y + b.getHeight());

		});

		btnGeneratePrice.addActionListener(ae -> ThreadManager.getInstance().execute(() -> {
			lblLoading.setVisible(true);
			for (int i : table.getSelectedRows()) {
				MagicCardStock s = (MagicCardStock) table.getModel().getValueAt(table.convertRowIndexToModel(i), 0);
				Collection<Double> prices;
				Double price = 0.0;
				try {
					prices = MTGControler.getInstance().getEnabledDashBoard().getPriceVariation(s.getMagicCard(), null)
							.values();
					if (!prices.isEmpty())
						price = (Double) prices.toArray()[prices.size() - 1];
					else
						price = 0.0;
				} catch (IOException e) {
					price = 0.0;
				}
				double old = s.getPrice();
				s.setPrice(price);
				if (old != s.getPrice())
					s.setUpdate(true);

				model.fireTableDataChanged();
			}
			lblLoading.setVisible(false);

		}, "generate prices for stock"));

		cboSelections.addItemListener(ie -> {
			multiselection = true;
			if (String.valueOf(cboSelections.getSelectedItem()).equals(selections[1])) {
				table.clearSelection();

				for (int i = 0; i < table.getRowCount(); i++) {

					if (table.getValueAt(i, 0).toString().equals("-1")) {
						table.addRowSelectionInterval(i, i);
					}
				}

			} else if (String.valueOf(cboSelections.getSelectedItem()).equals(selections[2])) {
				table.clearSelection();

				for (int i = 0; i < table.getRowCount(); i++) {
					if (((MagicCardStock) table.getValueAt(i, 0)).isUpdate())
						table.addRowSelectionInterval(i, i);
				}
			}
			multiselection = false;
		});

		btnApplyModification.addActionListener(event -> {
			int res = JOptionPane.showConfirmDialog(null,
					MTGControler.getInstance().getLangService().getCapitalize("CHANGE_X_ITEMS",
							table.getSelectedRowCount()),
					MTGControler.getInstance().getLangService().getCapitalize("CONFIRMATION"),
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (res == JOptionPane.YES_OPTION) {
				for (int i : table.getSelectedRows()) {
					MagicCardStock s = (MagicCardStock) table.getModel().getValueAt(table.convertRowIndexToModel(i), 0);
					s.setUpdate(true);
					if (((Integer) spinner.getValue()).intValue() > 0)
						s.setQte((Integer) spinner.getValue());
					if (!textPane.getText().equals(""))
						s.setComment(textPane.getText());
					if (cboAltered.getSelectedItem() != null)
						s.setAltered((Boolean) cboAltered.getSelectedItem());
					if (cboSigned.getSelectedItem() != null)
						s.setSigned((Boolean) cboSigned.getSelectedItem());
					if (cboFoil.getSelectedItem() != null)
						s.setFoil((Boolean) cboFoil.getSelectedItem());
					if (cboLanguages != null)
						s.setLanguage(String.valueOf(cboLanguages.getSelectedItem()));
					if (cboQuality.getSelectedItem() != null)
						s.setCondition((EnumCondition) cboQuality.getSelectedItem());
					if (cboCollection.getSelectedItem() != null)
						s.setMagicCollection((MagicCollection) cboCollection.getSelectedItem());

				}
				model.fireTableDataChanged();
			}
		});

	}

	public void addStock(MagicCardStock mcs) {
		mcs.setIdstock(-1);
		mcs.setUpdate(true);
		model.add(mcs);
	}

	public void addCard(MagicCard mc) {
		MagicCardStock ms = new MagicCardStock();
		ms.setIdstock(-1);
		ms.setUpdate(true);
		ms.setMagicCard(mc);
		model.add(ms);

	}

	private List<MagicCardStock> extract(int[] ids) {
		List<MagicCardStock> select = new ArrayList<>();

		for (int l : ids) {
			select.add(((MagicCardStock) table.getValueAt(l, 0)));
		}
		return select;

	}

	private void initGUI() {

		JLabel lblSelect;
		JPanel bottomPanel;
		JLabel lblCollection;
		JLabel lblQuality;
		JLabel lblFoil;
		JLabel lblSigned;
		JLabel lblAltered;
		JSplitPane splitPane;
		JLabel lblQte;
		JLabel lblLanguage;
		JLabel lblComment;

		setLayout(new BorderLayout(0, 0));

		model = new CardStockTableModel();
		magicCardDetailPanel = new MagicCardDetailPanel();

		JPanel centerPanel = new JPanel();
		add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));
		JPanel actionPanel = new JPanel();
		centerPanel.add(actionPanel, BorderLayout.NORTH);

		btnDelete.setEnabled(false);
		btnDelete.setIcon(MTGConstants.ICON_DELETE);
		actionPanel.add(btnDelete);

		btnSave.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("BATCH_SAVE"));
		btnSave.setIcon(MTGConstants.ICON_SAVE);
		actionPanel.add(btnSave);

		btnReload = new JButton("");
		btnReload.setIcon(MTGConstants.ICON_REFRESH);
		btnReload.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("RELOAD"));
		actionPanel.add(btnReload);

		lblLoading = new JLabel();
		lblLoading.setVisible(false);

		btnshowMassPanel = new JButton("");

		btnImport = new JButton();
		btnImport.setIcon(MTGConstants.ICON_IMPORT);
		btnImport.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("IMPORT"));
		actionPanel.add(btnImport);

		btnExport = new JButton("");

		btnExport.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("EXPORT"));
		btnExport.setIcon(MTGConstants.ICON_EXPORT);
		actionPanel.add(btnExport);

		btnGeneratePrice = new JButton();

		btnGeneratePrice.setIcon(MTGConstants.ICON_EURO);
		btnGeneratePrice.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("GENERATE_PRICE"));
		actionPanel.add(btnGeneratePrice);
		btnshowMassPanel.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("MASS_MODIFICATION"));
		btnshowMassPanel.setIcon(MTGConstants.ICON_MANUAL);
		actionPanel.add(btnshowMassPanel);

		lblLoading.setIcon(MTGConstants.ICON_LOADING);
		actionPanel.add(lblLoading);

		JScrollPane scrollTable = new JScrollPane();

		table = new JXTable(model);
		StockTableRenderer render = new StockTableRenderer();

		table.setDefaultRenderer(Object.class, render);
		table.setDefaultEditor(EnumCondition.class, new EnumConditionEditor());
		table.setDefaultEditor(Integer.class, new IntegerCellEditor());

		table.getColumnModel().getColumn(2).setCellEditor(new MagicEditionListEditor());
		table.getColumnModel().getColumn(2).setCellRenderer(new MagicEditionRenderer());

		table.packAll();
		new TableFilterHeader(table, AutoChoices.ENABLED);
		scrollTable.setViewportView(table);

		magicCardDetailPanel.enableThumbnail(true);

		splitPane = new JSplitPane();
		splitPane.setDividerLocation(0.5);
		splitPane.setResizeWeight(0.5);

		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		centerPanel.add(splitPane, BorderLayout.CENTER);
		splitPane.setLeftComponent(scrollTable);
		splitPane.setRightComponent(magicCardDetailPanel);

		rightPanel = new JPanel();
		rightPanel.setBackground(SystemColor.inactiveCaption);
		rightPanel.setVisible(false);
		add(rightPanel, BorderLayout.EAST);
		GridBagLayout gblrightPanel = new GridBagLayout();
		gblrightPanel.columnWidths = new int[] { 84, 103, 0 };
		gblrightPanel.rowHeights = new int[] { 83, 56, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gblrightPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gblrightPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		rightPanel.setLayout(gblrightPanel);

		lblSelect = new JLabel("Select :");
		GridBagConstraints gbclblSelect = new GridBagConstraints();
		gbclblSelect.anchor = GridBagConstraints.NORTHEAST;
		gbclblSelect.insets = new Insets(0, 0, 5, 5);
		gbclblSelect.gridx = 0;
		gbclblSelect.gridy = 1;
		rightPanel.add(lblSelect, gbclblSelect);

		cboSelections = new JComboBox<>();

		cboSelections.setModel(new DefaultComboBoxModel<String>(selections));
		GridBagConstraints gbccomboBox = new GridBagConstraints();
		gbccomboBox.anchor = GridBagConstraints.NORTH;
		gbccomboBox.insets = new Insets(0, 0, 5, 0);
		gbccomboBox.fill = GridBagConstraints.HORIZONTAL;
		gbccomboBox.gridx = 1;
		gbccomboBox.gridy = 1;
		rightPanel.add(cboSelections, gbccomboBox);

		lblQte = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("QTY") + " :");
		GridBagConstraints gbclblQte = new GridBagConstraints();
		gbclblQte.anchor = GridBagConstraints.EAST;
		gbclblQte.insets = new Insets(0, 0, 5, 5);
		gbclblQte.gridx = 0;
		gbclblQte.gridy = 2;
		rightPanel.add(lblQte, gbclblQte);

		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(0, 0, null, 1));
		GridBagConstraints gbcspinner = new GridBagConstraints();
		gbcspinner.fill = GridBagConstraints.HORIZONTAL;
		gbcspinner.insets = new Insets(0, 0, 5, 0);
		gbcspinner.gridx = 1;
		gbcspinner.gridy = 2;
		rightPanel.add(spinner, gbcspinner);

		lblLanguage = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CARD_LANGUAGE") + " :");
		GridBagConstraints gbclblLanguage = new GridBagConstraints();
		gbclblLanguage.anchor = GridBagConstraints.EAST;
		gbclblLanguage.insets = new Insets(0, 0, 5, 5);
		gbclblLanguage.gridx = 0;
		gbclblLanguage.gridy = 3;
		rightPanel.add(lblLanguage, gbclblLanguage);

		DefaultComboBoxModel<String> lModel = new DefaultComboBoxModel<>();
		lModel.addElement(null);
		for (Locale l : Locale.getAvailableLocales())
			lModel.addElement(l.getDisplayLanguage(Locale.US));

		cboLanguages = new JComboBox<>(lModel);
		GridBagConstraints gbccboLanguages = new GridBagConstraints();
		gbccboLanguages.insets = new Insets(0, 0, 5, 0);
		gbccboLanguages.fill = GridBagConstraints.HORIZONTAL;
		gbccboLanguages.gridx = 1;
		gbccboLanguages.gridy = 3;
		rightPanel.add(cboLanguages, gbccboLanguages);

		lblFoil = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("FOIL") + " :");
		GridBagConstraints gbclblFoil = new GridBagConstraints();
		gbclblFoil.anchor = GridBagConstraints.EAST;
		gbclblFoil.insets = new Insets(0, 0, 5, 5);
		gbclblFoil.gridx = 0;
		gbclblFoil.gridy = 4;
		rightPanel.add(lblFoil, gbclblFoil);

		cboFoil = new JComboBox<>(new DefaultComboBoxModel<Boolean>(values));
		GridBagConstraints gbccboFoil = new GridBagConstraints();
		gbccboFoil.insets = new Insets(0, 0, 5, 0);
		gbccboFoil.fill = GridBagConstraints.HORIZONTAL;
		gbccboFoil.gridx = 1;
		gbccboFoil.gridy = 4;
		rightPanel.add(cboFoil, gbccboFoil);

		lblSigned = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("SIGNED") + " :");
		GridBagConstraints gbclblSigned = new GridBagConstraints();
		gbclblSigned.anchor = GridBagConstraints.EAST;
		gbclblSigned.insets = new Insets(0, 0, 5, 5);
		gbclblSigned.gridx = 0;
		gbclblSigned.gridy = 5;
		rightPanel.add(lblSigned, gbclblSigned);

		cboSigned = new JComboBox<>(new DefaultComboBoxModel<Boolean>(values));
		GridBagConstraints gbccboSigned = new GridBagConstraints();
		gbccboSigned.insets = new Insets(0, 0, 5, 0);
		gbccboSigned.fill = GridBagConstraints.HORIZONTAL;
		gbccboSigned.gridx = 1;
		gbccboSigned.gridy = 5;
		rightPanel.add(cboSigned, gbccboSigned);

		lblAltered = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("ALTERED") + " :");
		GridBagConstraints gbclblAltered = new GridBagConstraints();
		gbclblAltered.anchor = GridBagConstraints.EAST;
		gbclblAltered.insets = new Insets(0, 0, 5, 5);
		gbclblAltered.gridx = 0;
		gbclblAltered.gridy = 6;
		rightPanel.add(lblAltered, gbclblAltered);

		cboAltered = new JComboBox<>(new DefaultComboBoxModel<Boolean>(values));
		GridBagConstraints gbccboAltered = new GridBagConstraints();
		gbccboAltered.insets = new Insets(0, 0, 5, 0);
		gbccboAltered.fill = GridBagConstraints.HORIZONTAL;
		gbccboAltered.gridx = 1;
		gbccboAltered.gridy = 6;
		rightPanel.add(cboAltered, gbccboAltered);

		lblQuality = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("QUALITY") + " :");
		GridBagConstraints gbclblQuality = new GridBagConstraints();
		gbclblQuality.anchor = GridBagConstraints.EAST;
		gbclblQuality.insets = new Insets(0, 0, 5, 5);
		gbclblQuality.gridx = 0;
		gbclblQuality.gridy = 7;
		rightPanel.add(lblQuality, gbclblQuality);

		DefaultComboBoxModel<EnumCondition> qModel = new DefaultComboBoxModel<>();
		qModel.addElement(null);
		for (EnumCondition l : EnumCondition.values())
			qModel.addElement(l);

		cboQuality = new JComboBox<>(qModel);

		GridBagConstraints gbccboQuality = new GridBagConstraints();
		gbccboQuality.insets = new Insets(0, 0, 5, 0);
		gbccboQuality.fill = GridBagConstraints.HORIZONTAL;
		gbccboQuality.gridx = 1;
		gbccboQuality.gridy = 7;
		rightPanel.add(cboQuality, gbccboQuality);

		lblCollection = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("COLLECTION") + " :");
		GridBagConstraints gbclblCollection = new GridBagConstraints();
		gbclblCollection.anchor = GridBagConstraints.EAST;
		gbclblCollection.insets = new Insets(0, 0, 5, 5);
		gbclblCollection.gridx = 0;
		gbclblCollection.gridy = 8;
		rightPanel.add(lblCollection, gbclblCollection);

		DefaultComboBoxModel<MagicCollection> cModel = new DefaultComboBoxModel<>();
		cModel.addElement(null);
		try {
			for (MagicCollection l : MTGControler.getInstance().getEnabledDAO().getCollections())
				cModel.addElement(l);
		} catch (SQLException e1) {
			logger.error(e1);
		}

		cboCollection = new JComboBox<>(cModel);
		GridBagConstraints gbccboCollection = new GridBagConstraints();
		gbccboCollection.insets = new Insets(0, 0, 5, 0);
		gbccboCollection.fill = GridBagConstraints.HORIZONTAL;
		gbccboCollection.gridx = 1;
		gbccboCollection.gridy = 8;
		rightPanel.add(cboCollection, gbccboCollection);

		lblComment = new JLabel("Comment :");
		GridBagConstraints gbclblComment = new GridBagConstraints();
		gbclblComment.insets = new Insets(0, 0, 5, 5);
		gbclblComment.gridx = 0;
		gbclblComment.gridy = 9;
		rightPanel.add(lblComment, gbclblComment);

		textPane = new JTextPane();
		GridBagConstraints gbctextPane = new GridBagConstraints();
		gbctextPane.insets = new Insets(0, 0, 5, 0);
		gbctextPane.gridwidth = 2;
		gbctextPane.gridheight = 3;
		gbctextPane.fill = GridBagConstraints.BOTH;
		gbctextPane.gridx = 0;
		gbctextPane.gridy = 10;
		rightPanel.add(textPane, gbctextPane);

		btnApplyModification = new JButton(MTGControler.getInstance().getLangService().getCapitalize("APPLY"));

		GridBagConstraints gbcbtnApplyModification = new GridBagConstraints();
		gbcbtnApplyModification.gridwidth = 2;
		gbcbtnApplyModification.gridx = 0;
		gbcbtnApplyModification.gridy = 13;
		rightPanel.add(btnApplyModification, gbcbtnApplyModification);

		bottomPanel = new JPanel();
		add(bottomPanel, BorderLayout.SOUTH);

		lblCount = new JLabel();
		bottomPanel.add(lblCount);

		ThreadManager.getInstance().execute(() -> {
			try {
				lblLoading.setVisible(true);
				model.init();
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(),
						MTGControler.getInstance().getLangService().getError(), JOptionPane.ERROR_MESSAGE);
			}
			lblLoading.setVisible(false);
			updateCount();

		}, "init stock");

	}

	public void updateCount() {
		lblCount.setText(MTGControler.getInstance().getLangService().getCapitalize("ITEMS_IN_STOCK") + ": "
				+ table.getRowCount());
	}

}
