package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MTGFormat;
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
	private JComboBox<MTGFormat> cboFormats;
	private AbstractBuzyIndicatorComponent lblLoading;
	private JLabel lblInfoUpdate;

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_EURO;
	}

	public void initGUI() {
		JButton btnRefresh;
		JPanel panel;
		JPanel panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);

		cboFormats = UITools.createCombobox(MTGFormat.values());
		cboFormats.addItemListener(ie -> init());
		panneauHaut.add(cboFormats);

		lblLoading = AbstractBuzyIndicatorComponent.createLabelComponent();

		btnRefresh = new JButton("");
		btnRefresh.addActionListener(ae -> init());
		btnRefresh.setIcon(MTGConstants.ICON_REFRESH);
		panneauHaut.add(btnRefresh);
		panneauHaut.add(lblLoading);

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		modStandard = new CardShakerTableModel();
		table = new JXTable();

		scrollPane.setViewportView(table);

		if (getProperties().size() > 0) {
			Rectangle r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));

			try {
				cboFormats.setSelectedItem(MTGFormat.valueOf(getString("FORMAT")));

			} catch (Exception e) {
				logger.error(e);
			}
			setBounds(r);
		}

		UITools.initTableFilter(table);

		UITools.initCardToolTipTable(table, 0, 1);

		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);

		lblInfoUpdate = new JLabel("");
		panel.add(lblInfoUpdate);

		setVisible(true);

	}

	public void init() {
		ThreadManager.getInstance().execute(() -> {
			lblLoading.start();
			try {
				List<CardShake> l = MTGControler.getInstance().getEnabled(MTGDashBoard.class).getShakerFor((MTGFormat) cboFormats.getSelectedItem());
				modStandard.init(l);
				table.setModel(modStandard);
			} catch (Exception e) {
				logger.error(e);
			}
			setProperty("FORMAT", ((MTGFormat) cboFormats.getSelectedItem()).toString());
			lblLoading.end();
			table.getColumnModel().getColumn(3).setCellRenderer(new CardShakeRenderer());

			lblInfoUpdate.setText(MTGControler.getInstance().getEnabled(MTGDashBoard.class).getName() + "(updated : "
					+ new SimpleDateFormat("dd/MM/yyyy HH:mm")
							.format(MTGControler.getInstance().getEnabled(MTGDashBoard.class).getUpdatedDate())
					+ ")");

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
		}, "Init Formats Dashlet");
	}

	@Override
	public String getName() {
		return "Trendings";
	}

}
