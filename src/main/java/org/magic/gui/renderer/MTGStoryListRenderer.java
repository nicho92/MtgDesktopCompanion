package org.magic.gui.renderer;

import java.awt.Component;
import java.awt.SystemColor;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.magic.api.beans.MTGStory;
import org.magic.api.beans.MagicCard;
import org.magic.gui.components.renderer.CardListPanel;
import org.magic.gui.components.renderer.MTGStoryListPanel;

public class MTGStoryListRenderer implements ListCellRenderer<MTGStory> {

	DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
	MTGStoryListPanel render;
	
	@Override
	public Component getListCellRendererComponent(JList<? extends MTGStory> list, MTGStory value, int index,boolean isSelected, boolean cellHasFocus) {
		render =new MTGStoryListPanel(value); 
		 if (isSelected) {
             render.setBackground(SystemColor.inactiveCaption);
         }
		
		return render;
	}

}
