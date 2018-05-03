package org.magic.api.notifiers.impl;

import java.io.IOException;

import org.magic.api.beans.MTGNotification;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMTGNotifier;

public class TelegraphNotifier extends AbstractMTGNotifier {

	@Override
	public void send(MTGNotification notification) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return "Telegraph";
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
		return "1.0";
	}

}
