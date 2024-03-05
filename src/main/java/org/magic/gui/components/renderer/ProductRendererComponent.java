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
import javax.swing.border.LineBorder;

import org.apache.logging.log4j.Logger;
import org.magic.api.interfaces.MTGProduct;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;


public class ProductRendererComponent extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel lblProductName;
	private JLabel lblProductSet;
	private JLabel lblProductType;
	private JLabel lblImage;
	protected transient Logger logger = MTGLogger.getLogger(getClass());

	
	private Map<Long, BufferedImage> imageCache;
	
	
	
	public ProductRendererComponent() {
		initGUI();
		
	}

	public ProductRendererComponent(MTGProduct mc) {
		initGUI();

		if(mc!=null)
			init(mc);
	}


	private void initGUI() {
		
		imageCache =  new ConcurrentHashMap<>();
		
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

		var img = imageCache.computeIfAbsent(p.getProductId(),i->{
			try {
				return URLTools.extractAsImage(p.getUrl());
			} catch (Exception e) {
				return new BufferedImage(110, 150, Image.SCALE_FAST);
			}
		});

		lblImage.setIcon(new ImageIcon(img.getScaledInstance(110, 150, Image.SCALE_FAST)));	
	
		
	}
	
}
