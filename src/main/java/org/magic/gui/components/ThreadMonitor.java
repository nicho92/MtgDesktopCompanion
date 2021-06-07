package org.magic.gui.components;

import static org.magic.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.lang.management.ManagementFactory;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import org.jdesktop.swingx.JXTable;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.ThreadsTableModel;
import org.magic.services.MTGConstants;
import org.magic.tools.UITools;
public class ThreadMonitor extends MTGUIComponent  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JXTable tableT;
	private ThreadsTableModel modelT;
	private JButton btnRefresh;
	private Timer t;
	private JVMemoryPanel memoryPanel;
	
	
	public ThreadMonitor() {
		setLayout(new BorderLayout(0, 0));
		modelT = new ThreadsTableModel();
		tableT = UITools.createNewTable(modelT);
		
		
		add(new JScrollPane(tableT), BorderLayout.CENTER);
		var panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		btnRefresh = new JButton("Pause");
		btnRefresh.addActionListener(ae -> {
			if (t.isRunning()) {
				t.stop();
				btnRefresh.setText(capitalize("START"));
			} else {
				t.start();
				btnRefresh.setText(capitalize("PAUSE"));

			}
		});
		panel.add(btnRefresh);
		memoryPanel = new JVMemoryPanel();
		panel.add(memoryPanel);
		
		t = new Timer(5000, e ->{ 
			modelT.init(ManagementFactory.getThreadMXBean().dumpAllThreads(true, true));
			memoryPanel.refresh();
			tableT.packAll();
		});
		
		
		t.start();
	}
	
	
	@Override
	public void onDestroy() {
		t.stop();
	}

	@Override
	public String getTitle() {
		return capitalize("THREADS");
	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_ADMIN;
	}
	
}
