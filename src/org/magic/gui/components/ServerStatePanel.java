package org.magic.gui.components;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.magic.api.interfaces.MTGServer;

public class ServerStatePanel extends JPanel {

	private MTGServer server;
	private Map<Boolean,ImageIcon> icons;	
	private JButton btnStartStop;
	private JLabel lblalive;
	private JLabel lblLogs;
	
	
	public ServerStatePanel(MTGServer s) {
		FlowLayout flowLayout = (FlowLayout) getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		this.server=s;
		icons = new HashMap<Boolean,ImageIcon>();
		
		icons.put(false, new ImageIcon(ServerStatePanel.class.getResource("/res/delete.png")));
		icons.put(true, new ImageIcon(ServerStatePanel.class.getResource("/res/check.png")));
		
		
		JLabel lblName = new JLabel(server.getName());
		add(lblName);
		
		lblalive = new JLabel("");
		lblalive.setIcon(icons.get(server.isAlive()));
		add(lblalive);
		
		btnStartStop = new JButton((server.isAlive()?"Stop":"Start"));
		add(btnStartStop);
		
		lblLogs = new JLabel("New label");
		add(lblLogs);
		
		btnStartStop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(server.isAlive())
					{
						server.stop();
						btnStartStop.setText("Start");
						
					}
					else
					{
						server.start();
						btnStartStop.setText("Stop");
					}
					
					lblalive.setIcon(icons.get(server.isAlive()));
					lblLogs.setText("");
				} catch (Exception e1) {
					lblLogs.setText(e1.getMessage());
				}
				
				
				
			}
		});
		
		
		
	}
	
	
	
	
}
