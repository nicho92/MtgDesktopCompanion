package org.magic.gui.components;

import static org.magic.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.lang.management.ManagementFactory;
import java.sql.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.Timer;

import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.TaskTableModel;
import org.magic.gui.models.ThreadsTableModel;
import org.magic.gui.renderer.standard.DateTableCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.UITools;
public class ThreadMonitor extends MTGUIComponent  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ThreadsTableModel modelT;
	private JButton btnRefresh;
	private Timer t;
	private JVMemoryPanel memoryPanel;
	private TaskTableModel modelTasks;
	
	public ThreadMonitor() {
		setLayout(new BorderLayout(0, 0));
		modelT = new ThreadsTableModel();
		modelTasks = new TaskTableModel();
		var tabs = new JTabbedPane();
		
		add(tabs, BorderLayout.CENTER);
		modelTasks.bind(ThreadManager.getInstance().listTasks());
		
		var tableTasks = UITools.createNewTable(modelTasks);
		UITools.initTableFilter(tableTasks);
		
		
		
		tableTasks.setDefaultRenderer(Date.class, new DateTableCellEditorRenderer(true));
		
		
		tabs.addTab("Threads",new JScrollPane(UITools.createNewTable(modelT)));
		tabs.addTab("Tasks",new JScrollPane(tableTasks));
			
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
			modelTasks.fireTableDataChanged();
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
