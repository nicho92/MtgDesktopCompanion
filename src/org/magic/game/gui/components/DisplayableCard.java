package org.magic.game.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import org.magic.api.beans.MTGKeyWord;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MTGKeyWord.EVENT;
import org.magic.api.beans.MTGKeyWord.TYPE;
import org.magic.game.actions.cards.AttachActions;
import org.magic.game.actions.cards.BonusCounterActions;
import org.magic.game.actions.cards.EmblemActions;
import org.magic.game.actions.cards.FixCreaturePowerActions;
import org.magic.game.actions.cards.FlipActions;
import org.magic.game.actions.cards.ItemCounterActions;
import org.magic.game.actions.cards.LoyaltyActions;
import org.magic.game.actions.cards.RemoveCounterActions;
import org.magic.game.actions.cards.RotateActions;
import org.magic.game.actions.cards.SelectionActions;
import org.magic.game.actions.cards.TapActions;
import org.magic.game.actions.cards.TokensActions;
import org.magic.game.actions.cards.TransferActions;
import org.magic.game.actions.cards.TransformActions;
import org.magic.game.model.GameManager;
import org.magic.game.model.PositionEnum;
import org.magic.game.model.Turn.PHASES;
import org.magic.game.model.counters.AbstractCounter;
import org.magic.game.model.counters.BonusCounter;
import org.magic.game.model.counters.ItemCounter;
import org.magic.game.model.counters.LoyaltyCounter;
import org.magic.game.transfert.CardTransfertHandler;
import org.magic.services.CockatriceTokenProvider;
import org.magic.services.KeyWordManager;
import org.magic.services.MTGControler;


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
	private boolean selected=false;
	private boolean rotated;
	private boolean showPT; 
	private List<DisplayableCard> attachedCards;
	private List<AbstractCounter> counters;
	private Image fullResPics;
	private boolean showLoyalty;
	private KeyWordManager keywordsManager;
	
	public void addCounter(AbstractCounter c)
	{
		counters.add(c);
		c.apply(this);
		initActions();
	}
	
	public void removeCounter(AbstractCounter c)
	{
		counters.remove(c);
		c.remove(this);
		initActions();
	}
	
	
	@Override
	public Border getBorder() {
		 if(isSelected())
		  return new LineBorder(Color.RED);
		 else
			 return null;
		 
		
	}

	public void removeAllCounters() {
		for(AbstractCounter c : counters)
		{
			c.remove(this);
		}
		counters.clear();
		initActions();
		
	}
	
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
	
	
	public Icon toIcon() {
		if(magicCard != null)
			return image;
		
		return super.getIcon();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		if(image != null)
		{
			g.drawImage(image.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);

			if(showPT)
				drawString(g, magicCard.getPower()+"/"+magicCard.getToughness(), Color.BLACK, Color.WHITE, this.getWidth()-33, this.getHeight()-10);
			
			if(showLoyalty)
				drawString(g, ""+magicCard.getLoyalty(), Color.BLACK, Color.WHITE, this.getWidth()-23, this.getHeight()-15);
			
			validate();
		}
		//super.paint(g);
	}
	
	private void drawString(Graphics g, String s,Color background,Color foreground,int x,int y)
	{
		g.setFont(new Font("default", Font.BOLD, 12));
		g.setColor(background);
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D rect = fm.getStringBounds(s, g);
		g.fillRect(x,y - fm.getAscent(),(int) rect.getWidth(),(int) rect.getHeight());g.setColor(foreground);
		g.drawString(s,x,y);
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
		counters = new ArrayList<AbstractCounter>();
		setSize(width,height);
		setPreferredSize(new Dimension(width, height));
		setHorizontalAlignment(JLabel.CENTER);
		setVerticalAlignment(JLabel.CENTER);
		setMagicCard(mc);
		setTransferHandler(new CardTransfertHandler());
		//setToolTipText(mc.getText());
		keywordsManager = new KeyWordManager();
		
		addMouseListener(new TransferActions());
		
		if(activateCards)
			initActions();
	}
	
	public void initActions() {
		
		menu.removeAll();
		
		
		menu.add(new JMenuItem(new SelectionActions(this)));
		menu.add(new JMenuItem(new TapActions(this)));
		menu.add(new JMenuItem(new RotateActions(this)));
	
		if(magicCard.getTypes().contains("Creature"))
		{
			JMenu mnuModifier = new JMenu("P/T");
			mnuModifier.add(new BonusCounterActions(this, new BonusCounter(1, 0)));
			mnuModifier.add(new BonusCounterActions(this, new BonusCounter(-1, 0)));
			mnuModifier.add(new BonusCounterActions(this, new BonusCounter(0, 1)));
			mnuModifier.add(new BonusCounterActions(this, new BonusCounter(0, -1)));
			mnuModifier.add(new BonusCounterActions(this,  new BonusCounter(1, 1)));
			mnuModifier.add(new BonusCounterActions(this,  new BonusCounter(-1, -1)));
			mnuModifier.add(new FixCreaturePowerActions(this));
			menu.add(mnuModifier);
		}
		
		JMenu mnuCounter = new JMenu("Counter");
		
		mnuCounter.add(new ItemCounterActions(this, new ItemCounter("Yellow", Color.YELLOW)));
		mnuCounter.add(new ItemCounterActions(this, new ItemCounter("Green", Color.GREEN)));
		mnuCounter.add(new ItemCounterActions(this, new ItemCounter("Orange", Color.ORANGE)));
		menu.add(mnuCounter);
		
		
		if(magicCard.getTypes().contains("Planeswalker"))
		{
			JMenu mnuModifier = new JMenu("Loyalty");
			
			for(LoyaltyCounter count : listLoyalty())
				mnuModifier.add(new LoyaltyActions(this, count));
			menu.add(mnuModifier);
		}
		
		if(magicCard.getSubtypes().contains("Aura")|| magicCard.getSubtypes().contains("Equipment"))
		{
			menu.add(new AttachActions(this));
		}
		

		Set<MTGKeyWord> l = MTGControler.getInstance().getKeyWordManager().getKeywordsFrom(magicCard);
		if(l.size()>0){
			JMenu abilities = new JMenu("KeyWords");
			for(MTGKeyWord k : l)
			{
				JMenuItem it = new JMenuItem(k.getKeyword());
						  it.setToolTipText(k.getDescription());
				abilities.add(it);
			}
			menu.add(abilities);
		}
	
		
		if(counters.size()>0){
			JMenu mnuModifier = new JMenu("Remove Counter");
			for(final AbstractCounter count : counters)
				mnuModifier.add(new JMenuItem(new RemoveCounterActions(this, count)));
			menu.add(mnuModifier);
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
			mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", getMagicCard().getRotatedCardName(), getMagicCard().getEditions().get(0)).get(0);
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
			
			removeAllCounters();
			
			MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", getMagicCard().getRotatedCardName(), getMagicCard().getEditions().get(0)).get(0);
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
		
			 if(isTapped())
			 {
				 GamePanelGUI.getInstance().getPlayer().logAction("Untap " + magicCard);
			 }
			 else
			 {
				if(GameManager.getInstance().getActualTurn().currentPhase()==PHASES.Attack)
					GamePanelGUI.getInstance().getPlayer().logAction("Attack with " + magicCard);
				else
					GamePanelGUI.getInstance().getPlayer().logAction("Tap " + magicCard);
			 }

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

	
	private List<LoyaltyCounter> listLoyalty(){
		String[] values = magicCard.getText().split("\n");
		List<LoyaltyCounter> actions = new ArrayList<LoyaltyCounter>();
					
					for(String s:values)
					{
						
						if(s.startsWith("+"))
						{
							LoyaltyCounter act = new LoyaltyCounter(Integer.parseInt(s.substring(s.indexOf("+"),s.indexOf(":")).trim()), s.substring(s.indexOf(":")+1).trim());
							actions.add(act);
						}
						else if(s.startsWith("0"))
						{
							LoyaltyCounter act = new LoyaltyCounter(0, s.substring(s.indexOf(":")+1).trim());
							actions.add(act);
						}
						else
						{
							LoyaltyCounter act = new LoyaltyCounter(Integer.parseInt("-"+s.substring(1,s.indexOf(":")).trim()), s.substring(s.indexOf(":")+1).trim());
							actions.add(act);
						}
					}
					return actions;
	}
	
	
	public MagicCard getMagicCard() {
		return magicCard;
	}
	
	public void setMagicCard(MagicCard mc) {
		this.magicCard = mc;
		try {
			if(mc.getLayout().equals(MagicCard.LAYOUT.Token.toString()) || mc.getLayout().equals(MagicCard.LAYOUT.Emblem.toString()))
			{
				fullResPics = new CockatriceTokenProvider().getPictures(mc);
				image = new ImageIcon(fullResPics.getScaledInstance(getWidth(), getHeight(), Image.SCALE_FAST));
			}
			else
			{
				fullResPics = MTGControler.getInstance().getEnabledPicturesProvider().getPicture(mc,null);
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

	public void showLoyalty(boolean b) {
		showLoyalty=b;
		
	}



}
