	package org.magic.gui.components.dialog.importer;

import static org.magic.services.tools.MTG.capitalize;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.shop.Contact;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.AbstractDelegatedImporterDialog;
import org.magic.gui.models.ContactTableModel;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;
public class JContactChooserDialog extends AbstractDelegatedImporterDialog<Contact> {

	private static final long serialVersionUID = 1L;
	private AbstractBuzyIndicatorComponent buzy;
	private ContactTableModel decksModel;
	private JXTable table;
	
	
	public JContactChooserDialog() {
		setTitle(capitalize("CONTACT"));
		setSize(950, 600);

		decksModel = new ContactTableModel();
		
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		
		commandePanel.add(buzy);
		
		var sw2 = new AbstractObservableWorker<List<Contact>, Contact, MTGDao>(buzy,MTG.getEnabledPlugin(MTGDao.class))
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
	}
	
	@Override
	public JComponent getSelectComponent() {
		
		table = UITools.createNewTable(decksModel,true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if(UITools.getTableSelections(table, 0).isEmpty())
					return;

				setSelectedItem(UITools.getTableSelections(table, 0));

				if (event.getClickCount() == 2) {
					dispose();
				}
			}
		});
		return new JScrollPane(table);
	}
	
}
