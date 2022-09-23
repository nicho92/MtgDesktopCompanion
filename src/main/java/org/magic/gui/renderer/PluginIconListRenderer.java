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
			var l = new JLabel(value.getName());
			l.setOpaque(true);
			l.setToolTipText(value.getName());
			if (isSelected) {
				l.setBackground(list.getSelectionBackground());
				l.setForeground(list.getSelectionForeground());
			} else {
				l.setBackground(list.getBackground());
				l.setForeground(list.getForeground());
			}
			l.setIcon(value.getIcon());
			return l;
		}
		return new JLabel(String.valueOf(value));

	}


}
