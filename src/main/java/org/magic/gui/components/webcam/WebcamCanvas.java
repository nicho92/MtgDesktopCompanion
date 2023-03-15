package org.magic.gui.components.webcam;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.apache.logging.log4j.Logger;
import org.magic.gui.abstracts.AbstractRecognitionArea;
import org.magic.services.logging.MTGLogger;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamLockException;

public class WebcamCanvas extends JPanel
{
	private static final long serialVersionUID = 1L;
	private transient Webcam webcam;
	private Canvas canvas;
	private transient  BufferedImage lastDrawn;
	private transient  BufferedImage buf;
	private transient  AbstractRecognitionArea strat;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public WebcamCanvas(Webcam w, AbstractRecognitionArea s) {
		super();
		webcam = w;
		strat = s;
		canvas = new Canvas();
		setSize(w.getViewSize());
		canvas.setPreferredSize(w.getViewSize());
		setBackground(Color.BLACK);
		add(canvas);
		canvas.addMouseListener(strat);
		canvas.addMouseMotionListener(strat);

		if(strat!=null)
			strat.init(webcam.getViewSize().width, webcam.getViewSize().height);
	}





	public void setWebcam(Webcam w)
	{
		webcam = w;
		try {
			webcam.setViewSize(webcam.getDevice().getResolutions()[webcam.getDevice().getResolutions().length - 1]);
		} catch (Exception e) {
			logger.error(e);
		}
		setSize(w.getViewSize());
		canvas.setSize(w.getViewSize());

		if(strat!=null)
			strat.init(webcam.getViewSize().width, webcam.getViewSize().height);

		canvas.setPreferredSize(new Dimension(webcam.getViewSize().width, webcam.getViewSize().height));
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

		strat.init(webcam.getViewSize().width, webcam.getViewSize().height);
	}

	public Webcam getWebcam() {
		return webcam;
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
		if(!webcam.isOpen())
		{
			try {
				webcam.open();
				logger.debug("webcam open = {}", webcam.isOpen());

			}catch(WebcamLockException e)
			{
				logger.error("{} is locked",webcam);
			}
		}

		if(!webcam.isOpen())
		{
			logger.warn("cam is not opened");
			return;
		}



		lastDrawn = webcam.getImage();

		if(lastDrawn==null)
		{
			logger.warn("Lastdrawn is null with {}",webcam);
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
		boolean ret = webcam.close();
		logger.debug("{} is closed :{} ",webcam,ret);

	}

}
