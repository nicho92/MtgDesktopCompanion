package org.magic.gui.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Currency;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.magic.api.beans.Announce;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.dialog.JContactChooserDialog;
import org.magic.tools.UITools;

public class AnnounceDetailPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private Announce announce;
	private JComboBox<Currency> cboCurrency;
	private JTextArea descriptionJTextArea;
	private JXDateTimePicker endDateFld;
	private JXDateTimePicker startDateFld;
	private JTextField titleJTextField;
	private JTextField totalPriceJTextField;
	private JComboBox<TransactionDirection> cboType;
	private JComboBox<EnumItems> cboCategories;
	private JButton btnContact;
	private JSpinner sldReduction;

	public AnnounceDetailPanel() {
		
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 104, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4 };
		setLayout(gridBagLayout);
				
		GridBagConstraints labelGbc7 = new GridBagConstraints();
		labelGbc7.insets = new Insets(5, 5, 5, 5);
		labelGbc7.gridx = 0;
		labelGbc7.gridy = 0;
		add(new JLangLabel("TYPE",true), labelGbc7);

		cboType = UITools.createCombobox(TransactionDirection.values());
		GridBagConstraints componentGbc7 = new GridBagConstraints();
		componentGbc7.insets = new Insets(5, 0, 5, 0);
		componentGbc7.fill = GridBagConstraints.HORIZONTAL;
		componentGbc7.gridx = 1;
		componentGbc7.gridy = 0;
		add(cboType, componentGbc7);
		
				
		GridBagConstraints labelGbc5 = new GridBagConstraints();
		labelGbc5.insets = new Insets(5, 5, 5, 5);
		labelGbc5.gridx = 0;
		labelGbc5.gridy = 1;
		add(new JLangLabel("TITLE",true), labelGbc5);

		titleJTextField = new JTextField();
		GridBagConstraints componentGbc5 = new GridBagConstraints();
		componentGbc5.insets = new Insets(5, 0, 5, 0);
		componentGbc5.fill = GridBagConstraints.HORIZONTAL;
		componentGbc5.gridx = 1;
		componentGbc5.gridy = 1;
		add(titleJTextField, componentGbc5);

		GridBagConstraints labelGbc1 = new GridBagConstraints();
		labelGbc1.insets = new Insets(5, 5, 5, 5);
		labelGbc1.gridx = 0;
		labelGbc1.gridy = 2;
		add(new JLangLabel("DESCRIPTION",true), labelGbc1);

		descriptionJTextArea = new JTextArea();
		GridBagConstraints componentGbc1 = new GridBagConstraints();
		componentGbc1.insets = new Insets(5, 0, 5, 0);
		componentGbc1.fill = GridBagConstraints.BOTH;
		componentGbc1.gridx = 1;
		componentGbc1.gridy = 2;
		add(new JScrollPane(descriptionJTextArea), componentGbc1);
		
		GridBagConstraints labelGbc4 = new GridBagConstraints();
		labelGbc4.insets = new Insets(5, 5, 5, 5);
		labelGbc4.gridx = 0;
		labelGbc4.gridy = 3;
		add(new JLangLabel("START_DATE",true), labelGbc4);

		startDateFld = new JXDateTimePicker();
		GridBagConstraints componentGbc4 = new GridBagConstraints();
		componentGbc4.insets = new Insets(5, 0, 5, 0);
		componentGbc4.fill = GridBagConstraints.HORIZONTAL;
		componentGbc4.gridx = 1;
		componentGbc4.gridy = 3;
		add(startDateFld, componentGbc4);

		GridBagConstraints labelGbc2 = new GridBagConstraints();
		labelGbc2.insets = new Insets(5, 5, 5, 5);
		labelGbc2.gridx = 0;
		labelGbc2.gridy = 4;
		add(new JLangLabel("END_DATE",true), labelGbc2);

		endDateFld = new JXDateTimePicker();
		GridBagConstraints componentGbc2 = new GridBagConstraints();
		componentGbc2.insets = new Insets(5, 0, 5, 0);
		componentGbc2.fill = GridBagConstraints.HORIZONTAL;
		componentGbc2.gridx = 1;
		componentGbc2.gridy = 4;
		add(endDateFld, componentGbc2);

		GridBagConstraints labelGbc6 = new GridBagConstraints();
		labelGbc6.insets = new Insets(5, 5, 5, 5);
		labelGbc6.gridx = 0;
		labelGbc6.gridy =5;
		add(new JLangLabel("TOTAL",true), labelGbc6);

		totalPriceJTextField = new JTextField();
		GridBagConstraints componentGbc6 = new GridBagConstraints();
		componentGbc6.insets = new Insets(5, 0, 5, 0);
		componentGbc6.fill = GridBagConstraints.HORIZONTAL;
		componentGbc6.gridx = 1;
		componentGbc6.gridy = 5;
		add(totalPriceJTextField, componentGbc6);
		
		GridBagConstraints labelGbc0 = new GridBagConstraints();
		labelGbc0.insets = new Insets(5, 5, 0, 5);
		labelGbc0.gridx = 0;
		labelGbc0.gridy = 6;
		add(new JLangLabel("CURRENCY",true), labelGbc0);
		
		cboCurrency = UITools.createCombobox(Currency.getAvailableCurrencies().stream().toList());
		GridBagConstraints componentGbc0 = new GridBagConstraints();
		componentGbc0.insets = new Insets(5, 0, 0, 0);
		componentGbc0.fill = GridBagConstraints.HORIZONTAL;
		componentGbc0.gridx = 1;
		componentGbc0.gridy = 6;
		add(cboCurrency, componentGbc0);
		
		
		GridBagConstraints labelGbc11 = new GridBagConstraints();
		labelGbc11.insets = new Insets(5, 5, 0, 5);
		labelGbc11.gridx = 0;
		labelGbc11.gridy = 7;
		add(new JLangLabel("CATEGORIE",true), labelGbc11);
		
		cboCategories= UITools.createCombobox(EnumItems.values());
		GridBagConstraints componentGbc10 = new GridBagConstraints();
		componentGbc10.insets = new Insets(5, 0, 0, 0);
		componentGbc10.fill = GridBagConstraints.HORIZONTAL;
		componentGbc10.gridx = 1;
		componentGbc10.gridy = 7;
		add(cboCategories, componentGbc10);
		
	
		GridBagConstraints labelGbc13 = new GridBagConstraints();
		labelGbc13.insets = new Insets(5, 5, 0, 5);
		labelGbc13.gridx = 0;
		labelGbc13.gridy = 8;
		add(new JLangLabel("REDUCTION",true), labelGbc13);
		
		sldReduction= new JSpinner(new SpinnerNumberModel(0.0, 0.0,100, 0.5));
		GridBagConstraints componentGbc13 = new GridBagConstraints();
		componentGbc13.insets = new Insets(5, 0, 0, 0);
		componentGbc13.fill = GridBagConstraints.HORIZONTAL;
		componentGbc13.gridx = 1;
		componentGbc13.gridy = 8;
		add(sldReduction, componentGbc13);
		
		GridBagConstraints labelGbc12 = new GridBagConstraints();
		labelGbc12.insets = new Insets(5, 5, 0, 5);
		labelGbc12.gridx = 0;
		labelGbc12.gridy = 9;
		add(new JLangLabel("CONTACT",true), labelGbc12);				
		
		
		btnContact = new JButton();
		GridBagConstraints labelGbc14 = new GridBagConstraints();
		labelGbc14.insets = new Insets(5, 5, 0, 5);
		labelGbc14.gridx = 1;
		labelGbc14.gridy = 9;
		add(btnContact, labelGbc14);				
		
		
		
		btnContact.addActionListener(al->{
			JContactChooserDialog diag = new JContactChooserDialog();
			diag.setVisible(true);
			
			if(diag.getSelectedContacts()!=null) {
				announce.setContact(diag.getSelectedContacts());
				btnContact.setText(announce.getContact().toString());
				
			}
			
			
		});
		
		
	}

	public Announce getAnnounce() {
		
		announce.setTitle(titleJTextField.getText());
		announce.setDescription(descriptionJTextArea.getText());
		announce.setStartDate(startDateFld.getDate());
		announce.setEndDate(endDateFld.getDate());
		announce.setCurrency((Currency)cboCurrency.getSelectedItem());
		announce.setType( (TransactionDirection) cboType.getSelectedItem());
		announce.setTotalPrice(UITools.parseDouble(totalPriceJTextField.getText()));
		announce.setCategorie((EnumItems)cboCategories.getSelectedItem());
		announce.setPercentReduction(((Number)sldReduction.getValue()).doubleValue());
		return announce;
	}

	public void setAnnounce(Announce announce) {
			this.announce=announce;
			titleJTextField.setText(announce.getTitle());
			descriptionJTextArea.setText(announce.getDescription());
			startDateFld.setDate(announce.getStartDate());
			endDateFld.setDate(announce.getEndDate());
			cboCurrency.setSelectedItem(announce.getCurrency());
			cboType.setSelectedItem(announce.getType());
			totalPriceJTextField.setText(String.valueOf(announce.getTotalPrice()));
			cboCategories.setSelectedItem(announce.getCategorie());
			btnContact.setText(announce.getContact().toString());
			sldReduction.setValue(announce.getPercentReduction());
	}

	//@Override
	public String getTitle() {
		return "Details";
	}

}
