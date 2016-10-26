package org.magic.gui.game;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.magic.api.beans.MagicCard;
import org.magic.game.PositionEnum;
import org.magic.gui.game.actions.cards.AttachActions;
import org.magic.gui.game.actions.cards.ChangeCreaturePTActions;
import org.magic.gui.game.actions.cards.EmblemActions;
import org.magic.gui.game.actions.cards.FlipActions;
import org.magic.gui.game.actions.cards.LoyaltyActions;
import org.magic.gui.game.actions.cards.RotateActions;
import org.magic.gui.game.actions.cards.SelectionActions;
import org.magic.gui.game.actions.cards.TapActions;
import org.magic.gui.game.actions.cards.TokensActions;
import org.magic.gui.game.actions.cards.TransferActions;
import org.magic.gui.game.actions.cards.TransformActions;
import org.magic.gui.game.transfert.CardTransfertHandler;
import org.magic.services.CockatriceTokenProvider;
import org.magic.services.MagicFactory;


public class DisplayableCard extends JLabel implements Draggable
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
	private boolean showPT; 
	private List<DisplayableCard> attachedCards;
	
	
	@Override
	public String toString() {
		return String.valueOf(magicCard);
	}
	
	public List<DisplayableCard> getAttachedCards()
	{
		return attachedCards;
	}
	
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
	
	@Override
	public Icon getIcon() {
		if(magicCard != null)
			return image;
		
		return super.getIcon();
	}
	
	public void setImage(ImageIcon image) {
		this.image = image;
		repaint();
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


	public void showPT(boolean t)
	{
		showPT=t;
	}
	
	public DisplayableCard(MagicCard mc,int width,int height, boolean activateCards) {
	
		attachedCards = new ArrayList<DisplayableCard>();    
		setSize(width,height);
		setHorizontalAlignment(JLabel.CENTER);
		setVerticalAlignment(JLabel.CENTER);
		setMagicCard(mc);
		setTransferHandler(new CardTransfertHandler());
				
		if(activateCards)
		{ 
			initActions();
		}
		
		
	}
	
	public void initActions() {
		
		menu.removeAll();
		
		
		addMouseListener(new TransferActions());
		
		menu.add(new JMenuItem(new TapActions(this)));
		menu.add(new JMenuItem(new SelectionActions(this)));
		menu.add(new JMenuItem(new RotateActions(this)));
	
		if(magicCard.getTypes().contains("Creature"))
		{
			
			JMenu mnuModifier = new JMenu("P/T");
						
			mnuModifier.add(new ChangeCreaturePTActions(this, 1, ChangeCreaturePTActions.TypeCounter.Strength));
			mnuModifier.add(new ChangeCreaturePTActions(this, -1, ChangeCreaturePTActions.TypeCounter.Strength));
			mnuModifier.add(new ChangeCreaturePTActions(this, 1, ChangeCreaturePTActions.TypeCounter.Toughness));
			mnuModifier.add(new ChangeCreaturePTActions(this, -1, ChangeCreaturePTActions.TypeCounter.Toughness));
			mnuModifier.add(new ChangeCreaturePTActions(this, 0, ChangeCreaturePTActions.TypeCounter.Both));

			menu.add(mnuModifier);
		}
		
		if(magicCard.getTypes().contains("Planeswalker"))
		{
			JMenu mnuModifier = new JMenu("Loyalty");
			
			mnuModifier.add(new LoyaltyActions(this, 1));
			mnuModifier.add(new LoyaltyActions(this, -1));
			menu.add(mnuModifier);
			
		}
		
		if(magicCard.getSubtypes().contains("Aura")||magicCard.getSubtypes().contains("Equipment"))
		{
			menu.add(new AttachActions(this));
		}
		
		if(magicCard.isTranformable())
			menu.add(new JMenuItem(new TransformActions(this)));

		if(magicCard.isFlippable())
			menu.add(new JMenuItem(new FlipActions(this)));

		
		if(GamePanelGUI.getInstance().getTokenGenerator().isTokenizer(magicCard))
			menu.add(new JMenuItem(new TokensActions(this)));
		
		if(GamePanelGUI.getInstance().getTokenGenerator().isEmblemizer(magicCard))
			menu.add(new JMenuItem(new EmblemActions(this)));
		
		
		setComponentPopupMenu(menu);
		
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

	public MagicCard getMagicCard() {
		return magicCard;
	}
	
	public void setMagicCard(MagicCard mc) {
		this.magicCard = mc;
		try {
			if(mc.getLayout().equals(MagicCard.LAYOUT.Token.toString()) || mc.getLayout().equals(MagicCard.LAYOUT.Emblem.toString()))
			{
				fullResPics = new CockatriceTokenProvider().getTokenPics(mc);
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

	public void setRotated(boolean b) {
		this.rotated=b;
		
	}

	@Override
	public void moveCard(MagicCard mc, PositionEnum to) {
		System.out.println(mc +" " + to);
	}
		
	

	@Override
	public void addComponent(DisplayableCard i) {
		System.out.println("merge " + this + " " + i);
		
	}

	@Override
	public PositionEnum getOrigine() {
		return PositionEnum.CARD;
	}


}
