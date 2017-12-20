package org.magic.gui.components;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.magic.api.interfaces.MTGServer;

public class ServerStatePanel extends JPanel {

	private MTGServer server;
	private Map<Boolean,ImageIcon> icons;	
	private JButton btnStartStop;
	private JLabel lblalive;
	private JLabel lblLogs;
	
	
	public ServerStatePanel(MTGServer s) {
		this.server=s;
		icons = new HashMap<Boolean,ImageIcon>();
		
		icons.put(false, new ImageIcon(ServerStatePanel.class.getResource("/res/delete.png")));
		icons.put(true, new ImageIcon(ServerStatePanel.class.getResource("/res/check.png")));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{109, 40, 47, 42, 0, 0};
		gridBagLayout.rowHeights = new int[]{23, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		
		JLabel lblName = new JLabel(server.getName());
		lblName.setToolTipText(server.description());
		lblName.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.WEST;
		gbc_lblName.insets = new Insets(0, 0, 0, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		add(lblName, gbc_lblName);
		
		lblalive = new JLabel("");
		lblalive.setIcon(icons.get(server.isAlive()));
		GridBagConstraints gbc_lblalive = new GridBagConstraints();
		gbc_lblalive.insets = new Insets(0, 0, 0, 5);
		gbc_lblalive.gridx = 2;
		gbc_lblalive.gridy = 0;
		add(lblalive, gbc_lblalive);
		
		lblLogs = new JLabel("");
		GridBagConstraints gbc_lblLogs = new GridBagConstraints();
		gbc_lblLogs.insets = new Insets(0, 0, 0, 5);
		gbc_lblLogs.gridx = 3;
		gbc_lblLogs.gridy = 0;
		add(lblLogs, gbc_lblLogs);
		
		btnStartStop = new JButton((server.isAlive()?"Stop":"Start"));
		GridBagConstraints gbc_btnStartStop = new GridBagConstraints();
		gbc_btnStartStop.gridx = 4;
		gbc_btnStartStop.gridy = 0;
		add(btnStartStop, gbc_btnStartStop);
		
		
		TimerTask tache = new TimerTask() {    
            public void run() {
            	if(server.isAlive())
            		btnStartStop.setText("Stop");
            	else
            		btnStartStop.setText("Start");
            	lblalive.setIcon(icons.get(server.isAlive()));
            }
		};
		Timer timer = new Timer("Timer-"+server.getName());
		
		timer.scheduleAtFixedRate(tache,0,1000);

		
		btnStartStop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(server.isAlive())
						server.stop();
					else
						server.start();

					lblLogs.setText("");
				} catch (Exception e1) {
					lblLogs.setText(e1.getMessage());
				}
			}
		});
		
		
		
	}
	
	
	
	
}
