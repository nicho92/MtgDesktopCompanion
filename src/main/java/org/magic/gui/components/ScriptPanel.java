package org.magic.gui.components;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ScriptPanel extends JComponent {
	private JTable table;
	
	
	public ScriptPanel() {
		setLayout(new BorderLayout(0, 0));
		
		table = new JTable();
		add(new JScrollPane(table), BorderLayout.NORTH);
		
		JEditorPane editorPane = new JEditorPane();
		add(editorPane, BorderLayout.CENTER);
	}

	

}
