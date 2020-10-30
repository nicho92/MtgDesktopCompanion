package org.magic.gui.components;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.Timer;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXImageView;
import org.jdesktop.swingx.graphics.ReflectionRenderer;
import org.jdesktop.swingx.painter.MattePainter;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;

public class CardsPicPanel2 extends JXImageView {

	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private transient ReflectionRenderer renderer;
	private BufferedImage back;
	private BufferedImage imgFront;
	private Timer timer;
	private float xScale = 1f;
	private float xDelta = 0.05f;
	int loop = 0;
	private boolean launched = false;


	public void showPhoto(MagicCard mc) {
		showPhoto(mc, null);
	}
	

	public CardsPicPanel2() {
		renderer = new ReflectionRenderer();
		setBackgroundPainter(new MattePainter(MTGConstants.PICTURE_PAINTER, true));
		
		setCursor(Cursor.getDefaultCursor());
		
		addMouseWheelListener(new MouseAdapter() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getWheelRotation() < 0) {
					setScale(getScale()*1.1);
		        }
				
		        if (e.getWheelRotation() > 0) {
		        	setScale(getScale()/1.1);
		        }
			}
		});
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!launched) {
					timer.start();
					launched = true;
				}
			}
		});
		
		timer = new Timer(MTGConstants.ROTATED_TIMEOUT, e -> {
			repaint();
			
			xScale += xDelta;
			
			if (xScale > 1 || xScale < -1) 
			{
				int pX = (int) ((getWidth() - (getImage().getWidth(null) * xScale)) / 2);
				int pY = (getHeight() - getImage().getHeight(null)) / 2;

				AffineTransform at = new AffineTransform();
				at.translate(pX, pY);
				at.scale(xScale, 1);
				
				((Graphics2D)getGraphics()).setTransform(at);				
				
				logger.debug(xScale);
				if (xScale < 0)
					setImage(back);
				else
					setImage(imgFront);
			}

			if (loop > 0 && ((int) xScale == 1 || (int) xScale == -1)) {
				timer.stop();
				launched = false;

			}
			loop++;
		});
		
	}
	
	public void showPhoto(MagicCard mc, MagicEdition edition) {
		
		if(mc == null)
			return;
		
		
		back = getEnabledPlugin(MTGPictureProvider.class).getBackPicture();
		
		if (mc.isDoubleFaced()) 
		{
			try {
				back = getEnabledPlugin(MTGPictureProvider.class).getPicture(mc.getRotatedCard(), null);
			} catch (IOException e) {
				logger.error("error loading " + mc.getRotatedCard());
			}
		}
		
		try {
			imgFront = renderer.appendReflection(getEnabledPlugin(MTGPictureProvider.class).getPicture(mc, edition));
			setImage(imgFront);
		} catch (IOException e) {
			logger.error("error loading picture for " + mc);
			setImage(back);
		}
		
	}
	
	

}
