package org.magic.gui.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import org.magic.game.model.Player;

public class PlayersListModel extends DefaultListModel<Player> {

	
	private static final long serialVersionUID = 1L;

	
	public List<Player> getPlayers()
	{
		
		List<Player> ret = new ArrayList<>();
		
		for(int i =0;i<getSize();i++)
			ret.add(getElementAt(i));
		
		return ret;
			
	}
	
	
	
}
