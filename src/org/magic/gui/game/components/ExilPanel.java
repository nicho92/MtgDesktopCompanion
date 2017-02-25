package org.magic.gui.game.components;

import java.awt.Color;

import javax.swing.border.LineBorder;

import org.magic.api.beans.MagicCard;
import org.magic.game.model.PositionEnum;

public class ExilPanel extends DraggablePanel {
	
	public ExilPanel() {
		super();
		setLayout(null);
		setBorder(new LineBorder(Color.BLACK));
		setBackground(Color.GRAY);
		
	}
	
	@Override
	public PositionEnum getOrigine() {
		return PositionEnum.EXIL;
	}

	@Override
	public void addComponent(DisplayableCard i) {
		if(i.isTapped())
			i.tap(false);
		add(i);
	}


	@Override
	public void moveCard(MagicCard mc, PositionEnum to) {
		/*switch (to) {
			case BATTLEFIELD:player.returnCardFromGraveyard(mc);break;
			case EXIL:player.exileCardFromGraveyard(mc);break;
			case HAND:player.returnCardFromGraveyard(mc);break;
			case LIBRARY:player.putCardInLibraryFromGraveyard(mc, true);
			default:break;
		}*/
		
	}
	
	@Override
	public void postTreatment() {
		
		
		
	}
	
}
