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
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.abstracts.AbstractCardExport.MODS;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.gui.components.charts.HistoryPricesPanel;
import org.magic.gui.components.dialog.CardSearchImportDialog;
import org.magic.gui.components.renderer.MagicPricePanel;
import org.magic.gui.models.CardAlertTableModel;
import org.magic.gui.renderer.AlertedCardsRenderer;
import org.magic.servers.impl.PricesCheckerTimer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;

public class AlarmGUI extends JPanel {
	private JTable table;
	private CardAlertTableModel model;
	private MagicCardDetailPanel magicCardDetailPanel;
	private DefaultListModel<MagicPrice> resultListModel;
	private JList<MagicPrice> list;
	private JSplitPane splitPanel;
	private JPanel panel;
	private JButton btnRefresh;
	private JButton btnDelete;
	private HistoryPricesPanel variationPanel;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private JTabbedPane tabbedPane;
	private JButton btnImport;
	private JLabel lblLoading;
	private File f;

	public AlarmGUI() {

		logger.info("init Alarm GUI");
		setLayout(new BorderLayout());

		splitPanel = new JSplitPane();
		splitPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPanel, BorderLayout.CENTER);

		JScrollPane scrollTable = new JScrollPane();
		scrollTable.setPreferredSize(new Dimension(2, 200));
		splitPanel.setLeftComponent(scrollTable);
		table = new JTable();
		model = new CardAlertTableModel();
		table.setModel(model);

		lblLoading = new JLabel(MTGConstants.ICON_LOADING);
		lblLoading.setVisible(false);
		table.getColumnModel().getColumn(3).setCellRenderer(new AlertedCardsRenderer());

		scrollTable.setViewportView(table);

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				resultListModel.removeAllElements();
				MagicCardAlert selected = (MagicCardAlert) table.getValueAt(table.getSelectedRow(), 0);
				magicCardDetailPanel.setMagicCard(selected.getCard());
				variationPanel.init(selected.getCard(), null, selected.getCard().getName());
				for (MagicPrice mp : selected.getOffers())
					resultListModel.addElement(mp);
			}
		});

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPanel.setRightComponent(tabbedPane);

		magicCardDetailPanel = new MagicCardDetailPanel();
		variationPanel = new HistoryPricesPanel();

		tabbedPane.addTab("Card", null, magicCardDetailPanel, null);
		tabbedPane.addTab("Variations", null, variationPanel, null);

		magicCardDetailPanel.enableThumbnail(true);

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.EAST);

		resultListModel = new DefaultListModel<>();

		list = new JList<>(resultListModel);

		list.setCellRenderer((JList<? extends MagicPrice> obj, MagicPrice value, int index, boolean isSelected,
				boolean cellHasFocus) -> new MagicPricePanel(value));

		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {

					if (e.getClickCount() == 2 && (list.getSelectedValue() != null)) {
						MagicPrice p = list.getSelectedValue();
						Desktop.getDesktop().browse(new URI(p.getUrl()));
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1, MTGControler.getInstance().getLangService().getError(),
							JOptionPane.ERROR_MESSAGE);
				}

			}
		});
		scrollPane.setViewportView(list);

		panel = new JPanel();
		add(panel, BorderLayout.NORTH);

		btnRefresh = new JButton();
		btnRefresh.addActionListener(e -> {

			if (!MTGControler.getInstance().isRunning(new PricesCheckerTimer())) {
				int res = JOptionPane.showConfirmDialog(null,
						MTGControler.getInstance().getLangService().getCapitalize("PRICE_TIMER_LAUNCH"),
						MTGControler.getInstance().getLangService().getCapitalize("PRICE_TIMER_STOPPED"),
						JOptionPane.YES_NO_OPTION);

				if (res == JOptionPane.YES_OPTION)
					for (MTGServer serv : MTGControler.getInstance().getEnabledServers())
						if (serv.getName().equals(new PricesCheckerTimer().getName()))
							try {
								serv.start();
							} catch (Exception ex) {
								logger.error(ex);
							}
			}

			model.fireTableDataChanged();
		});

		btnImport = new JButton(MTGConstants.ICON_IMPORT);
		panel.add(btnImport);
		btnRefresh.setIcon(MTGConstants.ICON_REFRESH);
		panel.add(btnRefresh);

		btnDelete = new JButton(MTGConstants.ICON_DELETE);

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

		panel.add(btnDelete);
		panel.add(lblLoading);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent componentEvent) {
				splitPanel.setDividerLocation(.5);
				model.fireTableDataChanged();
				removeComponentListener(this);
			}

		});

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

	private void addCard(MagicCard mc) {
		MagicCardAlert alert = new MagicCardAlert();
		alert.setCard(mc);
		alert.setPrice(0.0);
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
