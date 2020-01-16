package org.magic.gui.components.renderer;

import java.awt.Image;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.magic.api.beans.GedEntry;
import org.magic.tools.ImageTools;

public class GedEntryComponent<T> extends JLabel {

	private static final long serialVersionUID = 1L;
	private GedEntry<T> entry;

	public GedEntryComponent(GedEntry<T> e) 
	{
		this.entry = e;
		
		setText(entry.getName());
		setIcon(entry.getIcon());
		
		setVerticalTextPosition(SwingConstants.BOTTOM);
		setHorizontalTextPosition(SwingConstants.CENTER);
	}
	
	public ImageIcon getThumbnail()
	{
		   if(entry.isImage()) 
		   {
			   try {
				   return new ImageIcon(ImageTools.read(entry.getFile()).getScaledInstance(50, 50, Image.SCALE_SMOOTH));
			   } catch (IOException e) {
				   return null;
			   }
		   }
		   
		   return null;
	}
	
}
