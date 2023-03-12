package org.magic.api.interfaces;

import java.io.IOException;

import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Tracking;

public interface MTGTrackingService extends MTGPlugin{


	public Tracking track(String number, Contact c) throws IOException;
	public Tracking track(String number) throws IOException;
	
}
