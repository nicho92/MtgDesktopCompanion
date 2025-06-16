package org.magic.gui.components.dialog.importer;

import java.awt.Dimension;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.magic.api.beans.MTGWallpaper;
import org.magic.api.interfaces.MTGWallpaperProvider;
import org.magic.gui.abstracts.AbstractDelegatedImporterDialog;
import org.magic.gui.components.wallpaper.ImageGalleryPanel;
import org.magic.services.MTGControler;
import org.magic.services.tools.MTG;

public class WallPaperChooseDialog extends AbstractDelegatedImporterDialog<MTGWallpaper>{

	private static final long serialVersionUID = 1L;
	private ImageGalleryPanel 	panel ;
	
	public static void main(String[] args) throws SQLException {
		MTGControler.getInstance().init();
		var text = JOptionPane.showInputDialog("search","");
		
		var ret = MTG.listEnabledPlugins(MTGWallpaperProvider.class).stream().flatMap(p->p.search(text).stream()).toList();
		
		var wallChooser = new WallPaperChooseDialog();
		wallChooser.getGalleryPanel().init(ret);
		wallChooser.setVisible(true);
	}
	
	@Override
	public JComponent getSelectComponent() {
		return getGalleryPanel();
	}
	
	public ImageGalleryPanel getGalleryPanel() {
		panel = new  ImageGalleryPanel(false);
		panel.setPreferredSize(new Dimension(1024, 768));
		return panel;
	}
	
	@Override
	public MTGWallpaper getSelectedItem() {
		try {
			return getSelectedItems().get(0);
		}
		catch(Exception _)
		{
			return null;
		}
	}
	
	
	@Override
	public List<MTGWallpaper> getSelectedItems() {
		return panel.getSelected();
	}
	
	

}
