package org.magic.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JTable;

import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.magic.services.MTGConstants;

public class MagicCollectionTableCellRenderer extends DefaultTableRenderer {

	HashMap<String, ImageIcon> cache;
	Color c ;
	
	public MagicCollectionTableCellRenderer() {
		cache=new HashMap<>();
	}
	
	Component pane;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
	{
			if(column==4)
			{
				value = new DecimalFormat("#0%").format((double)value);
				//value=value.toString()+"%";
			}
			
			pane = super.getTableCellRendererComponent(table, value, isSelected,hasFocus, row, column);
			pane.setBackground(c);
			
			
			if((double)table.getValueAt(row, 4)*100<5)
			{
				pane.setBackground(table.getBackground());
				pane.setForeground(Color.BLACK);
			}
			
			if((double)table.getValueAt(row, 4)*100>=5 && (double)table.getValueAt(row, 4)*100<50)
			{
				pane.setBackground(MTGConstants.COLLECTION_5PC);
				pane.setForeground(Color.BLACK);
			}
			
			if((double)table.getValueAt(row, 4)*100>=50)
			{
				pane.setBackground(MTGConstants.COLLECTION_50PC);
				pane.setForeground(Color.BLACK);
			}
			
			if((double)table.getValueAt(row, 4)*100>=90)
			{
				pane.setBackground(MTGConstants.COLLECTION_90PC);
				pane.setForeground(Color.BLACK);
			}
			
			if((double)table.getValueAt(row, 4)*100>=100)
			{
				pane.setBackground(MTGConstants.COLLECTION_100PC);
				pane.setForeground(Color.BLACK);
			}
			return pane;
		}

	}
	
	
}
