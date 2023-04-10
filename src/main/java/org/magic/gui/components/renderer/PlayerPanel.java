package org.magic.gui.components.renderer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.magic.game.model.Player;
import org.magic.services.MTGControler;
import org.magic.services.tools.ImageTools;
import org.ocpsoft.prettytime.PrettyTime;


public class PlayerPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel lblIcon;
	private JLabel lblName;
	private JLabel lblCountry;
	private JLabel lblStatus;
	private static final int ICONSIZE=50;
	/**
	 * Create the panel.
	 */
	public PlayerPanel() {
		setLayout(new BorderLayout(0, 0));
		lblIcon = new JLabel(" ");
		lblIcon.setPreferredSize(new Dimension(ICONSIZE,ICONSIZE));
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
		try {
			lblIcon.setIcon(new ImageIcon(ImageTools.resize(p.getAvatar(), ICONSIZE, ICONSIZE)));
		}catch(Exception e)
		{
			//no avatar. do nothing
		}
		lblName.setText(p.getName());
		lblCountry.setText(p.getLocal().getDisplayCountry());
		
		try{
			lblStatus.setText(p.getState().name() + " (" + new PrettyTime(MTGControler.getInstance().getLocale()).format(p.getOnlineConnectionDate())+")");
		}catch(Exception e)
		{
			lblStatus.setText("");
		}


	}
}
