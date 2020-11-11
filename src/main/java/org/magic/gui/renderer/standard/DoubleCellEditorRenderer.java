package org.magic.gui.renderer.standard;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.AbstractCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class DoubleCellEditorRenderer extends AbstractCellEditor implements TableCellEditor, TableCellRenderer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFormattedTextField fmtTxtField;
	private String format="#0.0";
	
	public DoubleCellEditorRenderer(String f) {
		this.format=f;
		fmtTxtField = new JFormattedTextField(new DecimalFormat (f));
	}
	
	
	public DoubleCellEditorRenderer() {
		fmtTxtField = new JFormattedTextField(new DecimalFormat (format));
	}

	@Override
	public Object getCellEditorValue() {
		return fmtTxtField.getValue();
	}

	@Override
	public Component getTableCellEditorComponent(JTable arg0, Object value, boolean arg2, int arg3, int arg4) {
		fmtTxtField.setValue(value);
		return fmtTxtField;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		JLabel l= new JLabel(new DecimalFormat(format).format((double) value));
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

}