package org.magic.gui.components;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Currency;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.OrderEntry.TYPE_ITEM;
import org.magic.api.beans.OrderEntry.TYPE_TRANSACTION;
import org.magic.tools.UITools;

public class OrderEntryPanel extends JPanel {
	
	
	private JTextField txtDescription;
	private JTextField txtPrice;
	private JTextField txtSource;
	private JTextField txtidTransaction;
	private JTextField txtDateTransaction;
	private JComboBox<MagicEdition> cboEditions;
	private JComboBox<TYPE_TRANSACTION> cboTransactionType;
	private JComboBox<TYPE_ITEM> cboTypeItem;
	private JComboBox<Currency> cboCurrency;
	private OrderEntry o;
	
	public OrderEntryPanel() {
		initGUI();
		o=new OrderEntry();
	}
	
	public void setOrderEntry(OrderEntry o)
	{
		this.o=o;
		initField();
	}
	
	private void initField()
	{
		txtDescription.setText(o.getDescription());
		txtPrice.setText(String.valueOf(o.getItemPrice()));
		txtSource.setText(o.getSource());
		txtidTransaction.setText(o.getIdTransation());
		cboEditions.setSelectedItem(o.getEdition());
		cboTransactionType.setSelectedItem(o.getTypeTransaction());
		cboCurrency.setSelectedItem(o.getCurrency());
		cboTypeItem.setSelectedItem(o.getType());
		txtDateTransaction.setText(UITools.formatDate(o.getTransationDate()));
	}
	
	public OrderEntry getOrderEntry()
	{
		return o;
	}
	
	
	public void save()
	{
		o.setDescription(txtDescription.getText());
		o.setEdition((MagicEdition)cboEditions.getSelectedItem());
		o.setCurrency((Currency)cboCurrency.getSelectedItem());
		o.setIdTransation(txtidTransaction.getText());
		o.setItemPrice(Double.parseDouble(txtPrice.getText()));
		o.setSource(txtSource.getText());
		o.setTypeTransaction((TYPE_TRANSACTION)cboTransactionType.getSelectedItem());
		o.setType((TYPE_ITEM)cboTypeItem.getSelectedItem());
		o.setTransationDate(UITools.parseDate(txtDateTransaction.getText()));
		o.setUpdated(true);
	}
	
	
	
	
	public void initGUI() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{126, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		setLayout(gridBagLayout);
		
		
		txtDescription = new JTextField(10);
		add(txtDescription, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 0));
		
		add(new JLabel("Description :"), UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 0, 0));
		add(new JLabel("Edition :"), UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 0, 1));
		add(new JLabel("Transaction : "), UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 0, 2));
		add(new JLabel("Item Type :"), UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 0, 3));		
		add(new JLabel("Price : "), UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 0, 4));
		add(new JLabel("Source :"), UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 0, 5));
		add(new JLabel("ID Transaction :"), UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 0, 6));
		add(new JLabel("Date :"), UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 0, 7));
		
		cboEditions = UITools.createComboboxEditions();
		add(cboEditions, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 1));
	
		cboTransactionType = UITools.createCombobox(OrderEntry.TYPE_TRANSACTION.values());
		add(cboTransactionType, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 2));
				
		cboTypeItem = UITools.createCombobox(OrderEntry.TYPE_ITEM.values());
		add(cboTypeItem, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 3));
		
		JPanel panelPrice = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelPrice.getLayout();
		flowLayout.setVgap(0);
		flowLayout.setHgap(0);
		add(panelPrice, UITools.createGridBagConstraints(GridBagConstraints.WEST,GridBagConstraints.VERTICAL, 1, 4));
		txtPrice = new JTextField(10);
		panelPrice.add(txtPrice);
		
		cboCurrency = UITools.createCombobox(new ArrayList<>(Currency.getAvailableCurrencies()));
		panelPrice.add(cboCurrency);
		
		txtSource = new JTextField(10);
		add(txtSource, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 5));
		
		txtidTransaction = new JTextField(10);
		add(txtidTransaction, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 6));
		
		txtDateTransaction = new JTextField(10);
		add(txtDateTransaction, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 7));
		
	}

	public OrderEntry newOrderEntry() {
		OrderEntry o = new OrderEntry();
		o.setDescription(txtDescription.getText());
		o.setEdition((MagicEdition)cboEditions.getSelectedItem());
		o.setCurrency((Currency)cboCurrency.getSelectedItem());
		o.setIdTransation(txtidTransaction.getText());
		o.setItemPrice(Double.parseDouble(txtPrice.getText()));
		o.setSource(txtSource.getText());
		o.setTypeTransaction((TYPE_TRANSACTION)cboTransactionType.getSelectedItem());
		o.setType((TYPE_ITEM)cboTypeItem.getSelectedItem());
		o.setTransationDate(UITools.parseDate(txtDateTransaction.getText()));
		o.setUpdated(true);
		return o;
	}

//	
//	@Override
//	public String getTitle() {
//		return "Order Entry Panel";
//	}
	
}
