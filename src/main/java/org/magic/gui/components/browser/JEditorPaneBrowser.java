package org.magic.gui.components.browser;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;

import org.magic.gui.abstracts.MTGUIBrowserComponent;
import org.magic.services.ThreadManager;
import org.magic.tools.URLTools;


public class JEditorPaneBrowser extends MTGUIBrowserComponent {

	private static final long serialVersionUID = 1L;
	private JEditorPane browse;
	
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
		
		ThreadManager.getInstance().executeThread(()->{
				try {
					browse.setText(URLTools.extractHtml(url).html());
					browse.setCaretPosition(0);
				}
				catch(Exception e)
				{
					browse.setText("Error " + e);
				}
				
		}, "loading " + url);
		
		
		
	}
	
}
