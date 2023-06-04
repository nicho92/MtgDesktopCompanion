package org.magic.gui.components;

import static org.magic.services.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import org.magic.api.beans.JsonMessage;
import org.magic.api.beans.JsonMessage.MSG_TYPE;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.MTGNetworkClient;
import org.magic.game.model.Player;
import org.magic.game.model.Player.STATUS;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.widgets.JLangLabel;
import org.magic.gui.renderer.JsonMessageRenderer;
import org.magic.gui.renderer.PlayerRenderer;
import org.magic.servers.impl.ActiveMQServer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;


public class NetworkChatPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JTextField txtServer;
	private JList<Player> listPlayers;
	private JList<JsonMessage> listMsg;
	private JButton btnConnect;
	private JButton btnLogout;
	private JTextArea editorPane;
	private JComboBox<STATUS> cboStates;
	private JButton btnColorChoose;
	private JButton btnSearch;
	private DefaultListModel<JsonMessage> listMsgModel;
	private DefaultListModel<Player> listPlayerModel;
	private transient MTGNetworkClient client;

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
		var panel = new JPanel();
		editorPane = new JTextArea();
		var panel1 = new JPanel();
		btnColorChoose = new JButton(MTGConstants.ICON_GAME_COLOR);
		cboStates = UITools.createCombobox(STATUS.values());
		var panelChatBox = new JPanel();
		txtServer.setText(MTGControler.getInstance().get("network-config/network-last-server", "tcp://mtgcompanion.me:61616"));
		txtServer.setColumns(10);
		btnLogout.setEnabled(false);
		panel.setLayout(new BorderLayout());
		panelChatBox.setLayout(new BorderLayout());
		editorPane.setText(capitalize("CHAT_INTRO_TEXT"));
		editorPane.setLineWrap(true);
		editorPane.setWrapStyleWord(true);
		editorPane.setRows(3);
		
			
		btnSearch = new JButton("Search");

		try {
			editorPane.setForeground(new Color(Integer.parseInt(MTGControler.getInstance().get("/game/player-profil/foreground"))));
		} catch (Exception e) {
			editorPane.setForeground(Color.BLACK);
		}
		
		
		listPlayers.setCellRenderer(new PlayerRenderer());
		listMsg.setCellRenderer(new JsonMessageRenderer());

		add(panneauHaut, BorderLayout.NORTH);
		panneauHaut.add(lblIp);
		panneauHaut.add(txtServer);
		panneauHaut.add(btnConnect);
		panneauHaut.add(btnLogout);

		add(new JScrollPane(listPlayers), BorderLayout.EAST);
		add(panneauBas, BorderLayout.SOUTH);
		add(panel, BorderLayout.CENTER);
		
		
		panel.add(new JScrollPane(listMsg), BorderLayout.CENTER);
		panel.add(panelChatBox, BorderLayout.SOUTH);

		panelChatBox.add(editorPane, BorderLayout.CENTER);
		panelChatBox.add(panel1, BorderLayout.NORTH);

		panel1.add(btnColorChoose);
		panel1.add(cboStates);
		panel1.add(btnSearch);
		
		
		initActions();
		
		
		if(MTG.readPropertyAsBoolean("network-config/online-autoconnect"))
			btnConnect.doClick();
		
	}

	private void initActions() {

		editorPane.setEditable(false);
		
	
		btnConnect.addActionListener(ae -> {
			try {
				
				client.join(MTGControler.getInstance().getProfilPlayer(),  txtServer.getText(),ActiveMQServer.DEFAULT_ADDRESS);
				txtServer.setEnabled(!client.isActive());
				btnConnect.setEnabled(!client.isActive());
				btnLogout.setEnabled(client.isActive());
				editorPane.setEditable(client.isActive());
				MTGControler.getInstance().setProperty("network-config/network-last-server",txtServer.getText());
			} catch (Exception e) {
				MTGControler.getInstance().notify(e);
			}
			
			

			var sw = new SwingWorker<Void, JsonMessage>(){

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
					txtServer.setEnabled(true);
					btnConnect.setEnabled(true);
					btnLogout.setEnabled(false);
					listPlayerModel.removeAllElements();
					editorPane.setEditable(false);
					listMsgModel.removeAllElements();
				}



				@Override
				protected void process(List<JsonMessage> chunks) {
					
					txtServer.setEnabled(false);
					btnConnect.setEnabled(false);
					btnLogout.setEnabled(true);
					editorPane.setEditable(true);
					
					
					for(var s : chunks)
					{
						switch(s.getTypeMessage())
						{
						case CHANGESTATUS:Collections.list(listPlayerModel.elements()).stream().filter(p->p.getId().equals(s.getAuthor().getId())).forEach(p->p.setState(STATUS.valueOf(s.getMessage())));listPlayers.updateUI();break;
						
						case CONNECT:listPlayerModel.addElement(s.getAuthor());
											   listPlayers.updateUI();
											   if(!client.getPlayer().getId().equals(s.getAuthor().getId()) && client.getPlayer().getState()!=STATUS.BUSY)
												   MTGControler.getInstance().notify(new MTGNotification("New connection", s.getAuthor() + " is online", MESSAGE_TYPE.INFO)); 
											   break;
						case DISCONNECT:listPlayerModel.removeElement(s.getAuthor());listPlayers.updateUI();break;
						case SEARCH: try {
								MTG.getEnabledPlugin(MTGNetworkClient.class).searchStock(s);
							} catch (IOException e) {
								logger.error(e);
							}break;
						default:listMsgModel.addElement(s);break;
						
						}
					}
					
					listMsg.ensureIndexIsVisible( listMsg.getModel().getSize() - 1 );
					
				}

				
			};
			
			ThreadManager.getInstance().runInEdt(sw, "NetworkClient listening");
			
			
		});



		btnLogout.addActionListener(ae -> {
			try {
				client.logout();
			} catch (IOException e) {
				logger.error(e);
			}
		});

		btnColorChoose.addActionListener(ae -> {
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
				
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					e.consume();
					try {
						client.sendMessage(editorPane.getText().trim(), editorPane.getForeground(),MSG_TYPE.TALK);
					} catch (IOException e1) {
						logger.error(e1);
					}
					editorPane.setText("");
				}

			}
		});
		
		
		btnSearch.addActionListener(al->{
			try {
				client.sendMessage(editorPane.getText().trim(), editorPane.getForeground(),MSG_TYPE.SEARCH);
			} catch (IOException e1) {
				logger.error(e1);
			}
		});
		
		

		cboStates.addItemListener(ie -> {
			if(ie.getStateChange()==ItemEvent.SELECTED)
				try {
					client.changeStatus((STATUS) cboStates.getSelectedItem());
				} catch (IOException e1) {
					logger.error(e1);
				}
		});

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


