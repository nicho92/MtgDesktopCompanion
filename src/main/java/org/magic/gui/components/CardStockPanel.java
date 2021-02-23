package org.magic.gui.components;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.CardStockTableModel;
import org.magic.gui.renderer.StockTableRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.UITools;


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
	private MagicCard mc;
	private MagicCollection col;

	public void enabledAdd(boolean b) {
		btnAdd.setEnabled(b);
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
		
		JPanel panneauHaut = new JPanel();
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
				for (MagicCardStock ms : model.getItems())
					if (ms.isUpdate())
						try {
							getEnabledPlugin(MTGDao.class).saveOrUpdateStock(ms);
							ms.setUpdate(false);
							
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
		List<MagicCardStock> st = UITools.getTableSelections(table, 0);
		
		model.removeItem(st);
		st.removeIf(s->s.getIdstock()==-1);
		
		if(!st.isEmpty())
		{
		try {
			getEnabledPlugin(MTGDao.class).deleteStock(st);
		} catch (SQLException e) {
			logger.error(e);
		}
		}
	}

	public void addLine()
	{
		try {
			MagicCardStock st = MTGControler.getInstance().getDefaultStock();
			st.setMagicCard(mc);
			st.setMagicCollection(col);
			st.setUpdate(true);
			model.addItem(st);
		} catch (Exception e) {
			logger.error(e);
		}

	}
	
	public void initMagicCardStock(MagicCard mc, MagicCollection col) {
		
		if(mc==null)
			return;
		
		this.mc=mc;
		this.col=col;
		btnAdd.setEnabled(true);
		btnSave.setEnabled(true);
		btnDelete.setEnabled(true);

		try {
			
			if(col==null)
				model.init(getEnabledPlugin(MTGDao.class).listStocks(mc));
			else
				model.init(getEnabledPlugin(MTGDao.class).listStocks(mc, col,true));
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
