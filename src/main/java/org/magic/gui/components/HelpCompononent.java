package org.magic.gui.components;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.jsoup.nodes.Document;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.ThreadManager;
import org.magic.tools.URLTools;

public class HelpCompononent extends MTGUIComponent {

	private static final long serialVersionUID = 1L;

	private JEditorPane pane;
	
	public HelpCompononent() {
		pane = new JEditorPane();
		pane.setContentType("text/html");
		setLayout(new BorderLayout());
		pane.setEditable(false);
		add(new JScrollPane(pane),BorderLayout.CENTER);
	}
	
	public void init(MTGPlugin mtg)
	{
			SwingWorker<Document,Void> sw = new SwingWorker<>() {
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
					return URLTools.extractMarkDownAsDocument(MTGConstants.MTG_DESKTOP_WIKI_RAW_URL+"/"+mtg.getName().replaceAll(" ", "%20")+".md");
				}
			};
			
			ThreadManager.getInstance().runInEdt(sw, "loading help for " + mtg);
	}
	
	@Override
	public String getTitle() {
		return "Help";
	}

}
