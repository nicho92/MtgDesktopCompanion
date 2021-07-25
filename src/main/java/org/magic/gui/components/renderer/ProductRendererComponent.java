package org.magic.gui.components.renderer;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.api.mkm.modele.Product;
import org.magic.tools.UITools;
import org.magic.tools.URLTools;

public class ProductRendererComponent extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel lblProductName;
	private JLabel lblProductSet;
	private JLabel lblProductType;
	private JLabel lblImage;
	private transient Map<Integer,Image> temp;
	
	public ProductRendererComponent() {
		
		temp= new HashMap<>();
		initGUI();
	}

	public ProductRendererComponent(Product mc) {
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

	public void init(Product p) {
		
		if(p==null)
			return;
		

		lblProductName.setText(p.getEnName());
		lblProductSet.setText(p.getExpansionName());
		lblProductType.setText(p.getCategoryName()+" ("+p.getIdProduct() +")");
		lblImage.setIcon(new ImageIcon(temp.computeIfAbsent(p.getIdProduct(),i->{
			try {
				return URLTools.extractImage(p.getImage()).getScaledInstance(150, 110, Image.SCALE_SMOOTH);
			} catch (IOException e) {
				//do nothing
			}
			return null;
		})));
	}

}
