package org.magic.gui.components.shops;

import java.awt.BorderLayout;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.magic.api.beans.shop.Contact;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;

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
	}



	public ContactSelectionPanel() {
		cbo = new JComboBox<>();
		cbo.setRenderer((JList<? extends Contact> _, Contact value, int _,boolean _, boolean _)->{
				var l= new JLabel(MTGConstants.ICON_TAB_USER);
					l.setText(value.getName() + " "+ value.getLastName());
					l.setHorizontalAlignment(SwingConstants.LEFT);
				return l;

		});
		setLayout(new BorderLayout());

		this.add(cbo,BorderLayout.CENTER);
		
		var sw = new SwingWorker<List<Contact>, Void>() {
			@Override
			protected List<Contact> doInBackground() throws Exception {
				return MTG.getEnabledPlugin(MTGDao.class).listContacts();
			}
			
			@Override
			protected void done() {
				
				try {
					get().stream().forEach(cbo::addItem);
				} catch (InterruptedException _) {
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					logger.error(e);
				}
			}
			
		};
		
		ThreadManager.getInstance().runInEdt(sw, "Loading contacts choose");
	}
	
	public Contact getContact()
	{
		return (Contact) cbo.getSelectedItem();
	}



	public void setContact(Contact contact) {
		cbo.setSelectedItem(contact);

	}



}
