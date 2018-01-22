package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.magic.services.MTGConstants;

public class CardShakeRenderer extends DefaultTableCellRenderer {

	JLabel comp;
	
	public CardShakeRenderer() {
		comp=new JLabel();
		comp.setHorizontalAlignment(JLabel.CENTER);
		comp.setOpaque(false);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		{
			try 
			{
				
				comp.setText(value.toString());
					
				if(((Double)value).doubleValue()>0)
					comp.setIcon(MTGConstants.ICON_UP);
	
				if(((Double)value).doubleValue()<0)
					comp.setIcon(MTGConstants.ICON_DOWN);
				
				if(((Double)value).doubleValue()==0)
					comp.setIcon(null);
			
				return comp;
			}
			catch(Exception e)
			{
				comp.setText(e.getMessage());
				return comp;
			}
		}
	}
	
	
}
