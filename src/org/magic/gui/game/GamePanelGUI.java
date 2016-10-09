package org.magic.gui.game;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.exports.impl.MTGDesktopCompanionExport;
import org.magic.game.GameManager;
import org.magic.game.Player;
import org.magic.services.CockatriceTokenProvider;
import org.magic.services.MagicFactory;

public class GamePanelGUI extends JPanel implements Observer {
	
	
	private JSpinner spinLife;
	private JSpinner spinPoison;
	private ThumbnailPanel handPanel;
	private BattleFieldPanel panelBattleField;
	private ManaPoolPanel manaPoolPanel ;
	private JPanel panneauGauche;
	private JPanel panneauDroit;
	private JList<String> listActions;
	private JLabel lblPlayer;
	public  Player player;
	private LibraryPanel panelLibrary;
	private GraveyardPanel panelGrave;
	private JLabel lblThumbnailPics;
	private LightDescribeCardPanel panneauHaut;
	private JLabel lblHandCount;
	private JLabel lblLibraryCount;
	private static GamePanelGUI instance;
	private JTextField txtChat;
	
	
	public static GamePanelGUI getInstance()
	{
		if (instance==null)
			instance = new GamePanelGUI();
		
		return instance;
	}
	
	
	
	public void initGame()
	{
		player.init();
	}
	
	public void setPlayer(Player p1) {
		player=p1;
		lblPlayer.setText(p1.getName());
		player.addObserver(this);
		spinLife.setValue(p1.getLife());
		spinPoison.setValue(p1.getPoisonCounter());
		
		handPanel.setPlayer(p1);
		panelGrave.setPlayer(p1);
		manaPoolPanel.setPlayer(p1);
		panelBattleField.setPlayer(p1);
		panelLibrary.setPlayer(p1);
	}
	
	
	private GamePanelGUI() {
		
		setLayout(new BorderLayout(0, 0));
		
		panneauGauche = new JPanel();
		panneauDroit = new JPanel();
		
		
		JSplitPane splitPane = new JSplitPane();
		add(splitPane, BorderLayout.CENTER);
		
		splitPane.setLeftComponent(panneauGauche);
		panneauGauche.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollActions = new JScrollPane();
		panneauGauche.add(scrollActions);
		
		scrollActions.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
	        public void adjustmentValueChanged(AdjustmentEvent e) {  
	            e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
	        }
	    });
		
		listActions = new JList<String>();
		listActions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listActions.setValueIsAdjusting(true);
		listActions.setModel(new DefaultListModel<String>());
		scrollActions.setViewportView(listActions);
		
		
		splitPane.setRightComponent(panneauDroit);
		panneauDroit.setLayout(new BorderLayout(0, 0));
		
		JPanel panelInfo = new JPanel();
		panneauDroit.add(panelInfo, BorderLayout.WEST);
		panelInfo.setLayout(new BorderLayout(0, 0));
		
		JPanel lifePanel = new JPanel();
		lifePanel.setAlignmentY(Component.TOP_ALIGNMENT);
		panelInfo.add(lifePanel, BorderLayout.NORTH);
						lifePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
						
						lblPlayer = new JLabel("");
						lblPlayer.setIcon(new ImageIcon(GamePanelGUI.class.getResource("/res/planeswalker.png")));
						lifePanel.add(lblPlayer);
		
		JPanel panelActions = new JPanel();
		panelActions.setAlignmentY(Component.TOP_ALIGNMENT);
		panelInfo.add(panelActions, BorderLayout.SOUTH);
		GridBagLayout gbl_panelActions = new GridBagLayout();
		gbl_panelActions.columnWidths = new int[]{86, 86, 86, 0};
		gbl_panelActions.rowHeights = new int[]{23, 23, 23, 0, 0};
		gbl_panelActions.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelActions.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelActions.setLayout(gbl_panelActions);
		
		JButton btnShuffle = new JButton("Shuffle");
		btnShuffle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				player.shuffleLibrary();
			}
		});
		
		JButton btnNewGame = new JButton("New Game");
		btnNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser choose = new JFileChooser(new File(MagicFactory.CONF_DIR,"decks"));
				choose.showOpenDialog(null);
				try {
					MagicDeck deck = new MTGDesktopCompanionExport().importDeck(choose.getSelectedFile());
					
					Player p = new Player(deck);
					GameManager.getInstance().addPlayer(p);
					GameManager.getInstance().initGame();
					GameManager.getInstance().nextTurn();
					setPlayer(p);
					clean();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		GridBagConstraints gbc_btnNewGame = new GridBagConstraints();
		gbc_btnNewGame.fill = GridBagConstraints.BOTH;
		gbc_btnNewGame.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewGame.gridx = 0;
		gbc_btnNewGame.gridy = 0;
		panelActions.add(btnNewGame, gbc_btnNewGame);
		
		JButton btnSearch = new JButton("Search");
		GridBagConstraints gbc_btnSearch = new GridBagConstraints();
		gbc_btnSearch.fill = GridBagConstraints.BOTH;
		gbc_btnSearch.insets = new Insets(0, 0, 5, 5);
		gbc_btnSearch.gridx = 1;
		gbc_btnSearch.gridy = 0;
		panelActions.add(btnSearch, gbc_btnSearch);
		
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				player.logAction("search in library");
				SearchLibraryFrame f = new SearchLibraryFrame(player);
				f.setVisible(true);
				
			}
		});
		
		JButton btnDrawHand = new JButton("Draw Hand");
		GridBagConstraints gbc_btnDrawHand = new GridBagConstraints();
		gbc_btnDrawHand.fill = GridBagConstraints.BOTH;
		gbc_btnDrawHand.insets = new Insets(0, 0, 5, 0);
		gbc_btnDrawHand.gridx = 2;
		gbc_btnDrawHand.gridy = 0;
		panelActions.add(btnDrawHand, gbc_btnDrawHand);
		
		btnDrawHand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				player.mixHandAndLibrary();
				player.shuffleLibrary();
				try{
					player.drawCard(7);
					lblHandCount.setText(String.valueOf(player.getHand().size()));
					lblLibraryCount.setText(String.valueOf(player.getLibrary().size()));
				}catch (IndexOutOfBoundsException e)
				{
					JOptionPane.showMessageDialog(null, "Not enougth cards in library","Error",JOptionPane.ERROR_MESSAGE);
				}
			    handPanel.initThumbnails(player.getHand(),true);
			}
		});
		GridBagConstraints gbc_btnShuffle = new GridBagConstraints();
		gbc_btnShuffle.fill = GridBagConstraints.BOTH;
		gbc_btnShuffle.insets = new Insets(0, 0, 5, 5);
		gbc_btnShuffle.gridx = 0;
		gbc_btnShuffle.gridy = 1;
		panelActions.add(btnShuffle, gbc_btnShuffle);
		
		JButton btnToken = new JButton("Token");
		btnToken.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				
				for(Component c : panelBattleField.getComponents())
				{
					if(((DisplayableCard)c).isSelected())
					{
						try{
							MagicCard tok = new CockatriceTokenProvider().generateTokenFor( ((DisplayableCard)c).getMagicCard() );
							DisplayableCard dc = new DisplayableCard( tok, ((DisplayableCard)c).getWidth(), ((DisplayableCard)c).getHeight(),true);
							dc.setMagicCard(tok);
							//dc.setImage(new ImageIcon(new CockatriceTokenProvider().getToken(tok).getScaledInstance(((DisplayableCard)c).getWidth(), ((DisplayableCard)c).getHeight(), BufferedImage.SCALE_SMOOTH)));
							
							panelBattleField.addComponent(dc);
							panelBattleField.revalidate();
							panelBattleField.repaint();
							
							player.logAction("generate " + tok + " token");
						}
						catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			}
		});
		
		JButton btnScry = new JButton("Scry");
		btnScry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				String res = JOptionPane.showInputDialog("How many scry card ?");
				if(res!=null)
					new SearchLibraryFrame(player,player.scry(Integer.parseInt(res))).setVisible(true);
				
			}
		});
		GridBagConstraints gbc_btnScry = new GridBagConstraints();
		gbc_btnScry.fill = GridBagConstraints.BOTH;
		gbc_btnScry.insets = new Insets(0, 0, 5, 5);
		gbc_btnScry.gridx = 1;
		gbc_btnScry.gridy = 1;
		panelActions.add(btnScry, gbc_btnScry);
		
		JButton btnEndTurn = new JButton("End Turn");
		GridBagConstraints gbc_btnEndTurn = new GridBagConstraints();
		gbc_btnEndTurn.fill = GridBagConstraints.BOTH;
		gbc_btnEndTurn.insets = new Insets(0, 0, 5, 0);
		gbc_btnEndTurn.gridx = 2;
		gbc_btnEndTurn.gridy = 1;
		panelActions.add(btnEndTurn, gbc_btnEndTurn);
		
		btnEndTurn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				GameManager.getInstance().nextTurn();
			}
		});
		GridBagConstraints gbc_btnToken = new GridBagConstraints();
		gbc_btnToken.fill = GridBagConstraints.BOTH;
		gbc_btnToken.insets = new Insets(0, 0, 5, 5);
		gbc_btnToken.gridx = 0;
		gbc_btnToken.gridy = 2;
		panelActions.add(btnToken, gbc_btnToken);
		
		txtChat = new JTextField("Say something");
		txtChat.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				txtChat.setText("");
			}
		});
		
		
		txtChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				player.say(txtChat.getText());
				txtChat.setText("");
			}
		});
		
		JButton btnEmblem = new JButton("Emblem");
		btnEmblem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for(Component c : panelBattleField.getComponents())
				{
					if(((DisplayableCard)c).isSelected())
					{
						try{
							MagicCard tok = new CockatriceTokenProvider().generateEmblemFor(((DisplayableCard)c).getMagicCard()  );
							DisplayableCard dc = new DisplayableCard( tok, ((DisplayableCard)c).getWidth(), ((DisplayableCard)c).getHeight(),true);
							dc.setMagicCard(tok);
							
							//dc.setImage(new ImageIcon(new CockatriceTokenProvider().getEmblem(tok).getScaledInstance(((DisplayableCard)c).getWidth(), ((DisplayableCard)c).getHeight(), BufferedImage.SCALE_SMOOTH)));
							
							panelBattleField.addComponent(dc);
							panelBattleField.revalidate();
							panelBattleField.repaint();
							
							player.logAction("generate " + tok + " emblem");
						}
						catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			}
		});
		GridBagConstraints gbc_btnEmblem = new GridBagConstraints();
		gbc_btnEmblem.insets = new Insets(0, 0, 5, 5);
		gbc_btnEmblem.fill = GridBagConstraints.BOTH;
		gbc_btnEmblem.gridx = 1;
		gbc_btnEmblem.gridy = 2;
		panelActions.add(btnEmblem, gbc_btnEmblem);
		GridBagConstraints gbc_txtChat = new GridBagConstraints();
		gbc_txtChat.gridwidth = 2;
		gbc_txtChat.insets = new Insets(0, 0, 0, 5);
		gbc_txtChat.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtChat.gridx = 0;
		gbc_txtChat.gridy = 3;
		panelActions.add(txtChat, gbc_txtChat);
		txtChat.setColumns(10);
		
		JPanel panelPoolandDescribes = new JPanel();
		panelInfo.add(panelPoolandDescribes, BorderLayout.CENTER);
		
		panelPoolandDescribes.setLayout(new BorderLayout(0, 0));
		
		JPanel panelPoolandHandsLib = new JPanel();
		panelPoolandDescribes.add(panelPoolandHandsLib, BorderLayout.NORTH);
		panelPoolandHandsLib.setLayout(new BorderLayout(0, 0));
		
		manaPoolPanel = new ManaPoolPanel();
		panelPoolandHandsLib.add(manaPoolPanel);
		
		JPanel panelHandLib = new JPanel();
		panelPoolandHandsLib.add(panelHandLib, BorderLayout.EAST);
		panelHandLib.setLayout(new GridLayout(2, 1, 0, 0));
		
		lblHandCount = new JLabel("0");
		lblHandCount.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblHandCount.setHorizontalTextPosition(JLabel.CENTER);
		lblHandCount.setIcon(new ImageIcon(GamePanelGUI.class.getResource("/res/hand.png")));
		panelHandLib.add(lblHandCount);
		
		lblLibraryCount = new JLabel("");
		lblLibraryCount.setHorizontalTextPosition(SwingConstants.CENTER);
		lblLibraryCount.setHorizontalAlignment(SwingConstants.CENTER);
		lblLibraryCount.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblLibraryCount.setIcon(new ImageIcon(GamePanelGUI.class.getResource("/res/librarysize.png")));
		panelHandLib.add(lblLibraryCount);
		
		JPanel panel = new JPanel();
		panelPoolandHandsLib.add(panel, BorderLayout.WEST);
				panel.setLayout(new GridLayout(2, 2, 0, 0));
		
				
				JLabel lblLife = new JLabel("");
				panel.add(lblLife);
				lblLife.setHorizontalAlignment(SwingConstants.CENTER);
				lblLife.setIcon(new ImageIcon(GamePanelGUI.class.getResource("/res/heart.png")));
				
				spinLife = new JSpinner();
				panel.add(spinLife);
				spinLife.setFont(new Font("Tahoma", Font.BOLD, 17));
				
				JLabel lblPoison = new JLabel("");
				panel.add(lblPoison);
				lblPoison.setHorizontalAlignment(SwingConstants.CENTER);
				lblPoison.setIcon(new ImageIcon(GamePanelGUI.class.getResource("/res/poison.png")));
				
				spinPoison = new JSpinner();
				panel.add(spinPoison);
				spinPoison.setFont(new Font("Tahoma", Font.BOLD, 15));
				
				spinPoison.addChangeListener(new ChangeListener() {
					
					public void stateChanged(ChangeEvent e) {
						if(player !=null)
							player.setPoisonCounter((int)spinPoison.getValue());
						
					}
				});
				spinLife.addChangeListener(new ChangeListener() {
					
					public void stateChanged(ChangeEvent e) {
						if(player !=null) 
							player.setLife((int)spinLife.getValue());
						
					}
				});
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		panelPoolandDescribes.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());
		
		panneauHaut = new LightDescribeCardPanel();
		pane.add(panneauHaut, BorderLayout.CENTER);
		
		tabbedPane.addTab("Description", null, pane, null);
		
		
		JPanel panelPics = new JPanel();
		tabbedPane.addTab("Picture", null, panelPics, null);
		panelPics.setLayout(new BorderLayout(0, 0));
		
		lblThumbnailPics = new JLabel("");
		lblThumbnailPics.setHorizontalTextPosition(SwingConstants.CENTER);
		lblThumbnailPics.setHorizontalAlignment(SwingConstants.CENTER);
		panelPics.add(lblThumbnailPics);
		
		
		JPanel panelLibraryAndGrave = new JPanel();
		panneauDroit.add(panelLibraryAndGrave, BorderLayout.EAST);
		panelLibraryAndGrave.setLayout(new BorderLayout(0, 0));
		
		JPanel panelDeck = new JPanel();
		panelLibraryAndGrave.add(panelDeck, BorderLayout.NORTH);
		
		panelLibrary = new LibraryPanel();
		panelLibrary.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		panelDeck.setLayout(new BoxLayout(panelDeck, BoxLayout.Y_AXIS));
		panelDeck.add(panelLibrary);
		
		
		panelLibrary.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {

				player.drawCard(1);
				DisplayableCard c = new DisplayableCard(player.getHand().get(player.getHand().size()-1),handPanel.getCardWidth(),handPanel.getCardHeight(),true);
				c.enableDrag(true);
				handPanel.addComponent(c);
			}
		});
		
		panelGrave = new GraveyardPanel();
		panelLibraryAndGrave.add(panelGrave);
		
		handPanel = new ThumbnailPanel();
		handPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		handPanel.enableDragging(true);
		handPanel.setThumbnailSize(179, 240);
		handPanel.setRupture(7);
		
		JScrollPane scrollPane = new JScrollPane();
		panneauDroit.add(scrollPane, BorderLayout.SOUTH);
		scrollPane.setPreferredSize(new Dimension(2, handPanel.getCardHeight()));
		
		scrollPane.setViewportView(handPanel);
		
		panelBattleField = new BattleFieldPanel();
		panneauDroit.add(panelBattleField, BorderLayout.CENTER);
		panelBattleField.setLayout(null);
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
	
	public LibraryPanel getLblLibrary() {
		return panelLibrary;
	}
	public GraveyardPanel getPanelGrave() {
		return panelGrave;
	}

	@Override
	public void update(Observable o, Object arg) {
		String act = player.getName() +" " + arg.toString();
		((DefaultListModel)listActions.getModel()).addElement(act);
		lblHandCount.setText(String.valueOf(player.getHand().size()));
		lblLibraryCount.setText(String.valueOf(player.getLibrary().size()));
	}

	private void clean() {
		handPanel.removeAll();
		panelBattleField.removeAll();
		panelGrave.removeAll();
	
		handPanel.revalidate();
		panelBattleField.revalidate();
		panelGrave.revalidate();
		
		handPanel.repaint();
		panelBattleField.repaint();
		panelGrave.repaint();
		
		
		lblHandCount.setText(String.valueOf(player.getHand().size()));
		lblLibraryCount.setText(String.valueOf(player.getLibrary().size()));
		
	}

	public Player getPlayer() {
		return player;
	}
	
	public void describeCard(DisplayableCard mc) 
	{
		panneauHaut.setCard(mc.getMagicCard());
		lblThumbnailPics.setIcon(new ImageIcon(mc.getFullResPics().getScaledInstance(223,310, BufferedImage.SCALE_SMOOTH)));
		//
	}
}
