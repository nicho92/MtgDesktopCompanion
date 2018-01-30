package org.magic.gui.renderer;

import java.awt.Component;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.magic.api.beans.MagicEdition;

public class MagicEditionRenderer extends DefaultTableCellRenderer {
	JComboBox<MagicEdition> cbo;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		
		List<MagicEdition> e = (List)value;
		cbo = new JComboBox<>(e.toArray(new MagicEdition[e.size()]));
		return cbo;
	}
}
