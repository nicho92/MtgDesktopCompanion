package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.magic.api.beans.MagicCardAlert;
import org.magic.gui.abstracts.AbstractJDashlet;
import org.magic.gui.components.charts.HistoryPricesPanel;
import org.magic.gui.models.CardAlertTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.ThreadManager;

public class AlertedCardsTrendingDashlet extends AbstractJDashlet {
	private CardAlertTableModel model;

	public AlertedCardsTrendingDashlet() {
		super();
		setFrameIcon(MTGConstants.ICON_ALERT);
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
		JTable table = new JTable(model);
		scrollPane.setViewportView(table);

		HistoryPricesPanel historyPricesPanel = new HistoryPricesPanel();
		historyPricesPanel.setMaximumSize(new Dimension(2147483647, 200));
		historyPricesPanel.setPreferredSize(new Dimension(119, 200));
		getContentPane().add(historyPricesPanel, BorderLayout.SOUTH);

		table.getSelectionModel().addListSelectionListener(event -> {

			if (!event.getValueIsAdjusting()) {
				ThreadManager.getInstance().execute(() -> {
					int row = table.getSelectedRow();
					MagicCardAlert alt = (MagicCardAlert) table.getValueAt(row, 0);
					historyPricesPanel.init(alt.getCard(), alt.getCard().getEditions().get(0),
							alt.getCard().toString());
					historyPricesPanel.revalidate();
				});

			}
		});

		if (getProperties().size() > 0) {
			Rectangle r = new Rectangle((int) Double.parseDouble(getProperty("x")),
					(int) Double.parseDouble(getProperty("y")), (int) Double.parseDouble(getProperty("w")),
					(int) Double.parseDouble(getProperty("h")));
			setBounds(r);
		}

		setVisible(true);

	}

}
