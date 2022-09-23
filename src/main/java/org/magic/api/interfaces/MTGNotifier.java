package org.magic.api.interfaces;

import java.io.IOException;

import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;

public interface MTGNotifier extends MTGPlugin{


	public void send(MTGNotification notification) throws IOException;
	public void send(String notification) throws IOException;
	public FORMAT_NOTIFICATION getFormat();
	public boolean isExternal();

}
