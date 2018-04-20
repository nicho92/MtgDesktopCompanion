package org.magic.game.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicDeck;
import org.magic.game.model.Player;
import org.magic.game.model.Player.STATE;
import org.magic.game.network.MinaClient;
import org.magic.game.network.actions.AbstractNetworkAction;
import org.magic.game.network.actions.ListPlayersAction;
import org.magic.game.network.actions.ReponseAction;
import org.magic.game.network.actions.ReponseAction.CHOICE;
import org.magic.game.network.actions.RequestPlayAction;
import org.magic.game.network.actions.ShareDeckAction;
import org.magic.game.network.actions.SpeakAction;
import org.magic.gui.components.dialog.JDeckChooserDialog;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public class GamingRoomPanel extends JPanel {

	private JTextField txtServer;
	private JTextField txtPort;
	private JTable table;
	private transient MinaClient client;
	private PlayerTableModel mod;
	private JTextField txtName;
	private JList list = new JList<>(new DefaultListModel<>());
	private JButton btnPlayGame;
	private JButton btnConnect;

	Player otherplayer = null;

	private transient Observer obs = new Observer() {

		private void printMessage(AbstractNetworkAction sa) {
			((DefaultListModel) list.getModel()).addElement(sa);
		}

		@Override
		public void update(Observable o, Object arg) {
			if (arg instanceof ShareDeckAction) {
				ShareDeckAction lpa = (ShareDeckAction) arg;
				printMessage(lpa);
			}
			if (arg instanceof ListPlayersAction) {
				ListPlayersAction lpa = (ListPlayersAction) arg;
				mod.init(lpa.getList());
			}
			if (arg instanceof SpeakAction) {
				SpeakAction lpa = (SpeakAction) arg;
				printMessage(lpa);
			}
			if (arg instanceof RequestPlayAction) {
				RequestPlayAction lpa = (RequestPlayAction) arg;
				int res = JOptionPane.showConfirmDialog(getRootPane(),
						MTGControler.getInstance().getLangService().getCapitalize("CHALLENGE_REQUEST",
								lpa.getRequestPlayer()),
						MTGControler.getInstance().getLangService().getCapitalize("NEW_GAME_REQUEST"),
						JOptionPane.YES_NO_OPTION);

				if (res == JOptionPane.YES_OPTION) {
					client.reponse(lpa, CHOICE.YES);
				} else {
					client.reponse(lpa, CHOICE.NO);
				}
			}
			if (arg instanceof ReponseAction) {
				ReponseAction resp = (ReponseAction) arg;
				if (resp.getReponse().equals(ReponseAction.CHOICE.YES)) {
					printMessage(new SpeakAction(resp.getRequest().getAskedPlayer(),
							MTGControler.getInstance().getLangService().getCapitalize("CHALLENGE_ACCEPTED")));
					client.changeStatus(STATE.GAMING);
					GamePanelGUI.getInstance().setPlayer(client.getP());
					GamePanelGUI.getInstance().addPlayer(resp.getRequest().getAskedPlayer());
					GamePanelGUI.getInstance().initGame();
				} else {
					printMessage(new SpeakAction(resp.getRequest().getAskedPlayer(),
							" " + MTGControler.getInstance().getLangService().getCapitalize("CHALLENGE_DECLINE")));
				}
			}
		}
	};

	public GamingRoomPanel() {
		setLayout(new BorderLayout(0, 0));
		final JButton btnLogout = new JButton(MTGControler.getInstance().getLangService().getCapitalize("LOGOUT"));
		JPanel panneauHaut = new JPanel();
		add(panneauHaut, BorderLayout.NORTH);

		JLabel lblIp = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("HOST") + " :");
		panneauHaut.add(lblIp);

		txtServer = new JTextField();
		txtServer.setText("");
		panneauHaut.add(txtServer);
		txtServer.setColumns(10);

		JLabel lblPort = new JLabel("Port :");
		panneauHaut.add(lblPort);

		txtPort = new JTextField();
		txtPort.setText("18567");
		panneauHaut.add(txtPort);
		txtPort.setColumns(10);

		btnConnect = new JButton(MTGControler.getInstance().getLangService().getCapitalize("CONNECT"));

		btnConnect.addActionListener(ae -> {
			try {
				client = new MinaClient(txtServer.getText(), Integer.parseInt(txtPort.getText()));
				client.addObserver(obs);
				client.getP().setName(txtName.getText());
				client.join();

				ThreadManager.getInstance().execute(() -> {
					while (client.getSession().isActive()) {
						txtName.setEnabled(!client.getSession().isActive());
						txtServer.setEnabled(!client.getSession().isActive());
						txtPort.setEnabled(!client.getSession().isActive());
						btnConnect.setEnabled(false);
						btnLogout.setEnabled(true);
					}
					txtName.setEnabled(true);
					txtServer.setEnabled(true);
					txtPort.setEnabled(true);
					btnConnect.setEnabled(true);
					btnLogout.setEnabled(false);
				}, "live connection");

			} catch (Exception e) {
				JOptionPane.showMessageDialog(getRootPane(), e, MTGControler.getInstance().getLangService().getError(),
						JOptionPane.ERROR_MESSAGE);
			}
		});

		JLabel lblName = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("NAME") + " :");
		panneauHaut.add(lblName);

		txtName = new JTextField();
		panneauHaut.add(txtName);
		txtName.setColumns(10);
		txtName.setText(MTGControler.getInstance().get("/game/player-profil/name"));
		panneauHaut.add(btnConnect);

		btnLogout.addActionListener(ae -> {
			client.sendMessage("logged out");
			client.logout();
		});
		btnLogout.setEnabled(false);
		panneauHaut.add(btnLogout);

		list.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);
				try {
					label.setForeground(((SpeakAction) value).getColor());
				} catch (Exception e) {
					// do nothing
				}
				return label;
			}
		});

		mod = new PlayerTableModel();
		table = new JTable(mod);
		table.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
		JScrollPane scrollPane = new JScrollPane();
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int modelrow = table.convertRowIndexToModel(table.getSelectedRow());
				otherplayer = (Player) table.getModel().getValueAt(modelrow, 0);

				if (otherplayer != null && otherplayer.getDeck() != null)
					btnPlayGame.setEnabled(true);

			}
		});
		add(scrollPane, BorderLayout.CENTER);

		scrollPane.setViewportView(table);

		JPanel panneauBas = new JPanel();
		add(panneauBas, BorderLayout.SOUTH);

		JButton btnDeck = new JButton("Change deck");
		panneauBas.add(btnDeck);

		btnPlayGame = new JButton("Ask for Game");
		btnPlayGame.addActionListener(e -> {
			int res = JOptionPane.showConfirmDialog(getRootPane(), "Want to play with " + otherplayer + " ?",
					"Gaming request", JOptionPane.YES_NO_OPTION);
			if (res == JOptionPane.YES_OPTION)
				client.requestPlay(otherplayer);
		});
		btnPlayGame.setEnabled(false);
		panneauBas.add(btnPlayGame);

		JPanel panel = new JPanel();
		add(panel, BorderLayout.EAST);
		panel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_1 = new JScrollPane();
		panel.add(scrollPane_1, BorderLayout.CENTER);

		scrollPane_1.setViewportView(list);

		JPanel panelChatBox = new JPanel();
		panel.add(panelChatBox, BorderLayout.SOUTH);
		panelChatBox.setLayout(new BorderLayout(0, 0));

		final JTextArea editorPane = new JTextArea();
		editorPane.setText(MTGControler.getInstance().getLangService().getCapitalize("CHAT_INTRO_TEXT"));
		editorPane.setLineWrap(true);
		editorPane.setWrapStyleWord(true);
		editorPane.setRows(2);
		try {
			editorPane.setForeground(
					new Color(Integer.parseInt(MTGControler.getInstance().get("/game/player-profil/foreground"))));
		} catch (Exception e) {
			editorPane.setForeground(Color.BLACK);
		}

		panelChatBox.add(editorPane, BorderLayout.CENTER);

		JPanel panel_1 = new JPanel();
		panelChatBox.add(panel_1, BorderLayout.NORTH);

		JButton btnShareDeck = new JButton("");
		btnShareDeck.addActionListener(ae -> {
			JDeckChooserDialog diag = new JDeckChooserDialog();
			diag.setVisible(true);
			MagicDeck d = diag.getSelectedDeck();
			Player p = (Player) table.getModel().getValueAt(table.getSelectedRow(), 0);
			client.sendDeck(d, p);
		});
		btnShareDeck.setToolTipText("Share a deck");
		btnShareDeck.setIcon(MTGConstants.ICON_COLLECTION_SMALL);
		panel_1.add(btnShareDeck);

		JButton btnColorChoose = new JButton("");
		btnColorChoose.setIcon(MTGConstants.ICON_COLORS);
		panel_1.add(btnColorChoose);

		final JComboBox cboStates = new JComboBox(new DefaultComboBoxModel<STATE>(STATE.values()));
		cboStates.addItemListener(ie -> client.changeStatus((STATE) cboStates.getSelectedItem()));

		panel_1.add(cboStates);
		btnColorChoose.addActionListener(ae -> {
			Color c = JColorChooser.showDialog(null, "Choose Text Color", Color.BLACK);
			editorPane.setForeground(c);
			MTGControler.getInstance().setProperty("/game/player-profil/foreground", c.getRGB());
		});

		editorPane.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent fe) {
				if (editorPane.getText()
						.equals(MTGControler.getInstance().getLangService().getCapitalize("CHAT_INTRO_TEXT")))
					editorPane.setText("");
			}
		});

		editorPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					client.sendMessage(editorPane.getText(), editorPane.getForeground());
					editorPane.setText("");
				}

			}

		});

		btnDeck.addActionListener(ae -> {

			JDeckChooserDialog diag = new JDeckChooserDialog();
			diag.setVisible(true);
			MagicDeck d = diag.getSelectedDeck();
			client.updateDeck(d);
		});

	}

}

class PlayerTableModel extends DefaultTableModel {

	private static final String[] columns = { MTGControler.getInstance().getLangService().getCapitalize("PLAYER"),
			MTGControler.getInstance().getLangService().getCapitalize("DECK"),
			MTGControler.getInstance().getLangService().getCapitalize("CARD_COLOR"),
			MTGControler.getInstance().getLangService().getCapitalize("FORMAT"),
			MTGControler.getInstance().getLangService().getCapitalize("COUNTRY"),
			MTGControler.getInstance().getLangService().getCapitalize("STATE") };
	private List<Player> players;

	public void init(List<Player> play) {
		this.players = play;
		fireTableDataChanged();
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public String getColumnName(int column) {
		return columns[column];
	}

	@Override
	public int getRowCount() {
		if (players == null)
			return 0;

		return players.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		switch (column) {
		case 0:
			return players.get(row);
		case 1:
			return players.get(row).getDeck();
		case 2:
			return players.get(row).getDeck().getColors();
		case 3:
			return players.get(row).getDeck().getLegality();
		case 4:
			return players.get(row).getLocal();
		case 5:
			return players.get(row).getState();
		default:
			return null;
		}
	}

}
