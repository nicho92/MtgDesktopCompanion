package org.magic.gui.models;

import java.util.Date;

import org.magic.api.beans.enums.EnumPaymentProvider;
import org.magic.api.beans.enums.EnumTransactionDirection;
import org.magic.api.beans.enums.EnumTransactionStatus;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.MoneyValue;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.services.tools.UITools;

public class TransactionsTableModel extends GenericTableModel<Transaction> {


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	public TransactionsTableModel() {

		setWritable(true);
		setColumns("ID","DATE","CONTACT","ITEMS", "SHIPPING","Reduction","TOTAL", "DATE PAYMENT","PAYMENT METHOD","DATE SEND","TRACKING NUMBER","MESSAGE","STATUT","DIRECTION","Source","EXT TRANSACTION ID" );
	}


	@Override
	public synchronized void setValueAt(Object aValue, int row, int column) {
		
		
		switch (column) {
			case 1:  getItemAt(row).setDateCreation((Date)aValue);break;
			case 4: getItemAt(row).setShippingPrice(UITools.parseDouble(aValue.toString())); break;
			case 5: getItemAt(row).setReduction(UITools.parseDouble(aValue.toString())); break;
			case 7:  getItemAt(row).setDatePayment((Date)aValue);break;
			case 8 : getItemAt(row).setPaymentProvider(EnumPaymentProvider.valueOf(aValue.toString()));break;
			case 9:  getItemAt(row).setDateSend((Date)aValue);break;
			case 10: getItemAt(row).setTransporterShippingCode(String.valueOf(aValue)); break;
			case 11: getItemAt(row).setMessage(String.valueOf(aValue).trim()); break;
			case 12 : getItemAt(row).setStatut(EnumTransactionStatus.valueOf(aValue.toString()));break;
			case 13 : getItemAt(row).setTypeTransaction(EnumTransactionDirection.valueOf(aValue.toString()));break;
			case 14 : getItemAt(row).setSourceShopName(String.valueOf(aValue).trim());break;
			case 15 : getItemAt(row).setSourceShopId(String.valueOf(aValue).trim());break;
		default:break;
		}
		fireTableCellUpdated(row, column);
	}

	@Override
	public Object getValueAt(int row, int column) {

		Transaction it = items.get(row);
		switch (column)
		{
			case 0 : return it;
			case 1 : return it.getDateCreation();
			case 2 : return it.getContact();
			case 3 : return it.getItems().stream().mapToInt(msi->msi.getQte()).sum();
			case 4 : return new MoneyValue(it.getShippingPrice(),it.getCurrency());
			case 5 : return new MoneyValue(it.getReduction(),it.getCurrency());
			case 6 : return new MoneyValue(it.total(),it.getCurrency());
			case 7 : return it.getDatePayment();
			case 8 : return it.getPaymentProvider();
			case 9 : return it.getDateSend();
			case 10 : return it.getTransporterShippingCode();
			case 11 : return it.getMessage();
			case 12 : return it.getStatut();
			case 13 : return it.getTypeTransaction();
			case 14 : return it.getSourceShopName();
			case 15 : return it.getSourceShopId();
			default : return 0;
		}
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if(isWritable())
		{
			return (column!=0 && column!=2 && column!=3 && column!=6);
		}
		else
		{
			return false;
		}
	}


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex==0)
			return Transaction.class;
		
		if(columnIndex==1 || columnIndex==7 || columnIndex==9)
			return Date.class;

		if(columnIndex==3)
			return Integer.class;

		if(columnIndex==2)
			return Contact.class;
		
		if(columnIndex==4 || columnIndex==5 || columnIndex==6)
			return MoneyValue.class;

		if(columnIndex==12)
			return EnumTransactionStatus.class;
		
		if(columnIndex==8)
			return EnumPaymentProvider.class;
		
		if(columnIndex==13)
			return EnumTransactionDirection.class;
		
		
		
		return super.getColumnClass(columnIndex);
	}

}
