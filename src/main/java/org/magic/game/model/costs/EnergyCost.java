package org.magic.game.model.costs;

@Deprecated
public class EnergyCost extends NumberCost {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final String text = "{E}";

	@Deprecated
	public EnergyCost(int qty) {
		super(qty);
	}

	@Deprecated
	@Override
	public String toString() {
		final var append = new StringBuilder();

		for (var i = 0; i < value; i++) {
		    append.append(text);
		}

		return append.toString();
	}

}
