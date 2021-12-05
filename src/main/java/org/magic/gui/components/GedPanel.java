package org.magic.gui.components;

import static org.magic.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.magic.api.beans.GedEntry;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.renderer.GedEntryComponent;
import org.magic.gui.decorators.FileDropDecorator;
import org.magic.services.GedService;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.FileTools;
public class GedPanel<T> extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JPanel panneauCenter;
	private Class<T> classe;
	private transient T instance;
	private ImagePanel viewPanel;
	private List<GedEntry<T>> entries;
	
	
	
	public void init(Class<T> t, T instance)
	{
		this.classe=t;
		this.instance=instance;
		entries = new ArrayList<>();
		
		if(isVisible())
			onVisible();
	
		
		
		
	}
	
	@Override
	public void onVisible() {
		
		if(classe==null)
			return;
		
		logger.debug("Show ged for " + classe.getSimpleName());
		listDirectory(GedService.inst().getPath(classe,instance));

	}
	
	@Override
	public void onFirstShowing() {
		listDirectory(GedService.inst().root());
	}
	
	public GedPanel() {
		setLayout(new BorderLayout());

		var panneauHaut = new JPanel();
		panneauCenter = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		viewPanel = new ImagePanel(true, false, true);
		
		var buzy = AbstractBuzyIndicatorComponent.createProgressComponent();
		add(panneauHaut, BorderLayout.NORTH);
		add(panneauCenter, BorderLayout.CENTER);
		
		panneauHaut.add(new JLabel(capitalize("DRAG_HERE")));
		panneauHaut.add(buzy);
		
		
		
		new FileDropDecorator().init(panneauCenter, (File[] files) -> {
			buzy.start(files.length);
			SwingWorker<Void, GedEntry<T>> sw = new SwingWorker<>() {

				@Override
				protected Void doInBackground() throws Exception {
					for(File f : files)
					{
						try {
							GedEntry<T> entry = new GedEntry<>(f,classe);
										entry.setObject(instance);
							
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
				protected void process(List<GedEntry<T>> chunks) {
					chunks.forEach(c->{
						addEntry(c);
						buzy.progressSmooth(1);
					});
				}

			};
			ThreadManager.getInstance().runInEdt(sw, "Upload File");
		});
	}

	
	
	public List<GedEntry<T>> listEntries()
	{
		return entries;
	}
	

	private void addEntry(GedEntry<T> c) {
		
		entries.add(c);
		var e = new GedEntryComponent(c,150,100);
		panneauCenter.add(e);
		
		e.setCallable(new Callable<Void>() {
			
			@Override
			public Void call() throws Exception {
				if(e.getEntry().isImage()) {
					viewPanel.setImg(e.getPicture());
					viewPanel.setPreferredSize(new Dimension(e.getPicture().getWidth(),e.getPicture().getHeight()));
					MTGUIComponent.createJDialog(MTGUIComponent.build(viewPanel, "view", MTGConstants.ICON_TAB_PICTURE), true, true).setVisible(true);
				}
				else
				{
					try {
						var tmp = File.createTempFile(e.getEntry().getName(), "."+e.getEntry().getExt());
						FileTools.saveFile(tmp, e.getEntry().getContent());
						Desktop.getDesktop().open(tmp);
					}catch(Exception e)
					{
						logger.error(e);
					}
				}
				return null;
			}
		});
		
		
		e.getRemoveComponent().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) 
			{
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
		return MTGConstants.ICON_TAB_GED;
	}
	
	private void listDirectory(Path p)
	{
		entries.clear();
		panneauCenter.removeAll();
		panneauCenter.revalidate();
		panneauCenter.repaint();
		SwingWorker<Void, GedEntry<T>> sw = new SwingWorker<>() 
		{
			protected Void doInBackground() throws Exception {
				
				try(Stream<Path> s = GedService.inst().listDirectory(p))
				{
					s.forEach(p->{
						try 
						{
							if(!Files.isDirectory(p))
							{
								@SuppressWarnings("unchecked")
								GedEntry<T> ged = (GedEntry<T>) GedService.inst().read(p);
								publish(ged);
							}
						}
						catch (Exception e) 
						{
							logger.error(e);
						}
					});
					return null;
				}
			}
			
			@Override
			protected void process(List<GedEntry<T>> chunks) {
				for(GedEntry<T> g : chunks)
					addEntry(g);
			}
			
			@Override
			protected void done()
			{
				panneauCenter.revalidate();
				panneauCenter.repaint();
			}
			
		};
		ThreadManager.getInstance().runInEdt(sw, "loading ged elements for " + p);
	}
	

}
