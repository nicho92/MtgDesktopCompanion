package org.magic.game.gui.components;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import org.magic.api.beans.MagicCard;
import org.magic.game.model.PositionEnum;
import org.magic.services.MTGControler;



public class ThumbnailPanel extends DraggablePanel {

	GridBagConstraints c;
	int index=0;
	int val=7;
	
	private PositionEnum origine = PositionEnum.HAND;


	@Override
	public void moveCard(MagicCard mc, PositionEnum to) {
		
		switch (to) {
			case BATTLEFIELD:player.playCard(mc);break;
			case EXIL:player.exileCardFromHand(mc);break;
			case GRAVEYARD:player.discardCardFromHand(mc);break;
			case LIBRARY:player.putCardInLibraryFromHand(mc, true);
			
			default:break;
		}
		
	}
	
	
	
	public void setRupture(int val)
	{
		this.val = val;
	}
	
	
	public ThumbnailPanel() {
		super();
		setLayout(new GridBagLayout());
		c = new GridBagConstraints();
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
		i.setPosition(getOrigine());
	}
	
	Thread t;
	public void initThumbnails(final List<MagicCard> cards, final boolean activateCards) {
		
		
		if(t!=null)
			if(t.isAlive())
				t.stop();
		
		
	//	addMouseListener(new MouseAction(player));
		
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
					if(d==null)
						d=MTGControler.getInstance().getCardsDimension();
					
					
					DisplayableCard lab = new DisplayableCard(mc,d,activateCards);
					lab.setTappable(activateCards);
					
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
		return origine;
	}



	public void setOrigine(PositionEnum or) {
		origine=or;
		
	}



	@Override
	public void postTreatment() {
		// TODO Auto-generated method stub
		
	}



	public void setMaxCardsRow(int i) {
		val=i;
		
	}


		
	

}
