package org.magic.gui.components;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONPanel extends JComponent {

	private static final long serialVersionUID = 1L;
	private JTextArea textpane;

	public JSONPanel() {
		setLayout(new BorderLayout());
		textpane = new JTextArea();
		textpane.setLineWrap(true);
		textpane.setEditable(false);
		textpane.setWrapStyleWord(true);
		add(new JScrollPane(textpane),BorderLayout.CENTER);
	}

	public void show(Object mc) {
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		textpane.setText(g.toJson(mc));
		textpane.setCaretPosition(0);
	}

}
