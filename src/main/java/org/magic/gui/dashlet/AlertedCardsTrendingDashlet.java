package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.components.charts.HistoryPricesPanel;
import org.magic.gui.models.CardAlertTableModel;
import org.magic.gui.renderer.CardShakeRenderer;
import org.magic.gui.renderer.MagicEditionsComboBoxCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.ThreadManager;

public class AlertedCardsTrendingDashlet extends AbstractJDashlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CardAlertTableModel model;

	
	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_ALERT;
	}
	

	@Override
	public String getName() {
		return "My Alerts";
	}

	@Override
	public void init() {
		model.fireTableDataChanged();
	}

	@Override
	public void initGUI() {
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		model = new CardAlertTableModel();
		JXTable table = new JXTable(model);
		scrollPane.setViewportView(table);

		HistoryPricesPanel historyPricesPanel = new HistoryPricesPanel(false);
		historyPricesPanel.setPreferredSize(new Dimension(119, 200));
		getContentPane().add(historyPricesPanel, BorderLayout.SOUTH);
		table.getColumnModel().getColumn(4).setCellRenderer(new CardShakeRenderer());
		table.getColumnModel().getColumn(5).setCellRenderer(new CardShakeRenderer());
		table.getColumnModel().getColumn(1).setCellRenderer(new MagicEditionsComboBoxCellRenderer(false));
		
		table.getSelectionModel().addListSelectionListener(event -> {
			if (!event.getValueIsAdjusting()) {
				ThreadManager.getInstance().runInEdt(() -> {
					int row = table.getSelectedRow();
					MagicCardAlert alt = (MagicCardAlert) table.getValueAt(row, 0);
					historyPricesPanel.init(alt.getCard(), alt.getCard().getCurrentSet(),alt.getCard().toString());
					historyPricesPanel.revalidate();
				}, "Loading history alert ");

			}
		});

		if (getProperties().size() > 0) {
			Rectangle r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));
			setBounds(r);
		}

		table.packAll();
	
	}

}
