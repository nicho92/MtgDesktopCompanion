package org.magic.gui.renderer;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.magic.api.beans.MagicEdition;
import org.magic.services.MTGConstants;
import org.magic.services.extra.IconSetProvider;

public class MagicEditionJLabelRenderer extends DefaultTableCellRenderer {

	
	private static final long serialVersionUID = 1L;
	private Font f = MTGConstants.FONT.deriveFont(Font.PLAIN);
	
	
	public MagicEditionJLabelRenderer() {
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setVgap(0);
		flowLayout.setAlignment(FlowLayout.LEFT);
		pane.setLayout(flowLayout);
	}

	JPanel pane = new JPanel();

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		
		
		if(value==null)
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		pane.removeAll();
		pane.setBackground(super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column).getBackground());

		MagicEdition ed = (MagicEdition) value;
			JLabel l = new JLabel(IconSetProvider.getInstance().get16(ed.getId()));
			l.setText(ed.getSet());
			l.setToolTipText(ed.getSet());
			l.setOpaque(false);
			l.setFont(f);
			pane.add(l);
		return pane;

	}

}
