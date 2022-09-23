package org.magic.gui.components.shops;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.MapTableModel;
import org.magic.services.MTGConstants;
import org.magic.tools.UITools;
public class StockItemsSynchronizationPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JXTable table;
	private MapTableModel<String, String> model;
	private MTGStockItem st;
	private JComboBox<MTGCardsExport> cboPlugins;


	public StockItemsSynchronizationPanel() {
		setLayout(new BorderLayout(0, 0));
		model = new MapTableModel<>();
		table = UITools.createNewTable(model);


		add(new JScrollPane(table));

		var panel = new JPanel();
		add(panel, BorderLayout.NORTH);

		var btnDelete = UITools.createBindableJButton(null, MTGConstants.ICON_DELETE, KeyEvent.VK_DELETE, "Delete Sync");
		var btnAdd = UITools.createBindableJButton(null, MTGConstants.ICON_NEW, KeyEvent.VK_N, "Add Sync");
		cboPlugins = UITools.createCombobox(MTGCardsExport.class,false);
		panel.add(btnDelete);

		panel.add(cboPlugins);
		panel.add(btnAdd);

		btnDelete.addActionListener(al->{

			String name = UITools.getTableSelection(table,0);

			if(name!=null) {
				st.getTiersAppIds().remove(name);
				st.setUpdated(true);
				init();
			}
		});

		btnAdd.addActionListener(al->{
			st.getTiersAppIds().put(cboPlugins.getSelectedItem().toString(), "-1");
			st.setUpdated(true);
			init();
		});

		model.setWritable(true);
		model.setColumnNames("Plugin", "id");


	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_SYNC;
	}


	@Override
	public String getTitle() {
		return "Synchronization";
	}

	public void init(MTGStockItem st)
	{
		this.st=st;
		init();
	}


	public void init()
	{
		model.clear();
		model.init(st.getTiersAppIds());
		model.fireTableDataChanged();
	}


}
