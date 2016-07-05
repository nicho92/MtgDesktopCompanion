package org.magic.gui.game;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import org.magic.api.beans.MagicCard;
import org.magic.services.games.PositionEnum;



public class ThumbnailPanel extends DraggablePanel {

	GridBagConstraints c;
	int index=0;
	int width=112;
	int height=155;
	int val=7;
	boolean dragging=false;
	
	
	
	
	public int getCardWidth() {
		return width;
	}

	public void setCardWidth(int width) {
		this.width = width;
	}

	public int getCardHeight() {
		return height;
	}

	public void setCardHeight(int height) {
		this.height = height;
	}

	public boolean isDragging() {
		return dragging;
	}

	public void enableDragging(boolean dragging) {
		this.dragging = dragging;
	}

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
		super();
		setLayout(new GridBagLayout());
		
	}
	
	public void addComponent(DisplayableCard i)
	{
		
		if(index>=val)
		{
			c.gridy=c.gridy+1;
			c.gridx=0;
			index=0;
		}
		c.gridx=c.gridx+1;
		
		 
	   i.setHorizontalTextPosition(JLabel.CENTER);
	   i.setVerticalTextPosition(JLabel.BOTTOM);
	   i.enableDrag(dragging);
		
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
					
					DisplayableCard lab = new DisplayableCard(mc,width,height);
					  	 lab.setOrigine(getOrigine()); 
					try {
						
						if(mc.getEditions().get(0).getMultiverse_id()=="0")
						{
							lab.setText(mc.getName());
						}
						addComponent(lab);
						revalidate();
						//repaint();
					} catch (Exception e) {
						lab.setText(mc.getName());
						lab.setBorder(new LineBorder(Color.BLACK));
					}
					
				}
				
			}
		});
		
		
		
		
		t.start();
		
		}

	@Override
	public PositionEnum getOrigine() {
		return PositionEnum.HAND;
	}


		
	

}
