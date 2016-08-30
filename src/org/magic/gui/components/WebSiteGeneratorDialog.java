package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.magic.api.beans.MagicCollection;
import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.services.MagicFactory;

public class WebSiteGeneratorDialog extends JDialog {
	private JTextField txtDest;
	
	private boolean value=false;
	JComboBox cboTemplates;
	JList<MagicCollection> list ;
	JList<MagicPricesProvider> lstProviders;
	
	public File getDest() {
		return new File(txtDest.getText());
	}

	public String getTemplate() {
		return cboTemplates.getSelectedItem().toString();
	}
	

	public WebSiteGeneratorDialog(List<MagicCollection> cols) {
		setSize(new Dimension(571, 329));
		setModal(true);
		setTitle("WebSite Properties");
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		
		File f = new File("./templates");
		
		List<String> arrayTemplates=new ArrayList<String>();
		
		for (File temp : f.listFiles())
			arrayTemplates.add(temp.getName());
		
		cboTemplates = new JComboBox(arrayTemplates.toArray());
	
		panel.add(cboTemplates);
		
		txtDest = new JTextField(new File(MagicFactory.getInstance().get("default-website-dir")).getAbsolutePath());
		
		panel.add(txtDest);
		txtDest.setColumns(20);
		
		JButton btnDestChoose = new JButton("...");
		
		panel.add(btnDestChoose);
		
		JPanel panneauBas = new JPanel();
		getContentPane().add(panneauBas, BorderLayout.SOUTH);
		
		JButton btnGenerate = new JButton("Generate");
		
		panneauBas.add(btnGenerate);
		
		JPanel panneaucentral = new JPanel();
		getContentPane().add(panneaucentral, BorderLayout.CENTER);
		GridBagLayout gbl_panneaucentral = new GridBagLayout();
		gbl_panneaucentral.columnWidths = new int[]{258, 258, 0};
		gbl_panneaucentral.rowHeights = new int[]{35, 191, 0};
		gbl_panneaucentral.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panneaucentral.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panneaucentral.setLayout(gbl_panneaucentral);
		
		JLabel lblChooseYourCollections = new JLabel("Choose your collections :");
		GridBagConstraints gbc_lblChooseYourCollections = new GridBagConstraints();
		gbc_lblChooseYourCollections.insets = new Insets(0, 0, 5, 5);
		gbc_lblChooseYourCollections.gridx = 0;
		gbc_lblChooseYourCollections.gridy = 0;
		panneaucentral.add(lblChooseYourCollections, gbc_lblChooseYourCollections);
		
		JLabel lblChooseYourPrices = new JLabel("Choose your prices providers (or not) :");
		GridBagConstraints gbc_lblChooseYourPrices = new GridBagConstraints();
		gbc_lblChooseYourPrices.insets = new Insets(0, 0, 5, 0);
		gbc_lblChooseYourPrices.gridx = 1;
		gbc_lblChooseYourPrices.gridy = 0;
		panneaucentral.add(lblChooseYourPrices, gbc_lblChooseYourPrices);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		panneaucentral.add(scrollPane, gbc_scrollPane);
		list = new JList<MagicCollection>(cols.toArray(new MagicCollection[cols.size()]));
		lstProviders = new JList<MagicPricesProvider>(MagicFactory.getInstance().getEnabledPricers().toArray(new MagicPricesProvider[MagicFactory.getInstance().getEnabledPricers().size() ]));
		
		scrollPane.setViewportView(list);
		
		JScrollPane scrollProviders = new JScrollPane();
		GridBagConstraints gbc_scrollProviders = new GridBagConstraints();
		gbc_scrollProviders.fill = GridBagConstraints.BOTH;
		gbc_scrollProviders.gridx = 1;
		gbc_scrollProviders.gridy = 1;
		panneaucentral.add(scrollProviders, gbc_scrollProviders);
		
		scrollProviders.setViewportView(lstProviders);
		
		btnDestChoose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser choose = new JFileChooser(txtDest.getText());
				choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				choose.showSaveDialog(null);
				File dest = choose.getSelectedFile();
				
				if(dest==null)
					dest=new File(".");

				txtDest.setText(dest.getAbsolutePath());
			}
		});
		
		btnGenerate.addActionListener(new ActionListener() {
			

			public void actionPerformed(ActionEvent arg0) {
				value=true;
				setVisible(false);
				
			}
		});
		
		setLocationRelativeTo(null);
	}


	public List<MagicCollection> getSelectedCollections() {
		return list.getSelectedValuesList();
	}

	public boolean value() {
		return value;
	}

	public List<MagicPricesProvider> getPriceProviders() {
		return lstProviders.getSelectedValuesList();
	}

}
