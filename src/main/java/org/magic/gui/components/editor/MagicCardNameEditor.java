package org.magic.gui.components.editor;

import java.awt.Component;
import java.util.Locale;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;

public class MagicCardNameEditor extends DefaultCellEditor {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private DefaultComboBoxModel<String> model;

	public MagicCardNameEditor() {
		super(new JComboBox<>());
		model = (DefaultComboBoxModel<String>) ((JComboBox<String>) getComponent()).getModel();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		model.removeAllElements();

		for (Locale l : Locale.getAvailableLocales())
			model.addElement(l.getDisplayLanguage(Locale.US));

		return super.getTableCellEditorComponent(table, model.getSelectedItem(), isSelected, row, column);
	}
}