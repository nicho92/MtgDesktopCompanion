package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.magic.api.beans.MTGCard;
import org.magic.gui.components.renderer.CardListPanel;

public class MagicCardListRenderer extends CardListPanel implements ListCellRenderer<MTGCard> {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList<? extends MTGCard> list, MTGCard value, int index,boolean isSelected, boolean cellHasFocus) {
		setMagicCard(value);

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		return this;
	}

}
