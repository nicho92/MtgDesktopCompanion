package org.magic.gui.game;

import javax.swing.JPanel;

import org.magic.services.games.PositionEnum;

public abstract class DragDestinationPanel extends JPanel{

	
  public abstract PositionEnum getOrigine();
}
