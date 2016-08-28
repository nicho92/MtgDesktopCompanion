package org.magic.gui.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.interfaces.MagicDAO;
import org.magic.services.MagicFactory;

public class ConfigurationPanel extends JPanel {
	private JTextField textField;
	private JComboBox cboTargetDAO;
	
	
	public ConfigurationPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblBackupDao = new JLabel("Backup dao : ");
		GridBagConstraints gbc_lblBackupDao = new GridBagConstraints();
		gbc_lblBackupDao.anchor = GridBagConstraints.EAST;
		gbc_lblBackupDao.insets = new Insets(0, 0, 5, 5);
		gbc_lblBackupDao.gridx = 1;
		gbc_lblBackupDao.gridy = 1;
		add(lblBackupDao, gbc_lblBackupDao);
		
		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 2;
		gbc_textField.gridy = 1;
		add(textField, gbc_textField);
		textField.setColumns(10);
		
		JButton btnBackup = new JButton("backup");
		btnBackup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		GridBagConstraints gbc_btnBackup = new GridBagConstraints();
		gbc_btnBackup.insets = new Insets(0, 0, 5, 5);
		gbc_btnBackup.gridx = 3;
		gbc_btnBackup.gridy = 1;
		add(btnBackup, gbc_btnBackup);
		
		JLabel lblDuplicateDb = new JLabel("Duplicate DB :");
		GridBagConstraints gbc_lblDuplicateDb = new GridBagConstraints();
		gbc_lblDuplicateDb.anchor = GridBagConstraints.EAST;
		gbc_lblDuplicateDb.insets = new Insets(0, 0, 0, 5);
		gbc_lblDuplicateDb.gridx = 1;
		gbc_lblDuplicateDb.gridy = 2;
		add(lblDuplicateDb, gbc_lblDuplicateDb);
		
		cboTargetDAO = new JComboBox();
		
		for(MagicDAO daos :  MagicFactory.getInstance().getDaoProviders())
			if(!daos.getName().equals(MagicFactory.getInstance().getEnabledDAO().getName()))
			{
				cboTargetDAO.addItem(daos);
			}
		
		GridBagConstraints gbc_cboTargetDAO = new GridBagConstraints();
		gbc_cboTargetDAO.insets = new Insets(0, 0, 0, 5);
		gbc_cboTargetDAO.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboTargetDAO.gridx = 2;
		gbc_cboTargetDAO.gridy = 2;
		add(cboTargetDAO, gbc_cboTargetDAO);
		
		JButton btnDuplicate = new JButton("duplicate");
		btnDuplicate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
						MagicDAO dao = (MagicDAO)cboTargetDAO.getSelectedItem();
						dao.init();
						for(MagicCollection col : MagicFactory.getInstance().getEnabledDAO().getCollections())
							for(MagicCard mc : MagicFactory.getInstance().getEnabledDAO().getCardsFromCollection(col))
								{
									dao.saveCard(mc, col);
								}
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		GridBagConstraints gbc_btnDuplicate = new GridBagConstraints();
		gbc_btnDuplicate.insets = new Insets(0, 0, 0, 5);
		gbc_btnDuplicate.gridx = 3;
		gbc_btnDuplicate.gridy = 2;
		add(btnDuplicate, gbc_btnDuplicate);
	}

}
