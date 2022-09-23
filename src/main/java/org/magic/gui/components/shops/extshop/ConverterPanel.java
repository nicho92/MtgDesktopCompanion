package org.magic.gui.components.shops.extshop;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.technical.ConverterItem;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.ConverterItemsTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.MTG;
import org.magic.tools.UITools;

public class ConverterPanel extends MTGUIComponent{

	private static final long serialVersionUID = 1L;
	private ConverterItemsTableModel model;
	private JXTable table;



	@Override
	public void onFirstShowing() {

		try {
			model.init(MTG.getEnabledPlugin(MTGDao.class).listConversionItems());
		} catch (SQLException e1) {
			MTGControler.getInstance().notify(e1);
		}

		UITools.initTableFilter(table);
		table.packAll();
	}

	public ConverterPanel() {
		setLayout(new BorderLayout(0, 0));

		var panel = new JPanel();
		var btnReload = UITools.createBindableJButton("", MTGConstants.ICON_REFRESH, KeyEvent.VK_R, "Reload");
		var btnSave = UITools.createBindableJButton("", MTGConstants.ICON_SAVE, KeyEvent.VK_S, "Save");
		var btnAdd = UITools.createBindableJButton("", MTGConstants.ICON_NEW, KeyEvent.VK_N, "New");
		var btnDelete = UITools.createBindableJButton("", MTGConstants.ICON_DELETE, KeyEvent.VK_D, "Delete");


		model = new ConverterItemsTableModel();
		table = UITools.createNewTable(model);



		panel.add(btnReload);
		panel.add(btnAdd);
		panel.add(btnDelete);
		panel.add(btnSave);

		add(panel, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);

		btnReload.addActionListener(el->{
			try {
				model.init(MTG.getEnabledPlugin(MTGDao.class).listConversionItems());
			} catch (SQLException e1) {
				MTGControler.getInstance().notify(e1);
			}
		});



		btnDelete.addActionListener(el->{
			List<ConverterItem> its = UITools.getTableSelections(table, 0);

			var sw = new SwingWorker<Void, ConverterItem>()
					{

						@Override
						protected Void doInBackground() throws Exception {
							for(ConverterItem it : its)
							{
								MTG.getEnabledPlugin(MTGDao.class).deleteConversionItem(it);
								publish(it);
							}
							return null;
						}

						@Override
						protected void process(List<ConverterItem> chunks) {
							for(ConverterItem it : chunks)
								model.removeItem(it);

							model.fireTableDataChanged();
						}




					};


			ThreadManager.getInstance().runInEdt(sw,"deleting converter");


		});


		btnAdd.addActionListener(el->model.addItem(new ConverterItem()));

		btnSave.addActionListener(el->{
				for(ConverterItem it : model.getItems())
				{
					try {
						MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateConversionItem(it);
					} catch (SQLException e) {
						MTGControler.getInstance().notify(e);
					}
				}
		});

	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_SYNC;
	}


	@Override
	public String getTitle() {
		return "Conversions";
	}

}
