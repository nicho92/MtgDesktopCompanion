package org.magic.gui.components;
import static org.magic.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.lang.management.ManagementFactory;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.MapTableModel;
import org.magic.gui.models.conf.NetworkTableModel;
import org.magic.gui.models.conf.TaskTableModel;
import org.magic.gui.models.conf.ThreadsTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.UITools;


public class ThreadMonitor extends MTGUIComponent  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ThreadsTableModel modelT;
	private Timer t;
	private JVMemoryPanel memoryPanel;
	private TaskTableModel modelTasks;
	private NetworkTableModel modelNetwork;
	private MapTableModel<Object, Object> modelConfig;
	
	
	public ThreadMonitor() {
		setLayout(new BorderLayout(0, 0));
		modelT = new ThreadsTableModel();
		modelTasks = new TaskTableModel();
		modelNetwork = new NetworkTableModel();
		var tabs = new JTabbedPane();
		var btnClean = UITools.createBindableJButton("Clean",MTGConstants.ICON_DELETE, KeyEvent.VK_C , "Cleaning");
		add(tabs, BorderLayout.CENTER);
		modelTasks.bind(ThreadManager.getInstance().listTasks());	
		modelNetwork.bind(URLTools.getNetworksInfos());
		modelConfig = new MapTableModel<>();
		modelConfig.init(System.getProperties().entrySet());
		
		var tableTasks = UITools.createNewTable(modelTasks);
		UITools.initTableFilter(tableTasks);
		
		var tableNetwork = UITools.createNewTable(modelNetwork);
		UITools.initTableFilter(tableNetwork);
		
		TableCellRenderer durationRenderer = (JTable table, Object value, boolean isSelected,boolean hasFocus, int row, int column)->{
						var lab= new JLabel(DurationFormatUtils.formatDurationHMS((Long)value));
						lab.setOpaque(false);
						return lab;
				};
				
		tableTasks.setDefaultRenderer(Long.class, durationRenderer);
		tableNetwork.setDefaultRenderer(Long.class, durationRenderer);
		
		tabs.addTab("Config",MTGConstants.ICON_SMALL_HELP,new JScrollPane(UITools.createNewTable(modelConfig)));
		tabs.addTab("Threads",MTGConstants.ICON_TAB_ADMIN,new JScrollPane(UITools.createNewTable(modelT)));
		tabs.addTab("Tasks",MTGConstants.ICON_TAB_ADMIN,new JScrollPane(tableTasks));
		tabs.addTab("Network",MTGConstants.ICON_TAB_NETWORK,new JScrollPane(tableNetwork));
		
		UITools.addTab(tabs, new LoggerViewPanel());
		
		var panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		
	
		btnClean.addActionListener(ae -> ThreadManager.getInstance().clean());
		
		panel.add(btnClean);
		
		
		
		memoryPanel = new JVMemoryPanel();
		panel.add(memoryPanel);
		
		t = new Timer(5000, e ->{ 
			modelT.init(ManagementFactory.getThreadMXBean().dumpAllThreads(true, true));
			memoryPanel.refresh();
			modelTasks.fireTableDataChanged();
			modelNetwork.fireTableDataChanged();
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
