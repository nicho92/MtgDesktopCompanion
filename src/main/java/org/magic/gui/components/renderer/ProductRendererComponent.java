package org.magic.gui.components.renderer;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;

import org.apache.logging.log4j.Logger;
import org.magic.api.interfaces.MTGProduct;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.network.URLTools;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;


public class ProductRendererComponent extends JPanel {

	private static final long serialVersionUID = 1L;
	protected JLabel lblProductName;
	protected JLabel lblProductSet;
	protected JLabel lblProductType;
	protected JLabel lblImage;
	protected transient Logger logger = MTGLogger.getLogger(getClass());

	
	private transient Map<MTGProduct, BufferedImage> loadedImages;
	
	
	
	public ProductRendererComponent() {
		initGUI();
		
	}

	public ProductRendererComponent(MTGProduct mc) {
		initGUI();

		if(mc!=null)
			init(mc);
	}


	private void initGUI() {
		
		loadedImages =  new ConcurrentHashMap<>();
		
		setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{90, 267, 0};
		gridBagLayout.rowHeights = new int[]{43, 38, 36, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		lblImage = new JLabel(MTGConstants.ICON_LOADING);
		add(lblImage, UITools.createGridBagConstraints(null,null,0, 0,null,3));

		lblProductName = new JLabel("");
		add(lblProductName, UITools.createGridBagConstraints(GridBagConstraints.WEST,null,1, 0));

		lblProductSet = new JLabel("");
		add(lblProductSet, UITools.createGridBagConstraints(GridBagConstraints.WEST,null,1, 1));

		lblProductType = new JLabel();
		add(lblProductType, UITools.createGridBagConstraints(GridBagConstraints.WEST,null,1, 2));

		
	

		
	}

	public void init(MTGProduct p) {

		if(p==null)
			return;
		
		lblProductName.setText(p.getName());
		if(p.getEdition()!=null)
			lblProductSet.setText(p.getEdition().getSet());

		if(p.getCategory()!=null)
			lblProductType.setText(p.getCategory().getCategoryName()+" ("+p.getProductId() +")");
		
		 var image = loadedImages.get(p);
        if (image == null)
        {
        	var sw = new SwingWorker<BufferedImage, Void>()
        	{
        	        @Override
        	        protected BufferedImage doInBackground() throws Exception
        	        {
        	            try
        	            {
        	                var image = URLTools.extractAsImage(p.getUrl());
        	                loadedImages.put(p, image);
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
        	        	//list.repaint();
        	        }
        	        
        	};
        	
        	ThreadManager.getInstance().runInEdt(sw, "loading");
        	lblImage.setIcon(MTGConstants.ICON_LOADING);
        }
        else
        {
        	   	lblImage.setIcon(new ImageIcon(image.getScaledInstance(110, 150, Image.SCALE_FAST)));
        }
	
		
	}
	
}
