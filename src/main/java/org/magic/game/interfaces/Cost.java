package org.magic.game.interfaces;

import java.io.Serializable;

public interface Cost extends Serializable {

	public default boolean isNumberCost() {
		return false;
	}

}
