package org.magic.game.model;

import java.awt.event.ActionEvent;

public class TriggeredSpell extends AbstractSpell {


	public TriggeredSpell(String name, String description) {
		super(name, description);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getCost() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isStackable() {
		return true;
	}

}
