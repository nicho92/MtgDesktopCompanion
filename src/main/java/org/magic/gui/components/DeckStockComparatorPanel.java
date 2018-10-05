package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.SQLException;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.components.dialog.JDeckChooserDialog;
import org.magic.gui.models.DeckStockComparisonModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class DeckStockComparatorPanel extends JPanel {
	
	private JComboBox<MagicCollection> cboCollections;
	private JBuzyLabel buzyLabel;
	private DefaultComboBoxModel<MagicCollection> colMod;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private MagicDeck currentDeck;
	private DeckStockComparisonModel model;
	private JXTable table;
	private JButton btnCompare;
	private JButton btnExport;
	
	public void setCurrentDeck(MagicDeck c) {
		this.currentDeck = c;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
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
		
		initGUI();
		
		initActions();

		
		
	}

	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		btnCompare = new JButton("Compare");
		JPanel panneauHaut = new JPanel();
		JPanel panneauBas = new JPanel();
		btnExport = new JButton(MTGConstants.ICON_EXPORT);
		colMod = new DefaultComboBoxModel<>();
		cboCollections = new JComboBox<>(colMod);
		buzyLabel = new JBuzyLabel();
		model = new DeckStockComparisonModel();
		
		table = new JXTable();
		table.setModel(model);
		add(panneauHaut, BorderLayout.NORTH);
		panneauHaut.add(cboCollections);
		panneauHaut.add(btnCompare);
		add(panneauBas, BorderLayout.SOUTH);
		panneauBas.add(btnExport);
		panneauBas.add(buzyLabel);
		add(new JScrollPane(table), BorderLayout.CENTER);
		
		
		try {
			MTGControler.getInstance().getEnabled(MTGDao.class).getCollections().forEach(collection->colMod.addElement(collection));
		} catch (SQLException e) {
			logger.error("Error retrieving collections",e);
		}
		
	}

	private void initActions() {
		
		btnExport.addActionListener(ae-> {
			
			
		});
		
		
		btnCompare.addActionListener(ae-> {
			model.removeAll();
			if(currentDeck!=null)
			{
				MagicCollection col = (MagicCollection)cboCollections.getSelectedItem();
				currentDeck.getMap().entrySet().forEach(entry->{
					try {
						boolean has = MTGControler.getInstance().getEnabled(MTGDao.class).listCollectionFromCards(entry.getKey()).contains(col);
						List<MagicCardStock> stocks = MTGControler.getInstance().getEnabled(MTGDao.class).listStocks(entry.getKey(), col);
						int qty = currentDeck.getMap().get(entry.getKey());
						model.addRow(entry.getKey(),qty,has, stocks);
					} catch (SQLException e) {
						logger.error(e);
					}
				});
			}
		});
		
	}


}
