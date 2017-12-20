package org.magic.game.gui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JMenuItem;
import javax.swing.border.LineBorder;

import org.magic.game.actions.player.SearchActions;
import org.magic.game.model.PositionEnum;

public class ExilPanel extends DraggablePanel {
	
	
	public ExilPanel() {
		super();
		setLayout(null);
		setBorder(new LineBorder(Color.BLACK));
		setBackground(Color.GRAY);
		
		menu.removeAll();
		menu.add(new JMenuItem(new SearchActions(getOrigine())));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		if(GamePanelGUI.getInstance().getPlayer()!=null)
		{
			g.setFont(new Font("default", Font.BOLD, 12));
			g.setColor(Color.BLACK);
			g.drawString(GamePanelGUI.getInstance().getPlayer().getExil().size() +" exiled cards",15,15);
			revalidate();
		}
	}
	
	
	@Override
	public PositionEnum getOrigine() {
		return PositionEnum.EXIL;
	}

	@Override
	public void addComponent(DisplayableCard i) {
		i.setPosition(getOrigine());
		add(i);
	}


	@Override
	public void moveCard(DisplayableCard mc, PositionEnum to) {
		switch (to) {
		case BATTLEFIELD:player.playCardFromExile(mc.getMagicCard());break;
		case HAND:player.returnCardFromExile(mc.getMagicCard());break;
		case GRAVEYARD:player.discardCardFromExile(mc.getMagicCard());
		case LIBRARY:player.putCardInLibraryFromExile(mc.getMagicCard(), true);
		default:break;
	}
	}
	
	@Override
	public void postTreatment(DisplayableCard c) {
		remove(c);
		revalidate();
		repaint();
		
	}
	
}
