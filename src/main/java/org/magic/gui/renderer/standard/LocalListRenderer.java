package org.magic.gui.renderer.standard;

import java.awt.Component;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.apache.commons.lang3.StringUtils;
import org.magic.services.MTGControler;

public class LocalListRenderer extends JLabel implements ListCellRenderer<Locale> {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList<? extends Locale> list, Locale value, int index,
			boolean isSelected, boolean cellHasFocus) {

		if (value != null)
			setText(StringUtils.capitalize(value.getDisplayLanguage(MTGControler.getInstance().getLocale())));
		else
			setText("");

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
