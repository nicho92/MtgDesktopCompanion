package org.magic.game.model.counters;

import java.awt.Color;

import org.magic.game.gui.components.DisplayableCard;

public class ItemCounter extends AbstractCounter{

	private String name;
	private Color color;

	public ItemCounter(String name,Color c)
	{
		this.name=name;
		this.color=c;
	}

	@Override
	public void apply(DisplayableCard displayableCard) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(DisplayableCard displayableCard) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String describe() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
