package org.magic.gui.dashlet;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.magic.api.interfaces.MTGExternalShop;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.components.charts.TransactionHistoryChartPanel;
import org.magic.services.MTGConstants;

public class TransactionHistoryDashlet extends AbstractJDashlet {

	private static final long serialVersionUID = 1L;
	private TransactionHistoryChartPanel chart;


	@Override
	public String getCategory() {
		return "Financial";
	}

	@Override
	public void initGUI() {
		getContentPane().setLayout(new BorderLayout(0, 0));

		var panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		chart = new TransactionHistoryChartPanel();


		getContentPane().add(chart,BorderLayout.CENTER);


		if (getProperties().size() > 0) {
			var r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));


			setBounds(r);
		}

	}

	@Override
	public void init() {
		try {
			chart.init(getEnabledPlugin(MTGExternalShop.class).listTransaction());
		}
		catch (Exception e) {
			logger.error(e);
		}

	}

	@Override
	public ImageIcon getDashletIcon() {
		return MTGConstants.ICON_SHOP;
	}

	@Override
	public String getName() {
		return "Transactions History";
	}


}
