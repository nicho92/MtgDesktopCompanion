package org.magic.gui.components.shops;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.shop.Contact;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.CardStockPanel;
import org.magic.gui.components.ContactPanel;
import org.magic.gui.components.ObjectViewerPanel;
import org.magic.gui.models.ContactTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.TransactionService;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;

import com.jogamp.newt.event.KeyEvent;

public class ContactsManagementPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JXTable table;
	private ContactTableModel model;
	private ContactPanel contactPanel;
	private ObjectViewerPanel viewerPanel;
	private AbstractBuzyIndicatorComponent buzy;

	public ContactsManagementPanel() {


		setLayout(new BorderLayout(0, 0));
		var panneauHaut = new JPanel();
		var stockDetailPanel = new CardStockPanel();
		var tabbedPane = new JTabbedPane();
		contactPanel = new ContactPanel(true);
		model = new ContactTableModel();
		viewerPanel = new ObjectViewerPanel();
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		var btnRefresh = UITools.createBindableJButton("", MTGConstants.ICON_REFRESH,KeyEvent.VK_R,"reload");
		var btnNewContact = UITools.createBindableJButton("", MTGConstants.ICON_NEW, KeyEvent.VK_N, "NewContact");
		var btnDeleteContact = UITools.createBindableJButton("", MTGConstants.ICON_DELETE, KeyEvent.VK_DELETE, "DeleteContact");


		table = UITools.createNewTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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
		panneauHaut.add(btnDeleteContact);
		panneauHaut.add(buzy);

		table.getSelectionModel().addListSelectionListener(lsl->{

			Contact t = UITools.getTableSelection(table, 0);

			if(t==null)
				return;

			contactPanel.setContact(t);
			viewerPanel.show(t);

		});

		btnRefresh.addActionListener(al->reload());


		btnDeleteContact.addActionListener(al->{
			try {
					TransactionService.deleteContact(contactPanel.getContact());
					reload();
			} catch (Exception e) {
				MTGControler.getInstance().notify(e);
			}



		});


		btnNewContact.addActionListener(al->{
			var c = new Contact();
			c.setName("New");
			c.setLastName("Contact");

			btnNewContact.setEnabled(false);
			SwingWorker<Integer, Void> sw = new SwingWorker<>()
					{

						@Override
						protected Integer doInBackground() throws Exception {
							return TransactionService.saveOrUpdateContact(c);
						}

						@Override
						protected void done() {
							try {
								get();
								reload();
							}
							catch (InterruptedException e)
							{
								Thread.currentThread().interrupt();

							}
							catch (Exception e)
							{
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
		buzy.start();
		model.clear();
		var sw = new SwingWorker<List<Contact>, Void>(){

			@Override
			protected List<Contact> doInBackground() throws Exception {
				return TransactionService.listContacts();
			}

			@Override
			protected void done() {
				try {
					model.addItems(get());
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					logger.error(e);
				}
				buzy.end();
				model.fireTableDataChanged();
			}


		};

		ThreadManager.getInstance().runInEdt(sw, "Load contacts");

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
