package org.magic.gui.renderer;

import java.awt.Component;
import java.util.Locale;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;

public class MagicCardNameEditor extends DefaultCellEditor {

    private DefaultComboBoxModel model;
 
    public MagicCardNameEditor() {
        super(new JComboBox());
        model = (DefaultComboBoxModel)((JComboBox)getComponent()).getModel();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
             model.removeAllElements();
             String selectedItem = table.getValueAt(row, column).toString();
             
           for(Locale l : Locale.getAvailableLocales())
        	   model.addElement(l.getDisplayLanguage(Locale.US));
             
        return super.getTableCellEditorComponent(table, model.getSelectedItem(), isSelected, row, column);
     }
    }