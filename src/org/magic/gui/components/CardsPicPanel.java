package org.magic.gui.components;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.graphics.ReflectionRenderer;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.util.PaintUtils;



public class CardsPicPanel extends JXPanel {
	
	
	
	private static final long serialVersionUID = 1L;

	private BufferedImage image=null;
	private Shape selectedShape = null;
	private ReflectionRenderer renderer;
	private Point pointInitial = null;
	private boolean isCtrlPressed = false;

	private boolean moveable=true;

	public void setMoveable(boolean bool)
	{
		this.moveable=bool;
	}
	  
	public CardsPicPanel()
	{
		initGUI();
	}
	
//	public void showImage(Image i)
//	{
//		image=(BufferedImage) i;
//		image=renderer.appendReflection(image);
//		int w = getWidth();
//	    int h = getHeight();
//	    int x = (w - image.getWidth())/2;
//	    int y = (h - image.getHeight())/2;
//		
//	    selectedShape= new Rectangle2D.Double(x, y, image.getWidth(null),  image.getHeight(null));
//	    repaint();
//	}
	
	public void showPhoto(final URL photo) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					image = ImageIO.read(photo);
					
					image=renderer.appendReflection(image);
					int w = getWidth();
				    int h = getHeight();
				    int x = (w - image.getWidth())/2;
				    int y = (h - image.getHeight())/2;
					
				    selectedShape= new Rectangle2D.Double(x, y, image.getWidth(null),  image.getHeight(null));
				  
					
				} catch (Exception e) {
					e.printStackTrace();
				} 
				repaint();
				
			}
		}).start();
	}

	
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setStroke(new BasicStroke(0));
		
		if(image !=null)
		{
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0));   
			g2.draw(selectedShape);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
			g2.drawImage(image, (int)selectedShape.getBounds().getX(), 
  		  			  (int)selectedShape.getBounds().getY(),
  		  			  (int)selectedShape.getBounds().getWidth(),
  		  			  (int)selectedShape.getBounds().getHeight(),
//  		  			  (int)selectedShape.getBounds(),
//  		  			  (int)selectedShape.getBounds().getHeight(),
//  		  			  (int)selectedShape.getBounds().getHeight(), 
//  		  			  (int)selectedShape.getBounds().getHeight(),
  		  			  null);    
			
		}
	}

	private void initGUI() {
		renderer = new ReflectionRenderer();
		setBackgroundPainter(new MattePainter(PaintUtils.NIGHT_GRAY,true));

	    GestionnaireEvenements interactionManager = new GestionnaireEvenements(this); 
		    this.addMouseListener(interactionManager);
		    this.addMouseMotionListener(interactionManager);
		    this.addMouseWheelListener(interactionManager);
		    this.addKeyListener(interactionManager);
		    this.setFocusable(true);
		    
	}

	private class GestionnaireEvenements extends MouseAdapter implements KeyListener 
	  {
			public JXPanel mainPanel;
		
			public GestionnaireEvenements(JXPanel panel)
			{
					this.mainPanel = panel;
			}
		
		
		 	public void mouseWheelMoved(MouseWheelEvent e) {
					double quotien = 1.1;
					
			          if (selectedShape.contains(e.getPoint()))
			          {
			            if(e.getWheelRotation()==-1)//zoom
			            {
			            	selectedShape= new Rectangle2D.Double((int)selectedShape.getBounds().getX(),(int) selectedShape.getBounds().getY(), (int)selectedShape.getBounds().getWidth()*quotien, (int)selectedShape.getBounds().getHeight()*quotien);
			            }
			            else
			            {
			            	selectedShape= new Rectangle2D.Double((int)selectedShape.getBounds().getX(),(int) selectedShape.getBounds().getY(), (int)selectedShape.getBounds().getWidth()/quotien, (int)selectedShape.getBounds().getHeight()/quotien);
			            }
			            mainPanel.repaint();
			          }
			}
			
			public void mousePressed(MouseEvent e)
		    {
			  if(moveable)
		      if (selectedShape.contains(e.getPoint()))
		      {
		            pointInitial = e.getPoint();
		            mainPanel.repaint();
		      }
		    }
		  
			public void mouseReleased(MouseEvent e){    }
		    
		    public void mouseDragged(MouseEvent e) {
		    	
		    	if(isCtrlPressed)
			    {
			    	System.out.println("rotation " + Math.PI);
			    }
		    	if(moveable) 
		    	if (selectedShape != null) {
		           int deltaX = e.getX() - pointInitial.x;
		           int deltaY = e.getY() - pointInitial.y;
		           pointInitial = e.getPoint();
		           AffineTransform at = AffineTransform.getTranslateInstance(deltaX,deltaY);
		           selectedShape = at.createTransformedShape(selectedShape);
		           mainPanel.repaint();
		        }
		        
		        
		        
		     }
		
			public void keyPressed(KeyEvent e) {
				
				if(e.getKeyCode()==KeyEvent.VK_CONTROL)
					isCtrlPressed=true;
			}
		
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_CONTROL)
					isCtrlPressed=false;
			}
		
			public void keyTyped(KeyEvent e) {	}
		    
	    }
}