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
	private JLabel lab;

	public DoubleCellEditorRenderer() {
		this(false);
	}

	public DoubleCellEditorRenderer(boolean enabledArrow)
	{
		this.enableArrow=enabledArrow;
		fmtTxtField = new JFormattedTextField(format);
		lab=new JLabel();
		lab.setOpaque(true);
		lab.setHorizontalAlignment(SwingConstants.CENTER);
	}

	public DoubleCellEditorRenderer(boolean enablePercent, boolean enabledArrow)
	{
		if(enablePercent)
			this.format=NumberFormat.getPercentInstance();

		lab = new JLabel();
		lab.setOpaque(true);
		lab.setHorizontalAlignment(SwingConstants.CENTER);

		this.enableArrow=enabledArrow;
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

		Double val=-1.0 ;


		if(value==null)
			return lab;

		if(value instanceof Long l)
			val = l.doubleValue();
		else if(value instanceof Double l)
			val = l;


		lab.setText(format.format(val));
		
		if(isSelected)
		{
			lab.setBackground(table.getSelectionBackground());
			lab.setForeground(table.getSelectionForeground());
		}
		else
		{
			lab.setBackground(table.getBackground());
			lab.setForeground(table.getForeground());

		}

		if(enableArrow) {
			lab.setHorizontalTextPosition(SwingConstants.LEFT);
			if (val > 0)
				lab.setIcon(MTGConstants.ICON_UP);

			if (val < 0)
				lab.setIcon(MTGConstants.ICON_DOWN);

			if (val == 0)
				lab.setIcon(MTGConstants.ICON_STANDBY);

		}

		return lab;
	}


}
