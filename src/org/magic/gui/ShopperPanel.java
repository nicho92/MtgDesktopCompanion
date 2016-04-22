package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.DefaultRowSorter;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.magic.gui.models.ShopItemTableModel;

public class ShopperPanel extends JPanel {
	private JTextField txtSearch;
	private JTable tableItemShop;
	
	JButton btnSearch = new JButton("search");
	JPanel panel = new JPanel();
	JLabel lblSearch = new JLabel("search :");
	JScrollPane shopItemScrollPane = new JScrollPane();
	ShopItemTableModel mod;
	
	public ShopperPanel() {
		setLayout(new BorderLayout(0, 0));
		
		
		add(panel, BorderLayout.NORTH);
		
		panel.add(lblSearch);
		
		txtSearch = new JTextField();
		panel.add(txtSearch);
		txtSearch.setColumns(35);
		txtSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSearch.doClick();

			}
		});
		
		
		panel.add(btnSearch);
		
		add(shopItemScrollPane, BorderLayout.CENTER);
		mod = new ShopItemTableModel();
		tableItemShop = new JTable(mod);
		
		DefaultRowSorter sorter = new TableRowSorter<DefaultTableModel>(mod);
		tableItemShop.setRowSorter(sorter);
		tableItemShop.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {

			    SimpleDateFormat f = new SimpleDateFormat("dd/MM/yy HH:mm");

			    public Component getTableCellRendererComponent(JTable table,
			            Object value, boolean isSelected, boolean hasFocus,
			            int row, int column) {
			        if( value instanceof Date) {
			            value = f.format(value);
			        }
			        return super.getTableCellRendererComponent(table, value, isSelected,
			                hasFocus, row, column);
			    }
			}
				); 
			
		
		
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
							mod.init(txtSearch.getText());
							mod.fireTableDataChanged();
					}
				}).start();
				
				
			}
		});
		
	}

}
