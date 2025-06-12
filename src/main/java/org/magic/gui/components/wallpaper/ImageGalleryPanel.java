package org.magic.gui.components.wallpaper;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.magic.api.beans.MTGWallpaper;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.network.URLTools;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.ImageTools;

public class ImageGalleryPanel extends MTGUIComponent {
	
		private static final long serialVersionUID = 1L;
		private static final int THUMBNAIL_SIZE = 150;

	    public ImageGalleryPanel()
	    {
	    	setLayout(new WrapLayout(FlowLayout.LEFT, 10, 10));
	    }
	    
	    
	    public void init(List<MTGWallpaper> list) {
	    

	        for (var thumbItem : list) 
	        {
	        	
	        	var thumb = new JWallThumb(thumbItem);
	        	thumb.setPreferredSize(new Dimension(THUMBNAIL_SIZE, THUMBNAIL_SIZE + 20));
	        	thumb.setName("...");

	            add(thumb);

	            ThreadManager.getInstance().submitCallable(() -> {
	                try {
	                    
	                	var img = URLTools.extractAsImage(thumbItem.getUrlThumb().toASCIIString());
	                    if (img != null) 
	                    {
	                    		thumb.setIcon(new ImageIcon(ImageTools.resize(img,THUMBNAIL_SIZE, THUMBNAIL_SIZE)));
		                        thumb.addMouseListener(new MouseAdapter() {
		                            @Override
		                            public void mouseClicked(MouseEvent e) {
		                            	if(e.getClickCount()==1)
		                            		thumb.selected(!thumb.isSelected());
		                            	else if(e.getClickCount()==2)
		                            		showFullImage(thumbItem);
		                            }
		                        });
		                        return img;
	                    }
	                } catch (Exception e) {
	                	remove(thumb);
	                	logger.error(e);
	                }
					return null;
	            });
	        }
	    }

	    private void showFullImage(MTGWallpaper wall) {
	    	try {
				MTGUIComponent.createJDialog(MTGUIComponent.build(new JScrollPane(new JLabel(new ImageIcon(URLTools.extractAsImage(wall.getUrl().toASCIIString())))), wall.getName(), getIcon()), true, false).setVisible(true);
			} catch (IOException e) {
				logger.error(e);
			}
	    }


		@Override
		public String getTitle() {
			return "THUMBNAIL";
		}
}