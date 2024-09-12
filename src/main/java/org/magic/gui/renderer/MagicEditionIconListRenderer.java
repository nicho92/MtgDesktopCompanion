package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.magic.api.beans.MTGEdition;
import org.magic.services.providers.IconsProvider;

public class MagicEditionIconListRenderer implements ListCellRenderer<MTGEdition> {

	public enum SIZE {SMALL,MEDIUM}
	private SIZE size;
	private JLabel l;


	public MagicEditionIconListRenderer(SIZE s) {
		size=s;
		l = new JLabel();
	}

	public MagicEditionIconListRenderer() {
		size=SIZE.MEDIUM;
		l = new JLabel();
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

			l.setText(value.getSet());
			l.setIcon(ic);
			l.setOpaque(true);


			if (isSelected) {
				l.setBackground(list.getSelectionBackground());
				l.setForeground(list.getSelectionForeground());
			} else {
				l.setBackground(list.getBackground());
				l.setForeground(list.getForeground());
			}
		}

		return l;


	}

}
