package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.magic.api.beans.MagicCollection;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class PriceCatalogExportDialog extends JDialog {
	private JTextField txtDest;

	private boolean value = false;
	JComboBox<MTGPricesProvider> lstProviders;

	public File getDest() {
		return new File(txtDest.getText());
	}

	public PriceCatalogExportDialog(MagicCollection selectedcol) {
		setSize(new Dimension(420, 170));
		setModal(true);
		setIconImage(MTGConstants.ICON_TAB_PRICES.getImage());
		setTitle("Price Catalog Export : " + selectedcol.getName());
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		txtDest = new JTextField(new File(MTGConstants.CONF_DIR, selectedcol.getName() + ".csv").getAbsolutePath());

		panel.add(txtDest);
		txtDest.setColumns(20);

		JButton btnDestChoose = new JButton("...");

		panel.add(btnDestChoose);

		JPanel panneauBas = new JPanel();
		getContentPane().add(panneauBas, BorderLayout.SOUTH);

		JButton btnGenerate = new JButton(MTGControler.getInstance().getLangService().getCapitalize("EXPORT"));

		panneauBas.add(btnGenerate);
		lstProviders = new JComboBox<>(MTGControler.getInstance().getEnabledPricers()
				.toArray(new MTGPricesProvider[MTGControler.getInstance().getEnabledPricers().size()]));

		JScrollPane scrollProviders = new JScrollPane();
		getContentPane().add(scrollProviders, BorderLayout.CENTER);

		scrollProviders.setViewportView(lstProviders);

		btnDestChoose.addActionListener(e -> {
			JFileChooser choose = new JFileChooser(txtDest.getText());
			choose.setFileSelectionMode(JFileChooser.FILES_ONLY);
			choose.showSaveDialog(null);
			File dest = choose.getSelectedFile();

			if (dest == null)
				dest = new File(".");

			txtDest.setText(dest.getAbsolutePath());
		});

		btnGenerate.addActionListener(e -> {
			value = true;
			setVisible(false);
		});
		setLocationRelativeTo(null);
	}

	public boolean value() {
		return value;
	}

	public MTGPricesProvider getPriceProviders() {
		return (MTGPricesProvider) lstProviders.getSelectedItem();
	}

}
