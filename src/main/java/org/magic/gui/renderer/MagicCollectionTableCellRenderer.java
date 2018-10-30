package org.magic.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.magic.services.MTGConstants;
import org.magic.tools.UITools;

public class MagicCollectionTableCellRenderer extends DefaultTableRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<String, ImageIcon> cache;
	Color c;

	public MagicCollectionTableCellRenderer() {
		cache = new HashMap<>();
	}

	Component pane;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {

		if (column == 4)
			value = new DecimalFormat("#0.0%").format((double) value);

		if(value instanceof ImageIcon)
		{
			pane=new JLabel((ImageIcon)value);
			((JLabel)pane).setOpaque(true);
		}
		else if(value instanceof Boolean)
		{
			pane=new JPanel();
			JCheckBox jcbox=new JCheckBox("",Boolean.parseBoolean(value.toString()));
			jcbox.setOpaque(false);
			((JPanel)pane).add(jcbox);
		}
		else
		{
			pane = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
		
		pane.setBackground(c);
		
		double val = (double) table.getValueAt(row, 4);
		
		if (val * 100 >= 1 && val * 100 < 50) {
			pane.setBackground(MTGConstants.COLLECTION_1PC);
			pane.setForeground(Color.BLACK);
		}

		if (val * 100 >= 50) {
			pane.setBackground(MTGConstants.COLLECTION_50PC);
			pane.setForeground(Color.BLACK);
		}

		if (val * 100 >= 90) {
			pane.setBackground(MTGConstants.COLLECTION_90PC);
			pane.setForeground(Color.BLACK);
		}

		if (val * 100 >= 100) {
			pane.setBackground(MTGConstants.COLLECTION_100PC);
			pane.setForeground(Color.BLACK);
		}

		if(isSelected)
			UITools.applyDefaultSelection(pane);
		
		
		return pane;
	}

}
