package org.magic.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXTable;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.events.TournamentPanel;
import org.magic.gui.models.MagicEventsTableModel;

public class EventManagerGUI extends MTGUIComponent{

	private static final long serialVersionUID = 1L;
	private JXTable tableEvents;
	private MagicEventsTableModel model;
	
	@Override
	public String getTitle() {
		return "Events Manager";
	}
	
	public EventManagerGUI() {
		setLayout(new BorderLayout());
		model = new MagicEventsTableModel();
		tableEvents = new JXTable(model);
		add(new TournamentPanel(),BorderLayout.EAST);
		add(new JScrollPane(tableEvents),BorderLayout.CENTER);
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.getContentPane().add(new EventManagerGUI());
		f.pack();
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.setVisible(true);
	}

}
