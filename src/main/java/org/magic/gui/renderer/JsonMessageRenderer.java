package org.magic.gui.renderer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import org.magic.api.beans.JsonMessage;
import org.magic.services.MTGControler;
import org.magic.services.tools.ImageTools;
import org.ocpsoft.prettytime.PrettyTime;

public class JsonMessageRenderer implements ListCellRenderer<JsonMessage> {

	
	@Override
	public Component getListCellRendererComponent(JList<? extends JsonMessage> list, JsonMessage value, int index,boolean isSelected, boolean cellHasFocus) {
		int iconSize=25;
		var panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		
		
		
		var lbl = new JLabel("<html><b>"+value.getAuthor().getName()+"</b><i> ("+new PrettyTime(MTGControler.getInstance().getLocale()).format(new Date(value.getTimeStamp()))+")</i></html>",
							 new ImageIcon(ImageTools.resize(value.getAuthor().getAvatar(), iconSize, iconSize)),
							 SwingConstants.LEFT  );
		
		panel.add(lbl, BorderLayout.NORTH);
		panel.add(new JSeparator(SwingConstants.VERTICAL),BorderLayout.WEST);
		
		JTextArea textArea = new JTextArea(value.getMessage());
		textArea.setEditable(false);
		textArea.setForeground(value.getColor());
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		panel.add(textArea, BorderLayout.CENTER);
		
		return panel;
	}
	
	

}
