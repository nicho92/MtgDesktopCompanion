package org.magic.api.interfaces;

import java.io.IOException;

import org.magic.api.beans.MTGNotification;

public interface MTGNotifier extends MTGPlugin{

	
	public void send(MTGNotification notification) throws IOException;

	
}
