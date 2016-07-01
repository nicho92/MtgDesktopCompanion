package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.magic.api.beans.MagicCollection;
import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.services.MagicFactory;

public class PriceCatalogExportDialog extends JDialog {
	private JTextField txtDest;
	
	private boolean value=false;
	JComboBox<MagicPricesProvider> lstProviders;
	
	public File getDest() {
		return new File(txtDest.getText());
	}

	

	public PriceCatalogExportDialog(MagicCollection selectedcol) {
		setSize(new Dimension(420, 170));
		setModal(true);
		setTitle("Price Catalog Export : " + selectedcol.getName());
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		
		txtDest = new JTextField(new File(".").getAbsolutePath()+selectedcol.getName()+".csv");
		
		panel.add(txtDest);
		txtDest.setColumns(20);
		
		JButton btnDestChoose = new JButton("...");
		
		panel.add(btnDestChoose);
		
		JPanel panneauBas = new JPanel();
		getContentPane().add(panneauBas, BorderLayout.SOUTH);
		
		JButton btnGenerate = new JButton("Export");
		
		panneauBas.add(btnGenerate);
		lstProviders = new JComboBox<MagicPricesProvider>(MagicFactory.getInstance().getEnabledPricers().toArray(new MagicPricesProvider[MagicFactory.getInstance().getEnabledPricers().size() ]));
		
		JScrollPane scrollProviders = new JScrollPane();
		getContentPane().add(scrollProviders, BorderLayout.CENTER);
		
		scrollProviders.setViewportView(lstProviders);
		
		btnDestChoose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser choose = new JFileChooser(txtDest.getText());
				choose.setFileSelectionMode(JFileChooser.FILES_ONLY);
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
	}
	public boolean value() {
		return value;
	}

	public MagicPricesProvider getPriceProviders() {
		return (MagicPricesProvider)lstProviders.getSelectedItem();
	}

}
