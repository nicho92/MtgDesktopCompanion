package org.magic.game.gui.components;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;

import org.magic.game.model.Player;
import org.magic.game.model.PositionEnum;
import org.magic.services.MTGControler;



public class BoosterPanel extends DraggablePanel {

	int lastColumn=-1;
	
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
			p = new GraveyardPanel();
			add(p);
			lastColumn=column;
		}
		else
		{
			p = getColumnAt(lastColumn);
		}
		p.setPlayer(new Player());
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


	@Override
	public void moveCard(DisplayableCard mc, PositionEnum to) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void addComponent(DisplayableCard i) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public PositionEnum getOrigine() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void postTreatment(DisplayableCard c) {
		// TODO Auto-generated method stub
		
	}
	

}
