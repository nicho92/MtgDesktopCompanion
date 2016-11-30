package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultRowSorter;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.magic.services.ThreadManager;

public class ThreadMonitorFrame extends JFrame {
	private JTable table;
	private ThreadModel model;
	private DefaultRowSorter sorterCards ;
	private JButton btnRefresh;
	private Timer t;
	private JLabel lblThreads;
	
	
	public ThreadMonitorFrame() {
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		model = new ThreadModel();
		table = new JTable(model);
		scrollPane.setViewportView(table);
		sorterCards = new TableRowSorter<DefaultTableModel>(model);
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		
		btnRefresh = new JButton("Pause");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(t.isRunning())
					{
						t.stop();
						btnRefresh.setText("Start");
					}
					else
					{
						t.start();
						btnRefresh.setText("Pause");
						lblThreads.setText(ThreadManager.getInstance().getInfo());
						
					}
					
			}

		});
		table.setRowSorter(sorterCards);
		panel.add(btnRefresh);
		
		lblThreads = new JLabel("Threads");
		panel.add(lblThreads);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setSize(700, 400);
		
		t = new Timer(1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
               model.fireTableDataChanged();
            }
        });
        t.start();
		
	}
}


class ThreadModel extends DefaultTableModel
{
	
	String[] columns = new String[]{"Group","Name","State"};
	
	@Override
	public Object getValueAt(int row, int column) {
		Thread[] t = Thread.getAllStackTraces().keySet().toArray(new Thread[getRowCount()]);
		
		switch(column)
		{
		case 0 : return t[row].getThreadGroup().getName();
		case 1 : return t[row].getName();
		case 2 : return t[row].getState();
		default : return null;
		}
		
	}
	
	@Override
	public String getColumnName(int column) {
		return columns[column];
	}
	
	@Override
	public int getRowCount() {
		return Thread.getAllStackTraces().keySet().size();
	}
	
	@Override
	public int getColumnCount() {
		return 3;
	}
}
