package org.magic.gui;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getEnabledPlugin;
import static org.magic.services.tools.MTG.getPlugin;
import static org.magic.services.tools.MTG.listEnabledPlugins;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGAlert;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGPrice;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsExport.MODS;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.extra.MTGPriceSuggester;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.card.MagicCardDetailPanel;
import org.magic.gui.components.charts.HistoryPricesPanel;
import org.magic.gui.components.dialog.importer.CardImporterDialog;
import org.magic.gui.components.prices.DeckPricePanel;
import org.magic.gui.components.prices.GroupedShoppingPanel;
import org.magic.gui.components.prices.PriceSuggesterComponent;
import org.magic.gui.components.prices.PricesTablePanel;
import org.magic.gui.components.renderer.MagicPricePanel;
import org.magic.gui.components.tech.ObjectViewerPanel;
import org.magic.gui.components.tech.ServerStatePanel;
import org.magic.gui.components.widgets.JExportButton;
import org.magic.gui.models.CardAlertTableModel;
import org.magic.gui.renderer.AlertedCardsRenderer;
import org.magic.gui.renderer.standard.DoubleCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;


public class AlarmGUI extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JXTable table;
	private CardAlertTableModel model;
	private MagicCardDetailPanel magicCardDetailPanel;
	private DefaultListModel<MTGPrice> resultListModel;
	private JList<MTGPrice> list;
	private JButton btnRefresh;
	private JButton btnDelete;
	private HistoryPricesPanel variationPanel;
	private JButton btnImport;
	private AbstractBuzyIndicatorComponent lblLoading;
	private File f;
	private PricesTablePanel pricesTablePanel;
	private JButton btnSuggestPrice;
	private JSplitPane splitPanel;
	private JExportButton btnExport;
	private DeckPricePanel globalSearchPanel;
	private GroupedShoppingPanel groupShopPanel;
	private ObjectViewerPanel jsonPanel;

	public AlarmGUI() {
		initGUI();
		initActions();
	}


	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_ALERT;
	}

	@Override
	public String getTitle() {
		return capitalize("ALERT_MODULE");
	}


	public void initGUI() {
		splitPanel = new JSplitPane();
		btnExport = new JExportButton(MODS.EXPORT);

		model = new CardAlertTableModel();
		globalSearchPanel = new DeckPricePanel();
		magicCardDetailPanel = new MagicCardDetailPanel(false);
		variationPanel = new HistoryPricesPanel(true);
		var panelRight = new JPanel();
		resultListModel = new DefaultListModel<>();
		groupShopPanel = new GroupedShoppingPanel();
		jsonPanel = new ObjectViewerPanel();
		list = new JList<>(resultListModel);
		var panel = new JPanel();
		btnRefresh = UITools.createBindableJButton(null, MTGConstants.ICON_REFRESH, KeyEvent.VK_R, "refresh Alarm");
		btnImport = UITools.createBindableJButton(null, MTGConstants.ICON_IMPORT, KeyEvent.VK_I, "import Alarm");
		btnDelete = UITools.createBindableJButton(null, MTGConstants.ICON_DELETE, KeyEvent.VK_D, "delete Alarm");
		btnSuggestPrice = UITools.createBindableJButton(null, MTGConstants.ICON_EURO, KeyEvent.VK_S, "suggestPrices Alarm");
		lblLoading = AbstractBuzyIndicatorComponent.createProgressComponent();
		var serversPanel = new JPanel();
		var oversightPanel = new ServerStatePanel(false,getPlugin("Alert Trend Server", MTGServer.class));
		var serverPricePanel = new ServerStatePanel(false,getPlugin("Alert Price Checker", MTGServer.class));
		table = UITools.createNewTable(model,true);
		pricesTablePanel = new PricesTablePanel();
		
///////CONFIG
		setLayout(new BorderLayout());
		splitPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		magicCardDetailPanel.enableThumbnail(true);
		list.setCellRenderer((JList<? extends MTGPrice> obj, MTGPrice value, int index, boolean isSelected,boolean cellHasFocus) -> new MagicPricePanel(value));
		table.getColumnModel().getColumn(5).setCellRenderer(new AlertedCardsRenderer());
		table.getColumnModel().getColumn(6).setCellRenderer(new DoubleCellEditorRenderer(true));
		table.getColumnModel().getColumn(7).setCellRenderer(new DoubleCellEditorRenderer(true));
		table.getColumnModel().getColumn(8).setCellRenderer(new DoubleCellEditorRenderer(true));

		btnSuggestPrice.setToolTipText(capitalize("SUGGEST_PRICE"));

		globalSearchPanel.enableControle(true);
		groupShopPanel.enableControle(true);
		panelRight.setLayout(new BorderLayout());

///////ADDS
		splitPanel.setLeftComponent(new JScrollPane(table));
		add(splitPanel, BorderLayout.CENTER);
		splitPanel.setRightComponent(getContextTabbedPane());

		serversPanel.setLayout(new GridLayout(2, 1, 0, 0));
		serversPanel.add(oversightPanel);
		serversPanel.add(serverPricePanel);
		panelRight.add(serversPanel,BorderLayout.SOUTH);
		
		addContextComponent(magicCardDetailPanel);
		addContextComponent(variationPanel);
		addContextComponent(globalSearchPanel);
		addContextComponent(groupShopPanel);
		addContextComponent(pricesTablePanel);
		
		if(MTG.readPropertyAsBoolean("debug-json-panel"))
			addContextComponent(jsonPanel);
		
		add(panelRight, BorderLayout.EAST);
		panelRight.add(new JScrollPane(list),BorderLayout.CENTER);
		add(panel, BorderLayout.NORTH);
		panel.add(btnDelete);
		panel.add(btnImport);
		panel.add(btnExport);
		panel.add(btnRefresh);
		panel.add(btnSuggestPrice);
		panel.add(lblLoading);

	}


	@Override
	public void onFirstShowing() {
		splitPanel.setDividerLocation(.5);
		loaddata();
	}


	private void loaddata() {
		var sw = new SwingWorker<List<MTGAlert>, Void>()
		{

				@Override
				protected List<MTGAlert> doInBackground() throws Exception {
					return getEnabledPlugin(MTGDao.class).listAlerts();
				}

				@Override
				protected void done() {

					try {
						model.bind(get());

					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					} catch (ExecutionException e) {
						MTGControler.getInstance().notify(e);
					}


				}
		};

		ThreadManager.getInstance().runInEdt(sw, "Loading alerts");

	}


	private void initActions() {

		groupShopPanel.getBtnCheckPrice().addActionListener(al->{
		List<MTGAlert> selectList = UITools.getTableSelections(table, 0);

			if(!selectList.isEmpty())
				groupShopPanel.initList(selectList);
			else
				groupShopPanel.initList(model.getItems());

		});


		globalSearchPanel.getBtnCheckPrice().addActionListener(al->{
			var tdek = new MTGDeck();
			model.getItems().forEach(e->tdek.getMain().put(e.getCard(),e.getQty()));
			globalSearchPanel.init(tdek);
		});


		btnExport.initAlertsExport(new Callable<List<MTGAlert>>() {
			@Override
			public List<MTGAlert> call() throws Exception {
				return model.getItems();
			}
		},lblLoading);


		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				resultListModel.removeAllElements();
				int viewRow = table.getSelectedRow();
				if (viewRow > -1) {

					MTGAlert selected = UITools.getTableSelection(table, 0);
					updateInfo(selected);
					table.setRowSelectionInterval(viewRow, viewRow);
					for (MTGPrice mp : selected.getOffers())
						resultListModel.addElement(mp);
				}
			}
		});

		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {

					if (e.getClickCount() == 2 && (list.getSelectedValue() != null)) {
						UITools.browse(list.getSelectedValue().getUrl());
					}
				} catch (Exception e1) {
					MTGControler.getInstance().notify(e1);
				}
			}
		});

		btnRefresh.addActionListener(al->loaddata());

		btnSuggestPrice.addActionListener(ae->{

			if(table.getSelectedRows().length<=0)
				return;

			var comp = new PriceSuggesterComponent();
			var jd = MTGUIComponent.createJDialog(comp, false, true);
			comp.getBtnValidate().addActionListener(l->jd.dispose());

			jd.setVisible(true);

			MTGPriceSuggester suggester = comp.getSelectedPlugin();
			
			
			lblLoading.start(table.getSelectedRows().length);
			
			var sw = new SwingWorker<Void, MTGAlert>()
					{
						@Override
						protected void done() {
							lblLoading.end();
							model.fireTableDataChanged();
						}

						@Override
						protected void process(List<MTGAlert> chunks) {
							lblLoading.progressSmooth(chunks.size());

							chunks.forEach(mca->mca.getOffers().forEach(resultListModel::addElement));


						}

						@Override
						protected Void doInBackground(){
							List<MTGAlert> alerts = UITools.getTableSelections(table,0);
							for (MTGAlert alert : alerts)
							{
								
								var price = suggester.getSuggestedPrice(alert.getCard(), alert.isFoil());
								  	alert.setPrice(price);
									try {
										getEnabledPlugin(MTGDao.class).updateAlert(alert);
									} catch (SQLException e) {
										logger.error("error updating {}",alert,e);
									}
								
								publish(alert);
							}
							return null;
						}

					};

					ThreadManager.getInstance().runInEdt(sw,"suggest prices");
		});


		btnDelete.addActionListener(event -> {
			int res = JOptionPane.showConfirmDialog(null,capitalize("CONFIRM_DELETE",table.getSelectedRows().length + " item(s)"),
					capitalize("DELETE") + " ?",JOptionPane.YES_NO_OPTION);

			if (res == JOptionPane.YES_OPTION)
			{
				int[] selected = table.getSelectedRows();
				lblLoading.start(selected.length);


				SwingWorker<List<MTGAlert>, MTGAlert> sw = new SwingWorker<>()
				{

					@Override
					protected List<MTGAlert> doInBackground() throws Exception {
						List<MTGAlert> alerts = UITools.getTableSelections(table,0);
						for (MTGAlert alert : alerts)
						{
							getEnabledPlugin(MTGDao.class).deleteAlert(alert);
							publish(alert);
						}
						return alerts;
					}

					@Override
					protected void done() {
						try {
							model.removeItem(get());
						}
						catch(InterruptedException ex)
						{
							Thread.currentThread().interrupt();
						}
						catch(Exception e)
						{
							MTGControler.getInstance().notify(e);
						}
						model.fireTableDataChanged();
						lblLoading.end();
					}

					@Override
					protected void process(List<MTGAlert> chunks) {
						lblLoading.progress(chunks.size());
					}
				};

				ThreadManager.getInstance().runInEdt(sw, "delete alerts");

			}
		});


		btnImport.addActionListener(ae -> {
			var menu = new JPopupMenu();

			var mnuImportSearch = new JMenuItem(capitalize("IMPORT_FROM", MTGControler.getInstance().getLangService().get("SEARCH_MODULE")));
			mnuImportSearch.setIcon(MTGConstants.ICON_SEARCH);

			mnuImportSearch.addActionListener(importAE -> {
				var cdSearch = new CardImporterDialog();
				cdSearch.setVisible(true);
				if (cdSearch.hasSelected()) {
					for (MTGCard mc : cdSearch.getSelectedItems())
						addCard(mc);
				}
			});
			menu.add(mnuImportSearch);

			for (final MTGCardsExport exp : listEnabledPlugins(MTGCardsExport.class)) {
				if (exp.getMods() == MODS.BOTH || exp.getMods() == MODS.IMPORT) {

					var it = new JMenuItem();
					it.setIcon(exp.getIcon());
					it.setText(exp.getName());
					it.addActionListener(itEvent -> {
						var jf = new JFileChooser(".");
						jf.setFileFilter(new FileFilter() {
							@Override
							public String getDescription() {
								return exp.getName();
							}

							@Override
							public boolean accept(File f) {
								return (f.isDirectory() || f.getName().endsWith(exp.getFileExtension()));
							}
						});
						int res = -1;
						f = new File("");

						if (!exp.needDialogForDeck(MODS.IMPORT)) {
							res = jf.showOpenDialog(null);
							f = jf.getSelectedFile();
						} else {

							try {
								exp.importDeckFromFile(null).getMain().keySet().forEach(this::addCard);
							} catch (IOException e1) {
								logger.error(e1);
							}

						}

						if (res == JFileChooser.APPROVE_OPTION)
						{

							AbstractObservableWorker<MTGDeck, MTGCard, MTGCardsExport> sw = new AbstractObservableWorker<>(lblLoading,exp) {

								@Override
								protected MTGDeck doInBackground() throws Exception {
									return plug.importDeckFromFile(f);
								}

								@Override
								protected void done() {
									super.done();

									if(getResult()!=null)
										for (MTGCard mc : getResult().getMain().keySet())
											addCard(mc);
								}
							};
							ThreadManager.getInstance().runInEdt(sw,"import alarms");
						}
					});

					UITools.buildCategorizedMenu(menu,it,exp);
				}
			}

			Component b = (Component) ae.getSource();
			var point = b.getLocationOnScreen();
			menu.show(b, 0, 0);
			menu.setLocation(point.x, point.y + b.getHeight());

		});


	}


	private void updateInfo(MTGAlert selected) {
		magicCardDetailPanel.init(selected.getCard());
		variationPanel.init(selected.getCard(), null, selected.getCard().getName());
		pricesTablePanel.init(selected.getCard(), selected.isFoil());
		jsonPanel.init(selected);
	}

	private void addCard(MTGCard mc) {
		var alert = new MTGAlert();
		alert.setCard(mc);
		alert.setPrice(1.0);
		try {
			getEnabledPlugin(MTGDao.class).saveAlert(alert);
		} catch (SQLException e) {
			logger.error(e);
		}
		model.fireTableDataChanged();

	}


}