package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicDAO;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

public class MassMoverDialog extends JDialog {
	private JTable tableCards;
	private MagicCardTableModel model;
	private MagicDAO dao;
	private MagicCollection toSaveCol;
	private MagicEdition toSaveEd;
	private boolean change=false;
	private JComboBox cboCollections;
	private JLabel lblWaiting;
	private JButton btnMove;
	
	Logger logger = MTGLogger.getLogger(this.getClass());

	public MassMoverDialog(MagicCollection col,MagicEdition ed) {
		setSize(new Dimension(640, 370));
		setTitle("Mass mover " + col);
		setModal(true);
		getContentPane().setLayout(new BorderLayout(0, 0));
		setLocationRelativeTo(null);
		
		
		dao = MTGControler.getInstance().getEnabledDAO();
		this.toSaveCol=col;
		this.toSaveEd=ed;
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		btnMove = new JButton("Move to");
	
		panel.add(btnMove);
		
		cboCollections = null;
		try {
			cboCollections = new JComboBox(dao.getCollections().toArray(new MagicCollection[dao.getCollections().size()]));
		} catch (SQLException e) {
			logger.error(e);
		}
		panel.add(cboCollections);
		
		lblWaiting = new JLabel("");
		lblWaiting.setVisible(false);
		lblWaiting.setIcon(new ImageIcon(MassMoverDialog.class.getResource("/res/load.gif")));
		panel.add(lblWaiting);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		model = new MagicCardTableModel();
		try {
			if(ed==null)
				model.init(dao.getCardsFromCollection(col));
			else
				model.init(dao.getCardsFromCollection(col,ed));
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
		
		
		btnMove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lblWaiting.setVisible(true);
				btnMove.setEnabled(false);
				
				if(tableCards.getSelectedRowCount()>0)
				{
					ThreadManager.getInstance().execute(new Runnable() {
						
						public void run() {
							
							for (int i = 0; i < tableCards.getSelectedRowCount(); i++) 
							{ 
								int viewRow = tableCards.getSelectedRows()[i];
								int modelRow = tableCards.convertRowIndexToModel(viewRow);
								MagicCard mc = (MagicCard)tableCards.getModel().getValueAt(modelRow, 0);
								try {
									dao.removeCard(mc, toSaveCol);
									dao.saveCard(mc, (MagicCollection)cboCollections.getSelectedItem() );
								//	dao.moveCards(toSaveCol, (MagicCollection)cboCollections.getSelectedItem(), mc);
									
									logger.info("moving " + mc +" to "  + cboCollections.getSelectedItem() );
									change=true;
									lblWaiting.setText("moving " + mc);
								} 
								catch (SQLException e1) 
								{
									logger.error(e1);
									JOptionPane.showMessageDialog(null, e1,"ERROR",JOptionPane.ERROR_MESSAGE);
									lblWaiting.setVisible(false);
									btnMove.setEnabled(true);
									
								}
							}
							
							try {
								lblWaiting.setText("update");
								if(toSaveEd==null)
									model.init(dao.getCardsFromCollection(toSaveCol));
								else
									model.init(dao.getCardsFromCollection(toSaveCol,toSaveEd));
							} catch (SQLException e) {
								logger.error(e);
								JOptionPane.showMessageDialog(null, e,"ERROR",JOptionPane.ERROR_MESSAGE);
							}
							
							model.fireTableDataChanged();
							lblWaiting.setVisible(false);
							btnMove.setEnabled(true);
						}
					}, "mass movement");
					
					
					
				}
			}
		});
		
	}

	public boolean hasChange() {
		return change;
	}

}
