package org.magic.api.main;

import static org.magic.tools.MTG.listPlugins;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.MTGPictureCache;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.gui.components.JTextFieldFileChooser;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.FileTools;
import org.magic.tools.UITools;


public class SpeedConfigurator extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JComboBox<MTGDao> cboDAOs;
	private JComboBox<MTGPictureProvider> cboPictures;
	private JComboBox<MTGDashBoard> cboDashboard;
	private JComboBox<MTGPictureCache> cboCaches;
	private JComboBox<MTGCardsProvider> cboProvider;
	
	public static void main(String[] args) {
		MTGControler.getInstance();
		new SpeedConfigurator().setVisible(true);

	}
	

	public SpeedConfigurator() {
		setTitle("Speed Configurator");
		setIconImage(MTGConstants.ICON_CONFIG.getImage());
		getContentPane().setLayout(new BorderLayout(0, 0));

		var tabbedPane = new JTabbedPane(SwingConstants.TOP);
		getContentPane().add(tabbedPane);

		var panneau1 = new JPanel();
		tabbedPane.addTab("Configuration", null, panneau1, null);
		var gblpanneau1 = new GridBagLayout();
		gblpanneau1.columnWidths = new int[] { 47, 160, 0 };
		gblpanneau1.rowHeights = new int[] { 33, 0, 0, 0, 0, 0, 0 };
		gblpanneau1.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gblpanneau1.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panneau1.setLayout(gblpanneau1);

		var lblProvider = new JLabel("Provider :");
		var gbclblProvider = new GridBagConstraints();
		gbclblProvider.anchor = GridBagConstraints.WEST;
		gbclblProvider.insets = new Insets(0, 0, 5, 5);
		gbclblProvider.gridx = 0;
		gbclblProvider.gridy = 0;
		panneau1.add(lblProvider, gbclblProvider);

		cboProvider = UITools.createCombobox(MTGCardsProvider.class, true);
		var gbccboProvider = new GridBagConstraints();
		gbccboProvider.fill = GridBagConstraints.HORIZONTAL;
		gbccboProvider.insets = new Insets(0, 0, 5, 0);
		gbccboProvider.gridx = 1;
		gbccboProvider.gridy = 0;
		panneau1.add(cboProvider, gbccboProvider);

		var lblDatasource = new JLabel("Datasource : ");
		var gbclblDatasource = new GridBagConstraints();
		gbclblDatasource.anchor = GridBagConstraints.WEST;
		gbclblDatasource.insets = new Insets(0, 0, 5, 5);
		gbclblDatasource.gridx = 0;
		gbclblDatasource.gridy = 1;
		panneau1.add(lblDatasource, gbclblDatasource);

		cboDAOs = UITools.createCombobox(MTGDao.class, true);
		var gbccboDAOs = new GridBagConstraints();
		gbccboDAOs.fill = GridBagConstraints.HORIZONTAL;
		gbccboDAOs.insets = new Insets(0, 0, 5, 0);
		gbccboDAOs.gridx = 1;
		gbccboDAOs.gridy = 1;
		panneau1.add(cboDAOs, gbccboDAOs);

		var lblPictures = new JLabel("Picture :");
		var gbclblPictures = new GridBagConstraints();
		gbclblPictures.anchor = GridBagConstraints.WEST;
		gbclblPictures.insets = new Insets(0, 0, 5, 5);
		gbclblPictures.gridx = 0;
		gbclblPictures.gridy = 2;
		panneau1.add(lblPictures, gbclblPictures);

		cboPictures = UITools.createCombobox(MTGPictureProvider.class, true);
		var gbccboPictures = new GridBagConstraints();
		gbccboPictures.fill = GridBagConstraints.HORIZONTAL;
		gbccboPictures.insets = new Insets(0, 0, 5, 0);
		gbccboPictures.gridx = 1;
		gbccboPictures.gridy = 2;
		panneau1.add(cboPictures, gbccboPictures);

		var lblDashboard = new JLabel("Dashboard :");
		var gbclblDashboard = new GridBagConstraints();
		gbclblDashboard.anchor = GridBagConstraints.WEST;
		gbclblDashboard.insets = new Insets(0, 0, 5, 5);
		gbclblDashboard.gridx = 0;
		gbclblDashboard.gridy = 3;
		panneau1.add(lblDashboard, gbclblDashboard);

		cboDashboard = UITools.createCombobox(MTGDashBoard.class, true);
		var gbccboDashboard = new GridBagConstraints();
		gbccboDashboard.fill = GridBagConstraints.HORIZONTAL;
		gbccboDashboard.insets = new Insets(0, 0, 5, 0);
		gbccboDashboard.gridx = 1;
		gbccboDashboard.gridy = 3;
		panneau1.add(cboDashboard, gbccboDashboard);

		var lblCaches = new JLabel("Cache :");
		var gbclblCaches = new GridBagConstraints();
		gbclblCaches.anchor = GridBagConstraints.WEST;
		gbclblCaches.insets = new Insets(0, 0, 5, 5);
		gbclblCaches.gridx = 0;
		gbclblCaches.gridy = 4;
		panneau1.add(lblCaches, gbclblCaches);

		cboCaches = UITools.createCombobox(MTGPictureCache.class, true);
		var gbccboCaches = new GridBagConstraints();
		gbccboCaches.fill = GridBagConstraints.HORIZONTAL;
		gbccboCaches.insets = new Insets(0, 0, 5, 0);
		gbccboCaches.gridx = 1;
		gbccboCaches.gridy = 4;
		panneau1.add(cboCaches, gbccboCaches);

		var panel = new JPanel();
		var gbcpanel = new GridBagConstraints();
		gbcpanel.gridwidth = 2;
		gbcpanel.anchor = GridBagConstraints.NORTH;
		gbcpanel.gridx = 0;
		gbcpanel.gridy = 5;
		panneau1.add(panel, gbcpanel);

		var btnSave = new JButton("Save");
		btnSave.addActionListener(e -> save());
		panel.add(btnSave);

		pack();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
	}

	protected void save() {

		for (MTGCardsProvider prov : listPlugins(MTGCardsProvider.class)) {
			prov.enable(false);

			if (prov.getName().equals(cboProvider.getSelectedItem().toString()))
				prov.enable(true);

			MTGControler.getInstance().setProperty(prov, prov.isEnable());
		}

		for (MTGDao prov : listPlugins(MTGDao.class)) {
			prov.enable(false);

			if (prov.getName().equals(cboDAOs.getSelectedItem().toString()))
				prov.enable(true);

			MTGControler.getInstance().setProperty(prov, prov.isEnable());
		}

		for (MTGPictureProvider prov : listPlugins(MTGPictureProvider.class)) {
			prov.enable(false);

			if (prov.getName().equals(cboPictures.getSelectedItem().toString()))
				prov.enable(true);

			MTGControler.getInstance().setProperty(prov, prov.isEnable());
		}

		for (MTGPictureCache prov : listPlugins(MTGPictureCache.class)) {
			prov.enable(false);

			if (prov.getName().equals(cboCaches.getSelectedItem().toString()))
				prov.enable(true);

			MTGControler.getInstance().setProperty(prov, prov.isEnable());
		}

		for (MTGDashBoard prov : listPlugins(MTGDashBoard.class)) {
			prov.enable(false);

			if (prov.getName().equals(cboDashboard.getSelectedItem().toString()))
				prov.enable(true);

			MTGControler.getInstance().setProperty(prov, prov.isEnable());
		}

		System.exit(0);

	}

}
