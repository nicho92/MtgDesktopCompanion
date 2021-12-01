package org.magic.gui.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Currency;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.magic.api.beans.Announce;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.tools.UITools;

public class AnnounceDetailPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private Announce announce;
	private JComboBox<Currency> cboCurrency;
	private JTextArea descriptionJTextArea;
	private JXDateTimePicker endDateFld;
	private JXDateTimePicker expirationDateFld;
	private JXDateTimePicker startDateFld;
	private JTextField titleJTextField;
	private JTextField totalPriceJTextField;
	private JComboBox<TransactionDirection> cboType;

	public AnnounceDetailPanel(Announce newAnnounce) {
		this();
		setAnnounce(newAnnounce);
	}

	public AnnounceDetailPanel() {
		
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 104, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4 };
		setLayout(gridBagLayout);
				
						JLabel typeLabel = new JLabel("Type:");
						GridBagConstraints labelGbc7 = new GridBagConstraints();
						labelGbc7.insets = new Insets(5, 5, 5, 5);
						labelGbc7.gridx = 0;
						labelGbc7.gridy = 0;
						add(typeLabel, labelGbc7);
				
						cboType = UITools.createCombobox(TransactionDirection.values());
						GridBagConstraints componentGbc7 = new GridBagConstraints();
						componentGbc7.insets = new Insets(5, 0, 5, 0);
						componentGbc7.fill = GridBagConstraints.HORIZONTAL;
						componentGbc7.gridx = 1;
						componentGbc7.gridy = 0;
						add(cboType, componentGbc7);
		
				JLabel titleLabel = new JLabel("Title:");
				GridBagConstraints labelGbc5 = new GridBagConstraints();
				labelGbc5.insets = new Insets(5, 5, 5, 5);
				labelGbc5.gridx = 0;
				labelGbc5.gridy = 1;
				add(titleLabel, labelGbc5);
		
				titleJTextField = new JTextField();
				GridBagConstraints componentGbc5 = new GridBagConstraints();
				componentGbc5.insets = new Insets(5, 0, 5, 0);
				componentGbc5.fill = GridBagConstraints.HORIZONTAL;
				componentGbc5.gridx = 1;
				componentGbc5.gridy = 1;
				add(titleJTextField, componentGbc5);
		
				JLabel descriptionLabel = new JLabel("Description:");
				GridBagConstraints labelGbc1 = new GridBagConstraints();
				labelGbc1.insets = new Insets(5, 5, 5, 5);
				labelGbc1.gridx = 0;
				labelGbc1.gridy = 2;
				add(descriptionLabel, labelGbc1);
		
				descriptionJTextArea = new JTextArea();
				GridBagConstraints componentGbc1 = new GridBagConstraints();
				componentGbc1.insets = new Insets(5, 0, 5, 0);
				componentGbc1.fill = GridBagConstraints.BOTH;
				componentGbc1.gridx = 1;
				componentGbc1.gridy = 2;
				add(descriptionJTextArea, componentGbc1);


		JLabel startDateLabel = new JLabel("StartDate:");
		GridBagConstraints labelGbc4 = new GridBagConstraints();
		labelGbc4.insets = new Insets(5, 5, 5, 5);
		labelGbc4.gridx = 0;
		labelGbc4.gridy = 3;
		add(startDateLabel, labelGbc4);

		startDateFld = new JXDateTimePicker();
		GridBagConstraints componentGbc4 = new GridBagConstraints();
		componentGbc4.insets = new Insets(5, 0, 5, 0);
		componentGbc4.fill = GridBagConstraints.HORIZONTAL;
		componentGbc4.gridx = 1;
		componentGbc4.gridy = 3;
		add(startDateFld, componentGbc4);
		
				
				
				
		JLabel endDateLabel = new JLabel("EndDate:");
		GridBagConstraints labelGbc2 = new GridBagConstraints();
		labelGbc2.insets = new Insets(5, 5, 5, 5);
		labelGbc2.gridx = 0;
		labelGbc2.gridy = 4;
		add(endDateLabel, labelGbc2);

		endDateFld = new JXDateTimePicker();
		GridBagConstraints componentGbc2 = new GridBagConstraints();
		componentGbc2.insets = new Insets(5, 0, 5, 0);
		componentGbc2.fill = GridBagConstraints.HORIZONTAL;
		componentGbc2.gridx = 1;
		componentGbc2.gridy = 4;
		add(endDateFld, componentGbc2);

		JLabel expirationDateLabel = new JLabel("ExpirationDate:");
		GridBagConstraints labelGbc3 = new GridBagConstraints();
		labelGbc3.insets = new Insets(5, 5, 5, 5);
		labelGbc3.gridx = 0;
		labelGbc3.gridy = 5;
		add(expirationDateLabel, labelGbc3);

		expirationDateFld = new JXDateTimePicker();
		GridBagConstraints componentGbc3 = new GridBagConstraints();
		componentGbc3.insets = new Insets(5, 0, 5, 0);
		componentGbc3.fill = GridBagConstraints.HORIZONTAL;
		componentGbc3.gridx = 1;
		componentGbc3.gridy = 5;
		add(expirationDateFld, componentGbc3);

		JLabel totalPriceLabel = new JLabel("TotalPrice:");
		GridBagConstraints labelGbc6 = new GridBagConstraints();
		labelGbc6.insets = new Insets(5, 5, 5, 5);
		labelGbc6.gridx = 0;
		labelGbc6.gridy =7;
		add(totalPriceLabel, labelGbc6);

		totalPriceJTextField = new JTextField();
		GridBagConstraints componentGbc6 = new GridBagConstraints();
		componentGbc6.insets = new Insets(5, 0, 5, 0);
		componentGbc6.fill = GridBagConstraints.HORIZONTAL;
		componentGbc6.gridx = 1;
		componentGbc6.gridy = 7;
		add(totalPriceJTextField, componentGbc6);
		
				JLabel currencyLabel = new JLabel("Currency:");
				GridBagConstraints labelGbc0 = new GridBagConstraints();
				labelGbc0.insets = new Insets(5, 5, 0, 5);
				labelGbc0.gridx = 0;
				labelGbc0.gridy = 8;
				add(currencyLabel, labelGbc0);
				
					cboCurrency = UITools.createCombobox(Currency.getAvailableCurrencies().stream().toList());
						GridBagConstraints componentGbc0 = new GridBagConstraints();
						componentGbc0.insets = new Insets(5, 0, 0, 0);
						componentGbc0.fill = GridBagConstraints.HORIZONTAL;
						componentGbc0.gridx = 1;
						componentGbc0.gridy = 8;
						add(cboCurrency, componentGbc0);

					
						
						
		if (announce != null) {
			setAnnounce(announce);
		}
	}

	public Announce getAnnounce() {
		return announce;
	}

	public void setAnnounce(Announce announce) {
		this.announce=announce;
		
			titleJTextField.setText(announce.getTitle());
			descriptionJTextArea.setText(announce.getDescription());
			startDateFld.setDate(announce.getStartDate());
			endDateFld.setDate(announce.getEndDate());
			expirationDateFld.setDate(announce.getExpirationDate());
			cboCurrency.setSelectedItem(announce.getCurrency());
			cboType.setSelectedItem(announce.getType());
			totalPriceJTextField.setText(String.valueOf(announce.getTotalPrice()));
	}

	//@Override
	public String getTitle() {
		return "Details";
	}

}
