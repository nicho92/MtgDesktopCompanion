package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.log4j.Logger;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.MTGDeckSniffer;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.gui.components.JBuzyLabel;
import org.magic.gui.models.DeckSnifferTableModel;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;

public class DeckSnifferDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable table;
	private JComboBox<AbstractDeckSniffer> cboSniffers;
	private JComboBox<String> cboFormats;
	private DeckSnifferTableModel model;
	private MagicDeck importedDeck;
	private JBuzyLabel lblLoad;
	private JButton btnImport;
	private transient MTGDeckSniffer selectedSniffer;
	private JButton btnConnect;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public DeckSnifferDialog() {

		importedDeck = new MagicDeck();
		setSize(new Dimension(500, 300));
		setTitle(MTGControler.getInstance().getLangService().getCapitalize("DECKS_IMPORTER"));
		setIconImage(MTGConstants.ICON_DECK.getImage());
		setModal(true);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		model = new DeckSnifferTableModel();
		table.setModel(model);
		scrollPane.setViewportView(table);
		
		

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		cboSniffers = new JComboBox(MTGControler.getInstance().listEnabled(MTGDeckSniffer.class).toArray());

		cboSniffers.addActionListener(e -> selectedSniffer = (MTGDeckSniffer) cboSniffers.getSelectedItem());

		selectedSniffer = MTGControler.getInstance().listEnabled(MTGDeckSniffer.class).get(0);
		panel.add(cboSniffers);
		
		btnConnect = new JButton(MTGControler.getInstance().getLangService().getCapitalize("CONNECT"));
		btnConnect.addActionListener(e -> ThreadManager.getInstance().execute(() -> {
			try {
				lblLoad.buzy(true);
				selectedSniffer.connect();
				cboFormats.removeAllItems();

				for (String s : selectedSniffer.listFilter())
					cboFormats.addItem(s);

				
				lblLoad.buzy(false);

			} catch (Exception e1) {
				lblLoad.buzy(false);
				MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),e1));
			}
		}, "Connection to " + selectedSniffer));
		panel.add(btnConnect);

		cboFormats = new JComboBox<>();
		cboFormats.addActionListener(e -> {
			try {
				lblLoad.buzy(true);
				selectedSniffer.setProperty("FORMAT", cboFormats.getSelectedItem());
				model.init(selectedSniffer);
				model.fireTableDataChanged();
				lblLoad.buzy(false);
			} catch (Exception e1) {
				lblLoad.buzy(false);
				logger.error("error change cboFormat", e1);
				MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),e1));
			}
		});
		panel.add(cboFormats);

		lblLoad = new JBuzyLabel();
		panel.add(lblLoad);
	
		JPanel panel1 = new JPanel();
		getContentPane().add(panel1, BorderLayout.SOUTH);

		JButton btnClose = new JButton(MTGControler.getInstance().getLangService().getCapitalize("CANCEL"));
		btnClose.addActionListener(e -> dispose());
		panel1.add(btnClose);

		btnImport = new JButton(MTGControler.getInstance().getLangService().getCapitalize("IMPORT"));
		btnImport.addActionListener(e -> ThreadManager.getInstance().execute(() -> {
			try {
				lblLoad.buzy(true);
				btnImport.setEnabled(false);
				importedDeck = selectedSniffer.getDeck((RetrievableDeck) model.getValueAt(table.getSelectedRow(), 0)); 
				lblLoad.buzy(false);
				btnImport.setEnabled(true);
				dispose();
			} catch (Exception e1) {
				logger.error("Error snif",e1);
				MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),e1));
				importedDeck = null;
				lblLoad.buzy(false);
				btnImport.setEnabled(true);
			}
		}, "Import deck"));

		panel1.add(btnImport);
		setLocationRelativeTo(null);

		table.getColumnModel().getColumn(1).setCellRenderer(new ManaCellRenderer());
	}

	public MagicDeck getSelectedDeck() {
		return importedDeck;
	}

}
