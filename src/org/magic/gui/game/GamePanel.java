package org.magic.gui.game;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class GamePanel extends JPanel {
	public GamePanel() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panelBattle = new JPanel();
		add(panelBattle, BorderLayout.NORTH);
		
		JPanel panelHand = new JPanel();
		panelHand.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 10));
		add(panelHand, BorderLayout.SOUTH);
		
		JPanel panelInfo = new JPanel();
		add(panelInfo, BorderLayout.WEST);
		
		JPanel panelBattleField = new JPanel();
		add(panelBattleField, BorderLayout.CENTER);
		
		JPanel panelLibraryAndGrave = new JPanel();
		add(panelLibraryAndGrave, BorderLayout.EAST);
	}

}
