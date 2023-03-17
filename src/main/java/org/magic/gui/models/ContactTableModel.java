package org.magic.gui.models;

import org.magic.api.beans.shop.Contact;
import org.magic.gui.abstracts.GenericTableModel;

public class ContactTableModel extends GenericTableModel<Contact> {


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	public ContactTableModel() {
		setWritable(false);
		columns = new String[] { "ID","FORENAME","LASTNAME","ADDRESS","ZIP","CITY","COUNTRY","EMAIL","TELEPHONE","ACTIVE","EMAIL_ACCEPT" };
	}



	@Override
	public Object getValueAt(int row, int column) {

		Contact it = items.get(row);

		switch (column)
		{
			case 0 : return it;
			case 1 : return it.getName();
			case 2 : return it.getLastName();
			case 3 : return it.getAddress();
			case 4 : return it.getZipCode();
			case 5 : return it.getCity();
			case 6 : return it.getCountry();
			case 7 : return it.getEmail();
			case 8 : return it.getTelephone();
			case 9 : return it.isActive();
			case 10 : return it.isEmailAccept();
			default : return 0;
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex>=9)
			return Boolean.class;

		return super.getColumnClass(columnIndex);
	}

}
