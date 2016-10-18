package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.gui.components.charts.CmcChartPanel;
import org.magic.gui.models.DeckSelectionModel;
import org.magic.gui.renderer.ManaCellRenderer;

public class JDeckChooserDialog extends JDialog {
	
	JXTable table;
	JList<String> list;
	CmcChartPanel cmcChartPanel;
	MagicDeck selectedDeck;
	
	
	public MagicDeck getSelectedDeck() {
		return selectedDeck;
	}
	
	public static void main(String[] args) {
		new JDeckChooserDialog().setVisible(true);;
	}
	
	
	
	
	public JDeckChooserDialog() {
		setTitle("Choose your deck");
		setSize(750, 400);
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		
		addWindowListener(new WindowAdapter() 
					{
					  public void windowClosed(WindowEvent e)
					  {
					    selectedDeck=null;
					  }
			
					  public void windowClosing(WindowEvent e)
					  {
						  selectedDeck=null;
					  }
					});
		
		table = new JXTable(new DeckSelectionModel());
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				((DefaultListModel)list.getModel()).removeAllElements();
				
				selectedDeck = (MagicDeck)table.getModel().getValueAt(table.getSelectedRow(),0);
				
				for(MagicCard mc : selectedDeck.getMap().keySet())
					((DefaultListModel)list.getModel()).addElement( selectedDeck.getMap().get(mc)+" "+ mc);
				
				cmcChartPanel.init(selectedDeck);
				cmcChartPanel.revalidate();
				cmcChartPanel.repaint();
				
			}
		});
		
		scrollPane.setViewportView(table);
		
		JPanel panelBas = new JPanel();
		getContentPane().add(panelBas, BorderLayout.SOUTH);
		
		JButton btnSelect = new JButton("Select");
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(selectedDeck==null)
					JOptionPane.showMessageDialog(null, "Please choose a deck");
				else	
					dispose();
			}
		});
		panelBas.add(btnSelect);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectedDeck=null;
				dispose();
			}
		});
		panelBas.add(btnCancel);
		
		JPanel panelRight = new JPanel();
		getContentPane().add(panelRight, BorderLayout.EAST);
		panelRight.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane2 = new JScrollPane();
		panelRight.add(scrollPane2, BorderLayout.CENTER);
		list = new JList(new DefaultListModel<MagicCard>());
		scrollPane2.setViewportView(list);
		
		cmcChartPanel = new CmcChartPanel();
		cmcChartPanel.setPreferredSize(new Dimension(250, 150));
		panelRight.add(cmcChartPanel, BorderLayout.SOUTH);
		
		table.getColumnModel().getColumn(1).setCellRenderer(new ManaCellRenderer());
		table.packAll();
		setLocationRelativeTo(null);
		setModal(true);
	}
}

