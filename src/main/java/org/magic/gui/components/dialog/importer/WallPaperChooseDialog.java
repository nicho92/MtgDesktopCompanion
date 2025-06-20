package org.magic.gui.components.dialog.importer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.magic.api.beans.MTGWallpaper;
import org.magic.api.interfaces.MTGWallpaperProvider;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.AbstractDelegatedImporterDialog;
import org.magic.gui.components.wallpaper.ImageGalleryPanel;
import org.magic.services.MTGConstants;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class WallPaperChooseDialog extends AbstractDelegatedImporterDialog<MTGWallpaper>{

	private static final long serialVersionUID = 1L;
	private ImageGalleryPanel 	panel ;
	private AbstractBuzyIndicatorComponent buzy;
	
	public WallPaperChooseDialog() {
		super();
		
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		var text = new JTextField(20);
		var btn  = UITools.createBindableJButton("", MTGConstants.ICON_SEARCH, KeyEvent.VK_S, "searchwalldialog");
		
		getContentPane().add(UITools.createFlowCenterPanel(text,btn,buzy),BorderLayout.NORTH);	
		
		btn.addActionListener(_->{
			var ret = MTG.listEnabledPlugins(MTGWallpaperProvider.class).stream().flatMap(p->p.search(text.getText()).stream()).toList();
			panel.init(ret);
		});
		
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
