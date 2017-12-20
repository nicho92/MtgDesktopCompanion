package org.magic.gui.components;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONPanel extends JScrollPane {
	JTextArea textpane;
	
	public JSONPanel() {
		
		textpane = new JTextArea();
		textpane.setLineWrap(true);
		textpane.setEditable(false);
		textpane.setWrapStyleWord(true);
		setViewportView(textpane);
		//textpane.setContentType("text/json");
	}
	
	public void show(Object mc)
	{
		
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		textpane.setText(g.toJson(mc));
		
	}

}
