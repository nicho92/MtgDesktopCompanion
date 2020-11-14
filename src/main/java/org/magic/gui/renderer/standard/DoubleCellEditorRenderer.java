package org.magic.gui.renderer.standard;

import java.awt.Component;
import java.text.DecimalFormat;

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
	private String format="#0.00";
	private boolean enableArrow;
	
	
	public DoubleCellEditorRenderer() {
		fmtTxtField = new JFormattedTextField(new DecimalFormat (format));
		enableArrow=false;
	}
	
	public DoubleCellEditorRenderer(String format)
	{
		this.enableArrow=false;
		this.format=format;
		fmtTxtField = new JFormattedTextField(new DecimalFormat (format));
	}
	
	public DoubleCellEditorRenderer(boolean enabledArrow)
	{
		this.enableArrow=enabledArrow;
		fmtTxtField = new JFormattedTextField(new DecimalFormat (format));
	}
	
	public DoubleCellEditorRenderer(String format, boolean enabledArrow)
	{
		this.enableArrow=enabledArrow;
		this.format=format;
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
		
		
		double val = (double) value;

		JLabel l= new JLabel(new DecimalFormat(format).format(val),SwingConstants.RIGHT);
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
				l.setIcon(null);
			}
			
		}
		
		return l;
	}

}