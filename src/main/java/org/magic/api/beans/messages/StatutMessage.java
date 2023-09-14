package org.magic.api.beans.messages;

import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.game.model.Player;
import org.magic.game.model.Player.STATUS;

public class StatutMessage extends AbstractMessage {

	private static final long serialVersionUID = 1L;
	private STATUS statut;

	public StatutMessage(Player p, STATUS statut) {
		super(p);
		setTypeMessage(MSG_TYPE.CHANGESTATUS);
		setAuthor(p);
		this.statut=statut;
	}
	
	public void setStatut(STATUS statut) {
		this.statut = statut;
	}
	
	public STATUS getStatut() {
		return statut;
	}
}
