package org.magic.gui;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.listPlugins;

import java.awt.GridLayout;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import org.magic.api.interfaces.MTGServer;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.tech.ServerStatePanel;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
public class ServersGUI extends MTGUIComponent {

	private static final long serialVersionUID = 1L;

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_ACTIVESERVER;
	}

	@Override
	public String getTitle() {
		return capitalize("ACTIVE_SERVERS");
	}


	public ServersGUI() {
		var sw = new SwingWorker<List<MTGServer>, Void>() {
			@Override
			protected List<MTGServer> doInBackground() throws Exception {
				return listPlugins(MTGServer.class);
			}
			
			@Override
			protected void done() {
				try {
					setLayout(new GridLayout(get().size(), 1, 0, 0));
					
					for(var s : get())
					{
						add(new ServerStatePanel(s));
					}
					
					
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					logger.error(e);
				}

			}
			
		};
		
		ThreadManager.getInstance().runInEdt(sw, "Adding server panels");


	}
}
