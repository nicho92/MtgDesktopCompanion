package org.magic.gui.components.browser;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLEditorKit;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.magic.gui.abstracts.MTGUIBrowserComponent;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;
import org.magic.tools.URLToolsClient;


public class JEditorPaneBrowser extends MTGUIBrowserComponent {

	private static final long serialVersionUID = 1L;
	private JEditorPane browse;
	private transient URLToolsClient client;
	private String currentUrl="";
	
	
	public JEditorPaneBrowser() {
		setLayout(new BorderLayout());
		browse = new JEditorPane() ;
		browse.setContentType(URLTools.HEADER_HTML);
		var kit = new HTMLEditorKit();
		browse.setEditorKit(kit);
		browse.setEditable(false);
		add(browse,BorderLayout.CENTER);
		client = URLTools.newClient();
		
	}
	
	@Override
	public String getCurrentURL() {
		return currentUrl;
	}
	
	
	@Override
	public void loadURL(String url) {
		logger.debug("loading " + url);
		currentUrl=url;
		ThreadManager.getInstance().executeThread(()->{
				try {
					
					var w = Safelist.basic();
					w.addTags("img");
					w.addAttributes("img", "src");
					
					String contf = Jsoup.clean(RequestBuilder.build().clean().url(url).method(METHOD.GET).setClient(client).toHtml().html(),w);
					browse.setText(contf);
				}
				catch(Exception e)
				{
					logger.error(e);
					browse.setText("Error " + e);
				}
				
		}, "loading " + url);
		
		
		
	}
	
}
