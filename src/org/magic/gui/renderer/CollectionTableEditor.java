package org.magic.gui.renderer;

import java.awt.Component;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;

import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.MagicEdition;

public class CollectionTableEditor extends DefaultCellEditor {

    private DefaultComboBoxModel model;
 
    public CollectionTableEditor() {
        super(new JComboBox());
        model = (DefaultComboBoxModel)((JComboBox)getComponent()).getModel();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
             model.removeAllElements();
           for(EnumCondition e : EnumCondition.values())
        	   model.addElement(e);
             
        return super.getTableCellEditorComponent(table, model.getSelectedItem(), isSelected, row, column);
     }
    }