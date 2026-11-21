package org.magic.gui.components.network;

import static org.magic.services.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.api.beans.abstracts.AbstractMessage.MSG_TYPE;
import org.magic.api.beans.enums.EnumPlayerStatus;
import org.magic.api.beans.messages.DeckMessage;
import org.magic.api.beans.messages.SearchMessage;
import org.magic.api.beans.messages.SendStockMessage;
import org.magic.api.beans.messages.StatutMessage;
import org.magic.api.beans.messages.TechnicalMessage;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGNetworkClient;
import org.magic.game.model.beans.Player;
import org.magic.gui.StockPanelGUI;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.deck.ConstructPanel;
import org.magic.gui.components.deck.JDeckChooserDialog;
import org.magic.gui.components.dialog.importer.CardImporterDialog;
import org.magic.gui.components.widgets.JLangLabel;
import org.magic.gui.models.CardStockTableModel;
import org.magic.gui.renderer.PlayerRenderer;
import org.magic.main.IAVirtualUser;
import org.magic.servers.impl.ActiveMQServer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGDeckManager;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class NetworkChatPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private final JTextField txtServer;
	private final JList<Player> listPlayers;

	private final JButton btnConnect;
	private final JButton btnLogout;
	private final JTextArea txtChatMessageEditor;
	private final JComboBox<EnumPlayerStatus> cboStates;
	private final JButton btnColorChoose;
	private final JButton btnSearch;
	private final JButton btnDeck;
	private final JToggleButton btnIa;
	private final DefaultListModel<Player> listPlayerModel;
	private final JChatPanel chatPanel;
	private transient MTGNetworkClient client;
	private final CardStockTableModel stockResultModel;
	private final JTabbedPane tabbedPane;

	public NetworkChatPanel() {
		setLayout(new BorderLayout(0, 0));

		client = MTG.getEnabledPlugin(MTGNetworkClient.class);
		final var server = MTGControler.getInstance().get("network-config/network-last-server",
				MTGConstants.MTG_CHAT_DEFAULT_URI);

		btnLogout = new JButton(capitalize("LOGOUT"));
		btnConnect = new JButton(capitalize("CONNECT"));
		txtServer = new JTextField(server, 25);
		tabbedPane = new JTabbedPane();
		listPlayerModel = new DefaultListModel<>();
		stockResultModel = new CardStockTableModel();
		chatPanel = new JChatPanel();
		listPlayers = new JList<>(listPlayerModel);
		txtChatMessageEditor = new JTextArea();
		btnColorChoose = new JButton(MTGConstants.ICON_GAME_COLOR);
		cboStates = UITools.createCombobox(Arrays.asList(EnumPlayerStatus.values()).stream()
				.filter(s -> s != EnumPlayerStatus.DISCONNECTED).toList());
		btnSearch = UITools.createBindableJButton("", MTGConstants.ICON_SEARCH_24, KeyEvent.VK_S, "searchquery");
		btnDeck = UITools.createBindableJButton("", MTGConstants.ICON_DECK, KeyEvent.VK_F, "sharedeck");
		btnIa = new JToggleButton("", MTGConstants.ICON_IA);

		final var lblIp = new JLangLabel("HOST", true);

		final var panelChat = new JPanel();
		final var panelChatBox = new JPanel();
		final var panelSearch = new JScrollPane();
		final var tableResult = UITools.createNewTable(stockResultModel, true);

		panelChat.setBorder(new TitledBorder(null, "Chat", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		listPlayers.setBorder(new TitledBorder(null, "Online", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		btnLogout.setEnabled(false);

		panelChat.setLayout(new BorderLayout());
		panelChatBox.setLayout(new BorderLayout());

		listPlayers.setCellRenderer(new PlayerRenderer());

		txtChatMessageEditor.setText(capitalize("CHAT_INTRO_TEXT"));
		txtChatMessageEditor.setLineWrap(true);
		txtChatMessageEditor.setWrapStyleWord(true);
		txtChatMessageEditor.setRows(3);
		txtChatMessageEditor.setEditable(false);

		stockResultModel.setWritable(false);

		tabbedPane.addTab(capitalize("CHAT"), MTGConstants.ICON_TAB_CHAT, panelChat);
		tabbedPane.addTab(capitalize("SEARCH"), MTGConstants.ICON_SEARCH, panelSearch, null);

		add(UITools.createFlowCenterPanel(lblIp, txtServer, btnConnect, btnLogout, btnIa), BorderLayout.NORTH);
		add(new JScrollPane(listPlayers), BorderLayout.EAST);
		add(tabbedPane, BorderLayout.CENTER);

		panelChat.add(chatPanel, BorderLayout.CENTER);
		panelChat.add(panelChatBox, BorderLayout.SOUTH);
		panelChatBox.add(txtChatMessageEditor, BorderLayout.CENTER);
		panelChatBox.add(UITools.createFlowCenterPanel(cboStates, btnColorChoose, btnSearch, btnDeck),
				BorderLayout.NORTH);
		panelSearch.setViewportView(tableResult);

		initActions();

		if (MTG.readPropertyAsBoolean("network-config/online-autoconnect")) {
		    btnConnect.doClick();
		}

	}

	private void initActions() {

		chatPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					chatPanel.setSelectedIndex(e.getPoint());
					final var menu = new JPopupMenu();
					final var selected = chatPanel.getSelectedValue();

					if (selected.getTypeMessage() == MSG_TYPE.DECK) {
						final var deck = ((DeckMessage) selected).getAttachement();
						deck.setId(-1);

						final var itemImport = new JMenuItem("Import " + selected.getTypeMessage());
						itemImport.addActionListener(_ -> {
							try {
								new MTGDeckManager().saveDeck(deck);
							} catch (final IOException _) {
								logger.error(e);
							}
						});
						menu.add(itemImport);

						final var itemOpen = new JMenuItem("Open " + selected.getTypeMessage());
						itemOpen.addActionListener(_ -> {
							final var deckV = new ConstructPanel();
							deckV.hideSearchComponent(true);
							deckV.setDeck(deck);

							MTGUIComponent.createJDialog(deckV, true, true).setVisible(true);

						});
						menu.add(itemOpen);
					}

					if (selected.getTypeMessage() == MSG_TYPE.ANSWER) {
						final var items = ((SendStockMessage) selected).getResultItems();

						final var itemOpen = new JMenuItem("Open " + selected.getTypeMessage());
						itemOpen.addActionListener(_ -> {

							final var panel = new StockPanelGUI();

							for (final var mc : items) {
							    panel.addStock(mc);
							}

							MTGUIComponent.createJDialog(panel, true, true).setVisible(true);

						});
						menu.add(itemOpen);
					}
					menu.show(chatPanel.getComponent(), e.getPoint().x, e.getPoint().y);
				}
			}
		});

		btnConnect.addActionListener(_ -> {

			final var swConnect = new SwingWorker<Void, Void>() {

				@Override
				protected Void doInBackground() throws Exception {
					client.join(MTGControler.getInstance().getProfilPlayer(), txtServer.getText(),
							ActiveMQServer.DEFAULT_TOPIC);
					return null;
				}

				@Override
				protected void done() {

					try {
						get();

						txtServer.setEnabled(!client.isActive());
						btnConnect.setEnabled(!client.isActive());
						btnLogout.setEnabled(client.isActive());
						txtChatMessageEditor.setEditable(client.isActive());
						MTGControler.getInstance().setProperty("network-config/network-last-server",
								txtServer.getText());
					} catch (final InterruptedException ie) {
						Thread.currentThread().interrupt();
						logger.error(ie);
					} catch (final Exception e) {
						MTGControler.getInstance().notify(e);
					}

					if (client.isActive()) {
					    runningDaemon();
					}

				}

			};

			ThreadManager.getInstance().runInEdt(swConnect, "Connection to Server");

		});

		btnIa.addItemListener(ev -> {

			if (ev.getStateChange() == ItemEvent.SELECTED) {
				ThreadManager.getInstance().executeThread(new MTGRunnable() {

					@Override
					protected void auditedRun() {

						try {
							IAVirtualUser.init(txtServer.getText());

						} catch (final IOException e) {
							MTGControler.getInstance().notify(e);
						}
					}
				}, "MTGAssistant Agent");
			} else {
				try {
					IAVirtualUser.stop();
				} catch (final IOException e) {
					MTGControler.getInstance().notify(e);
				}
			}
		});

		btnLogout.addActionListener(_ -> {
			try {
				client.logout();
			} catch (final IOException e) {
				logger.error(e);
			}
		});

		btnColorChoose.addActionListener(_ -> {
			final var c = JColorChooser.showDialog(null, "Choose Text Color", client.getPlayer().getColor());
			if (c != null) {
				MTGControler.getInstance().setProperty("/game/player-profil/foreground", c.getRGB());
				client.getPlayer().setColor(c);
			}
		});

		txtChatMessageEditor.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent fe) {
				if (txtChatMessageEditor.getText().equals(capitalize("CHAT_INTRO_TEXT"))) {
				    txtChatMessageEditor.setText("");
				}
			}
		});

		txtChatMessageEditor.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_ENTER && !txtChatMessageEditor.getText().isEmpty()) {
					e.consume();
					try {
						client.sendMessage(txtChatMessageEditor.getText().trim());
					} catch (final IOException e1) {
						logger.error(e1);
					}
					txtChatMessageEditor.setText("");
				}

			}
		});

		btnSearch.addActionListener(_ -> {
			try {
				final var diag = new CardImporterDialog();
				diag.setVisible(true);

				if (diag.hasSelected()) {
					final var msg = new SearchMessage(diag.getSelectedItem());
					client.sendMessage(msg);
				}

			} catch (final IOException e1) {
				logger.error(e1);
			}
			tabbedPane.setSelectedIndex(1);

		});

		btnDeck.addActionListener(_ -> {
			try {
				final var diag = new JDeckChooserDialog();
				diag.setVisible(true);

				if (diag.getSelectedDeck() != null) {
					client.sendMessage(new DeckMessage(diag.getSelectedDeck()));
				}

			} catch (final IOException e1) {
				logger.error(e1);
			}
		});

		cboStates.addItemListener(ie -> {
			if (ie.getStateChange() == ItemEvent.SELECTED) {
			    try {
			    	client.changeStatus((EnumPlayerStatus) cboStates.getSelectedItem());
			    } catch (final IOException e1) {
			    	logger.error(e1);
			    }
			}
		});

	}

	private void runningDaemon() {
		final var sw = new SwingWorker<Void, AbstractMessage>() {

			@Override
			protected Void doInBackground() throws Exception {
				while (client.isActive()) {
					final var s = client.consume();
					if (s != null) {
					    publish(s);
					}
				}
				return null;
			}

			@Override
			protected void done() {

				try {
					get();
				} catch (final InterruptedException e) {
					Thread.currentThread().interrupt();
					logger.error(e);
				} catch (final Exception e) {
					logger.error(e);
				}

				txtServer.setEnabled(true);
				btnConnect.setEnabled(true);
				btnLogout.setEnabled(false);
				listPlayerModel.removeAllElements();
				txtChatMessageEditor.setEditable(false);
				chatPanel.chatClear();
			}

			@Override
			protected void process(List<AbstractMessage> chunks) {

				txtServer.setEnabled(false);
				btnConnect.setEnabled(false);
				btnLogout.setEnabled(true);
				txtChatMessageEditor.setEditable(true);

				for (final var s : chunks) {
					switch (s.getTypeMessage()) {
						case CHANGESTATUS :
							final var msg = (StatutMessage) s;
							switch (msg.getStatut()) {
								case ONLINE :
									listPlayerModel.addElement(s.getAuthor());
									break;
								case DISCONNECTED :
									listPlayerModel.removeElement(s.getAuthor());
									break;
								default :
									Collections.list(listPlayerModel.elements()).stream()
											.filter(p -> p.getId().equals(s.getAuthor().getId()))
											.forEach(p -> p.setState(msg.getStatut()));
									break;
							}
							break;

						case SYSTEM :
							final var tm = (TechnicalMessage) s;
							listPlayerModel.removeAllElements();
							listPlayerModel.addAll(tm.getPlayers());
							break;

						case SEARCH :
							final var msgs = (SearchMessage) s;
							chatPanel.chatAddMessage(msgs);

							if (!MTG.readPropertyAsBoolean("network-config/online-query")) {
							    break;
							}

							try {
								if (!msgs.getAuthor().getId().equals(client.getPlayer().getId())) {
									final var ret = MTG.getEnabledPlugin(MTGDao.class)
											.listStocks((MTGCard) msgs.getAttachement()).stream()
											.filter(mcs -> mcs.getQte() > 0).toList();
									if (!ret.isEmpty()) {
									    client.sendMessage(new SendStockMessage(msgs, ret));
									}
								}
							} catch (final Exception e) {
								logger.error(e);
							}
							break;

						case ANSWER :
							final var ret = ((SendStockMessage) s).getResultItems();

							for (final var c : ret) {
								c.setComment(s.getAuthor().getName());
								stockResultModel.addItem(c);
								stockResultModel.fireTableDataChanged();
							}

							break;

						default :
						    chatPanel.chatAddMessage(s);
							break;

					}

				}
			}
		};
		ThreadManager.getInstance().runInEdt(sw, "NetworkClient listening");
	}

	@Override
	public String getTitle() {
		return "Network";
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_CHAT;
	}

}
