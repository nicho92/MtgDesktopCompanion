package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGFormat;
import org.magic.gui.abstracts.AbstractJDashlet;
import org.magic.gui.models.CardDominanceTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;

public class BestCardsDashlet extends AbstractJDashlet {

	private JXTable table;
	private CardDominanceTableModel models;
	JComboBox<MTGFormat> cboFormat;
	JComboBox<String> cboFilter;
	private JLabel lblLoading;

	public BestCardsDashlet() {
		super();
		setFrameIcon(MTGConstants.ICON_UP);
	}

	@Override
	public String getName() {
		return "Most Played cards";
	}

	@Override
	public void initGUI() {
		JPanel panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);

		cboFormat = new JComboBox<>();
		cboFormat.setModel(new DefaultComboBoxModel<>(MTGFormat.values()));

		panneauHaut.add(cboFormat);

		cboFilter = new JComboBox<>();
		cboFilter.setModel(
				new DefaultComboBoxModel<>(MTGControler.getInstance().getEnabledDashBoard().getDominanceFilters()));
		panneauHaut.add(cboFilter);

		lblLoading = new JLabel("");
		lblLoading.setIcon(MTGConstants.ICON_LOADING);
		panneauHaut.add(lblLoading);

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		models = new CardDominanceTableModel();
		table = new JXTable(models);
		scrollPane.setViewportView(table);
		initToolTip(table, 0, null);

		cboFormat.addActionListener(ae -> init());

		cboFilter.addActionListener(ae -> init());

		if (getProperties().size() > 0) {
			Rectangle r = new Rectangle((int) Double.parseDouble(getProperty("x")),
					(int) Double.parseDouble(getProperty("y")), (int) Double.parseDouble(getProperty("w")),
					(int) Double.parseDouble(getProperty("h")));

			try {
				cboFormat.setSelectedItem(getProperty("FORMAT", "standard"));
				cboFilter.setSelectedItem(getProperty("FILTER", "all"));
			} catch (Exception e) {
				logger.error("can't get value", e);
			}
			setBounds(r);
		}
		setVisible(true);

	}

	@Override
	public void init() {
		ThreadManager.getInstance().execute(() -> {
			lblLoading.setVisible(true);
			models.init((MTGFormat) cboFormat.getSelectedItem(), cboFilter.getSelectedItem().toString());
			models.fireTableDataChanged();
			table.packAll();
			table.setRowSorter(new TableRowSorter(models));
			save("FORMAT", cboFormat.getSelectedItem().toString());
			save("FILTER", cboFilter.getSelectedItem().toString());
			lblLoading.setVisible(false);
		}, "init BestCardsDashlet");
	}

}
