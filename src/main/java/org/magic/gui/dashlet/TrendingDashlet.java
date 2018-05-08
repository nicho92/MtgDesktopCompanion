package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGFormat;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.models.CardsShakerTableModel;
import org.magic.gui.renderer.CardShakeRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

public class TrendingDashlet extends AbstractJDashlet {
	private JXTable table;
	private CardsShakerTableModel modStandard;
	private JComboBox<MTGFormat> cboFormats;
	private JLabel lblLoading;
	private JLabel lblInfoUpdate;

	public TrendingDashlet() {
		super();
		setFrameIcon(MTGConstants.ICON_EURO);
	}

	public void initGUI() {
		JButton btnRefresh;
		JPanel panel;
		JPanel panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);

		cboFormats = new JComboBox<>(new DefaultComboBoxModel<MTGFormat>(MTGFormat.values()));
		cboFormats.addItemListener(ie -> init());
		panneauHaut.add(cboFormats);

		lblLoading = new JLabel("");
		lblLoading.setIcon(MTGConstants.ICON_LOADING);
		lblLoading.setVisible(false);

		btnRefresh = new JButton("");
		btnRefresh.addActionListener(ae -> init());
		btnRefresh.setIcon(MTGConstants.ICON_REFRESH);
		panneauHaut.add(btnRefresh);
		panneauHaut.add(lblLoading);

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		modStandard = new CardsShakerTableModel();
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

		new TableFilterHeader(table, AutoChoices.ENABLED);

		initToolTip(table, 0, 1);

		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);

		lblInfoUpdate = new JLabel("");
		panel.add(lblInfoUpdate);

		setVisible(true);

	}

	public void init() {
		ThreadManager.getInstance().execute(() -> {
			lblLoading.setVisible(true);
			modStandard.init((MTGFormat) cboFormats.getSelectedItem());

			try {
				table.setModel(modStandard);
			} catch (Exception e) {
				logger.error(e);
			}
			setProperty("FORMAT", ((MTGFormat) cboFormats.getSelectedItem()).toString());
			lblLoading.setVisible(false);
			table.getColumnModel().getColumn(3).setCellRenderer(new CardShakeRenderer());

			lblInfoUpdate.setText(MTGControler.getInstance().getEnabledDashBoard().getName() + "(updated : "
					+ new SimpleDateFormat("dd/MM/yyyy HH:mm")
							.format(MTGControler.getInstance().getEnabledDashBoard().getUpdatedDate())
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
