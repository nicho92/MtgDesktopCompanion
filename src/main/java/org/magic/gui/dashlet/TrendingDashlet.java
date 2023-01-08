package org.magic.gui.dashlet;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingWorker;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MTGFormat;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.models.CardShakerTableModel;
import org.magic.gui.renderer.standard.DoubleCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;

public class TrendingDashlet extends AbstractJDashlet {

	private static final long serialVersionUID = 1L;
	private JXTable table;
	private CardShakerTableModel modStandard;
	private JComboBox<MTGFormat.FORMATS> cboFormats;
	private AbstractBuzyIndicatorComponent lblLoading;

	@Override
	public ImageIcon getDashletIcon() {
		return MTGConstants.ICON_EURO;
	}

	@Override
	public String getCategory() {
		return "Market";
	}

	@Override
	public void initGUI() {
		JButton btnRefresh;
		JPanel panel;
		var panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);

		cboFormats = UITools.createCombobox(MTGFormat.FORMATS.values());
		panneauHaut.add(cboFormats);

		lblLoading = AbstractBuzyIndicatorComponent.createLabelComponent();

		btnRefresh = new JButton("");
		btnRefresh.addActionListener(ae -> init());

		cboFormats.addItemListener(ie -> {
			if(ie.getStateChange()==ItemEvent.SELECTED)
				init();
		});


		btnRefresh.setIcon(MTGConstants.ICON_REFRESH);
		panneauHaut.add(btnRefresh);
		panneauHaut.add(lblLoading);



		modStandard = new CardShakerTableModel();
		table = UITools.createNewTable(modStandard);

		table.getColumnModel().getColumn(3).setCellRenderer(new DoubleCellEditorRenderer(true));
		table.getColumnModel().getColumn(4).setCellRenderer(new DoubleCellEditorRenderer(true,true));
		table.getColumnModel().getColumn(5).setCellRenderer(new DoubleCellEditorRenderer(true));
		table.getColumnModel().getColumn(6).setCellRenderer(new DoubleCellEditorRenderer(true,true));

		table.getColumnExt(modStandard.getColumnName(5)).setVisible(false);
		table.getColumnExt(modStandard.getColumnName(6)).setVisible(false);


		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);



		if (getProperties().size() > 0) {
			var r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));

			try {
				cboFormats.setSelectedItem(MTGFormat.FORMATS.valueOf(getString("FORMAT")));

			} catch (Exception e) {
				logger.error(e);
			}
			setBounds(r);
		}

		UITools.initTableFilter(table);
		UITools.initCardToolTipTable(table, 0, 1, 8,new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try {
					CardShake cs = UITools.getTableSelection(table, 0);
					UITools.browse(cs.getLink());

				}catch(Exception ex)
				{
					logger.error("error", ex);
				}
				return null;
			}
		});

		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
	}

	@Override
	public void init() {



		SwingWorker<List<CardShake>, CardShake> sw = new SwingWorker<>()
		{

			@Override
			protected List<CardShake> doInBackground() throws Exception {
				return getEnabledPlugin(MTGDashBoard.class).getShakerFor((MTGFormat.FORMATS) cboFormats.getSelectedItem());
			}

			@Override
			protected void done() {
				try {
					modStandard.init(get());
					table.setModel(modStandard);
				}
				catch(InterruptedException ex)
				{
					Thread.currentThread().interrupt();
				}
				catch (Exception e) {
					logger.error(e);
				}
				lblLoading.end();
				setProperty("FORMAT", ((MTGFormat.FORMATS) cboFormats.getSelectedItem()).toString());

				List<SortKey> keys = new ArrayList<>();
				var sortKey = new SortKey(3, SortOrder.DESCENDING);// column index 2
				keys.add(sortKey);
				try {
					table.setRowSorter(new TableRowSorter<>(modStandard));
					table.getRowSorter().setSortKeys(keys);
					((TableRowSorter) table.getRowSorter()).sort();
					modStandard.fireTableDataChanged();
					table.packAll();
				} catch (Exception e) {
					// do nothing
				}
			}

		};

		lblLoading.start();
		ThreadManager.getInstance().runInEdt(sw,"Init Formats Dashlet");

	}

	@Override
	public String getName() {
		return "Trendings";
	}

}