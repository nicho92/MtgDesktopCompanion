package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.magic.api.beans.MagicCard;

public class MagicCardListRenderer implements ListCellRenderer<MagicCard> {

	DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
	
	@Override
	public Component getListCellRendererComponent(JList<? extends MagicCard> list, MagicCard value, int index,boolean isSelected, boolean cellHasFocus) {
		 JLabel lab = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,isSelected, cellHasFocus);
		 
		 lab.setText(value.getName() + " ["+value.getEditions().get(0).getId()+"]");
		return lab;
	}

}
