package org.magic.gui.renderer;

import java.awt.Component;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;

import org.magic.api.beans.MagicEdition;

public class MagicEditionEditor extends DefaultCellEditor {

    private DefaultComboBoxModel<MagicEdition> model;
 
    public MagicEditionEditor() {
        super(new JComboBox<MagicEdition>());
        model = (DefaultComboBoxModel<MagicEdition>)((JComboBox<MagicEdition>)getComponent()).getModel();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
             model.removeAllElements();
             List<MagicEdition> selectedItem = (List<MagicEdition>)table.getValueAt(row, column);
            
             for(MagicEdition e : selectedItem)
        	   model.addElement(e);
             
        return super.getTableCellEditorComponent(table, model.getSelectedItem(), isSelected, row, column);
     }
    }