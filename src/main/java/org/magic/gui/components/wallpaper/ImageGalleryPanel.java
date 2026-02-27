package org.magic.gui.components.wallpaper;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.magic.api.beans.MTGWallpaper;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.ImagePanel2;
import org.magic.gui.components.editor.JTagsPanel;
import org.magic.services.MTGConstants;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.MTG;

public class ImageGalleryPanel extends MTGUIComponent {
	
		private static final long serialVersionUID = 1L;
		private static final int THUMBNAIL_SIZE = 150;
		private boolean openingLargePic=true;
		private transient SwingWorker<Void, MTGWallpaper> sw2;
		private boolean multipleSelection;
		private MTGHttpClient client;
		
	    public ImageGalleryPanel(boolean openingLarge, boolean multiple)
	    {
	    	client = URLTools.newClient();
	    	this.openingLargePic = openingLarge;
	    	this.multipleSelection = multiple;
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
	    		sw2.cancel(false);
		}
		
		
		
	    public void init(List<MTGWallpaper> list) {
	    	
	    	onDestroy();
	    	
	    	clean();
	    	
	    	
	    	
	    	sw2 = new SwingWorker<Void, MTGWallpaper>()
			{

				@Override
				protected Void doInBackground() throws Exception {
					for(var wall : list)
					{
						if(isCancelled())
							break;
						try {		
							
							var b = RequestBuilder.build().setClient(client).get().url(wall.getUrlThumb().toASCIIString());
							
							wall.getHeaders().entrySet().forEach(e->b.addHeader(e.getKey(), e.getValue()));
							
							
							
							if(wall.isMature() && MTG.readPropertyAsBoolean("allow-nsfw")==false)
								wall.setPicture(ImageTools.fastBlur(b.toImage(),25,0.2));
							else
								wall.setPicture(b.toImage());
							
							
							publish(wall);
							}
							catch(Exception e)
							{
								logger.error("Exception for {} : {}",wall.getUrl(),e.getMessage());
							}
						
					}
					return null;
				}
				@Override
				protected void process(List<MTGWallpaper> chunks) {
						for(var img : chunks)
						{
							var thumb = new JWallThumb(img);
							thumb.setPreferredSize(new Dimension(THUMBNAIL_SIZE, THUMBNAIL_SIZE + 20));
							
							try {
			                    	    thumb.addMouseListener(new MouseAdapter() {
				                            @Override
				                            public void mouseClicked(MouseEvent e) {
				                            	if(e.getClickCount()==1)
				                            	{
				                            		if(!multipleSelection)
				                            		{
				                            			for (var comp : getComponents()){
				                    						var th = (JWallThumb) comp;
				                    						th.selected(false);
				                    					}
				                            		}
				                            		thumb.selected(!thumb.isSelected());
				                            	}
				                            	else if(e.getClickCount()==2 && openingLargePic)
				                            		showFullImage(img);
				                            }
				                        });
			        	        		thumb.setIcon(new ImageIcon(ImageTools.resize(img.getPicture(),THUMBNAIL_SIZE, THUMBNAIL_SIZE)));
			        	        		
			                } catch (Exception _) {
			                	thumb.setIcon(new ImageIcon(MTGConstants.NO_PIC));
			                	logger.error("Error getting image for {} at {}",img.getName(),img.getUrlThumb());
			                }
						      
	                        add(thumb);
	                        revalidate();
	                        repaint();
						}
					}
				@Override
				protected void done() {
					
					try {
						get();
					}
					catch(InterruptedException _)
					{
						Thread.currentThread().interrupt();
					
					} catch (ExecutionException e) {
						logger.error(e);
					}
					catch(CancellationException _)
					{
						//do nothign
					}
					
					
					
					
					getParent().revalidate();
				}
			};

			ThreadManager.getInstance().runInEdt(sw2,"loading thumbs");
	    	
	    }

	    private void showFullImage(MTGWallpaper wall) {
	  
	    		var pane = new ImagePanel2(false, false, true, true);
	    		var tags = new JTagsPanel();
	    				tags.setEditable(false);
	    				tags.setFontSize(9);
	    			
	    			
	    		var container = new JPanel();
	    			  container.setLayout(new BorderLayout());
	    			  container.add(pane,BorderLayout.CENTER);
	    			  container.add(tags,BorderLayout.SOUTH);
	    		
				var diag = MTGUIComponent.createJDialog(MTGUIComponent.build(container, wall.getName() + " By " + wall.getAuthor(), getIcon()), true, false);
				
				diag.setVisible(true);
				diag.setLocationRelativeTo(SwingUtilities.getRootPane(this));
				diag.setPreferredSize(new Dimension(1024,768));
				diag.setSize(new Dimension(1024,768));
				
				
				ThreadManager.getInstance().invokeLater(new MTGRunnable() {
					
					@Override
					protected void auditedRun() {
						BufferedImage img;
						try {
							
							var b = RequestBuilder.build().setClient(client).get().url(wall.getUrl().toASCIIString()).addHeader(URLTools.ACCEPT, "image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
							
							wall.getHeaders().entrySet().forEach(e->b.addHeader(e.getKey(), e.getValue()));
							
							wall.setPicture(b.toImage());
							
							img = wall.getPicture();
							pane.setImg(img);
							tags.bind(wall.getTags());
				    		pane.setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
							diag.revalidate();
						} catch (Exception e) {
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