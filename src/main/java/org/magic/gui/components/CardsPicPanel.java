package org.magic.gui.components;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
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

public class CardsPicPanel extends JXPanel {

	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	private transient BufferedImage imgFront = null;
	private transient BufferedImage back;
	private transient Shape selectedShape = null;
	private transient ReflectionRenderer renderer;
	private Point pointInitial = null;
	private transient BufferedImage printed;
	private float xScale = 1f;
	private float xDelta = 0.05f;
	boolean launched = false;
	private Timer timer;
	int pX;
	int pY;
	double rotate;
	private boolean moveable = true;
	private MagicCard card;

	private boolean debug=true;
	
	
	public CardsPicPanel() {
		setLayout(new BorderLayout(0, 0));
		initGUI();
	}

	public void showCard(MagicCard mc) {
		showCard(mc, null);
	}
	
	public void setImg(BufferedImage img)
    {
			if(img==null)
				return;
		
	   		back = getEnabledPlugin(MTGPictureProvider.class).getBackPicture();
	   		printed=img;
	   		imgFront=img;
    		selectedShape = new Rectangle2D.Double(15, 15, img.getWidth(null), img.getHeight(null));
    		card=new MagicCard();
    		card.setLayout(MTGLayout.NORMAL);
    }
	

	public void showCard(MagicCard mc, MagicEdition edition) {

		this.card = mc;

		
		if(card == null)
			return;
		
		if (!mc.isDoubleFaced()) 
		{
			back = getEnabledPlugin(MTGPictureProvider.class).getBackPicture();
		} 
		else 
		{
			try {
				MagicCard flipC =card.getRotatedCard();
				back = getEnabledPlugin(MTGPictureProvider.class).getPicture(flipC, null);
			} catch (Exception e) {
				logger.error("error loading flip",e);

			}
		}
		ThreadManager.getInstance().executeThread(() -> {
			try {
				if (edition == null)
					imgFront = renderer.appendReflection(getEnabledPlugin(MTGPictureProvider.class).getPicture(card, null));
				else
					imgFront = renderer.appendReflection(getEnabledPlugin(MTGPictureProvider.class).getPicture(card, edition));

				back = ImageTools.mirroring(back);
				back = renderer.appendReflection(back);

				printed = imgFront;
				
				selectedShape = new Rectangle2D.Double(15, 15, printed.getWidth(null), printed.getHeight(null));

			} catch (Exception e) {
				imgFront = back;
			}
			repaint();
		}, "showPhoto");
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setStroke(new BasicStroke(0));

		if (printed != null) {

			pX = (int) ((getWidth() - (printed.getWidth() * xScale)) / 2);
			pY = (getHeight() - printed.getHeight()) / 2;

			
			if(debug)
			{
				g2.setColor(Color.red);
				g2.fillOval(pX, pY, 10, 10);
				
				g2.draw(selectedShape);
				
			}
			
			AffineTransform at = new AffineTransform();
			at.translate(pX, pY);
			at.scale(xScale, 1);
			g2.setTransform(at);

			
			
			if (card.isFlippable())
				g2.rotate(Math.toRadians(rotate));

			if (xScale < 0)
				printed = back;
			else
				printed = imgFront;

			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0));
			g2.draw(selectedShape);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

			g2.drawImage(printed, (int) selectedShape.getBounds().getX(), (int) selectedShape.getBounds().getY(),(int) selectedShape.getBounds().getWidth(), (int) selectedShape.getBounds().getHeight(), null);

			g2.dispose();
		}

	}

	int loop = 0;

	private void initGUI() {
		renderer = new ReflectionRenderer();
		setBackgroundPainter(new MattePainter(MTGConstants.PICTURE_PAINTER, true));

		GestionnaireEvenements interactionManager = new GestionnaireEvenements(this);
		this.addMouseListener(interactionManager);
		this.addMouseMotionListener(interactionManager);
		this.addMouseWheelListener(interactionManager);

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
		private JXPanel mainPanel;

		public GestionnaireEvenements(JXPanel panel) {
			this.mainPanel = panel;
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			double quotien = 1.1;
			
			if(selectedShape==null)
				return;
			
		//	if (selectedShape.contains(e.getPoint())) {
				if (e.getWheelRotation() == -1)// zoom
				{
					selectedShape = new Rectangle2D.Double((int) selectedShape.getBounds().getX(),
							(int) selectedShape.getBounds().getY(),
							(int) selectedShape.getBounds().getWidth() * quotien,
							(int) selectedShape.getBounds().getHeight() * quotien);
				} else {
					selectedShape = new Rectangle2D.Double((int) selectedShape.getBounds().getX(),
							(int) selectedShape.getBounds().getY(),
							(int) selectedShape.getBounds().getWidth() / quotien,
							(int) selectedShape.getBounds().getHeight() / quotien);
				}
				mainPanel.repaint();
		//	}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if(selectedShape==null)
				return;
			
			if (!launched) {
				timer.start();
				launched = true;
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if(selectedShape==null)
				return;
			
			if (selectedShape.contains(e.getPoint())) {
				pointInitial = e.getPoint();
				mainPanel.repaint();
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if(selectedShape==null)
				return;
			
			if (moveable) 
			{
				int deltaX = e.getX() - pointInitial.x;
				int deltaY = e.getY() - pointInitial.y;
				pointInitial = e.getPoint();
				AffineTransform at = AffineTransform.getTranslateInstance(deltaX, deltaY);
				selectedShape = at.createTransformedShape(selectedShape);
				mainPanel.repaint();
			}
		}

	}
}