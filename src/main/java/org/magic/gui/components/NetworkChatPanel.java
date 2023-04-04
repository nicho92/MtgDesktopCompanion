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
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGNetworkClient;
import org.magic.api.network.impl.ActiveMQNetworkClient;
import org.magic.game.model.Player;
import org.magic.game.model.Player.STATUS;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.widgets.JLangLabel;
import org.magic.gui.renderer.JsonMessageRenderer;
import org.magic.gui.renderer.PlayerRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;


public class NetworkChatPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JTextField txtServer;
	private JList<Player> listPlayers;
	private transient MTGNetworkClient client;
	private JList<JsonMessage> listMsg;
	private JButton btnConnect;
	private JButton btnLogout;
	private JTextArea editorPane;
	private JComboBox<STATUS> cboStates;
	private JButton btnColorChoose;
	private JButton btnSearch;
	private JsonExport serializer;
	private JScrollPane scroll;
	private DefaultListModel<JsonMessage> listMsgModel;
	private DefaultListModel<Player> listPlayerModel;

	public NetworkChatPanel() {
		setLayout(new BorderLayout(0, 0));

		
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
		scroll = new JScrollPane(listMsg);
		txtServer.setText("tcp://localhost:61616");
		txtServer.setColumns(10);
		btnLogout.setEnabled(false);
		panel.setLayout(new BorderLayout());
		panelChatBox.setLayout(new BorderLayout());
		editorPane.setText(capitalize("CHAT_INTRO_TEXT"));
		editorPane.setLineWrap(true);
		editorPane.setWrapStyleWord(true);
		editorPane.setRows(2);
		btnSearch = new JButton("Search");
		serializer = new JsonExport();
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
		
		
		panel.add(scroll, BorderLayout.CENTER);
		panel.add(panelChatBox, BorderLayout.SOUTH);

		panelChatBox.add(editorPane, BorderLayout.CENTER);
		panelChatBox.add(panel1, BorderLayout.NORTH);

		panel1.add(btnColorChoose);
		panel1.add(cboStates);
		panel1.add(btnSearch);
		
		initActions();
	}

	private void initActions() {

		
		btnConnect.addActionListener(ae -> {
			try {
				client = new ActiveMQNetworkClient();
				
				client.join(MTGControler.getInstance().getProfilPlayer(),  txtServer.getText(),"welcome");
				

			} catch (Exception e) {
				MTGControler.getInstance().notify(e);
			}
			
			txtServer.setEnabled(false);
			btnConnect.setEnabled(false);
			btnLogout.setEnabled(true);
			
			var sw = new SwingWorker<Void, JsonMessage>(){

				@Override
				protected Void doInBackground() throws Exception {
					while(client.isActive())
					{
						var s = client.consume();
						var json = serializer.fromJson(s, JsonMessage.class);
						publish(json);
					}
					return null;
				}
				
				@Override
				protected void done() {
					txtServer.setEnabled(true);
					btnConnect.setEnabled(true);
					btnLogout.setEnabled(false);
					listPlayerModel.removeAllElements();
					listMsgModel.removeAllElements();
				}



				@Override
				protected void process(List<JsonMessage> chunks) {
					
					txtServer.setEnabled(false);
					btnConnect.setEnabled(false);
					btnLogout.setEnabled(true);
					
					for(var s : chunks)
					{
						switch(s.getTypeMessage())
						{
						case CHANGESTATUS:Collections.list(listPlayerModel.elements()).stream().filter(p->p.getId().equals(s.getAuthor().getId())).forEach(p->p.setState(STATUS.valueOf(s.getMessage())));listPlayers.updateUI();break;
						case CONNECT:listPlayerModel.addElement(s.getAuthor());listPlayers.updateUI();break;
						case DISCONNECT:listPlayerModel.removeElement(s.getAuthor());listPlayers.updateUI();break;
						case TALK:listMsgModel.addElement(s);break;
						default:break;
						
						}
					}
					
					
					
					var vscp = scroll.getVerticalScrollBar();
					vscp.setValue(vscp.getMaximum());
					
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
						client.sendMessage(editorPane.getText().trim(), editorPane.getForeground());
					} catch (IOException e1) {
						logger.error(e1);
					}
					editorPane.setText("");
				}

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


