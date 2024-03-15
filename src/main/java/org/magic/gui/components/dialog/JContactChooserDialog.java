	package org.magic.gui.components.dialog;

import static org.magic.services.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.shop.Contact;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.models.ContactTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;
public class JContactChooserDialog extends JDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JXTable table;
	private Contact selectedContact;
	private AbstractBuzyIndicatorComponent buzy;


	public Contact getSelectedContacts() {
		return selectedContact;
	}



	public JContactChooserDialog() {
		setTitle(capitalize("CONTACT"));
		setIconImage(MTGConstants.ICON_USER.getImage());
		setSize(950, 600);

		var decksModel = new ContactTableModel();
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				selectedContact = null;
			}

			@Override
			public void windowClosing(WindowEvent e) {
				selectedContact = null;
			}
		});


		AbstractObservableWorker<List<Contact>, Contact, MTGDao> sw2 = new AbstractObservableWorker<>(buzy,MTG.getEnabledPlugin(MTGDao.class))
				{

					@Override
					protected List<Contact> doInBackground() throws Exception {
						return plug.listContacts();
					}
					@Override
					protected void process(List<Contact> chunks) {
						super.process(chunks);
						decksModel.addItems(chunks);
					}
					@Override
					protected void done() {
						super.done();

						table.packAll();
					}
				};

		ThreadManager.getInstance().runInEdt(sw2,"loading contacts");


		table = UITools.createNewTable(decksModel,true);

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {

				if(UITools.getTableSelections(table, 0).isEmpty())
					return;

				selectedContact = UITools.getTableSelection(table, 0);

				if (event.getClickCount() == 2) {
					dispose();
				}

			}

		});

		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

		var panelBas = new JPanel();
		getContentPane().add(panelBas, BorderLayout.SOUTH);

		var btnSelect = new JButton(MTGConstants.ICON_OPEN);
		btnSelect.setToolTipText(capitalize("OPEN"));
		btnSelect.addActionListener(e -> {
			if (selectedContact == null)
				MTGControler.getInstance().notify(new NullPointerException(capitalize("CHOOSE_CONTACT")));
			else
				dispose();
		});
		panelBas.add(btnSelect);

		var btnCancel = new JButton(MTGConstants.ICON_CANCEL);
		btnCancel.setToolTipText(capitalize("CANCEL"));
		btnCancel.addActionListener(e -> {
			selectedContact = null;
			dispose();
		});

		panelBas.add(btnCancel);
		panelBas.add(buzy);

		setLocationRelativeTo(null);
		setModal(true);

	}
}
