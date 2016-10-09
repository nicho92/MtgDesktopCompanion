package org.magic.gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTextField;

import org.magic.gui.models.CardAlertTableModel;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.ImageIcon;

public class AlarmGUI extends JPanel {
	private JTextField textField;
	private JTable table;
	public AlarmGUI() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		
		textField = new JTextField();
		panel.add(textField);
		textField.setColumns(20);
		
		JButton btnRemove = new JButton("");
		btnRemove.setIcon(new ImageIcon(AlarmGUI.class.getResource("/res/delete.png")));
		panel.add(btnRemove);
		
		JScrollPane scrollTable = new JScrollPane();
		add(scrollTable, BorderLayout.CENTER);
		
		table = new JTable(new CardAlertTableModel());
		scrollTable.setViewportView(table);
		
		JScrollPane scrollList = new JScrollPane();
		add(scrollList, BorderLayout.WEST);
		
		JList list = new JList();
		scrollList.setViewportView(list);
	}

}
