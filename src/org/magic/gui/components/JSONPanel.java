package org.magic.gui.components;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import org.magic.api.beans.MagicCard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONPanel extends JScrollPane {
	JTextPane textpane;
	
	public JSONPanel() {
		
		textpane = new JTextPane();
		textpane.setEditable(false);
		setViewportView(textpane);
		textpane.setContentType("text/json");
	}
	
	public void showCard(MagicCard mc)
	{
		
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		textpane.setText(g.toJson(mc));
		
	}

}
