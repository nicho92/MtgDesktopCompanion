package org.magic.gui;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getEnabledPlugin;
import static org.magic.services.tools.MTG.listEnabledPlugins;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.SystemColor;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.SortOrder;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import org.apache.logging.log4j.Level;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsExport.MODS;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.extra.MTGPriceSuggester;
import org.magic.api.sorters.NumberSorter;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.GedPanel;
import org.magic.gui.components.GradingEditorPane;
import org.magic.gui.components.card.MagicCardDetailPanel;
import org.magic.gui.components.charts.HistoryPricesPanel;
import org.magic.gui.components.deck.CardsDeckCheckerPanel;
import org.magic.gui.components.dialog.importer.CardImporterDialog;
import org.magic.gui.components.editor.LanguageComboBoxCellEditor;
import org.magic.gui.components.prices.PriceSuggesterComponent;
import org.magic.gui.components.prices.PricesTablePanel;
import org.magic.gui.components.shops.StockItemsSynchronizationPanel;
import org.magic.gui.components.tech.LoggerViewPanel;
import org.magic.gui.components.tech.ObjectViewerPanel;
import org.magic.gui.components.widgets.JExportButton;
import org.magic.gui.components.widgets.JLangLabel;
import org.magic.gui.models.CardStockTableModel;
import org.magic.gui.renderer.StockTableRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.BeanTools;
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
	private GedPanel<MTGCardStock> gedPanel;
	private StockItemsSynchronizationPanel syncPanel;
	private JCheckBox chkboxForceFoil;
	private static Boolean[] values = { null, true, false };
	private JComboBox<EnumCondition> cboQuality;
	private JButton btnImport;
	private JComboBox<MTGCollection> cboCollection;
	private JExportButton btnExport;
	private JButton btnGeneratePrice;
	private JButton btnMerge;
	private JLabel lblCount;
	
	private JComboBox<String> cboSelections;
	private String[] selections = new String[] { "", MTGControler.getInstance().getLangService().get("NEW"),MTGControler.getInstance().getLangService().get("UPDATED"),MTGControler.getInstance().getLangService().get("ALL") };
	private File fileImport;
	private JButton btnDuplicate;
	private JComboBox<Boolean> cboDigital;
	
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

		btnSave.addActionListener(_ ->{
			List<MTGCardStock> updates = model.getItems().stream().filter(MTGCardStock::isUpdated).toList();
			var sw = new AbstractObservableWorker<Void, MTGCardStock,MTGDao>(lblLoading, getEnabledPlugin(MTGDao.class),updates.size())
			{
				@Override
				protected void done() {
					super.done();
					model.fireTableDataChanged();
				}

				@Override
				protected Void doInBackground(){
					for (MTGCardStock ms : updates)
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
					MTGCardStock selectedStock = UITools.getTableSelection(table, 0);
					btnDelete.setEnabled(true);
					btnDuplicate.setEnabled(true);
					updatePanels(selectedStock);
					updateCount(UITools.getTableSelections(table, 0));
				}
				
				
				btnMerge.setEnabled(table.getSelectedRowCount()>1);
				
				
			}
			
			
		});
		
		
		btnMerge.addActionListener(_ ->{
			
			List<MTGCardStock> lines = UITools.getTableSelections(table, 0);
			
			if(lines.isEmpty())
				return;
			
			
			
			if(lines.stream().map(MTGCardStock::hashId).distinct().count()>1)
			{
				MTGControler.getInstance().notify(new MTGNotification("Error", "Stock items are not equals", MESSAGE_TYPE.ERROR));
				return;
			}
			
			
			var firstOne = lines.get(0);
				 firstOne.setQte(lines.stream().mapToInt(MTGCardStock::getQte).sum());
			
			lines.remove(0);


				var sw = new AbstractObservableWorker<Void, MTGCardStock,MTGDao>(lblLoading, getEnabledPlugin(MTGDao.class),lines.size())
						{

							@Override
							protected Void doInBackground() throws Exception {
								plug.deleteStock(lines);
								plug.saveOrUpdateCardStock(firstOne);
								return null;
							}

							@Override
							protected void notifyEnd() {
								model.removeItem(lines);
								model.fireTableDataChanged();
							}
						};
			 ThreadManager.getInstance().runInEdt(sw, "Merging " + lines.size() + " items");
			
		});
		
	
		
		btnDelete.addActionListener(_ -> {
			int res = JOptionPane.showConfirmDialog(null,
					capitalize("CONFIRM_DELETE",table.getSelectedRows().length + " item(s)"),
					capitalize("DELETE") + " ?",JOptionPane.YES_NO_OPTION);

			if (res == JOptionPane.YES_OPTION) {

				List<MTGCardStock> stocks = UITools.getTableSelections(table, 0);
				model.removeItem(stocks);
				var sw = new AbstractObservableWorker<Void, MTGCardStock, MTGDao>(lblLoading,getEnabledPlugin(MTGDao.class),stocks.size()) {
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
					protected void process(List<MTGCardStock> chunks) {
						super.process(chunks);
						model.removeItem(chunks);
					}

					@Override
					protected void done() {
						super.done();
						model.fireTableDataChanged();
						updateCount(null);
					}
				};
				ThreadManager.getInstance().runInEdt(sw,"delete stocks");
			}
		});

		btnReload.addActionListener(_ -> {
			int res = JOptionPane.showConfirmDialog(null, capitalize("CANCEL_CHANGES"),capitalize("CONFIRM_UNDO"),JOptionPane.YES_NO_OPTION);
			if (res == JOptionPane.YES_OPTION)
				ThreadManager.getInstance().runInEdt(newLoadWorker() , "reload stock");
		});

		btnshowMassPanel.addActionListener(_ -> rightPanel.setVisible(!rightPanel.isVisible()));

		btnImport.addActionListener(ae -> {
			var menu = new JPopupMenu();

			var mnuImportSearch = new JMenuItem(MTGControler.getInstance().getLangService()
					.getCapitalize("IMPORT_FROM", MTGControler.getInstance().getLangService().get("SEARCH_MODULE")));
			mnuImportSearch.setIcon(MTGConstants.ICON_SEARCH);

			mnuImportSearch.addActionListener(_ -> {
				var cdSearch = new CardImporterDialog();
				cdSearch.setVisible(true);
				if (cdSearch.hasSelected()) {
					for (MTGCard mc : cdSearch.getSelectedItems())
						addCard(mc);
				}
			});
			menu.add(mnuImportSearch);

			for (final MTGCardsExport exp :listEnabledPlugins(MTGCardsExport.class)) {
				if (exp.getMods() == MODS.BOTH || exp.getMods() == MODS.IMPORT) {

					var it = new JMenuItem();
					it.setIcon(exp.getIcon());
					it.setText(exp.getName());
					it.addActionListener(_ -> {
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

							AbstractObservableWorker<List<MTGCardStock>, MTGCard, MTGCardsExport> sw = new AbstractObservableWorker<>(lblLoading,exp,total)
							{
								@Override
								protected List<MTGCardStock> doInBackground() throws Exception {
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
										for (MTGCardStock mc : getResult()) {
											addStock(mc);
										}
										model.fireTableDataChanged();
										updateCount(null);
									}
								}
							};
							ThreadManager.getInstance().runInEdt(sw,"import stocks from " + fileImport);

						}
					});
					UITools.buildCategorizedMenu(menu,it,exp);
				}
			}

			var b = (Component) ae.getSource();
			var p = b.getLocationOnScreen();
			menu.show(b, 0, 0);
			menu.setLocation(p.x, p.y + b.getHeight());
		});


		btnExport.initStockExport(new Callable<List<MTGCardStock>>() {

			@Override
			public List<MTGCardStock> call() throws Exception {

				List<MTGCardStock> export = UITools.getTableSelections(table,0);

				if(export.isEmpty())
					return model.getItems();
				else
					return export;
			}
		}, lblLoading);

		
		btnDuplicate.addActionListener(_->{
			try {
				List<MTGCardStock> list = UITools.getTableSelections(table, 0);
				
				for(var mcs : list) {
					
					var mcs2 = BeanTools.cloneBean(mcs);
					mcs2.setId(-1);
					
					if(chkboxForceFoil.isSelected())
						mcs2.setFoil(true);
					
					mcs2.setUpdated(true);
					model.addItem(mcs2);
				}
				model.fireTableDataChanged();
				
				
				
			} catch (Exception e1) {
				logger.error(e1);
			} 
			
		});
		


		btnGeneratePrice.addActionListener(_ -> {
			lblLoading.start(table.getSelectedRows().length);

			var comp = new PriceSuggesterComponent();
			var jd = MTGUIComponent.createJDialog(comp, false, true);
			comp.getBtnValidate().addActionListener(_->jd.dispose());

			jd.setVisible(true);

			MTGPriceSuggester suggester = comp.getSelectedPlugin();

			SwingWorker<Void,MTGCardStock> sw = new SwingWorker<>() {

				@Override
				public void done() {
					lblLoading.end();
				}

				@Override
				protected void process(List<MTGCardStock> chunks) {
					lblLoading.progressSmooth(chunks.size());
					model.fireTableDataChanged();
				}


				@Override
				protected Void doInBackground(){
					List<MTGCardStock> sts = UITools.getTableSelections(table,0);
					for (MTGCardStock s : sts)
					{
						try {

							var price = suggester.getSuggestedPrice(s.getProduct(), s.isFoil());
							double old = s.getValue().doubleValue();
							s.setPrice(price);
							if (old != s.getValue().doubleValue())
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

		cboSelections.addItemListener(_ -> {
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
					if (((MTGCardStock) table.getValueAt(i, 0)).isUpdated())
						table.addRowSelectionInterval(i, i);
				}
			}
			else if (String.valueOf(cboSelections.getSelectedItem()).equals(selections[3])) {
				table.clearSelection();
				table.addRowSelectionInterval(0, table.getRowCount()-1);
			}
			multiselection = false;
		});

		btnApplyModification.addActionListener(_ -> {
			int res = JOptionPane.showConfirmDialog(null,
					capitalize("CHANGE_X_ITEMS",
							table.getSelectedRowCount()),
					capitalize("CONFIRMATION"),
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (res == JOptionPane.YES_OPTION) {

				List<MTGCardStock> list = UITools.getTableSelections(table,0);

				for (MTGCardStock  s : list) {
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
						s.setMagicCollection((MTGCollection) cboCollection.getSelectedItem());
					if(cboDigital.getSelectedItem()!=null)
						s.setDigital((Boolean) cboDigital.getSelectedItem());
				}
				model.fireTableDataChanged();
			}
		});

	}

	private void updatePanels(MTGCardStock selectedStock) {

		if(selectedStock!=null) {
			magicCardDetailPanel.init(selectedStock.getProduct());
			historyPricePanel.init(selectedStock.getProduct(), null, selectedStock.getProduct().getName());
			pricePanel.init(selectedStock.getProduct(), selectedStock.isFoil());
			jsonPanel.init(selectedStock);
			deckPanel.init(selectedStock.getProduct());
			gradePanel.setGrading(selectedStock.getGrade());
			gedPanel.init(MTGCardStock.class, selectedStock);
			syncPanel.init(selectedStock);
		}
	}

	public void addStock(MTGCardStock mcs) {
		
		mcs.setUpdated(true);
		model.addItem(mcs);
		model.fireTableDataChanged();
	}

	public void addCard(MTGCard mc) {
		MTGCardStock ms = MTGControler.getInstance().getDefaultStock();
		ms.setId(-1);
		ms.setUpdated(true);
		ms.setProduct(mc);
		model.addItem(ms);
		model.fireTableDataChanged();
	}


	private void initGUI() {

		JSplitPane splitPane;
		
		var bottomPanel = new JPanel();
		gradePanel = new GradingEditorPane();
		gedPanel = new GedPanel<>();
		setLayout(new BorderLayout(0, 0));
		deckPanel = new CardsDeckCheckerPanel();
		model = new CardStockTableModel();
		magicCardDetailPanel = new MagicCardDetailPanel(true);
		historyPricePanel = new HistoryPricesPanel(true);
		pricePanel = new PricesTablePanel();
		syncPanel = new StockItemsSynchronizationPanel();
		chkboxForceFoil = new JCheckBox("Force foil on duplication");
		var	importLogPanel = new LoggerViewPanel();
		
		
		importLogPanel.enabledAutoLoad();
		importLogPanel.setLevel(Level.ERROR);
		
		var centerPanel = new JPanel();
		add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));
		var actionPanel = new JPanel();
		centerPanel.add(actionPanel, BorderLayout.NORTH);
	
		btnDuplicate= UITools.createBindableJButton(null, MTGConstants.ICON_COPY, KeyEvent.VK_C, "duplicate ligne");
		btnDuplicate.setEnabled(false);
		btnDuplicate.setToolTipText(capitalize("DUPLICATE"));
		actionPanel.add(btnDuplicate);
	
		btnMerge= UITools.createBindableJButton(null, MTGConstants.ICON_MERGE, KeyEvent.VK_M, "merge items");
		btnMerge.setEnabled(false);
		actionPanel.add(btnMerge);
		
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
		btnshowMassPanel.setToolTipText(capitalize("MASS_MODIFICATION"));
		btnApplyModification = UITools.createBindableJButton(capitalize("APPLY"), MTGConstants.ICON_CHECK, KeyEvent.VK_A, "stock apply");

		actionPanel.add(btnGeneratePrice);
		actionPanel.add(btnshowMassPanel);
		actionPanel.add(lblLoading);

		table = UITools.createNewTable(model,true);
		table.getColumn(8).setCellEditor(new LanguageComboBoxCellEditor());
	
		UITools.initTableVisibility(table, model);
		UITools.setDefaultRenderer(table, new StockTableRenderer());
		UITools.sort(table,0,SortOrder.DESCENDING);
		UITools.setSorter(table,1,new NumberSorter());
	
		
		table.packAll();
		
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
		addContextComponent(syncPanel);
		addContextComponent(deckPanel);
		addContextComponent(gedPanel);
		addContextComponent(importLogPanel);
		
		if(MTG.readPropertyAsBoolean("debug-json-panel"))
			addContextComponent(jsonPanel);


		rightPanel = new JPanel();
		rightPanel.setBackground(SystemColor.inactiveCaption);
		rightPanel.setVisible(false);
		add(rightPanel, BorderLayout.EAST);
		
		var gblrightPanel = new GridBagLayout();
		gblrightPanel.columnWidths = new int[] { 84, 103, 0 };
		gblrightPanel.rowHeights = new int[] { 83, 56, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0 };
		gblrightPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gblrightPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,0.0,Double.MIN_VALUE };
		rightPanel.setLayout(gblrightPanel);

		rightPanel.add(new JLangLabel("SELECT",true), UITools.createGridBagConstraints(null, GridBagConstraints.EAST ,0, 1));
		rightPanel.add(new JLangLabel("QTY",true), UITools.createGridBagConstraints(null,GridBagConstraints.EAST, 0, 2));
		rightPanel.add(new JLangLabel("CARD_LANGUAGE",true), UITools.createGridBagConstraints(null, GridBagConstraints.EAST, 0, 3));
		rightPanel.add(new JLangLabel("FOIL",true), UITools.createGridBagConstraints(null, GridBagConstraints.EAST, 0, 4));
		rightPanel.add(new JLangLabel("SIGNED",true), UITools.createGridBagConstraints(null, GridBagConstraints.EAST, 0, 5));
		rightPanel.add(new JLangLabel("ALTERED",true), UITools.createGridBagConstraints(null, GridBagConstraints.EAST, 0, 6));
		rightPanel.add(new JLangLabel("DIGITAL",true), UITools.createGridBagConstraints(null, GridBagConstraints.EAST, 0, 7));
		rightPanel.add(new JLangLabel("QUALITY",true), UITools.createGridBagConstraints(null, GridBagConstraints.EAST, 0, 8));
		rightPanel.add(new JLangLabel("COLLECTION",true), UITools.createGridBagConstraints(null, GridBagConstraints.EAST, 0, 9));
		rightPanel.add(new JLangLabel("COMMENT",true), UITools.createGridBagConstraints(null, GridBagConstraints.EAST, 0, 10));
		
		
		
		
		cboSelections = UITools.createCombobox(selections);
		rightPanel.add(cboSelections, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 1));

		spinner = new JSpinner(new SpinnerNumberModel(-1, -1, null, 1));
		rightPanel.add(spinner, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 2));


		var lModel = new DefaultComboBoxModel<String>();
		lModel.addElement(null);
		
		MTG.getEnabledPlugin(MTGCardsProvider.class).getLanguages().forEach(lModel::addElement);

		cboLanguages = new JComboBox<>(lModel);
		rightPanel.add(cboLanguages, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 3));

		cboFoil = UITools.createCombobox(values);
		rightPanel.add(cboFoil, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 4));

		cboSigned = UITools.createCombobox(values);
		rightPanel.add(cboSigned, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 5));

		cboAltered = UITools.createCombobox(values);
		rightPanel.add(cboAltered, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 6));

		cboDigital = UITools.createCombobox(values);
		rightPanel.add(cboDigital, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 7));

		cboQuality = UITools.createCombobox(Lists.asList(null,EnumCondition.values()));
		rightPanel.add(cboQuality, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 8));

		cboCollection = UITools.createComboboxCollection();
		rightPanel.add(cboCollection, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 9));


		textPane = new JTextPane();
		rightPanel.add(textPane, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 0, 11,2,3));

		var gbcbtnApplyModification = new GridBagConstraints();
		gbcbtnApplyModification.gridwidth = 2;
		gbcbtnApplyModification.gridx = 0;
		gbcbtnApplyModification.gridy = 14;
		rightPanel.add(btnApplyModification, gbcbtnApplyModification);

		var gbcSep = new GridBagConstraints();
		gbcSep.gridwidth = 2;
		gbcSep.gridx = 0;
		gbcSep.gridy = 15;
		rightPanel.add(new JSeparator(), gbcSep);
		
		var gbcSepFoil = new GridBagConstraints();
		gbcSepFoil.gridwidth = 2;
		gbcSepFoil.gridx = 0;
		gbcSepFoil.gridy = 16;
		rightPanel.add(chkboxForceFoil, gbcSepFoil);
		
		
		add(bottomPanel, BorderLayout.SOUTH);

		lblCount = new JLabel();
		bottomPanel.add(lblCount);


		gradePanel.getBtnSave().addActionListener(_->{
			try{
				MTGCardStock st = UITools.getTableSelection(table, 0);
				gradePanel.saveTo(st);
				model.fireTableDataChanged();
			}
			catch(Exception e)
			{
				MTG.notifyError("Choose a stock");
			}
		});

	}

	@Override
	public void onFirstShowing() {
		lblLoading.start();
		ThreadManager.getInstance().runInEdt(newLoadWorker(), "init stock");

	}


	private SwingWorker<?, ?> newLoadWorker() {
		return new AbstractObservableWorker<List<MTGCardStock>, MTGCardStock, MTGDao>(lblLoading,getEnabledPlugin(MTGDao.class)) {

			@Override
			protected List<MTGCardStock> doInBackground() throws Exception {
				return plug.listStocks();
			}

			@Override
			protected void notifyEnd() {
				model.init(getResult());
				updateCount(null);
			}
		};
	}

	public void updateCount(List<MTGCardStock> selection) {
		if(selection==null)
			lblCount.setText(capitalize("ITEMS_IN_STOCK") + ": "+ model.getItems().stream().mapToLong(mcs->mcs.getQte()).sum() + "( distinct cards :"+model.getRowCount() +")  / " + UITools.formatDouble(model.getItems().stream().mapToDouble(mcs->mcs.getValue().doubleValue()*(mcs.getQte()==0?1:mcs.getQte())).sum()) + " " + MTGControler.getInstance().getCurrencyService().getCurrentCurrency());
		else
			lblCount.setText(capitalize("ITEMS_IN_STOCK") + ": "+ 
								   model.getItems().stream().mapToLong(mcs->mcs.getQte()).sum() + "( distinct cards :"+model.getRowCount() +")  / "+ 
								   UITools.formatDouble(model.getItems().stream().mapToDouble(mcs->mcs.getValue().doubleValue()*(mcs.getQte()==0?1:mcs.getQte())).sum()) + " " + MTGControler.getInstance().getCurrencyService().getCurrentCurrency()+
								   " selection= " + UITools.formatDouble(selection.stream().mapToDouble(mcs->mcs.getValue().doubleValue()*(mcs.getQte()==0?1:mcs.getQte())).sum()) + MTGControler.getInstance().getCurrencyService().getCurrentCurrency() 
					);
	}

}
