package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.magic.api.interfaces.MTGPlugin;

public class PluginIconListRenderer extends JLabel implements ListCellRenderer<MTGPlugin> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList<? extends MTGPlugin> list, MTGPlugin value, int index,boolean isSelected, boolean cellHasFocus) {

		if (value != null) {
			setText(value.getName());
			setOpaque(true);
			setToolTipText(value.getName());
			
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setIcon(value.getIcon());
			return this;
		}
		return new JLabel(String.valueOf(value));

	}


}
