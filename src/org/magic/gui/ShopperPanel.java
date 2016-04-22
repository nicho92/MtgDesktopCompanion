package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.magic.gui.models.ShopItemTableModel;

public class ShopperPanel extends JPanel {
	private JTextField txtSearch;
	private JTable tableItemShop;
	
	
	public ShopperPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		
		JLabel lblSearch = new JLabel("search :");
		panel.add(lblSearch);
		
		txtSearch = new JTextField();
		panel.add(txtSearch);
		txtSearch.setColumns(10);
		
		JButton btnSearch = new JButton("search");
		
		panel.add(btnSearch);
		
		JScrollPane shopItemScrollPane = new JScrollPane();
		add(shopItemScrollPane, BorderLayout.CENTER);
		
		tableItemShop = new JTable(new ShopItemTableModel());
		tableItemShop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) {
				if(ev.getClickCount()==2 && !ev.isConsumed())
				{
					ev.consume();
					try {
						URL url = (URL)tableItemShop.getValueAt(tableItemShop.getSelectedRow(), 5);
						Desktop.getDesktop().browse(url.toURI());
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			}
		});
		shopItemScrollPane.setViewportView(tableItemShop);

		
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						((ShopItemTableModel)tableItemShop.getModel()).init(txtSearch.getText());
						((ShopItemTableModel)tableItemShop.getModel()).fireTableDataChanged();
					}
				}).start();
				
				
			}
		});
		
	}

}
