package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.MagicDeck;
import org.magic.game.model.Player;
import org.magic.game.network.MinaClient;
import org.magic.game.network.actions.ListPlayersAction;
import org.magic.game.network.actions.ReponseAction;
import org.magic.game.network.actions.ReponseAction.CHOICE;
import org.magic.game.network.actions.RequestPlayAction;
import org.magic.game.network.actions.SpeakAction;
import org.magic.gui.components.dialog.JDeckChooserDialog;
import org.magic.services.ThreadManager;

public class GamingRoomPanel extends JPanel {
	private JTextField txtServer;
	private JTextField txtPort;
	private JTable table;
	private MinaClient client;
	private PlayerTableModel mod;
	private JTextField txtName;
	private JList list = new JList(new DefaultListModel());
	private JButton btnPlayGame;
	private JButton btnConnect;
	
	
	Player p = new Player();
	Player otherplayer =null;
	
	private void printMessage(String text)
	{
		((DefaultListModel)list.getModel()).addElement(text);
	}
	
	
	private void printMessage(SpeakAction sa) {
		
		if(sa.getP()==null)
			((DefaultListModel)list.getModel()).addElement(sa.getText());
		else
			((DefaultListModel)list.getModel()).addElement(sa.getP() +": "+sa.getText());
		
	}
	

	private Observer obs = new Observer() {
		
		@Override
		public void update(Observable o, Object arg) {
			if(arg instanceof ListPlayersAction)
			{
				ListPlayersAction lpa = (ListPlayersAction)arg;
			//	lpa.getList().remove(p);
				mod.init(lpa.getList());
			}
			
			if(arg instanceof SpeakAction)
			{
				SpeakAction lpa = (SpeakAction)arg;
					printMessage(lpa);
			}
		

			if(arg instanceof RequestPlayAction)
			{
				RequestPlayAction lpa = (RequestPlayAction)arg;
				int res = JOptionPane.showConfirmDialog(null, lpa.getRequestPlayer() +" ask you to play a game. Accept ?","New Game Request !",JOptionPane.YES_NO_OPTION);
				
				if(res==JOptionPane.YES_OPTION)
				{
					client.reponse(lpa,CHOICE.YES);
					
					//GamePanelGUI.getInstance().setPlayer(p);
					//GamePanelGUI.getInstance().initGame();
				}
				else
				{
					client.reponse(lpa,CHOICE.NO);
				}
				
			}
			
			if(arg instanceof ReponseAction)
			{
				ReponseAction resp = (ReponseAction)arg;
				switch(resp.getReponse())
				{
					case YES: printMessage("Challenge Accepted ! " + resp.getRequest().getRequestPlayer() + " vs " + resp.getRequest().getAskedPlayer()); break;
					case NO: printMessage(resp.getRequest().getAskedPlayer() +" decline your challenge");break;
				}
			}
			
		}

	};
	
	public GamingRoomPanel() {
		setLayout(new BorderLayout(0, 0));
		final JButton btnLogout = new JButton("Logout");
		JPanel panneauHaut = new JPanel();
		add(panneauHaut, BorderLayout.NORTH);
		
		JLabel lblIp = new JLabel("Host : ");
		panneauHaut.add(lblIp);
		
		txtServer = new JTextField();
		txtServer.setText("127.0.0.1");
		panneauHaut.add(txtServer);
		txtServer.setColumns(10);
		
		JLabel lblPort = new JLabel("Port :");
		panneauHaut.add(lblPort);
		
		txtPort = new JTextField();
		txtPort.setText("18567");
		panneauHaut.add(txtPort);
		txtPort.setColumns(10);
		
		btnConnect = new JButton("Connect");
		
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{ 
					client = new MinaClient(txtServer.getText(), Integer.parseInt(txtPort.getText()));
					client.addObserver(obs);
					p.setName(txtName.getText());
					client.join(p);
					
					ThreadManager.getInstance().execute(new Runnable() {
						public void run() {
							while(client.getSession().isActive())
							{
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
	
						}
				}, "live connection");
				
				}
				catch(Exception e)
				{
					JOptionPane.showMessageDialog(null, e,"ERROR",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		JLabel lblName = new JLabel("Name :");
		panneauHaut.add(lblName);
		
		txtName = new JTextField();
		panneauHaut.add(txtName);
		txtName.setColumns(10);
		panneauHaut.add(btnConnect);
		
		
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				client.sendMessage("logged out");
				client.logout();
			}
		});
		btnLogout.setEnabled(false);
		panneauHaut.add(btnLogout);
		
		mod = new PlayerTableModel();
		table = new JTable(mod);
		
		JScrollPane scrollPane = new JScrollPane();
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int modelrow= table.convertRowIndexToModel(table.getSelectedRow());
				otherplayer = (Player) table.getModel().getValueAt(modelrow, 0);
				
				if(otherplayer!=null)
					if(otherplayer.getDeck()!=null)
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
		btnPlayGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int res = JOptionPane.showConfirmDialog(null, "Want to play with " + otherplayer+" ?","Gaming request",JOptionPane.YES_NO_OPTION);
				if(res==JOptionPane.YES_OPTION)
					client.requestPlay(otherplayer);
			}
		});
		btnPlayGame.setEnabled(false);
		panneauBas.add(btnPlayGame);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.EAST);
		panel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panel.add(scrollPane_1, BorderLayout.CENTER);
		
		scrollPane_1.setViewportView(list);
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		final JTextArea editorPane = new JTextArea();
		editorPane.setLineWrap(true);
		editorPane.setWrapStyleWord(true);
		editorPane.setRows(3);
		
		panel_1.add(editorPane, BorderLayout.CENTER);
		
		JButton btnColorChoose = new JButton("New button");
		btnColorChoose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Color c = JColorChooser.showDialog(null, "Choose Text Color", Color.BLACK);
				editorPane.setForeground(c);
			}
		});
		panel_1.add(btnColorChoose, BorderLayout.NORTH);
	
		editorPane.addKeyListener(new KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{ 
				 client.sendMessage(editorPane.getText());
				 editorPane.setText("");
				}
				
			};
			
		});
		
		btnDeck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				JDeckChooserDialog diag = new JDeckChooserDialog();
				diag.setVisible(true);
				MagicDeck d = diag.getSelectedDeck();
				client.updateDeck(d);
				
			}
		});
		
	}

}

class PlayerTableModel extends DefaultTableModel
{
	
	private static final String[] columns = {"Player","Deck","Format", "Country"};
	private List<Player> players ;
	
	public void init(List<Player> play)
	{
		this.players=play;
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
		if(players==null)
			return 0;
		
		return players.size();
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		switch(column)
		{
		case 0: return players.get(row);
		case 1: return players.get(row).getDeck();
		case 2: return players.get(row).getDeck().getLegality();
		case 3: return players.get(row).getLocal();
		default: return null;
		}
	}
	
}


