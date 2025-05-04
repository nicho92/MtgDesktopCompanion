package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import org.magic.api.beans.MTGGrading;
import org.magic.api.interfaces.MTGGraders;
import org.magic.services.PluginRegistry;

public class GradingCellRenderer implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object g, boolean isSelected, boolean hasFocus,int row, int column) {
		
		JLabel pane = new JLabel();
		pane.setOpaque(true);
		var grad = (MTGGrading)g;
		
		
		try {
			var c = PluginRegistry.inst().getPlugin(grad.getGraderName(), MTGGraders.class).getIcon();
			pane.setText(grad.toString());
			pane.setIcon(c);
			pane.setHorizontalAlignment(SwingConstants.LEADING);
		}
		catch(Exception _)
		{
			pane.setText(null);
		}
		
		if(isSelected)
			pane.setBackground(table.getSelectionBackground());
		
		return pane;
		
	}



}
