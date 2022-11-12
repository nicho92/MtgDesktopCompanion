package org.magic.game.actions.abbstract;

import javax.swing.AbstractAction;

import org.apache.logging.log4j.Logger;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.model.ZoneEnum;
import org.magic.services.logging.MTGLogger;

public abstract class AbstractCardAction extends AbstractAction{

	private static final long serialVersionUID = 1L;
	protected DisplayableCard card;
	protected transient Logger logger = MTGLogger.getLogger(this.getClass());
	
	
	protected AbstractCardAction(String string) {
		super(string);
	}

	protected AbstractCardAction(DisplayableCard mc) {
		this.card=mc;
	}
	
	

	public abstract ZoneEnum playableFrom();
	
	
}
