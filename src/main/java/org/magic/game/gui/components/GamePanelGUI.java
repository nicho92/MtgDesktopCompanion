package org.magic.game.gui.components;

import static org.magic.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MagicDeck;
import org.magic.game.actions.library.DrawActions;
import org.magic.game.actions.library.DrawHandActions;
import org.magic.game.gui.components.dialog.DeckSideBoardSwitcherDialog;
import org.magic.game.model.GameManager;
import org.magic.game.model.Player;
import org.magic.gui.components.dialog.JDeckChooserDialog;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;


public class GamePanelGUI extends JPanel implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JSpinner spinLife;
	private JSpinner spinPoison;
	private HandPanel handPanel;
	private BattleFieldPanel panelBattleField;
	private ManaPoolPanel manaPoolPanel;
	private JPanel panneauDroit;
	private JList<AbstractAction> listActions;
	private JLabel lblPlayer;
	private Player player;
	private LibraryPanel panelLibrary;
	private GraveyardPanel panelGrave;
	private JLabel lblThumbnailPics;
	private LightDescribeCardPanel panneauHaut;
	private JLabel lblHandCount;
	private JLabel lblLibraryCount;
	private static GamePanelGUI instance;
	private JTextField txtChat;
	private JPanel panelInfo;
	private TurnsPanel turnsPanel;
	private ExilPanel exilPanel;
	private StackPanel stackPanel;
	
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public static GamePanelGUI getInstance() {
		if (instance == null)
			instance = new GamePanelGUI();

		return instance;
	}

	public TurnsPanel getTurnsPanel() {
		return turnsPanel;

	}

	public LibraryPanel getPanelLibrary() {
		return panelLibrary;
	}

	public static GamePanelGUI newInstance() {
		return new GamePanelGUI();
	}

	public BattleFieldPanel getPanelBattleField() {
		return panelBattleField;
	}

	public void initGame() {
		player.init();
	}

	public void setPlayer(Player p1) {
		player = p1;
		lblPlayer.setText(p1.getName());
		player.addObserver(this);
		spinLife.setValue(p1.getLife());
		spinPoison.setValue(p1.getPoisonCounter());
		handPanel.setPlayer(p1);
		panelGrave.setPlayer(p1);
		manaPoolPanel.setPlayer(p1);
		panelBattleField.setPlayer(p1);
		panelLibrary.setPlayer(p1);
		exilPanel.setPlayer(p1);
		
	}

	private GamePanelGUI() {

		setLayout(new BorderLayout(0, 0));

		panneauDroit = new JPanel();
		stackPanel=new StackPanel(true);
		
		GameManager.getInstance().getStack().addObserver(stackPanel);
		
		panelInfo = new JPanel();
			

		add(panneauDroit, BorderLayout.CENTER);
		panneauDroit.setLayout(new BorderLayout(0, 0));

		panneauDroit.add(panelInfo, BorderLayout.WEST);
		panelInfo.setLayout(new BorderLayout(0, 0));

		var panelActions = new JPanel();
		panelActions.setAlignmentY(Component.TOP_ALIGNMENT);
		panelInfo.add(panelActions, BorderLayout.SOUTH);
		var gblpanelActions = new GridBagLayout();
		gblpanelActions.columnWidths = new int[] { 30, 20, 0, 0 };
		gblpanelActions.rowHeights = new int[] { 0, 23, 0, 0, 0 };
		gblpanelActions.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gblpanelActions.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		panelActions.setLayout(gblpanelActions);
		
		var btnStart = new JButton(MTGConstants.PLAY_ICON);
				btnStart.setToolTipText(capitalize("START"));
				btnStart.addActionListener(ae -> {
					GameManager.getInstance().removePlayers();
					GameManager.getInstance().addPlayer(player);
					GameManager.getInstance().addPlayer(new Player(capitalize("PLAYER") + " 2", 20));
					GameManager.getInstance().initGame();
					manaPoolPanel.init(player.getManaPool());
					player.getManaPool().removeObserver(manaPoolPanel);
					player.getManaPool().addObserver(manaPoolPanel);
					((DefaultListModel<AbstractAction>) listActions.getModel()).removeAllElements();
					player.shuffleLibrary();
					turnsPanel.initTurn();
					new DrawHandActions().actionPerformed(ae);
					clean();
				});
						
				var btnSideboard = new JButton(MTGConstants.ICON_IMPORT);
								btnSideboard.setToolTipText(capitalize("SIDEBOARD"));
								btnSideboard.addActionListener(e -> {
									var gui = new DeckSideBoardSwitcherDialog(player.getDeck());
									gui.setVisible(true);
									player.setDeck(gui.getDeck());
								});
								
								var btnNewGame = new JButton(MTGConstants.ICON_OPEN);
										btnNewGame.setToolTipText(capitalize("CHOOSE_DECK"));
										btnNewGame.addActionListener(ae -> {
											var choose = new JDeckChooserDialog();
											choose.setVisible(true);
											try {
												MagicDeck deck = choose.getSelectedDeck();
												if (deck != null) {

													var p1 = MTGControler.getInstance().getProfilPlayer();
													p1.setDeck(deck);
													setPlayer(p1);
													GameManager.getInstance().initGame();
													
												}
											} catch (Exception e) {
												logger.error("Error loading deck", e);
											}
										});
										var gbcbtnNewGame = new GridBagConstraints();
										gbcbtnNewGame.fill = GridBagConstraints.BOTH;
										gbcbtnNewGame.insets = new Insets(0, 0, 5, 5);
										gbcbtnNewGame.gridx = 0;
										gbcbtnNewGame.gridy = 0;
										panelActions.add(btnNewGame, gbcbtnNewGame);
										var gbcbtnSideboard = new GridBagConstraints();
								gbcbtnSideboard.fill = GridBagConstraints.HORIZONTAL;
								gbcbtnSideboard.insets = new Insets(0, 0, 5, 5);
								gbcbtnSideboard.gridx = 1;
								gbcbtnSideboard.gridy = 0;
								panelActions.add(btnSideboard, gbcbtnSideboard);
				
								var gbcbtnStart = new GridBagConstraints();
						gbcbtnStart.fill = GridBagConstraints.HORIZONTAL;
						gbcbtnStart.insets = new Insets(0, 0, 5, 0);
						gbcbtnStart.gridx = 2;
						gbcbtnStart.gridy = 0;
						panelActions.add(btnStart, gbcbtnStart);

						var panel1 = new JPanel();
						var gbcpanel1 = new GridBagConstraints();
		gbcpanel1.gridheight = 2;
		gbcpanel1.gridwidth = 3;
		gbcpanel1.insets = new Insets(0, 0, 5, 0);
		gbcpanel1.fill = GridBagConstraints.BOTH;
		gbcpanel1.gridx = 0;
		gbcpanel1.gridy = 1;
		panelActions.add(panel1, gbcpanel1);
		panel1.setLayout(new BorderLayout(0, 0));

		txtChat = new JTextField(capitalize("SAY_SOMETHING"));
		panel1.add(txtChat);
		txtChat.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				txtChat.setText("");
			}
		});

		txtChat.addActionListener(ae -> {
			player.say(txtChat.getText());
			txtChat.setText("");
		});
		txtChat.setColumns(10);

		var panelPoolandDescribes = new JPanel();
		panelInfo.add(panelPoolandDescribes, BorderLayout.CENTER);

		panelPoolandDescribes.setLayout(new BorderLayout(0, 0));

		var panelPoolandHandsLib = new JPanel();
		panelPoolandDescribes.add(panelPoolandHandsLib, BorderLayout.NORTH);
		panelPoolandHandsLib.setLayout(new BorderLayout(0, 0));

		manaPoolPanel = new ManaPoolPanel();
		manaPoolPanel.setMinimumSize(new Dimension(0, 0));
		panelPoolandHandsLib.add(manaPoolPanel, BorderLayout.CENTER);

		var panelHandLib = new JPanel();
		panelPoolandHandsLib.add(panelHandLib, BorderLayout.SOUTH);
		panelHandLib.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		lblHandCount = new JLabel("0");
		lblHandCount.setFont(new Font(MTGControler.getInstance().getFont().getFontName(), Font.BOLD, 18));
		lblHandCount.setHorizontalTextPosition(SwingConstants.CENTER);
		lblHandCount.setIcon(MTGConstants.ICON_GAME_HAND);
		panelHandLib.add(lblHandCount);

		lblLibraryCount = new JLabel("");
		lblLibraryCount.setHorizontalTextPosition(SwingConstants.CENTER);
		lblLibraryCount.setHorizontalAlignment(SwingConstants.CENTER);
		lblLibraryCount.setFont(new Font(MTGControler.getInstance().getFont().getFontName(), Font.BOLD, 18));
		lblLibraryCount.setIcon(MTGConstants.ICON_GAME_LIBRARY);
		panelHandLib.add(lblLibraryCount);

		var lifePanel = new JPanel();
		panelPoolandHandsLib.add(lifePanel, BorderLayout.WEST);
		lifePanel.setAlignmentY(Component.TOP_ALIGNMENT);
		var gbllifePanel = new GridBagLayout();
		gbllifePanel.columnWidths = new int[] { 60, 0 };
		gbllifePanel.rowHeights = new int[] { 64, 0, 0 };
		gbllifePanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbllifePanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		lifePanel.setLayout(gbllifePanel);

		lblPlayer = new JLabel("");
		lblPlayer.setVerticalAlignment(SwingConstants.BOTTOM);
		lblPlayer.setIcon(MTGConstants.ICON_GAME_PLANESWALKER);
		var gbclblPlayer = new GridBagConstraints();
		gbclblPlayer.anchor = GridBagConstraints.WEST;
		gbclblPlayer.insets = new Insets(0, 0, 5, 0);
		gbclblPlayer.gridx = 0;
		gbclblPlayer.gridy = 0;
		lifePanel.add(lblPlayer, gbclblPlayer);

		var panel = new JPanel();
		var gbcpanel = new GridBagConstraints();
		gbcpanel.anchor = GridBagConstraints.NORTHWEST;
		gbcpanel.gridx = 0;
		gbcpanel.gridy = 1;
		lifePanel.add(panel, gbcpanel);
		panel.setLayout(new GridLayout(2, 2, 0, 0));

		var lblLife = new JLabel("");
		panel.add(lblLife);
		lblLife.setHorizontalAlignment(SwingConstants.CENTER);
		lblLife.setIcon(MTGConstants.ICON_GAME_LIFE);

		spinLife = new JSpinner();
		panel.add(spinLife);
		spinLife.setFont(new Font(MTGControler.getInstance().getFont().getFontName(), Font.BOLD, 17));

		var lblPoison = new JLabel("");
		panel.add(lblPoison);
		lblPoison.setHorizontalAlignment(SwingConstants.CENTER);
		lblPoison.setIcon(MTGConstants.ICON_GAME_POISON);

		spinPoison = new JSpinner();
		panel.add(spinPoison);
		spinPoison.setFont(new Font(MTGControler.getInstance().getFont().getFontName(), Font.BOLD, 15));

		spinPoison.addChangeListener(e -> {
			if (player != null)
				player.setPoisonCounter((int) spinPoison.getValue());

		});
		spinLife.addChangeListener(e -> {
			if (player != null)
				player.setLife((int) spinLife.getValue());
		});

		var tabbedPane = new JTabbedPane(SwingConstants.TOP);
		panelPoolandDescribes.add(tabbedPane, BorderLayout.CENTER);


		listActions = new JList<>();
		listActions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listActions.setModel(new DefaultListModel<>());
		var scrollActions = new JScrollPane(listActions);
		scrollActions.setPreferredSize(new Dimension((int) MTGControler.getInstance().getCardsGameDimension().getWidth(), 0));
		tabbedPane.addTab(capitalize("EVENTS"), null, scrollActions,null);

		
		
		var pane = new JPanel();
		pane.setLayout(new BorderLayout());

		panneauHaut = new LightDescribeCardPanel();
		pane.add(panneauHaut, BorderLayout.CENTER);

		tabbedPane.addTab(capitalize("DESCRIPTION"), MTGConstants.ICON_TAB_DETAILS, pane, null);
		tabbedPane.addTab(capitalize("STACK"), MTGConstants.ICON_TAB_DECK, stackPanel, null);
		
		
		var panelPics = new JPanel();
		tabbedPane.addTab(capitalize("PICTURES"), MTGConstants.ICON_TAB_PICTURE, panelPics, null);
		panelPics.setLayout(new BorderLayout(0, 0));

		lblThumbnailPics = new JLabel("");
		lblThumbnailPics.setHorizontalTextPosition(SwingConstants.CENTER);
		lblThumbnailPics.setHorizontalAlignment(SwingConstants.CENTER);
		panelPics.add(lblThumbnailPics);

		var panelLibraryAndGrave = new JPanel();
		panneauDroit.add(panelLibraryAndGrave, BorderLayout.EAST);
		panelLibraryAndGrave.setLayout(new BorderLayout(0, 0));

		var panelDeck = new JPanel();
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

				if (SwingUtilities.isLeftMouseButton(me)) {
					new DrawActions().actionPerformed(new ActionEvent(me.getSource(), me.getID(), me.paramString()));
				}

			}
		});
		var js = new JScrollPane();
		panelGrave = new GraveyardPanel();
		js.setViewportView(panelGrave);
		panelLibraryAndGrave.add(js);

		panelBattleField = new BattleFieldPanel();
		panneauDroit.add(panelBattleField, BorderLayout.CENTER);
		panelBattleField.setLayout(null);

		var panelBottom = new JPanel();
		panneauDroit.add(panelBottom, BorderLayout.SOUTH);

		handPanel = new HandPanel();
		handPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		handPanel.enableDragging(true);
		handPanel.setThumbnailSize(MTGControler.getInstance().getCardsGameDimension());
		panelBottom.setLayout(new BorderLayout(0, 0));
		handPanel.setRupture(7);

		var scrollPane = new JScrollPane(handPanel);
		panelBottom.add(scrollPane);
		scrollPane.setPreferredSize(new Dimension(2, handPanel.getCardHeight()));

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

	public GraveyardPanel getPanelGrave() {
		return panelGrave;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void update(Observable o, Object arg) {
		((DefaultListModel) listActions.getModel()).addElement(arg);

		listActions.setSelectedIndex(listActions.getModel().getSize() - 1);
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

	public void describeCard(DisplayableCard mc) {
		
		panneauHaut.setCard(mc.getMagicCard());
		lblThumbnailPics.setIcon(new ImageIcon(mc.getFullResPics().getScaledInstance(223, 310, Image.SCALE_SMOOTH)));
	}

	public void addPlayer(Player p) {
		GameManager.getInstance().addPlayer(p);
	}


	public JLabel getLblHandCount() {
		return lblHandCount;
	}

	
	public JLabel getLblLibraryCount() {
		return lblLibraryCount;
	}
	
	public ManaPoolPanel getManaPoolPanel() {
		return manaPoolPanel;
	}
}
