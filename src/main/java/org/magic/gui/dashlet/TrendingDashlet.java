package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.Icon;
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
import org.magic.api.beans.MagicFormat;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.models.CardShakerTableModel;
import org.magic.gui.renderer.CardShakeRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;
import org.magic.tools.UITools;

public class TrendingDashlet extends AbstractJDashlet {
	
	private static final long serialVersionUID = 1L;
	private JXTable table;
	private CardShakerTableModel modStandard;
	private JComboBox<MagicFormat.FORMATS> cboFormats;
	private AbstractBuzyIndicatorComponent lblLoading;

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_EURO;
	}

	public void initGUI() {
		JButton btnRefresh;
		JPanel panel;
		JPanel panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);

		cboFormats = UITools.createCombobox(MagicFormat.FORMATS.values());
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
		table = new JXTable();
		table.setModel(modStandard);
		table.getColumnExt(modStandard.getColumnName(5)).setVisible(false);
		table.getColumnExt(modStandard.getColumnName(6)).setVisible(false);
	
		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
		
		
		
		if (getProperties().size() > 0) {
			Rectangle r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));

			try {
				cboFormats.setSelectedItem(MagicFormat.FORMATS.valueOf(getString("FORMAT")));

			} catch (Exception e) {
				logger.error(e);
			}
			setBounds(r);
		}

		UITools.initTableFilter(table);
		UITools.initCardToolTipTable(table, 0, 1, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try {
					CardShake cs = UITools.getTableSelection(table, 0);
					Desktop.getDesktop().browse(new URI(cs.getLink()));
				
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

	public void init() {
		
		
		
		SwingWorker<List<CardShake>, CardShake> sw = new SwingWorker<>()
		{
			
			@Override
			protected List<CardShake> doInBackground() throws Exception {
				return MTGControler.getInstance().getEnabled(MTGDashBoard.class).getShakerFor((MagicFormat.FORMATS) cboFormats.getSelectedItem());
			}
			
			@Override
			protected void done() {
				try {
					modStandard.init(get());
					table.setModel(modStandard);
				} catch (Exception e) {
					logger.error(e);
				} 
				lblLoading.end();
				table.getColumnModel().getColumn(3).setCellRenderer(new CardShakeRenderer());
				setProperty("FORMAT", ((MagicFormat.FORMATS) cboFormats.getSelectedItem()).toString());

				List<SortKey> keys = new ArrayList<>();
				SortKey sortKey = new SortKey(3, SortOrder.DESCENDING);// column index 2
				keys.add(sortKey);
				try {
					table.setRowSorter(new TableRowSorter(modStandard));
					((TableRowSorter) table.getRowSorter()).setSortKeys(keys);
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
