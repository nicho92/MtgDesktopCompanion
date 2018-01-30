package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.magic.api.beans.MagicCollection;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

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
			MTGLogger.printStackTrace(e1);
		}
		
		panel.add(cboCollections);
		
		JPanel panel1 = new JPanel();
		getContentPane().add(panel1, BorderLayout.SOUTH);
		
		JButton btnClose = new JButton(MTGControler.getInstance().getLangService().getCapitalize("CANCEL"));
		btnClose.addActionListener(ae->dispose());
		
		panel1.add(btnClose);
		
		btnImport = new JButton(MTGControler.getInstance().getLangService().getCapitalize("SELECT"));
		btnImport.addActionListener(e->{
				selectedCollection = (MagicCollection)cboCollections.getSelectedItem();
				dispose();
		});
		panel1.add(btnImport);
		pack();
		setModal(true);
		setLocationRelativeTo(null);
	}

	public MagicCollection getSelectedCollection() {
			return selectedCollection;
	}

}
