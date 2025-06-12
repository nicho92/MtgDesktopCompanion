package org.magic.gui;

import static org.magic.services.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.magic.api.beans.MTGWallpaper;
import org.magic.api.interfaces.MTGWallpaperProvider;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.wallpaper.ImageGalleryPanel;
import org.magic.gui.components.wallpaper.JWallThumb;
import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;
public class WallpaperGUI extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JComboBox<MTGWallpaperProvider> cboWallpapersProv;
	private transient MTGWallpaperProvider selectedProvider;
	private AbstractBuzyIndicatorComponent lblLoad;
	private ImageGalleryPanel panelThumnail;
	private JTextField txtSearch;
	private JButton btnImport;
	private JCheckBox chkSelectAll;

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_WALLPAPER;
	}

	@Override
	public String getTitle() {
		return capitalize("WALLPAPER");
	}

	public WallpaperGUI() {

		setLayout(new BorderLayout(0, 0));
		
		
		panelThumnail = new ImageGalleryPanel();
		var panel = new JPanel();
		chkSelectAll = new JCheckBox("Select All");
		cboWallpapersProv = UITools.createComboboxPlugins(MTGWallpaperProvider.class, false);
		selectedProvider = cboWallpapersProv.getItemAt(0);
		cboWallpapersProv.addActionListener(_ -> selectedProvider = (MTGWallpaperProvider) cboWallpapersProv.getSelectedItem());
		txtSearch = UITools.createSearchField();
		lblLoad = AbstractBuzyIndicatorComponent.createLabelComponent();
		var panel1 = new JPanel();
		
		var scroll = new JScrollPane(panelThumnail,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		
		
		add(scroll, BorderLayout.CENTER);
		add(panel, BorderLayout.NORTH);
		add(panel1, BorderLayout.SOUTH);
		
		
		panel.add(cboWallpapersProv);
		panel.add(txtSearch);
		txtSearch.setColumns(20);
		txtSearch.addActionListener(_ ->{
			panelThumnail.removeAll();
			panelThumnail.revalidate();
			lblLoad.start();

			var sw = new SwingWorker<List<MTGWallpaper>, MTGWallpaper>() {

				@Override
				protected List<MTGWallpaper> doInBackground() throws Exception {
					return selectedProvider.search(txtSearch.getText());
				}

				@Override
				protected void done() {
					lblLoad.end();
					
					try {
						panelThumnail.init(get());
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					} catch (ExecutionException e) {
						logger.error(e);
					}
					
				}
			};
			ThreadManager.getInstance().runInEdt(sw,"searching " + txtSearch.getText());
		});

		
		panel.add(lblLoad);

		
		

		btnImport = UITools.createBindableJButton(null,MTGConstants.ICON_IMPORT,KeyEvent.VK_I,"wallpaper import");
		btnImport.setToolTipText(capitalize("IMPORT"));
		panel1.add(chkSelectAll);
		panel1.add(btnImport);


		btnImport.addActionListener(_ ->{
			
			lblLoad.start();
			var sw =  new SwingWorker<Void, Void>()
			{
				@Override
				protected Void doInBackground() throws Exception {
					for (var comp : panelThumnail.getComponents()) 
					{
						var th = (JWallThumb) comp;

						if (th.isSelected() || chkSelectAll.isSelected()) 
						{
							try {

									if (!MTGConstants.MTG_WALLPAPER_DIRECTORY.exists())
										MTGConstants.MTG_WALLPAPER_DIRECTORY.mkdir();
											
								URLTools.download(th.getWallpaper().getUrl().toASCIIString(), new File(MTGConstants.MTG_WALLPAPER_DIRECTORY, th.getWallpaper().getName() + "." + th.getWallpaper().getFormat()));

								th.selected(false);
							} catch (IOException e1) {
								logger.error(e1);
							}
						}
					}
					return null;
				}

				@Override
				protected void done() {
					lblLoad.end();
				}
			};
			
			ThreadManager.getInstance().runInEdt(sw, "Saving wallpapers");
		});
	}

}


