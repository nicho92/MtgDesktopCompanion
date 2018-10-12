package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.services.MTGConstants;
import org.magic.services.extra.IconSetProvider;

public class MagicCollectionIconListRenderer extends JLabel implements ListCellRenderer<MagicCollection> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MagicCollectionIconListRenderer() {
		
	}
	
	
	@Override
	public Component getListCellRendererComponent(JList<? extends MagicCollection> list, MagicCollection value, int index,boolean isSelected, boolean cellHasFocus) {

		if (value != null) {
			JLabel l = new JLabel(value.getName());
			l.setOpaque(true);
			l.setToolTipText(value.getName());
			if (isSelected) {
				l.setBackground(list.getSelectionBackground());
				l.setForeground(list.getSelectionForeground());
			} else {
				l.setBackground(list.getBackground());
				l.setForeground(list.getForeground());
			}
			l.setIcon(MTGConstants.ICON_COLLECTION);
			return l;
		}
		return new JLabel();

	}

}
