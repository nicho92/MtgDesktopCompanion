package org.magic.api.interfaces;

import java.io.IOException;

import org.apache.log4j.Logger;

public interface MTGServer extends MTGPlugin {

	public void start() throws IOException;

	public void stop() throws IOException;

	public boolean isAlive();

	public boolean isAutostart();

	public String description();

}
