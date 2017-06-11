package org.magic.game.gui.components;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.magic.api.beans.MagicDeck;
import org.magic.game.actions.library.DrawActions;
import org.magic.game.actions.library.DrawHandActions;
import org.magic.game.gui.components.dialog.DeckSideBoardSwitcherDialog;
import org.magic.game.model.GameManager;
import org.magic.game.model.Player;
import org.magic.game.network.actions.AbstractNetworkAction;
import org.magic.game.network.actions.SpeakAction;
import org.magic.gui.components.dialog.JDeckChooserDialog;
import org.magic.services.CockatriceTokenProvider;
import org.magic.services.MTGControler;
import java.awt.Color;

public class GamePanelGUI extends JPanel implements Observer {
	
	
	private JSpinner spinLife;
	private JSpinner spinPoison;
	private HandPanel handPanel;
	private BattleFieldPanel panelBattleField;
	private ManaPoolPanel manaPoolPanel ;
	private JPanel panneauDroit;
	private JList<AbstractAction> listActions;
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
	private CockatriceTokenProvider tokenGenerator;
	private PlayerGameBoard playerGameBoard;
	private JPanel panelInfo;
	private TurnsPanel turnsPanel;
	private ExilPanel exilPanel;
	

	public static GamePanelGUI getInstance()
	{
		if (instance==null)
			instance = new GamePanelGUI();
		
		return instance;
	}
	
	public TurnsPanel getTurnsPanel()
	{
		return turnsPanel;
		
	}
	
	
	public static GamePanelGUI newInstance()
	{
		return new GamePanelGUI();
	}
	
	
	public BattleFieldPanel getPanelBattleField() {
		return panelBattleField;
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
	
	
	public CockatriceTokenProvider getTokenGenerator() {
		return tokenGenerator;
	}



	private GamePanelGUI() {
		
		setLayout(new BorderLayout(0, 0));
		
		panneauDroit = new JPanel();
		
		tokenGenerator= new CockatriceTokenProvider();
		
		add(panneauDroit, BorderLayout.CENTER);
		panneauDroit.setLayout(new BorderLayout(0, 0));
						
						panelInfo = new JPanel();
						panneauDroit.add(panelInfo, BorderLayout.WEST);
						panelInfo.setLayout(new BorderLayout(0, 0));
						
						JPanel panelActions = new JPanel();
						panelActions.setAlignmentY(Component.TOP_ALIGNMENT);
						panelInfo.add(panelActions, BorderLayout.SOUTH);
						GridBagLayout gbl_panelActions = new GridBagLayout();
						gbl_panelActions.columnWidths = new int[]{30, 20, 0};
						gbl_panelActions.rowHeights = new int[]{23, 0, 23, 0, 0, 0};
						gbl_panelActions.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
						gbl_panelActions.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
						panelActions.setLayout(gbl_panelActions);
						
						JButton btnNewGame = new JButton("Select Deck");
						btnNewGame.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ae) {
								JDeckChooserDialog choose = new JDeckChooserDialog();
								choose.setVisible(true);
								try {
									MagicDeck deck = choose.getSelectedDeck();
									if(deck!=null){
										
										Player p1 = MTGControler.getInstance().getProfilPlayer();
										p1.setDeck(deck);
										setPlayer(p1);
									}
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
						
						
						
						JButton btnSideboard = new JButton("SideBoard");
						btnSideboard.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								DeckSideBoardSwitcherDialog gui = new DeckSideBoardSwitcherDialog(player.getDeck());
								gui.setVisible(true);
								player.setDeck(gui.getDeck());
							}
						});
						GridBagConstraints gbc_btnSideboard = new GridBagConstraints();
						gbc_btnSideboard.fill = GridBagConstraints.HORIZONTAL;
						gbc_btnSideboard.insets = new Insets(0, 0, 5, 5);
						gbc_btnSideboard.gridx = 0;
						gbc_btnSideboard.gridy = 1;
						panelActions.add(btnSideboard, gbc_btnSideboard);
						
						JButton btnStart = new JButton("Start");
						btnStart.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ae) {
								GameManager.getInstance().removePlayers();
								GameManager.getInstance().addPlayer(player);
								GameManager.getInstance().addPlayer(new Player("Player 2",20));
								
								GameManager.getInstance().initGame();
								manaPoolPanel.init(player.getManaPool());
								((DefaultListModel)listActions.getModel()).removeAllElements();
								player.shuffleLibrary();
								turnsPanel.initTurn();
								new DrawHandActions().actionPerformed(ae);
								clean();
							}
						});
						GridBagConstraints gbc_btnStart = new GridBagConstraints();
						gbc_btnStart.fill = GridBagConstraints.HORIZONTAL;
						gbc_btnStart.insets = new Insets(0, 0, 5, 0);
						gbc_btnStart.gridx = 1;
						gbc_btnStart.gridy = 1;
						panelActions.add(btnStart, gbc_btnStart);
						
						JPanel panel_1 = new JPanel();
						GridBagConstraints gbc_panel_1 = new GridBagConstraints();
						gbc_panel_1.gridheight = 2;
						gbc_panel_1.gridwidth = 2;
						gbc_panel_1.insets = new Insets(0, 0, 5, 0);
						gbc_panel_1.fill = GridBagConstraints.BOTH;
						gbc_panel_1.gridx = 0;
						gbc_panel_1.gridy = 2;
						panelActions.add(panel_1, gbc_panel_1);
						panel_1.setLayout(new BorderLayout(0, 0));
						
						txtChat = new JTextField("Say something");
						panel_1.add(txtChat);
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
						txtChat.setColumns(10);
						
						JPanel panelPoolandDescribes = new JPanel();
						panelInfo.add(panelPoolandDescribes, BorderLayout.CENTER);
						
						panelPoolandDescribes.setLayout(new BorderLayout(0, 0));
						
						JPanel panelPoolandHandsLib = new JPanel();
						panelPoolandDescribes.add(panelPoolandHandsLib, BorderLayout.NORTH);
						panelPoolandHandsLib.setLayout(new BorderLayout(0, 0));
						
						manaPoolPanel = new ManaPoolPanel();
						manaPoolPanel.setMinimumSize(new Dimension(0, 0));
						panelPoolandHandsLib.add(manaPoolPanel, BorderLayout.CENTER);
						
						JPanel panelHandLib = new JPanel();
						panelPoolandHandsLib.add(panelHandLib, BorderLayout.SOUTH);
						panelHandLib.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
						
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
						
						JPanel lifePanel = new JPanel();
						panelPoolandHandsLib.add(lifePanel, BorderLayout.WEST);
						lifePanel.setAlignmentY(Component.TOP_ALIGNMENT);
						GridBagLayout gbl_lifePanel = new GridBagLayout();
						gbl_lifePanel.columnWidths = new int[]{60, 0};
						gbl_lifePanel.rowHeights = new int[]{64, 0, 0};
						gbl_lifePanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
						gbl_lifePanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
						lifePanel.setLayout(gbl_lifePanel);
						
						lblPlayer = new JLabel("");
						lblPlayer.setVerticalAlignment(SwingConstants.BOTTOM);
						lblPlayer.setIcon(new ImageIcon(GamePanelGUI.class.getResource("/res/planeswalker.png")));
						GridBagConstraints gbc_lblPlayer = new GridBagConstraints();
						gbc_lblPlayer.anchor = GridBagConstraints.WEST;
						gbc_lblPlayer.insets = new Insets(0, 0, 5, 0);
						gbc_lblPlayer.gridx = 0;
						gbc_lblPlayer.gridy = 0;
						lifePanel.add(lblPlayer, gbc_lblPlayer);
								
								JPanel panel = new JPanel();
								GridBagConstraints gbc_panel = new GridBagConstraints();
								gbc_panel.anchor = GridBagConstraints.NORTHWEST;
								gbc_panel.gridx = 0;
								gbc_panel.gridy = 1;
								lifePanel.add(panel, gbc_panel);
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
								
								JScrollPane scrollActions = new JScrollPane();
								
								scrollActions.setPreferredSize(new Dimension((int)MTGControler.getInstance().getCardsDimension().getWidth(), 0));
								tabbedPane.addTab("Events", null, scrollActions, null);
								
								listActions = new JList<AbstractAction>();
								listActions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
								listActions.setModel(new DefaultListModel<AbstractAction>());
								scrollActions.setViewportView(listActions);
								
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
								panelLibrary.setPreferredSize(new Dimension(170, 215));
								panelLibrary.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
								
										panelDeck.setLayout(new BoxLayout(panelDeck, BoxLayout.Y_AXIS));
										panelDeck.add(panelLibrary);
										
										exilPanel = new ExilPanel();
										exilPanel.setPreferredSize(new Dimension(0, 50));
										
										panelDeck.add(exilPanel);
										
										panelLibrary.addMouseListener(new MouseAdapter() {
											@Override
											public void mouseClicked(MouseEvent me) {

												if(SwingUtilities.isLeftMouseButton(me))
												{	
													new DrawActions().actionPerformed( new ActionEvent(me.getSource(), me.getID(), me.paramString()));
												}
												
											}
										});
										JScrollPane js = new JScrollPane();
										panelGrave = new GraveyardPanel();
										js.setViewportView(panelGrave);
										panelLibraryAndGrave.add(js);
										
										panelBattleField = new BattleFieldPanel();
										panneauDroit.add(panelBattleField, BorderLayout.CENTER);
										panelBattleField.setLayout(null);
										
										JPanel panelBottom = new JPanel();
										panneauDroit.add(panelBottom, BorderLayout.SOUTH);
										
										handPanel = new HandPanel();
										handPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
										handPanel.enableDragging(true);
										handPanel.setThumbnailSize(MTGControler.getInstance().getCardsDimension());
										panelBottom.setLayout(new BorderLayout(0, 0));
										handPanel.setRupture(7);
										
										JScrollPane scrollPane = new JScrollPane();
										panelBottom.add(scrollPane);
										scrollPane.setPreferredSize(new Dimension(2, handPanel.getCardHeight()));
										
										scrollPane.setViewportView(handPanel);
										
										turnsPanel = new TurnsPanel();
										panelBottom.add(turnsPanel, BorderLayout.NORTH);
									
	}
	
	public ExilPanel getExilPanel() {
		return exilPanel;
	}

	public JSpinner getSpinLife() {
		return spinLife;
	}
	public JSpinner getSpinPoison() {
		return spinPoison;
	}
	public HandPanel getHandPanel() {
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
		((DefaultListModel)listActions.getModel()).addElement(arg);
		
		listActions.setSelectedIndex(listActions.getModel().getSize()-1);
		listActions.ensureIndexIsVisible(listActions.getSelectedIndex());
		
		lblHandCount.setText(String.valueOf(player.getHand().size()));
		lblLibraryCount.setText(String.valueOf(player.getLibrary().size()));
	}

	private void clean() {
		handPanel.removeAll();
		panelBattleField.removeAll();
		panelGrave.removeAll();
		exilPanel.removeAll();
		
		handPanel.revalidate();
		panelBattleField.revalidate();
		panelGrave.revalidate();
		exilPanel.revalidate();
		
		handPanel.repaint();
		panelBattleField.repaint();
		panelGrave.repaint();
		exilPanel.repaint();
		
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
	
	
	public void addPlayer(Player p)
	{
		GameManager.getInstance().addPlayer(p);
		playerGameBoard = new PlayerGameBoard();
		playerGameBoard.setPlayer(p);
		playerGameBoard.getPanelInfo().setPreferredSize(new Dimension(panelInfo.getWidth(), panneauDroit.getHeight()/3));
		panneauDroit.add(playerGameBoard, BorderLayout.NORTH);
		
	}
	
	public void removePlayer()
	{
		panneauDroit.remove(playerGameBoard);
	}

	public JLabel getLblHandCount() {
		return lblHandCount;
	}

	public ManaPoolPanel getManaPoolPanel() {
		return manaPoolPanel;
	}
}
