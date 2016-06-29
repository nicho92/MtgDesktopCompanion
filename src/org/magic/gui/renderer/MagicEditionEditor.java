package org.magic.gui.renderer;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;

import org.magic.api.beans.MagicEdition;

public class MagicEditionEditor extends DefaultCellEditor {

    private DefaultComboBoxModel model;
 
    public MagicEditionEditor() {
        super(new JComboBox());
        model = (DefaultComboBoxModel)((JComboBox)getComponent()).getModel();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
             model.removeAllElements();
             List<MagicEdition> selectedItem = (List)table.getValueAt(row, column);
             for(MagicEdition e : selectedItem)
        	   model.addElement(e);
             
        return super.getTableCellEditorComponent(table, model.getSelectedItem(), isSelected, row, column);
     }
    }