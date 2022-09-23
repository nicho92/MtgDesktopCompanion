package org.magic.gui.components.browser;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.SwingWorker;
import javax.swing.text.html.HTMLEditorKit;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.magic.gui.abstracts.MTGUIBrowserComponent;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.RequestBuilder.METHOD;
import org.magic.services.network.URLTools;
import org.magic.services.threads.ThreadManager;


public class JEditorPaneBrowser extends MTGUIBrowserComponent {

	private static final long serialVersionUID = 1L;
	private JEditorPane browse;
	private transient MTGHttpClient client;
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
		logger.debug("loading {}",url);
		currentUrl=url;
		var sw = new SwingWorker<String,Void>()
				{

					@Override
					protected String doInBackground() throws Exception {
						var w = Safelist.basic();
						w.addTags("img");
						w.addAttributes("img", "src");

						return Jsoup.clean(RequestBuilder.build().clean().url(url).method(METHOD.GET).setClient(client).toHtml().html(),w);
					}

					@Override
					protected void done() {
						try {
							browse.setText(get());
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						} catch (Exception e) {
							browse.setText(e.getMessage());
						}
					}
				};

		ThreadManager.getInstance().runInEdt(sw, "loading text from " + url);


	}

}
