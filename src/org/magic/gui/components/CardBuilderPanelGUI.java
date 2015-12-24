package org.magic.gui.components;

import java.awt.BorderLayout;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JPanel;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import javax.swing.JTabbedPane;

public class CardBuilderPanelGUI extends JPanel {
	
	private MagicCard card;
	
	
	private void init()
	{
		card = new MagicCard();
		MagicEdition ed = new MagicEdition();
		card.getEditions().add(ed);
		
				
	}
	
	public CardBuilderPanelGUI() {
		
		init();
		
		setLayout(new BorderLayout(0, 0));
		
		
		
	}

}
