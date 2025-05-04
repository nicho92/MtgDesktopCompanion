package org.magic.game.gui.components;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.TransferHandler;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGKeyWord;
import org.magic.api.beans.enums.EnumLayout;
import org.magic.api.beans.game.GameManager;
import org.magic.api.beans.game.Player;
import org.magic.api.beans.game.Turn.PHASES;
import org.magic.api.beans.game.ZoneEnum;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGTokensProvider;
import org.magic.game.actions.abbstract.AbstractCardAction;
import org.magic.game.actions.cards.AbilitiesActions;
import org.magic.game.actions.cards.AttachActions;
import org.magic.game.actions.cards.BonusCounterActions;
import org.magic.game.actions.cards.CreateActions;
import org.magic.game.actions.cards.EmblemActions;
import org.magic.game.actions.cards.FixCreaturePowerActions;
import org.magic.game.actions.cards.ItemCounterActions;
import org.magic.game.actions.cards.LoyaltyActions;
import org.magic.game.actions.cards.RemoveCounterActions;
import org.magic.game.actions.cards.SelectionActions;
import org.magic.game.actions.cards.TapActions;
import org.magic.game.model.abilities.AbstractAbilities;
import org.magic.game.model.counters.AbstractCounter;
import org.magic.game.model.counters.BonusCounter;
import org.magic.game.model.counters.ItemCounter;
import org.magic.game.model.counters.LoyaltyCounter;
import org.magic.game.model.factories.AbilitiesFactory;
import org.magic.game.model.factories.CountersFactory;
import org.magic.game.transfert.CardTransfertHandler;
import org.magic.services.PluginRegistry;
import org.magic.services.keywords.AbstractKeyWordsManager;
import org.magic.services.logging.MTGLogger;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.ImageTools;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public class DisplayableCard extends JLabel implements Draggable {

	/**
	 *
	 */

	private static final long serialVersionUID = 1L;
	private JPopupMenu menu;
	private MTGCard magicCard;
	private boolean tapped = false;
	private ImageIcon image;
	private boolean draggable = true;
	private boolean tappable = true;
	private String title;
	private String bottom;
	private boolean selected = false;
	private boolean rotated;
	private boolean showPT;
	private JSeparator sep;
	private List<DisplayableCard> attachedCards;
	private transient List<AbstractCounter> counters;
	private transient Image fullResPics;
	private boolean showLoyalty;
	private ZoneEnum position;
	private boolean rightActions;
	private transient Observable obs;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private Player owner;


	public Player getOwner() {
		return owner;
	}

	public List<AbstractCounter> getCounters() {
		return counters;
	}

	public void setCounters(List<AbstractCounter> counters) {
		this.counters = counters;
	}

	public ZoneEnum getPosition() {
		return position;
	}

	public void setPosition(ZoneEnum position) {
		this.position = position;
	}

	public void addCounter(AbstractCounter c) {
		counters.add(c);
		c.apply(this);
		initActions();
	}


	public void removeCounter(AbstractCounter c) {
		counters.remove(c);
		c.remove(this);
		initActions();
	}

	@Override
	public Border getBorder() {
		if (isSelected())
			return new LineBorder(Color.RED);
		else
			return null;

	}

	public void removeAllCounters() {
		for (AbstractCounter c : counters) {
			c.remove(this);
		}
		counters.clear();
		initActions();

	}

	@Override
	public String toString() {
		return String.valueOf(magicCard);
	}

	public List<DisplayableCard> getAttachedCards() {
		return attachedCards;
	}

	public boolean isRotated() {
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
		if (magicCard != null)
			return image;

		return super.getIcon();
	}

	@Override
	public void paintComponent(Graphics g) {
		if (image != null) {
			g.drawImage(image.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);

			if (showPT)
				drawString(g, magicCard.getPower() + "/" + magicCard.getToughness(), Color.BLACK, Color.WHITE,
						this.getWidth() - 33, this.getHeight() - 10);

			if (showLoyalty)
				drawString(g, "" + magicCard.getLoyalty(), Color.BLACK, Color.WHITE, this.getWidth() - 23,
						this.getHeight() - 15);

			validate();
		}
	}

	private void drawString(Graphics g, String s, Color background, Color foreground, int x, int y) {
		g.setFont(new Font("default", Font.BOLD, 12));
		g.setColor(background);
		var fm = g.getFontMetrics();
		var rect = fm.getStringBounds(s, g);
		g.fillRect(x, y - fm.getAscent(), (int) rect.getWidth(), (int) rect.getHeight());
		g.setColor(foreground);
		g.drawString(s, x, y);
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

	public void showPT(boolean t) {
		showPT = t;
	}

	public DisplayableCard(MTGCard mc, Dimension d, boolean activateCards, boolean rightClick) {
		construct(mc, d, activateCards, rightClick);
	}

	public DisplayableCard(MTGCard mc, Dimension d, boolean activateCards) {
		construct(mc, d, activateCards, true);
	}

	public void construct(MTGCard mc, Dimension d, boolean activateCards, boolean rightClick) {

		owner = GameManager.getInstance().getCurrentPlayer();
		rightActions = rightClick;
		menu = new JPopupMenu();
		sep = new JSeparator();
		attachedCards = new ArrayList<>();
		obs = new Observable();
		counters = new ArrayList<>();

		setSize(d);
		setPreferredSize(d);
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalAlignment(SwingConstants.CENTER);
		setMagicCard(mc);


		if (activateCards) {
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					setCursor(new Cursor(Cursor.HAND_CURSOR));
					describe();
					
				}
				
				@Override
				public void mouseExited(MouseEvent e) {
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}

				@Override
				public void mouseClicked(MouseEvent e) {

					if (SwingUtilities.isLeftMouseButton(e)) {
						if (e.getClickCount() == 1 && e.isControlDown()) {
							setSelected(!isSelected());
							repaint();
						}
						if (e.getClickCount() == 2 && isTappable()) {
							tap(!isTapped());
						}
					}

				}
			});

			addMouseMotionListener(new MouseAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
					if (SwingUtilities.isLeftMouseButton(e) && isDraggable())
						enableDrag(e);
				}
			});
		}

		initActions();

		setTransferHandler(new CardTransfertHandler());
	}

	private void describe() {
		obs.setChanged();
		obs.notifyObservers(getMagicCard());
		GamePanelGUI.getInstance().describeCard(this);
	}

	public void enableDrag(MouseEvent e) {
		((DraggablePanel) getParent()).getTransferHandler().exportAsDrag(this, e, TransferHandler.MOVE);
	}

	private AbstractCardAction generateActionFromKey(MTGKeyWord k) throws NoSuchMethodException,InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
			var a = PluginRegistry.inst().loadClass("org.magic.game.actions.cards." + k.toString() + "Actions");
			@SuppressWarnings("unchecked")
			var ctor = a.getDeclaredConstructor(DisplayableCard.class);
			var aaction = (AbstractCardAction) ctor.newInstance(this);
			aaction.putValue(Action.LONG_DESCRIPTION, k.getKeyword());
		return aaction;
	}

	public void initActions() {

		if (rightActions) {
			menu.removeAll();
			menu.add(new JMenuItem(new SelectionActions(this)));

			if(getMagicCard().isPermanent())
			{
					if(!getMagicCard().isPlaneswalker())
						menu.add(new JMenuItem(new TapActions(this)));

					if (magicCard.isCreature() || magicCard.isVehicule()) {
						var mnuModifier = new JMenu("P/T");
						mnuModifier.add(new BonusCounterActions(this, new BonusCounter(1, 0)));
						mnuModifier.add(new BonusCounterActions(this, new BonusCounter(-1, 0)));
						mnuModifier.add(new BonusCounterActions(this, new BonusCounter(0, 1)));
						mnuModifier.add(new BonusCounterActions(this, new BonusCounter(0, -1)));
						mnuModifier.add(new BonusCounterActions(this, new BonusCounter(1, 1)));
						mnuModifier.add(new BonusCounterActions(this, new BonusCounter(-1, -1)));
						mnuModifier.add(new FixCreaturePowerActions(this));
						menu.add(mnuModifier);
					}


					if (!counters.isEmpty()) {
						var mnuModifier = new JMenu("Remove Counter");
						counters.forEach(count->mnuModifier.add(new JMenuItem(new RemoveCounterActions(this, count))));
						menu.add(mnuModifier);
					}

					if (magicCard.isPlaneswalker()) {
						var mnuModifier = new JMenu("Loyalty");
						AbilitiesFactory.getInstance().getLoyaltyAbilities(getMagicCard()).forEach(la->mnuModifier.add(new LoyaltyActions(this, new LoyaltyCounter(la))));
						menu.add(mnuModifier);
					}

			}

			List<AbstractAbilities> abs = AbilitiesFactory.getInstance().getActivatedAbilities(getMagicCard());
			if(!abs.isEmpty())
			{
				var mnuAbilities = new JMenu("Activate");
				abs.stream().filter(c->!c.isLoyalty()).forEach(c->mnuAbilities.add(new AbilitiesActions(c)));
				menu.add(mnuAbilities);
			}

			Set<MTGKeyWord> l = AbstractKeyWordsManager.getInstance().getKeywordsFrom(magicCard);

			if (!l.isEmpty()) {
				var actions = new JMenu("Abilities");

				for (final MTGKeyWord k : l) {
					JMenuItem it;
					try {
						var act = generateActionFromKey(k);
						if(act.playableFrom()==position)
						{
							it = new JMenuItem(act);
							actions.add(it);
						}
						
						
					} catch (Exception e) {
						logger.trace("error {} : {}",k,e);
						it = new JMenuItem(k.getKeyword());
						actions.add(it);
					}
				
				}
				menu.add(actions);
			}


			List<ItemCounter> items = CountersFactory.getInstance().createItemCounter(getMagicCard());
			if(!items.isEmpty())
			{
				var mnuCounter = new JMenu("Counters");
				items.forEach(c->mnuCounter.add(new ItemCounterActions(this, c)));
				menu.add(mnuCounter);
			}



			if (magicCard.getSubtypes().contains("Aura") || magicCard.getSubtypes().contains("Equipment")) {
				menu.add(new JMenuItem(new AttachActions(this)));
			}

			menu.add(sep);

			if (getEnabledPlugin(MTGTokensProvider.class).isTokenizer(magicCard)) {
				menu.add(new JMenuItem(new CreateActions(this)));
			}

			if (getEnabledPlugin(MTGTokensProvider.class).isEmblemizer(magicCard)) {
				menu.add(new JMenuItem(new EmblemActions(this)));
			}


			setComponentPopupMenu(menu);
		}

	}

	public void tap(boolean t) {

		if (!tappable)
			return;

		if (isTapped()) {
			GamePanelGUI.getInstance().getPlayer().logAction("Untap " + magicCard);
		} else {
			if (GameManager.getInstance().getActualTurn().currentPhase() == PHASES.ATTACK)
				GamePanelGUI.getInstance().getPlayer().logAction("Attack with " + magicCard);
			else
				GamePanelGUI.getInstance().getPlayer().logAction("Tap " + magicCard);
		}

		var angle = 0;
		if (t)
			angle = 90;
		else
			angle = -90;

		this.image = new ImageIcon(ImageTools.rotate(ImageTools.imageToBufferedImage(getImageIcon().getImage()), angle));
		this.setSize(getHeight(), getWidth());
		this.tapped = t;
	}

	public MTGCard getMagicCard() {
		return magicCard;
	}

	public void setMagicCard(MTGCard mc) {
		try {
			this.magicCard = (MTGCard) BeanUtils.cloneBean(mc);
		} catch (Exception e1) {
			logger.error("error setting {}",mc, e1);
		}


		SwingWorker<Image, Image> sw = new SwingWorker<>()
		{
			Image temp = null;
			@Override
			protected Image doInBackground() throws Exception {
				try {
					if (mc.getLayout() ==EnumLayout.TOKEN|| mc.getLayout()==EnumLayout.EMBLEM) {
						temp = getEnabledPlugin(MTGTokensProvider.class).getPictures(mc);
					}
					else {
						temp = getEnabledPlugin(MTGPictureProvider.class).getPicture(mc);
					}
					publish(temp);
				} catch (Exception _) {
					temp = getEnabledPlugin(MTGPictureProvider.class).getBackPicture(mc);
				}


				return temp;
			}


			@Override
			protected void done() {
				try {
					fullResPics=get();
					image = new ImageIcon(fullResPics.getScaledInstance(getWidth(), getHeight(), Image.SCALE_FAST));
					revalidate();
					repaint();
				}catch(InterruptedException _)
				{
					Thread.currentThread().interrupt();
				}
				catch (Exception e) {
					logger.error(e);
				}
			}

		};

		ThreadManager.getInstance().runInEdt(sw,"Init DisplayableCard:"+mc);


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
		this.rotated = b;

	}

	@Override
	public void moveCard(DisplayableCard mc, ZoneEnum to) {
		((DraggablePanel) getParent()).moveCard(mc, to);
	}

	@Override
	public void addComponent(DisplayableCard i) {
		if (i.getMagicCard().getSubtypes().contains("Aura")) {
			getAttachedCards().add(i);
		}

		((DraggablePanel) getParent()).addComponent(i);
	}

	@Override
	public ZoneEnum getOrigine() {
		return ((DraggablePanel) getParent()).getOrigine();
	}

	public void showLoyalty(boolean b) {
		showLoyalty = b;

	}

	@Override
	public void updatePanel() {
		// do nothing
	}

	@Override
	public void postTreatment(DisplayableCard c) {
		((DraggablePanel) getParent()).postTreatment(c);

	}

	public void addObserver(Observer panelDetail) {
		obs.addObserver(panelDetail);
	}



}
