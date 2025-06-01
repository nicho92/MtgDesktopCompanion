package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.magic.api.beans.MTGCard;
import org.magic.gui.components.renderer.CardListPanel;

public class MagicCardListRenderer implements ListCellRenderer<MTGCard> {

	private CardListPanel render;

	public MagicCardListRenderer() {
		render = new CardListPanel();
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends MTGCard> list, MTGCard value, int index,boolean isSelected, boolean cellHasFocus) {
		render.setMagicCard(value);

		if (isSelected) {
			render.setBackground(list.getSelectionBackground());
		} else {
			render.setBackground(list.getBackground());
		}

		return render;
	}

}
