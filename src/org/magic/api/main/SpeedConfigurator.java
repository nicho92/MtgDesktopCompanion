package org.magic.api.main;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.magic.api.interfaces.DashBoard;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.api.interfaces.MagicDAO;
import org.magic.api.interfaces.PictureProvider;
import org.magic.services.MTGControler;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SpeedConfigurator extends JFrame {
	private JComboBox<MagicDAO> cboDAOs;
	private JComboBox<PictureProvider> cboPictures;
	private JComboBox<DashBoard> cboDashboard;
	private JComboBox<MTGPicturesCache> cboCaches;
	private JComboBox cboProvider;

	public static void main(String[] args) {
		new SpeedConfigurator().setVisible(true);

	}
	
	
	public SpeedConfigurator()
	{
		setTitle("Speed Configurator");
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JLabel lblProvider = new JLabel("Provider :");
		GridBagConstraints gbc_lblProvider = new GridBagConstraints();
		gbc_lblProvider.anchor = GridBagConstraints.EAST;
		gbc_lblProvider.insets = new Insets(0, 0, 5, 5);
		gbc_lblProvider.gridx = 0;
		gbc_lblProvider.gridy = 0;
		getContentPane().add(lblProvider, gbc_lblProvider);
		
		cboProvider = new JComboBox(new DefaultComboBoxModel(MTGControler.getInstance().getListProviders().toArray()));
		GridBagConstraints gbc_cboProvider = new GridBagConstraints();
		gbc_cboProvider.insets = new Insets(0, 0, 5, 0);
		gbc_cboProvider.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboProvider.gridx = 1;
		gbc_cboProvider.gridy = 0;
		getContentPane().add(cboProvider, gbc_cboProvider);
		
		JLabel lblDatasource = new JLabel("Datasource : ");
		GridBagConstraints gbc_lblDatasource = new GridBagConstraints();
		gbc_lblDatasource.anchor = GridBagConstraints.EAST;
		gbc_lblDatasource.insets = new Insets(0, 0, 5, 5);
		gbc_lblDatasource.gridx = 0;
		gbc_lblDatasource.gridy = 1;
		getContentPane().add(lblDatasource, gbc_lblDatasource);
		
		cboDAOs = new JComboBox(new DefaultComboBoxModel(MTGControler.getInstance().getDaoProviders().toArray()));
		GridBagConstraints gbc_cboDAOs = new GridBagConstraints();
		gbc_cboDAOs.insets = new Insets(0, 0, 5, 0);
		gbc_cboDAOs.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboDAOs.gridx = 1;
		gbc_cboDAOs.gridy = 1;
		getContentPane().add(cboDAOs, gbc_cboDAOs);
		
		JLabel lblPictures = new JLabel("Picture :");
		GridBagConstraints gbc_lblPictures = new GridBagConstraints();
		gbc_lblPictures.anchor = GridBagConstraints.EAST;
		gbc_lblPictures.insets = new Insets(0, 0, 5, 5);
		gbc_lblPictures.gridx = 0;
		gbc_lblPictures.gridy = 2;
		getContentPane().add(lblPictures, gbc_lblPictures);
		
		cboPictures = new JComboBox(new DefaultComboBoxModel(MTGControler.getInstance().getPicturesProviders().toArray()));
		GridBagConstraints gbc_cboPictures = new GridBagConstraints();
		gbc_cboPictures.insets = new Insets(0, 0, 5, 0);
		gbc_cboPictures.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboPictures.gridx = 1;
		gbc_cboPictures.gridy = 2;
		getContentPane().add(cboPictures, gbc_cboPictures);
		
		JLabel lblDashboard = new JLabel("Dashboard :");
		GridBagConstraints gbc_lblDashboard = new GridBagConstraints();
		gbc_lblDashboard.anchor = GridBagConstraints.EAST;
		gbc_lblDashboard.insets = new Insets(0, 0, 5, 5);
		gbc_lblDashboard.gridx = 0;
		gbc_lblDashboard.gridy = 3;
		getContentPane().add(lblDashboard, gbc_lblDashboard);
		
		cboDashboard = new JComboBox(new DefaultComboBoxModel(MTGControler.getInstance().getDashBoards().toArray()));
		GridBagConstraints gbc_cboDashboard = new GridBagConstraints();
		gbc_cboDashboard.insets = new Insets(0, 0, 5, 0);
		gbc_cboDashboard.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboDashboard.gridx = 1;
		gbc_cboDashboard.gridy = 3;
		getContentPane().add(cboDashboard, gbc_cboDashboard);
		
		JLabel lblCaches = new JLabel("Cache :");
		GridBagConstraints gbc_lblCaches = new GridBagConstraints();
		gbc_lblCaches.anchor = GridBagConstraints.EAST;
		gbc_lblCaches.insets = new Insets(0, 0, 5, 5);
		gbc_lblCaches.gridx = 0;
		gbc_lblCaches.gridy = 4;
		getContentPane().add(lblCaches, gbc_lblCaches);
		
		cboCaches = new JComboBox(new DefaultComboBoxModel(MTGControler.getInstance().getListCaches().toArray()));
		GridBagConstraints gbc_cboCaches = new GridBagConstraints();
		gbc_cboCaches.insets = new Insets(0, 0, 5, 0);
		gbc_cboCaches.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboCaches.gridx = 1;
		gbc_cboCaches.gridy = 4;
		getContentPane().add(cboCaches, gbc_cboCaches);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.fill = GridBagConstraints.VERTICAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 5;
		getContentPane().add(panel, gbc_panel);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		panel.add(btnSave);
		
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


	public JComboBox getCboDAOs() {
		return cboDAOs;
	}
	public JComboBox getCboPictures() {
		return cboPictures;
	}
	public JComboBox getCboDashboard() {
		return cboDashboard;
	}
	public JComboBox getCboCaches() {
		return cboCaches;
	}
	public JComboBox getCboProvider() {
		return cboProvider;
	}
}
