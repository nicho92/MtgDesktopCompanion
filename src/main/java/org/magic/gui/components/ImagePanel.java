package org.magic.gui.components;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.Timer;

import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.graphics.ReflectionRenderer;
import org.jdesktop.swingx.painter.MattePainter;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.network.URLTools;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.ImageTools;

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
		var interactionManager = new GestionnaireEvenements();
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

	   		repaint();
    }


	public void setUrlImage(String url){
		var sw = new SwingWorker<BufferedImage, Void>() {
			@Override
			protected BufferedImage doInBackground() throws Exception {
				return URLTools.extractAsImage(url);
			}

			@Override
			protected void done() {
				try {
					setImg(get());

				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					logger.error(e);
				}
			}
		};

		ThreadManager.getInstance().runInEdt(sw, "loading product image");
	}


	public void showCard(MagicCard mc)
	{
		if(mc == null)
			return;

		if (!mc.isDoubleFaced())
		{
			imgBack = getEnabledPlugin(MTGPictureProvider.class).getBackPicture(mc);
		}
		else
		{
			try {
				MagicCard rcard =mc.getRotatedCard();
				imgBack = getEnabledPlugin(MTGPictureProvider.class).getPicture(rcard);
			} catch (Exception e) {
				logger.error("error loading rotated card : {}",mc.getRotatedCard(),e);

			}
		}

		ThreadManager.getInstance().executeThread(new MTGRunnable() {

			@Override
			protected void auditedRun() {
				try {
					imgFront = getEnabledPlugin(MTGPictureProvider.class).getPicture(mc);

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

			}


		}, "show img for " + mc);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		ImageTools.initGraphics(g2);

		if (printed == null)
			return;


		var pX = (int) ((getWidth() - (printed.getWidth() * xScale)) / 2);
		var pY = (getHeight() - printed.getHeight()) / 2;



		if (xScale < 0)
			printed = imgBack;
		else
			printed = imgFront;


		var at = new AffineTransform();
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


	public double getZoomFactor() {
		return zoomFactor;
	}

	public void setZoomFactor(double zoomFactor) {
		this.zoomFactor = zoomFactor;
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

			var curPoint = e.getPoint();
		        xDiff = (double)curPoint.x - startPoint.x;
		        yDiff = (double)curPoint.y - startPoint.y;
		        repaint();
		}



	}

}