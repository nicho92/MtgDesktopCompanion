package org.magic.gui.components.dialog;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.listEnabledPlugins;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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

import org.magic.api.beans.MagicCollection;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.gui.components.widgets.JTextFieldFileChooser;
import org.magic.gui.renderer.MagicCollectionIconListRenderer;
import org.magic.gui.renderer.PluginIconListRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.tools.UITools;

public class WebSiteGeneratorDialog extends JDialog {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private JTextFieldFileChooser txtDest;

	private boolean value = false;
	private JComboBox<String> cboTemplates;
	private JList<MagicCollection> list;
	private JList<MTGPricesProvider> lstProviders;

	public File getDest() {
		return txtDest.getFile();
	}

	public String getTemplate() {
		return cboTemplates.getSelectedItem().toString();
	}

	public WebSiteGeneratorDialog(List<MagicCollection> cols) {
		setSize(new Dimension(571, 329));
		setModal(true);
		setTitle(capitalize("GENERATE_WEBSITE"));
		setIconImage(MTGConstants.ICON_WEBSITE_24.getImage());
		getContentPane().setLayout(new BorderLayout(0, 0));

		var panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		var f = new File(MTGConstants.MTG_TEMPLATES_DIR);

		List<String> arrayTemplates = new ArrayList<>();

		for (File temp : f.listFiles())
			arrayTemplates.add(temp.getName());

		cboTemplates = UITools.createCombobox(arrayTemplates);

		panel.add(cboTemplates);

		txtDest = new JTextFieldFileChooser(20,JFileChooser.DIRECTORIES_ONLY,MTGControler.getInstance().get("default-website-dir"));
		panel.add(txtDest);

		var panneauBas = new JPanel();
		getContentPane().add(panneauBas, BorderLayout.SOUTH);

		var btnGenerate = new JButton(capitalize("START"));

		panneauBas.add(btnGenerate);

		var panneaucentral = new JPanel();
		getContentPane().add(panneaucentral, BorderLayout.CENTER);
		var gblpanneaucentral = new GridBagLayout();
		gblpanneaucentral.columnWidths = new int[] { 258, 258, 0 };
		gblpanneaucentral.rowHeights = new int[] { 35, 191, 0 };
		gblpanneaucentral.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gblpanneaucentral.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panneaucentral.setLayout(gblpanneaucentral);

		var lblChooseYourCollections = new JLabel(
				capitalize("CHOOSE_COLLECTIONS") + " :");
		var gbclblChooseYourCollections = UITools.createGridBagConstraints(null, null, 0, 0);
		panneaucentral.add(lblChooseYourCollections, gbclblChooseYourCollections);

		var lblChooseYourPrices = new JLabel(
				capitalize("CHOOSE_PRICER") + " :");
		var gbclblChooseYourPrices = UITools.createGridBagConstraints(null, null,1, 0);
		panneaucentral.add(lblChooseYourPrices, gbclblChooseYourPrices);


		list = new JList<>(cols.toArray(new MagicCollection[cols.size()]));
		list.setCellRenderer(new MagicCollectionIconListRenderer());
		lstProviders = new JList<>(listEnabledPlugins(MTGPricesProvider.class)
				.toArray(new MTGPricesProvider[listEnabledPlugins(MTGPricesProvider.class).size()]));
		lstProviders.setCellRenderer(new PluginIconListRenderer());
		panneaucentral.add(new JScrollPane(list), UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 0, 1));


		panneaucentral.add(new JScrollPane(lstProviders), UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 1));


		btnGenerate.addActionListener(e -> {
			value = true;
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
