package org.magic.api.main;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.plaf.FontUIResource;

import org.apache.log4j.Logger;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGServer;
import org.magic.gui.MagicGUI;
import org.magic.gui.components.dialog.MTGSplashScreen;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;
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

			logger.debug("result config updated : " + updated);

			if (updated)
				MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getCapitalize("NEW"), MTGControler.getInstance().getLangService().getCapitalize("NEW_MODULE_INSTALLED"), MESSAGE_TYPE.INFO));
			
			MTGLogger.changeLevel(MTGControler.getInstance().get("loglevel"));
			MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
			MTGControler.getInstance().getEnabled(MTGDao.class).init();
			MTGControler.getInstance().getEnabled(MTGCardsProvider.class).loadEditions();
		
			
			logger.info("Init MTG Desktop Companion GUI");
		} catch (Exception e) {
			logger.error("Error initialisation", e);
			JOptionPane.showMessageDialog(null, e, MTGControler.getInstance().getLangService().getError(),JOptionPane.ERROR_MESSAGE);
		}

		ThreadManager.getInstance().invokeLater(() -> {

			MagicGUI gui = new MagicGUI();
			MTGControler.getInstance().getLafService().setFont(new FontUIResource(MTGConstants.FONT));
			MTGControler.getInstance().getLafService().setLookAndFeel(gui,MTGControler.getInstance().get("lookAndFeel"),false);
			gui.setExtendedState(JFrame.MAXIMIZED_BOTH);
			gui.setVisible(true);
			
			MTGControler.getInstance().cleaning();
			
			launch.stop();
			
			MTGControler.getInstance().listEnabled(MTGServer.class).stream().filter(MTGServer::isAutostart).forEach(s->{
				try {
					s.start();
				} catch (IOException e) {
					logger.error(e);
				}
			});
			long time = chrono.stop();
			logger.info(MTGConstants.MTG_APP_NAME + " started in " + time + " sec");
		});
	}

}
