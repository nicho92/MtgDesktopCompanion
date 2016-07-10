package org.magic.gui.game;

import java.awt.Color;

import javax.swing.border.LineBorder;

import org.magic.api.beans.MagicCard;
import org.magic.game.GameManager;
import org.magic.game.PositionEnum;
import org.magic.gui.game.actions.MouseAction;

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
			case BATTLEFIELD:player.returnCardFromGraveyard(mc);break;
			case EXIL:player.exileCardFromGraveyard(mc);break;
			case HAND:player.returnCardFromGraveyard(mc);break;
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
