package org.magic.gui.game;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;

import org.magic.gui.components.ThumbnailPanel;
import org.magic.services.games.Player;

public class GamePanel extends JPanel {
	private JSpinner spinLife;
	private JSpinner spinPoison;
	private ThumbnailPanel thumbnailPanel;
	public GamePanel(Player p) {
		
		setLayout(new BorderLayout(0, 0));
		
		JPanel panelInfo = new JPanel();
		add(panelInfo, BorderLayout.WEST);
		panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
		
		JPanel lifePanel = new JPanel();
		panelInfo.add(lifePanel);
		lifePanel.setLayout(new GridLayout(2, 2, 0, 0));
		
		JLabel lblLife = new JLabel("");
		lblLife.setHorizontalAlignment(SwingConstants.CENTER);
		lblLife.setIcon(new ImageIcon(GamePanel.class.getResource("/res/heart.png")));
		lifePanel.add(lblLife);
		
		spinLife = new JSpinner();
		spinLife.setValue(p.getLife());
		lifePanel.add(spinLife);
		
		JLabel lblPoison = new JLabel("");
		lblPoison.setHorizontalAlignment(SwingConstants.CENTER);
		lblPoison.setIcon(new ImageIcon(GamePanel.class.getResource("/res/poison.png")));
		lifePanel.add(lblPoison);
		
		spinPoison = new JSpinner();
		spinPoison.setValue(p.getPoisonCounter());
		
		lifePanel.add(spinPoison);
		
		ManaPoolPanel manaPoolPanel = new ManaPoolPanel();
		panelInfo.add(manaPoolPanel);
		
		JButton btnEndTurn = new JButton("End Turn");
		panelInfo.add(btnEndTurn);
		
		JPanel panelBattleField = new JPanel();
		add(panelBattleField, BorderLayout.CENTER);
		panelBattleField.setLayout(null);
		
		JPanel panelLibraryAndGrave = new JPanel();
		add(panelLibraryAndGrave, BorderLayout.EAST);
		panelLibraryAndGrave.setLayout(new BoxLayout(panelLibraryAndGrave, BoxLayout.Y_AXIS));
		
		JPanel panelDeck = new JPanel();
		panelLibraryAndGrave.add(panelDeck);
		
		JLabel lblLibrary = new JLabel("");
		lblLibrary.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblLibrary.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});
		
		
		try {
			lblLibrary.setIcon(new ImageIcon(new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=132667&type=card")));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		panelDeck.setLayout(new BoxLayout(panelDeck, BoxLayout.Y_AXIS));
		panelDeck.add(lblLibrary);
		
		JLabel lblLibraryCountCard = new JLabel("60");
		lblLibraryCountCard.setHorizontalAlignment(SwingConstants.CENTER);
		panelDeck.add(lblLibraryCountCard);
		
		JPanel panelGrave = new JPanel();
		panelLibraryAndGrave.add(panelGrave);
		
		thumbnailPanel = new ThumbnailPanel();
		getThumbnailPanel().setThumbnailSize(179, 240);
		getThumbnailPanel().setRupture(7);
	
		add(thumbnailPanel, BorderLayout.SOUTH);
	}
	public JSpinner getSpinLife() {
		return spinLife;
	}
	public JSpinner getSpinPoison() {
		return spinPoison;
	}
	public ThumbnailPanel getThumbnailPanel() {
		return thumbnailPanel;
	}
}
