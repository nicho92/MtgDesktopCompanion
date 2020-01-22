package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.magic.api.beans.GedEntry;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.renderer.GedEntryComponent;
import org.magic.gui.tools.FileDropDecorator;
import org.magic.services.GedService;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;

public class GedPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JPanel panneauCenter;
	
	@Override
	public void onFirstShowing() {

		
		SwingWorker<Void, GedEntry> sw = new SwingWorker<>() {
			protected Void doInBackground() throws Exception {
				
				GedService.inst().listRoot().forEach(p->{
					try {
						publish(new GedEntry(p));
					}
					catch (IOException e) 
					{
						logger.error(e);
					}
				});
				return null;
			}
			
			@Override
			protected void process(List<GedEntry> chunks) {
				for(GedEntry g : chunks)
					addEntry(g);
			}
			
			@Override
			protected void done()
			{
				panneauCenter.revalidate();
			}
			
			
		};
		
		ThreadManager.getInstance().runInEdt(sw, "loading ged elements");
		
	}
	
	public GedPanel() {
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
						addEntry(c);
						buzy.progressSmooth(1);
					});
				}

			};
			ThreadManager.getInstance().runInEdt(sw, "Upload File");
		});
	}


	private void addEntry(GedEntry c) {
		GedEntryComponent e = new GedEntryComponent(c);
		panneauCenter.add(e);

		e.getRemoveComponent().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) {
					if(GedService.inst().delete(c)) {
						panneauCenter.remove(e);
						panneauCenter.revalidate();
						panneauCenter.repaint();
					}
			}
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
