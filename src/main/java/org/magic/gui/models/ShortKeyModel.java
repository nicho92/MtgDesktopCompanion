package org.magic.gui.models;

import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.magic.gui.abstracts.GenericTableModel;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.ShortKeyManager;


public class ShortKeyModel extends GenericTableModel<JButton>
{
	private static final long serialVersionUID = 1L;
	private int mainObjectIndex =0;

	public ShortKeyModel() {
		columns = new String[] {"Module","Button","key"};
		items = ShortKeyManager.inst().getMapping();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {


		if(columnIndex == 0)
			return MTGUIComponent.class;


		if(columnIndex == 1)
			return JButton.class;

		return super.getColumnClass(columnIndex);
	}

	@Override
	public Object getValueAt(int row, int column) {
		switch (column)
		{
			case 0: return SwingUtilities.getAncestorOfClass(MTGUIComponent.class, items.get(row));
			case 1: return items.get(row);
			case 2: return KeyEvent.getKeyText(items.get(row).getMnemonic());
			default: throw new IllegalArgumentException("Unexpected value: " + column);
		}
	}

	public int getMainObjectIndex() {
		return mainObjectIndex ;
	}
	public void setMainObjectIndex(int mainObjectIndex) {
		this.mainObjectIndex = mainObjectIndex;
	}




}
