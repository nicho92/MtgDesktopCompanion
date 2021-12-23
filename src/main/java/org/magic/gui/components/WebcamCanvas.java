package org.magic.gui.components;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.magic.gui.abstracts.AbstractRecognitionArea;
import org.magic.services.MTGLogger;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamLockException;

public class WebcamCanvas extends JPanel 
{
	private static final long serialVersionUID = 1L;
	private transient Webcam cam;
	private Canvas canvas;
	private transient  BufferedImage lastDrawn;
	private transient  BufferedImage buf;
	private transient  AbstractRecognitionArea strat;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public WebcamCanvas(Webcam w, AbstractRecognitionArea s) {
		super();
		cam = w;
		strat = s;
		canvas = new Canvas();
		setSize(w.getViewSize());
		canvas.setPreferredSize(w.getViewSize());
		setBackground(Color.BLACK);
		add(canvas);
		canvas.addMouseListener(strat);
		canvas.addMouseMotionListener(strat);
		
		if(strat!=null)
			strat.init(cam.getViewSize().width, cam.getViewSize().height);
	}


	public void setWebcam(Webcam w)
	{
		cam = w;
		try {
			cam.setViewSize(cam.getDevice().getResolutions()[cam.getDevice().getResolutions().length - 1]);
		} catch (Exception e) {
			logger.error(e);
		}
		setSize(w.getViewSize());
		canvas.setSize(w.getViewSize());
		
		if(strat!=null)
			strat.init(cam.getViewSize().width, cam.getViewSize().height);
		
		canvas.setPreferredSize(new Dimension(cam.getViewSize().width, cam.getViewSize().height));
		add(canvas);
	}

	public void setAreaStrat(AbstractRecognitionArea s)
	{
		if(s==null)
			return;
		
		strat = s;
		canvas.removeMouseListener(strat);
		canvas.removeMouseMotionListener(strat);
		canvas.addMouseListener(strat);
		canvas.addMouseMotionListener(strat);
		
		strat.init(cam.getViewSize().width, cam.getViewSize().height);
	}

	public Webcam getWebcam() {
		return cam;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public BufferedImage lastDrawn()
	{
		return lastDrawn;
	}
	
	public AbstractRecognitionArea getAreaRecognitionStrategy() {
		return strat;
	}

	public void draw()
	{
		if(!cam.isOpen())
		{
			try {
				cam.open();
			}catch(WebcamLockException e)
			{
				logger.error(cam + " is locked");
			}
		}
		
		lastDrawn = cam.getImage();
		
		if(lastDrawn==null)
		{
			logger.warn("Lastdrawn is null with " + cam);
			return;
		}
		
		if(buf == null || buf.getHeight() != lastDrawn.getHeight() || buf.getWidth() != lastDrawn.getWidth())
		{
			buf = new BufferedImage(lastDrawn.getWidth(),lastDrawn.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		}
		var gi = canvas.getGraphics();
		var g = buf.getGraphics();
		g.drawImage(lastDrawn, 0, 0, null);
		
		if(strat!=null)
			strat.draw(g);
		
		
		if(gi!=null)
			gi.drawImage(buf, 0, 0, null);
	}

	public void close()
	{
		boolean ret = cam.close();
		logger.debug(cam + " is closed : " + ret);
		
	}

}
