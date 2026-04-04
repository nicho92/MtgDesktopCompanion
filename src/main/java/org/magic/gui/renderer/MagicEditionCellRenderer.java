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

public class MagicEditionCellRenderer extends JPanel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;
	private Font f = MTGControler.getInstance().getFont().deriveFont(Font.PLAIN);
	private JLabel lab;
	
	
	public MagicEditionCellRenderer() {
		var flowLayout = new FlowLayout();
		flowLayout.setVgap(0);
		flowLayout.setAlignment(FlowLayout.LEFT);
		setLayout(flowLayout);
		lab = new JLabel();
		add(lab);
		
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {

		
		if(value==null)
			return new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

//	removeAll();
		setBackground(table.getBackground());

		MTGEdition ed = (MTGEdition) value;
			lab.setIcon(IconsProvider.getInstance().get16(ed.getId()));
			lab.setText(ed.getSet());
			lab.setToolTipText(ed.getSet());
			lab.setOpaque(false);
			if(isSelected)
			{
				lab.setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			}
			else
			{
				lab.setForeground(table.getForeground());
				setBackground(table.getBackground());
			}

			lab.setFont(f);
			
		return this;

	}

}
