package org.magic.gui.renderer;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.magic.api.beans.MTGEdition;
import org.magic.services.MTGControler;
import org.magic.services.providers.IconsProvider;

public class MagicEditionJLabelRenderer implements TableCellRenderer {

	private Font f = MTGControler.getInstance().getFont().deriveFont(Font.PLAIN);


	public MagicEditionJLabelRenderer() {
		var flowLayout = new FlowLayout();
		flowLayout.setVgap(0);
		flowLayout.setAlignment(FlowLayout.LEFT);
		pane.setLayout(flowLayout);
	}

	JPanel pane = new JPanel();

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {


		if(value==null)
			return new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		pane.removeAll();
		pane.setBackground(table.getBackground());

		MTGEdition ed = (MTGEdition) value;
		var l = new JLabel(IconsProvider.getInstance().get16(ed.getId()));
			l.setText(ed.getSet());
			l.setToolTipText(ed.getSet());
			l.setOpaque(false);
			if(isSelected)
			{
				l.setForeground(table.getSelectionForeground());
				pane.setBackground(table.getSelectionBackground());
			}
			else
			{
				l.setForeground(table.getForeground());
				pane.setBackground(table.getBackground());
			}

			l.setFont(f);
			pane.add(l);
		return pane;

	}

}
