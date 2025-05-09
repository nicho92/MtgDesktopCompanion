package org.magic.api.beans.enums;

import java.awt.Color;

import javax.swing.Icon;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.interfaces.extra.MTGIconable;
import org.magic.services.tools.UITools;

public enum EnumTransactionDirection implements MTGIconable {
		BUY,SELL;

		@Override
		public Icon getIcon() {
			if(equals(BUY))
				return UITools.generateRoundedIcon(Color.RED);
			
			return UITools.generateRoundedIcon(Color.GREEN);
		}

		@Override
		public String getName() {
			return StringUtils.capitalize(name().toLowerCase());
		}
		
		
		
		
}
