package org.magic.gui.components;

import java.awt.BorderLayout;
import java.sql.SQLException;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;

import org.magic.api.beans.shop.Contact;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.tools.MTG;

public class ContactSelectionPanel extends MTGUIComponent {
	
	private static final long serialVersionUID = 1L;
	private JComboBox<Contact> cbo;


	@Override
	public String getTitle() {
		return "CONTACT_CHOOSE";
	}

	@Override
	public void onVisible() {
		
		cbo.removeAllItems();
		logger.info("List contactz");
		
		
	}
	
	
	
	public ContactSelectionPanel() {
		cbo = new JComboBox<>();
		cbo.setRenderer((JList<? extends Contact> list, Contact value, int index,boolean isSelected, boolean cellHasFocus)->{
				var l= new JLabel(MTGConstants.ICON_TAB_USER);
					l.setText(value.toString());
					l.setHorizontalAlignment(SwingConstants.LEFT);
				return l;
			
		});
		setLayout(new BorderLayout());
		
		this.add(cbo,BorderLayout.CENTER);
		
		try {
			for(Contact c : MTG.getEnabledPlugin(MTGDao.class).listContacts())
				cbo.addItem(c);
			
		} catch (SQLException e) {
			logger.error(e);
		}
		
		
	}
	
	public Contact getContact()
	{
		return (Contact) cbo.getSelectedItem();
	}



	public void setContact(Contact contact) {
		cbo.setSelectedItem(contact);
		
	}
	
	
	
}
