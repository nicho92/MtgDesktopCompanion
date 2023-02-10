package org.magic.gui.components;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.magic.api.beans.technical.GedEntry;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGGedStorage;
import org.magic.api.interfaces.MTGSerializable;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.GedEntryTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;

public class GedBrowserPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JTable table;
	private JComboBox<MTGGedStorage> cboGed;
	private GedEntryTableModel model;
	private AbstractBuzyIndicatorComponent buzy;
	private JButton btnDelete;


	private transient AbstractObservableWorker<List<GedEntry<MTGSerializable>>, GedEntry<MTGSerializable>, MTGGedStorage> sw;

	public GedBrowserPanel() {
		setLayout(new BorderLayout(0, 0));
		model = new GedEntryTableModel();
		cboGed = UITools.createCombobox(MTGGedStorage.class,true);
		var panneauHaut = new JPanel();
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		cboGed.setSelectedItem(MTG.getEnabledPlugin(MTGGedStorage.class));
		cboGed.addItemListener(il->reload());
		btnDelete = new JButton(MTGConstants.ICON_DELETE);
		btnDelete.setEnabled(false);
		table = UITools.createNewTable(model);
		UITools.initTableFilter(table);


		table.setDefaultRenderer(Long.class, (JTable t, Object value, boolean isSelected, boolean hasFocus,int row, int column)->{
				var lab = new DefaultTableCellRenderer();
				lab.setText(UITools.humanReadableSize((Long)value));
				return lab;
		});

		panneauHaut.add(cboGed);
		panneauHaut.add(btnDelete);
		panneauHaut.add(buzy);
		add(panneauHaut, BorderLayout.NORTH);
		add(new JScrollPane(table),BorderLayout.CENTER);


		table.getSelectionModel().addListSelectionListener(lsl->btnDelete.setEnabled(UITools.getTableSelection(table, 0)!=null));

		btnDelete.addActionListener(al->{
			GedEntry<MTGSerializable> select = UITools.getTableSelection(table, 0);
			var confirm = JOptionPane.showConfirmDialog(this, MTG.capitalize("CONFIRM_DELETE",select));
			if(confirm==JOptionPane.YES_OPTION)
			{
				try {
					MTG.getEnabledPlugin(MTGDao.class).deleteEntry(select);
					model.removeItem(select);
					btnDelete.setEnabled(false);
				} catch (Exception e) {
					MTGControler.getInstance().notify(e);
				}
			}



		});



	}

	@Override
	public void onFirstShowing() {
		reload();
	}

	private void reload() {

		if(sw!=null && !sw.isDone())
			sw.cancel(true);


		sw = new AbstractObservableWorker<>(buzy,(MTGGedStorage)cboGed.getSelectedItem()) {
					@Override
					protected List<GedEntry<MTGSerializable>> doInBackground() throws Exception {
						return plug.listAll();
					}

					@Override
					protected void notifyEnd() {
						model.bind(getResult());
					}

				};

				ThreadManager.getInstance().runInEdt(sw, "Loading Geds files");

	}

	@Override
	public String getTitle() {
		return "GED";
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_GED;
	}



}

