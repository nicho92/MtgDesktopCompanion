package org.magic.gui.renderer;

import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.magic.api.interfaces.extra.MTGProduct;
import org.magic.gui.components.renderer.ProductRendererComponent;

public class ProductListRenderer extends ProductRendererComponent implements ListCellRenderer<MTGProduct> {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList<? extends MTGProduct> list, MTGProduct value, int index,
			boolean isSelected, boolean cellHasFocus) {
		init(value);

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
