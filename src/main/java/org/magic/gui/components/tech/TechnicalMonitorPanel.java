package org.magic.gui.components.tech;
import static org.magic.services.tools.MTG.capitalize;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGServer;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.MapTableModel;
import org.magic.gui.models.conf.DiscordInfoTableModel;
import org.magic.gui.models.conf.FileAccessTableModel;
import org.magic.gui.models.conf.JsonInfoTableModel;
import org.magic.gui.models.conf.NetworkTableModel;
import org.magic.gui.models.conf.QueriesTableModel;
import org.magic.gui.models.conf.TaskTableModel;
import org.magic.gui.models.conf.ThreadsTableModel;
import org.magic.servers.impl.ActiveMQServer;
import org.magic.servers.impl.DiscordBotServer;
import org.magic.servers.impl.JSONHttpServer;
import org.magic.servers.impl.QwartzServer;
import org.magic.services.MTGConstants;
import org.magic.services.TechnicalServiceManager;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

import com.google.gson.JsonObject;


public class TechnicalMonitorPanel extends MTGUIComponent  {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private ThreadsTableModel modelThreads;
	private Timer t;
	private JVMemoryPanel memoryPanel;
	private TaskTableModel modelTasks;
	private NetworkTableModel modelNetwork;
	private QueriesTableModel queryModel;
	private MapTableModel<Object, Object> modelConfig;
	private FileAccessTableModel modelFileAccess;
	private GenericTableModel<JsonObject> modelScript;
	private MapTableModel<String, Long> modelDao;
	private MapTableModel<String,Object> modelCacheJson;
	private JsonInfoTableModel modelJsonServerInfo;
	private DiscordInfoTableModel discordModel;
	private GedBrowserPanel gedPanel;
	private ActiveMQServerPanel activemqPanel;

	public TechnicalMonitorPanel() {
		setLayout(new BorderLayout(0, 0));
		modelThreads = new ThreadsTableModel();
		modelTasks = new TaskTableModel();
		modelNetwork = new NetworkTableModel();
		queryModel = new QueriesTableModel();
		modelConfig = new MapTableModel<>();
		modelDao = new MapTableModel<>();
		modelCacheJson = new MapTableModel<>();
		modelJsonServerInfo = new JsonInfoTableModel();
		discordModel = new DiscordInfoTableModel();
		modelFileAccess= new FileAccessTableModel();
		gedPanel = new GedBrowserPanel();
		activemqPanel = new ActiveMQServerPanel();
		
		
		modelScript = new GenericTableModel<>()
				{
					private static final long serialVersionUID = 1L;
					@Override
					public Object getValueAt(int row, int column) {
						return items.get(row).get(getColumnName(column));
					}

					@Override
					public String getColumnName(int column) {
						return columns[column];
					}
				};

		
		add(getContextTabbedPane(), BorderLayout.CENTER);


		modelTasks.bind(TechnicalServiceManager.inst().getTasksInfos());
		modelNetwork.bind(TechnicalServiceManager.inst().getNetworkInfos());
		queryModel.bind(TechnicalServiceManager.inst().getDaoInfos());
		modelJsonServerInfo.bind(TechnicalServiceManager.inst().getJsonInfo());
		discordModel.bind(TechnicalServiceManager.inst().getDiscordInfos());
		modelFileAccess.bind(TechnicalServiceManager.inst().getFileInfos());
		
		modelConfig.init(TechnicalServiceManager.inst().getSystemInfo());
		modelDao.init(MTG.getEnabledPlugin(MTGDao.class).getDBSize());

		
		
		var tableTasks = UITools.createNewTable(modelTasks);
		UITools.initTableFilter(tableTasks);

		var tableNetwork = UITools.createNewTable(modelNetwork);
		UITools.initTableFilter(tableNetwork);

		var tableScripts = UITools.createNewTable(modelScript);
		UITools.initTableFilter(tableScripts);

		var tableQueries = UITools.createNewTable(queryModel);
		UITools.initTableFilter(tableQueries);

		var tableDaos = UITools.createNewTable(modelDao);
		UITools.initTableFilter(tableDaos);

		var tableCacheJson = UITools.createNewTable(modelCacheJson);
		UITools.initTableFilter(tableCacheJson);

		var tableJsonInfo= UITools.createNewTable(modelJsonServerInfo);
		UITools.initTableFilter(tableJsonInfo);

		var tableDiscordInfo= UITools.createNewTable(discordModel);
		UITools.initTableFilter(tableDiscordInfo);
		
		var tableFileAccessIInfo= UITools.createNewTable(modelFileAccess);
		UITools.initTableFilter(tableFileAccessIInfo);
		

		TableCellRenderer durationRenderer = (JTable table, Object value, boolean isSelected,boolean hasFocus, int row, int column)->{
					var lab= new JLabel();
						 lab.setOpaque(false);
					try {
						lab.setText(DurationFormatUtils.formatDurationHMS((Long)value));
					}
					catch(Exception e )
					{
						lab.setText(String.valueOf(value));
					}
					
					return lab;
				};

		TableCellRenderer sizeRenderer = (JTable table, Object value, boolean isSelected,boolean hasFocus, int row, int column)->{
					var lab= new JLabel(UITools.humanReadableSize((Long)value));
					lab.setOpaque(false);
					return lab;
			};


		tableTasks.setDefaultRenderer(Long.class, durationRenderer);
		tableNetwork.setDefaultRenderer(Long.class, durationRenderer);
		tableQueries.setDefaultRenderer(Long.class, durationRenderer);
		tableDaos.setDefaultRenderer(Long.class, sizeRenderer);
		tableJsonInfo.setDefaultRenderer(Long.class, durationRenderer);
		tableDiscordInfo.setDefaultRenderer(Long.class, durationRenderer);
		tableFileAccessIInfo.setDefaultRenderer(Long.class, durationRenderer);
		activemqPanel.getTable().setDefaultRenderer(Long.class, durationRenderer);
		
		
		
		getContextTabbedPane().addTab("Config",MTGConstants.ICON_SMALL_HELP,new JScrollPane(UITools.createNewTable(modelConfig)));
		getContextTabbedPane().addTab("Threads",MTGConstants.ICON_TAB_ADMIN,new JScrollPane(UITools.createNewTable(modelThreads)));
		getContextTabbedPane().addTab("Tasks",MTGConstants.ICON_TAB_ADMIN,new JScrollPane(tableTasks));
		getContextTabbedPane().addTab("Network",MTGConstants.ICON_TAB_NETWORK,new JScrollPane(tableNetwork));
		getContextTabbedPane().addTab("Qwartz Script",MTGConstants.ICON_SMALL_SCRIPT,new JScrollPane(tableScripts));
		getContextTabbedPane().addTab("Queries",MTGConstants.ICON_TAB_DAO,new JScrollPane(tableQueries));
		getContextTabbedPane().addTab("DB Size",MTGConstants.ICON_TAB_DAO,new JScrollPane(tableDaos));
		getContextTabbedPane().addTab("Discord",ImageTools.resize(new DiscordBotServer().getIcon(),15,15)  ,new JScrollPane(tableDiscordInfo));
		getContextTabbedPane().addTab("JsonServer Cache",MTGConstants.ICON_TAB_CACHE,new JScrollPane(tableCacheJson));
		getContextTabbedPane().addTab("JsonServer Queries",MTGConstants.ICON_TAB_SERVER,new JScrollPane(tableJsonInfo));
		getContextTabbedPane().addTab("Files Access",MTGConstants.ICON_TAB_IMPORT,new JScrollPane(tableFileAccessIInfo));
		UITools.addTab(getContextTabbedPane(), new LoggerViewPanel());
		UITools.addTab(getContextTabbedPane(), gedPanel);
		UITools.addTab(getContextTabbedPane(), activemqPanel);
		




		var panel = new JPanel();
		add(panel, BorderLayout.NORTH);




		memoryPanel = new JVMemoryPanel();
		panel.add(memoryPanel);

		t = new Timer(MTGConstants.TECHNICAL_REFRESH, e ->{
			modelThreads.init(TechnicalServiceManager.inst().getThreadsInfos());
			memoryPanel.refresh();
			modelTasks.fireTableDataChanged();
			modelNetwork.fireTableDataChanged();
			modelConfig.fireTableDataChanged();
			queryModel.fireTableDataChanged();
			modelDao.fireTableDataChanged();
			modelJsonServerInfo.fireTableDataChanged();
			discordModel.fireTableDataChanged();
			modelFileAccess.fireTableDataChanged();

			if(MTG.getPlugin("Qwartz", MTGServer.class).isAlive()) {
				try {
					modelScript.bind( ((QwartzServer)MTG.getPlugin("Qwartz", MTGServer.class)).getJobs()  );
					modelScript.setColumns(modelScript.getItems().get(0).keySet().stream().toArray(String[]::new));
					modelScript.fireTableStructureChanged();
					modelScript.fireTableDataChanged();

				}
				catch(Exception ex)
				{
					logger.error("error loading . Maybe Qwartz server is stopped",ex);
				}
			}

			if(MTG.getPlugin("Json Http Server", MTGServer.class).isAlive()) {
				try {
					modelCacheJson.init( ((JSONHttpServer)MTG.getPlugin("Json Http Server", MTGServer.class)).getCache().entries() );
					modelCacheJson.fireTableDataChanged();
				}
				catch(Exception ex)
				{
					logger.error("error loading . Maybe Qwartz server is stopped",ex);
				}
			}
			
			if(MTG.getPlugin("ActiveMQ", MTGServer.class).isAlive()) {
				try {
					activemqPanel.init( ((ActiveMQServer)MTG.getPlugin("ActiveMQ", MTGServer.class)));
					
				}
				catch(Exception ex)
				{
					logger.error("error loading . Maybe ActiveMQ server is stopped",ex);
				}
			}



		});


		t.start();
	}


	@Override
	public void onDestroy() {
		t.stop();
	}

	@Override
	public String getTitle() {
		return capitalize("TECHNICAL");
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_ADMIN;
	}

}
