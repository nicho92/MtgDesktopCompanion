package org.magic.api.main;

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
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGPicturesCache;
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
	private JComboBox<MTGPicturesCache> cboCaches;
	private JComboBox<MTGCardsProvider> cboProvider;
	
	public static void main(String[] args) {
		new SpeedConfigurator().setVisible(true);

	}
	

	public SpeedConfigurator() {
		setTitle("Speed Configurator");
		setIconImage(MTGConstants.ICON_CONFIG.getImage());
		getContentPane().setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		getContentPane().add(tabbedPane);

		JPanel panneau1 = new JPanel();
		tabbedPane.addTab("Configuration", null, panneau1, null);
		GridBagLayout gblpanneau1 = new GridBagLayout();
		gblpanneau1.columnWidths = new int[] { 47, 160, 0 };
		gblpanneau1.rowHeights = new int[] { 33, 0, 0, 0, 0, 0, 0 };
		gblpanneau1.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gblpanneau1.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panneau1.setLayout(gblpanneau1);

		JLabel lblProvider = new JLabel("Provider :");
		GridBagConstraints gbclblProvider = new GridBagConstraints();
		gbclblProvider.anchor = GridBagConstraints.WEST;
		gbclblProvider.insets = new Insets(0, 0, 5, 5);
		gbclblProvider.gridx = 0;
		gbclblProvider.gridy = 0;
		panneau1.add(lblProvider, gbclblProvider);

		cboProvider = UITools.createCombobox(MTGCardsProvider.class, true);
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

		cboDAOs = UITools.createCombobox(MTGDao.class, true);
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

		cboPictures = UITools.createCombobox(MTGPictureProvider.class, true);
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

		cboDashboard = UITools.createCombobox(MTGDashBoard.class, true);
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

		cboCaches = UITools.createCombobox(MTGPicturesCache.class, true);
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
		btnSave.addActionListener(e -> save());
		panel.add(btnSave);
		
		JPanel panneau2 = new JPanel();
		tabbedPane.addTab("Update", null, panneau2, null);
		GridBagLayout gblpanneau2 = new GridBagLayout();
		gblpanneau2.columnWidths = new int[]{272, 272, 0, 0};
		gblpanneau2.rowHeights = new int[]{47, 47, 0};
		gblpanneau2.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gblpanneau2.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panneau2.setLayout(gblpanneau2);
		
		JLabel lblNewLabel = new JLabel("Update Zip File :");
		GridBagConstraints gbclblNewLabel = new GridBagConstraints();
		gbclblNewLabel.fill = GridBagConstraints.BOTH;
		gbclblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbclblNewLabel.gridx = 0;
		gbclblNewLabel.gridy = 0;
		panneau2.add(lblNewLabel, gbclblNewLabel);
		
		JTextFieldFileChooser textFieldFileChooser = new JTextFieldFileChooser();
		textFieldFileChooser.getTextField().setColumns(25);
		GridBagConstraints gbctextFieldFileChooser = new GridBagConstraints();
		gbctextFieldFileChooser.fill = GridBagConstraints.BOTH;
		gbctextFieldFileChooser.insets = new Insets(0, 0, 5, 5);
		gbctextFieldFileChooser.gridx = 1;
		gbctextFieldFileChooser.gridy = 0;
		panneau2.add(textFieldFileChooser, gbctextFieldFileChooser);
		
		JButton btnUpdate = new JButton("Update");
		GridBagConstraints gbcbtnUpdate = new GridBagConstraints();
		gbcbtnUpdate.insets = new Insets(0, 0, 5, 0);
		gbcbtnUpdate.fill = GridBagConstraints.BOTH;
		gbcbtnUpdate.gridx = 2;
		gbcbtnUpdate.gridy = 0;
		panneau2.add(btnUpdate, gbcbtnUpdate);
		
		JTextFieldFileChooser lblNewLabel1 = new JTextFieldFileChooser(Paths.get("..").toAbsolutePath().normalize().toString(),JFileChooser.SAVE_DIALOG);
		GridBagConstraints gbclblNewLabel1 = new GridBagConstraints();
		gbclblNewLabel1.gridwidth = 3;
		gbclblNewLabel1.insets = new Insets(0, 0, 0, 5);
		gbclblNewLabel1.fill = GridBagConstraints.BOTH;
		gbclblNewLabel1.gridx = 0;
		gbclblNewLabel1.gridy = 1;
		panneau2.add(lblNewLabel1, gbclblNewLabel1);

		pack();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		
		btnUpdate.addActionListener(e->{
			try {
				FileTools.unzip(textFieldFileChooser.getFile(), lblNewLabel1.getFile());
			} catch (IOException e1) {
				MTGControler.getInstance().notify(e1);
			}
		});
		
	}

	protected void save() {

		for (MTGCardsProvider prov : MTGControler.getInstance().getPlugins(MTGCardsProvider.class)) {
			prov.enable(false);

			if (prov.getName().equals(cboProvider.getSelectedItem().toString()))
				prov.enable(true);

			MTGControler.getInstance().setProperty(prov, prov.isEnable());
		}

		for (MTGDao prov : MTGControler.getInstance().getPlugins(MTGDao.class)) {
			prov.enable(false);

			if (prov.getName().equals(cboDAOs.getSelectedItem().toString()))
				prov.enable(true);

			MTGControler.getInstance().setProperty(prov, prov.isEnable());
		}

		for (MTGPictureProvider prov : MTGControler.getInstance().getPlugins(MTGPictureProvider.class)) {
			prov.enable(false);

			if (prov.getName().equals(cboPictures.getSelectedItem().toString()))
				prov.enable(true);

			MTGControler.getInstance().setProperty(prov, prov.isEnable());
		}

		for (MTGPicturesCache prov : MTGControler.getInstance().getPlugins(MTGPicturesCache.class)) {
			prov.enable(false);

			if (prov.getName().equals(cboCaches.getSelectedItem().toString()))
				prov.enable(true);

			MTGControler.getInstance().setProperty(prov, prov.isEnable());
		}

		for (MTGDashBoard prov : MTGControler.getInstance().getPlugins(MTGDashBoard.class)) {
			prov.enable(false);

			if (prov.getName().equals(cboDashboard.getSelectedItem().toString()))
				prov.enable(true);

			MTGControler.getInstance().setProperty(prov, prov.isEnable());
		}

		System.exit(0);

	}

}
