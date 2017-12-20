package org.magic.api.main;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.magic.api.interfaces.DashBoard;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.api.interfaces.MagicDAO;
import org.magic.api.interfaces.PictureProvider;
import org.magic.services.MTGControler;

public class SpeedConfigurator extends JFrame {
	private JComboBox<MagicDAO> cboDAOs;
	private JComboBox<PictureProvider> cboPictures;
	private JComboBox<DashBoard> cboDashboard;
	private JComboBox<MTGPicturesCache> cboCaches;
	private JComboBox<MagicCardsProvider> cboProvider;
	private JCheckBox checkBox;
	private JCheckBox checkBox_2;
	private JCheckBox checkBox_4;
	private JCheckBox checkBox_6;
	private JCheckBox checkBox_8;
	private JCheckBox checkBox_1;
	private JCheckBox checkBox_3;
	private JCheckBox checkBox_5;
	private JCheckBox checkBox_7;
	private JCheckBox checkBox_9;

	public static void main(String[] args) {
		new SpeedConfigurator().setVisible(true);

	}
	
	
	public SpeedConfigurator()
	{
		setTitle("Speed Configurator");
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane);
		
		JPanel panneau1 = new JPanel();
		tabbedPane.addTab("Configuration", null, panneau1, null);
		GridBagLayout gbl_panneau1 = new GridBagLayout();
		gbl_panneau1.columnWidths = new int[]{47, 160, 0};
		gbl_panneau1.rowHeights = new int[]{33, 0, 0, 0, 0, 0, 0};
		gbl_panneau1.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panneau1.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panneau1.setLayout(gbl_panneau1);
		
		JLabel lblProvider = new JLabel("Provider :");
		GridBagConstraints gbc_lblProvider = new GridBagConstraints();
		gbc_lblProvider.anchor = GridBagConstraints.WEST;
		gbc_lblProvider.insets = new Insets(0, 0, 5, 5);
		gbc_lblProvider.gridx = 0;
		gbc_lblProvider.gridy = 0;
		panneau1.add(lblProvider, gbc_lblProvider);
		
		cboProvider = new JComboBox(new DefaultComboBoxModel(MTGControler.getInstance().getListProviders().toArray()));
		GridBagConstraints gbc_cboProvider = new GridBagConstraints();
		gbc_cboProvider.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboProvider.insets = new Insets(0, 0, 5, 0);
		gbc_cboProvider.gridx = 1;
		gbc_cboProvider.gridy = 0;
		panneau1.add(cboProvider, gbc_cboProvider);
		
		JLabel lblDatasource = new JLabel("Datasource : ");
		GridBagConstraints gbc_lblDatasource = new GridBagConstraints();
		gbc_lblDatasource.anchor = GridBagConstraints.WEST;
		gbc_lblDatasource.insets = new Insets(0, 0, 5, 5);
		gbc_lblDatasource.gridx = 0;
		gbc_lblDatasource.gridy = 1;
		panneau1.add(lblDatasource, gbc_lblDatasource);
		
		cboDAOs = new JComboBox(new DefaultComboBoxModel(MTGControler.getInstance().getDaoProviders().toArray()));
		GridBagConstraints gbc_cboDAOs = new GridBagConstraints();
		gbc_cboDAOs.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboDAOs.insets = new Insets(0, 0, 5, 0);
		gbc_cboDAOs.gridx = 1;
		gbc_cboDAOs.gridy = 1;
		panneau1.add(cboDAOs, gbc_cboDAOs);
		
		JLabel lblPictures = new JLabel("Picture :");
		GridBagConstraints gbc_lblPictures = new GridBagConstraints();
		gbc_lblPictures.anchor = GridBagConstraints.WEST;
		gbc_lblPictures.insets = new Insets(0, 0, 5, 5);
		gbc_lblPictures.gridx = 0;
		gbc_lblPictures.gridy = 2;
		panneau1.add(lblPictures, gbc_lblPictures);
		
		cboPictures = new JComboBox(new DefaultComboBoxModel(MTGControler.getInstance().getPicturesProviders().toArray()));
		GridBagConstraints gbc_cboPictures = new GridBagConstraints();
		gbc_cboPictures.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboPictures.insets = new Insets(0, 0, 5, 0);
		gbc_cboPictures.gridx = 1;
		gbc_cboPictures.gridy = 2;
		panneau1.add(cboPictures, gbc_cboPictures);
		
		JLabel lblDashboard = new JLabel("Dashboard :");
		GridBagConstraints gbc_lblDashboard = new GridBagConstraints();
		gbc_lblDashboard.anchor = GridBagConstraints.WEST;
		gbc_lblDashboard.insets = new Insets(0, 0, 5, 5);
		gbc_lblDashboard.gridx = 0;
		gbc_lblDashboard.gridy = 3;
		panneau1.add(lblDashboard, gbc_lblDashboard);
		
		cboDashboard = new JComboBox(new DefaultComboBoxModel(MTGControler.getInstance().getDashBoards().toArray()));
		GridBagConstraints gbc_cboDashboard = new GridBagConstraints();
		gbc_cboDashboard.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboDashboard.insets = new Insets(0, 0, 5, 0);
		gbc_cboDashboard.gridx = 1;
		gbc_cboDashboard.gridy = 3;
		panneau1.add(cboDashboard, gbc_cboDashboard);
		
		JLabel lblCaches = new JLabel("Cache :");
		GridBagConstraints gbc_lblCaches = new GridBagConstraints();
		gbc_lblCaches.anchor = GridBagConstraints.WEST;
		gbc_lblCaches.insets = new Insets(0, 0, 5, 5);
		gbc_lblCaches.gridx = 0;
		gbc_lblCaches.gridy = 4;
		panneau1.add(lblCaches, gbc_lblCaches);
		
		cboCaches = new JComboBox(new DefaultComboBoxModel(MTGControler.getInstance().getListCaches().toArray()));
		GridBagConstraints gbc_cboCaches = new GridBagConstraints();
		gbc_cboCaches.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboCaches.insets = new Insets(0, 0, 5, 0);
		gbc_cboCaches.gridx = 1;
		gbc_cboCaches.gridy = 4;
		panneau1.add(cboCaches, gbc_cboCaches);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.anchor = GridBagConstraints.NORTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 5;
		panneau1.add(panel, gbc_panel);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		panel.add(btnSave);
		
		JPanel panneau2 = new JPanel();
		tabbedPane.addTab("Modules", null, panneau2, null);
		GridBagLayout gbl_panneau2 = new GridBagLayout();
		gbl_panneau2.columnWidths = new int[]{0, 0, 0, 103, 0};
		gbl_panneau2.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_panneau2.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panneau2.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panneau2.setLayout(gbl_panneau2);
		
		checkBox = new JCheckBox("Search ");
		checkBox.setSelected(false);
		GridBagConstraints gbc_checkBox = new GridBagConstraints();
		gbc_checkBox.anchor = GridBagConstraints.WEST;
		gbc_checkBox.insets = new Insets(0, 0, 5, 5);
		gbc_checkBox.gridx = 1;
		gbc_checkBox.gridy = 0;
		panneau2.add(checkBox, gbc_checkBox);
		
		checkBox_1 = new JCheckBox("Collection");
		checkBox_1.setSelected(false);
		GridBagConstraints gbc_checkBox_1 = new GridBagConstraints();
		gbc_checkBox_1.anchor = GridBagConstraints.WEST;
		gbc_checkBox_1.insets = new Insets(0, 0, 5, 0);
		gbc_checkBox_1.gridx = 3;
		gbc_checkBox_1.gridy = 0;
		panneau2.add(checkBox_1, gbc_checkBox_1);
		
		checkBox_2 = new JCheckBox("DashBoard");
		checkBox_2.setSelected(false);
		GridBagConstraints gbc_checkBox_2 = new GridBagConstraints();
		gbc_checkBox_2.anchor = GridBagConstraints.WEST;
		gbc_checkBox_2.insets = new Insets(0, 0, 5, 5);
		gbc_checkBox_2.gridx = 1;
		gbc_checkBox_2.gridy = 1;
		panneau2.add(checkBox_2, gbc_checkBox_2);
		
		checkBox_3 = new JCheckBox("Game");
		checkBox_3.setSelected(false);
		GridBagConstraints gbc_checkBox_3 = new GridBagConstraints();
		gbc_checkBox_3.anchor = GridBagConstraints.WEST;
		gbc_checkBox_3.insets = new Insets(0, 0, 5, 0);
		gbc_checkBox_3.gridx = 3;
		gbc_checkBox_3.gridy = 1;
		panneau2.add(checkBox_3, gbc_checkBox_3);
		
		checkBox_4 = new JCheckBox("Deck Builder");
		checkBox_4.setSelected(false);
		GridBagConstraints gbc_checkBox_4 = new GridBagConstraints();
		gbc_checkBox_4.anchor = GridBagConstraints.WEST;
		gbc_checkBox_4.insets = new Insets(0, 0, 5, 5);
		gbc_checkBox_4.gridx = 1;
		gbc_checkBox_4.gridy = 2;
		panneau2.add(checkBox_4, gbc_checkBox_4);
		
		checkBox_5 = new JCheckBox("Shopper");
		checkBox_5.setSelected(false);
		GridBagConstraints gbc_checkBox_5 = new GridBagConstraints();
		gbc_checkBox_5.anchor = GridBagConstraints.WEST;
		gbc_checkBox_5.insets = new Insets(0, 0, 5, 0);
		gbc_checkBox_5.gridx = 3;
		gbc_checkBox_5.gridy = 2;
		panneau2.add(checkBox_5, gbc_checkBox_5);
		
		checkBox_6 = new JCheckBox("Alert");
		checkBox_6.setSelected(false);
		GridBagConstraints gbc_checkBox_6 = new GridBagConstraints();
		gbc_checkBox_6.anchor = GridBagConstraints.WEST;
		gbc_checkBox_6.insets = new Insets(0, 0, 5, 5);
		gbc_checkBox_6.gridx = 1;
		gbc_checkBox_6.gridy = 3;
		panneau2.add(checkBox_6, gbc_checkBox_6);
		
		checkBox_7 = new JCheckBox("Rss");
		checkBox_7.setSelected(false);
		GridBagConstraints gbc_checkBox_7 = new GridBagConstraints();
		gbc_checkBox_7.anchor = GridBagConstraints.WEST;
		gbc_checkBox_7.insets = new Insets(0, 0, 5, 0);
		gbc_checkBox_7.gridx = 3;
		gbc_checkBox_7.gridy = 3;
		panneau2.add(checkBox_7, gbc_checkBox_7);
		
		checkBox_8 = new JCheckBox("Card Builder");
		checkBox_8.setSelected(false);
		GridBagConstraints gbc_checkBox_8 = new GridBagConstraints();
		gbc_checkBox_8.anchor = GridBagConstraints.WEST;
		gbc_checkBox_8.insets = new Insets(0, 0, 0, 5);
		gbc_checkBox_8.gridx = 1;
		gbc_checkBox_8.gridy = 4;
		panneau2.add(checkBox_8, gbc_checkBox_8);
		
		checkBox_9 = new JCheckBox("Stock");
		checkBox_9.setSelected(false);
		GridBagConstraints gbc_checkBox_9 = new GridBagConstraints();
		gbc_checkBox_9.anchor = GridBagConstraints.WEST;
		gbc_checkBox_9.gridx = 3;
		gbc_checkBox_9.gridy = 4;
		panneau2.add(checkBox_9, gbc_checkBox_9);
		
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
	}

	protected void save() {
		
		for(MagicCardsProvider prov : MTGControler.getInstance().getListProviders())
		{
			prov.enable(false);
			
			if(prov.getName().equals(cboProvider.getSelectedItem().toString()))
				prov.enable(true);
			
			MTGControler.getInstance().setProperty(prov, prov.isEnable());
		}
		
		for(MagicDAO prov : MTGControler.getInstance().getDaoProviders())
		{
			prov.enable(false);
			
			if(prov.getName().equals(cboDAOs.getSelectedItem().toString()))
				prov.enable(true);
			
			MTGControler.getInstance().setProperty(prov, prov.isEnable());
		}
		
		for(PictureProvider prov : MTGControler.getInstance().getPicturesProviders())
		{
			prov.enable(false);
			
			if(prov.getName().equals(cboPictures.getSelectedItem().toString()))
				prov.enable(true);
			
			MTGControler.getInstance().setProperty(prov, prov.isEnable());
		}
		
		for(MTGPicturesCache prov : MTGControler.getInstance().getListCaches())
		{
			prov.enable(false);
			
			if(prov.getName().equals(cboCaches.getSelectedItem().toString()))
				prov.enable(true);
			
			MTGControler.getInstance().setProperty(prov, prov.isEnable());
		}
		
		for(DashBoard prov : MTGControler.getInstance().getDashBoards())
		{
			prov.enable(false);
			
			if(prov.getName().equals(cboDashboard.getSelectedItem().toString()))
				prov.enable(true);
			
			MTGControler.getInstance().setProperty(prov, prov.isEnable());
		}
		
		System.exit(0);
		
	}

}
