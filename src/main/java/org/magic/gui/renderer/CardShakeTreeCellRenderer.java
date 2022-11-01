package org.magic.gui.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicEdition;
import org.magic.gui.renderer.MagicEditionIconListRenderer.SIZE;
import org.magic.services.MTGConstants;
import org.magic.services.tools.UITools;

public class CardShakeTreeCellRenderer implements TreeCellRenderer, TableCellRenderer{

	private JList<MagicEdition> defaultJlist;

	public CardShakeTreeCellRenderer() {
		defaultJlist=new JList<>();
	}


	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		if(value instanceof Double)
			return new JLabel(UITools.formatDouble(value));

		return new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree,Object value,boolean selected,boolean expanded,boolean leaf,int row,boolean hasFocus) {

		if(value instanceof MagicEdition ed)
			return new MagicEditionIconListRenderer(SIZE.SMALL).getListCellRendererComponent(defaultJlist,ed, 0, selected, hasFocus);

		if(value instanceof CardShake)
		{
			var l = new JLabel(value.toString());
			l.setIcon(MTGConstants.ICON_TAB_CARD);
			return l;
		}


		return new JLabel(String.valueOf(value));


	}


}
