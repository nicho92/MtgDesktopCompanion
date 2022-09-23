package org.magic.gui.dashlet;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.components.charts.OrderEntryHistory3DChartPanel;
import org.magic.services.MTGConstants;

public class OrderHistoryDashlet extends AbstractJDashlet {

	private static final long serialVersionUID = 1L;
	private OrderEntryHistory3DChartPanel chart;


	@Override
	public String getCategory() {
		return "Financial";
	}

	@Override
	public void initGUI() {
		getContentPane().setLayout(new BorderLayout(0, 0));

		var panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		chart = new OrderEntryHistory3DChartPanel();


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
		chart.init(getEnabledPlugin(MTGDao.class).listOrders());

	}

	@Override
	public ImageIcon getDashletIcon() {
		return MTGConstants.ICON_SHOP;
	}

	@Override
	public String getName() {
		return "Orders History";
	}


}
