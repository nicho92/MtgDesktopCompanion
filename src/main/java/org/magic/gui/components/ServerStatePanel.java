package org.magic.gui.components;

import static org.magic.tools.MTG.capitalize;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import org.magic.gui.models.conf.LogTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.ImageTools;
import org.magic.tools.UITools;
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
		
		
		var gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 36, 229, 47, 42, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 48, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		var lblName = new JLabel(server.getName(),ImageTools.resize(server.getIcon(), 32, 32),SwingConstants.LEFT);
		lblName.setToolTipText(server.description());
		lblName.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD, 11));
		add(lblName, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 1, 0));

		lblalive = new JLabel(icons.get(server.isAlive()));
		add(lblalive, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 2, 0));
	
		btnStartStop = new JButton((server.isAlive() ? "Stop" : "Start"));
		add(btnStartStop, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 3, 0));
		
		if(b) 
		{
			model = new LogTableModel();
			JXTable table = UITools.createNewTable(model);
			
			for(int i: new int[] {0,1,2,3})
				table.getColumnExt(model.getColumnName(i)).setVisible(false);

			
			
			table.setRowFilter(RowFilter.regexFilter(server.getClass().getName(), 3));
			table.setTableHeader(null);

			add(new JScrollPane(table), UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 4, 0));
		}
		
		TimerTask tache = new TimerTask() {
			public void run() {
				btnStartStop.setEnabled(server.isEnable());
				lblName.setEnabled(server.isEnable());
				lblalive.setEnabled(server.isEnable());
				
				if (server.isAlive())
				{
					btnStartStop.setText(capitalize("STOP"));
					
					if(b)
					{
						model.fireTableDataChanged();
					}
				}
				else
				{
					btnStartStop.setText(capitalize("START"));
				}
				
				lblalive.setIcon(icons.get(server.isAlive()));
					
				}
			
		};
		var timer = new Timer("Timer-" + server.getName());

		timer.scheduleAtFixedRate(tache, 0, 1000);

		btnStartStop.addActionListener(e -> {
			try {
				if (server.isAlive())
					server.stop();
				else
					server.start();
				
			} catch (Exception e1) {
				MTGControler.getInstance().notify(e1);
			}

			if(b)
				model.fireTableDataChanged();
		});

	}


}
