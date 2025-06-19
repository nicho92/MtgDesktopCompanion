package org.magic.gui.components.dialog.importer;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JComponent;

import org.magic.api.beans.MTGWallpaper;
import org.magic.gui.abstracts.AbstractDelegatedImporterDialog;
import org.magic.gui.components.wallpaper.ImageGalleryPanel;

public class WallPaperChooseDialog extends AbstractDelegatedImporterDialog<MTGWallpaper>{

	private static final long serialVersionUID = 1L;
	private ImageGalleryPanel 	panel ;

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
