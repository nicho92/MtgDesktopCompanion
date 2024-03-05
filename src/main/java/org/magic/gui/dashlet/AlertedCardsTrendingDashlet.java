package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGAlert;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.components.charts.HistoryPricesPanel;
import org.magic.gui.models.CardAlertTableModel;
import org.magic.gui.renderer.MagicEditionsComboBoxCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;

public class AlertedCardsTrendingDashlet extends AbstractJDashlet {

	private static final long serialVersionUID = 1L;
	private CardAlertTableModel model;


	@Override
	public ImageIcon getDashletIcon() {
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
	public String getCategory() {
		return "Collection";
	}


	@Override
	public void initGUI() {
		var scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		model = new CardAlertTableModel();
		JXTable table = UITools.createNewTable(model);
		scrollPane.setViewportView(table);

		var historyPricesPanel = new HistoryPricesPanel(false);
		historyPricesPanel.setPreferredSize(new Dimension(119, 200));
		getContentPane().add(historyPricesPanel, BorderLayout.SOUTH);
		table.getColumnModel().getColumn(1).setCellRenderer(new MagicEditionsComboBoxCellRenderer(false));

		table.getSelectionModel().addListSelectionListener(event -> {
			if (!event.getValueIsAdjusting()) {
				ThreadManager.getInstance().invokeLater(new MTGRunnable() {

					@Override
					protected void auditedRun() {
						MTGAlert alt = UITools.getTableSelection(table,0);
						historyPricesPanel.init(alt.getCard(), alt.getCard().getEdition(),alt.getCard().toString());
						historyPricesPanel.revalidate();

					}
				}, " loading prices alerts");

			}
		});

		if (getProperties().size() > 0) {
			var r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));
			setBounds(r);
		}

		table.packAll();

	}

}