package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.magic.gui.models.conf.LogTableModel;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;



public class LoggerViewFrame extends JFrame {
	private JXTable table;
	LogTableModel model;
	private TableFilterHeader filterHeader;
	
	
	public LoggerViewFrame() {
		setTitle("Logs");
		model=new LogTableModel();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		table = new JXTable(model);
		scrollPane.setViewportView(table);
		filterHeader = new TableFilterHeader(table, AutoChoices.ENABLED);
		
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		
		JButton btnRefresh = new JButton("");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				model.fireTableDataChanged();
			}
		});
		btnRefresh.setIcon(new ImageIcon(LoggerViewFrame.class.getResource("/res/refresh.png")));
		panel.add(btnRefresh);
		
		table.packAll();
		pack();
	
	}

}
