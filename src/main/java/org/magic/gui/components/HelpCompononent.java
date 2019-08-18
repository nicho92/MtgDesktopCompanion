package org.magic.gui.components;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.jsoup.nodes.Document;
import org.magic.api.beans.MTGDocumentation;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.ThreadManager;
import org.magic.tools.URLTools;

public class HelpCompononent extends MTGUIComponent {

	private static final long serialVersionUID = 1L;

	private JEditorPane pane;

	private transient MTGPlugin plug;
	
	public HelpCompononent() {
		pane = new JEditorPane();
		pane.setContentType("text/html");
		setLayout(new BorderLayout());
		pane.setEditable(false);
		add(new JScrollPane(pane),BorderLayout.CENTER);
	}
	
	public void init(MTGPlugin mtg)
	{
		this.plug = mtg;
		
		loadDoc();
			
	}
	
	@Override
	public void onFirstShowing() {
		if(plug!=null)
			onVisible();
	}
	
	
	@Override
	public void onVisible() {
		if(plug!=null)
			loadDoc();
	}
	
	
	private void loadDoc()
	{
		
		if(isVisible())
			ThreadManager.getInstance().runInEdt(new SwingWorker<Document,Void>() {
				@Override
				protected void done() {
					try {
						
						Document d= get();
						int width = (int)getSize().getWidth();
						
						if(width<=0)
							width=450;
						
						d.select("img").attr("width", String.valueOf(width));
						pane.setText(d.html());
					} catch (Exception e) {
						logger.error("error loading help",e);
						pane.setText(e.getLocalizedMessage());
					} 
				}
				
				@Override
				protected Document doInBackground() throws Exception {
					if(plug.getDocumentation().getContentType().equals(FORMAT_NOTIFICATION.MARKDOWN))
						return URLTools.extractMarkDownAsDocument(plug.getDocumentation().getUrl());
					else
						return URLTools.extractHtml(plug.getDocumentation().getUrl());
				}
			}, "Loading doc for "+ plug);
	}
	
	
	@Override
	public String getTitle() {
		return "Help";
	}

}
