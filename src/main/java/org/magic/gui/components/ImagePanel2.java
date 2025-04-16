package org.magic.gui.components;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.Timer;

import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.graphics.ReflectionRenderer;
import org.jdesktop.swingx.painter.MattePainter;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.enums.EnumLayout;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.services.network.URLTools;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.ImageTools;

public class ImagePanel2 extends JXPanel {
	
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	private double scale = 1.0;
	private double translateX = 0;
	private double translateY = 0;
	private double lastMouseX;
	private double lastMouseY;
	private boolean dragging = false;
	private boolean rotating = false;
	private double rotationX = 1.0;
	private BufferedImage print;
	private BufferedImage front;
	private BufferedImage back;
	private boolean reflexion;
	private ReflectionRenderer renderer;

	
	public ImagePanel2() {
		this(true,true,true,true);
	}
	
	
	public ImagePanel2(boolean moveable, boolean rotable, boolean zoomable, boolean reflexion) {

		setBackgroundPainter(new MattePainter(MTGConstants.PICTURE_PAINTER, true));
		
		renderer = new ReflectionRenderer();
		
		this.reflexion=reflexion;
		
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				lastMouseX = e.getX();
				lastMouseY = e.getY();
				dragging = true;
			}
			
			public void mouseReleased(MouseEvent e) {
				dragging = false;
				MTGControler.getInstance().setProperty("/card-pictures-dimension/x",e.getPoint().getX());
				MTGControler.getInstance().setProperty("/card-pictures-dimension/y",e.getPoint().getY());
			}
			
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && !rotating && rotable) {
					animateRotation();
				}
			}
		});
		
		if (zoomable)
			addMouseWheelListener(e -> {
				double delta = 0.1 * e.getPreciseWheelRotation();
				scale = Math.max(0.1, scale - delta);
				MTGControler.getInstance().setProperty("/card-pictures-dimension/zoom",scale);
				repaint();
			});
		
		
		if (moveable)
			addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					if (dragging) {
						double dx = e.getX() - lastMouseX;
						double dy = e.getY() - lastMouseY;
						translateX += dx;
						translateY += dy;

					lastMouseX = e.getX();

					lastMouseY = e.getY();
						repaint();
					}
				}
			});
	}
	
	
	public void setImg(BufferedImage img)
    {
			if(img==null)
				return;

	   		back=img;
	   		front=img;
	   		print=img;


	   		if(reflexion) {
				front = renderer.appendReflection(front);
				back = renderer.appendReflection(ImageTools.mirroring(back));
			}

	   		repaint();
    }
	
	
	public void init(MTGCard mc)
	{
		if(mc == null)
			return;

		ThreadManager.getInstance().executeThread(new MTGRunnable() {
			@Override
			protected void auditedRun() {
				try {
					
					front = getEnabledPlugin(MTGPictureProvider.class).getPicture(mc);
					
					if (mc.isDoubleFaced())
						back = getEnabledPlugin(MTGPictureProvider.class).getPicture(mc.getRotatedCard());
					else
						back = getEnabledPlugin(MTGPictureProvider.class).getBackPicture(mc);
					
					if(mc.isFlippable())
						back = ImageTools.rotate(front, 180);

					if(mc.getLayout()==EnumLayout.SPLIT)
					{
						front = ImageTools.rotate(front, 90);
						back= ImageTools.rotate(back, 90);
					}

					
					if (reflexion) {
						front = renderer.appendReflection(front);
						back = renderer.appendReflection(back);
					}

			 
					print = front;
				} catch (Exception e) {
					front = back;
				}

				repaint();

			}


		}, "show img for " + mc);
	}
	
	
	public void loadImage(String url) throws IOException {

		ThreadManager.getInstance().runInEdt(new SwingWorker<BufferedImage, Void>() {
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
		}, "loading product image");
	}
	
	
	
	private void animateRotation() {
		rotating = true;
		Timer timer = new Timer(MTGConstants.ROTATED_TIMEOUT, null);
		final int[] frame = { 0 };
		final double totalFrames = 75;
		timer.addActionListener(_ -> {
			frame[0]++;
			double progress = frame[0] / totalFrames;
			rotationX = Math.cos(progress * Math.PI); 
			if (progress >= 0.5) {
				print = ImageTools.mirroring(back);
			}
			repaint();
			if (frame[0] >= totalFrames) {
				rotationX = 1.0;
				rotating = false;
				timer.stop();
				print = back;
				back = front;
				front = print;
			}

	});
		timer.start();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		if (print == null)
			return;
		
		
		var g2 = (Graphics2D) g;
		ImageTools.initGraphics(g2);
		
		
		var transform = new AffineTransform();
			transform.translate(getWidth() / 2.0, getHeight() / 2.0);
			transform.translate(translateX, translateY);
			transform.scale(scale * rotationX, scale);
			transform.translate(-print.getWidth() / 2.0, -print.getHeight() / 2.0);
		
			g2.drawImage(print, transform,null);
			
	}
}