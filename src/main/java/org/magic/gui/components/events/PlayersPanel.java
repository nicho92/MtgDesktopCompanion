package org.magic.gui.components.events;

import java.awt.BorderLayout;

import javax.swing.JList;
import javax.swing.JScrollPane;

import org.magic.api.beans.MagicEvent;
import org.magic.game.model.Player;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.PlayersListModel;

public class PlayersPanel extends MTGUIComponent{
	private static final long serialVersionUID = 1L;
	
	
	private PlayersListModel model ;
	private JList<Player> listPlayers;
	
	
	
	public PlayersPanel() {
		setLayout(new BorderLayout());
		model = new PlayersListModel();
		listPlayers = new JList<>(model);
		add(new JScrollPane(listPlayers),BorderLayout.CENTER);
	}
	
	



	@Override
	public String getTitle() {
		return "PLAYERS";
	}





	public void setTournament(MagicEvent currentEvent) {
		model.clear();
		model.addAll(currentEvent.getPlayers());
	}
	
	
	
}
