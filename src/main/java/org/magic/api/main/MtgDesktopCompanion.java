package org.magic.api.main;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
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
		chrono.start();

		launch = new MTGSplashScreen();
		MTGLogger.getMTGAppender().addObserver(launch);
		launch.start();

		try {
			boolean updated = MTGControler.getInstance().updateConfigMods();

			logger.debug("result config updated : " + updated);

			if (updated)
				MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getCapitalize("NEW"), MTGControler.getInstance().getLangService().getCapitalize("NEW_MODULE_INSTALLED"), MESSAGE_TYPE.INFO));
			
			MTGLogger.changeLevel(MTGControler.getInstance().get("loglevel"));
			MTGControler.getInstance().getEnabledCardsProviders().init();
			MTGControler.getInstance().getEnabledDAO().init();

			logger.info("Init MTG Desktop Companion GUI");
		} catch (Exception e) {
			logger.error("Error initialisation", e);
			JOptionPane.showMessageDialog(null, e, MTGControler.getInstance().getLangService().getError(),JOptionPane.ERROR_MESSAGE);
		}

		ThreadManager.getInstance().runInEdt(() -> {

			MagicGUI gui = new MagicGUI();
			MTGControler.getInstance().getLafService().setLookAndFeel(gui,
					MTGControler.getInstance().get("lookAndFeel"));
			gui.setExtendedState(JFrame.MAXIMIZED_BOTH);
			gui.setVisible(true);
			launch.stop();

			for (MTGServer serv : MTGControler.getInstance().getEnabledServers())
				if (serv.isAutostart())
					try {
						serv.start();
					} catch (Exception e) {
						logger.error(e);
					}

			long time = chrono.stop();
			logger.info(MTGConstants.MTG_APP_NAME + " started in " + time + " sec");
		});
	}

}
