package org.magic.gui.renderer;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGGrading;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumRarity;
import org.magic.api.interfaces.MTGGraders;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.gui.renderer.standard.BooleanCellEditorRenderer;
import org.magic.gui.renderer.standard.DoubleCellEditorRenderer;
import org.magic.gui.renderer.standard.NumberCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.PluginRegistry;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.UITools;

public class StockTableRenderer implements TableCellRenderer{

	Component pane;

	@SuppressWarnings("unchecked")
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		pane = new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if(value instanceof Boolean)
		{
			pane= new BooleanCellEditorRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
		else if(value instanceof Integer)
		{
			pane= new NumberCellEditorRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
		else if(value instanceof Double)
		{
			pane= new DoubleCellEditorRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
		else if(value instanceof Map)
		{
			pane = new JPanel();
			((Map<String,Object>)value).entrySet().forEach(e->{
				var plug = PluginRegistry.inst().listPlugins().stream().filter(p->p.getName().equalsIgnoreCase(e.getKey())).findFirst().orElse(null);
				if(plug!=null)
					((JPanel)pane).add(new JLabel(ImageTools.resize(plug.getIcon(), new Dimension(MTGConstants.TABLE_ROW_HEIGHT,MTGConstants.TABLE_ROW_HEIGHT))));
				else
					((JPanel)pane).add(new JLabel(e.getKey()));
			}
			 );
		}
		else if(value instanceof MTGGrading g)
		{

			try {
				var c = PluginRegistry.inst().getPlugin(g.getGraderName(), MTGGraders.class).getIcon();
				pane= new JLabel(g.toString(),c,SwingConstants.LEADING );
			}
			catch(Exception e)
			{
				pane = new JLabel(g.toString());
			}
		}
		else if(value instanceof MTGEdition )
		{
			pane = new MagicEditionJLabelRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
		else if(value instanceof EnumCondition cond)
		{
			pane = new JLabel(cond.getLabel(),cond.getIcon(),SwingConstants.LEADING );
		}
		else if(value instanceof EnumRarity ra)
		{
			pane = new JLabel(ra.toPrettyString(),ra.getIcon(),SwingConstants.LEADING );
		}
		else if(value instanceof MTGCollection c)
		{
			pane = new JLabel(c.getName(),MTGConstants.ICON_TAB_BACK,SwingConstants.LEADING );
		}
		
		
		if(pane instanceof JLabel lab)
		{
			lab.setOpaque(true);
		}
		
		
		if (((MTGStockItem) UITools.getModelValueAt(table, row, 0)).isUpdated()) {
			pane.setBackground(MTGConstants.COLOR_UPDATE);
			pane.setForeground(table.getForeground());
		} else if (isSelected) {
			pane.setBackground(table.getSelectionBackground());
			pane.setForeground(table.getSelectionForeground());
		} else {
			pane.setBackground(table.getBackground());
			pane.setForeground(table.getForeground());
		}

		return pane;
	}

}