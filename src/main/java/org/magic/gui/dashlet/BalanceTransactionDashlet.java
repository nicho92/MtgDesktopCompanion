package org.magic.gui.dashlet;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.components.charts.TransactionBalance3DChartPanel;
import org.magic.services.MTGConstants;

public class BalanceTransactionDashlet extends AbstractJDashlet {

	private static final long serialVersionUID = 1L;
	private TransactionBalance3DChartPanel chart;



	@Override
	public String getCategory() {
		return "Financial";
	}

	@Override
	public void initGUI() {
		getContentPane().setLayout(new BorderLayout(0, 0));

		var panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		chart = new TransactionBalance3DChartPanel(true);


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
			chart.init(getEnabledPlugin(MTGDao.class).listTransactions());
		} catch (SQLException e) {
			logger.error(e);
		}
	}

	@Override
	public ImageIcon getDashletIcon() {
		return MTGConstants.ICON_SHOP;
	}

	@Override
	public String getName() {
		return "Balance";
	}


}
