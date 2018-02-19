package org.magic.game.gui.components;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import org.magic.game.model.PositionEnum;



public class BoosterPanel extends DraggablePanel {

	private PositionEnum origine = PositionEnum.BATTLEFIELD;


	@Override
	public void moveCard(DisplayableCard mc, PositionEnum to) {

	}
	
	
	public BoosterPanel() {
		super();
		setLayout(new FlowLayout());
	}
	
	public void addComponent(DisplayableCard i)
	{
		add(i);
		i.setPosition(getOrigine());
		revalidate();
	}
	

	@Override
	public PositionEnum getOrigine() {
		return origine;
	}



	public void setOrigine(PositionEnum or) {
		origine=or;
		
	}



	@Override
	public void postTreatment(DisplayableCard c) {
	

	}

	@Override
	public String toString() {
		return "BoosterPanel";
	}

		
	

}
