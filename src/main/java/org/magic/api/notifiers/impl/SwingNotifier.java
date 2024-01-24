package org.magic.api.notifiers.impl;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.beans.technical.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.abstracts.AbstractMTGNotifier;

public class SwingNotifier extends AbstractMTGNotifier {

	@Override
	public FORMAT_NOTIFICATION getFormat() {
		return FORMAT_NOTIFICATION.TEXT;
	}

	@Override
	public void send(MTGNotification notification) throws IOException {
		JOptionPane.showMessageDialog(null, notification.getMessage(), notification.getTitle(), convert(notification.getType()));
	}

	private int convert(MESSAGE_TYPE type) {
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

}
