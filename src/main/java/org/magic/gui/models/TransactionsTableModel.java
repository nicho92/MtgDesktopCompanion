package org.magic.gui.models;

import java.sql.SQLException;
import java.util.Date;

import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.services.tools.MTG;

public class TransactionsTableModel extends GenericTableModel<Transaction> {


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	public TransactionsTableModel() {

		setWritable(true);
		columns = new String[] { "ID","DATE","CONTACT","ITEMS","TOTAL","SHIPPING","MESSAGE","STATUT","DATE PAYMENT","PAYMENT METHOD","DATE SEND" };
	}


	@Override
	public void setValueAt(Object aValue, int row, int column) {
		getItemAt(row).setStatut(TransactionStatus.valueOf(aValue.toString()));

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
			case 5 : return it.getShippingPrice();
			case 6 : return it.getMessage();
			case 7 : return it.getStatut();
			case 8 : return it.getDatePayment();
			case 9 : return it.getPaymentProvider();
			case 10 : return it.getDateSend();
			default : return 0;
		}
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if(writable)
		{
			return (column==7);
		}
		else
		{
			return false;
		}
	}


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex==1 || columnIndex==8 || columnIndex==10)
			return Date.class;

		if(columnIndex==4 || columnIndex==5)
			return Double.class;

		if(columnIndex==7)
			return TransactionStatus.class;

		return super.getColumnClass(columnIndex);
	}

}
