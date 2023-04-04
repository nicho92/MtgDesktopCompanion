package org.magic.gui.renderer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.border.LineBorder;

import org.magic.api.beans.JsonMessage;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.UITools;

public class JsonMessageRenderer implements ListCellRenderer<JsonMessage> {

	private final int iconSize=50;
	
	@Override
	public Component getListCellRendererComponent(JList<? extends JsonMessage> list, JsonMessage value, int index,boolean isSelected, boolean cellHasFocus) {
		var comp= component(value);
		comp.setBorder(new LineBorder(Color.BLACK));
		return comp;
	}
	

	private JComponent component(JsonMessage value) {
		
		var panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(new JLabel(new ImageIcon(ImageTools.resize(value.getAuthor().getAvatar(), iconSize, iconSize))), BorderLayout.WEST);
		
		var lbl = new JLabel("<html><b>"+value.getAuthor().getName()+"</b><i> ("+UITools.formatDateTime(new Date(value.getTimeStamp()))+")</i></html>"  );
		
		panel.add(lbl, BorderLayout.NORTH);
		
		JTextArea textArea = new JTextArea(value.getMessage());
		textArea.setEditable(false);
		textArea.setForeground(value.getColor());
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		panel.add(textArea, BorderLayout.CENTER);
		
		return panel;
		
	}

	

}
