package org.magic.gui.renderer.standard;

import java.awt.Component;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;

import org.magic.tools.UITools;

public class ComboBoxEditor<T> extends DefaultCellEditor {

	private static final long serialVersionUID = 1L;
	private DefaultComboBoxModel<T> model;
	private transient List<T> values;


	@SuppressWarnings("unchecked")
	public ComboBoxEditor(List<T> values) {
		super(UITools.createCombobox(values));
		model = (DefaultComboBoxModel<T>) ((JComboBox<T>) getComponent()).getModel();
		this.values=values;
	}

	@SuppressWarnings("unchecked")
	public ComboBoxEditor(T[] values) {
		super(UITools.createCombobox(values));
		model = (DefaultComboBoxModel<T>) ((JComboBox<T>) getComponent()).getModel();
		this.values=Arrays.asList(values);
	}


	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		model.removeAllElements();

		for (T e : values)
			model.addElement(e);

		return super.getTableCellEditorComponent(table, model.getSelectedItem(), isSelected, row, column);
	}

}
