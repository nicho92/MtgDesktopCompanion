package org.magic.api.beans;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class DAOInfo implements Serializable {

	
	private static final long serialVersionUID = 1L;
	private Statement stat;
	
	
	
	public DAOInfo(PreparedStatement stat2) {
	this.stat=stat2;
	}

	public void setStat(Statement stat) {
		this.stat = stat;
	}
	
	public Statement getStat() {
		return stat;
	}
	
	
}
