package org.magic.gui.components;

import java.awt.BorderLayout;
import java.io.File;
import java.nio.file.Paths;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.magic.gui.abstracts.GenericTableModel;
import org.magic.services.MTGConstants;

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
