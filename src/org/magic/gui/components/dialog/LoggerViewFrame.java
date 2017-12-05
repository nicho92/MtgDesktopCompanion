package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import org.jdesktop.swingx.JXTable;
import org.magic.gui.models.conf.LogTableModel;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;



public class LoggerViewFrame extends JFrame {
	private JXTable table;
	private LogTableModel model;
	private Timer t;
	private TableFilterHeader filterHeader;
	private JCheckBox chckbxAutorefresh;
	private JButton btnRefresh; 
	
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
		
		btnRefresh = new JButton("");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				model.fireTableDataChanged();
			}
		});
		btnRefresh.setIcon(new ImageIcon(LoggerViewFrame.class.getResource("/res/refresh.png")));
		panel.add(btnRefresh);
		
		t = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               model.fireTableDataChanged();
            }
			});
		
		
		chckbxAutorefresh = new JCheckBox("Auto-refresh");
		chckbxAutorefresh.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				
				if(chckbxAutorefresh.isSelected())
				{
					t.start();
					btnRefresh.setEnabled(false);
				}
				else
				{
					t.stop();
					btnRefresh.setEnabled(true);
				}
				
			}
		});
		panel.add(chckbxAutorefresh);
		
		table.packAll();
		pack();
	
	}

}
