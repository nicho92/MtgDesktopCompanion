package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.magic.api.beans.MagicCardAlert;
import org.magic.gui.abstracts.AbstractJDashlet;
import org.magic.gui.components.charts.HistoryPricesPanel;
import org.magic.gui.models.CardAlertTableModel;

public class AlertedCardsTrendingDashlet extends AbstractJDashlet{
	private JTable table;
	private CardAlertTableModel model;
	private HistoryPricesPanel historyPricesPanel;
	
	public AlertedCardsTrendingDashlet() {
		super();
		setFrameIcon(new ImageIcon(AlertedCardsTrendingDashlet.class.getResource("/bell.png")));
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
		table = new JTable(model);
		scrollPane.setViewportView(table);
		
		historyPricesPanel = new HistoryPricesPanel();
		historyPricesPanel.setMaximumSize(new Dimension(2147483647, 200));
		historyPricesPanel.setPreferredSize(new Dimension(119, 200));
		getContentPane().add(historyPricesPanel, BorderLayout.SOUTH);
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {
	        	
	        	if(!event.getValueIsAdjusting())
	        	{
	        		int row = table.getSelectedRow();
	        		MagicCardAlert alt =(MagicCardAlert)table.getValueAt(row,0);
	        		historyPricesPanel.init(alt.getCard(), alt.getCard().getEditions().get(0), alt.getCard().toString());
	        		//historyPricesPanel.zoom(new Date());
	        		historyPricesPanel.revalidate();
	        		
				}
		    }
		});
		
		if(props.size()>0) {
			Rectangle r = new Rectangle((int)Double.parseDouble(props.getProperty("x")), 
										(int)Double.parseDouble(props.getProperty("y")),
										(int)Double.parseDouble(props.getProperty("w")),
										(int)Double.parseDouble(props.getProperty("h")));
			setBounds(r);
			}
		
		setVisible(true);
		
		
	}


}
