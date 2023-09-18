package org.magic.api.beans.messages;

import java.awt.Color;

import org.magic.api.beans.abstracts.AbstractMessage;

public class TalkMessage extends AbstractMessage{

	private static final long serialVersionUID = 1L;

	

	
	public String toChatString() {
		return getAuthor().getName() + " : " + getMessage();
	}
	
	public TalkMessage(String message, Color color) {
		setTypeMessage(MSG_TYPE.TALK);
		setMessage(message);
		setColor(color);
	}
	
}
