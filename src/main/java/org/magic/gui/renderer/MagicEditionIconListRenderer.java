package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.magic.api.beans.MagicEdition;
import org.magic.services.extra.IconSetProvider;

public class MagicEditionIconListRenderer extends JLabel implements ListCellRenderer<MagicEdition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public enum SIZE {SMALL,MEDIUM}
	
	private SIZE size;
	
	public MagicEditionIconListRenderer(SIZE s) {
		size=s;
	}
	
	public MagicEditionIconListRenderer() {
		size=SIZE.MEDIUM;
	}
	
	
	@Override
	public Component getListCellRendererComponent(JList<? extends MagicEdition> list, MagicEdition value, int index,boolean isSelected, boolean cellHasFocus) {

		if (value != null) 
		{
			ImageIcon ic;
			if(size==SIZE.SMALL)
				ic = IconSetProvider.getInstance().get16(value.getId());
			else
				ic = IconSetProvider.getInstance().get24(value.getId());
			
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
		return new JLabel();

	}

}
