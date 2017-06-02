package org.magic.game.gui.components.dialog;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.beanutils.BeanUtils;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.services.ThreadManager;

public class DescribeCardDialog extends JDialog {
	private JTable table;
	private DisplayableCardModel mod;
	
	public DescribeCardDialog(DisplayableCard c) {
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		mod = new DisplayableCardModel(c);
		table.setModel(mod);
		scrollPane.setViewportView(table);
		pack();
		
	}

}


class DisplayableCardModel extends DefaultTableModel
{
	Map<String,String> map;
	static final String[] columns = new String[]{"attributes","values"};
	
	public DisplayableCardModel(DisplayableCard c) {
		try {
			map = BeanUtils.describe(c);
		} catch (Exception e) {}
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		if(column==0)
			return map.keySet().toArray()[row];
		else
			return map.values().toArray()[row];
	}
	
	@Override
	public String getColumnName(int column) {
		return columns[column];
	}
	
	@Override
	public int getRowCount() {
		if(map==null)
			return 0;
		
		return map.size();
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	@Override
	public int getColumnCount() {
		return 2;
	}
}
