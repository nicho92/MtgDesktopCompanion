package org.magic.api.beans.abstracts;

import java.time.Instant;

import org.apache.commons.lang3.builder.ToStringBuilder;

public abstract class AbstractMessage extends AbstractAuditableItem {

	public enum MSG_TYPE { CONNECT, CHANGESTATUS, DISCONNECT, TALK, SYSTEM, SEARCH, ANSWER}

	
	private static final long serialVersionUID = 1L;
	private String id;
	private MSG_TYPE typeMessage;
	
	protected AbstractMessage() {
		setStart(Instant.now());
		setEnd(Instant.now());
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
