package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class WebSiteGeneratorDialog extends JDialog {
	private JTextField txtDest;
	
	private boolean value=false;
	JComboBox cboTemplates;
	JList<MagicCollection> list ;
	JList<MTGPricesProvider> lstProviders;
	
	public File getDest() {
		return new File(txtDest.getText());
	}

	public String getTemplate() {
		return cboTemplates.getSelectedItem().toString();
	}
	

	public WebSiteGeneratorDialog(List<MagicCollection> cols) {
		setSize(new Dimension(571, 329));
		setModal(true);
		setTitle(MTGControler.getInstance().getLangService().getCapitalize("GENERATE_WEBSITE"));
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		
		File f = new File(MTGConstants.MTG_TEMPLATES_DIR);
		
		List<String> arrayTemplates=new ArrayList<>();
		
		for (File temp : f.listFiles())
			arrayTemplates.add(temp.getName());
		
		cboTemplates = new JComboBox(arrayTemplates.toArray());
	
		panel.add(cboTemplates);
		
		txtDest = new JTextField(new File(MTGControler.getInstance().get("default-website-dir")).getAbsolutePath());
		
		panel.add(txtDest);
		txtDest.setColumns(20);
		
		JButton btnDestChoose = new JButton("...");
		
		panel.add(btnDestChoose);
		
		JPanel panneauBas = new JPanel();
		getContentPane().add(panneauBas, BorderLayout.SOUTH);
		
		JButton btnGenerate = new JButton(MTGControler.getInstance().getLangService().getCapitalize("START"));
		
		panneauBas.add(btnGenerate);
		
		JPanel panneaucentral = new JPanel();
		getContentPane().add(panneaucentral, BorderLayout.CENTER);
		GridBagLayout gblpanneaucentral = new GridBagLayout();
		gblpanneaucentral.columnWidths = new int[]{258, 258, 0};
		gblpanneaucentral.rowHeights = new int[]{35, 191, 0};
		gblpanneaucentral.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gblpanneaucentral.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panneaucentral.setLayout(gblpanneaucentral);
		
		JLabel lblChooseYourCollections = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CHOOSE_COLLECTIONS")+" :");
		GridBagConstraints gbclblChooseYourCollections = new GridBagConstraints();
		gbclblChooseYourCollections.insets = new Insets(0, 0, 5, 5);
		gbclblChooseYourCollections.gridx = 0;
		gbclblChooseYourCollections.gridy = 0;
		panneaucentral.add(lblChooseYourCollections, gbclblChooseYourCollections);
		
		JLabel lblChooseYourPrices = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("CHOOSE_PRICER")+" :");
		GridBagConstraints gbclblChooseYourPrices = new GridBagConstraints();
		gbclblChooseYourPrices.insets = new Insets(0, 0, 5, 0);
		gbclblChooseYourPrices.gridx = 1;
		gbclblChooseYourPrices.gridy = 0;
		panneaucentral.add(lblChooseYourPrices, gbclblChooseYourPrices);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbcscrollPane = new GridBagConstraints();
		gbcscrollPane.fill = GridBagConstraints.BOTH;
		gbcscrollPane.insets = new Insets(0, 0, 0, 5);
		gbcscrollPane.gridx = 0;
		gbcscrollPane.gridy = 1;
		panneaucentral.add(scrollPane, gbcscrollPane);
		list = new JList<>(cols.toArray(new MagicCollection[cols.size()]));
		lstProviders = new JList<>(MTGControler.getInstance().getEnabledPricers().toArray(new MTGPricesProvider[MTGControler.getInstance().getEnabledPricers().size() ]));
		
		scrollPane.setViewportView(list);
		
		JScrollPane scrollProviders = new JScrollPane();
		GridBagConstraints gbcscrollProviders = new GridBagConstraints();
		gbcscrollProviders.fill = GridBagConstraints.BOTH;
		gbcscrollProviders.gridx = 1;
		gbcscrollProviders.gridy = 1;
		panneaucentral.add(scrollProviders, gbcscrollProviders);
		
		scrollProviders.setViewportView(lstProviders);
		
		btnDestChoose.addActionListener(e->{
				JFileChooser choose = new JFileChooser(txtDest.getText());
				choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				choose.showSaveDialog(null);
				File dest = choose.getSelectedFile();
				
				if(dest==null)
					dest=new File(".");

				txtDest.setText(dest.getAbsolutePath());
		});
		
		btnGenerate.addActionListener(e->{
				value=true;
				setVisible(false);
		});
		
		setLocationRelativeTo(null);
	}


	public List<MagicCollection> getSelectedCollections() {
		return list.getSelectedValuesList();
	}

	public boolean value() {
		return value;
	}

	public List<MTGPricesProvider> getPriceProviders() {
		return lstProviders.getSelectedValuesList();
	}

}
