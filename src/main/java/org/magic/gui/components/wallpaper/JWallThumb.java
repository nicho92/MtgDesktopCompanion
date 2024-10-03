package org.magic.gui.components.wallpaper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.SystemColor;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGWallpaper;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.ImageTools;

public class JWallThumb extends JLabel {


	private static final long serialVersionUID = 1L;
	private boolean selected = false;
	private Color c = getBackground();
	private transient MTGWallpaper wall;
	private int size;
	private int fontHeight = 20;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public boolean isSelected() {
		return selected;
	}

	public MTGWallpaper getWallpaper() {
		return wall;
	}

	public void resizePic(int size) {
		this.size = size;
		try {

			int w = wall.getPicture().getWidth(null);
			int h = wall.getPicture().getHeight(null);
			float scaleW = (float) size / w;
			float scaleH = (float) size / h;
			if (scaleW > scaleH) {
				w = -1;
				h = (int) (h * scaleH);
			} else {
				w = (int) (w * scaleW);
				h = -1;
			}
			Image img = ImageTools.resize(wall.getPicture(),w, h);
			setIcon(new ImageIcon(img));
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void selected(boolean s) {
		selected = s;
		if (selected)
			setBackground(SystemColor.inactiveCaption);
		else
			setBackground(c);
	}

	public JWallThumb(MTGWallpaper w,boolean title) {
		wall = w;
		setHorizontalTextPosition(SwingConstants.CENTER);
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalTextPosition(SwingConstants.BOTTOM);
		
		if(title)
			setText(w.getName());
		
		setOpaque(true);

		if(w.getPicture()==null)
			try {
				wall = w.load();
			} catch (IOException e) {
				logger.error(e);
			}

		resizePic(400);
	}



	@Override
	public Dimension getPreferredSize() {
		return new Dimension(size, size + fontHeight);
	}

	@Override
	public String toString() {
		return getName();
	}

}