package org.magic.api.main;

import static org.magic.tools.MTG.capitalize;
import static org.magic.tools.MTG.getEnabledPlugin;
import static org.magic.tools.MTG.listEnabledPlugins;

import java.awt.Frame;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.plaf.FontUIResource;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGServer;
import org.magic.gui.MagicGUI;
import org.magic.gui.components.dialog.MTGSplashScreen;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.Chrono;
public class MtgDesktopCompanion {

	private final Logger logger = MTGLogger.getLogger(this.getClass());
	private MTGSplashScreen launch;
	private Chrono chrono;

	public static void main(String[] args) {
		new MtgDesktopCompanion();
	}

	public MtgDesktopCompanion() {
		chrono = new Chrono();
		
		launch = new MTGSplashScreen();
		MTGLogger.getMTGAppender().addObserver(launch);
		
		
		launch.start();
		chrono.start();
		try {
			
			boolean updated = MTGControler.getInstance().updateConfigMods();

			
			MTGControler.getInstance().loadAccountsConfiguration();
			MTGLogger.changeLevel(MTGControler.getInstance().get("loglevel"));

	
			logger.trace("result config updated : {}",updated);

			if (updated)
				MTGControler.getInstance().notify(new MTGNotification(capitalize("NEW"), capitalize("NEW_MODULE_INSTALLED"), MESSAGE_TYPE.INFO));
				
			getEnabledPlugin(MTGCardsProvider.class).init();
			getEnabledPlugin(MTGDao.class).init();
			
			logger.info("Init {} GUI",MTGConstants.MTG_APP_NAME);
		} catch (Exception e) {
			logger.error("Error initialisation", e);
			JOptionPane.showMessageDialog(null, e, MTGControler.getInstance().getLangService().getError(),JOptionPane.ERROR_MESSAGE);
		}

		ThreadManager.getInstance().invokeLater(new MTGRunnable() {
			
			@Override
			protected void auditedRun() {
				var gui = new MagicGUI();
				MTGControler.getInstance().getLafService().setFont(new FontUIResource(MTGControler.getInstance().getFont()));
				MTGControler.getInstance().getLafService().setLookAndFeel(gui,MTGControler.getInstance().get("lookAndFeel"),false);
				gui.setExtendedState(Frame.MAXIMIZED_BOTH);
				gui.setVisible(true);
				
				MTGControler.getInstance().cleaning();
				
				launch.stop();
				
				listEnabledPlugins(MTGServer.class).stream().filter(MTGServer::isAutostart).forEach(s->{
					try {
						s.start();
					} catch (IOException e) {
						logger.error(e);
					}
				});
				long time = chrono.stop();
				logger.info("{} started in {} sec",MTGConstants.MTG_APP_NAME ,time);
				
			}
		}, "Running Main App GUI");
	}

}
