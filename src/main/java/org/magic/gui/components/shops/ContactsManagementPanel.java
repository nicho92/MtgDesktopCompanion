package org.magic.gui.components.shops;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.Contact;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.CardStockPanel;
import org.magic.gui.components.ContactPanel;
import org.magic.gui.components.ObjectViewerPanel;
import org.magic.gui.models.ContactTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.MTG;
import org.magic.tools.UITools;

import com.jogamp.newt.event.KeyEvent;

public class ContactsManagementPanel extends MTGUIComponent {
	private JXTable table;
	private ContactTableModel model;
	private ContactPanel contactPanel;
	private ObjectViewerPanel viewerPanel;
	
	public ContactsManagementPanel() {
		setLayout(new BorderLayout(0, 0));
		var panneauHaut = new JPanel();
		var stockDetailPanel = new CardStockPanel();
		var tabbedPane = new JTabbedPane();
		contactPanel = new ContactPanel(true);
		model = new ContactTableModel();
		viewerPanel = new ObjectViewerPanel();
		
		var btnRefresh = UITools.createBindableJButton("", MTGConstants.ICON_REFRESH,KeyEvent.VK_R,"reload");
		var btnNewContact = UITools.createBindableJButton("", MTGConstants.ICON_NEW, KeyEvent.VK_N, "NewContact");
		table = UITools.createNewTable(model);
		UITools.initTableFilter(table);

		UITools.addTab(tabbedPane, contactPanel);
		
		if(MTGControler.getInstance().get("debug-json-panel").equals("true"))
			UITools.addTab(tabbedPane, viewerPanel);
		
		table.packAll();
		stockDetailPanel.showAllColumns();
		
		add(new JScrollPane(table));
		add(panneauHaut, BorderLayout.NORTH);
		add(tabbedPane,BorderLayout.SOUTH);
		panneauHaut.add(btnRefresh);
		panneauHaut.add(btnNewContact);
		
		table.getSelectionModel().addListSelectionListener(lsl->{
			
			List<Contact> t = UITools.getTableSelections(table, 0);

			if(t.isEmpty())
				return;
			
			contactPanel.setContact(t.get(0));
			viewerPanel.show(t.get(0));
		});
		
		btnRefresh.addActionListener(al->reload());
		
		btnNewContact.addActionListener(al->{
			var c = new Contact();
			c.setName("New");
			c.setLastName("Contact");
			
			btnNewContact.setEnabled(false);
			SwingWorker<Integer, Void> sw = new SwingWorker<>()
					{

						@Override
						protected Integer doInBackground() throws Exception {
							return MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateContact(c);
						}

						@Override
						protected void done() {
							try {
								get();
								model.fireTableDataChanged();
							} catch (InterruptedException | ExecutionException e) {
								Thread.currentThread().interrupt();
								logger.error(e);
								MTGControler.getInstance().notify(e);
							}
							btnNewContact.setEnabled(true);
						}
					};
					
					ThreadManager.getInstance().runInEdt(sw,"create new contact");		
					
		});
		
		
	}
	
	private void reload()
	{
		try {
			model.clear();
			model.addItems(MTG.getEnabledPlugin(MTGDao.class).listContacts());
			model.fireTableDataChanged();
		} catch (Exception e) {
			logger.error("error loading transactions",e);
		}
	}

	@Override
	public void onFirstShowing() {
		reload();
		
	}
	
	@Override
	public String getTitle() {
		return "Contacts";
	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_USER;
	}

	
}
