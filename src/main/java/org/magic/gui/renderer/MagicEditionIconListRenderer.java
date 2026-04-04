package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.magic.api.beans.MTGEdition;
import org.magic.services.providers.IconsProvider;

public class MagicEditionIconListRenderer extends JLabel implements ListCellRenderer<MTGEdition> {

	
	
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
	public Component getListCellRendererComponent(JList<? extends MTGEdition> list, MTGEdition value, int index,boolean isSelected, boolean cellHasFocus) {

		if (value != null)
		{
			ImageIcon ic;
			if(size==SIZE.SMALL)
				ic = IconsProvider.getInstance().get16(value.getId());
			else
				ic = IconsProvider.getInstance().get24(value.getId());

			setText(value.getSet());
			setIcon(ic);
			setOpaque(true);


			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
		}
		return this;
	}

}
