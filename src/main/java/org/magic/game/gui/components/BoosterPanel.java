package org.magic.game.gui.components;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JPanel;

import org.magic.api.beans.MagicCard;
import org.magic.game.model.GameManager;
import org.magic.game.model.Player;
import org.magic.game.model.PositionEnum;
import org.magic.services.MTGControler;



public class BoosterPanel extends JPanel {

	int lastColumn=-1;
	private List<MagicCard> list;
	
	public void clear()
	{
		lastColumn=-1;
		removeAll();
		revalidate();
	}
	
	
	public BoosterPanel() {
		super();
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		setLayout(flowLayout);
	}
	
	public void addComponent(DisplayableCard i, int column)
	{
		GraveyardPanel p;
		if(lastColumn<column)
		{
			p = new GraveyardPanel() {
				@Override
				public void moveCard(DisplayableCard mc, PositionEnum to) {
					
					if(to==PositionEnum.HAND)
						list.remove(mc.getMagicCard());
				}
			};
			add(p);
			lastColumn=column;
		}
		else
		{
			p = getColumnAt(lastColumn);
		}
		p.setPlayer(GameManager.getInstance().getCurrentPlayer());
		p.setPreferredSize(new Dimension((int)MTGControler.getInstance().getCardsDimension().getWidth()+5, (int) (MTGControler.getInstance().getCardsDimension().getHeight()*30)));
		p.addComponent(i);
		p.postTreatment(i);
		
		revalidate();
		
	}

	public String toString() {
		return "BoosterPanel";
	}

		
	public GraveyardPanel getColumnAt(int i)
	{
		return (GraveyardPanel)getComponent(i-1);
	}


	public void setList(List<MagicCard> list) {
		this.list = list;
	}
	

}
