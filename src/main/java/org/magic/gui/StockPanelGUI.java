package org.magic.gui;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getEnabledPlugin;
import static org.magic.services.tools.MTG.listEnabledPlugins;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
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
import javax.swing.SortOrder;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsExport.MODS;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGPriceSuggester;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.CardsDeckCheckerPanel;
import org.magic.gui.components.GedPanel;
import org.magic.gui.components.GradingEditorPane;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.gui.components.ObjectViewerPanel;
import org.magic.gui.components.PriceSuggesterComponent;
import org.magic.gui.components.PricesTablePanel;
import org.magic.gui.components.charts.HistoryPricesPanel;
import org.magic.gui.components.dialog.CardSearchImportDialog;
import org.magic.gui.components.widgets.JExportButton;
import org.magic.gui.models.CardStockTableModel;
import org.magic.gui.renderer.StockTableRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;

import com.google.common.collect.Lists;
public class StockPanelGUI extends MTGUIComponent {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JXTable table;
	private CardStockTableModel model;
	private JButton btnDelete;
	private JButton btnSave;
	private boolean multiselection = false;
	private MagicCardDetailPanel magicCardDetailPanel;
	private HistoryPricesPanel historyPricePanel;
	private PricesTablePanel pricePanel;
	private ObjectViewerPanel jsonPanel;
	private JButton btnReload;
	private AbstractBuzyIndicatorComponent lblLoading;
	private JPanel rightPanel;
	private JSpinner spinner;
	private JComboBox<String> cboLanguages;
	private JTextPane textPane;
	private JComboBox<Boolean> cboFoil;
	private JComboBox<Boolean> cboSigned;
	private JComboBox<Boolean> cboAltered;
	private JButton btnshowMassPanel;
	private JButton btnApplyModification;
	private CardsDeckCheckerPanel deckPanel;
	private GradingEditorPane gradePanel;
	private GedPanel<MagicCardStock> gedPanel;

	private static Boolean[] values = { null, true, false };
	private JComboBox<EnumCondition> cboQuality;
	private JButton btnImport;
	private JComboBox<MagicCollection> cboCollection;
	private JExportButton btnExport;
	private JButton btnGeneratePrice;
	private JLabel lblCount;

	private JComboBox<String> cboSelections;
	private String[] selections = new String[] { "", MTGControler.getInstance().getLangService().get("NEW"),MTGControler.getInstance().getLangService().get("UPDATED"),MTGControler.getInstance().getLangService().get("ALL") };
	private File fileImport;

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_STOCK;
	}

	@Override
	public String getTitle() {
		return capitalize("STOCK_MODULE");
	}



	public StockPanelGUI() {

		initGUI();

		btnSave.addActionListener(e ->{
			List<MagicCardStock> updates = model.getItems().stream().filter(MagicCardStock::isUpdated).toList();
			AbstractObservableWorker<Void, MagicCardStock,MTGDao> sw = new AbstractObservableWorker<>(lblLoading, getEnabledPlugin(MTGDao.class),updates.size())
			{
				@Override
				protected void done() {
					super.done();
					model.fireTableDataChanged();
				}

				@Override
				protected Void doInBackground(){
					for (MagicCardStock ms : updates)
					{
						try {
							plug.saveOrUpdateCardStock(ms);
							ms.setUpdated(false);
						} catch (Exception e1) {
							logger.error(e1);
						}
					}
					return null;
				}
			};

			ThreadManager.getInstance().runInEdt(sw,"Batch stock save");

		});

		table.getSelectionModel().addListSelectionListener(event -> {
			if (!multiselection && !event.getValueIsAdjusting()) {
				int viewRow = table.getSelectedRow();
				if (viewRow > -1) {
					MagicCardStock selectedStock = UITools.getTableSelection(table, 0);
					btnDelete.setEnabled(true);
					updatePanels(selectedStock);
				}
			}
		});

		btnDelete.addActionListener(event -> {
			int res = JOptionPane.showConfirmDialog(null,
					capitalize("CONFIRM_DELETE",table.getSelectedRows().length + " item(s)"),
					capitalize("DELETE") + " ?",JOptionPane.YES_NO_OPTION);

			if (res == JOptionPane.YES_OPTION) {

				List<MagicCardStock> stocks = UITools.getTableSelections(table, 0);
				model.removeItem(stocks);
				AbstractObservableWorker<Void, MagicCardStock, MTGDao> sw = new AbstractObservableWorker<>(lblLoading,getEnabledPlugin(MTGDao.class),stocks.size()) {
					@Override
					protected Void doInBackground(){
						stocks.removeIf(st->st.getId()==-1);
						if(!stocks.isEmpty())
						{
							try {
								plug.deleteStock(stocks);

							} catch (Exception e) {
								logger.error(e);
							}
						}

						return null;
					}

					@Override
					protected void process(List<MagicCardStock> chunks) {
						super.process(chunks);
						model.removeItem(chunks);
					}

					@Override
					protected void done() {
						super.done();
						model.fireTableDataChanged();
						updateCount();
					}
				};
				ThreadManager.getInstance().runInEdt(sw,"delete stocks");
			}
		});

		btnReload.addActionListener(event -> {
			int res = JOptionPane.showConfirmDialog(null,
					capitalize("CANCEL_CHANGES"),capitalize("CONFIRM_UNDO"),JOptionPane.YES_NO_OPTION);
			if (res == JOptionPane.YES_OPTION)
			{
				logger.debug("reload collection");
				AbstractObservableWorker<Void, MagicCardStock, MTGDao> sw = new AbstractObservableWorker<>(lblLoading, getEnabledPlugin(MTGDao.class), -1) {
					@Override
					protected Void doInBackground() throws Exception {
						model.init(plug.listStocks());
						return null;
					}
					@Override
					protected void done()
					{
						super.done();
						updateCount();
					}
				};
				ThreadManager.getInstance().runInEdt(sw, "reload stock");

			}

		});

		btnshowMassPanel.addActionListener(event -> rightPanel.setVisible(!rightPanel.isVisible()));

		btnImport.addActionListener(ae -> {
			var menu = new JPopupMenu();

			var mnuImportSearch = new JMenuItem(MTGControler.getInstance().getLangService()
					.getCapitalize("IMPORT_FROM", MTGControler.getInstance().getLangService().get("SEARCH_MODULE")));
			mnuImportSearch.setIcon(MTGConstants.ICON_SEARCH);

			mnuImportSearch.addActionListener(importAE -> {
				var cdSearch = new CardSearchImportDialog();
				cdSearch.setVisible(true);
				if (cdSearch.getSelection() != null) {
					for (MagicCard mc : cdSearch.getSelection())
						addCard(mc);
				}
			});
			menu.add(mnuImportSearch);

			for (final MTGCardsExport exp :listEnabledPlugins(MTGCardsExport.class)) {
				if (exp.getMods() == MODS.BOTH || exp.getMods() == MODS.IMPORT) {

					var it = new JMenuItem();
					it.setIcon(exp.getIcon());
					it.setText(exp.getName());
					it.addActionListener(itemEvent -> {
						var jf = new JFileChooser(".");
						jf.setFileHidingEnabled(false);
						jf.setFileFilter(new FileFilter() {

							@Override
							public String getDescription() {
								return exp.getName() +" ("+exp.getFileExtension()+")" ;
							}

							@Override
							public boolean accept(File f) {
								if (f.isDirectory())
									return true;
								return f.getName().endsWith(exp.getFileExtension());
							}
						});

						int res = -1;

						if (!exp.needDialogForStock(MODS.IMPORT) && exp.needFile()) {
							res = jf.showOpenDialog(null);
							fileImport = jf.getSelectedFile();
						}
						else if(!exp.needFile() && !exp.needDialogForStock(MODS.IMPORT))
						{
							logger.debug("{} need no file. Skip",exp);
							res = JFileChooser.APPROVE_OPTION;
						}
						else
						{
							try {
								res=-1;
								exp.importStockFromFile(null).forEach(this::addStock);
							} catch (IOException e1) {
								logger.error(e1);
							}
						}

						if (res == JFileChooser.APPROVE_OPTION)
						{

							int total = -1;
							if(fileImport!=null)
								total = FileTools.linesCount(fileImport);

							AbstractObservableWorker<List<MagicCardStock>, MagicCard, MTGCardsExport> sw = new AbstractObservableWorker<>(lblLoading,exp,total)
							{
								@Override
								protected List<MagicCardStock> doInBackground() throws Exception {
									return plug.importStockFromFile(fileImport);
								}

								@Override
								protected void notifyEnd() {
									MTGControler.getInstance().notify(new MTGNotification(
										MTGControler.getInstance().getLangService().combine("IMPORT", "FINISHED"),
										exp.getName() + " "+ capitalize("FINISHED"),
										MESSAGE_TYPE.INFO
									));
								}

								@Override
								protected void done() {
									super.done();
									if(getResult()!=null)
									{
										for (MagicCardStock mc : getResult()) {
											addStock(mc);
										}
										model.fireTableDataChanged();
										updateCount();
									}
								}
							};
							ThreadManager.getInstance().runInEdt(sw,"import stocks from " + fileImport);

						}
					});
					UITools.buildCategorizedMenu(menu,it,exp);
				}
			}

			Component b = (Component) ae.getSource();
			Point p = b.getLocationOnScreen();
			menu.show(b, 0, 0);
			menu.setLocation(p.x, p.y + b.getHeight());
		});


		btnExport.initStockExport(new Callable<List<MagicCardStock>>() {

			@Override
			public List<MagicCardStock> call() throws Exception {

				List<MagicCardStock> export = UITools.getTableSelections(table,0);

				if(export.isEmpty())
					return model.getItems();
				else
					return export;
			}
		}, lblLoading);




		btnGeneratePrice.addActionListener(ae -> {
			lblLoading.start(table.getSelectedRows().length);

			var comp = new PriceSuggesterComponent();
			var jd = MTGUIComponent.createJDialog(comp, false, true);
			comp.getBtnValidate().addActionListener(l->jd.dispose());

			jd.setVisible(true);

			MTGPriceSuggester suggester = comp.getSelectedPlugin();

			SwingWorker<Void,MagicCardStock> sw = new SwingWorker<>() {

				@Override
				public void done() {
					lblLoading.end();
				}

				@Override
				protected void process(List<MagicCardStock> chunks) {
					lblLoading.progressSmooth(chunks.size());
					model.fireTableDataChanged();
				}


				@Override
				protected Void doInBackground(){
					List<MagicCardStock> sts = UITools.getTableSelections(table,0);
					for (MagicCardStock s : sts)
					{
						try {

							Double price = suggester.getSuggestedPrice(s.getProduct(), s.isFoil());
							double old = s.getPrice();
							s.setPrice(price);
							if (old != s.getPrice())
								s.setUpdated(true);
						}
						catch (NullPointerException e) {
							logger.error("{} is not found : ",s.getProduct(),e);
						}

						publish(s);
					}
					return null;
				}


			};

			ThreadManager.getInstance().runInEdt(sw, "generate prices for stock");
		});

		cboSelections.addItemListener(ie -> {
			multiselection = true;
			if (String.valueOf(cboSelections.getSelectedItem()).equals(selections[1])) {
				table.clearSelection();

				for (var i = 0; i < table.getRowCount(); i++) {
					if (table.getValueAt(i, 0).toString().equals("-1")) {
						table.addRowSelectionInterval(i, i);
					}
				}

			} else if (String.valueOf(cboSelections.getSelectedItem()).equals(selections[2])) {
				table.clearSelection();

				for (var i = 0; i < table.getRowCount(); i++) {
					if (((MagicCardStock) table.getValueAt(i, 0)).isUpdated())
						table.addRowSelectionInterval(i, i);
				}
			}
			else if (String.valueOf(cboSelections.getSelectedItem()).equals(selections[3])) {
				table.clearSelection();
				table.addRowSelectionInterval(0, table.getRowCount()-1);
			}
			multiselection = false;
		});

		btnApplyModification.addActionListener(event -> {
			int res = JOptionPane.showConfirmDialog(null,
					capitalize("CHANGE_X_ITEMS",
							table.getSelectedRowCount()),
					capitalize("CONFIRMATION"),
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (res == JOptionPane.YES_OPTION) {

				List<MagicCardStock> list = UITools.getTableSelections(table,0);

				for (MagicCardStock  s : list) {
					s.setUpdated(true);
					if (((Integer) spinner.getValue()).intValue() > -1)
						s.setQte((Integer) spinner.getValue());
					if (!textPane.getText().equals(""))
						s.setComment(textPane.getText());
					if (cboAltered.getSelectedItem() != null)
						s.setAltered((Boolean) cboAltered.getSelectedItem());
					if (cboSigned.getSelectedItem() != null)
						s.setSigned((Boolean) cboSigned.getSelectedItem());
					if (cboFoil.getSelectedItem() != null)
						s.setFoil((Boolean) cboFoil.getSelectedItem());
					if (cboLanguages.getSelectedItem() != null)
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

	private void updatePanels(MagicCardStock selectedStock) {

		if(selectedStock!=null) {
		magicCardDetailPanel.init(selectedStock.getProduct());
		historyPricePanel.init(selectedStock.getProduct(), null, selectedStock.getProduct().getName());
		pricePanel.init(selectedStock.getProduct(), selectedStock.isFoil());
		jsonPanel.init(selectedStock);
		deckPanel.init(selectedStock.getProduct());
		gradePanel.setGrading(selectedStock.getGrade());
		gedPanel.init(MagicCardStock.class, selectedStock);
		}
	}

	public void addStock(MagicCardStock mcs) {
		mcs.setUpdated(true);
		model.addItem(mcs);
	}

	public void addCard(MagicCard mc) {
		MagicCardStock ms = MTGControler.getInstance().getDefaultStock();
		ms.setId(-1);
		ms.setUpdated(true);
		ms.setProduct(mc);
		model.addItem(ms);
		model.fireTableDataChanged();
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
		gradePanel = new GradingEditorPane();
		gedPanel = new GedPanel<>();
		setLayout(new BorderLayout(0, 0));
		deckPanel = new CardsDeckCheckerPanel();
		model = new CardStockTableModel();
		magicCardDetailPanel = new MagicCardDetailPanel();
		historyPricePanel = new HistoryPricesPanel(true);
		pricePanel = new PricesTablePanel();

		var centerPanel = new JPanel();
		add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));
		var actionPanel = new JPanel();
		centerPanel.add(actionPanel, BorderLayout.NORTH);
		btnDelete = UITools.createBindableJButton(null, MTGConstants.ICON_DELETE, KeyEvent.VK_D, "stock delete");
		btnDelete.setEnabled(false);
		actionPanel.add(btnDelete);

		btnSave = UITools.createBindableJButton(null, MTGConstants.ICON_SAVE, KeyEvent.VK_S, "stock save");
		btnSave.setToolTipText(capitalize("BATCH_SAVE"));
		actionPanel.add(btnSave);

		btnReload = UITools.createBindableJButton(null, MTGConstants.ICON_REFRESH, KeyEvent.VK_R, "stock reload");
		btnReload.setToolTipText(capitalize("RELOAD"));
		actionPanel.add(btnReload);

		lblLoading = AbstractBuzyIndicatorComponent.createProgressComponent();

		btnshowMassPanel = UITools.createBindableJButton(null, MTGConstants.ICON_MANUAL, KeyEvent.VK_M, "stock mass panel show");

		btnImport = UITools.createBindableJButton(null, MTGConstants.ICON_IMPORT, KeyEvent.VK_I, "stock import");
		btnImport.setToolTipText(capitalize("IMPORT"));
		actionPanel.add(btnImport);

		btnExport = new JExportButton(MODS.EXPORT);
		UITools.bindJButton(btnExport, KeyEvent.VK_E, "stock export");
		btnExport.setToolTipText(capitalize("EXPORT"));
		actionPanel.add(btnExport);

		btnGeneratePrice = UITools.createBindableJButton(null, MTGConstants.ICON_EURO, KeyEvent.VK_E, "stock price suggestion");
		jsonPanel = new ObjectViewerPanel();
		btnGeneratePrice.setToolTipText(capitalize("GENERATE_PRICE"));
		actionPanel.add(btnGeneratePrice);
		btnshowMassPanel.setToolTipText(capitalize("MASS_MODIFICATION"));

		btnApplyModification = UITools.createBindableJButton(capitalize("APPLY"), MTGConstants.ICON_CHECK, KeyEvent.VK_A, "stock apply");


		actionPanel.add(btnshowMassPanel);
		actionPanel.add(lblLoading);

		table = UITools.createNewTable(model);
		UITools.setDefaultRenderer(table, new StockTableRenderer());
		table.packAll();
		UITools.initTableFilter(table);
		UITools.sort(table,0,SortOrder.DESCENDING);




		magicCardDetailPanel.enableThumbnail(true);

		splitPane = new JSplitPane();
		splitPane.setDividerLocation(0.5);
		splitPane.setResizeWeight(0.5);

		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		centerPanel.add(splitPane, BorderLayout.CENTER);
		splitPane.setLeftComponent(new JScrollPane(table));

		splitPane.setRightComponent(getContextTabbedPane());


		addContextComponent(magicCardDetailPanel);
		addContextComponent(gradePanel);
		addContextComponent(pricePanel);
		addContextComponent(historyPricePanel);
		addContextComponent(deckPanel);
		addContextComponent(gedPanel);

		if(MTG.readPropertyAsBoolean("debug-json-panel"))
			addContextComponent(jsonPanel);


		rightPanel = new JPanel();
		rightPanel.setBackground(SystemColor.inactiveCaption);
		rightPanel.setVisible(false);
		add(rightPanel, BorderLayout.EAST);
		var gblrightPanel = new GridBagLayout();
		gblrightPanel.columnWidths = new int[] { 84, 103, 0 };
		gblrightPanel.rowHeights = new int[] { 83, 56, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gblrightPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gblrightPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		rightPanel.setLayout(gblrightPanel);

		lblSelect = new JLabel("Select :");
		var gbclblSelect = new GridBagConstraints();
		gbclblSelect.anchor = GridBagConstraints.NORTHEAST;
		gbclblSelect.insets = new Insets(0, 0, 5, 5);
		gbclblSelect.gridx = 0;
		gbclblSelect.gridy = 1;
		rightPanel.add(lblSelect, gbclblSelect);

		cboSelections = UITools.createCombobox(selections);
		var gbccomboBox = new GridBagConstraints();
		gbccomboBox.anchor = GridBagConstraints.NORTH;
		gbccomboBox.insets = new Insets(0, 0, 5, 0);
		gbccomboBox.fill = GridBagConstraints.HORIZONTAL;
		gbccomboBox.gridx = 1;
		gbccomboBox.gridy = 1;
		rightPanel.add(cboSelections, gbccomboBox);

		lblQte = new JLabel(capitalize("QTY") + " :");
		var gbclblQte = new GridBagConstraints();
		gbclblQte.anchor = GridBagConstraints.EAST;
		gbclblQte.insets = new Insets(0, 0, 5, 5);
		gbclblQte.gridx = 0;
		gbclblQte.gridy = 2;
		rightPanel.add(lblQte, gbclblQte);

		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(-1, -1, null, 1));
		var gbcspinner = new GridBagConstraints();
		gbcspinner.fill = GridBagConstraints.HORIZONTAL;
		gbcspinner.insets = new Insets(0, 0, 5, 0);
		gbcspinner.gridx = 1;
		gbcspinner.gridy = 2;
		rightPanel.add(spinner, gbcspinner);

		lblLanguage = new JLabel(capitalize("CARD_LANGUAGE") + " :");
		var gbclblLanguage = new GridBagConstraints();
		gbclblLanguage.anchor = GridBagConstraints.EAST;
		gbclblLanguage.insets = new Insets(0, 0, 5, 5);
		gbclblLanguage.gridx = 0;
		gbclblLanguage.gridy = 3;
		rightPanel.add(lblLanguage, gbclblLanguage);

		DefaultComboBoxModel<String> lModel = new DefaultComboBoxModel<>();
		lModel.addElement(null);
		for (String l : MTG.getEnabledPlugin(MTGCardsProvider.class).getLanguages())
			lModel.addElement(l);

		cboLanguages = new JComboBox<>(lModel);
		var gbccboLanguages = new GridBagConstraints();
		gbccboLanguages.insets = new Insets(0, 0, 5, 0);
		gbccboLanguages.fill = GridBagConstraints.HORIZONTAL;
		gbccboLanguages.gridx = 1;
		gbccboLanguages.gridy = 3;
		rightPanel.add(cboLanguages, gbccboLanguages);

		lblFoil = new JLabel(capitalize("FOIL") + " :");
		var gbclblFoil = new GridBagConstraints();
		gbclblFoil.anchor = GridBagConstraints.EAST;
		gbclblFoil.insets = new Insets(0, 0, 5, 5);
		gbclblFoil.gridx = 0;
		gbclblFoil.gridy = 4;
		rightPanel.add(lblFoil, gbclblFoil);

		cboFoil = UITools.createCombobox(values);
		var gbccboFoil = new GridBagConstraints();
		gbccboFoil.insets = new Insets(0, 0, 5, 0);
		gbccboFoil.fill = GridBagConstraints.HORIZONTAL;
		gbccboFoil.gridx = 1;
		gbccboFoil.gridy = 4;
		rightPanel.add(cboFoil, gbccboFoil);

		lblSigned = new JLabel(capitalize("SIGNED") + " :");
		var gbclblSigned = new GridBagConstraints();
		gbclblSigned.anchor = GridBagConstraints.EAST;
		gbclblSigned.insets = new Insets(0, 0, 5, 5);
		gbclblSigned.gridx = 0;
		gbclblSigned.gridy = 5;
		rightPanel.add(lblSigned, gbclblSigned);

		cboSigned = UITools.createCombobox(values);
		var gbccboSigned = new GridBagConstraints();
		gbccboSigned.insets = new Insets(0, 0, 5, 0);
		gbccboSigned.fill = GridBagConstraints.HORIZONTAL;
		gbccboSigned.gridx = 1;
		gbccboSigned.gridy = 5;
		rightPanel.add(cboSigned, gbccboSigned);

		lblAltered = new JLabel(capitalize("ALTERED") + " :");
		var gbclblAltered = new GridBagConstraints();
		gbclblAltered.anchor = GridBagConstraints.EAST;
		gbclblAltered.insets = new Insets(0, 0, 5, 5);
		gbclblAltered.gridx = 0;
		gbclblAltered.gridy = 6;
		rightPanel.add(lblAltered, gbclblAltered);

		cboAltered = UITools.createCombobox(values);
		var gbccboAltered = new GridBagConstraints();
		gbccboAltered.insets = new Insets(0, 0, 5, 0);
		gbccboAltered.fill = GridBagConstraints.HORIZONTAL;
		gbccboAltered.gridx = 1;
		gbccboAltered.gridy = 6;
		rightPanel.add(cboAltered, gbccboAltered);

		lblQuality = new JLabel(capitalize("QUALITY") + " :");
		var gbclblQuality = new GridBagConstraints();
		gbclblQuality.anchor = GridBagConstraints.EAST;
		gbclblQuality.insets = new Insets(0, 0, 5, 5);
		gbclblQuality.gridx = 0;
		gbclblQuality.gridy = 7;
		rightPanel.add(lblQuality, gbclblQuality);



		cboQuality = UITools.createCombobox(Lists.asList(null,EnumCondition.values()));
		var gbccboQuality = new GridBagConstraints();
		gbccboQuality.insets = new Insets(0, 0, 5, 0);
		gbccboQuality.fill = GridBagConstraints.HORIZONTAL;
		gbccboQuality.gridx = 1;
		gbccboQuality.gridy = 7;
		rightPanel.add(cboQuality, gbccboQuality);

		lblCollection = new JLabel(capitalize("COLLECTION") + " :");
		var gbclblCollection = new GridBagConstraints();
		gbclblCollection.anchor = GridBagConstraints.EAST;
		gbclblCollection.insets = new Insets(0, 0, 5, 5);
		gbclblCollection.gridx = 0;
		gbclblCollection.gridy = 8;
		rightPanel.add(lblCollection, gbclblCollection);

		cboCollection = UITools.createComboboxCollection();
		var gbccboCollection = new GridBagConstraints();
		gbccboCollection.insets = new Insets(0, 0, 5, 0);
		gbccboCollection.fill = GridBagConstraints.HORIZONTAL;
		gbccboCollection.gridx = 1;
		gbccboCollection.gridy = 8;
		rightPanel.add(cboCollection, gbccboCollection);

		lblComment = new JLabel("Comment :");
		var gbclblComment = new GridBagConstraints();
		gbclblComment.insets = new Insets(0, 0, 5, 5);
		gbclblComment.gridx = 0;
		gbclblComment.gridy = 9;
		rightPanel.add(lblComment, gbclblComment);

		textPane = new JTextPane();
		var gbctextPane = new GridBagConstraints();
		gbctextPane.insets = new Insets(0, 0, 5, 0);
		gbctextPane.gridwidth = 2;
		gbctextPane.gridheight = 3;
		gbctextPane.fill = GridBagConstraints.BOTH;
		gbctextPane.gridx = 0;
		gbctextPane.gridy = 10;
		rightPanel.add(textPane, gbctextPane);




		var gbcbtnApplyModification = new GridBagConstraints();
		gbcbtnApplyModification.gridwidth = 2;
		gbcbtnApplyModification.gridx = 0;
		gbcbtnApplyModification.gridy = 13;
		rightPanel.add(btnApplyModification, gbcbtnApplyModification);

		bottomPanel = new JPanel();
		add(bottomPanel, BorderLayout.SOUTH);

		lblCount = new JLabel();
		bottomPanel.add(lblCount);


		gradePanel.getBtnSave().addActionListener(al->{
			try{
				MagicCardStock st = UITools.getTableSelection(table, 0);
				gradePanel.saveTo(st);
				model.fireTableDataChanged();
			}
			catch(Exception e)
			{
				MTGControler.getInstance().notify(new MTGNotification("ERROR", "Choose a stock", MESSAGE_TYPE.ERROR));
			}
		});

	}

	@Override
	public void onFirstShowing() {
		lblLoading.start();


		var sw = new AbstractObservableWorker<List<MagicCardStock>, MagicCardStock, MTGDao>(lblLoading,getEnabledPlugin(MTGDao.class)) {

			@Override
			protected List<MagicCardStock> doInBackground() throws Exception {
				return plug.listStocks();
			}

			@Override
			protected void notifyEnd() {
				model.init(getResult());
			}


		};
		ThreadManager.getInstance().runInEdt(sw, "init stock");

	}


	public void updateCount() {
		lblCount.setText(capitalize("ITEMS_IN_STOCK") + ": "+ table.getRowCount());
	}

}
