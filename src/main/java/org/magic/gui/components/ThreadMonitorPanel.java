package org.magic.gui.components;

import java.awt.BorderLayout;

import javax.swing.DefaultRowSorter;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;

public class ThreadMonitorPanel extends JPanel {
	private JTable table;
	private ThreadModel model;
	private transient DefaultRowSorter sorterCards;
	private JButton btnRefresh;
	private Timer t;
	private JLabel lblThreads;
	private JVMemoryPanel memoryPanel;

	public ThreadMonitorPanel() {
		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		model = new ThreadModel();
		table = new JTable(model);
		scrollPane.setViewportView(table);
		sorterCards = new TableRowSorter<DefaultTableModel>(model);
		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);

		btnRefresh = new JButton("Pause");
		btnRefresh.addActionListener(ae -> {
			if (t.isRunning()) {
				t.stop();
				btnRefresh.setText(MTGControler.getInstance().getLangService().getCapitalize("START"));
			} else {
				t.start();
				btnRefresh.setText(MTGControler.getInstance().getLangService().getCapitalize("PAUSE"));
				lblThreads.setText(ThreadManager.getInstance().getInfo());

			}
		});
		table.setRowSorter(sorterCards);
		panel.add(btnRefresh);

		lblThreads = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("THREADS"));
		panel.add(lblThreads);

		memoryPanel = new JVMemoryPanel();
		panel.add(memoryPanel);

		t = new Timer(3000, e -> model.fireTableDataChanged());
		t.start();
		

	}
}

class ThreadModel extends DefaultTableModel {

	String[] columns = new String[] { MTGControler.getInstance().getLangService().getCapitalize("GROUP"),
			MTGControler.getInstance().getLangService().getCapitalize("NAME"),
			MTGControler.getInstance().getLangService().getCapitalize("STATE") };

	@Override
	public Object getValueAt(int row, int column) {
		Thread[] t = Thread.getAllStackTraces().keySet().toArray(new Thread[getRowCount()]);

		try {
			switch (column) {
			case 0:
				return t[row].getThreadGroup().getName();
			case 1:
				return t[row].getName();
			case 2:
				return t[row].getState();
			default:
				return null;
			}
		} catch (Exception e) {
			return null;
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
