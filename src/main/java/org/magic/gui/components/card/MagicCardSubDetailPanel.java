package org.magic.gui.components.card;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import org.magic.api.beans.MagicCard;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.MapTableModel;
import org.magic.services.tools.BeanTools;
import org.magic.services.tools.UITools;

public class MagicCardSubDetailPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private MapTableModel<String, Object> mod;

	@Override
	public String getTitle() {
		return "DETAIL";
	}
	
	public MagicCardSubDetailPanel() {
		setLayout(new BorderLayout());
		
		mod = new MapTableModel<>() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Object.class;
			}
		};
		
		
		
		var table = UITools.createNewTable(mod);
		add(new JScrollPane(table),BorderLayout.CENTER);
	}
	
	public void init(MagicCard mc)
	{
		mod.init(BeanTools.describe(mc));
	}
	
	
	
	
	

}
