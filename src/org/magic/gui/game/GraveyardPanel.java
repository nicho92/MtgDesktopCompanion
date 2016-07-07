package org.magic.gui.game;

import java.awt.Color;

import javax.swing.border.LineBorder;

import org.magic.api.beans.MagicCard;
import org.magic.game.GameManager;
import org.magic.game.PositionEnum;

public class GraveyardPanel extends DraggablePanel {

	public GraveyardPanel() {
		super();
		setLayout(null);
		setBorder(new LineBorder(Color.BLACK));
	}
	
	@Override
	public PositionEnum getOrigine() {
		return PositionEnum.GRAVEYARD;
	}

	@Override
	public void addComponent(DisplayableCard i) {
		add(i);
	}


	@Override
	public void moveCard(MagicCard mc, PositionEnum to) {
		switch (to) {
			case BATTLEFIELD:GameManager.getInstance().getPlayer().returnCardFromGraveyard(mc);break;
			case EXIL:GameManager.getInstance().getPlayer().exileCardFromGraveyard(mc);break;
			case HAND:GameManager.getInstance().getPlayer().returnCardFromGraveyard(mc);break;
			default:break;
		}
		
	}
	
	
	/*@Override
	public void paintComponent(Graphics g) {
		 super.paintComponents(g);
		Image bg = new ImageIcon(getClass().getResource("/res/graveyard.png")).getImage();
		g.drawImage(bg,0,0,null);
	}
	*/

}
