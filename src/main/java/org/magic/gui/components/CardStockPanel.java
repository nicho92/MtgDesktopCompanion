package org.magic.gui.components;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.models.CardStockTableModel;
import org.magic.gui.renderer.DoubleCellEditor;
import org.magic.gui.renderer.EnumConditionEditor;
import org.magic.gui.renderer.IntegerCellEditor;
import org.magic.gui.renderer.StockTableRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.extra.GraderServices;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.UITools;

public class CardStockPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
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
		table = new JXTable(model);
		StockTableRenderer render = new StockTableRenderer();

		table.setDefaultRenderer(Object.class, render);
		table.setDefaultRenderer(Boolean.class, render);
		table.setDefaultRenderer(Double.class, render);
		table.setDefaultEditor(EnumCondition.class, new EnumConditionEditor());
		table.setDefaultEditor(Integer.class, new IntegerCellEditor());
		table.getColumnModel().getColumn(12).setCellEditor(new DefaultCellEditor(UITools.createCombobox(GraderServices.inst().listGraders())));
		table.getColumnModel().getColumn(13).setCellEditor(new DoubleCellEditor());
		
		table.getColumnExt(model.getColumnName(1)).setVisible(false);
		table.getColumnExt(model.getColumnName(2)).setVisible(false);
		table.getColumnExt(model.getColumnName(3)).setVisible(false);

		
		
		JPanel panneauHaut = new JPanel();
		add(panneauHaut, BorderLayout.NORTH);
		add(new JScrollPane(table),BorderLayout.CENTER);
		btnAdd = new JButton(MTGConstants.ICON_NEW);
		btnDelete = new JButton(MTGConstants.ICON_DELETE);
		btnSave = new JButton(MTGConstants.ICON_SAVE);
		
		
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
		ThreadManager.getInstance().executeThread(() -> {
			for (MagicCardStock ms : model.getItems())
				if (ms.isUpdate())
					try {
						MTGControler.getInstance().getEnabled(MTGDao.class).saveOrUpdateStock(ms);
						ms.setUpdate(false);
						
					} catch (SQLException e1) {
						MTGControler.getInstance().notify(e1);
					}

			model.fireTableDataChanged();
		}, "Batch stock save");
	}

	private void delete() {
		List<MagicCardStock> st = UITools.getTableSelections(table, 0);
		
		model.removeItem(st);
		st.removeIf(s->s.getIdstock()==-1);
		
		if(!st.isEmpty())
		{
		try {
			MTGControler.getInstance().getEnabled(MTGDao.class).deleteStock(st);
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
		this.mc=mc;
		this.col=col;
		btnAdd.setEnabled(true);
		btnSave.setEnabled(true);
		btnDelete.setEnabled(true);

		try {
			model.init(MTGControler.getInstance().getEnabled(MTGDao.class).listStocks(mc, col,true));
			table.packAll();
		} catch (Exception e) {
			logger.error(e);
		}

	}

}
