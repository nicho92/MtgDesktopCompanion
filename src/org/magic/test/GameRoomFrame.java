package org.magic.test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.MagicDeck;
import org.magic.game.Player;
import org.magic.gui.components.dialog.JDeckChooserDialog;

public class GameRoomFrame extends JFrame {
	private JTextField txtServer;
	private JTextField txtPort;
	private JTable table;
	private MinaClient client;
	private PlayerTableModel mod;
	
	public static void main(String[] args) {
		new GameRoomFrame().setVisible(true);
	}
	
	
	public GameRoomFrame() {
		setTitle("MTG Game Room");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		
		JLabel lblIp = new JLabel("IP : ");
		panel.add(lblIp);
		
		txtServer = new JTextField();
		txtServer.setText("127.0.0.1");
		panel.add(txtServer);
		txtServer.setColumns(10);
		
		JLabel lblPort = new JLabel("Port :");
		panel.add(lblPort);
		
		txtPort = new JTextField();
		txtPort.setText("18567");
		panel.add(txtPort);
		txtPort.setColumns(10);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				client = new MinaClient(txtServer.getText(), Integer.parseInt(txtPort.getText()));
				JDeckChooserDialog diag = new JDeckChooserDialog();
				diag.setVisible(true);
				MagicDeck d = diag.getSelectedDeck();
				Player p = new Player();
				p.setDeck(d);
				p.setName(JOptionPane.showInputDialog("Name ?"));
				client.join(p);
				//List<Player> list = client.listPlayers();
				client.listPlayers();
				//mod.setPlayers(list);
			}
		});
		panel.add(btnConnect);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		mod = new PlayerTableModel();
		table = new JTable(mod);
		scrollPane.setViewportView(table);
		pack();
	}

}

class PlayerTableModel extends DefaultTableModel
{
	
	private static final String[] columns = {"Player","Deck"};
	private List<Player> players ;
	
	public void setPlayers(List<Player> play)
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
		case 0: return players.get(row).getName();
		case 1: return players.get(row).getDeck();
		default: return null;
		}
	}
	
}


