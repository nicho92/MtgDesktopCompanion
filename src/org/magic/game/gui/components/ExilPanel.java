package org.magic.game.gui.components;

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
		i.setPosition(getOrigine());
	}


	@Override
	public void moveCard(DisplayableCard mc, PositionEnum to) {
	
	}
	
	@Override
	public void postTreatment() {
		
		
		
	}
	
}
