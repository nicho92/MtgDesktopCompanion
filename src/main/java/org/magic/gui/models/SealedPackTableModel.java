package org.magic.gui.models;

import java.util.List;
import java.util.Map.Entry;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.SealedPack;
import org.magic.services.MTGControler;

public class SealedPackTableModel extends DefaultTableModel{
	private SealedPack pack;
	
	private static final String[] COLUMNS = {MTGControler.getInstance().getLangService().getCapitalize("EDITION"),
											 MTGControler.getInstance().getLangService().getCapitalize("QTY")};
	
	
	
	public SealedPack getSealedPack()
	{
		return pack;
	}

	
	public void add(MagicEdition ed, Integer qty)
	{
		pack.set(ed, qty);
		fireTableDataChanged();
	}
	
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0: return MagicEdition.class;
		case 1: return Integer.class;
		default : return super.getColumnClass(columnIndex);
		}
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		MagicEdition ed = pack.listEditions().get(row);
		switch (column) {
		case 0: return ed;
		case 1: return pack.getQty(ed);
		default : return "";
		}
	}
	
	
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		MagicEdition ed = pack.listEditions().get(row);
		switch (column) {
			case 0: pack.set((MagicEdition)aValue, 0);break;
			case 1: 
				if(Integer.parseInt(aValue.toString())>0)
					pack.set(ed, Integer.parseInt(aValue.toString()));
					else
					{
						pack.remove(ed);
						fireTableDataChanged();
					}
			break;
		}
	}
	
	public void clear() {
		pack.clear();
		
	}

	@Override
	public String getColumnName(int column) {
		return COLUMNS[column];
	}
	
	public void addLine(MagicEdition ed, Integer qty)
	{
		pack.set(ed, qty);
		fireTableDataChanged();
	}
	
	public SealedPackTableModel() {
		pack=new SealedPack();
	}
	
	public void init(SealedPack lines)
	{
		this.pack=lines;
		fireTableDataChanged();
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return column==1;
	}
	
	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}
	
	@Override
	public int getRowCount() {
		if(pack==null)
			return 0;
		else
			return pack.size();
	}
	
}
