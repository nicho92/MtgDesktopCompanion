package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.abstracts.AbstractCardExport.MODS;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.gui.components.PricesTablePanel;
import org.magic.gui.components.ServerStatePanel;
import org.magic.gui.components.charts.HistoryPricesPanel;
import org.magic.gui.components.dialog.CardSearchImportDialog;
import org.magic.gui.components.renderer.MagicPricePanel;
import org.magic.gui.models.CardAlertTableModel;
import org.magic.gui.renderer.AlertedCardsRenderer;
import org.magic.gui.renderer.CardShakeRenderer;
import org.magic.servers.impl.AlertTrendServer;
import org.magic.servers.impl.PricesCheckerTimer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

import java.awt.GridLayout;

public class AlarmGUI extends JPanel {
	private JTable table;
	private CardAlertTableModel model;
	private MagicCardDetailPanel magicCardDetailPanel;
	private DefaultListModel<MagicPrice> resultListModel;
	private JList<MagicPrice> list;
	private JPanel panel;
	private JButton btnRefresh;
	private JButton btnDelete;
	private HistoryPricesPanel variationPanel;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private JTabbedPane tabbedPane;
	private JButton btnImport;
	private JLabel lblLoading;
	private File f;
	private PricesTablePanel pricesTablePanel;
	
	
	public AlarmGUI() {

		logger.info("init Alarm GUI");

///////INIT 
		lblLoading = new JLabel(MTGConstants.ICON_LOADING);
		JSplitPane splitPanel = new JSplitPane();
		JScrollPane scrollTable = new JScrollPane();
		table = new JTable();
		model = new CardAlertTableModel();
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		magicCardDetailPanel = new MagicCardDetailPanel();
		variationPanel = new HistoryPricesPanel();
		JScrollPane scrollListOffers = new JScrollPane();
		JPanel panelRight = new JPanel();
		resultListModel = new DefaultListModel<>();
		list = new JList<>(resultListModel);
		panel = new JPanel();
		btnRefresh = new JButton(MTGConstants.ICON_REFRESH);
		btnImport = new JButton(MTGConstants.ICON_IMPORT);
		btnDelete = new JButton(MTGConstants.ICON_DELETE);
		JPanel serversPanel = new JPanel();
		ServerStatePanel oversightPanel = new ServerStatePanel(MTGControler.getInstance().getServer(AlertTrendServer.class));
		ServerStatePanel serverPricePanel = new ServerStatePanel(MTGControler.getInstance().getServer(PricesCheckerTimer.class));
		new TableFilterHeader(table, AutoChoices.ENABLED);

		
///////CONFIG		
		setLayout(new BorderLayout());
		splitPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		scrollTable.setPreferredSize(new Dimension(2, 200));
		table.setModel(model);
		lblLoading.setVisible(false);
		table.getColumnModel().getColumn(3).setCellRenderer(new AlertedCardsRenderer());
		magicCardDetailPanel.enableThumbnail(true);
		list.setCellRenderer((JList<? extends MagicPrice> obj, MagicPrice value, int index, boolean isSelected,boolean cellHasFocus) -> new MagicPricePanel(value));
		table.getColumnModel().getColumn(4).setCellRenderer(new CardShakeRenderer());
		table.getColumnModel().getColumn(5).setCellRenderer(new CardShakeRenderer());
		table.getColumnModel().getColumn(6).setCellRenderer(new CardShakeRenderer());
		panelRight.setLayout(new BorderLayout());
	
///////ADDS	
		splitPanel.setLeftComponent(scrollTable);
		add(splitPanel, BorderLayout.CENTER);
		scrollTable.setViewportView(table);
		splitPanel.setRightComponent(tabbedPane);
		panelRight.add(scrollListOffers,BorderLayout.CENTER);
		serversPanel.setLayout(new GridLayout(2, 1, 0, 0));
		serversPanel.add(oversightPanel);
		serversPanel.add(serverPricePanel);
		
		panelRight.add(serversPanel,BorderLayout.SOUTH);
		
		
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("DETAILS"), MTGConstants.ICON_TAB_DETAILS, magicCardDetailPanel, null);
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("PRICE_VARIATIONS"), MTGConstants.ICON_TAB_VARIATIONS, variationPanel, null);
		pricesTablePanel = new PricesTablePanel();
		tabbedPane.addTab(MTGControler.getInstance().getLangService().getCapitalize("PRICES"), MTGConstants.ICON_TAB_PRICES, pricesTablePanel, null);
		add(panelRight, BorderLayout.EAST);
		scrollListOffers.setViewportView(list);
		add(panel, BorderLayout.NORTH);
		panel.add(btnDelete);
		panel.add(lblLoading);
		panel.add(btnImport);
		panel.add(btnRefresh);
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent componentEvent) {
				splitPanel.setDividerLocation(.5);
				model.fireTableDataChanged();
				removeComponentListener(this);
			}

		});
		initActions();
	}

	private void initActions() {
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				resultListModel.removeAllElements();
				
				int viewRow = table.getSelectedRow();
				if (viewRow > -1) {
					int modelRow = table.convertRowIndexToModel(viewRow);
					MagicCardAlert selected = (MagicCardAlert) table.getModel().getValueAt(modelRow, 0);
					updateInfo(selected);
					
					for (MagicPrice mp : selected.getOffers())
						resultListModel.addElement(mp);
				}
			}
		});
		
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {

					if (e.getClickCount() == 2 && (list.getSelectedValue() != null)) {
						Desktop.getDesktop().browse(new URI(list.getSelectedValue().getUrl()));
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1, MTGControler.getInstance().getLangService().getError(),
							JOptionPane.ERROR_MESSAGE);
				}

			}
		});
		
		btnRefresh.addActionListener(e -> model.fireTableDataChanged());
		
		
		btnDelete.addActionListener(event -> {
			int res = JOptionPane.showConfirmDialog(null,MTGControler.getInstance().getLangService().getCapitalize("CONFIRM_DELETE",table.getSelectedRows().length + " item(s)"),
					MTGControler.getInstance().getLangService().getCapitalize("DELETE") + " ?",JOptionPane.YES_NO_OPTION);
			
			if (res == JOptionPane.YES_OPTION) {
				ThreadManager.getInstance().execute(() -> {
					try {
						int[] selected = table.getSelectedRows();
						lblLoading.setVisible(true);
						List<MagicCardAlert> alerts = extract(selected);
						for (MagicCardAlert alert : alerts)
							MTGControler.getInstance().getEnabledDAO().deleteAlert(alert);

						model.fireTableDataChanged();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, e.getMessage(),
								MTGControler.getInstance().getLangService().getError(), JOptionPane.ERROR_MESSAGE);
						lblLoading.setVisible(false);
					}
					lblLoading.setVisible(false);

				}, "delete alerts");

			}
		});
		
		
		btnImport.addActionListener(ae -> {
			JPopupMenu menu = new JPopupMenu();

			JMenuItem mnuImportSearch = new JMenuItem(MTGControler.getInstance().getLangService().getCapitalize("IMPORT_FROM", MTGControler.getInstance().getLangService().get("SEARCH_MODULE")));
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
					it.addActionListener(itEvent -> {
						JFileChooser jf = new JFileChooser(".");
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

						if (!exp.needDialogGUI()) {
							res = jf.showOpenDialog(null);
							f = jf.getSelectedFile();
						} else {
							res = JFileChooser.APPROVE_OPTION;

						}

						if (res == JFileChooser.APPROVE_OPTION)
							ThreadManager.getInstance().execute(() -> {
								try {
									loading(true, MTGControler.getInstance().getLangService().get("LOADING_FILE",
											f.getName(), exp));
									MagicDeck deck = exp.importDeck(f);

									for (MagicCard mc : deck.getMap().keySet())
										addCard(mc);

									loading(false, "");
								} catch (Exception e) {
									logger.error("error import", e);
									loading(false, "");
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
			Point point = b.getLocationOnScreen();
			menu.show(b, 0, 0);
			menu.setLocation(point.x, point.y + b.getHeight());

		});
		
		
	}

	private void updateInfo(MagicCardAlert selected) {
		magicCardDetailPanel.setMagicCard(selected.getCard());
		variationPanel.init(selected.getCard(), null, selected.getCard().getName());
		pricesTablePanel.init(selected.getCard(), selected.getCard().getCurrentSet());
	}

	private void addCard(MagicCard mc) {
		MagicCardAlert alert = new MagicCardAlert();
		alert.setCard(mc);
		alert.setPrice(1.0);
		try {
			MTGControler.getInstance().getEnabledDAO().saveAlert(alert);
		} catch (SQLException e) {
			logger.error(e);
		}
		model.fireTableDataChanged();

	}

	private void loading(boolean b, String string) {
		lblLoading.setText(string);
		lblLoading.setVisible(b);

	}

	private List<MagicCardAlert> extract(int[] ids) {
		List<MagicCardAlert> select = new ArrayList<>();

		for (int l : ids) {
			select.add(((MagicCardAlert) table.getValueAt(l, 0)));
		}
		return select;

	}

}
