package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.gui.models.DeckSelectionModel;
import org.magic.gui.renderer.ManaCellRenderer;

public class DeckSelection extends JDialog {
	
	JXTable table;
	JList<MagicCard> list;
	public static void main(String[] args) {
		new DeckSelection().setVisible(true);;
	}
	
	public DeckSelection() {
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		table = new JXTable(new DeckSelectionModel());
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				((DefaultListModel)list.getModel()).removeAllElements();
				
				MagicDeck d = (MagicDeck)table.getModel().getValueAt(table.getSelectedRow(),0);
				
				for(MagicCard mc : d.getMap().keySet())
					((DefaultListModel)list.getModel()).addElement(mc);
				
				
			}
		});
		table.getColumnModel().getColumn(1).setCellRenderer(new ManaCellRenderer());
		
		
		scrollPane.setViewportView(table);
		
		JScrollPane scrollPane2 = new JScrollPane();
		list = new JList(new DefaultListModel<MagicCard>());
		scrollPane2.setViewportView(list);
		getContentPane().add(scrollPane2, BorderLayout.EAST);
		setLocationRelativeTo(null);
	}
}



