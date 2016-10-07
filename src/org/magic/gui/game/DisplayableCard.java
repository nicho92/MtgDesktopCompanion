package org.magic.gui.game;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.magic.api.beans.MagicCard;
import org.magic.gui.game.actions.SelectionActions;
import org.magic.gui.game.actions.FlipActions;
import org.magic.gui.game.actions.TapActions;
import org.magic.gui.game.actions.TransformActions;
import org.magic.gui.game.transfert.CardTransfertHandler;
import org.magic.services.CockatriceTokenProvider;
import org.magic.services.MagicFactory;


public class DisplayableCard extends JLabel
{

	JPopupMenu menu = new JPopupMenu();
	private MagicCard magicCard;
	
	private boolean tapped=false;
	private ImageIcon image;
	private boolean draggable=true;
	private boolean tappable=true;
	private String title;
	private String bottom;
	private boolean selected;
	private boolean rotated; 
	
	
	public boolean isRotated(){
		return rotated;
	}
	
	public boolean isTappable() {
		return tappable;
	}
	

	public void setTappable(boolean tappable) {
		this.tappable = tappable;
	}


	public boolean isSelected() {
		return selected;
	}


	public void setSelected(boolean selected) {
		this.selected = selected;
	}


	public ImageIcon getImageIcon() {
		return image;
	}


	public void setImage(ImageIcon image) {
		this.image = image;
	}
	
	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
		setText(title);
	}


	public String getBottom() {
		return bottom;
	}


	public void setBottom(String bottom) {
		this.bottom = bottom;
	}


	public boolean isDraggable() {
		return draggable;
	}


	public void enableDrag(boolean drag) {
		this.draggable = drag;
	}


	public DisplayableCard(MagicCard mc,int width,int height, boolean activateCards) {
		
		setSize(width, height);
		setHorizontalAlignment(JLabel.CENTER);
		setVerticalAlignment(JLabel.CENTER);
		setMagicCard(mc);
		setTransferHandler(new CardTransfertHandler());
			
		if(activateCards)
		{ 
			addMouseListener(new SelectionActions());
			addMouseMotionListener(new SelectionActions());
			
			menu.add(new JMenuItem(new TapActions("Tap","tap the card",KeyEvent.VK_T,this)));
			
			if(magicCard.isTranformable())
				menu.add(new JMenuItem(new TransformActions("Transform","Transform the card",KeyEvent.VK_A,this)));

			if(magicCard.isFlippable())
				menu.add(new JMenuItem(new FlipActions("Flip","Flip the card",KeyEvent.VK_F,this)));

			
			setComponentPopupMenu(menu);
		}
		
		
	}
	
	public void flip(boolean t)
	{
		
		MagicCard mc;
		try {
			mc = MagicFactory.getInstance().getEnabledProviders().searchCardByCriteria("name", getMagicCard().getRotatedCardName(), getMagicCard().getEditions().get(0)).get(0);
			setMagicCard(mc);
	        BufferedImage bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
	    		
			AffineTransform tx = AffineTransform.getScaleInstance(-1, -1);
		    tx.translate(-bufferedImage.getWidth(null), -bufferedImage.getHeight(null));
		    AffineTransformOp op = new AffineTransformOp(tx,AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		    bufferedImage = op.filter(bufferedImage, null);
			
	        Graphics2D g2 = bufferedImage.createGraphics();
			           g2.drawImage(image.getImage(), tx,null);
			           g2.dispose();
	        setImage(new ImageIcon(bufferedImage));
	        
	        rotated=true;
	        
			revalidate();
			repaint();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void transform()
	{
		try {
			MagicCard mc = MagicFactory.getInstance().getEnabledProviders().searchCardByCriteria("name", getMagicCard().getRotatedCardName(), getMagicCard().getEditions().get(0)).get(0);
			setMagicCard(mc);
			revalidate();
			repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void tap(boolean t) {
			
			if(!tappable)
				return;
		
			int angle=0;
			if(t)
				angle=90;
			else
				angle=-90;
		
	        int w = getWidth();
	        int h = getHeight();
	        int type = BufferedImage.TYPE_INT_RGB;  // other options, see api
	        BufferedImage bfImage = new BufferedImage(h, w, type);
	        Graphics2D g2 = bfImage.createGraphics();
	        double x = (h - w)/2.0;
	        double y = (w - h)/2.0;
	        AffineTransform at = AffineTransform.getTranslateInstance(x, y);
	        at.rotate(Math.toRadians(angle), w/2.0, h/2.0);
	        g2.drawImage(getImageIcon().getImage(), at,null);
	        g2.dispose();
	        this.image=new ImageIcon((Image)bfImage);
	        this.setSize(h, w);
	        this.tapped=t;
	}
	
	@Override
	public Icon getIcon() {
		
		if(magicCard != null)
			return image;
		
		return super.getIcon();
	}
	
	public MagicCard getMagicCard() {
		return magicCard;
	}
	
	public void setMagicCard(MagicCard mc) {
		this.magicCard = mc;
		try {
			if(mc.getLayout().equals(MagicCard.LAYOUT.Token.toString()))
			{
				fullResPics = new CockatriceTokenProvider().getToken(mc);
				image = new ImageIcon(fullResPics.getScaledInstance(getWidth(), getHeight(), Image.SCALE_FAST));
			}
			else if (mc.getLayout().equals(MagicCard.LAYOUT.Emblem.toString()))
			{
				fullResPics = new CockatriceTokenProvider().getEmblem(mc);
				image = new ImageIcon(fullResPics.getScaledInstance(getWidth(), getHeight(), Image.SCALE_FAST));
			}
			else
			{
				fullResPics = MagicFactory.getInstance().getEnabledPicturesProvider().getPicture(mc,null);
				image = new ImageIcon(fullResPics.getScaledInstance(getWidth(), getHeight(), Image.SCALE_FAST));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isTapped() {
		return tapped;
	}
	public void setTapped(boolean tapped) {
		this.tapped = tapped;
	}

	private Image fullResPics;
	
	public Image getFullResPics() {
		return fullResPics;
	}


}
