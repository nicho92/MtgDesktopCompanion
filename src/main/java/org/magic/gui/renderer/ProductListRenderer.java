package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.magic.api.interfaces.MTGProduct;
import org.magic.gui.components.renderer.ProductRendererComponent;

public class ProductListRenderer implements ListCellRenderer<MTGProduct> {

	ProductRendererComponent render;
	
	@Override
	public Component getListCellRendererComponent(JList<? extends MTGProduct> list, MTGProduct value, int index,boolean isSelected, boolean cellHasFocus) {


		if(value!=null)
		{
			
			if(render==null)
				render = new ProductRendererComponent(list);
			
			
			render.init(value);
			
			
			if (isSelected) {
				render.setBackground(list.getSelectionBackground());
				render.setForeground(list.getSelectionForeground());
			} else {
				render.setBackground(list.getBackground());
				render.setForeground(list.getForeground());
			}

			return render;
		}

		return new JLabel(String.valueOf(value));
	}

}
