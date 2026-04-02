package org.magic.gui.renderer;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.gui.components.renderer.JsonMessagePanel;

public class MessageRenderer extends JPanel implements ListCellRenderer<AbstractMessage> {
	
	private static final long serialVersionUID = 1L;
	private JsonMessagePanel pane;
	
	public MessageRenderer() {
		pane = new JsonMessagePanel();
		setLayout(new BorderLayout());
		add(new JPanel(),BorderLayout.NORTH);
		add(pane,BorderLayout.CENTER);
	}
	
	@Override
	public Component getListCellRendererComponent(JList<? extends AbstractMessage> list, AbstractMessage value, int index,boolean isSelected, boolean cellHasFocus) {

		pane.setMessage(value);
		
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
		return this;
	}
}