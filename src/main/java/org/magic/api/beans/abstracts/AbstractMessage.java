package org.magic.api.beans.abstracts;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.magic.game.model.Player;
import org.magic.services.tools.CryptoUtils;

public abstract class AbstractMessage extends AbstractAuditableItem {

	public enum MSG_TYPE { CONNECT, CHANGESTATUS, DISCONNECT, TALK, SYSTEM, SEARCH, ANSWER}

	private Player author;
	private static final long serialVersionUID = 1L;
	private String id;
	private MSG_TYPE  typeMessage;
	private String message;
	
	
	
	protected AbstractMessage() {
		setId(CryptoUtils.generateMD5(UUID.randomUUID().toString()+new Date()+typeMessage));
		setStart(Instant.now());
	}
	


	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Player getAuthor() {
		return author;
	}
	public void setAuthor(Player author) {
		this.author = author;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	
	public MSG_TYPE getTypeMessage() {
		return typeMessage;
	}
	
	public void setTypeMessage(MSG_TYPE typeMessage) {
		this.typeMessage = typeMessage;
	}
	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
