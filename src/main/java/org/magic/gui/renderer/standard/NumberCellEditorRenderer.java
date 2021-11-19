package org.magic.gui.renderer.standard;

import java.awt.Component;
import java.text.NumberFormat;

import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang3.StringUtils;

public class NumberCellEditorRenderer extends AbstractCellEditor implements TableCellEditor, TableCellRenderer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JSpinner spinner;
	private NumberFormat formater;

	public NumberCellEditorRenderer() {
		spinner = new JSpinner();
		var model1 = new SpinnerNumberModel();
		model1.setMinimum(0);
		spinner.setModel(model1);
		formater =NumberFormat.getNumberInstance();
		formater.setGroupingUsed(false);
	}
	

	@Override
	public Object getCellEditorValue() {
		return spinner.getValue();
	}

	@Override
	public Component getTableCellEditorComponent(JTable arg0, Object value, boolean arg2, int arg3, int arg4) {
		spinner.setValue(value);
		return spinner;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		
		var text = String.valueOf(value);
	
		if(StringUtils.isNumeric(text)) 
		{
			text = formater.format(value);
		}
		
		
		var l= new JLabel(text,SwingConstants.CENTER);
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