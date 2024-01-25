package org.magic.gui.models;

import java.util.Date;

import org.magic.api.beans.MTGAnnounce;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.EnumTransactionDirection;
import org.magic.api.beans.shop.Contact;
import org.magic.gui.abstracts.GenericTableModel;

public class AnnouncesTableModel extends GenericTableModel<MTGAnnounce> {

	private static final long serialVersionUID = 1L;


	public AnnouncesTableModel() {
		setColumns("id","type","categorie","title","totalPrice","contact","startDate","endDate","status");
	}


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex)
		{
		case 0 : return MTGAnnounce.class;
		case 1 : return EnumTransactionDirection.class;
		case 2 : return EnumItems.class;
		case 4 : return Double.class;
		case 5 : return Contact.class;
		case 6|7 : return Date.class;
		case 8 : return MTGAnnounce.STATUS.class;
		default : return super.getColumnClass(columnIndex);

		}
	}

	@Override
	public Object getValueAt(int row, int column) {
		switch (column)
		{
		case 0 : return items.get(row);
		case 1 : return items.get(row).getType();
		case 2 : return items.get(row).getCategorie();
		case 3 : return items.get(row).getTitle();
		case 4 : return items.get(row).getTotalPrice();
		case 5 : return items.get(row).getContact();
		case 6 : return items.get(row).getStartDate();
		case 7 : return items.get(row).getEndDate();
		case 8 : return items.get(row).getStatus();
		default : return super.getValueAt(row, column);

		}
	}


}
