package org.magic.gui.components;

import java.awt.BorderLayout;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.tools.MemoryTools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ObjectViewerPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JTextArea textpane;
	private JRadioButton rdoJson;
	private JRadioButton rdoMemory;
	private transient Object currentObject;

	public ObjectViewerPanel() {
		setLayout(new BorderLayout());
		textpane = new JTextArea();
		textpane.setLineWrap(true);
		textpane.setEditable(false);
		textpane.setWrapStyleWord(true);
		add(new JScrollPane(textpane),BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		
		rdoJson = new JRadioButton("Json");
		rdoJson.setSelected(true);
		rdoMemory = new JRadioButton("Memory");		
		
		ButtonGroup group = new ButtonGroup();
					group.add(rdoJson);
					group.add(rdoMemory);
		
		panel.add(rdoJson);
		panel.add(rdoMemory);
		
		
		rdoJson.addItemListener(il->show(currentObject));
		rdoMemory.addItemListener(il->show(currentObject));
	}

	public void show(Object mc) {
		currentObject = mc;
		
		if(currentObject==null)
			return;
		
		if(rdoJson.isSelected())
		{
			Gson g = new GsonBuilder().setPrettyPrinting().create();
			textpane.setText(g.toJson(currentObject));
			textpane.setCaretPosition(0);
		}
		else
		{
			textpane.setText(MemoryTools.statInstanceToString(currentObject));
		}
	}

	@Override
	public String getTitle() {
		return "ObjectViewer";
	}

}
