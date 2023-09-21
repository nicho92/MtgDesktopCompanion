package org.magic.api.beans.messages;

import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.api.beans.enums.EnumPlayerStatus;

public class StatutMessage extends AbstractMessage {

	private static final long serialVersionUID = 1L;
	private EnumPlayerStatus statut;

	public StatutMessage(EnumPlayerStatus statut) {
		setTypeMessage(MSG_TYPE.CHANGESTATUS);
		this.statut=statut;
		setMessage("change status to "+ statut);
	}
	
	public void setStatut(EnumPlayerStatus statut) {
		this.statut = statut;
	}
	
	public EnumPlayerStatus getStatut() {
		return statut;
	}
}
