package org.magic.gui.renderer;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.magic.api.beans.game.Player;
import org.magic.gui.components.renderer.PlayerPanel;

public class PlayerRenderer extends JPanel implements TableCellRenderer, ListCellRenderer<Player> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PlayerPanel pComponent;
	private JPanel separator;

	public PlayerRenderer() {
		
		setLayout(new BorderLayout());
		separator = new JPanel();
		pComponent = new PlayerPanel();
		
		add(separator,BorderLayout.WEST);
		add(pComponent,BorderLayout.CENTER);

	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Player> list, Player value, int index,boolean isSelected, boolean cellHasFocus) {
		
		pComponent.setPlayer(value);
		
		var color = value.getState().getColor();
		pComponent.setBorder(new LineBorder(color));
		separator.setBackground(color);
		
		return this;
	}
	
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {

		if(value==null)
			return new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		
		pComponent.setPlayer((Player)value);
		
		
		
		if(isSelected)
		{
			pComponent.setForeground(table.getSelectionForeground());
			pComponent.setBackground(table.getSelectionBackground());
		}
		else
		{
			pComponent.setForeground(table.getForeground());
			pComponent.setBackground(table.getBackground());
		}


		return pComponent;
	}


}
