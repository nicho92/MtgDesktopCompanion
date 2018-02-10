package org.magic.api.main;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.services.MTGControler;

public class SpeedConfigurator extends JFrame {
	private JComboBox<MTGDao> cboDAOs;
	private JComboBox<MTGPictureProvider> cboPictures;
	private JComboBox<MTGDashBoard> cboDashboard;
	private JComboBox<MTGPicturesCache> cboCaches;
	private JComboBox<MTGCardsProvider> cboProvider;
	private JCheckBox checkBox;
	private JCheckBox checkBox2;
	private JCheckBox checkBox4;
	private JCheckBox checkBox6;
	private JCheckBox checkBox8;
	private JCheckBox checkBox1;
	private JCheckBox checkBox3;
	private JCheckBox checkBox5;
	private JCheckBox checkBox7;
	private JCheckBox checkBox9;

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
		GridBagLayout gblpanneau1 = new GridBagLayout();
		gblpanneau1.columnWidths = new int[]{47, 160, 0};
		gblpanneau1.rowHeights = new int[]{33, 0, 0, 0, 0, 0, 0};
		gblpanneau1.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gblpanneau1.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panneau1.setLayout(gblpanneau1);
		
		JLabel lblProvider = new JLabel("Provider :");
		GridBagConstraints gbclblProvider = new GridBagConstraints();
		gbclblProvider.anchor = GridBagConstraints.WEST;
		gbclblProvider.insets = new Insets(0, 0, 5, 5);
		gbclblProvider.gridx = 0;
		gbclblProvider.gridy = 0;
		panneau1.add(lblProvider, gbclblProvider);
		
		cboProvider = new JComboBox<>(new DefaultComboBoxModel(MTGControler.getInstance().getListProviders().toArray()));
		GridBagConstraints gbccboProvider = new GridBagConstraints();
		gbccboProvider.fill = GridBagConstraints.HORIZONTAL;
		gbccboProvider.insets = new Insets(0, 0, 5, 0);
		gbccboProvider.gridx = 1;
		gbccboProvider.gridy = 0;
		panneau1.add(cboProvider, gbccboProvider);
		
		JLabel lblDatasource = new JLabel("Datasource : ");
		GridBagConstraints gbclblDatasource = new GridBagConstraints();
		gbclblDatasource.anchor = GridBagConstraints.WEST;
		gbclblDatasource.insets = new Insets(0, 0, 5, 5);
		gbclblDatasource.gridx = 0;
		gbclblDatasource.gridy = 1;
		panneau1.add(lblDatasource, gbclblDatasource);
		
		cboDAOs = new JComboBox<>(new DefaultComboBoxModel(MTGControler.getInstance().getDaoProviders().toArray()));
		GridBagConstraints gbccboDAOs = new GridBagConstraints();
		gbccboDAOs.fill = GridBagConstraints.HORIZONTAL;
		gbccboDAOs.insets = new Insets(0, 0, 5, 0);
		gbccboDAOs.gridx = 1;
		gbccboDAOs.gridy = 1;
		panneau1.add(cboDAOs, gbccboDAOs);
		
		JLabel lblPictures = new JLabel("Picture :");
		GridBagConstraints gbclblPictures = new GridBagConstraints();
		gbclblPictures.anchor = GridBagConstraints.WEST;
		gbclblPictures.insets = new Insets(0, 0, 5, 5);
		gbclblPictures.gridx = 0;
		gbclblPictures.gridy = 2;
		panneau1.add(lblPictures, gbclblPictures);
		
		cboPictures = new JComboBox<>(new DefaultComboBoxModel(MTGControler.getInstance().getPicturesProviders().toArray()));
		GridBagConstraints gbccboPictures = new GridBagConstraints();
		gbccboPictures.fill = GridBagConstraints.HORIZONTAL;
		gbccboPictures.insets = new Insets(0, 0, 5, 0);
		gbccboPictures.gridx = 1;
		gbccboPictures.gridy = 2;
		panneau1.add(cboPictures, gbccboPictures);
		
		JLabel lblDashboard = new JLabel("Dashboard :");
		GridBagConstraints gbclblDashboard = new GridBagConstraints();
		gbclblDashboard.anchor = GridBagConstraints.WEST;
		gbclblDashboard.insets = new Insets(0, 0, 5, 5);
		gbclblDashboard.gridx = 0;
		gbclblDashboard.gridy = 3;
		panneau1.add(lblDashboard, gbclblDashboard);
		
		cboDashboard = new JComboBox(new DefaultComboBoxModel(MTGControler.getInstance().getDashBoards().toArray()));
		GridBagConstraints gbccboDashboard = new GridBagConstraints();
		gbccboDashboard.fill = GridBagConstraints.HORIZONTAL;
		gbccboDashboard.insets = new Insets(0, 0, 5, 0);
		gbccboDashboard.gridx = 1;
		gbccboDashboard.gridy = 3;
		panneau1.add(cboDashboard, gbccboDashboard);
		
		JLabel lblCaches = new JLabel("Cache :");
		GridBagConstraints gbclblCaches = new GridBagConstraints();
		gbclblCaches.anchor = GridBagConstraints.WEST;
		gbclblCaches.insets = new Insets(0, 0, 5, 5);
		gbclblCaches.gridx = 0;
		gbclblCaches.gridy = 4;
		panneau1.add(lblCaches, gbclblCaches);
		
		cboCaches = new JComboBox(new DefaultComboBoxModel(MTGControler.getInstance().getListCaches().toArray()));
		GridBagConstraints gbccboCaches = new GridBagConstraints();
		gbccboCaches.fill = GridBagConstraints.HORIZONTAL;
		gbccboCaches.insets = new Insets(0, 0, 5, 0);
		gbccboCaches.gridx = 1;
		gbccboCaches.gridy = 4;
		panneau1.add(cboCaches, gbccboCaches);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbcpanel = new GridBagConstraints();
		gbcpanel.gridwidth = 2;
		gbcpanel.anchor = GridBagConstraints.NORTH;
		gbcpanel.gridx = 0;
		gbcpanel.gridy = 5;
		panneau1.add(panel, gbcpanel);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(e->save());
		panel.add(btnSave);
		
		JPanel panneau2 = new JPanel();
		tabbedPane.addTab("Modules", null, panneau2, null);
		GridBagLayout gblpanneau2 = new GridBagLayout();
		gblpanneau2.columnWidths = new int[]{0, 0, 0, 103, 0};
		gblpanneau2.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gblpanneau2.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gblpanneau2.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panneau2.setLayout(gblpanneau2);
		
		checkBox = new JCheckBox("Search ");
		checkBox.setSelected(false);
		GridBagConstraints gbccheckBox = new GridBagConstraints();
		gbccheckBox.anchor = GridBagConstraints.WEST;
		gbccheckBox.insets = new Insets(0, 0, 5, 5);
		gbccheckBox.gridx = 1;
		gbccheckBox.gridy = 0;
		panneau2.add(checkBox, gbccheckBox);
		
		checkBox1 = new JCheckBox("Collection");
		checkBox1.setSelected(false);
		GridBagConstraints gbccheckBox1 = new GridBagConstraints();
		gbccheckBox1.anchor = GridBagConstraints.WEST;
		gbccheckBox1.insets = new Insets(0, 0, 5, 0);
		gbccheckBox1.gridx = 3;
		gbccheckBox1.gridy = 0;
		panneau2.add(checkBox1, gbccheckBox1);
		
		checkBox2 = new JCheckBox("DashBoard");
		checkBox2.setSelected(false);
		GridBagConstraints gbccheckBox2 = new GridBagConstraints();
		gbccheckBox2.anchor = GridBagConstraints.WEST;
		gbccheckBox2.insets = new Insets(0, 0, 5, 5);
		gbccheckBox2.gridx = 1;
		gbccheckBox2.gridy = 1;
		panneau2.add(checkBox2, gbccheckBox2);
		
		checkBox3 = new JCheckBox("Game");
		checkBox3.setSelected(false);
		GridBagConstraints gbccheckBox3 = new GridBagConstraints();
		gbccheckBox3.anchor = GridBagConstraints.WEST;
		gbccheckBox3.insets = new Insets(0, 0, 5, 0);
		gbccheckBox3.gridx = 3;
		gbccheckBox3.gridy = 1;
		panneau2.add(checkBox3, gbccheckBox3);
		
		checkBox4 = new JCheckBox("Deck Builder");
		checkBox4.setSelected(false);
		GridBagConstraints gbccheckBox4 = new GridBagConstraints();
		gbccheckBox4.anchor = GridBagConstraints.WEST;
		gbccheckBox4.insets = new Insets(0, 0, 5, 5);
		gbccheckBox4.gridx = 1;
		gbccheckBox4.gridy = 2;
		panneau2.add(checkBox4, gbccheckBox4);
		
		checkBox5 = new JCheckBox("Shopper");
		checkBox5.setSelected(false);
		GridBagConstraints gbccheckBox5 = new GridBagConstraints();
		gbccheckBox5.anchor = GridBagConstraints.WEST;
		gbccheckBox5.insets = new Insets(0, 0, 5, 0);
		gbccheckBox5.gridx = 3;
		gbccheckBox5.gridy = 2;
		panneau2.add(checkBox5, gbccheckBox5);
		
		checkBox6 = new JCheckBox("Alert");
		checkBox6.setSelected(false);
		GridBagConstraints gbccheckBox6 = new GridBagConstraints();
		gbccheckBox6.anchor = GridBagConstraints.WEST;
		gbccheckBox6.insets = new Insets(0, 0, 5, 5);
		gbccheckBox6.gridx = 1;
		gbccheckBox6.gridy = 3;
		panneau2.add(checkBox6, gbccheckBox6);
		
		checkBox7 = new JCheckBox("Rss");
		checkBox7.setSelected(false);
		GridBagConstraints gbccheckBox7 = new GridBagConstraints();
		gbccheckBox7.anchor = GridBagConstraints.WEST;
		gbccheckBox7.insets = new Insets(0, 0, 5, 0);
		gbccheckBox7.gridx = 3;
		gbccheckBox7.gridy = 3;
		panneau2.add(checkBox7, gbccheckBox7);
		
		checkBox8 = new JCheckBox("Card Builder");
		checkBox8.setSelected(false);
		GridBagConstraints gbccheckBox8 = new GridBagConstraints();
		gbccheckBox8.anchor = GridBagConstraints.WEST;
		gbccheckBox8.insets = new Insets(0, 0, 0, 5);
		gbccheckBox8.gridx = 1;
		gbccheckBox8.gridy = 4;
		panneau2.add(checkBox8, gbccheckBox8);
		
		checkBox9 = new JCheckBox("Stock");
		checkBox9.setSelected(false);
		GridBagConstraints gbccheckBox9 = new GridBagConstraints();
		gbccheckBox9.anchor = GridBagConstraints.WEST;
		gbccheckBox9.gridx = 3;
		gbccheckBox9.gridy = 4;
		panneau2.add(checkBox9, gbccheckBox9);
		
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
	}

	protected void save() {
		
		for(MTGCardsProvider prov : MTGControler.getInstance().getListProviders())
		{
			prov.enable(false);
			
			if(prov.getName().equals(cboProvider.getSelectedItem().toString()))
				prov.enable(true);
			
			MTGControler.getInstance().setProperty(prov, prov.isEnable());
		}
		
		for(MTGDao prov : MTGControler.getInstance().getDaoProviders())
		{
			prov.enable(false);
			
			if(prov.getName().equals(cboDAOs.getSelectedItem().toString()))
				prov.enable(true);
			
			MTGControler.getInstance().setProperty(prov, prov.isEnable());
		}
		
		for(MTGPictureProvider prov : MTGControler.getInstance().getPicturesProviders())
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
		
		for(MTGDashBoard prov : MTGControler.getInstance().getDashBoards())
		{
			prov.enable(false);
			
			if(prov.getName().equals(cboDashboard.getSelectedItem().toString()))
				prov.enable(true);
			
			MTGControler.getInstance().setProperty(prov, prov.isEnable());
		}
		
		System.exit(0);
		
	}

}
