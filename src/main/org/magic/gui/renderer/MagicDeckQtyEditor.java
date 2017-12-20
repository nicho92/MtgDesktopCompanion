package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableCellEditor;

public class MagicDeckQtyEditor extends AbstractCellEditor implements TableCellEditor{

	JSpinner spinner;

	public MagicDeckQtyEditor() {
		spinner = new JSpinner();
		//((JSpinner.DefaultEditor)spinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.CENTER);
		
		SpinnerNumberModel model1 = new SpinnerNumberModel();
		model1.setMinimum(0);
		
		
		
		spinner.setModel(model1);
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