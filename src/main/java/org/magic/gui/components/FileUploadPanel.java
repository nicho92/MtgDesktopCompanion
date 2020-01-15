package org.magic.gui.components;

import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileSystemView;

import org.magic.gui.decorators.FileDropDecorator;

public class FileUploadPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	public FileUploadPanel() {
		new FileDropDecorator().init(this, (File[] files) -> {

			for(File f : files)
			{
				
				JLabel l = new JLabel(f.getName(),FileSystemView.getFileSystemView().getSystemIcon(f),SwingConstants.CENTER);
				l.setVerticalTextPosition(SwingConstants.BOTTOM);
				add(l);
			}
			
			revalidate();

		});
	}

}
