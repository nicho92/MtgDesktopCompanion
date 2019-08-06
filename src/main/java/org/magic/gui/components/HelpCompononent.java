package org.magic.gui.components;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import org.magic.api.interfaces.MTGPlugin;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.tools.URLTools;

public class HelpCompononent extends MTGUIComponent {

	private static final long serialVersionUID = 1L;

	private JEditorPane pane;
	
	public HelpCompononent() {
		pane = new JEditorPane();
		pane.setContentType("text/html");
		setLayout(new BorderLayout());
		add(new JScrollPane(pane),BorderLayout.CENTER);
	}
	
	public void init(MTGPlugin mtg)
	{
		try {
			pane.setText(URLTools.extractMarkDownAsString(MTGConstants.MTG_DESKTOP_WIKI_URL+mtg.getName()+".md"));
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	@Override
	public String getTitle() {
		return "Help";
	}

}
