package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGFormat;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.components.JBuzyLabel;
import org.magic.gui.models.CardDominanceTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;

public class BestCardsDashlet extends AbstractJDashlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JXTable table;
	private CardDominanceTableModel models;
	private JComboBox<MTGFormat> cboFormat;
	private JComboBox<String> cboFilter;
	private JBuzyLabel lblLoading;

	
	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_UP;
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
				new DefaultComboBoxModel<>(MTGControler.getInstance().getEnabled(MTGDashBoard.class).getDominanceFilters()));
		panneauHaut.add(cboFilter);

		lblLoading = new JBuzyLabel();
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
			Rectangle r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));

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
			lblLoading.buzy(true);
			models.init((MTGFormat) cboFormat.getSelectedItem(), cboFilter.getSelectedItem().toString());
			models.fireTableDataChanged();
			table.packAll();
			table.setRowSorter(new TableRowSorter(models));
			setProperty("FORMAT", cboFormat.getSelectedItem().toString());
			setProperty("FILTER", cboFilter.getSelectedItem().toString());
			lblLoading.buzy(false);
		}, "init BestCardsDashlet");
	}

}
