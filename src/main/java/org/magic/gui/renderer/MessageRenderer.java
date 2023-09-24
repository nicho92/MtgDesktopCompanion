package org.magic.gui.renderer;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.gui.components.renderer.JsonMessagePanel;

public class MessageRenderer implements ListCellRenderer<AbstractMessage> {

	@Override
	public Component getListCellRendererComponent(JList<? extends AbstractMessage> list, AbstractMessage value, int index,boolean isSelected, boolean cellHasFocus) {
		var panel = new JPanel();
		var pane = new JsonMessagePanel(value);
		panel.setLayout(new BorderLayout());
		panel.add(new JPanel(),BorderLayout.NORTH);
		panel.add(pane,BorderLayout.CENTER);
		pane.setOpaque(true);
		
		if(isSelected)
		{
			pane.setBackground(list.getSelectionBackground());
			pane.setForeground(list.getSelectionForeground());
		}
		else
		{
			pane.setBackground(list.getBackground());
			pane.setForeground(list.getForeground());
		}
		
		
		return panel;
	}}
