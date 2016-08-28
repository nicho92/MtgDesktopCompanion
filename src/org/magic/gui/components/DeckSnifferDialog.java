package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.DeckSniffer;
import org.magic.gui.models.DeckSnifferModel;
import org.magic.services.MagicFactory;

public class DeckSnifferDialog extends JDialog{
	private JTable table;
	private JComboBox cboSniffers ;
	private JComboBox cboFormats; 
	private DeckSnifferModel model;
	
	private MagicDeck importedDeck;
	
	
	private DeckSniffer selectedSniffer;
	
	public DeckSnifferDialog() {
		setSize(new Dimension(500, 300));
		setTitle("Import Deck");
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		model = new DeckSnifferModel();
		table.setModel(model);
		scrollPane.setViewportView(table);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		
		cboSniffers = new JComboBox(MagicFactory.getInstance().getEnabledDeckSniffer().toArray());
		cboSniffers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedSniffer=(DeckSniffer)cboSniffers.getSelectedItem();
			}
		});
		selectedSniffer = MagicFactory.getInstance().getEnabledDeckSniffer().get(0);
		panel.add(cboSniffers);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					selectedSniffer.connect();
					cboFormats.removeAllItems();
					for(String s:selectedSniffer.listFilter())
						cboFormats.addItem(s);
					
					cboFormats.setSelectedItem(selectedSniffer.getProperty("FORMAT"));
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		panel.add(btnConnect);
		
		cboFormats = new JComboBox();
		cboFormats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					
					selectedSniffer.setProperties("FORMAT", cboFormats.getSelectedItem().toString());
					model.init(selectedSniffer);
					model.fireTableDataChanged();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		panel.add(cboFormats);
		
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);
		
		JButton btnClose = new JButton("Cancel");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		panel_1.add(btnClose);
		
		JButton btnImport = new JButton("Import");
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					importedDeck =  selectedSniffer.getDeck((RetrievableDeck)model.getValueAt(table.getSelectedRow(), 0));
					dispose();
				} catch (Exception e1) {
					e1.printStackTrace();
					importedDeck=null;
				}
			}
		});
		panel_1.add(btnImport);
		setLocationRelativeTo(null);
	}

	public MagicDeck getSelectedDeck() {
			return importedDeck;
	}

}
