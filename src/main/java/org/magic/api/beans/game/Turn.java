package org.magic.api.beans.game;

import java.util.ArrayList;
import java.util.List;

public class Turn {

	public enum PHASES {
		UNTAP, UPKEEP, DRAW, MAIN, COMBAT, ATTACK, BLOCK, DAMAGE, END_COMBAT, END, CLEANUP
	}

	private List<String> actions;
	private PHASES current;

	public Turn() {
		actions = new ArrayList<>();
	}

	public List<String> getActions() {
		return actions;
	}

	public void setActions(List<String> actions) {
		this.actions = actions;
	}

	public PHASES currentPhase() {
		return current;
	}

	public void setCurrentPhase(PHASES p) {
		this.current = p;

	}

}
