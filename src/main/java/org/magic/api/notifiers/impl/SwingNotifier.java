package org.magic.api.notifiers.impl;

import java.awt.TrayIcon.MessageType;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.magic.api.beans.MTGNotification;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMTGNotifier;

public class SwingNotifier extends AbstractMTGNotifier {

	@Override
	public void send(MTGNotification notification) throws IOException {
		
		JOptionPane.showMessageDialog(null, notification.getMessage(), notification.getTitle(), convert(notification.getType()));
	}

	private int convert(MessageType type) {
		switch(type)
		{
		 case ERROR : return JOptionPane.ERROR_MESSAGE;
		 case INFO : return JOptionPane.INFORMATION_MESSAGE;
		 case WARNING : return JOptionPane.WARNING_MESSAGE;
		 case NONE : return 0;
		 default: return JOptionPane.INFORMATION_MESSAGE;
		}
	}

	@Override
	public String getName() {
		return "Swing";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
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
