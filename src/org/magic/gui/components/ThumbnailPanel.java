package org.magic.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.net.URL;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.magic.api.beans.MagicCard;



public class ThumbnailPanel extends JPanel {

	GridBagConstraints c;
	int index=0;
	int width=112;
	int height=155;
	int val=7;
	
	public void setThumbnailSize(int w,int h)
	{
		this.width=w;
		this.height=h;
	}
	
	public void setRupture(int val)
	{
		this.val = val;
	}
	
	
	public ThumbnailPanel() {
		setLayout(new GridBagLayout());
	}
	
	public void addComponent(JLabel i)
	{
		if(index>=val)
		{
			c.gridy=c.gridy+1;
			c.gridx=0;
			index=0;
		}
		c.gridx=c.gridx+1;
		add(i,c);
		index++;
		
	}
	
	Thread t;
	public void initThumbnails(final List<MagicCard> cards) {
		
		if(t!=null)
			if(t.isAlive())
				t.stop();
		
		
		c = new GridBagConstraints();
		  c.weightx = 1;
		  c.weighty = 1;
		  c.gridx = 0;
		  c.gridy = 0;
		  c.insets = new Insets(2,2,2,2); 
		  c.anchor = GridBagConstraints.NORTHWEST;
		
		  this.removeAll();
		index=0;
		
		  
		
		t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				for(MagicCard mc : cards)
				{
					
					JLabel lab = new JLabel(mc.getName());
						   lab.setSize(new Dimension(width, height));
						   lab.setHorizontalTextPosition(JLabel.CENTER);
						   lab.setVerticalTextPosition(JLabel.BOTTOM);
					try {
						
						if(mc.getEditions().get(0).getMultiverse_id()=="0")
						{
							lab.setText(mc.getName());
						}
						
						ImageIcon icon = new ImageIcon(new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+mc.getEditions().get(0).getMultiverse_id()+"&type=card"));
						Image img = icon.getImage(); 
						Image newimg = img.getScaledInstance(lab.getWidth(), lab.getHeight(),  java.awt.Image.SCALE_SMOOTH);
						lab.setIcon( new ImageIcon(newimg));
						
						addComponent(lab);
						
						revalidate();
						repaint();
						
					} catch (Exception e) {
						lab.setText(mc.getName());
						lab.setBorder(new LineBorder(Color.BLACK));
					}
					
				}
				
			}
		});
		
		
		
		
		t.start();
		
		}


		
	

}
