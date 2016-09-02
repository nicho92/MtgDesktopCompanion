package org.magic.gui.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicDAO;
import org.magic.services.MagicFactory;
import org.magic.services.ThreadManager;
import org.magic.tools.db.NumberUpdater;

import javax.swing.ImageIcon;

public class ConfigurationPanel extends JPanel {
	private JTextField textField;
	private JComboBox cboTargetDAO;
	private JComboBox cboCollections;
	private JComboBox cboLogLevels;
	private JLabel lblLoading ;
	private JTextField txtdirWebsite;
	private JComboBox cboEditions;
	
	public void loading(boolean show,String text)
	{
		lblLoading.setText(text);
		lblLoading.setVisible(show);
	}
	
	public ConfigurationPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 106, 212, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 27, 0, 21, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblBackupDao = new JLabel("Backup dao file : ");
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
				
					ThreadManager.getInstance().execute(new Runnable() {
						
						@Override
						public void run() {
							try {
								loading(true,"backup " + MagicFactory.getInstance().getEnabledDAO() +" database");
								MagicFactory.getInstance().getEnabledDAO().backup(new File(textField.getText()));
								loading(false,"backup " + MagicFactory.getInstance().getEnabledDAO() +" end");
								
							} 
							catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					}, "backup " + MagicFactory.getInstance().getEnabledDAO() +" database");
					
				
			}
		});
		GridBagConstraints gbc_btnBackup = new GridBagConstraints();
		gbc_btnBackup.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnBackup.insets = new Insets(0, 0, 5, 5);
		gbc_btnBackup.gridx = 3;
		gbc_btnBackup.gridy = 1;
		add(btnBackup, gbc_btnBackup);
		
		JLabel lblDuplicateDb = new JLabel("Duplicate DB :");
		GridBagConstraints gbc_lblDuplicateDb = new GridBagConstraints();
		gbc_lblDuplicateDb.anchor = GridBagConstraints.EAST;
		gbc_lblDuplicateDb.insets = new Insets(0, 0, 5, 5);
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
		gbc_cboTargetDAO.insets = new Insets(0, 0, 5, 5);
		gbc_cboTargetDAO.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboTargetDAO.gridx = 2;
		gbc_cboTargetDAO.gridy = 2;
		add(cboTargetDAO, gbc_cboTargetDAO);
		
		JButton btnDuplicate = new JButton("duplicate");
		btnDuplicate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
						MagicDAO dao = (MagicDAO)cboTargetDAO.getSelectedItem();
						loading(true,"duplicate " + MagicFactory.getInstance().getEnabledDAO() +" database to" + dao);
						
						dao.init();
						for(MagicCollection col : MagicFactory.getInstance().getEnabledDAO().getCollections())
							for(MagicCard mc : MagicFactory.getInstance().getEnabledDAO().getCardsFromCollection(col))
								{
									dao.saveCard(mc, col);
								}
						
					loading(false,"");
				}catch(Exception e)
				{
					loading(false,"");
					e.printStackTrace();
				}
			}
		});
		GridBagConstraints gbc_btnDuplicate = new GridBagConstraints();
		gbc_btnDuplicate.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnDuplicate.insets = new Insets(0, 0, 5, 5);
		gbc_btnDuplicate.gridx = 3;
		gbc_btnDuplicate.gridy = 2;
		add(btnDuplicate, gbc_btnDuplicate);
		
		JLabel lblMainCol = new JLabel("Main Collection :");
		GridBagConstraints gbc_lblMainCol = new GridBagConstraints();
		gbc_lblMainCol.anchor = GridBagConstraints.EAST;
		gbc_lblMainCol.insets = new Insets(0, 0, 5, 5);
		gbc_lblMainCol.gridx = 1;
		gbc_lblMainCol.gridy = 3;
		add(lblMainCol, gbc_lblMainCol);
		
		cboCollections = new JComboBox();
		try {
			for(MagicCollection col :  MagicFactory.getInstance().getEnabledDAO().getCollections())
			{
				cboCollections.addItem(col);
				if(col.getName().equalsIgnoreCase(MagicFactory.getInstance().get("default-library")))
				{
					cboCollections.setSelectedItem(col);
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		GridBagConstraints gbc_cboCollections = new GridBagConstraints();
		gbc_cboCollections.insets = new Insets(0, 0, 5, 5);
		gbc_cboCollections.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboCollections.gridx = 2;
		gbc_cboCollections.gridy = 3;
		add(cboCollections, gbc_cboCollections);
		
		JButton btnSave = new JButton("Save");
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSave.insets = new Insets(0, 0, 5, 5);
		gbc_btnSave.gridx = 3;
		gbc_btnSave.gridy = 3;
		add(btnSave, gbc_btnSave);
		
		JLabel lblReloadConfig = new JLabel("Reload Config :");
		GridBagConstraints gbc_lblReloadConfig = new GridBagConstraints();
		gbc_lblReloadConfig.anchor = GridBagConstraints.EAST;
		gbc_lblReloadConfig.insets = new Insets(0, 0, 5, 5);
		gbc_lblReloadConfig.gridx = 1;
		gbc_lblReloadConfig.gridy = 4;
		add(lblReloadConfig, gbc_lblReloadConfig);
		
		JButton btnReload = new JButton("Reload");
		btnReload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loading(true,"reload config");
				MagicFactory.getInstance().reload();
				loading(false,"");
				
			}
		});
		GridBagConstraints gbc_btnReload = new GridBagConstraints();
		gbc_btnReload.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnReload.insets = new Insets(0, 0, 5, 5);
		gbc_btnReload.gridx = 3;
		gbc_btnReload.gridy = 4;
		add(btnReload, gbc_btnReload);
		
		JLabel lblWebsiteDir = new JLabel("Website dir :");
		GridBagConstraints gbc_lblWebsiteDir = new GridBagConstraints();
		gbc_lblWebsiteDir.anchor = GridBagConstraints.EAST;
		gbc_lblWebsiteDir.insets = new Insets(0, 0, 5, 5);
		gbc_lblWebsiteDir.gridx = 1;
		gbc_lblWebsiteDir.gridy = 5;
		add(lblWebsiteDir, gbc_lblWebsiteDir);
		
		txtdirWebsite = new JTextField(MagicFactory.getInstance().get("default-website-dir"));
		GridBagConstraints gbc_txtdirWebsite = new GridBagConstraints();
		gbc_txtdirWebsite.insets = new Insets(0, 0, 5, 5);
		gbc_txtdirWebsite.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtdirWebsite.gridx = 2;
		gbc_txtdirWebsite.gridy = 5;
		add(txtdirWebsite, gbc_txtdirWebsite);
		txtdirWebsite.setColumns(10);
		
		JButton btnWebsiteSave = new JButton("Save");
		btnWebsiteSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MagicFactory.getInstance().setProperty("default-website-dir", txtdirWebsite.getText());
			}
		});
		GridBagConstraints gbc_btnSave_1 = new GridBagConstraints();
		gbc_btnSave_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSave_1.insets = new Insets(0, 0, 5, 5);
		gbc_btnSave_1.gridx = 3;
		gbc_btnSave_1.gridy = 5;
		add(btnWebsiteSave, gbc_btnSave_1);
		
		JLabel lblLogLevel = new JLabel("Log level :");
		GridBagConstraints gbc_lblLogLevel = new GridBagConstraints();
		gbc_lblLogLevel.anchor = GridBagConstraints.EAST;
		gbc_lblLogLevel.insets = new Insets(0, 0, 5, 5);
		gbc_lblLogLevel.gridx = 1;
		gbc_lblLogLevel.gridy = 6;
		add(lblLogLevel, gbc_lblLogLevel);
		
		cboLogLevels = new JComboBox(new Level[]{Level.DEBUG,Level.INFO,Level.ERROR});
		for(int i=0;i<cboLogLevels.getItemCount();i++)
		{
			if(cboLogLevels.getItemAt(i).toString().equals(MagicFactory.getInstance().get("loglevel")))
				cboLogLevels.setSelectedIndex(i);
			
		}
		
		
		cboLogLevels.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LogManager.getRootLogger().setLevel((Level)cboLogLevels.getSelectedItem());
			}
		});
		
		GridBagConstraints gbc_cboLogLevels = new GridBagConstraints();
		gbc_cboLogLevels.insets = new Insets(0, 0, 5, 5);
		gbc_cboLogLevels.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboLogLevels.gridx = 2;
		gbc_cboLogLevels.gridy = 6;
		add(cboLogLevels, gbc_cboLogLevels);
		
		JButton btnSaveLoglevel = new JButton("Save");
		btnSaveLoglevel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MagicFactory.getInstance().setProperty("loglevel", (Level)cboLogLevels.getSelectedItem());
				LogManager.getRootLogger().setLevel((Level)cboLogLevels.getSelectedItem());
			}
		});
		GridBagConstraints gbc_btnSaveLoglevel = new GridBagConstraints();
		gbc_btnSaveLoglevel.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSaveLoglevel.insets = new Insets(0, 0, 5, 5);
		gbc_btnSaveLoglevel.gridx = 3;
		gbc_btnSaveLoglevel.gridy = 6;
		add(btnSaveLoglevel, gbc_btnSaveLoglevel);
		
		JLabel lblForceNumberFor = new JLabel("update Number for Edition :");
		GridBagConstraints gbc_lblForceNumberFor = new GridBagConstraints();
		gbc_lblForceNumberFor.anchor = GridBagConstraints.EAST;
		gbc_lblForceNumberFor.insets = new Insets(0, 0, 5, 5);
		gbc_lblForceNumberFor.gridx = 1;
		gbc_lblForceNumberFor.gridy = 7;
		add(lblForceNumberFor, gbc_lblForceNumberFor);
		
		cboEditions = new JComboBox(NumberUpdater.unavailableEds);
		GridBagConstraints gbc_cboEditions = new GridBagConstraints();
		gbc_cboEditions.insets = new Insets(0, 0, 5, 5);
		gbc_cboEditions.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboEditions.gridx = 2;
		gbc_cboEditions.gridy = 7;
		add(cboEditions, gbc_cboEditions);
		
		JButton btnUpdate = new JButton("Update");
		GridBagConstraints gbc_btnUpdate = new GridBagConstraints();
		gbc_btnUpdate.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnUpdate.insets = new Insets(0, 0, 5, 5);
		gbc_btnUpdate.gridx = 3;
		gbc_btnUpdate.gridy = 7;
		add(btnUpdate, gbc_btnUpdate);
		
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ThreadManager.getInstance().execute(new Runnable() {
					public void run() {
							loading(true,"update " + cboEditions.getSelectedItem().toString() + " numbers");
							try {
								NumberUpdater.update(cboEditions.getSelectedItem().toString());
							} catch (Exception e) {
								e.printStackTrace();
							}
							loading(false,"");
					}
				}, "updating "  + cboEditions.getSelectedItem() + " numbers");
			}
		});
		
		
		lblLoading = new JLabel("");
		lblLoading.setVisible(false);
		lblLoading.setIcon(new ImageIcon(ConfigurationPanel.class.getResource("/res/load.gif")));
		GridBagConstraints gbc_lblLoading = new GridBagConstraints();
		gbc_lblLoading.insets = new Insets(0, 0, 0, 5);
		gbc_lblLoading.gridx = 2;
		gbc_lblLoading.gridy = 8;
		add(lblLoading, gbc_lblLoading);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					
					MagicFactory.getInstance().setProperty("default-library", (MagicCollection)cboCollections.getSelectedItem());
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

}
