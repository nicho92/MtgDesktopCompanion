package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableCellEditor;

public class DoubleCellEditor extends AbstractCellEditor implements TableCellEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JSpinner spinner;

	public DoubleCellEditor() {
		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));
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

}