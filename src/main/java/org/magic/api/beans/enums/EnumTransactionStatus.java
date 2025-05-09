package org.magic.api.beans.enums;

import java.awt.Color;

import javax.swing.Icon;


import org.magic.api.interfaces.extra.MTGIconable;
import org.magic.services.tools.UITools;

public enum EnumTransactionStatus implements MTGIconable {
			NEW(Color.GREEN,"New"),
			IN_PROGRESS(Color.ORANGE,"In Progress"),
			PAYMENT_WAITING (Color.YELLOW,"Waiting for payment"),
			PAYMENT_SENT(Color.YELLOW,"Payment sent"), 
			REFUSED (Color.RED,"Refused"),
			PAID (Color.YELLOW,"Paid"),
			SENT(new Color(102,178,255),"Sent"), 
			CLOSED (Color.BLACK,"Closed"), 
			CANCELED (Color.GRAY,"Canceled"),
			CANCELATION_ASK (new Color(192,192,192),"Ask for Cancelation"),
			PRE_ORDERED (new Color(178,102,255),"Pre Ordered"),
			DELIVRED (Color.YELLOW,"Delivred");

			private String name;
			private Color color;


			EnumTransactionStatus(Color c, String s) {
				this.color=c;
				this.name=s;
			}
		
			
			@Override
			public String getName() {
				return name;
			}
			
			@Override
			public Icon getIcon() {
				return UITools.generateRoundedIcon(color);
			}
			
			
			
}
