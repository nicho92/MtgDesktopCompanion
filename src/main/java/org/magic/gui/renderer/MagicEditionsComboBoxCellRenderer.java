package org.magic.gui.renderer;

import java.awt.Component;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.magic.api.beans.MTGEdition;
import org.magic.gui.renderer.MagicEditionIconListRenderer.SIZE;
import org.magic.services.tools.UITools;

public class MagicEditionsComboBoxCellRenderer implements TableCellRenderer {

	private boolean enable;

	public MagicEditionsComboBoxCellRenderer() {
		enable=true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {

		if(value==null)
			return new JLabel();


		JComboBox<MTGEdition> cbo = UITools.createComboboxEditions((List<MTGEdition>) value,SIZE.SMALL);
		cbo.setEnabled(enable);
		cbo.setOpaque(true);

		return cbo;
	}
}
