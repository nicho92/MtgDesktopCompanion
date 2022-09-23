package org.magic.gui.renderer;

import java.awt.Component;
import java.awt.SystemColor;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.magic.api.beans.MTGStory;
import org.magic.gui.components.renderer.MTGStoryListPanel;

public class MTGStoryListRenderer implements ListCellRenderer<MTGStory> {

	@Override
	public Component getListCellRendererComponent(JList<? extends MTGStory> list, MTGStory value, int index,boolean isSelected, boolean cellHasFocus) {

		var render = new MTGStoryListPanel(value);
		if (isSelected) {
			render.setBackground(SystemColor.inactiveCaption);
		}

		return render;
	}

}
