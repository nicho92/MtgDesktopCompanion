package org.magic.game.gui.components.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.magic.game.model.AbstractSpell;

public class StackItemRenderer extends JLabel implements ListCellRenderer<AbstractSpell> 
{
	

	@Override
	public Component getListCellRendererComponent(JList<? extends AbstractSpell> list, AbstractSpell value, int index,boolean isSelected, boolean cellHasFocus) {
		
		try{		
			return new SpellRendererPanel(value);
		}
		catch(Exception e)
		{
			return new JLabel(value.toString());
		}
		
	}
}
