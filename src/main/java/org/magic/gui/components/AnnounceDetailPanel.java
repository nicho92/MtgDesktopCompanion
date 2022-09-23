package org.magic.gui.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Currency;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.magic.api.beans.Announce;
import org.magic.api.beans.Announce.STATUS;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.dialog.JContactChooserDialog;
import org.magic.tools.UITools;

public class AnnounceDetailPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private Announce announce;
	private JComboBox<Currency> cboCurrency;
	private RSyntaxTextArea descriptionJTextArea;
	private JXDateTimePicker endDateFld;
	private JXDateTimePicker startDateFld;
	private JTextField titleJTextField;
	private JTextField totalPriceJTextField;
	private JComboBox<TransactionDirection> cboType;
	private JComboBox<EnumItems> cboCategories;
	private JComboBox<STATUS> cboStatus;
	private JButton btnContact;
	private JSpinner sldReduction;
	private JComboBox<EnumCondition> cboConditions;

	public AnnounceDetailPanel() {


		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 104, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0E-4, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		setLayout(gridBagLayout);

		add(new JLangLabel("TYPE",true), UITools.createGridBagConstraints(null, null, 0, 0));
		cboType = UITools.createCombobox(TransactionDirection.values());
		add(cboType, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 0));

		add(new JLangLabel("TITLE",true), UITools.createGridBagConstraints(null, null, 0, 1));
		titleJTextField = new JTextField();
		add(titleJTextField,  UITools.createGridBagConstraints(null,  GridBagConstraints.HORIZONTAL, 1, 1));

		add(new JLangLabel("DESCRIPTION",true), UITools.createGridBagConstraints(null, null, 0, 2));
		descriptionJTextArea = new RSyntaxTextArea();
		descriptionJTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
		add(new RTextScrollPane(descriptionJTextArea), UITools.createGridBagConstraints(null,  GridBagConstraints.BOTH, 1, 2));

		add(new JLangLabel("START_DATE",true), UITools.createGridBagConstraints(null, null, 0, 3));
		startDateFld = new JXDateTimePicker();
		add(startDateFld, UITools.createGridBagConstraints(null,  GridBagConstraints.HORIZONTAL, 1, 3));

		add(new JLangLabel("END_DATE",true), UITools.createGridBagConstraints(null, null, 0, 4));
		endDateFld = new JXDateTimePicker();
		add(endDateFld, UITools.createGridBagConstraints(null,  GridBagConstraints.HORIZONTAL, 1, 4));

		add(new JLangLabel("TOTAL",true), UITools.createGridBagConstraints(null, null, 0, 5));
		totalPriceJTextField = new JTextField();
		add(totalPriceJTextField, UITools.createGridBagConstraints(null,  GridBagConstraints.HORIZONTAL, 1, 5));

		add(new JLangLabel("CURRENCY",true), UITools.createGridBagConstraints(null, null, 0, 6));
		cboCurrency = UITools.createCombobox(Currency.getAvailableCurrencies().stream().toList());
		add(cboCurrency, UITools.createGridBagConstraints(null,  GridBagConstraints.HORIZONTAL, 1, 6));

		add(new JLangLabel("CATEGORIE",true), UITools.createGridBagConstraints(null, null, 0, 7));
		cboCategories= UITools.createCombobox(EnumItems.values());
		add(cboCategories, UITools.createGridBagConstraints(null,  GridBagConstraints.HORIZONTAL, 1, 7));

		add(new JLangLabel("REDUCTION",true), UITools.createGridBagConstraints(null, null, 0, 8));
		sldReduction= new JSpinner(new SpinnerNumberModel(0.0, 0.0,100, 0.5));
		add(sldReduction, UITools.createGridBagConstraints(null,  GridBagConstraints.HORIZONTAL, 1, 8));

		add(new JLangLabel("CONTACT",true), UITools.createGridBagConstraints(null, null, 0, 9));
		btnContact = new JButton();
		add(btnContact, UITools.createGridBagConstraints(null,  GridBagConstraints.HORIZONTAL, 1, 9));

		add(new JLangLabel("CONDITION",true), UITools.createGridBagConstraints(null, null, 0, 10));
		cboConditions= UITools.createCombobox(EnumCondition.values());
		add(cboConditions, UITools.createGridBagConstraints(null,  GridBagConstraints.HORIZONTAL, 1, 10));


		add(new JLangLabel("STATUS",true), UITools.createGridBagConstraints(null, null, 0, 11));
		cboStatus= UITools.createCombobox(STATUS.values());
		add(cboStatus, UITools.createGridBagConstraints(null,  GridBagConstraints.HORIZONTAL, 1, 11));




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
		announce.setCondition((EnumCondition)cboConditions.getSelectedItem());
		announce.setStatus((STATUS)cboStatus.getSelectedItem());
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
			cboConditions.setSelectedItem(announce.getCondition());
			cboStatus.setSelectedItem(announce.getStatus());
	}

	//@Override
	@Override
	public String getTitle() {
		return "Details";
	}

}
