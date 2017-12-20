package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.magic.api.beans.MagicCollection;
import org.magic.services.MTGControler;

public class CollectionChooserDialog extends JDialog{
	private JComboBox<MagicCollection> cboCollections ;
	private JButton btnImport;
	private MagicCollection selectedCollection;
	
	
	public CollectionChooserDialog() {
		setSize(new Dimension(500, 130));
		setTitle("Select Collection");
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		
		cboCollections = new JComboBox();
		
		try {
			for(MagicCollection col : MTGControler.getInstance().getEnabledDAO().getCollections())
				cboCollections.addItem(col);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		panel.add(cboCollections);
		
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);
		
		JButton btnClose = new JButton("Cancel");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		panel_1.add(btnClose);
		
		btnImport = new JButton("Select");
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedCollection = (MagicCollection)cboCollections.getSelectedItem();
				dispose();
			
			}
		});
		panel_1.add(btnImport);
		pack();
		setModal(true);
		setLocationRelativeTo(null);
	}

	public MagicCollection getSelectedCollection() {
			return selectedCollection;
	}

}
