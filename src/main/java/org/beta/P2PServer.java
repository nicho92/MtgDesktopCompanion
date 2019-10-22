package org.beta;
import java.io.IOException;

import org.magic.api.interfaces.abstracts.AbstractMTGServer;


public class P2PServer extends AbstractMTGServer {

	
	public static void main(String[] args) throws IOException {
		new P2PServer().start();
	}

	@Override
	public void start() throws IOException {
		
		
	}

	@Override
	public void stop() throws IOException {
		
	}

	@Override
	public boolean isAlive() {
	
		
		return false;

	}

	@Override
	public boolean isAutostart() {
		return getBoolean("AUTO_START");
	}

	@Override
	public String description() {
		return "Share your decks around the world";
	}

	@Override
	public String getName() {
		return "P2P Sharing";
	}
	
	@Override
	public void initDefault() {
		setProperty("AUTO_START", "false");
	}
	
}
