	package org.magic.api.main;

import javax.swing.JFrame;

import org.magic.gui.components.GamingRoomPanel;

public class GameRoomClient {

	public static void main(String[] args) {
		JFrame f = new JFrame("Gaming Room Client");
		
		f.getContentPane().add(new GamingRoomPanel());
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);

	}

}
