package org.magic.gui.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.magic.api.beans.MTGNews;
import org.magic.api.interfaces.MTGNewsProvider;
import org.magic.gui.components.widgets.JLangLabel;
import org.magic.services.tools.UITools;
public class NewsEditorPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private MTGNews magicNews = new MTGNews();
	private JTextField categorieJTextField;
	private JTextField nameJTextField;
	private JTextField urlJTextField;
	private JComboBox<MTGNewsProvider> cboType;

	public NewsEditorPanel(org.magic.api.beans.MTGNews newMagicNews) {
		this();
		setMagicNews(newMagicNews);
	}

	public NewsEditorPanel() {
		var gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0E-4 };
		setLayout(gridBagLayout);

		var lblType = new JLabel("Type:");
		var gbclblType = new GridBagConstraints();
		gbclblType.anchor = GridBagConstraints.EAST;
		gbclblType.insets = new Insets(0, 0, 5, 5);
		gbclblType.gridx = 0;
		gbclblType.gridy = 0;
		add(lblType, gbclblType);

		cboType = UITools.createComboboxPlugins(MTGNewsProvider.class, false);

		var gbccboType = new GridBagConstraints();
		gbccboType.insets = new Insets(0, 0, 5, 0);
		gbccboType.fill = GridBagConstraints.HORIZONTAL;
		gbccboType.gridx = 1;
		gbccboType.gridy = 0;
		add(cboType, gbccboType);

		var categorieLabel = new JLabel("Category:");
		var labelGbc0 = new GridBagConstraints();
		labelGbc0.insets = new Insets(5, 5, 5, 5);
		labelGbc0.gridx = 0;
		labelGbc0.gridy = 1;
		add(categorieLabel, labelGbc0);

		categorieJTextField = new JTextField();
		var componentGbc0 = new GridBagConstraints();
		componentGbc0.insets = new Insets(5, 0, 5, 0);
		componentGbc0.fill = GridBagConstraints.HORIZONTAL;
		componentGbc0.gridx = 1;
		componentGbc0.gridy = 1;
		add(categorieJTextField, componentGbc0);

		var labelGbc2 = new GridBagConstraints();
		labelGbc2.insets = new Insets(5, 5, 5, 5);
		labelGbc2.gridx = 0;
		labelGbc2.gridy = 2;
		add(new JLangLabel("NAME",true), labelGbc2);

		nameJTextField = new JTextField();
		var componentGbc2 = new GridBagConstraints();
		componentGbc2.insets = new Insets(5, 0, 5, 0);
		componentGbc2.fill = GridBagConstraints.HORIZONTAL;
		componentGbc2.gridx = 1;
		componentGbc2.gridy = 2;
		add(nameJTextField, componentGbc2);

		var urlLabel = new JLabel("Url:");
		var labelGbc3 = new GridBagConstraints();
		labelGbc3.insets = new Insets(5, 5, 0, 5);
		labelGbc3.gridx = 0;
		labelGbc3.gridy = 3;
		add(urlLabel, labelGbc3);

		urlJTextField = new JTextField();
		var componentGbc3 = new GridBagConstraints();
		componentGbc3.insets = new Insets(5, 0, 0, 0);
		componentGbc3.fill = GridBagConstraints.HORIZONTAL;
		componentGbc3.gridx = 1;
		componentGbc3.gridy = 3;
		add(urlJTextField, componentGbc3);

	}

	public MTGNews getMagicNews() {
		magicNews.setProvider(((MTGNewsProvider) cboType.getSelectedItem()));
		magicNews.setCategorie(categorieJTextField.getText());
		magicNews.setName(nameJTextField.getText());
		magicNews.setUrl(urlJTextField.getText());
		return magicNews;
	}

	public void setMagicNews(MTGNews newMagicNews) {
		this.magicNews = newMagicNews;
		nameJTextField.setText(magicNews.getName());
		urlJTextField.setText(String.valueOf(magicNews.getUrl()));
		categorieJTextField.setText(magicNews.getCategorie());
		cboType.setSelectedItem(newMagicNews.getProvider());
	}

}
