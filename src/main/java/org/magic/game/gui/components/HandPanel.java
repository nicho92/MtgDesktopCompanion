package org.magic.game.gui.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.magic.api.beans.MagicCard;
import org.magic.game.model.ZoneEnum;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;

public class HandPanel extends DraggablePanel {

	private static final long serialVersionUID = 1L;
	private GridBagConstraints c;
	private int index = 0;
	private int val = 7;
	private transient SwingWorker<Void, MagicCard> sw;
	private ZoneEnum origine = ZoneEnum.HAND;

	@Override
	public void moveCard(DisplayableCard mc, ZoneEnum to) {

		switch (to) {
		case BATTLEFIELD:
			player.playCard(mc.getMagicCard());
			break;
		case EXIL:
			player.exileCardFromHand(mc.getMagicCard());
			break;
		case GRAVEYARD:
			player.discardCardFromHand(mc.getMagicCard());
			break;
		case LIBRARY:
			player.putCardInLibraryFromHand(mc.getMagicCard(), true);
			break;
		default:
			break;
		}
	}


	public void setRupture(int val) {
		this.val = val;
	}

	public HandPanel() {
		setLayout(new GridBagLayout());
		c = new GridBagConstraints();
	}

	public void addComponent(DisplayableCard i) {
		if (index >= val) {
			c.gridy = c.gridy + 1;
			c.gridx = 0;
			index = 0;
		}
		c.gridx = c.gridx + 1;
		i.setHorizontalTextPosition(SwingConstants.CENTER);
		i.setVerticalTextPosition(SwingConstants.BOTTOM);
		i.enableDrag(dragging);

		if (i.isTapped())
			i.tap(false);

		add(i, c);
		index++;
		i.setPosition(getOrigine());
	}

	public void initThumbnails(final List<MagicCard> cards, final boolean activateCards, final boolean rightClick) {

		if (sw != null && !sw.isDone())
		{
			boolean ret = sw.cancel(true);
			logger.debug("Closing initThumbnails SwingWorker : {}",ret);
		}

		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(2, 2, 2, 2);
		c.anchor = GridBagConstraints.NORTHWEST;

		this.removeAll();
		index = 0;
		
		
		if(d==null)
			d=MTGControler.getInstance().getCardsGameDimension();
		
		sw = new SwingWorker<>()
		{

			@Override
			protected void process(List<MagicCard> cards) {
				for(MagicCard mc : cards) {
					var lab = new DisplayableCard(mc, d, activateCards, rightClick);
						lab.setTappable(activateCards);
						addComponent(lab);
				}
				revalidate();
			}
			
			@Override
			protected Void doInBackground() throws Exception {
				publish(cards.toArray(new MagicCard[cards.size()]));
				return null;
			}
			
			@Override
			protected void done() {
				
				try {
					get();
				
				}catch(CancellationException e)
				{
					
					logger.info("{} is canceled",this);
				}
				catch(InterruptedException | ExecutionException ex)
				{
					Thread.currentThread().interrupt();
				}
				
				
				revalidate();
				repaint();
			}
		};
		
		ThreadManager.getInstance().runInEdt(sw, "thumbnail " + cards.size() +" items");
	}

	@Override
	public ZoneEnum getOrigine() {
		return origine;
	}

	public void setOrigine(ZoneEnum or) {
		origine = or;

	}

	@Override
	public void postTreatment(DisplayableCard c) {
		// do nothing

	}

	public void setMaxCardsRow(int i) {
		val = i;

	}

}
