package org.magic.gui.components.editor;

import java.awt.Component;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.tools.MTG;

import com.google.common.collect.Lists;

public class LanguageComboBoxCellEditor extends DefaultCellEditor{

	private static final long serialVersionUID = 1L;
	private DefaultComboBoxModel<String> model;

	public LanguageComboBoxCellEditor() {
		super(new JComboBox<>());
		model = (DefaultComboBoxModel<String>) ((JComboBox<String>) getComponent()).getModel();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		model.removeAllElements();
		List<String> selectedItem = Lists.newArrayList(MTG.getEnabledPlugin(MTGCardsProvider.class).getLanguages());
		Collections.sort(selectedItem);
		model.addAll(selectedItem);
		
		return super.getTableCellEditorComponent(table, model.getSelectedItem(), isSelected, row, column);
	}

}