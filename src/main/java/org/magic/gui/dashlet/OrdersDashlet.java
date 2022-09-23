package org.magic.gui.dashlet;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.components.charts.OrdersChartPanel;
import org.magic.services.MTGConstants;
import org.magic.tools.UITools;

import com.google.common.collect.Lists;

public class OrdersDashlet extends AbstractJDashlet {

	private static final long serialVersionUID = 1L;
	private static final String PROPERTY = "PROPERTY";
	private JCheckBox chkSumOrTotal;
	private JComboBox<String> cboProperty;
	private OrdersChartPanel chart;


	@Override
	public String getCategory() {
		return "Financial";
	}


	@Override
	public void initGUI() {
		getContentPane().setLayout(new BorderLayout(0, 0));

		var panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		cboProperty = UITools.createCombobox(Lists.newArrayList("source","edition","typeTransaction","type"));
		panel.add(cboProperty);

		chkSumOrTotal = new JCheckBox("Count");
		panel.add(chkSumOrTotal);

		chart = new OrdersChartPanel(true);


		getContentPane().add(chart,BorderLayout.CENTER);


		chkSumOrTotal.addChangeListener(e->init());
		cboProperty.addItemListener(ie -> {
			if(ie.getStateChange()==ItemEvent.SELECTED)
				init();
		});


		if (getProperties().size() > 0) {
			var r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));

			if(!getString(PROPERTY).isEmpty())
				cboProperty.setSelectedItem(getString(PROPERTY));


			chkSumOrTotal.setSelected(getString("COUNT").equals("true"));

			setBounds(r);
		}

	}

	@Override
	public void init() {
		setProperty(PROPERTY, String.valueOf(cboProperty.getSelectedItem()));
		setProperty("COUNT", String.valueOf(chkSumOrTotal.isSelected()));
		chart.init(getEnabledPlugin(MTGDao.class).listOrders(),cboProperty.getSelectedItem().toString(), chkSumOrTotal.isSelected());
	}

	@Override
	public ImageIcon getDashletIcon() {
		return MTGConstants.ICON_SHOP;
	}

	@Override
	public String getName() {
		return "Orders Analyse";
	}


}
