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

import org.magic.gui.game.transfert.MagicCardTargetAdapter;
import org.magic.services.games.Player;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import java.awt.Font;

public class GamePanel extends JPanel {
	private JSpinner spinLife;
	private JSpinner spinPoison;
	private ThumbnailPanel handPanel;
	private BattleFieldPanel panelBattleField;
	
	
	private JLabel lblLibraryCountCard;
	
	private Player player;
	
	public void initPlayer(Player p)
	{
		this.player=p;
		spinLife.setValue(p.getLife());
		spinPoison.setValue(p.getPoisonCounter());
		
	}
	
	public GamePanel() {
		
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
		spinLife.setFont(new Font("Tahoma", Font.BOLD, 17));
		lifePanel.add(spinLife);
		
		JLabel lblPoison = new JLabel("");
		lblPoison.setHorizontalAlignment(SwingConstants.CENTER);
		lblPoison.setIcon(new ImageIcon(GamePanel.class.getResource("/res/poison.png")));
		lifePanel.add(lblPoison);
		
		spinPoison = new JSpinner();
		spinPoison.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		lifePanel.add(spinPoison);
		
		ManaPoolPanel manaPoolPanel = new ManaPoolPanel();
		panelInfo.add(manaPoolPanel);
		
		JButton btnEndTurn = new JButton("End Turn");
		panelInfo.add(btnEndTurn);
		
		panelBattleField = new BattleFieldPanel();
		add(panelBattleField, BorderLayout.CENTER);
		panelBattleField.setLayout(null);
		
		
		JPanel panelLibraryAndGrave = new JPanel();
		add(panelLibraryAndGrave, BorderLayout.EAST);
		panelLibraryAndGrave.setLayout(new BoxLayout(panelLibraryAndGrave, BoxLayout.Y_AXIS));
		
		JPanel panelDeck = new JPanel();
		panelLibraryAndGrave.add(panelDeck);
		
		JLabel lblLibrary = new JLabel("");
		lblLibrary.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		
		try {
			lblLibrary.setIcon(new ImageIcon(new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=132667&type=card")));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		panelDeck.setLayout(new BoxLayout(panelDeck, BoxLayout.Y_AXIS));
		panelDeck.add(lblLibrary);
		
		lblLibraryCountCard = new JLabel("");
		lblLibraryCountCard.setHorizontalAlignment(SwingConstants.CENTER);
		panelDeck.add(lblLibraryCountCard);
		
		JButton btnDrawHand = new JButton("Draw Hand");
		
		btnDrawHand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				player.mixHandAndLibrary();
				player.shuffleLibrary();
				player.drawCard(7);
			    handPanel.initThumbnails(player.getHand());
				
			}
		});
		
		
		lblLibrary.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				player.drawCard(1);
				DisplayableCard c = new DisplayableCard(player.getHand().get(player.getHand().size()-1),handPanel.getCardWidth(),handPanel.getCardHeight());
				c.enableDrag(true);
				handPanel.addComponent(c);
				lblLibraryCountCard.setText(""+player.getLibrary().size());
			}
		});
		
		panelDeck.add(btnDrawHand);
		
		JPanel panelGrave = new JPanel();
		panelLibraryAndGrave.add(panelGrave);
		
		handPanel = new ThumbnailPanel();
		handPanel.enableDragging(true);
		handPanel.setThumbnailSize(179, 240);
		handPanel.setRupture(7);
		
		JScrollPane scrollPane = new JScrollPane();
					scrollPane.setPreferredSize(new Dimension(2, handPanel.getCardHeight()));
		
		add(scrollPane, BorderLayout.SOUTH);
		
		scrollPane.setViewportView(handPanel);


		new MagicCardTargetAdapter(handPanel,panelBattleField);
		
		
		
	}
	public JSpinner getSpinLife() {
		return spinLife;
	}
	public JSpinner getSpinPoison() {
		return spinPoison;
	}
	public ThumbnailPanel getThumbnailPanel() {
		return handPanel;
	}
	
	public JLabel getLblLibraryCountCard() {
		return lblLibraryCountCard;
	}
}
