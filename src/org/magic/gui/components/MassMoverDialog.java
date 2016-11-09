package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.interfaces.MagicDAO;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGDesktopCompanionControler;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

public class MassMoverDialog extends JDialog {
	private JTable tableCards;
	private MagicCardTableModel model;
	private MagicDAO dao;
	private MagicCollection toSaveCol;
	private boolean change=false;
	private JComboBox cboCollections;
	
	
	static final Logger logger = LogManager.getLogger(MassMoverDialog.class.getName());

	public MassMoverDialog(MagicCollection col) {
		setSize(new Dimension(640, 370));
		setTitle("Mass mover " + col);
		setModal(true);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		dao = MTGDesktopCompanionControler.getInstance().getEnabledDAO();
		this.toSaveCol=col;
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		JButton btnNewButton = new JButton("Move to");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if(tableCards.getSelectedRowCount()>0)
				{
					for (int i = 0; i < tableCards.getSelectedRowCount(); i++) 
					{ 
						int viewRow = tableCards.getSelectedRows()[i];
						int modelRow = tableCards.convertRowIndexToModel(viewRow);
						MagicCard mc = (MagicCard)tableCards.getModel().getValueAt(modelRow, 0);
						try {
							dao.removeCard(mc, toSaveCol);
							dao.saveCard(mc, (MagicCollection)cboCollections.getSelectedItem() );
							change=true;
						} 
						catch (SQLException e1) 
						{
							logger.error(e1);
							JOptionPane.showMessageDialog(null, e1,"ERROR",JOptionPane.ERROR_MESSAGE);
						}
					}
					try {
						model.init(dao.getCardsFromCollection(toSaveCol));
					} catch (SQLException e) {
						logger.error(e);
					}
					model.fireTableDataChanged();
				}
				
			}
		});
		panel.add(btnNewButton);
		
		cboCollections = null;
		try {
			cboCollections = new JComboBox(dao.getCollections().toArray(new MagicCollection[dao.getCollections().size()]));
		} catch (SQLException e) {
			logger.error(e);
		}
		panel.add(cboCollections);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		model = new MagicCardTableModel();
		try {
			model.init(dao.getCardsFromCollection(col));
		} catch (SQLException e) {
			logger.error(e);
		}
		
		tableCards = new JTable(model);
		tableCards.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
		
		scrollPane.setViewportView(tableCards);
		
		TableFilterHeader filterHeader = new TableFilterHeader(tableCards, AutoChoices.ENABLED);
		filterHeader.setSelectionBackground(Color.LIGHT_GRAY);
		//filterHeader.setTable(tableCards);
		//pack();
		setLocationRelativeTo(null);
	}

	public boolean hasChange() {
		return change;
	}

}
