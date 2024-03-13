package org.beta;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import org.magic.api.interfaces.MTGExternalShop;
import org.magic.api.interfaces.MTGProduct;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.network.URLTools;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;
import org.magic.services.workers.AbstractObservableWorker;

public class LazyImageLoadingCellRendererTest
{
	
	public static void main(String[] args) throws SQLException, IOException
	    {
	        MTGControler.getInstance().init();
	        
	       
	       
            
	        EventQueue.invokeLater(()->
	            {
	                var frame = new JFrame("WorkerTest");
	                var pane = new JPanel();
	                var model = new DefaultListModel<MTGProduct>();
	                var list = new JList<MTGProduct>(model);

	                var sw = new AbstractObservableWorker<List<MTGProduct>,MTGProduct,MTGExternalShop>(MTG.getEnabledPlugin(MTGExternalShop.class)){

	    				@Override
	    				protected List<MTGProduct> doInBackground() throws Exception {
	    					return plug.listProducts("Zendikar");
	    				} 
	        			
	    				@Override
	    				protected void process(List<MTGProduct> chunks) {
	    					for(var p : chunks)
	    						model.addElement(p);
	    				}
	    				
	    			
	                    	
	    			};
	                
	                ThreadManager.getInstance().runInEdt(sw, "searching");

	                pane.setLayout(new BorderLayout());
	                list.setCellRenderer(new LazyImageLoadingCellRenderer(list));
	                pane.add(new JScrollPane(list),BorderLayout.CENTER);
	                pane.setPreferredSize(new Dimension(1024,768));
	             
	                frame.setContentPane(pane);
					frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	                frame.setVisible(true);
	                frame.pack();
	        });
	    }
	
  

   
}

class LazyImageLoadingCellRenderer extends JLabel  implements ListCellRenderer<MTGProduct>
{
    private static final long serialVersionUID = 1L;
	private final JList<?> owner;
    private final transient Map<MTGProduct, BufferedImage> loadedImages;
    
    
    public LazyImageLoadingCellRenderer(JList<?> owner)
    {
        this.owner = Objects.requireNonNull(owner, "The owner may not be null");
        this.loadedImages = new ConcurrentHashMap<>();
        setOpaque(false);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends MTGProduct> list, MTGProduct value, int index, boolean isSelected, boolean cellHasFocus)
    {
        var image = loadedImages.get(value);
        if (image == null)
        {
        	var sw = new SwingWorker<BufferedImage, Void>()
        	{
        	        @Override
        	        protected BufferedImage doInBackground() throws Exception
        	        {
        	            try
        	            {
        	                BufferedImage image = URLTools.extractAsImage(value.getUrl());
        	                loadedImages.put(value, image);
        	                 return image;
        	            }
        	            catch (Exception e)
        	            {
        	               
        	                return null;
        	            }
        	        }

        	        @Override
        	        protected void done()
        	        {
        	            owner.repaint();
        	        }
        	        
        	};
        	
        	ThreadManager.getInstance().runInEdt(sw, "loading");
        	
        	
           setIcon(MTGConstants.ICON_LOADING);
        }
        else
        {

            setIcon(new ImageIcon(image));
        }
        return this;
    }
    
   

	
}


