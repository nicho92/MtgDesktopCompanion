package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.gui.models.CardAlertTableModel;

public class AlarmGUI extends JPanel {
	private JTable table;
	private CardAlertTableModel model;
	
	
	public AlarmGUI() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		
		JScrollPane scrollTable = new JScrollPane();
		add(scrollTable, BorderLayout.CENTER);
		
		model = new CardAlertTableModel();
		
		table = new JTable();
		table.setModel(model);
		scrollTable.setViewportView(table);
	}

}
