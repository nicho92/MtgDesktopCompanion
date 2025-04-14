package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.magic.api.beans.enums.EnumTransactionDirection;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.components.charts.MTGStockItemsChartPanel;
import org.magic.services.MTGConstants;
import org.magic.services.TransactionService;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;

import com.google.common.collect.Lists;

public class TransactionItemsDashlet extends AbstractJDashlet {

	private static final long serialVersionUID = 1L;
	private static final String PROPERTY = "PROPERTY";
	private JCheckBox chkSumOrTotal;
	private JCheckBox chkSell;
	private JCheckBox chkBuy;
	
	private JComboBox<String> cboProperty;
	private MTGStockItemsChartPanel chart;
	
	
	@Override
	public String getCategory() {
		return "Financial";
	}

	@Override
	public void initGUI() {
		getContentPane().setLayout(new BorderLayout(0, 0));

		var panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		cboProperty = UITools.createCombobox(Lists.newArrayList("condition","product.edition","product.typeProduct","language"));
		panel.add(cboProperty);

		chkSumOrTotal = new JCheckBox("Count");
		chkSell = new JCheckBox("SELL");
		chkBuy = new JCheckBox("BUY");
		
		panel.add(chkSumOrTotal);
		panel.add(chkSell);
		panel.add(chkBuy);
		
		panel.add(buzy);
		chart = new MTGStockItemsChartPanel(true) {
			private static final long serialVersionUID = 1L;
			@Override
			public String getTitle() {
				return "Items in transactions";
			}
			
		};
		

		getContentPane().add(chart,BorderLayout.CENTER);


		chkBuy.addItemListener(_->init());
		chkSumOrTotal.addItemListener(_->init());
		chkSell.addItemListener(_->init());


		
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
			chkSell.setSelected(getString("SELL").equals("true"));
			chkBuy.setSelected(getString("BUY").equals("true"));
			setBounds(r);
		}

	}

	@Override
	public void init() {
		setProperty(PROPERTY, String.valueOf(cboProperty.getSelectedItem()));
		setProperty("COUNT", String.valueOf(chkSumOrTotal.isSelected()));
		setProperty("SELL", String.valueOf(chkSell.isSelected()));
		setProperty("BUY", String.valueOf(chkBuy.isSelected()));
		
		buzy.start();
		var sw = new SwingWorker<List<MTGStockItem>, Void>() {

			@Override
			protected List<MTGStockItem> doInBackground() throws Exception {
				
				return TransactionService.listTransactions().stream().filter(t->{
					if(chkBuy.isSelected() && !chkSell.isSelected())
							return t.getTypeTransaction()==EnumTransactionDirection.BUY;
					else if (chkSell.isSelected() && !chkBuy.isSelected()) 
						return t.getTypeTransaction()==EnumTransactionDirection.SELL;
					else
						return true;
				}).flatMap(t -> t.getItems().stream()).toList();
			}
				
			@Override
			protected void done() {
				try {
					chart.init(get(),cboProperty.getSelectedItem().toString(), chkSumOrTotal.isSelected());
				}catch(InterruptedException inter)
				{
					Thread.currentThread().interrupt();
				}
				catch (Exception e) {
					logger.error(e);
				}
				finally {
					buzy.end();
				}
			}
			
		};
		
		ThreadManager.getInstance().runInEdt(sw, "Refresh " + getName());
		
		
	}

	@Override
	public ImageIcon getDashletIcon() {
		return MTGConstants.ICON_SHOP;
	}

	@Override
	public String getName() {
		return "Transactions Products Analyse";
	}


}
