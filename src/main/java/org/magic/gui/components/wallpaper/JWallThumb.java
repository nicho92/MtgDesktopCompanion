package org.magic.gui.components.wallpaper;

import java.awt.Color;
import java.awt.SystemColor;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.magic.api.beans.MTGWallpaper;

public class JWallThumb extends JLabel {


	private static final long serialVersionUID = 1L;
	private boolean selected = false;
	private Color c = getBackground();
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
			setBackground(SystemColor.inactiveCaption);
		else
			setBackground(c);
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
	}

	@Override
	public String toString() {
		return getName();
	}

}