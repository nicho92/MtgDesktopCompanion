package org.magic.gui;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.listPlugins;

import java.awt.BorderLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.apache.commons.io.FileUtils;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.FileTools;

import com.jidesoft.swing.JideTabbedPane;
import com.jidesoft.swing.TabEditingEvent;
import com.jidesoft.swing.TabEditingListener;

public class DashBoardGUI2 extends MTGUIComponent {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JideTabbedPane tabbedPane;

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_DASHBOARD;
	}

	@Override
	public String getTitle() {
		return capitalize("DASHBOARD_MODULE");
	}


	private void initNewDashBoardContainer(String name)
	{
		JMenuItem mntmSaveDisplay;
		var desktop = new JDesktopPane();


		tabbedPane.addTab(name,desktop);
		var menuBar = new JMenuBar();
		var mnNewMenu = new JMenu(capitalize("ADD"));
		var mnWindow = new JMenu(capitalize("WINDOW"));
		mntmSaveDisplay = new JMenuItem(capitalize("SAVE_DISPLAY"));
		desktop.setBackground(SystemColor.activeCaption);
		menuBar.setBounds(0, 0, 120, 21);
		menuBar.add(mnNewMenu);
		menuBar.add(mnWindow);
		mnWindow.add(mntmSaveDisplay);
		desktop.add(menuBar);


		initNewDashletAction(mnNewMenu,desktop);
		initSaveDisplayAction(mntmSaveDisplay,desktop,name);

		SwingWorker<Void, File> sw = new SwingWorker<>()
		{
			protected Void doInBackground() throws Exception {
				publish(new File(AbstractJDashlet.confdir,name).listFiles(File::isFile));
				return null;
			}

			@Override
			protected void process(List<File> chunks) {
				for (File f : chunks) {
					try (var fis = new FileInputStream(f)){
						var p = new Properties();
						p.load(fis);
						AbstractJDashlet dash = PluginRegistry.inst().newInstance(p.get("class").toString());
						dash.setProperties(p);
						addDash(desktop,dash);

					} catch (Exception e) {
						logger.error("Could not add {}",f, e);
					}
				}
			}
		};

		ThreadManager.getInstance().runInEdt(sw, "Loading dashlets");
	}

	private void initNewDashletAction(JMenu mnNewMenu,JDesktopPane desktop) {
		try {

			List<AbstractJDashlet> dashs = listPlugins(AbstractJDashlet.class);

			dashs.stream().map(AbstractJDashlet::getCategory).distinct().forEach(n->{
				var mntmCategory = new JMenu(n);

				for (AbstractJDashlet dash : dashs.stream().filter(d->d.getCategory().equalsIgnoreCase(n)).toList())
				{
					var mntmNewMenuItem = new JMenuItem(dash.getName(),dash.getIcon());
						mntmNewMenuItem.addActionListener(e -> {
							try {
								addDash(desktop,PluginRegistry.inst().newInstance(dash.getClass()));
							} catch (Exception ex) {
								logger.error("Error Loading {}", dash, ex);
							}
						});
					mntmCategory.add(mntmNewMenuItem);

				}
				mnNewMenu.add(mntmCategory);
			});

		} catch (Exception ex) {
			logger.error("Error", ex);
		}

	}

	public DashBoardGUI2() {
		setLayout(new BorderLayout());

		tabbedPane = new JideTabbedPane();
		tabbedPane.setShowCloseButtonOnTab(true);
		tabbedPane.setTabEditingAllowed(true);


		var popup = new JPopupMenu();
		var mnuAdd = new JMenuItem("New dashboard",MTGConstants.ICON_NEW);

		mnuAdd.addActionListener(al->{
			var defaultName="DashBoard-" + (tabbedPane.getTabCount()+1);
			var dir = new File(AbstractJDashlet.confdir,defaultName);

			try {
				FileUtils.forceMkdir(dir);
				initNewDashBoardContainer(defaultName);
			} catch (IOException e) {
				MTGControler.getInstance().notify(e);
			}
		});


		popup.add(mnuAdd);


		tabbedPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mev) {
				if(SwingUtilities.isRightMouseButton(mev))
					popup.show(mev.getComponent(), mev.getX(),mev.getY());
			}
		});


		tabbedPane.setCloseAction(new AbstractAction("Close") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				String name = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
				tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());

				try {
					FileUtils.deleteDirectory(new File(AbstractJDashlet.confdir,name));
				} catch (IOException e1) {
					logger.error("Error delete directory {}",name,e1);
				}

			}
		});

		tabbedPane.addTabEditingListener(new TabEditingListener() {

			@Override
			public void editingStopped(TabEditingEvent e) {
				var f = new File(AbstractJDashlet.confdir,e.getOldTitle());
				boolean res = f.renameTo(new File(AbstractJDashlet.confdir,e.getNewTitle()));
				logger.debug("Renaming dashbord {} to {} : {} ",e.getOldTitle(),e.getNewTitle(),res);
			}

			@Override
			public void editingStarted(TabEditingEvent e) {
				//do nothing
			}

			@Override
			public void editingCanceled(TabEditingEvent e) {
				//do nothing
			}
		});


		add(tabbedPane,BorderLayout.CENTER);

	}

	@Override
	public void onFirstShowing() {
		File[] dirs = AbstractJDashlet.confdir.listFiles(File::isDirectory);

		if(dirs.length>0)
		{
			for(File dir : dirs)
				initNewDashBoardContainer(dir.getName());
		}

	}

	private void initSaveDisplayAction(JMenuItem mntmSaveDisplay, JDesktopPane desktop,String name) {
		mntmSaveDisplay.addActionListener(ae -> {
			var i = 0;

			var dir = new File(AbstractJDashlet.confdir,name);

			try {
				FileUtils.cleanDirectory(dir);
			} catch (Exception e1) {
				logger.error(e1);
			}


			for (JInternalFrame jif : desktop.getAllFrames()) {
				i++;
				var dash = (AbstractJDashlet) jif;
				dash.setProperty("x", String.valueOf(dash.getBounds().getX()));
				dash.setProperty("y", String.valueOf(dash.getBounds().getY()));
				dash.setProperty("w", String.valueOf(dash.getBounds().getWidth()));
				dash.setProperty("h", String.valueOf(dash.getBounds().getHeight()));
				dash.setProperty("class", dash.getClass().getName());
				dash.setProperty("id", String.valueOf(i));
				var f = new File(dir, i + ".conf");

				try (var fos = new FileOutputStream(f)) {
					dash.getProperties().store(fos, "");
					logger.trace("saving {}:{}",f,dash.getProperties());

				} catch (IOException e) {
					logger.error(e);
				}
			}

	});

	}

	private void addDash(JDesktopPane desktop, AbstractJDashlet dash) {
			try {
				logger.debug("loading {}",dash.getName());
				dash.initGUI();
				desktop.add(dash);
				dash.init();
				dash.setVisible(true);
				dash.addInternalFrameListener(new InternalFrameAdapter() {
					@Override
					public void internalFrameClosed(InternalFrameEvent e) {
						AbstractJDashlet dash = (AbstractJDashlet) e.getInternalFrame();

						dash.onDestroy();
						if (dash.getProperties().get("id") != null)
						{

							var tab= tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());

							var conf = Paths.get(AbstractJDashlet.confdir.getAbsolutePath(),tab, dash.getProperties().get("id") + ".conf").toFile();
							try {
								FileTools.deleteFile(conf);
							} catch (IOException e1) {
								logger.error("error removing {}",conf);
							}
						}
					}
				});

			} catch (Exception e) {
				logger.error("error adding {}",dash,e);
				MTGControler.getInstance().notify(e);
			}
	}

}
