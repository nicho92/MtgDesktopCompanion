package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.DeckSniffer;
import org.magic.gui.models.DeckSnifferModel;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGDesktopCompanionControler;
import org.magic.services.ThreadManager;

public class DeckSnifferDialog extends JDialog{
	private JTable table;
	private JComboBox cboSniffers ;
	private JComboBox cboFormats; 
	private DeckSnifferModel model;
	
	private MagicDeck importedDeck;
	private JLabel lblLoad;
	private JButton btnImport;
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
		
		cboSniffers = new JComboBox(MTGDesktopCompanionControler.getInstance().getEnabledDeckSniffer().toArray());
		cboSniffers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedSniffer=(DeckSniffer)cboSniffers.getSelectedItem();
			}
		});
		selectedSniffer = MTGDesktopCompanionControler.getInstance().getEnabledDeckSniffer().get(0);
		panel.add(cboSniffers);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					selectedSniffer.connect();
					cboFormats.removeAllItems();
					
					for(String s:selectedSniffer.listFilter())
						cboFormats.addItem(s);
					
					//cboFormats.setSelectedItem(selectedSniffer.getProperty("FORMAT"));
					
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, e1,"Error",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		panel.add(btnConnect);
		
		cboFormats = new JComboBox();
		cboFormats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					
					selectedSniffer.setProperties("FORMAT", cboFormats.getSelectedItem());
					model.init(selectedSniffer);
					model.fireTableDataChanged();
				} catch (Exception e1) {
//					e1.printStackTrace();
//					JOptionPane.showMessageDialog(null, e1,"Error",JOptionPane.ERROR_MESSAGE);
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
		
		btnImport = new JButton("Import");
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
					ThreadManager.getInstance().execute(new Runnable() {
						
						@Override
						public void run() {
							try {
								lblLoad.setVisible(true);
								btnImport.setEnabled(false);
								importedDeck =  selectedSniffer.getDeck((RetrievableDeck)model.getValueAt(table.getSelectedRow(), 0));
								lblLoad.setVisible(false);
								btnImport.setEnabled(true);
								dispose();
							} catch (Exception e1) {
								e1.printStackTrace();
								
								JOptionPane.showMessageDialog(null, e1,"Error",JOptionPane.ERROR_MESSAGE);
								importedDeck=null;
								lblLoad.setVisible(false);
								btnImport.setEnabled(true);
							}
							
						}
					}, "Import deck");					
			
			}
		});
		panel_1.add(btnImport);
		
		lblLoad = new JLabel("");
		lblLoad.setIcon(new ImageIcon(DeckSnifferDialog.class.getResource("/res/load.gif")));
		lblLoad.setVisible(false);
		panel_1.add(lblLoad);
		setLocationRelativeTo(null);
		
		table.getColumnModel().getColumn(1).setCellRenderer(new ManaCellRenderer());
	}

	public MagicDeck getSelectedDeck() {
			return importedDeck;
	}

}
