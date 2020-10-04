package org.magic.gui.components.events;

import java.awt.BorderLayout;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.magic.game.model.Player;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.PlayersListModel;

public class PlayersPanel extends MTGUIComponent{
	private static final long serialVersionUID = 1L;
	
	
	private PlayersListModel model ;
	private JList<Player> listPlayers;
	private JTextField fieldName;
	
	
	public PlayersPanel() {
		setLayout(new BorderLayout());
		
		model = new PlayersListModel();
		fieldName = new JTextField(30);
		listPlayers = new JList<>(model);
		
		
		add(fieldName,BorderLayout.NORTH);
		add(new JScrollPane(listPlayers),BorderLayout.CENTER);
		
		
		fieldName.addActionListener(al->{ 
			
			if(fieldName.getText().isEmpty())
				return;
			
			model.addElement(new Player(fieldName.getText()));
			
			fieldName.setText("");
			
		});
	}
	

	@Override
	public String getTitle() {
		return "PLAYERS";
	}
	
	
	public PlayersListModel getModel()
	{
		return model;
	}
	
}
