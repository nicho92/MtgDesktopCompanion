package org.magic.gui.components.browser;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;

import org.magic.gui.abstracts.MTGUIBrowserComponent;


public class JEditorPaneBrowser extends MTGUIBrowserComponent {

	private JEditorPane browse;
	
	//sample https://github.com/flyingsaucerproject/flyingsaucer/blob/master/flying-saucer-examples/src/main/java/BrowsePanel.java
	public JEditorPaneBrowser() {
		setLayout(new BorderLayout());
		browse = new JEditorPane() ;
		browse.setContentType("text/html");
		browse.setEditable(false);
		add(browse,BorderLayout.CENTER);

		
	}
	
	
	@Override
	public void loadURL(String url) {
	
		logger.debug("loading " + url);
		try {
		browse.setPage(url);
		}
		catch(Exception e)
		{
			browse.setText("Error " + e);
		}
		
	}

	
}
