package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.magic.api.beans.MagicEdition;
import org.magic.services.IconSetProvider;

public class MagicEditionListRenderer extends JLabel implements ListCellRenderer<MagicEdition> {

	@Override
	public Component getListCellRendererComponent(JList<? extends MagicEdition> list, MagicEdition value, int index,boolean isSelected, boolean cellHasFocus) {
		ImageIcon ic = IconSetProvider.getInstance().get(value.getId());
		JLabel l = new JLabel(value.getSet());
		l.setOpaque(true);
		l.setToolTipText(value.getId());
		if (isSelected) {
            l.setBackground(list.getSelectionBackground());
            l.setForeground(list.getSelectionForeground());
        } else {
        	l.setBackground(list.getBackground());
        	l.setForeground(list.getForeground());
        }
		l.setIcon(ic);
		return l;
		
		
	}
	
	
}
