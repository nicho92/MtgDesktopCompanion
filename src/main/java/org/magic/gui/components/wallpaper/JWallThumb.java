package org.magic.gui.components.wallpaper;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.magic.api.beans.MTGWallpaper;
import org.magic.services.tools.UITools;

public class JWallThumb extends JLabel {


	private static final long serialVersionUID = 1L;
	private boolean selected = false;
	private transient MTGWallpaper wall;
	
	public boolean isSelected() {
		return selected;
	}

	public MTGWallpaper getWallpaper() {
		return wall;
	}
	
	
	public void selected(boolean s) {
		selected = s;
		if (selected)
			setBorder(new LineBorder(Color.RED));
		else
			setBorder(null);
		
	}

	
	public JWallThumb()
	{
		setHorizontalTextPosition(SwingConstants.CENTER);
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalTextPosition(SwingConstants.BOTTOM);
		
		setOpaque(true);
	}
	
	public JWallThumb(MTGWallpaper w) {
		super();
		wall = w;
		setText(w.getName());
		
		var b = new StringBuilder("<html>");
			 b.append("<b>").append(w.getName()).append("</b><br/>");
			 b.append("Author : ").append(w.getAuthor()).append("<br/>");
			 b.append("Date : ").append(UITools.formatDateTime(w.getPublishDate())).append("<br/>");
			 b.append("Nsfw : ").append(w.isMature()).append("<br/>");
			 b.append("Provider : ").append(w.getProvider()).append("<br/>");
			 if(!w.getTags().isEmpty())
				 b.append("Tags : ").append(String.join(", ", w.getTags())).append("<br/>");
		
		
		setToolTipText(b.toString());
	}

	@Override
	public String toString() {
		return getName();
	}

}