package org.magic.gui.components.renderer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.magic.game.model.Player;

public class PlayerPanel extends JPanel {
	private JLabel lblIcon;
	private JLabel lblName;
	private JLabel lblCountry;
	private JLabel lblStatus;

	/**
	 * Create the panel.
	 */
	public PlayerPanel() {
		setPreferredSize(new Dimension(381, 100));
		setLayout(new BorderLayout(0, 0));
		
		lblIcon = new JLabel(" ");
		lblIcon.setPreferredSize(new Dimension(100, 100));
		add(lblIcon, BorderLayout.WEST);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(3, 1, 0, 0));
		
		lblName = new JLabel(" ");
		panel.add(lblName);
		
		lblCountry = new JLabel(" ");
		panel.add(lblCountry);
		
		lblStatus = new JLabel("");
		panel.add(lblStatus);

	}
	
	public void setPlayer(Player p)
	{
		lblIcon.setIcon(new ImageIcon(p.getAvatar()));
		lblName.setText(p.getName());
		lblCountry.setText(p.getLocal().getDisplayCountry());
		lblStatus.setText(p.getState().name());
	
		
	}
}
