package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.management.ManagementFactory;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXTable;
import org.magic.gui.models.ThreadsTableModel;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;

public class ThreadMonitorDialog extends JDialog  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JXTable table;
	private ThreadsTableModel modelT;
	private JButton btnRefresh;
	private Timer t;
	private JLabel lblThreads;
	private JVMemoryPanel memoryPanel;
	
	public ThreadMonitorDialog() {
		setLayout(new BorderLayout(0, 0));

		modelT = new ThreadsTableModel();
		table = new JXTable();
		table.setModel(modelT);
		add(new JScrollPane(table), BorderLayout.CENTER);
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
		panel.add(btnRefresh);

		lblThreads = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("THREADS"));
		panel.add(lblThreads);

		memoryPanel = new JVMemoryPanel();
		panel.add(memoryPanel);
		t = new Timer(5000, e -> modelT.init(ManagementFactory.getThreadMXBean().dumpAllThreads(true, true)));
		t.start();
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				t.stop();
			}
		});
	
	}

}
