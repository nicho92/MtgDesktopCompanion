package org.magic.gui.components.renderer;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.magic.api.interfaces.MTGProduct;
import org.magic.tools.UITools;
import org.magic.tools.URLTools;

public class ProductRendererComponent extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel lblProductName;
	private JLabel lblProductSet;
	private JLabel lblProductType;
	private JLabel lblImage;
	private transient Map<String,Image> temp;
	
	public ProductRendererComponent() {
		
		temp= new HashMap<>();
		initGUI();
	}

	public ProductRendererComponent(MTGProduct mc) {
		initGUI();
		
		if(mc!=null)
			init(mc);
	}
	
	
	private void initGUI() {
		setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{90, 267, 0};
		gridBagLayout.rowHeights = new int[]{43, 38, 36, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		lblImage = new JLabel();
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
		
		lblProductType.setText(p.getCategory().getCategoryName()+" ("+p.getProductId() +")");
		lblImage.setIcon(new ImageIcon(temp.computeIfAbsent(p.getProductId(),i->{
			try {
				return URLTools.extractImage(p.getUrl()).getScaledInstance(150, 110, Image.SCALE_SMOOTH);
			} catch (Exception e) {
				return new BufferedImage(1, 1, Image.SCALE_FAST);
			}
			
		})));
	}

}
