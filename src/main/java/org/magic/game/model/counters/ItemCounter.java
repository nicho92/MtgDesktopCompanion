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
	
	public ItemCounter(Color c)
	{
		this.name=c.toString();
		this.color=c;
	}
	
	public void setName(String name)
	{
		this.name=name;
	}

	@Override
	public void apply(DisplayableCard displayableCard) {
		//do nothing
	}

	@Override
	public void remove(DisplayableCard displayableCard) {
			//do nothing
	}

	@Override
	public String describe() {
		return name +" counter";
	}
	
}
