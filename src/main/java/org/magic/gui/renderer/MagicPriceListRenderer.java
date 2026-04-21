package org.magic.gui.renderer;

import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.magic.api.beans.MTGPrice;
import org.magic.gui.components.renderer.MagicPricePanel;

public class MagicPriceListRenderer extends MagicPricePanel implements ListCellRenderer<MTGPrice> {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList<? extends MTGPrice> list, MTGPrice value, int index,
			boolean isSelected, boolean cellHasFocus) {
		init(value);
		return this;
	}

}
