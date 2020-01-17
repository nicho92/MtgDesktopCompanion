package org.magic.gui.components;

import java.io.File;

import javax.swing.JPanel;

import org.magic.api.beans.GedEntry;
import org.magic.api.beans.MagicCard;
import org.magic.api.dao.impl.FileDAO;
import org.magic.api.interfaces.MTGGedStorage;
import org.magic.gui.components.renderer.GedEntryComponent;
import org.magic.gui.tools.FileDropDecorator;

public class FileUploadPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	public FileUploadPanel() {
		new FileDropDecorator().init(this, (File[] files) -> {

			for(File f : files)
			{
				GedEntry<MagicCard> entry = new GedEntry<>(f);
				add(new GedEntryComponent<>(entry));
			}
			
			revalidate();
			
		});
	}

}
