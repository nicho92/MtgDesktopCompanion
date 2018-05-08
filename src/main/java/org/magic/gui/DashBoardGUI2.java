package org.magic.gui;

import java.awt.SystemColor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class DashBoardGUI2 extends JDesktopPane {

	private transient ClassLoader classLoader = DashBoardGUI2.class.getClassLoader();
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public DashBoardGUI2() {
		setBackground(SystemColor.activeCaption);

		logger.info("init Dashboard GUI");

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 84, 21);

		JMenu mnNewMenu = new JMenu(MTGControler.getInstance().getLangService().getCapitalize("ADD"));
		menuBar.add(mnNewMenu);

		JMenu mnWindow = new JMenu(MTGControler.getInstance().getLangService().getCapitalize("WINDOW"));
		menuBar.add(mnWindow);

		JMenuItem mntmSaveDisplay = new JMenuItem(
				MTGControler.getInstance().getLangService().getCapitalize("SAVE_DISPLAY"));
		mntmSaveDisplay.addActionListener(ae -> {
			int i = 0;

			try {
				FileUtils.cleanDirectory(AbstractJDashlet.confdir);
			} catch (IOException e1) {
				logger.error(e1);
			}

			for (JInternalFrame jif : getAllFrames()) {
				i++;
				AbstractJDashlet dash = (AbstractJDashlet) jif;
				dash.setProperty("x", String.valueOf(dash.getBounds().getX()));
				dash.setProperty("y", String.valueOf(dash.getBounds().getY()));
				dash.setProperty("w", String.valueOf(dash.getBounds().getWidth()));
				dash.setProperty("h", String.valueOf(dash.getBounds().getHeight()));
				dash.setProperty("class", dash.getClass().getName());
				dash.setProperty("id", String.valueOf(i));
				File f = new File(AbstractJDashlet.confdir, i + ".conf");

				try (FileOutputStream fos = new FileOutputStream(f)) {
					dash.getProperties().store(fos, "");
				} catch (IOException e) {
					logger.error(e);
				}
			}
		});

		mnWindow.add(mntmSaveDisplay);
		add(menuBar);

		try {
			for (AbstractJDashlet dash : MTGControler.getInstance().getDashlets()) {
				JMenuItem mntmNewMenuItem = new JMenuItem(dash.getName());
				mntmNewMenuItem.addActionListener(e -> {
					try {
						addDash(dash);
					} catch (Exception ex) {
						logger.error("Error Loading " + dash, ex);
					}
				});
				mnNewMenu.add(mntmNewMenuItem);
			}

		} catch (Exception e) {
			logger.error("Error", e);
		}

		if (!AbstractJDashlet.confdir.exists())
			AbstractJDashlet.confdir.mkdir();

		for (File f : AbstractJDashlet.confdir.listFiles()) {
			try {
				Properties p = new Properties();
				FileInputStream fis = new FileInputStream(f);
				p.load(fis);
				AbstractJDashlet dash = (AbstractJDashlet) classLoader.loadClass(p.get("class").toString())
						.getDeclaredConstructor().newInstance();
				dash.setProperties(p);
				fis.close();
				addDash(dash);

			} catch (Exception e) {
				logger.error("Could not add " + f, e);
			}
		}

	}

	public void addDash(AbstractJDashlet dash) {
		dash.initGUI();
		dash.init();
		add(dash);
	}

}
