package org.magic.api.interfaces;

import java.util.List;

import javax.swing.Icon;

import org.magic.api.beans.AccountAuthenticator;


public interface MTGAuthenticated {

	public void addAccount(AccountAuthenticator token);
	public String getTiersName();
	public List<String> listAttributes();
	public Icon getIcon() ;
}
