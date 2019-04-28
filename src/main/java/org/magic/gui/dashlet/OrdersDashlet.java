package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.components.charts.OrdersChartPanel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.UITools;

import com.google.common.collect.Lists;

public class OrdersDashlet extends AbstractJDashlet {
	
	private JCheckBox chkSumOrTotal;
	private JComboBox<String> cboProperty;
	private OrdersChartPanel chart;
	
	
	
	public OrdersDashlet() {
		
		initGUI();
		
	}

	@Override
	public void initGUI() {
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		
		cboProperty = UITools.createCombobox(Lists.newArrayList("source", "transactionDate","edition","typeTransaction","type"));
		panel.add(cboProperty);
		
		chkSumOrTotal = new JCheckBox("Count");
		panel.add(chkSumOrTotal);
		
		JButton btnRefresh = new JButton(MTGConstants.ICON_REFRESH);
		panel.add(btnRefresh);
		
		chart = new OrdersChartPanel();
		
		
		getContentPane().add(chart,BorderLayout.CENTER);
		
		
		btnRefresh.addActionListener(e->init());
		
		if (getProperties().size() > 0) {
			Rectangle r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));

			setBounds(r);
		}

	}

	@Override
	public void init() {
		chart.init(MTGControler.getInstance().getEnabled(MTGDao.class).listOrders(),cboProperty.getSelectedItem().toString(), chkSumOrTotal.isSelected());
	}

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_SHOP;
	}

	@Override
	public String getName() {
		return "Orders Analyse";
	}

	
}
