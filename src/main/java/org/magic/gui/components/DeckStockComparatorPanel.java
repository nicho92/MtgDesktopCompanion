package org.magic.gui.components;

import java.awt.BorderLayout;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.components.dialog.JDeckChooserDialog;
import org.magic.gui.models.conf.MapTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import javax.swing.JTable;

public class DeckStockComparatorPanel extends JPanel {
	
	private JComboBox<MagicCollection> cboCollections;
	private JBuzyLabel buzyLabel;
	private DefaultComboBoxModel<MagicCollection> colMod;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private MagicDeck currentDeck;
	private MapTableModel<MagicCard, Integer> model;
	private JTable table;
	
	public void setCurrentDeck(MagicDeck c) {
		this.currentDeck = c;
		logger.debug("set deck " + currentDeck);
		
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		MTGControler.getInstance().getEnabled(MTGDao.class).init();

		JFrame f = new JFrame();
		JDeckChooserDialog diag = new JDeckChooserDialog();
		diag.setVisible(true);
		MagicDeck d = diag.getSelectedDeck();
		DeckStockComparatorPanel pane = new DeckStockComparatorPanel();
		pane.setCurrentDeck(d);
		f.getContentPane().add(pane);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
		
	}
	
	
	public DeckStockComparatorPanel() {
		setLayout(new BorderLayout(0, 0));
		JButton btnCompare = new JButton("Compare");
		JPanel panneauHaut = new JPanel();
		JPanel panneauBas = new JPanel();
		JButton btnExport = new JButton(MTGConstants.ICON_EXPORT);
		
		colMod = new DefaultComboBoxModel<>();
		cboCollections = new JComboBox<>(colMod);
		buzyLabel = new JBuzyLabel();
		model = new MapTableModel<>();
		add(panneauHaut, BorderLayout.NORTH);
		panneauHaut.add(cboCollections);
		panneauHaut.add(btnCompare);
		add(panneauBas, BorderLayout.SOUTH);
		panneauBas.add(btnExport);
		panneauBas.add(buzyLabel);
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable(model);
		scrollPane.setViewportView(table);
		
		try {
			MTGControler.getInstance().getEnabled(MTGDao.class).getCollections().forEach(collection->colMod.addElement(collection));
		} catch (SQLException e) {
			logger.error("Error retrieving collections",e);
		}
		
		btnCompare.addActionListener(ae-> {
			model.removeAll();
			if(currentDeck!=null)
			{
				MagicCollection col = (MagicCollection)cboCollections.getSelectedItem();
				currentDeck.getMap().entrySet().forEach(entry->{
					try {
						boolean has = MTGControler.getInstance().getEnabled(MTGDao.class).listCollectionFromCards(entry.getKey()).contains(col);
						
						
						
						
						model.addRow(entry.getKey(), 1);
						if(has)
						  System.out.println("found " + entry.getKey() + " in "+ col);
						
						MTGControler.getInstance().getEnabled(MTGDao.class).listStocks(entry.getKey(), col).forEach(st->{
									System.out.println(st.getQte() + " "+  st.getCondition());
						});	
						
					} catch (SQLException e) {
						logger.error(e);
					}
					
					
				});
				
			
			}
		});

		
		
	}


}
