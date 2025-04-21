package org.magic.gui.dashlet;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.components.charts.TransactionHistoryChartPanel;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;

import com.jogamp.newt.event.KeyEvent;

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
		chart = new TransactionHistoryChartPanel();
		var panel = new JPanel();
		var btnRefresh = UITools.createBindableJButton("", MTGConstants.ICON_REFRESH, KeyEvent.VK_R, "refresh");

		
		getContentPane().add(panel, BorderLayout.NORTH);
		getContentPane().add(chart,BorderLayout.CENTER);

		panel.add(btnRefresh);
		panel.add(buzy);
		
		

		if (getProperties().size() > 0) {
			var r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));


			setBounds(r);
		}
		
		btnRefresh.addActionListener(_->init());

	}

	@Override
	public void init() {
		buzy.start();
		var sw = new SwingWorker<List<Transaction>, Void>() {
			@Override
			protected List<Transaction> doInBackground() throws Exception {
				return getEnabledPlugin(MTGExternalShop.class).listTransaction();
			}
			@Override
			protected void done() {
				try {
					chart.init(get());
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					logger.error(e);
				}
				buzy.end();
			}
			
			
		};
		
		ThreadManager.getInstance().runInEdt(sw, "Loading "+getName());
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
