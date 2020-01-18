package org.magic.gui.components.renderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.magic.api.beans.GedEntry;
import org.magic.tools.ImageTools;


public class GedEntryComponent<T> extends JLabel {

	private static final long serialVersionUID = 1L;
	private GedEntry<T> entry;
	private boolean selected = false;
	private Color defaultColor;
	public boolean isSelected() {
		return selected;
	}
	
	public GedEntryComponent(GedEntry<T> e) 
	{
		this.entry = e;
		setText(entry.getName());
		setIcon(entry.getIcon());
		
		defaultColor = getBackground();
		
		if(entry.isImage())
			setIcon(getThumbnail());
		
		setVerticalTextPosition(SwingConstants.BOTTOM);
		setHorizontalTextPosition(SwingConstants.CENTER);
		
		setToolTipText(e.getName());
		setOpaque(true);
		setPreferredSize(new Dimension(75,75));
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				selected=!selected;
				
				if(selected)
					setBackground(SystemColor.activeCaption);
				else
					setBackground(defaultColor);
			}
		});
	}
	
	public ImageIcon getThumbnail()
	{
		   if(entry.isImage()) 
		   {
			   try {
				   return new ImageIcon(ImageTools.read(entry.getContent()).getScaledInstance(50, 50, Image.SCALE_SMOOTH));
			   } catch (IOException e) {
				   return null;
			   }
		   }
		   
		   return null;
	}
	
}
