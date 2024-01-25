package org.magic.gui.renderer;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.magic.api.beans.MTGEdition;
import org.magic.services.providers.IconSetProvider;

public class MagicEditionsJLabelRenderer implements TableCellRenderer {

	public MagicEditionsJLabelRenderer() {
		var flowLayout = new FlowLayout();
		flowLayout.setVgap(0);
		flowLayout.setAlignment(FlowLayout.LEFT);
		pane.setLayout(flowLayout);
	}

	JPanel pane = new JPanel();

	@SuppressWarnings("unchecked")
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		pane.removeAll();
		pane.setBackground(new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column).getBackground());

		for (MTGEdition ed : (List<MTGEdition>) value) {
			var l = new JLabel(IconSetProvider.getInstance().get16(ed.getId()));
			l.setToolTipText(ed.getSet());
			l.setOpaque(false);
			pane.add(l);
		}
		return pane;

	}

}
