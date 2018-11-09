package org.magic.gui.components;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.jdesktop.swingx.JXTable;
import org.magic.api.interfaces.MTGServer;
import org.magic.gui.models.LogTableModel;
import org.magic.servers.impl.JSONHttpServer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class ServerStatePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient MTGServer server;
	private Map<Boolean, ImageIcon> icons;
	private JButton btnStartStop;
	private JLabel lblalive;
	private LogTableModel model; 
	
	public MTGServer getServer() {
		return server;
	}
	

	public ServerStatePanel(boolean b, MTGServer plugin) {
		init(b,plugin);
	}
	
	public ServerStatePanel() {
		init(true,new JSONHttpServer());
	}
	
	
	public ServerStatePanel(MTGServer s) {
		init(true,s);
	}
	
	private void init(boolean b, MTGServer s) {
		
		setBorder(new LineBorder(SystemColor.activeCaption, 1, true));

		if(s==null)
			return;
		
		this.server = s;
		
		
		
		icons = new HashMap<>();
		
		icons.put(false, MTGConstants.ICON_DELETE);
		icons.put(true, MTGConstants.ICON_CHECK);
		
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 36, 229, 47, 42, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 48, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblName = new JLabel(server.getName(),server.getIcon(),SwingConstants.LEFT);
		lblName.setToolTipText(server.description());
		lblName.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbclblName = new GridBagConstraints();
		gbclblName.anchor = GridBagConstraints.WEST;
		gbclblName.insets = new Insets(0, 0, 0, 5);
		gbclblName.gridx = 1;
		gbclblName.gridy = 0;
		add(lblName, gbclblName);

		lblalive = new JLabel(icons.get(server.isAlive()));
		GridBagConstraints gbclblalive = new GridBagConstraints();
		gbclblalive.anchor = GridBagConstraints.WEST;
		gbclblalive.insets = new Insets(0, 0, 0, 5);
		gbclblalive.gridx = 2;
		gbclblalive.gridy = 0;
		add(lblalive, gbclblalive);
	
		btnStartStop = new JButton((server.isAlive() ? "Stop" : "Start"));
		GridBagConstraints gbcbtnStartStop = new GridBagConstraints();
		gbcbtnStartStop.anchor = GridBagConstraints.WEST;
		gbcbtnStartStop.insets = new Insets(0, 0, 0, 5);
		gbcbtnStartStop.gridx = 3;
		gbcbtnStartStop.gridy = 0;
		add(btnStartStop, gbcbtnStartStop);
	
		if(b) 
		{
			
			model = new LogTableModel();
			JXTable table = new JXTable();
			table.setModel(model);
			table.getColumnExt(model.getColumnName(0)).setVisible(false);
			table.getColumnExt(model.getColumnName(1)).setVisible(false);
			table.getColumnExt(model.getColumnName(2)).setVisible(false);
			table.setRowFilter(RowFilter.regexFilter(server.getClass().getName(), 2));
			table.setTableHeader(null);

			GridBagConstraints gbcscrollPane = new GridBagConstraints();
			gbcscrollPane.fill = GridBagConstraints.BOTH;
			gbcscrollPane.gridx = 4;
			gbcscrollPane.gridy = 0;
			add(new JScrollPane(table), gbcscrollPane);
			
		}
		
		TimerTask tache = new TimerTask() {
			public void run() {
				btnStartStop.setEnabled(server.isEnable());
				lblName.setEnabled(server.isEnable());
				lblalive.setEnabled(server.isEnable());
				
				if (server.isAlive())
				{
					btnStartStop.setText(MTGControler.getInstance().getLangService().getCapitalize("STOP"));
					if(b)
					{
					model.fireTableDataChanged();
					}
				}
				else
				{
					btnStartStop.setText(MTGControler.getInstance().getLangService().getCapitalize("START"));
				}
				
				lblalive.setIcon(icons.get(server.isAlive()));
					
				}
			
		};
		Timer timer = new Timer("Timer-" + server.getName());

		timer.scheduleAtFixedRate(tache, 0, 1000);

		btnStartStop.addActionListener(e -> {
			try {
				if (server.isAlive())
					server.stop();
				else
					server.start();
				
				model.fireTableDataChanged();
			} catch (Exception e1) {
				model.fireTableDataChanged();
			}

		});

	}


}
