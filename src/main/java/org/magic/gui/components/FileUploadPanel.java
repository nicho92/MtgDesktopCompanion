package org.magic.gui.components;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.magic.api.beans.GedEntry;
import org.magic.api.beans.MagicCard;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.renderer.GedEntryComponent;
import org.magic.gui.tools.FileDropDecorator;
import org.magic.services.GedService;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.FileTools;

import java.awt.BorderLayout;
import java.awt.SystemColor;

public class FileUploadPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JPanel panneauCenter;
	
	@Override
	public void onFirstShowing() {

		GedService.inst().listRoot().forEach(p->{
			try {
				panneauCenter.add(new GedEntryComponent(new GedEntry(p)));
				panneauCenter.revalidate();
			} catch (IOException e) {
			}
		});
		
	}
	
	public FileUploadPanel() {
		setLayout(new BorderLayout());
		
		JPanel panneauHaut = new JPanel();
		panneauCenter = new JPanel();
		AbstractBuzyIndicatorComponent buzy = AbstractBuzyIndicatorComponent.createProgressComponent();
		
		add(panneauHaut, BorderLayout.NORTH);
		add(panneauCenter, BorderLayout.CENTER);
		
		panneauHaut.add(buzy);
		
		
		
		new FileDropDecorator().init(panneauCenter, (File[] files) -> {
			
			buzy.start(files.length);
			
			SwingWorker<Void, GedEntry> sw = new SwingWorker<>() {

				@Override
				protected Void doInBackground() throws Exception {
					for(File f : files)
					{
						try {
							GedEntry entry = new GedEntry(f);
							GedService.inst().store(entry);
							publish(entry);
							
						} catch (Exception e) {
							logger.error("Error uploading " + f,e);
						}	
						
					}
					return null;
				}

				@Override
				protected void done() {
					buzy.end();
					revalidate();
					
				}

				@Override
				protected void process(List<GedEntry> chunks) {
					chunks.forEach(c->{
						panneauCenter.add(new GedEntryComponent(c));
						buzy.progressSmooth(1);
					});
				}
			};
			ThreadManager.getInstance().runInEdt(sw, "Upload File");
		});
	}

	@Override
	public String getTitle() {
		return "GED";
	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_GED;
	}

}
