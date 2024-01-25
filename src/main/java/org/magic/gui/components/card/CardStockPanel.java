package org.magic.gui.components.card;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.CardStockTableModel;
import org.magic.gui.renderer.StockTableRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;


public class CardStockPanel extends MTGUIComponent {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JXTable table;
	private CardStockTableModel model;
	private JButton btnAdd;
	private JButton btnDelete;
	private JButton btnSave;
	private MTGCard mc;
	private MTGCollection col;
	private JPanel panneauHaut;

	public void enabledAdd(boolean b) {
		btnAdd.setEnabled(b);
	}

	public void showAllColumns()
	{
		for(var i=0;i<model.getColumnCount();i++)
			table.getColumnExt(model.getColumnName(i)).setVisible(true);
	}

	public CardStockPanel() {
		setLayout(new BorderLayout(0, 0));
		model = new CardStockTableModel();
		table = UITools.createNewTable(model);

		UITools.setDefaultRenderer(table, new StockTableRenderer());

		for(int i : model.defaultHiddenColumns())
		{
			table.getColumnExt(model.getColumnName(i)).setVisible(false);
		}

		panneauHaut = new JPanel();
		add(panneauHaut, BorderLayout.NORTH);
		add(new JScrollPane(table),BorderLayout.CENTER);
		btnAdd = UITools.createBindableJButton(null, MTGConstants.ICON_NEW, KeyEvent.VK_ADD, "newStock");
		btnDelete = UITools.createBindableJButton(null, MTGConstants.ICON_DELETE, KeyEvent.VK_MINUS, "delete");
		btnSave = UITools.createBindableJButton(null, MTGConstants.ICON_SAVE, KeyEvent.VK_S, "save");

		btnAdd.setEnabled(false);
		btnSave.setEnabled(false);
		btnDelete.setEnabled(false);

		btnAdd.addActionListener(ae -> addLine());
		btnSave.addActionListener(ae -> save());
		btnDelete.addActionListener(ae -> delete());



		panneauHaut.add(btnAdd);
		panneauHaut.add(btnDelete);
		panneauHaut.add(btnSave);

	}

	private void save() {

		SwingWorker<Void, Void> sw = new SwingWorker<>()
		{
			@Override
			protected Void doInBackground() throws Exception {
				for (MTGCardStock ms : model.getItems())
					if (ms.isUpdated())
						try {
							getEnabledPlugin(MTGDao.class).saveOrUpdateCardStock(ms);
							ms.setUpdated(false);

						} catch (SQLException e1) {
							MTGControler.getInstance().notify(e1);
						}

				return null;
			}

			@Override
			protected void done() {
				model.fireTableDataChanged();
			}



		};
		ThreadManager.getInstance().runInEdt(sw, "batch stock saving");
	}

	private void delete() {
		List<MTGCardStock> st = UITools.getTableSelections(table, 0);

		model.removeItem(st);
		st.removeIf(s->s.getId()==-1);

		if(!st.isEmpty())
		{
		try {
			getEnabledPlugin(MTGDao.class).deleteStock(st);
		} catch (SQLException e) {
			logger.error(e);
		}
		}
	}

	@Override
	public void onHide() {
		boolean isUpdatedModel = model.getItems().stream().anyMatch(MTGCardStock::isUpdated);

		if(isUpdatedModel)
		{
			MTGControler.getInstance().notify(new MTGNotification("Item Updated", "Don't forget to save your updates", MESSAGE_TYPE.WARNING));
		}

	}

	public void addLine()
	{
		try {
			MTGCardStock st = MTGControler.getInstance().getDefaultStock();
			st.setProduct(mc);

			if(col!=null)
				st.setMagicCollection(col);
			else
				st.setMagicCollection(new MTGCollection(MTGControler.getInstance().get("default-library")));

			st.setUpdated(true);
			model.addItem(st);
			model.fireTableDataChanged();
		} catch (Exception e) {
			logger.error(e);
		}

	}

	public void init(MTGCard mc, MTGCollection col) {

		if(mc==null)
			return;

		this.mc=mc;
		this.col=col;
		btnAdd.setEnabled(true);
		btnSave.setEnabled(true);
		btnDelete.setEnabled(true);

		if(isVisible())
		{
			onVisible();
		}

	}


	@Override
	public void onVisible() {
		
		if(mc==null)
			return;
		
		var sw = new SwingWorker<List<MTGCardStock> , Void>()
		{
			@Override
			protected List<MTGCardStock> doInBackground() throws Exception {
				if(col==null)
					return getEnabledPlugin(MTGDao.class).listStocks(mc);
				else
					return getEnabledPlugin(MTGDao.class).listStocks(mc, col,true);
			}


			@Override
			protected void done() {
				try {
					model.init(get());
					table.packAll();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					logger.error(e);
				}

			}

		};

		ThreadManager.getInstance().runInEdt(sw, "load stock");
	}



	public void disableCommands() {

		btnAdd.setEnabled(false);
		btnSave.setEnabled(false);
		btnDelete.setEnabled(false);
		panneauHaut.setVisible(false);


	}



	public void initMagicCardStock(List<MTGCardStock> st) {

		btnAdd.setEnabled(true);
		btnSave.setEnabled(true);
		btnDelete.setEnabled(true);

		try {
			model.init(st);
			table.packAll();
		} catch (Exception e) {
			logger.error(e);
		}

	}




	@Override
	public String getTitle() {
		return "Stock";
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_STOCK;
	}


}
