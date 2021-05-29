package org.magic.api.interfaces;

import java.io.IOException;
import java.net.URL;

import org.magic.api.beans.Tracking;

public interface MTGSTrackingService extends MTGPlugin{

	
	public Tracking track(String number) throws IOException;
	public URL trackUriFor(String number);
}
