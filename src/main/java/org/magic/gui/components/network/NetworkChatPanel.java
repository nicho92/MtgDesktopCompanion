package org.magic.gui.components.network;

import static org.magic.services.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.api.beans.abstracts.AbstractMessage.MSG_TYPE;
import org.magic.api.beans.enums.EnumPlayerStatus;
import org.magic.api.beans.game.Player;
import org.magic.api.beans.messages.DeckMessage;
import org.magic.api.beans.messages.SearchAnswerMessage;
import org.magic.api.beans.messages.SearchMessage;
import org.magic.api.beans.messages.StatutMessage;
import org.magic.api.beans.messages.UsersTechnicalMessage;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGNetworkClient;
import org.magic.gui.StockPanelGUI;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.deck.ConstructPanel;
import org.magic.gui.components.deck.JDeckChooserDialog;
import org.magic.gui.components.dialog.importer.CardImporterDialog;
import org.magic.gui.components.widgets.JLangLabel;
import org.magic.gui.models.CardStockTableModel;
import org.magic.gui.renderer.MessageRenderer;
import org.magic.gui.renderer.PlayerRenderer;
import org.magic.servers.impl.ActiveMQServer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGDeckManager;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class NetworkChatPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JTextField txtServer;
	private JList<Player> listPlayers;
	private JList<AbstractMessage> listMsg;
	private JButton btnConnect;
	private JButton btnLogout;
	private JTextArea editorPane;
	private JComboBox<EnumPlayerStatus> cboStates;
	private JButton btnColorChoose;
	private JButton btnSearch;
	private JButton btnDeck;
	private DefaultListModel<AbstractMessage> listMsgModel;
	private DefaultListModel<Player> listPlayerModel;
	private transient MTGNetworkClient client;
	private CardStockTableModel stockResultModel;
	private JTabbedPane tabbedPane;
	
	public NetworkChatPanel() {
		setLayout(new BorderLayout(0, 0));

		client = MTG.getEnabledPlugin(MTGNetworkClient.class);
		
		listMsgModel = new DefaultListModel<>();
		listPlayerModel= new DefaultListModel<>();
		listMsg = new JList<>(listMsgModel);
		listMsg.setBorder(new TitledBorder(null, "Chat", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		listPlayers = new JList<>(listPlayerModel);
		listPlayers.setBorder(new TitledBorder(null, "Online", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		btnLogout = new JButton(capitalize("LOGOUT"));
		var lblIp = new JLangLabel("HOST",true);
		btnConnect = new JButton(capitalize("CONNECT"));
		var panneauHaut = new JPanel();
		txtServer = new JTextField();
		var panneauBas = new JPanel();
		var panelChat = new JPanel();
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Chat",MTGConstants.ICON_TAB_CHAT,panelChat);
		
		editorPane = new JTextArea();
		var panel1 = new JPanel();
		btnColorChoose = new JButton(MTGConstants.ICON_GAME_COLOR);
		cboStates = UITools.createCombobox(Arrays.asList(EnumPlayerStatus.values()).stream().filter(s->s!=EnumPlayerStatus.CONNECTED).filter(s->s!=EnumPlayerStatus.DISCONNECTED).toList());
		var panelChatBox = new JPanel();
		
		
		//TODO MIGRATION PATCH TO REMOVE
		
		var server = MTGControler.getInstance().get("network-config/network-last-server", ActiveMQServer.DEFAULT_SERVER);
		if(server.equals("tcp://mtgcompanion.me:61616"))
				server = "tcp://my.mtgcompanion.org:61616";
		
		txtServer.setText(server);
		//////////////////////
		
		txtServer.setColumns(10);
		btnLogout.setEnabled(false);
		panelChat.setLayout(new BorderLayout());
		panelChatBox.setLayout(new BorderLayout());
		editorPane.setText(capitalize("CHAT_INTRO_TEXT"));
		editorPane.setLineWrap(true);
		editorPane.setWrapStyleWord(true);
		editorPane.setRows(3);
		listPlayers.setCellRenderer(new PlayerRenderer());
		listMsg.setCellRenderer(new MessageRenderer());
	 	listMsg.addMouseListener( new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e) {
				 if ( SwingUtilities.isRightMouseButton(e) ) {
					 listMsg.setSelectedIndex(listMsg.locationToIndex(e.getPoint()));
					    var menu = new JPopupMenu();
			            var selected = listMsg.getSelectedValue();
			            
			            if(selected.getTypeMessage()==MSG_TYPE.DECK) {
			            	var deck = ((DeckMessage)selected).getMagicDeck();
			        		deck.setId(-1);
			        		
				            var itemImport = new JMenuItem("Import " + selected.getTypeMessage());
				            itemImport.addActionListener(_->{
				            		try {
										new MTGDeckManager().saveDeck(deck);
									} catch (IOException _) {
										logger.error(e);
									}
				            });
				            menu.add(itemImport);
				            
				            var itemOpen = new JMenuItem("Open " + selected.getTypeMessage());
				            itemOpen.addActionListener(_->{
				            		ConstructPanel deckV = new ConstructPanel();
				            			deckV.hideSearchComponent(true);
				            			deckV.setDeck(deck);
				            			
										MTGUIComponent.createJDialog(deckV, true, true).setVisible(true);
									
				            });
				            menu.add(itemOpen);
			            }
			            
			            if(selected.getTypeMessage()==MSG_TYPE.ANSWER) {
			            	var items = ((SearchAnswerMessage)selected).getResultItems();
			        		
				            var itemOpen = new JMenuItem("Open " + selected.getTypeMessage());
				            itemOpen.addActionListener(_->{
				            		
				            	var panel = new StockPanelGUI();
				            	
				            	for(var mc : items)
				            		panel.addStock(mc);
				            	
				            	MTGUIComponent.createJDialog(panel, true, true).setVisible(true);
									
				            });
				            menu.add(itemOpen);
			            }
			            
			            
			            
			            
			            menu.show(listMsg, e.getPoint().x, e.getPoint().y);            
			        }
	        }

		});
		
		btnSearch = UITools.createBindableJButton("", MTGConstants.ICON_SEARCH_24,KeyEvent.VK_S,"searchquery");
		btnDeck = UITools.createBindableJButton("", MTGConstants.ICON_DECK,KeyEvent.VK_F,"deckquery");
		try {
			editorPane.setForeground(new Color(Integer.parseInt(MTGControler.getInstance().get("/game/player-profil/foreground"))));
		} catch (Exception _) {
			editorPane.setForeground(Color.BLACK);
		}

		add(panneauHaut, BorderLayout.NORTH);
		panneauHaut.add(lblIp);
		panneauHaut.add(txtServer);
		panneauHaut.add(btnConnect);
		panneauHaut.add(btnLogout);

		add(new JScrollPane(listPlayers), BorderLayout.EAST);
		add(panneauBas, BorderLayout.SOUTH);
		add(tabbedPane, BorderLayout.CENTER);
		
		
		panelChat.add(new JScrollPane(listMsg), BorderLayout.CENTER);
		panelChat.add(panelChatBox, BorderLayout.SOUTH);

		panelChatBox.add(editorPane, BorderLayout.CENTER);
		panelChatBox.add(panel1, BorderLayout.NORTH);

		panel1.add(cboStates);
		panel1.add(btnColorChoose);
		panel1.add(btnSearch);
		panel1.add(btnDeck);
		
		var panelSearch = new JScrollPane();
		tabbedPane.addTab("Search", MTGConstants.ICON_SEARCH, panelSearch, null);
		
		stockResultModel = new CardStockTableModel();
		stockResultModel.setWritable(false);
		var tableResult = UITools.createNewTable(stockResultModel, true );
		panelSearch.setViewportView(tableResult);
	
		
		initActions();
		
		
		if(MTG.readPropertyAsBoolean("network-config/online-autoconnect"))
			btnConnect.doClick();
		
	}

	private void initActions() {

		editorPane.setEditable(false);
		
	
		btnConnect.addActionListener(_ -> {
			
			var swConnect = new SwingWorker<Void, Void>(){
				
				@Override
				protected Void doInBackground() throws Exception {
					client.join(MTGControler.getInstance().getProfilPlayer(),  txtServer.getText(),ActiveMQServer.DEFAULT_TOPIC);
					return null;
				}
				
				@Override
				protected void done() {
					
					try {
						get();
						
						txtServer.setEnabled(!client.isActive());
						btnConnect.setEnabled(!client.isActive());
						btnLogout.setEnabled(client.isActive());
						editorPane.setEditable(client.isActive());
						MTGControler.getInstance().setProperty("network-config/network-last-server",txtServer.getText());
					} 
					catch(InterruptedException ie)
					{
						Thread.currentThread().interrupt();
						MTGControler.getInstance().notify(ie);
					}
					catch (Exception e) {
						MTGControler.getInstance().notify(e);
					}
				
					
					if(client.isActive())
						runningDaemon();
					
				}
				
			};
			
			ThreadManager.getInstance().runInEdt(swConnect, "Connection to Server");
			

			
			
		});



		btnLogout.addActionListener(_ -> {
			try {
				client.logout();
			} catch (IOException e) {
				logger.error(e);
			}
		});

		btnColorChoose.addActionListener(_ -> {
			var c = JColorChooser.showDialog(null, "Choose Text Color", editorPane.getForeground());

			if(c!=null) {
				editorPane.setForeground(c);
				MTGControler.getInstance().setProperty("/game/player-profil/foreground", c.getRGB());
			}
		});

		editorPane.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent fe) {
				if (editorPane.getText()
						.equals(capitalize("CHAT_INTRO_TEXT")))
					editorPane.setText("");
			}
		});

		editorPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(java.awt.event.KeyEvent e) {
				
				if (e.getKeyCode() == KeyEvent.VK_ENTER && !editorPane.getText().isEmpty()) {
					e.consume();
					try {
						client.sendMessage(editorPane.getText().trim(), editorPane.getForeground());
					} catch (IOException e1) {
						logger.error(e1);
					}
					editorPane.setText("");
				}

			}
		});
		
		
		btnSearch.addActionListener(_->{
			try 
			{
				var diag = new CardImporterDialog();
				diag.setVisible(true);
				
				if(diag.hasSelected())
				{
					var msg = new SearchMessage(diag.getSelectedItem());
					client.sendMessage(msg);
				}
				
			} catch (IOException e1) {
				logger.error(e1);
			}
			tabbedPane.setSelectedIndex(1);
			
		});
		
		btnDeck.addActionListener(_->{
			try 
			{
				var diag = new JDeckChooserDialog();
				diag.setVisible(true);
				
				if(diag.getSelectedDeck()!=null)
				{
					client.sendMessage(new DeckMessage(diag.getSelectedDeck()));
				}
				
			} catch (IOException e1) {
				logger.error(e1);
			}
		});

		cboStates.addItemListener(ie -> {
			if(ie.getStateChange()==ItemEvent.SELECTED)
				try {
					client.changeStatus((EnumPlayerStatus) cboStates.getSelectedItem());
				} catch (IOException e1) {
					logger.error(e1);
				}
		});

	}

	private void runningDaemon() {
		var sw = new SwingWorker<Void, AbstractMessage>(){

			@Override
			protected Void doInBackground() throws Exception {
				while(client.isActive())
				{
					var s = client.consume();
					if(s!=null)
						publish(s);
				}
				return null;
			}
			
			@Override
			protected void done() {
				
				try {
					get();
				}
				catch(InterruptedException e)
				{
					Thread.currentThread().interrupt();
					logger.error(e);
				}
				catch(Exception e)
				{
					logger.error(e);
				}
				
				
				txtServer.setEnabled(true);
				btnConnect.setEnabled(true);
				btnLogout.setEnabled(false);
				listPlayerModel.removeAllElements();
				editorPane.setEditable(false);
				listMsgModel.removeAllElements();
			}



			@Override
			protected void process(List<AbstractMessage> chunks) {
				
				txtServer.setEnabled(false);
				btnConnect.setEnabled(false);
				btnLogout.setEnabled(true);
				editorPane.setEditable(true);
				
				
				
				for(var s : chunks)
				{
				
					switch(s.getTypeMessage())
					{
						case CHANGESTATUS: 
								var msg = (StatutMessage)s;
								switch(msg.getStatut())
								{
									case CONNECTED : listPlayerModel.addElement(s.getAuthor());break;
									case DISCONNECTED:listPlayerModel.removeElement(s.getAuthor());break;
									default: Collections.list(listPlayerModel.elements()).stream().filter(p->p.getId().equals(s.getAuthor().getId())).forEach(p->p.setState(msg.getStatut()));break;
								}
								break;
						
						
						case SYSTEM : listPlayerModel.removeAllElements();
									  listPlayerModel.addAll(((UsersTechnicalMessage)s).getPlayers());
											  break;
							  
						case SEARCH: 
							var msgs = (SearchMessage)s;
							listMsgModel.addElement(msgs);
							
							if(!MTG.readPropertyAsBoolean("network-config/online-query"))
								break;
							
							try {
									if(!msgs.getAuthor().getId().equals(client.getPlayer().getId())) 
									{
												
											var ret = MTG.getEnabledPlugin(MTGDao.class).listStocks((MTGCard)msgs.getItem()).stream().filter(mcs->mcs.getQte()>0).toList();
											if(!ret.isEmpty())
												client.sendMessage(new SearchAnswerMessage(msgs, ret));
											
									} 
								}
								catch (Exception e) { 
									logger.error(e);
								}
								break;
								
						case ANSWER: 
							var ret = ((SearchAnswerMessage)s).getResultItems();
							
							for(var c : ret)
							{
								c.setComment(s.getAuthor().getName());
								stockResultModel.addItem(c);
								stockResultModel.fireTableDataChanged();
							}
							
							
							break;
								
							
						default:listMsgModel.addElement(s);break;
						
					}
				
				}
				
				listPlayers.updateUI();
				
				listMsg.ensureIndexIsVisible( listMsg.getModel().getSize() - 1 );
				
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


