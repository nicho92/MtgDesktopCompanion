package org.magic.gui.components.tech;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.apache.commons.lang3.SystemUtils;
import org.jsoup.nodes.Document;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.network.URLTools;
import org.magic.services.threads.ThreadManager;


public class HelpCompononent extends MTGUIComponent {

	private static final long serialVersionUID = 1L;

	private JEditorPane pane;

	private transient MTGPlugin plug;

	public HelpCompononent() {
		pane = new JEditorPane();
		pane.setContentType(URLTools.HEADER_HTML);
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
						pane.setText(d.html().replace("$USER_HOME", SystemUtils.USER_HOME));
					} catch(InterruptedException ex)
					{
						Thread.currentThread().interrupt();
					}catch (Exception e) {
						logger.error("error loading help",e);
						pane.setText(e.getLocalizedMessage());
					}
				}

				@Override
				protected Document doInBackground() throws Exception {
					if(plug.getDocumentation().getContentType().equals(FORMAT_NOTIFICATION.MARKDOWN))
						return URLTools.extractMarkdownAsHtml(plug.getDocumentation().getUrl().toString());
					else
						return URLTools.extractAsHtml(plug.getDocumentation().getUrl().toString());
				}
			}, "Loading doc for "+ plug);
	}


	@Override
	public String getTitle() {
		return "Help";
	}

}
