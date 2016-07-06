package org.magic.gui.game;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.dnd.DnDConstants;
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

import org.magic.game.GameManager;
import org.magic.game.Player;
import org.magic.gui.game.transfert.CardTransfertHandler;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import java.awt.Font;
import java.awt.Component;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;

import java.util.List;
import org.magic.api.beans.MagicCard;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.beansbinding.ObjectProperty;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import java.util.Map;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GamePanel extends JPanel {
	private JSpinner spinLife;
	private JSpinner spinPoison;
	private ThumbnailPanel handPanel;
	private BattleFieldPanel panelBattleField;
	
	
	private JLabel lblLibraryCountCard;
	private JLabel lblTurns;
	
	
	private Player player;
	private JLabel lblLibrary;
	private GraveyardPanel panelGrave;
	
	public void initGame()
	{
		player=GameManager.getInstance().getPlayer();
		spinLife.setValue(player.getLife());
		spinPoison.setValue(player.getPoisonCounter());
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
		
		JPanel panelTurns = new JPanel();
		panelInfo.add(panelTurns);
		
		lblTurns = new JLabel("1");
		panelTurns.add(lblTurns);
		
		JButton btnEndTurn = new JButton("End Turn");
		panelTurns.add(btnEndTurn);
		btnEndTurn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				player.nextTurn();
				lblTurns.setText(""+(player.getTurns().size()+1));
			}
		});
		
		panelBattleField = new BattleFieldPanel();
		add(panelBattleField, BorderLayout.CENTER);
		panelBattleField.setLayout(null);
		
		
		JPanel panelLibraryAndGrave = new JPanel();
		add(panelLibraryAndGrave, BorderLayout.EAST);
		panelLibraryAndGrave.setLayout(new BoxLayout(panelLibraryAndGrave, BoxLayout.Y_AXIS));
		
		JPanel panelDeck = new JPanel();
		panelLibraryAndGrave.add(panelDeck);
		
		GridBagLayout gbl_panelDeck = new GridBagLayout();
		gbl_panelDeck.columnWidths = new int[]{223, 0};
		gbl_panelDeck.rowHeights = new int[]{310, 23, 23, 0};
		gbl_panelDeck.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panelDeck.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelDeck.setLayout(gbl_panelDeck);
		
		JButton btnDrawHand = new JButton("Draw Hand");
		
		btnDrawHand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				player.mixHandAndLibrary();
				player.shuffleLibrary();
				player.drawCard(7);
			    handPanel.initThumbnails(player.getHand());
				
			}
		});
		
		lblLibrary = new JLabel("");
		lblLibrary.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		try {
			lblLibrary.setIcon(new ImageIcon(new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=132667&type=card")));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GridBagConstraints gbc_lblLibrary = new GridBagConstraints();
		gbc_lblLibrary.anchor = GridBagConstraints.WEST;
		gbc_lblLibrary.insets = new Insets(0, 0, 5, 0);
		gbc_lblLibrary.gridx = 0;
		gbc_lblLibrary.gridy = 0;
		panelDeck.add(lblLibrary, gbc_lblLibrary);
		
		
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
		
		lblLibraryCountCard = new JLabel();
		lblLibraryCountCard.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblLibraryCountCard = new GridBagConstraints();
		gbc_lblLibraryCountCard.anchor = GridBagConstraints.WEST;
		gbc_lblLibraryCountCard.insets = new Insets(0, 0, 5, 0);
		gbc_lblLibraryCountCard.gridx = 0;
		gbc_lblLibraryCountCard.gridy = 1;
		panelDeck.add(lblLibraryCountCard, gbc_lblLibraryCountCard);
		
		GridBagConstraints gbc_btnDrawHand = new GridBagConstraints();
		gbc_btnDrawHand.insets = new Insets(0, 0, 5, 0);
		gbc_btnDrawHand.gridx = 0;
		gbc_btnDrawHand.gridy = 1;
		panelDeck.add(btnDrawHand, gbc_btnDrawHand);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				SearchLibraryFrame f = new SearchLibraryFrame();
				f.setVisible(true);
				
			}
		});
		GridBagConstraints gbc_btnSearch = new GridBagConstraints();
		gbc_btnSearch.gridx = 0;
		gbc_btnSearch.gridy = 2;
		panelDeck.add(btnSearch, gbc_btnSearch);
		
		panelGrave = new GraveyardPanel();
		panelLibraryAndGrave.add(panelGrave);
		
		handPanel = new ThumbnailPanel();
		handPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		handPanel.enableDragging(true);
		handPanel.setThumbnailSize(179, 240);
		handPanel.setRupture(7);
		
		JScrollPane scrollPane = new JScrollPane();
					scrollPane.setPreferredSize(new Dimension(2, handPanel.getCardHeight()));
		
		add(scrollPane, BorderLayout.SOUTH);
		
		scrollPane.setViewportView(handPanel);
		
		
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
	public JLabel getLblLibrary() {
		return lblLibrary;
	}
	public GraveyardPanel getPanelGrave() {
		return panelGrave;
	}
	
}
