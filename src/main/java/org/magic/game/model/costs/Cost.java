package org.magic.game.model.costs;

import java.io.Serializable;

public interface Cost  extends Serializable{

	public default boolean isNumberCost()
	{
		return false;
	}

}
