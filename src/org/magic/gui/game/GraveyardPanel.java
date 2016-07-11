package org.magic.gui.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.border.LineBorder;

import org.magic.api.beans.MagicCard;
import org.magic.api.pictures.impl.GathererPicturesProvider;
import org.magic.game.PositionEnum;

public class GraveyardPanel extends DraggablePanel {
	Image i;
	
	public GraveyardPanel() {
		super();
		setLayout(null);
		setBorder(new LineBorder(Color.BLACK));
		setBackground(Color.GRAY);
		
		try {
			i=gatherer.getBackPicture();
		} catch (Exception e) {
			//e.printStackTrace();
		}
		setPreferredSize(new Dimension(i.getWidth(null), i.getHeight(null)));
	}
	
	@Override
	public PositionEnum getOrigine() {
		return PositionEnum.GRAVEYARD;
	}

	@Override
	public void addComponent(DisplayableCard i) {
		if(i.isTapped())
			i.tap(false);
		add(i);
	}


	@Override
	public void moveCard(MagicCard mc, PositionEnum to) {
		switch (to) {
			case BATTLEFIELD:player.returnCardFromGraveyard(mc);break;
			case EXIL:player.exileCardFromGraveyard(mc);break;
			case HAND:player.returnCardFromGraveyard(mc);break;
			case LIBRARY:player.putCardInLibraryFromGraveyard(mc, true);
			default:break;
		}
		
	}

	@Override
	public void postTreatment() {
		// TODO Auto-generated method stub
		
	}
	
	
	/*@Override
	public void paintComponent(Graphics g) {
		 super.paintComponents(g);
		Image bg = new ImageIcon(getClass().getResource("/res/graveyard.png")).getImage();
		g.drawImage(bg,0,0,null);
	}
	*/

}
