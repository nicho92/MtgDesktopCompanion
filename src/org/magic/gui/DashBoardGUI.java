package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.AbstractDashBoard;
import org.magic.gui.components.CardsPicPanel;
import org.magic.gui.models.CardsShakerTableModel;
import org.magic.gui.renderer.CardShakeRenderer;
import java.awt.Dimension;

public class DashBoardGUI extends JPanel {
	private JTable tableEdition;
	private JTable tableStandard;
	private JTable tableModern;
	private JTable tableLegacy;
	private JTable tableVintage;
	private CardsPicPanel cardsPicPanel;
	
	private void showCard(int row, CardsShakerTableModel model) {
					 
		MagicCard mc;
		try {
			cardsPicPanel.showPhoto(((CardShake)model.getValueAt(row, 0)).getImg(), null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public DashBoardGUI() {
		setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panneauFormat = new JPanel();
		tabbedPane.addTab("Format", null, panneauFormat, null);
		panneauFormat.setLayout(new GridLayout(2, 2, 0, 0));
		
		JPanel panel_1 = new JPanel();
		panneauFormat.add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		tableStandard = new JTable();
		
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		panel_1.add(scrollPane_1);
		
		scrollPane_1.setViewportView(tableStandard);
		
		JLabel lblStandard = new JLabel("Standard");
		lblStandard.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblStandard.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblStandard, BorderLayout.NORTH);
		
		JPanel panel_2 = new JPanel();
		panneauFormat.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));
		tableLegacy = new JTable();
		
		
		JScrollPane scrollPane_2 = new JScrollPane();
		panel_2.add(scrollPane_2);
		
		scrollPane_2.setViewportView(tableLegacy);
		
		JLabel lblLegacy = new JLabel("Legacy");
		lblLegacy.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblLegacy.setHorizontalAlignment(SwingConstants.CENTER);
		panel_2.add(lblLegacy, BorderLayout.NORTH);
		
		JPanel panel_3 = new JPanel();
		panneauFormat.add(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		tableModern = new JTable();
		
		JScrollPane scrollPane_3 = new JScrollPane();
		panel_3.add(scrollPane_3, BorderLayout.CENTER);
		
		
		scrollPane_3.setViewportView(tableModern);
		
		JLabel lblModern = new JLabel("Modern");
		lblModern.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblModern.setHorizontalAlignment(SwingConstants.CENTER);
		panel_3.add(lblModern, BorderLayout.NORTH);
		
		JPanel panel_4 = new JPanel();
		panneauFormat.add(panel_4);
		panel_4.setLayout(new BorderLayout(0, 0));
		tableVintage = new JTable();
		
		JScrollPane scrollPane_4 = new JScrollPane();
		panel_4.add(scrollPane_4);
		
		scrollPane_4.setViewportView(tableVintage);
		
		JLabel lblVintage = new JLabel("Vintage");
		lblVintage.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblVintage.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(lblVintage, BorderLayout.NORTH);
		
		JPanel panneauEdition = new JPanel();
		tabbedPane.addTab("Editions", null, panneauEdition, null);
		panneauEdition.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panneauEdition.add(panel, BorderLayout.NORTH);
		
		JComboBox cboEdition = new JComboBox();
		panel.add(cboEdition);
		
		JScrollPane scrollPane = new JScrollPane();
		panneauEdition.add(scrollPane, BorderLayout.CENTER);
		
		tableEdition = new JTable();
		
		scrollPane.setViewportView(tableEdition);
		
		JPanel panneauHaut = new JPanel();
		add(panneauHaut, BorderLayout.NORTH);
		
		JComboBox comboBox = new JComboBox();
		panneauHaut.add(comboBox);
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Daily", "Weekly"}));
		
		cardsPicPanel = new CardsPicPanel();
		cardsPicPanel.setPreferredSize(new Dimension(250, 10));
		add(cardsPicPanel, BorderLayout.EAST);
		
		
		tableLegacy.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				showCard(tableLegacy.getSelectedRow(),(CardsShakerTableModel)tableLegacy.getModel());
			}
		});
		
		tableModern.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				showCard(tableModern.getSelectedRow(),(CardsShakerTableModel)tableModern.getModel());
			}
		});
		tableStandard.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				showCard(tableStandard.getSelectedRow(),(CardsShakerTableModel)tableStandard.getModel());
			}
		});
		tableVintage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				showCard(tableVintage.getSelectedRow(),(CardsShakerTableModel)tableVintage.getModel());
			}
		});
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				CardsShakerTableModel modModern = new CardsShakerTableModel();
									  modModern.init(AbstractDashBoard.FORMAT.modern);
				tableModern.setModel(modModern);
				
				modModern.fireTableDataChanged();
				
				CardsShakerTableModel modStandard = new CardsShakerTableModel();
									 modStandard.init(AbstractDashBoard.FORMAT.standard);
				tableStandard.setModel(modStandard);
				modStandard.fireTableDataChanged();
				
				CardsShakerTableModel modLegacy = new CardsShakerTableModel();
									  modLegacy.init(AbstractDashBoard.FORMAT.legacy);
				tableLegacy.setModel(modLegacy);
				modLegacy.fireTableDataChanged();
				
				CardsShakerTableModel modVintage = new CardsShakerTableModel();
									  modVintage.init(AbstractDashBoard.FORMAT.vintage);
				tableVintage.setModel(modVintage);
				modVintage.fireTableDataChanged();
				
				
				tableVintage.getColumnModel().getColumn(5).setCellRenderer(new CardShakeRenderer());
				tableModern.getColumnModel().getColumn(5).setCellRenderer(new CardShakeRenderer());
				tableStandard.getColumnModel().getColumn(5).setCellRenderer(new CardShakeRenderer());
				tableLegacy.getColumnModel().getColumn(5).setCellRenderer(new CardShakeRenderer());
				
				
			}
		}).start();
	}

}
