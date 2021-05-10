package org.magic.gui.models;

import java.sql.SQLException;
import java.util.Date;

import org.magic.api.beans.Transaction;
import org.magic.api.beans.Transaction.STAT;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.tools.MTG;

public class TransactionsModel extends GenericTableModel<Transaction> {

	
	public TransactionsModel() {
		
		setWritable(true);
		columns = new String[] { "ID","DATEPROPOSITION","CONTACT","ITEMS","TOTAL","STATUT" };
	}
	
	
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		getItemAt(row).setStatut(STAT.valueOf(aValue.toString()));
		
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
			case 1 : return it.getDateProposition();
			case 2 : return it.getContact();
			case 3 : return it.getItems().size();
			case 4 : return it.total();
			case 5 : return it.getStatut();
			default : return 0;
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		if(writable)
		{
			return (column==5);
		}
		else
		{
			return false;
		}
	}
	
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex==1)
			return Date.class;
		
		if(columnIndex==4)
			return Double.class;
		
		if(columnIndex==5)
			return STAT.class;
		
		return super.getColumnClass(columnIndex);
	}
	
}
