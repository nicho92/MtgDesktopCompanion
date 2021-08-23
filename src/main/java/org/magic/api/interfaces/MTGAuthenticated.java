package org.magic.api.interfaces;

import java.util.List;

import org.magic.api.beans.AccountAuthenticator;
import org.magic.services.AccountsManager;


public interface MTGAuthenticated extends MTGPlugin{
	public List<String> listAuthenticationAttributes();
	
	default AccountAuthenticator getAuthenticator()
	{
		return AccountsManager.inst().getAuthenticator(this);
	}
	
	
}
