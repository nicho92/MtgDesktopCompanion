package org.magic.gui.models;

import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.magic.gui.abstracts.GenericTableModel;
import org.magic.gui.abstracts.MTGUIComponent;


public class ShortKeyModel extends GenericTableModel<JButton>
{
	private static final long serialVersionUID = 1L;

	public ShortKeyModel() {
		columns = new String[] {"Button","Module","key"};
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		if(columnIndex == 0)
			return JButton.class;
		
		return super.getColumnClass(columnIndex);
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		switch (column) 
		{
			case 0: return items.get(row);
			case 1: return SwingUtilities.getAncestorOfClass(MTGUIComponent.class, items.get(row));
			case 2: return KeyEvent.getKeyText(items.get(row).getMnemonic());
			default: throw new IllegalArgumentException("Unexpected value: " + column);
		}
	}
	
	
	
	
}
