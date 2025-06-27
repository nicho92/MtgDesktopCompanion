package org.magic.gui.components.dialog.importer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.magic.api.beans.MTGWallpaper;
import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.MTGWallpaperProvider;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.AbstractDelegatedImporterDialog;
import org.magic.gui.components.wallpaper.ImageGalleryPanel;
import org.magic.services.MTGControler;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class WallPaperChooseDialog extends AbstractDelegatedImporterDialog<MTGWallpaper>{

	private static final long serialVersionUID = 1L;
	private ImageGalleryPanel 	panel ;
	private AbstractBuzyIndicatorComponent buzy;
	
	public WallPaperChooseDialog() {
		super();
		setPreferredSize(new Dimension(1024, 768));
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		var text = new JTextField(20);
				
		getContentPane().add(UITools.createFlowCenterPanel(text,buzy),BorderLayout.NORTH);	
		
		text.addActionListener(_->{
			var ret = MTG.listEnabledPlugins(MTGWallpaperProvider.class).stream().flatMap(p->p.search(text.getText()).stream()).toList();
			if(ret.isEmpty())
			{
				MTGControler.getInstance().notify(new MTGNotification("Search", "No Results", MESSAGE_TYPE.ERROR));
				return;
			}
			panel.init(ret);
		});
		
	}
	
	@Override
	public void onDestroy() {
		if(panel!=null)
			panel.onDestroy();
	}
	
	
	
	@Override
	public JComponent getSelectComponent() {
		var scroll = new JScrollPane(getGalleryPanel(),JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		return scroll;
	}
	
	private ImageGalleryPanel getGalleryPanel() {
		panel = new  ImageGalleryPanel(false,false);
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
