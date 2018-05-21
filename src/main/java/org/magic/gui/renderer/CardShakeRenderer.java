package org.magic.gui.renderer;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.magic.services.MTGConstants;

public class CardShakeRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;
	private DecimalFormat df;

	public CardShakeRenderer() {
		df = new DecimalFormat("#.##");
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		JLabel comp = new JLabel();
		comp.setHorizontalAlignment(JLabel.CENTER);
		comp.setOpaque(true);
		
		if (isSelected) {
			comp.setBackground(table.getSelectionBackground());
			comp.setForeground(table.getSelectionForeground());
		} else {
			comp.setBackground(table.getBackground());
			comp.setForeground(table.getForeground());
		}
		
		
		try {
			
			comp.setText(df.format(value));
			if (((Double) value).doubleValue() > 0)
			{
				comp.setIcon(MTGConstants.ICON_UP);
			}

			if (((Double) value).doubleValue() < 0)
			{
				comp.setIcon(MTGConstants.ICON_DOWN);
			}

			if (((Double) value).doubleValue() == 0)
			{
				comp.setIcon(null);
			}
			
			
		
			
			return comp;
		} catch (Exception e) {
			comp.setText(e.getMessage());
			return comp;
		}

	}

}
