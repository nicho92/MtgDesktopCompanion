package org.utils.webcam;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Random;

import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;
import org.magic.services.tools.ImageTools;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDevice;


public class SimulatedWebcam extends Webcam {

    public SimulatedWebcam() {
    	super(new DummyWebcamDevice(MTGConstants.SAMPLE_PIC));
    }


    public void setImageUrl(URL u)
    {
    	((DummyWebcamDevice)getDevice()).setURL(u);
    }


    public void setImageUrl(File u)
    {
    	((DummyWebcamDevice)getDevice()).setFile(u);
    }

}

class DummyWebcamDevice implements WebcamDevice
{

    private BufferedImage buffer;
    private BufferedImage display;
    private Graphics2D g;
    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;

    private static final int BOUNCE_SIZE = 50;

    private static final double MAX_VEL = 100;

    private Image bounce;
    private double bounceX = (double)WIDTH / 2;
    private double bounceY = (double)HEIGHT / 2;

    private double xvel;
    private double yvel;

    private long time;

    private Dimension[] dimensions;
    private boolean open;

	public DummyWebcamDevice(Image ic) {
		bounce=ic;
		init();
	}


    private void init()
    {
    	  time = System.currentTimeMillis();
          buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
          display = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
          g = buffer.createGraphics();
          Random  r = new SecureRandom();
          xvel = (r.nextDouble()-0.5)*MAX_VEL*2;
          yvel = (r.nextDouble()-0.5)*MAX_VEL*2;
          dimensions = new Dimension[1];
          dimensions[0] = new Dimension(display.getWidth(),display.getHeight());
          open = false;
    }

	public void setFile(File f)
    {
    	 try {
             bounce = ImageTools.read(f);
         } catch (IOException _) {
             bounce = null;
         }
    }

    public void setURL(URL f)
    {
    	 try {
             bounce = URLTools.extractAsImage(f.toString());
         } catch (IOException _) {
             bounce = null;
         }
    }

    @Override
	public String getName()
    {
        return "Simulated Webcam";
    }

    @Override
    public Dimension getResolution()
    {
        return dimensions[0];
    }

    @Override
    public Dimension[] getResolutions()
    {
        return dimensions;
    }

    @Override
    public void setResolution(Dimension size)
    {
       //do nothing
    }

    @Override
    public void open()
    {
        open = true;
    }

    @Override
    public void close()
    {
        open = false;
    }

    private void draw()
    {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(bounce, (int)bounceX-BOUNCE_SIZE, (int)bounceY-BOUNCE_SIZE, null);

        display.getGraphics().drawImage(buffer, 0, 0, null);
    }

    private void update()
    {
        long now = System.currentTimeMillis();
        double delta = (now-time)/1000.0;
        time = now;
        bounceX += xvel*delta;
        bounceY += yvel*delta;
        if(bounceX > WIDTH - BOUNCE_SIZE)
        {
            bounceX = WIDTH-(double)BOUNCE_SIZE;
            xvel = -xvel;
        }
        if(bounceX < BOUNCE_SIZE)
        {
            bounceX = BOUNCE_SIZE;
            xvel = -xvel;
        }
        if(bounceY > HEIGHT - BOUNCE_SIZE)
        {
            bounceY = HEIGHT-(double)BOUNCE_SIZE;
            yvel = -yvel;
        }
        if(bounceY < BOUNCE_SIZE)
        {
            bounceY = BOUNCE_SIZE;
            yvel = -yvel;
        }
    }

    @Override
    public BufferedImage getImage()
    {
        draw();
        update();
        return display;
    }

    @Override
    public void dispose() {
       //do nothing
    }

    @Override
    public boolean isOpen() {
        return open;
    }
}