package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;

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
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.gui.models.DeckSnifferModel;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;

public class DeckSnifferDialog extends JDialog{
	private JTable table;
	private JComboBox<AbstractDeckSniffer> cboSniffers ;
	private JComboBox<String> cboFormats; 
	private DeckSnifferModel model;
	
	private MagicDeck importedDeck;
	private JLabel lblLoad;
	private JButton btnImport;
	private transient DeckSniffer selectedSniffer;
	private JButton btnConnect;
	
	
	public DeckSnifferDialog() {
		setSize(new Dimension(500, 300));
		setTitle(MTGControler.getInstance().getLangService().getCapitalize("DECKS_IMPORTER"));
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		model = new DeckSnifferModel();
		table.setModel(model);
		scrollPane.setViewportView(table);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		
		cboSniffers = new JComboBox(MTGControler.getInstance().getEnabledDeckSniffer().toArray());
		
		cboSniffers.addActionListener(e->
				selectedSniffer=(DeckSniffer)cboSniffers.getSelectedItem()
		);
		
		selectedSniffer = MTGControler.getInstance().getEnabledDeckSniffer().get(0);
		panel.add(cboSniffers);
		
		btnConnect = new JButton(MTGControler.getInstance().getLangService().getCapitalize("CONNECT"));
		btnConnect.addActionListener(e->
				ThreadManager.getInstance().execute(()->{
						try {
							lblLoad.setVisible(true);
							selectedSniffer.connect();
							cboFormats.removeAllItems();
							
							for(String s:selectedSniffer.listFilter())
								cboFormats.addItem(s);
							
							lblLoad.setVisible(false);
							
						} catch (Exception e1) {
							lblLoad.setVisible(false);
							JOptionPane.showMessageDialog(null, e1,"Error",JOptionPane.ERROR_MESSAGE);
						}
					}, "Connection to " + selectedSniffer )
		);
		panel.add(btnConnect);
		
		cboFormats = new JComboBox<>();
		cboFormats.addActionListener(e->{
				try {
					lblLoad.setVisible(true);
					selectedSniffer.setProperties("FORMAT", cboFormats.getSelectedItem());
					model.init(selectedSniffer);
					model.fireTableDataChanged();
					lblLoad.setVisible(false);
				} catch (Exception e1) {
					lblLoad.setVisible(false);
					JOptionPane.showMessageDialog(null, e1,MTGControler.getInstance().getLangService().getCapitalize("ERROR"),JOptionPane.ERROR_MESSAGE);
				}
		});
		panel.add(cboFormats);
		
		lblLoad = new JLabel("");
		panel.add(lblLoad);
		lblLoad.setIcon(MTGConstants.ICON_LOADING);
		lblLoad.setVisible(false);
		
		JPanel panel1 = new JPanel();
		getContentPane().add(panel1, BorderLayout.SOUTH);
		
		JButton btnClose = new JButton(MTGControler.getInstance().getLangService().getCapitalize("CANCEL"));
		btnClose.addActionListener(e->dispose());
		panel1.add(btnClose);
		
		btnImport = new JButton(MTGControler.getInstance().getLangService().getCapitalize("IMPORT"));
		btnImport.addActionListener(e->
					ThreadManager.getInstance().execute(()->{
							try {
								lblLoad.setVisible(true);
								btnImport.setEnabled(false);
								importedDeck =  selectedSniffer.getDeck((RetrievableDeck)model.getValueAt(table.getSelectedRow(), 0));
								lblLoad.setVisible(false);
								btnImport.setEnabled(true);
								dispose();
							} catch (Exception e1) {
								MTGLogger.printStackTrace(e1);
								JOptionPane.showMessageDialog(null, e1,MTGControler.getInstance().getLangService().getCapitalize("PROVIDERS"),JOptionPane.ERROR_MESSAGE);
								importedDeck=null;
								lblLoad.setVisible(false);
								btnImport.setEnabled(true);
							}
					}, "Import deck")					
		);
		
		panel1.add(btnImport);
		setLocationRelativeTo(null);
		
		table.getColumnModel().getColumn(1).setCellRenderer(new ManaCellRenderer());
	}

	public MagicDeck getSelectedDeck() {
			return importedDeck;
	}

}
