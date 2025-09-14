package org.magic.gui.components.dialog.importer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGWallpaper;
import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.MTGWallpaperProvider;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.AbstractDelegatedImporterDialog;
import org.magic.gui.components.wallpaper.ImageGalleryPanel;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class WallPaperChooseDialog extends AbstractDelegatedImporterDialog<MTGWallpaper>{

	private static final long serialVersionUID = 1L;
	private ImageGalleryPanel 	panel ;
	private AbstractBuzyIndicatorComponent buzy;
	protected Logger logger = MTGLogger.getLogger(this.getClass());

	public WallPaperChooseDialog() {
		super();
		setPreferredSize(new Dimension(1024, 768));
		var text = new JTextField(30);
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		
		getContentPane().add(UITools.createFlowPanel(text,buzy),BorderLayout.NORTH);	
		
		text.addActionListener(_->{
			
			buzy.start();
			ThreadManager.getInstance().runInEdt(new SwingWorker<List<MTGWallpaper>, Void>(){

				@Override
				protected List<MTGWallpaper> doInBackground() throws Exception {
					return MTG.listEnabledPlugins(MTGWallpaperProvider.class).stream().flatMap(p->{
						try {
							return p.search(text.getText().trim()).stream();
							}
							catch(Exception _)
							{
								return Stream.empty();
							}
							
						}).collect(Collectors.toList());
				}

				@Override
				protected void done() {
					try {
						if(get().isEmpty())
						{
							MTGControler.getInstance().notify(new MTGNotification("Search", "No Results", MESSAGE_TYPE.ERROR));
							return;
						}
						
						panel.init(get());
						
					} catch (InterruptedException _) {
						Thread.currentThread().interrupt();
					} catch (ExecutionException e) {
						logger.error(e);
					}
				
					buzy.end();
					
				}
				
				
				
				
			}, "search wallpaper");
			
			
			
		
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
