package org.magic.gui.models;

import java.sql.SQLException;
import java.util.Date;

import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class TransactionsTableModel extends GenericTableModel<Transaction> {


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	public TransactionsTableModel() {

		setWritable(true);
		columns = new String[] { "ID","DATE","CONTACT","ITEMS","TOTAL","Reduction","SHIPPING","MESSAGE","STATUT","DATE PAYMENT","PAYMENT METHOD","DATE SEND","DIRECTION","Source","SourceID" };
	}


	@Override
	public void setValueAt(Object aValue, int row, int column) {
		
		
		switch (column) {
			case 5: getItemAt(row).setReduction(UITools.parseDouble(aValue.toString())); break;
			case 6: getItemAt(row).setShippingPrice(UITools.parseDouble(aValue.toString())); break;
			case 8 : getItemAt(row).setStatut(TransactionStatus.valueOf(aValue.toString()));break;
		default:break;
		}
		
		try {
			MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateTransaction(getItemAt(row));
		} catch (SQLException e) {
		logger.error(e);
		}
	}

	@Override
	public Object getValueAt(int row, int column) {

		Transaction it = items.get(row);

		switch (column)
		{
			case 0 : return it;
			case 1 : return it.getDateCreation();
			case 2 : return it.getContact();
			case 3 : return it.getItems().size();
			case 4 : return it.total();
			case 5 : return it.getReduction();
			case 6 : return it.getShippingPrice();
			case 7 : return it.getMessage();
			case 8 : return it.getStatut();
			case 9 : return it.getDatePayment();
			case 10 : return it.getPaymentProvider();
			case 11 : return it.getDateSend();
			case 12 : return it.getTypeTransaction();
			case 13 : return it.getSourceShopName();
			case 14 : return it.getSourceShopId();
			
			default : return 0;
		}
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if(writable)
		{
			return (column==8 || column==5 || column==6);
		}
		else
		{
			return false;
		}
	}


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex==1 || columnIndex==9 || columnIndex==11)
			return Date.class;

		if(columnIndex==3)
			return Integer.class;

		
		if(columnIndex==4 || columnIndex==5 || columnIndex==6)
			return Double.class;

		if(columnIndex==8)
			return TransactionStatus.class;
		
		if(columnIndex==12)
			return TransactionDirection.class;
		
		
		
		return super.getColumnClass(columnIndex);
	}

}
