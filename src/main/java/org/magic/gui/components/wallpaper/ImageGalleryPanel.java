package org.magic.gui.components.wallpaper;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import org.magic.api.beans.MTGWallpaper;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.ImagePanel2;
import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.ImageTools;

public class ImageGalleryPanel extends MTGUIComponent {
	
		private static final long serialVersionUID = 1L;
		private static final int THUMBNAIL_SIZE = 150;
		private boolean openingLargePic=true;
		private SwingWorker<Void, MTGWallpaper> sw2;
		
		
	    public ImageGalleryPanel(boolean openingLarge, boolean multiple)
	    {
	    	this.openingLargePic = openingLarge;
	    	setLayout(new WrapLayout(FlowLayout.LEFT, 10, 10));
	    }
	    
		public List<MTGWallpaper> getSelected() {
			
			var ret = new ArrayList<MTGWallpaper>();
			
			for (var comp : getComponents()) 
			{
				var th = (JWallThumb) comp;
				if (th.isSelected())
					ret.add(th.getWallpaper());
			}
			
			return ret;
		}
		
		@Override
		public void onDestroy() {
			if(sw2!=null && !sw2.isCancelled())
	    		sw2.cancel(true);
		}
		
		
		
	    public void init(List<MTGWallpaper> list) {
	    	
	    	if(sw2!=null && !sw2.isCancelled())
	    		sw2.cancel(true);
	    	
	    	
	    	clean();
	    	
	    	sw2 = new SwingWorker<Void, MTGWallpaper>()
			{

				@Override
				protected Void doInBackground() throws Exception {
					for(var wall : list)
					{
						wall.setPicture(URLTools.extractAsImage(wall.getUrlThumb().toASCIIString()));
						publish(wall);
					}
					return null;
				}
				@Override
				protected void process(List<MTGWallpaper> chunks) {
						for(var img : chunks)
						{
							var thumb = new JWallThumb(img);
							try {
			                    		
			        	        		thumb.setPreferredSize(new Dimension(THUMBNAIL_SIZE, THUMBNAIL_SIZE + 20));
			        	        		thumb.setIcon(new ImageIcon(ImageTools.resize(img.getPicture(),THUMBNAIL_SIZE, THUMBNAIL_SIZE)));
				                        thumb.addMouseListener(new MouseAdapter() {
				                            @Override
				                            public void mouseClicked(MouseEvent e) {
				                            	if(e.getClickCount()==1)
				                            		thumb.selected(!thumb.isSelected());
				                            	else if(e.getClickCount()==2 && openingLargePic)
				                            		showFullImage(img);
				                            }
				                        });
				                        
				                        add(thumb);
				                        revalidate();
				                        repaint();
			                } catch (Exception e) {
			                	remove(thumb);
			                	logger.error(e);
			                }
						}
				}
				@Override
				protected void done() {
					getParent().revalidate();
				}
			};

			ThreadManager.getInstance().runInEdt(sw2,"loading thumbs");
	    	
	    }

	    private void showFullImage(MTGWallpaper wall) {
	  
	    		var pane = new ImagePanel2(false, false, true, true);
	    	
	    		
				var diag = MTGUIComponent.createJDialog(MTGUIComponent.build(pane, wall.getName() + " By " + wall.getAuthor(), getIcon()), true, false);
				diag.setLocationRelativeTo(null);
				diag.setVisible(true);
				
				ThreadManager.getInstance().invokeLater(new MTGRunnable() {
					
					@Override
					protected void auditedRun() {
						BufferedImage img;
						try {
							img = URLTools.extractAsImage(wall.getUrl().toASCIIString());
							pane.setImg(img);
				    		pane.setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
				    		diag.pack();
						} catch (IOException e) {
							logger.error(e);
						}
					}
				}, "load pic");
		
	    }
	    
	    @Override
	    public ImageIcon getIcon() {
		    return MTGConstants.ICON_TAB_PICTURE;
	    }
	    

		@Override
		public String getTitle() {
			return "THUMBNAIL";
		}

		public void clean() {
			removeAll();
			revalidate();
			
		}

}