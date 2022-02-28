package org.magic.api.beans.enums;

import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringUtils;
import org.magic.services.MTGConstants;

public enum MTGExportCategory {
		FILE (MTGConstants.ICON_NEW), 
		ONLINE (MTGConstants.ICON_WEBSITE), 
		MANUAL (MTGConstants.ICON_MANUAL), 
		APPLICATION (MTGConstants.ICON_CONFIG), 
		NONE (MTGConstants.ICON_CANCEL);
		
		
		private ImageIcon icon;


		public String toPrettyString() {
			return StringUtils.capitalize(name().toLowerCase());
		}
		
		
		private MTGExportCategory(ImageIcon ic) {
			this.icon=ic;
		}
		
		public ImageIcon getIcon() {
			return icon;
		}
		
}