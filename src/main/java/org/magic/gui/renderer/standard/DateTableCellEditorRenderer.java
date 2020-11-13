package org.magic.gui.renderer.standard;

import java.awt.Component;
import java.util.Date;

import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.JXDatePicker;
import org.magic.tools.UITools;

public class DateTableCellEditorRenderer extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
	
	private static final long serialVersionUID = 1L;
	private JXDatePicker picker;

	@Override
	public Object getCellEditorValue() {
		
		return picker.getDate();
	}
	
	public DateTableCellEditorRenderer() {
		picker = new JXDatePicker();
	}
	

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		JLabel l = new JLabel(UITools.formatDateTime((Date)value));
		l.setOpaque(true);
		if(isSelected)
		{
			l.setBackground(table.getSelectionBackground());
			l.setForeground(table.getSelectionForeground());
		}
		else
		{
			l.setBackground(table.getBackground());
			l.setForeground(table.getForeground());
			
		}
		
		return l;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		picker.setDate((Date)value);
		
		return picker;
	}

}
