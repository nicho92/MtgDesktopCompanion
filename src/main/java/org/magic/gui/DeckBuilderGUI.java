package org.magic.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.magic.api.main.MtgDesktopCompanion;
import org.magic.gui.components.ConstructPanel;
import org.magic.gui.components.SealedPanel;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class DeckBuilderGUI extends JPanel {
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	
	public DeckBuilderGUI() {
		setLayout(new BorderLayout());
		JTabbedPane tab = new JTabbedPane();
		add(tab,BorderLayout.CENTER);
		
		tab.add(MTGControler.getInstance().getLangService().getCapitalize("CONSTRUCT"), new ConstructPanel());
		tab.add(MTGControler.getInstance().getLangService().getCapitalize("SEALED"), new SealedPanel());
		
	}
	

}
