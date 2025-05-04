package org.magic.gui.components.editor;

import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;

import org.magic.api.beans.MTGEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.tools.MTG;


@SuppressWarnings("unchecked")
public class MagicEditionsComboBoxCellEditor extends DefaultCellEditor {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private DefaultComboBoxModel<MTGEdition> model;

	
	public MagicEditionsComboBoxCellEditor() {
		super(new JComboBox<>());
		model = (DefaultComboBoxModel<MTGEdition>) ((JComboBox<MTGEdition>) getComponent()).getModel();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		model.removeAllElements();
		List<MTGEdition> selectedItem =new ArrayList<>();
		
		try {
			selectedItem = (List<MTGEdition>) table.getValueAt(row, column);	
		}
		catch(Exception _)
		{	
			try {
				selectedItem = MTG.getEnabledPlugin(MTGCardsProvider.class).listEditions();
			} catch (IOException _) {
				selectedItem = new ArrayList<>();
			}
		}
		Collections.sort(selectedItem);
		
		for (MTGEdition e : selectedItem)
			model.addElement(e);

		return super.getTableCellEditorComponent(table, model.getSelectedItem(), isSelected, row, column);
	}
}