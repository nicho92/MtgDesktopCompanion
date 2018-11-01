package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.management.ManagementFactory;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;

import org.jdesktop.swingx.JXTable;
import org.magic.gui.models.MemoryTableModel;
import org.magic.gui.models.ThreadsTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;

public class ThreadMonitorDialog extends JDialog  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JXTable tableT;
	private JXTable tableM;
	private ThreadsTableModel modelT;
	private MemoryTableModel modelM;
	private JButton btnRefresh;
	private Timer t;
	private JLabel lblThreads;
	private JVMemoryPanel memoryPanel;
	
	public ThreadMonitorDialog() {
		getContentPane().setLayout(new BorderLayout(0, 0));

		
		setIconImage(MTGConstants.ICON_TAB_ADMIN.getImage());
		setTitle(MTGControler.getInstance().getLangService().getCapitalize("THREADS"));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		modelT = new ThreadsTableModel();
		modelM = new MemoryTableModel();
		tableT = new JXTable();
		tableT.setModel(modelT);
		
		tableM = new JXTable();
		tableM.setModel(modelM);
		
		tableM.setPreferredSize(new Dimension(0, 50));
		
		getContentPane().add(new JScrollPane(tableT), BorderLayout.SOUTH);
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

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
		
		JScrollPane scrollPane = new JScrollPane(tableM);
		scrollPane.setPreferredSize(new Dimension(0, 50));
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		
		t = new Timer(5000, e ->{ 
			modelT.init(ManagementFactory.getThreadMXBean().dumpAllThreads(true, true));
			modelM.init(Arrays.asList(ManagementFactory.getMemoryMXBean()));
			memoryPanel.refresh();
			tableT.packAll();
		});
		
		
		t.start();
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				t.stop();
			}
		});
	
	}

}
