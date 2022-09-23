package org.magic.gui.renderer.standard;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class BooleanCellEditorRenderer extends AbstractCellEditor implements TableCellEditor, TableCellRenderer{


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JCheckBox cbox;


	public BooleanCellEditorRenderer() {
		cbox = new JCheckBox();
		cbox.setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
	public Object getCellEditorValue() {
		return cbox.isSelected();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		var p = new JPanel();
			p.setOpaque(true);



			if(value==null)
				return p;


			cbox.setSelected(Boolean.parseBoolean(value.toString()));
			cbox.setOpaque(false);
			p.setLayout(new BorderLayout());
			p.add(cbox,BorderLayout.CENTER);

			if(isSelected)
				p.setBackground(table.getSelectionBackground());
			else
				p.setBackground(table.getBackground());

			return p;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		cbox.setSelected(Boolean.parseBoolean(value.toString()));

		return cbox;
	}


}