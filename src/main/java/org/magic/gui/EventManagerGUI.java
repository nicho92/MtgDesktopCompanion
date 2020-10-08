package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicEvent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.events.PlayersPanel;
import org.magic.gui.components.events.TournamentPanel;
import org.magic.gui.models.MagicEventsTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.UITools;


public class EventManagerGUI extends MTGUIComponent{

	private static final long serialVersionUID = 1L;
	private JXTable tableEvents;
	private MagicEventsTableModel model;
	private TournamentPanel tournamentPanel;
	private PlayersPanel players;
	
	@Override
	public String getTitle() {
		return "Events Manager";
	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_EVENTS;
	}
	
	
	public EventManagerGUI() {
		setLayout(new BorderLayout());
		model = new MagicEventsTableModel();
		tableEvents = new JXTable(model);
		
		try {
			MTGControler.getInstance().getEventsManager().load();
			model.init(MTGControler.getInstance().getEventsManager().getEvents());
		} catch (IOException e) {
			MTGControler.getInstance().notify(e);
		}
		
		
		tournamentPanel = new TournamentPanel();
		players = new PlayersPanel();
		JPanel pannhaut = new JPanel();

		JButton newTournament = UITools.createBindableJButton("New Event", MTGConstants.ICON_NEW, KeyEvent.VK_N, "newEvent");
		JButton saveTournament = UITools.createBindableJButton("Save Event", MTGConstants.ICON_SAVE, KeyEvent.VK_S, "saveEvent");
		JButton startTournament = UITools.createBindableJButton("Start Event", MTGConstants.ICON_SAVE, KeyEvent.VK_T, "startTournament");
		JButton deleteTournament = UITools.createBindableJButton("Delete Event", MTGConstants.ICON_DELETE, KeyEvent.VK_R, "deleteTournament");
		
		startTournament.setEnabled(false);
		deleteTournament.setEnabled(false);
		tableEvents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		pannhaut.add(newTournament);
		pannhaut.add(saveTournament);
		pannhaut.add(startTournament);
		pannhaut.add(deleteTournament);
		
		add(new JScrollPane(tableEvents),BorderLayout.CENTER);
		add(players, BorderLayout.EAST);
		add(tournamentPanel, BorderLayout.WEST);
		add(pannhaut,BorderLayout.NORTH);
		
		
		deleteTournament.addActionListener(al->{
			if(UITools.getTableSelection(tableEvents, 0)!=null)
				model.removeItem((MagicEvent)UITools.getTableSelection(tableEvents, 0));
		});

		saveTournament.addActionListener(al->{
			tournamentPanel.getCurrentEvent().setPlayers(players.getModel().getPlayers());
			tournamentPanel.save();
			try {
				MTGControler.getInstance().getEventsManager().saveEvents();
			} catch (IOException e) {
				MTGControler.getInstance().notify(e);
			}
			model.fireTableDataChanged();
		});
		
		
		newTournament.addActionListener(al->{
			tournamentPanel.save();
			MagicEvent e = tournamentPanel.newEvent();
			model.addItem(e);
			MTGControler.getInstance().getEventsManager().addEvent(e);
			tournamentPanel.clear();
		});
	
		tableEvents.getSelectionModel().addListSelectionListener(event -> {
			
			if (!event.getValueIsAdjusting()) 
			{
				MagicEvent ev = UITools.getTableSelection(tableEvents, 0);
				
				if(ev==null)
					return;
				
				
				tournamentPanel.setTournament(ev);
				players.getModel().clear();
				players.getModel().addAll(ev.getPlayers());
				
				
				deleteTournament.setEnabled(ev!=null);
				startTournament.setEnabled(ev!=null);
				saveTournament.setEnabled(ev!=null);
			}
		});
		
		
		startTournament.addActionListener(al->{
			
			MagicEvent ev = UITools.getTableSelection(tableEvents, 0);
			
			if(ev!=null)
				MTGControler.getInstance().getEventsManager().start(ev);
		});
		
		
		
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame();
		EventManagerGUI ev = new EventManagerGUI();
		f.getContentPane().add(ev);
		f.pack();
		f.setIconImage(ev.getIcon().getImage());
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.setVisible(true);
	}

}
