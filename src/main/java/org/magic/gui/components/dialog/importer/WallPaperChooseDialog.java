package org.magic.gui.components.dialog.importer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import org.magic.api.beans.MTGWallpaper;
import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.MTGWallpaperProvider;
import org.magic.gui.abstracts.AbstractDelegatedImporterDialog;
import org.magic.gui.components.wallpaper.ImageGalleryPanel;
import org.magic.services.MTGControler;
import org.magic.services.tools.MTG;

public class WallPaperChooseDialog extends AbstractDelegatedImporterDialog<MTGWallpaper>{

	private static final long serialVersionUID = 1L;
	private ImageGalleryPanel 	panel ;
	
	public WallPaperChooseDialog() {
		super();
		setPreferredSize(new Dimension(1024, 768));
		var text = new JTextField(20);
				
		getContentPane().add(text,BorderLayout.NORTH);	
		
		text.addActionListener(_->{
			var ret = MTG.listEnabledPlugins(MTGWallpaperProvider.class).stream().flatMap(p->{
				try {
				return p.search(text.getText().trim()).stream();
				}
				catch(Exception _)
				{
					return Stream.empty();
				}
				
			}).collect(Collectors.toList());
			
			
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
		var scroll = new JScrollPane(getGalleryPanel(),ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
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
