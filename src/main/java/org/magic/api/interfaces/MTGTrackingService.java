package org.magic.api.interfaces;

import java.io.IOException;

import org.magic.api.beans.Tracking;

public interface MTGTrackingService extends MTGPlugin{

	
	public Tracking track(String number) throws IOException;
}
