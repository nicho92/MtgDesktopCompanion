package org.magic.gui.components;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicDAO;
import org.magic.api.providers.impl.MtgjsonProvider;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;
import org.magic.tools.InstallCert;
import org.magic.tools.db.NumberUpdater;

public class ConfigurationPanel extends JPanel {
	
	
	private JTextField textField;
	private JComboBox<MagicDAO> cboTargetDAO;
	private JComboBox<MagicCollection> cboCollections;
	private JComboBox<Level> cboLogLevels;
	private JTextField txtdirWebsite;
	private JComboBox<MagicEdition> cboEditions;
	private JComboBox<MagicEdition> cboEditionLands;
	private JTextField txtMinPrice;
	private JComboBox<String> cbojsonView;
	private JTextField txtWebSiteCertificate;
	private JCheckBox chkToolTip ;
	private JLabel lblLoading = new JLabel();
	private JTextField txtName;
	private JLabel lblIconAvatar;
	private JTextField txtCardW;
	private JTextField txtCardH;
	
	public void loading(boolean show,String text)
	{
		lblLoading.setText(text);
		lblLoading.setVisible(show);
	}
	
	public ConfigurationPanel() {
		lblLoading.setIcon(new ImageIcon(ConfigurationPanel.class.getResource("/res/load.gif")));
		lblLoading.setVisible(false);
		
		
		
		cboTargetDAO = new JComboBox();
		cboEditions = new JComboBox(NumberUpdater.unavailableEds);
		cboCollections = new JComboBox();
		cboEditionLands=new JComboBox<MagicEdition>();
		
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{396, 212, 0};
		gridBagLayout.rowHeights = new int[]{179, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		for(MagicDAO daos :  MTGControler.getInstance().getDaoProviders())
			if(!daos.getName().equals(MTGControler.getInstance().getEnabledDAO().getName()))
			{
			
				cboTargetDAO.addItem(daos);
			}
		
		JPanel panelDAO = new JPanel();
		panelDAO.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "DAO", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(64, 64, 64)));
		GridBagConstraints gbc_panelDAO = new GridBagConstraints();
		gbc_panelDAO.insets = new Insets(0, 0, 5, 5);
		gbc_panelDAO.fill = GridBagConstraints.BOTH;
		gbc_panelDAO.gridx = 0;
		gbc_panelDAO.gridy = 0;
		add(panelDAO, gbc_panelDAO);
		GridBagLayout gbl_panelDAO = new GridBagLayout();
		gbl_panelDAO.columnWidths = new int[]{0, 0, 130, 0, 0};
		gbl_panelDAO.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panelDAO.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelDAO.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelDAO.setLayout(gbl_panelDAO);
		
		JLabel lblBackupDao = new JLabel("Backup dao file : ");
		GridBagConstraints gbc_lblBackupDao = new GridBagConstraints();
		gbc_lblBackupDao.insets = new Insets(0, 0, 5, 5);
		gbc_lblBackupDao.gridx = 0;
		gbc_lblBackupDao.gridy = 0;
		panelDAO.add(lblBackupDao, gbc_lblBackupDao);
		
		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridwidth = 2;
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		panelDAO.add(textField, gbc_textField);
		textField.setColumns(10);
		
		JButton btnBackup = new JButton("backup");
		GridBagConstraints gbc_btnBackup = new GridBagConstraints();
		gbc_btnBackup.insets = new Insets(0, 0, 5, 0);
		gbc_btnBackup.gridx = 3;
		gbc_btnBackup.gridy = 0;
		panelDAO.add(btnBackup, gbc_btnBackup);
		
		JLabel lblDuplicateDb = new JLabel("Duplicate " + MTGControler.getInstance().getEnabledDAO() +  " DB to :");
		GridBagConstraints gbc_lblDuplicateDb = new GridBagConstraints();
		gbc_lblDuplicateDb.insets = new Insets(0, 0, 5, 5);
		gbc_lblDuplicateDb.gridx = 0;
		gbc_lblDuplicateDb.gridy = 1;
		panelDAO.add(lblDuplicateDb, gbc_lblDuplicateDb);
		
		
		GridBagConstraints gbc_cboTargetDAO = new GridBagConstraints();
		gbc_cboTargetDAO.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboTargetDAO.gridwidth = 2;
		gbc_cboTargetDAO.insets = new Insets(0, 0, 5, 5);
		gbc_cboTargetDAO.gridx = 1;
		gbc_cboTargetDAO.gridy = 1;
		panelDAO.add(cboTargetDAO, gbc_cboTargetDAO);
		
		JButton btnDuplicate = new JButton("duplicate");
		GridBagConstraints gbc_btnDuplicate = new GridBagConstraints();
		gbc_btnDuplicate.insets = new Insets(0, 0, 5, 0);
		gbc_btnDuplicate.gridx = 3;
		gbc_btnDuplicate.gridy = 1;
		panelDAO.add(btnDuplicate, gbc_btnDuplicate);
		
		JLabel lblForceNumberFor = new JLabel("update Number for Edition :");
		GridBagConstraints gbc_lblForceNumberFor = new GridBagConstraints();
		gbc_lblForceNumberFor.insets = new Insets(0, 0, 0, 5);
		gbc_lblForceNumberFor.gridx = 0;
		gbc_lblForceNumberFor.gridy = 2;
		panelDAO.add(lblForceNumberFor, gbc_lblForceNumberFor);
		
		
		GridBagConstraints gbc_cboEditions = new GridBagConstraints();
		gbc_cboEditions.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboEditions.gridwidth = 2;
		gbc_cboEditions.insets = new Insets(0, 0, 0, 5);
		gbc_cboEditions.gridx = 1;
		gbc_cboEditions.gridy = 2;
		panelDAO.add(cboEditions, gbc_cboEditions);
		
		JButton btnUpdate = new JButton("Update");
		GridBagConstraints gbc_btnUpdate = new GridBagConstraints();
		gbc_btnUpdate.gridx = 3;
		gbc_btnUpdate.gridy = 2;
		panelDAO.add(btnUpdate, gbc_btnUpdate);
		btnUpdate.setEnabled(MTGControler.getInstance().getEnabledProviders() instanceof MtgjsonProvider);//only for mtgjson provider
		
		
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
		btnDuplicate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			
				
				ThreadManager.getInstance().execute(new Runnable() {
					
					@Override
					public void run() {
						try{
							MagicDAO dao = (MagicDAO)cboTargetDAO.getSelectedItem();
							loading(true,"duplicate " + MTGControler.getInstance().getEnabledDAO() +" database to" + dao);
							
							dao.init();
							for(MagicCollection col : MTGControler.getInstance().getEnabledDAO().getCollections())
								for(MagicCard mc : MTGControler.getInstance().getEnabledDAO().getCardsFromCollection(col))
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
				}, "duplicate " + MTGControler.getInstance().getEnabledDAO() + " to " + cboTargetDAO.getSelectedItem() );
			}
		});
		btnBackup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
					ThreadManager.getInstance().execute(new Runnable() {
						
						@Override
						public void run() {
							try {
								loading(true,"backup " + MTGControler.getInstance().getEnabledDAO() +" database");
								MTGControler.getInstance().getEnabledDAO().backup(new File(textField.getText()));
								loading(false,"backup " + MTGControler.getInstance().getEnabledDAO() +" end");
								
							} 
							catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					}, "backup " + MTGControler.getInstance().getEnabledDAO() +" database");
					
				
			}
		});
		
		JPanel panelConfig = new JPanel();
		panelConfig.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "Config", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(64, 64, 64)));
		GridBagConstraints gbc_panelConfig = new GridBagConstraints();
		gbc_panelConfig.gridheight = 2;
		gbc_panelConfig.insets = new Insets(0, 0, 5, 0);
		gbc_panelConfig.fill = GridBagConstraints.BOTH;
		gbc_panelConfig.gridx = 1;
		gbc_panelConfig.gridy = 0;
		add(panelConfig, gbc_panelConfig);
		GridBagLayout gbl_panelConfig = new GridBagLayout();
		gbl_panelConfig.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
		gbl_panelConfig.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panelConfig.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelConfig.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelConfig.setLayout(gbl_panelConfig);
		
		JLabel lblMainCol = new JLabel("Main Collection :");
		GridBagConstraints gbc_lblMainCol = new GridBagConstraints();
		gbc_lblMainCol.insets = new Insets(0, 0, 5, 5);
		gbc_lblMainCol.gridx = 0;
		gbc_lblMainCol.gridy = 0;
		panelConfig.add(lblMainCol, gbc_lblMainCol);
		
		GridBagConstraints gbc_cboCollections = new GridBagConstraints();
		gbc_cboCollections.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboCollections.gridwidth = 3;
		gbc_cboCollections.insets = new Insets(0, 0, 5, 5);
		gbc_cboCollections.gridx = 1;
		gbc_cboCollections.gridy = 0;
		panelConfig.add(cboCollections, gbc_cboCollections);
		
		JButton btnSave = new JButton("Save");
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.insets = new Insets(0, 0, 5, 0);
		gbc_btnSave.gridx = 4;
		gbc_btnSave.gridy = 0;
		panelConfig.add(btnSave, gbc_btnSave);
		
		JLabel lblDefaultLandManuel = new JLabel("Default Land deck import :");
		GridBagConstraints gbc_lblDefaultLandManuel = new GridBagConstraints();
		gbc_lblDefaultLandManuel.insets = new Insets(0, 0, 5, 5);
		gbc_lblDefaultLandManuel.gridx = 0;
		gbc_lblDefaultLandManuel.gridy = 1;
		panelConfig.add(lblDefaultLandManuel, gbc_lblDefaultLandManuel);
		
		
		GridBagConstraints gbc_cboEditionLands = new GridBagConstraints();
		gbc_cboEditionLands.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboEditionLands.gridwidth = 3;
		gbc_cboEditionLands.insets = new Insets(0, 0, 5, 5);
		gbc_cboEditionLands.gridx = 1;
		gbc_cboEditionLands.gridy = 1;
		panelConfig.add(cboEditionLands, gbc_cboEditionLands);
		
		JButton btnSave_1 = new JButton("save");
		GridBagConstraints gbc_btnSave_1 = new GridBagConstraints();
		gbc_btnSave_1.insets = new Insets(0, 0, 5, 0);
		gbc_btnSave_1.gridx = 4;
		gbc_btnSave_1.gridy = 1;
		panelConfig.add(btnSave_1, gbc_btnSave_1);
		
		JLabel lblLogLevel = new JLabel("Log level :");
		GridBagConstraints gbc_lblLogLevel = new GridBagConstraints();
		gbc_lblLogLevel.insets = new Insets(0, 0, 5, 5);
		gbc_lblLogLevel.gridx = 0;
		gbc_lblLogLevel.gridy = 2;
		panelConfig.add(lblLogLevel, gbc_lblLogLevel);
		
		cboLogLevels = new JComboBox(new Level[]{Level.DEBUG,Level.INFO,Level.ERROR});
		GridBagConstraints gbc_cboLogLevels = new GridBagConstraints();
		gbc_cboLogLevels.gridwidth = 3;
		gbc_cboLogLevels.insets = new Insets(0, 0, 5, 5);
		gbc_cboLogLevels.gridx = 1;
		gbc_cboLogLevels.gridy = 2;
		panelConfig.add(cboLogLevels, gbc_cboLogLevels);
		
		JButton btnSaveLoglevel = new JButton("Save");
		GridBagConstraints gbc_btnSaveLoglevel = new GridBagConstraints();
		gbc_btnSaveLoglevel.insets = new Insets(0, 0, 5, 0);
		gbc_btnSaveLoglevel.gridx = 4;
		gbc_btnSaveLoglevel.gridy = 2;
		panelConfig.add(btnSaveLoglevel, gbc_btnSaveLoglevel);
		
		JLabel lblShowJsonPanel = new JLabel("Show Json Panel");
		GridBagConstraints gbc_lblShowJsonPanel = new GridBagConstraints();
		gbc_lblShowJsonPanel.insets = new Insets(0, 0, 5, 5);
		gbc_lblShowJsonPanel.gridx = 0;
		gbc_lblShowJsonPanel.gridy = 3;
		panelConfig.add(lblShowJsonPanel, gbc_lblShowJsonPanel);
		
		cbojsonView = new JComboBox<String>();
		GridBagConstraints gbc_cbojsonView = new GridBagConstraints();
		gbc_cbojsonView.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbojsonView.gridwidth = 3;
		gbc_cbojsonView.insets = new Insets(0, 0, 5, 5);
		gbc_cbojsonView.gridx = 1;
		gbc_cbojsonView.gridy = 3;
		panelConfig.add(cbojsonView, gbc_cbojsonView);
		cbojsonView.setModel(new DefaultComboBoxModel<String>(new String[] {"true", "false"}));
		cbojsonView.setSelectedItem(MTGControler.getInstance().get("debug-json-panel"));
		
		JButton btnSaveJson = new JButton("save");
		GridBagConstraints gbc_btnSaveJson = new GridBagConstraints();
		gbc_btnSaveJson.insets = new Insets(0, 0, 5, 0);
		gbc_btnSaveJson.gridx = 4;
		gbc_btnSaveJson.gridy = 3;
		panelConfig.add(btnSaveJson, gbc_btnSaveJson);
		btnSaveJson.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MTGControler.getInstance().setProperty("debug-json-panel", cbojsonView.getSelectedItem());
				
			}
		});
		
		JLabel lblDontTakeAlert = new JLabel("don't show alert with price < than");
		GridBagConstraints gbc_lblDontTakeAlert = new GridBagConstraints();
		gbc_lblDontTakeAlert.insets = new Insets(0, 0, 5, 5);
		gbc_lblDontTakeAlert.gridx = 0;
		gbc_lblDontTakeAlert.gridy = 4;
		panelConfig.add(lblDontTakeAlert, gbc_lblDontTakeAlert);
		
		txtMinPrice = new JTextField(MTGControler.getInstance().get("min-price-alert"));
		GridBagConstraints gbc_txtMinPrice = new GridBagConstraints();
		gbc_txtMinPrice.gridwidth = 3;
		gbc_txtMinPrice.insets = new Insets(0, 0, 5, 5);
		gbc_txtMinPrice.gridx = 1;
		gbc_txtMinPrice.gridy = 4;
		panelConfig.add(txtMinPrice, gbc_txtMinPrice);
		txtMinPrice.setColumns(10);
		
		JButton btnSavePrice = new JButton("Save");
		GridBagConstraints gbc_btnSavePrice = new GridBagConstraints();
		gbc_btnSavePrice.insets = new Insets(0, 0, 5, 0);
		gbc_btnSavePrice.gridx = 4;
		gbc_btnSavePrice.gridy = 4;
		panelConfig.add(btnSavePrice, gbc_btnSavePrice);
		btnSavePrice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MTGControler.getInstance().setProperty("min-price-alert", txtMinPrice.getText());
				
			}
		});
		
		JLabel lblShowTooltip = new JLabel("Show Tooltip on startup :");
		GridBagConstraints gbc_lblShowTooltip = new GridBagConstraints();
		gbc_lblShowTooltip.insets = new Insets(0, 0, 5, 5);
		gbc_lblShowTooltip.gridx = 0;
		gbc_lblShowTooltip.gridy = 5;
		panelConfig.add(lblShowTooltip, gbc_lblShowTooltip);
		
		chkToolTip = new JCheckBox("");
		GridBagConstraints gbc_chkToolTip = new GridBagConstraints();
		gbc_chkToolTip.insets = new Insets(0, 0, 5, 5);
		gbc_chkToolTip.gridx = 2;
		gbc_chkToolTip.gridy = 5;
		panelConfig.add(chkToolTip, gbc_chkToolTip);
		chkToolTip.setSelected(MTGControler.getInstance().get("tooltip").equals("true"));
		chkToolTip.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				MTGControler.getInstance().setProperty("tooltip",chkToolTip.isSelected());	
			}
		});
		
		JLabel lblCardsLanguage = new JLabel("Cards Language :");
		GridBagConstraints gbc_lblCardsLanguage = new GridBagConstraints();
		gbc_lblCardsLanguage.insets = new Insets(0, 0, 5, 5);
		gbc_lblCardsLanguage.gridx = 0;
		gbc_lblCardsLanguage.gridy = 6;
		panelConfig.add(lblCardsLanguage, gbc_lblCardsLanguage);
		
		final JComboBox cboLanguages = new JComboBox();
		
		for(String s : MTGControler.getInstance().getEnabledProviders().getLanguages())
		{
			cboLanguages.addItem(s);
			if(MTGControler.getInstance().get("langage").equals(s))
				cboLanguages.setSelectedItem(s);
		}
		
		
		GridBagConstraints gbc_cboLanguages = new GridBagConstraints();
		gbc_cboLanguages.gridwidth = 3;
		gbc_cboLanguages.insets = new Insets(0, 0, 5, 5);
		gbc_cboLanguages.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboLanguages.gridx = 1;
		gbc_cboLanguages.gridy = 6;
		panelConfig.add(cboLanguages, gbc_cboLanguages);
	
		JButton btnSave_lang = new JButton("Save");
		btnSave_lang.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MTGControler.getInstance().setProperty("langage", cboLanguages.getSelectedItem().toString());
			}
		});
		
		GridBagConstraints gbc_btnSave_lang = new GridBagConstraints();
		gbc_btnSave_lang.insets = new Insets(0, 0, 5, 0);
		gbc_btnSave_lang.gridx = 4;
		gbc_btnSave_lang.gridy = 6;
		panelConfig.add(btnSave_lang, gbc_btnSave_lang);
		
			
		JLabel lblReloadConfig = new JLabel("Reload Config :");
		GridBagConstraints gbc_lblReloadConfig = new GridBagConstraints();
		gbc_lblReloadConfig.insets = new Insets(0, 0, 5, 5);
		gbc_lblReloadConfig.gridx = 0;
		gbc_lblReloadConfig.gridy = 8;
		panelConfig.add(lblReloadConfig, gbc_lblReloadConfig);
		
		JButton btnReload = new JButton("Reload");
		GridBagConstraints gbc_btnReload = new GridBagConstraints();
		gbc_btnReload.insets = new Insets(0, 0, 5, 0);
		gbc_btnReload.gridx = 4;
		gbc_btnReload.gridy = 8;
		panelConfig.add(btnReload, gbc_btnReload);
		btnReload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loading(true,"reload config");
				try {
					MTGControler.getInstance().reload();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e,"Error",JOptionPane.ERROR_MESSAGE);
				}
				loading(false,"");
				
			}
		});
		btnSaveLoglevel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MTGControler.getInstance().setProperty("loglevel", (Level)cboLogLevels.getSelectedItem());
				LogManager.getRootLogger().setLevel((Level)cboLogLevels.getSelectedItem());
			}
		});
		
		
		cboLogLevels.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LogManager.getRootLogger().setLevel((Level)cboLogLevels.getSelectedItem());
			}
		});
		btnSave_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MTGControler.getInstance().setProperty("default-land-deck", ((MagicEdition)cboEditionLands.getSelectedItem()).getId());
				
			}
		});
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					
					MTGControler.getInstance().setProperty("default-library", (MagicCollection)cboCollections.getSelectedItem());
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		try {
			for(MagicCollection col :  MTGControler.getInstance().getEnabledDAO().getCollections())
			{
				cboCollections.addItem(col);
				if(col.getName().equalsIgnoreCase(MTGControler.getInstance().get("default-library")))
				{
					cboCollections.setSelectedItem(col);
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		JPanel panelWebSite = new JPanel();
		panelWebSite.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "WebSites", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(64, 64, 64)));
		GridBagConstraints gbc_panelWebSite = new GridBagConstraints();
		gbc_panelWebSite.insets = new Insets(0, 0, 5, 5);
		gbc_panelWebSite.fill = GridBagConstraints.BOTH;
		gbc_panelWebSite.gridx = 0;
		gbc_panelWebSite.gridy = 1;
		add(panelWebSite, gbc_panelWebSite);
		GridBagLayout gbl_panelWebSite = new GridBagLayout();
		gbl_panelWebSite.columnWidths = new int[]{0, 0, 0, 103, 0, 0};
		gbl_panelWebSite.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_panelWebSite.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelWebSite.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelWebSite.setLayout(gbl_panelWebSite);
		
		JLabel lblWebsiteDir = new JLabel("Website dir :");
		GridBagConstraints gbc_lblWebsiteDir = new GridBagConstraints();
		gbc_lblWebsiteDir.insets = new Insets(0, 0, 5, 5);
		gbc_lblWebsiteDir.gridx = 0;
		gbc_lblWebsiteDir.gridy = 0;
		panelWebSite.add(lblWebsiteDir, gbc_lblWebsiteDir);
		
		txtdirWebsite = new JTextField(MTGControler.getInstance().get("default-website-dir"));
		GridBagConstraints gbc_txtdirWebsite = new GridBagConstraints();
		gbc_txtdirWebsite.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtdirWebsite.gridwidth = 3;
		gbc_txtdirWebsite.insets = new Insets(0, 0, 5, 5);
		gbc_txtdirWebsite.gridx = 1;
		gbc_txtdirWebsite.gridy = 0;
		panelWebSite.add(txtdirWebsite, gbc_txtdirWebsite);
		txtdirWebsite.setColumns(10);
		
		JButton btnWebsiteSave = new JButton("Save");
		GridBagConstraints gbc_btnWebsiteSave = new GridBagConstraints();
		gbc_btnWebsiteSave.insets = new Insets(0, 0, 5, 0);
		gbc_btnWebsiteSave.gridx = 4;
		gbc_btnWebsiteSave.gridy = 0;
		panelWebSite.add(btnWebsiteSave, gbc_btnWebsiteSave);
		
		JLabel lblAddWebsiteCertificate = new JLabel("Add Website Certificate : ");
		GridBagConstraints gbc_lblAddWebsiteCertificate = new GridBagConstraints();
		gbc_lblAddWebsiteCertificate.insets = new Insets(0, 0, 5, 5);
		gbc_lblAddWebsiteCertificate.gridx = 0;
		gbc_lblAddWebsiteCertificate.gridy = 1;
		panelWebSite.add(lblAddWebsiteCertificate, gbc_lblAddWebsiteCertificate);
		
		txtWebSiteCertificate = new JTextField();
		GridBagConstraints gbc_txtWebSiteCertificate = new GridBagConstraints();
		gbc_txtWebSiteCertificate.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtWebSiteCertificate.gridwidth = 3;
		gbc_txtWebSiteCertificate.insets = new Insets(0, 0, 5, 5);
		gbc_txtWebSiteCertificate.gridx = 1;
		gbc_txtWebSiteCertificate.gridy = 1;
		panelWebSite.add(txtWebSiteCertificate, gbc_txtWebSiteCertificate);
		txtWebSiteCertificate.setText("www.");
		txtWebSiteCertificate.setColumns(10);
		
		JButton btnAdd = new JButton("add");
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.insets = new Insets(0, 0, 5, 0);
		gbc_btnAdd.gridx = 4;
		gbc_btnAdd.gridy = 1;
		panelWebSite.add(btnAdd, gbc_btnAdd);
		
		JPanel panelProfil = new JPanel();
		panelProfil.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "Game", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(64, 64, 64)));
		GridBagConstraints gbc_panelProfil = new GridBagConstraints();
		gbc_panelProfil.insets = new Insets(0, 0, 5, 5);
		gbc_panelProfil.fill = GridBagConstraints.BOTH;
		gbc_panelProfil.gridx = 0;
		gbc_panelProfil.gridy = 2;
		add(panelProfil, gbc_panelProfil);
		GridBagLayout gbl_panelProfil = new GridBagLayout();
		gbl_panelProfil.columnWidths = new int[]{0, 71, 0, 0, 0, 0};
		gbl_panelProfil.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_panelProfil.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panelProfil.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelProfil.setLayout(gbl_panelProfil);
		
		JLabel lblName = new JLabel("Name :");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.EAST;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		panelProfil.add(lblName, gbc_lblName);
		
		txtName = new JTextField(MTGControler.getInstance().get("/game/player-profil/name"));
		GridBagConstraints gbc_txtName = new GridBagConstraints();
		gbc_txtName.gridwidth = 3;
		gbc_txtName.insets = new Insets(0, 0, 5, 5);
		gbc_txtName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtName.gridx = 1;
		gbc_txtName.gridy = 0;
		panelProfil.add(txtName, gbc_txtName);
		txtName.setColumns(10);
		
		JLabel lblAvatar = new JLabel("Avatar :");
		GridBagConstraints gbc_lblAvatar = new GridBagConstraints();
		gbc_lblAvatar.insets = new Insets(0, 0, 5, 5);
		gbc_lblAvatar.gridx = 0;
		gbc_lblAvatar.gridy = 1;
		panelProfil.add(lblAvatar, gbc_lblAvatar);
		
		try{
			lblIconAvatar = new JLabel(new ImageIcon(ImageIO.read(new File(MTGControler.getInstance().get("/game/player-profil/avatar")))));
		}
		catch(Exception e)
		{
			lblIconAvatar = new JLabel();
		}

		lblIconAvatar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent paramMouseEvent) {
				JFileChooser jf = new JFileChooser();
				jf.setFileFilter(new FileNameExtensionFilter("Images", "bmp", "gif", "jpg", "jpeg", "png"));
				 int result = jf.showOpenDialog(null);
				if(result==JFileChooser.APPROVE_OPTION)
				{
					MTGControler.getInstance().setProperty("/game/player-profil/avatar",jf.getSelectedFile().getAbsolutePath());
				}
			}
		});
		
		lblIconAvatar.setBorder(new LineBorder(Color.RED, 1, true));
		GridBagConstraints gbc_lblIconAvatar = new GridBagConstraints();
		gbc_lblIconAvatar.fill = GridBagConstraints.BOTH;
		gbc_lblIconAvatar.gridwidth = 2;
		gbc_lblIconAvatar.gridheight = 4;
		gbc_lblIconAvatar.insets = new Insets(0, 0, 0, 5);
		gbc_lblIconAvatar.gridx = 1;
		gbc_lblIconAvatar.gridy = 1;
		panelProfil.add(lblIconAvatar, gbc_lblIconAvatar);
		
		JButton btnSave_2 = new JButton("Save");
		btnSave_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MTGControler.getInstance().setProperty("/game/player-profil/name",txtName.getText());
				MTGControler.getInstance().setProperty("/game/cards/card-width",txtCardW.getText());
				MTGControler.getInstance().setProperty("/game/cards/card-heigth",txtCardH.getText());
			}
		});
		
		JPanel panelSubGame = new JPanel();
		GridBagConstraints gbc_panelSubGame = new GridBagConstraints();
		gbc_panelSubGame.gridheight = 3;
		gbc_panelSubGame.insets = new Insets(0, 0, 5, 5);
		gbc_panelSubGame.fill = GridBagConstraints.BOTH;
		gbc_panelSubGame.gridx = 3;
		gbc_panelSubGame.gridy = 1;
		panelProfil.add(panelSubGame, gbc_panelSubGame);
		panelSubGame.setLayout(new GridLayout(3, 2, 0, 0));
		
		JLabel lblCardW = new JLabel("Card Width :");
		panelSubGame.add(lblCardW);
		
		txtCardW = new JTextField(MTGControler.getInstance().get("/game/cards/card-width"));
		panelSubGame.add(txtCardW);
		txtCardW.setColumns(10);
		
		JLabel lblCardH = new JLabel("Card Height :");
		panelSubGame.add(lblCardH);
		
		txtCardH = new JTextField(MTGControler.getInstance().get("/game/cards/card-height"));
		panelSubGame.add(txtCardH);
		txtCardH.setColumns(10);
		GridBagConstraints gbc_btnSave_2 = new GridBagConstraints();
		gbc_btnSave_2.insets = new Insets(0, 0, 0, 5);
		gbc_btnSave_2.gridx = 3;
		gbc_btnSave_2.gridy = 4;
		panelProfil.add(btnSave_2, gbc_btnSave_2);
		
		GridBagConstraints gbc_lblLoading = new GridBagConstraints();
		gbc_lblLoading.gridwidth = 2;
		gbc_lblLoading.gridx = 0;
		gbc_lblLoading.gridy = 3;
		add(lblLoading, gbc_lblLoading);
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				try {
					InstallCert.install(txtWebSiteCertificate.getText(), MTGControler.KEYSTORE_NAME, MTGControler.KEYSTORE_PASS);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e,"ERROR",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnWebsiteSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MTGControler.getInstance().setProperty("default-website-dir", txtdirWebsite.getText());
			}
		});
		for(int i=0;i<cboLogLevels.getItemCount();i++)
		{
			if(cboLogLevels.getItemAt(i).toString().equals(MTGControler.getInstance().get("loglevel")))
				cboLogLevels.setSelectedIndex(i);
			
		}
		try {
				for(MagicEdition col :  MTGControler.getInstance().getEnabledProviders().loadEditions())
				{
					cboEditionLands.addItem(col);
					if(col.getId().equalsIgnoreCase(MTGControler.getInstance().get("default-land-deck")))
					{
						cboEditionLands.setSelectedItem(col);
					}
				}
			
			
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

}
