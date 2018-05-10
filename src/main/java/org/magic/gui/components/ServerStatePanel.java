package org.magic.gui.components;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.magic.api.interfaces.MTGServer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class ServerStatePanel extends JPanel {

	private transient MTGServer server;
	private Map<Boolean, ImageIcon> icons;
	private JButton btnStartStop;
	private JLabel lblalive;
	private JLabel lblLogs;

	public ServerStatePanel(MTGServer s) {

		if(s==null)
			return;
		
		this.server = s;
		icons = new HashMap<>();

		
		
		icons.put(false, MTGConstants.ICON_DELETE);
		icons.put(true, MTGConstants.ICON_CHECK);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 109, 40, 47, 42, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 23, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblName = new JLabel(server.getName());
		lblName.setToolTipText(server.description());
		lblName.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbclblName = new GridBagConstraints();
		gbclblName.anchor = GridBagConstraints.WEST;
		gbclblName.insets = new Insets(0, 0, 0, 5);
		gbclblName.gridx = 0;
		gbclblName.gridy = 0;
		add(lblName, gbclblName);

		lblalive = new JLabel("");
		lblalive.setIcon(icons.get(server.isAlive()));
		GridBagConstraints gbclblalive = new GridBagConstraints();
		gbclblalive.insets = new Insets(0, 0, 0, 5);
		gbclblalive.gridx = 2;
		gbclblalive.gridy = 0;
		add(lblalive, gbclblalive);

		lblLogs = new JLabel("");
		GridBagConstraints gbclblLogs = new GridBagConstraints();
		gbclblLogs.insets = new Insets(0, 0, 0, 5);
		gbclblLogs.gridx = 3;
		gbclblLogs.gridy = 0;
		add(lblLogs, gbclblLogs);

		btnStartStop = new JButton((server.isAlive() ? "Stop" : "Start"));
		GridBagConstraints gbcbtnStartStop = new GridBagConstraints();
		gbcbtnStartStop.gridx = 4;
		gbcbtnStartStop.gridy = 0;
		add(btnStartStop, gbcbtnStartStop);

		TimerTask tache = new TimerTask() {
			public void run() {
				
				btnStartStop.setEnabled(server.isEnable());
				lblName.setEnabled(server.isEnable());
				lblalive.setEnabled(server.isEnable());
				
				if (server.isAlive())
					btnStartStop.setText(MTGControler.getInstance().getLangService().getCapitalize("STOP"));
				else
					btnStartStop.setText(MTGControler.getInstance().getLangService().getCapitalize("START"));
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

				lblLogs.setText("");
			} catch (Exception e1) {
				lblLogs.setText(e1.getMessage());
			}

		});

	}

}
