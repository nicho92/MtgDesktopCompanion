package org.magic.servers.impl;

import java.io.IOException;

import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;

public class OversightServer extends AbstractMTGServer {

	@Override
	public void start() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isAlive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAutostart() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "Oversight Server";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public void initDefault() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

}
