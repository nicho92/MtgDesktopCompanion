package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;

import org.magic.api.beans.EnumCondition;

public class EnumConditionEditor extends DefaultCellEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DefaultComboBoxModel<EnumCondition> model;

	public EnumConditionEditor() {
		super(new JComboBox<EnumCondition>());
		model = (DefaultComboBoxModel<EnumCondition>) ((JComboBox<EnumCondition>) getComponent()).getModel();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		model.removeAllElements();
		for (EnumCondition e : EnumCondition.values())
			model.addElement(e);

		return super.getTableCellEditorComponent(table, model.getSelectedItem(), isSelected, row, column);
	}
}