package org.magic.gui.renderer;

import java.awt.Component;
import java.sql.SQLException;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;

import org.magic.api.beans.MagicCollection;
import org.magic.services.MTGControler;

public class EnumConditionEditor extends DefaultCellEditor {

    private DefaultComboBoxModel model;
 
    public EnumConditionEditor() {
        super(new JComboBox());
        model = (DefaultComboBoxModel)((JComboBox)getComponent()).getModel();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
             model.removeAllElements();
           try {
			for(MagicCollection e : MTGControler.getInstance().getEnabledDAO().getCollections())
				   model.addElement(e);
		} catch (SQLException e) {
			
		}
             
        return super.getTableCellEditorComponent(table, model.getSelectedItem(), isSelected, row, column);
     }
    }