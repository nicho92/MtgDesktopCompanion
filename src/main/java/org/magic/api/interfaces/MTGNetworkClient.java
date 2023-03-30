package org.magic.api.interfaces;

import java.awt.Color;
import java.io.IOException;

import org.magic.game.model.Player.STATUS;

public interface MTGNetworkClient {

	public void join() throws IOException;

	public void sendMessage(String text) throws IOException;

	public void sendMessage(String text, Color c) throws IOException;

	public void logout() throws IOException;

	public void changeStatus(STATUS selectedItem);

	public boolean isActive();



}