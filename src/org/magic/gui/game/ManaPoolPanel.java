package org.magic.gui.game;


import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ManaPoolPanel extends JScrollPane{
	private JTable table;
	
	public ManaPoolPanel() {
		table = new JTable(new ManaPoolTableModel());
		setViewportView(table);
	}

}


class ManaPoolTableModel extends DefaultTableModel
{
	
	String[] columns = new String[] {"Mana","Count"};
	
	@Override
	public int getColumnCount() {
		return columns.length;
	}
	
	@Override
	public String getColumnName(int column) {
		return columns[column];
	}
	
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return super.getRowCount();
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		// TODO Auto-generated method stub
		return super.getValueAt(row, column);
	}
}
