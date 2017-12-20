package org.magic.gui.renderer;

import java.awt.Component;
import java.awt.SystemColor;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.magic.api.beans.MagicCard;
import org.magic.gui.components.renderer.CardListPanel;

public class MagicCardListRenderer implements ListCellRenderer<MagicCard> {

	DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
	CardListPanel render;
	
	@Override
	public Component getListCellRendererComponent(JList<? extends MagicCard> list, MagicCard value, int index,boolean isSelected, boolean cellHasFocus) {
		render =new CardListPanel(value); 
		 if (isSelected) {
             render.setBackground(SystemColor.inactiveCaption);
         }
		
		return render;
	}

}
