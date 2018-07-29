package org.magic.gui.components;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;import org.magic.api.beans.MagicCard;
import org.magic.game.model.factories.AbilitiesFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONPanel extends JScrollPane {

	private static final long serialVersionUID = 1L;
	JTextArea textpane;

	public JSONPanel() {

		textpane = new JTextArea();
		textpane.setLineWrap(true);
		textpane.setEditable(false);
		textpane.setWrapStyleWord(true);
		setViewportView(textpane);
	}

	public void show(Object mc) {
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		textpane.setText(g.toJson(mc));
		textpane.setCaretPosition(0);
	}

}
