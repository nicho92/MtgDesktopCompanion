package org.magic.gui.renderer.standard;

import java.awt.Component;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.AbstractCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.magic.services.MTGConstants;

public class DoubleCellEditorRenderer extends AbstractCellEditor implements TableCellEditor, TableCellRenderer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFormattedTextField fmtTxtField;
	private NumberFormat format = new DecimalFormat();
	private boolean enableArrow;
	
	
	public DoubleCellEditorRenderer() {
		fmtTxtField = new JFormattedTextField(format);
		enableArrow=false;
	}
	
	public DoubleCellEditorRenderer(boolean enabledArrow)
	{
		this.enableArrow=enabledArrow;
		fmtTxtField = new JFormattedTextField(format);
	}
	
	public DoubleCellEditorRenderer(boolean enablePercent, boolean enabledArrow)
	{
		this.enableArrow=enabledArrow;
		if(enablePercent)
			this.format=NumberFormat.getPercentInstance();

		fmtTxtField = new JFormattedTextField(format);
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
		
		
		format.setMaximumFractionDigits(2);
		format.setMaximumFractionDigits(2);
		Double val ;
		
		
		if(value==null)
			return new JLabel();
		
		
		if(value instanceof Long l)
			val = l.doubleValue();
		else
			val = (Double)value;
		

		var l= new JLabel(format.format(val),SwingConstants.CENTER);
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
		
		if(enableArrow) {
			l.setHorizontalTextPosition(SwingConstants.LEFT);
			if (((Double) value).doubleValue() > 0)
			{
				l.setIcon(MTGConstants.ICON_UP);
			}
	
			if (((Double) value).doubleValue() < 0)
			{
				l.setIcon(MTGConstants.ICON_DOWN);
				
			}
	
			if (((Double) value).doubleValue() == 0)
			{
				l.setIcon(MTGConstants.ICON_STANDBY);
			}
			
		}
		
		return l;
	}

	
}