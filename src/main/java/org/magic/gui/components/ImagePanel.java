package org.magic.gui.components;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.Timer;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.graphics.ReflectionRenderer;
import org.jdesktop.swingx.painter.MattePainter;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.ImageTools;

public class ImagePanel extends JXPanel {

	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	
	
	
	private transient BufferedImage imgFront = null;
	private transient BufferedImage imgBack;
	private transient ReflectionRenderer renderer;
	private transient BufferedImage printed;
	
	
	boolean launched = false;
	private Timer timer;
	
	private double zoomFactor = 1;
	private double xDiff=0;
	private double yDiff=0;
	private int loop = 0;
	private float xScale = 1f;
	private float xDelta = 0.05f;
	private boolean rotable;
	private boolean moveable;
	private boolean zoomable;
	private boolean debug = false;
	private boolean reflection = true;
	
	private void setActions(boolean moveable,boolean rotable,boolean zoomable) 
	{
		GestionnaireEvenements interactionManager = new GestionnaireEvenements();
		this.rotable=rotable;
		this.moveable=moveable;
		this.zoomable=zoomable;
		this.addMouseListener(interactionManager);
		this.addMouseMotionListener(interactionManager);
		this.addMouseWheelListener(interactionManager);
	}

	
	public ImagePanel() {
		initGUI();
		setActions(true,true,true);
	}
	
	public ImagePanel(boolean moveable,boolean rotable,boolean zoomable) {
		initGUI();
		setActions(moveable,rotable,zoomable);
	}

	public void showCard(MagicCard mc) {
		showCard(mc, null);
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public void setReflection(boolean reflection) {
		this.reflection = reflection;
	}
	
	public void setImg(BufferedImage img)
    {
			if(img==null)
				return;
		
	   		imgBack=img;
	   		imgFront=img;
	   		printed=img;
	   		

	   		if(reflection) {
				imgFront = renderer.appendReflection(imgFront);
				imgBack = renderer.appendReflection(ImageTools.mirroring(imgBack));
			}
    }
	

	public void showCard(MagicCard mc, MagicEdition edition) 
	{
		if(mc == null)
			return;
		
		if (!mc.isDoubleFaced()) 
		{
			imgBack = getEnabledPlugin(MTGPictureProvider.class).getBackPicture();
		} 
		else 
		{
			try {
				MagicCard rcard =mc.getRotatedCard();
				imgBack = getEnabledPlugin(MTGPictureProvider.class).getPicture(rcard, null);
			} catch (Exception e) {
				logger.error("error loading rotated card : " + mc.getRotatedCard(),e);

			}
		}

		ThreadManager.getInstance().executeThread(() -> {
			try {
				if (edition == null)
					imgFront = getEnabledPlugin(MTGPictureProvider.class).getPicture(mc, null);
				else
					imgFront = getEnabledPlugin(MTGPictureProvider.class).getPicture(mc, edition);

				
				if(mc.isFlippable())
					imgBack = ImageTools.rotate(imgFront, 180);
				
				
				if(mc.getLayout()==MTGLayout.SPLIT)
					imgFront= ImageTools.rotate(imgFront, 90);
				
		   		if(reflection) {
					imgFront = renderer.appendReflection(imgFront);
					imgBack = renderer.appendReflection(ImageTools.mirroring(imgBack));
				}
				printed = imgFront;
			} catch (Exception e) {
				imgFront = imgBack;
			}

			repaint();
		}, "show img for " + mc);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
				   g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				   g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				   g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
				   g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				   
				   
		if (printed == null)
			return;
	

		int pX = (int) ((getWidth() - (printed.getWidth() * xScale)) / 2);
		int pY = (getHeight() - printed.getHeight()) / 2;
		
		

		if (xScale < 0)
			printed = imgBack;
		else
			printed = imgFront;

		
		AffineTransform at = new AffineTransform();
					    at.translate(pX+xDiff, pY+yDiff);
					    at.scale(xScale, 1);
	
		if(debug) 
		{
			g2.setColor(Color.red);
			g2.drawString("FRAME : W="+getWidth() +" h="+getHeight(), 5, 20);
			g2.drawString("TRANS : pX="+pX +" pY="+pY + " xScale="+xScale + " xDiff="+xDiff + " yDiff="+yDiff + " zoomFactor="+zoomFactor, 5, 35);
			g2.drawString("IMAGE : W=" + (int)(printed.getWidth()*zoomFactor) + " H=" + (int)(printed.getHeight()*zoomFactor), 5, 50);
			g2.drawString("AT =" + at,5,65);
		}
		
		g2.transform(at);
		g2.drawImage(printed, 0, 0,(int)(printed.getWidth()*zoomFactor),(int)( printed.getHeight()*zoomFactor),null);
		g2.dispose();


	}

	
	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		renderer = new ReflectionRenderer();
		setBackgroundPainter(new MattePainter(MTGConstants.PICTURE_PAINTER, true));
		timer = new Timer(MTGConstants.ROTATED_TIMEOUT, e -> {
			repaint();

			xScale += xDelta;

			if (xScale > 1 || xScale < -1) {
				xDelta *= -1;

			}

			if (loop > 0 && ((int) xScale == 1 || (int) xScale == -1)) {
				timer.stop();
				launched = false;

			}
			loop++;
		});
	}

	private class GestionnaireEvenements extends MouseAdapter {
		private Point startPoint;

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			
			if(!zoomable)
				return;
			
			if (e.getWheelRotation() == -1) // zoom
				zoomFactor *= 1.1;
			else
				zoomFactor /=1.1;

			
			repaint();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			
			if(!rotable)
				return;
			
			if (!launched) 
			{
				timer.start();
				launched = true;
			}
		}
	
		
		@Override
		public void mousePressed(MouseEvent e) {
			 startPoint = e.getPoint(); 
					 
		}
		
		
		@Override
		public void mouseDragged(MouseEvent e) {
			if(!moveable)
				return;
			
			 Point curPoint = e.getPoint();
		        xDiff = (double)curPoint.x - startPoint.x;
		        yDiff = (double)curPoint.y - startPoint.y;
		        repaint();
		}

		

	}
}