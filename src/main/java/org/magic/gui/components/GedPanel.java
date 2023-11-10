package org.magic.gui.components;

import static org.magic.services.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.apache.commons.io.FilenameUtils;
import org.magic.api.beans.technical.GedEntry;
import org.magic.api.interfaces.MTGGedStorage;
import org.magic.api.interfaces.MTGSerializable;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.renderer.GedEntryComponent;
import org.magic.gui.components.webcam.WebcamSnapShotComponent;
import org.magic.gui.decorators.FileDropDecorator;
import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

import com.jogamp.newt.event.KeyEvent;
public class GedPanel<T extends MTGSerializable> extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JPanel panneauCenter;
	private Class<T> classe;
	private transient T instance;
	private ImagePanel viewPanel;
	private AbstractBuzyIndicatorComponent buzy;
	private JButton btnLoadFromUrl;
	private JButton btnLoadFromWebcam;

	public void init(Class<T> t, T instance)
	{
		this.classe=t;
		this.instance=instance;

		btnLoadFromUrl.setEnabled(instance!=null);
		btnLoadFromWebcam.setEnabled(instance!=null);


		if(isVisible())
			onVisible();
	}

	@Override
	public void onVisible() {

		if(classe==null)
			return;

		logger.debug("Show ged for {}",classe.getSimpleName());
		try {
			listDirectory(MTG.getEnabledPlugin(MTGGedStorage.class).getPath(classe,instance));
		} catch (IOException e) {
			logger.error(e);
		}

	}

	public GedPanel() {
		setLayout(new BorderLayout());
		var panneauHaut = new JPanel();
		panneauCenter = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		viewPanel = new ImagePanel(true, false, true);
		btnLoadFromUrl = UITools.createBindableJButton("", MTGConstants.ICON_WEBSITE, KeyEvent.VK_U, "importUrl");
		btnLoadFromWebcam= UITools.createBindableJButton("", MTGConstants.ICON_WEBCAM, KeyEvent.VK_W, "importwebcam");


		btnLoadFromUrl.setEnabled(false);
		btnLoadFromWebcam.setEnabled(false);

		buzy = AbstractBuzyIndicatorComponent.createProgressComponent();
		add(panneauHaut, BorderLayout.NORTH);
		add(panneauCenter, BorderLayout.CENTER);

		panneauHaut.add(new JLabel(capitalize("DRAG_HERE")));
		panneauHaut.add(btnLoadFromUrl);
		panneauHaut.add(btnLoadFromWebcam);
		panneauHaut.add(buzy);

		btnLoadFromWebcam.addActionListener(al->{

			var g = new WebcamSnapShotComponent();
			var diag = MTGUIComponent.createJDialog(g, true, true);

			diag.addWindowListener(new WindowAdapter() {

				@Override
				public void windowClosing(WindowEvent e) {
					g.onDestroy();
				}

			});


			g.getBtnClose().addActionListener(l->{
				g.onDestroy();
				diag.dispose();
			});

			diag.setVisible(true);



			var img = g.getSnappedImages();
			if(!img.isEmpty()) {
				buzy.start(img.size());
				SwingWorker<Void,File> sw = new SwingWorker<>() {

					@Override
					protected Void doInBackground() throws Exception {
						for(var snap : img)
						{
							var f = FileTools.createTempFile("picture",".png");
							ImageTools.saveImageInPng(snap,f);
							publish(f);
						}
						return null;
					}

					@Override
					protected void process(List<File> chunks)
					{
						buzy.progressSmooth(chunks.size());
						for(File f :chunks) {
							try {
								var entry = new GedEntry<>(FileTools.readFileAsBinary(f),classe, instance.getStoreId(),f.getName());
								MTG.getEnabledPlugin(MTGGedStorage.class).store(entry);
								addEntry(entry);
							} catch (IOException e) {
								logger.error(e);
							}
						}
					}

					@Override
					protected void done() {
						buzy.end();
					}
				};

				ThreadManager.getInstance().runInEdt(sw, "importing snapshots");

			}
		});

		btnLoadFromUrl.addActionListener(al->{
			var url = JOptionPane.showInputDialog("URL");
			buzy.start();
			SwingWorker<Void, GedEntry<T>> sw = new SwingWorker<>() {
				@Override
				protected Void doInBackground() throws Exception {
						try {
							var temp = URLTools.readAsBinary(url);
							var entry = new GedEntry<>(temp,classe, instance.getStoreId(),FilenameUtils.getName(url));
							MTG.getEnabledPlugin(MTGGedStorage.class).store(entry);
							publish(entry);

						} catch (Exception e) {
							logger.error("Error uploading {}",url,e);
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
			ThreadManager.getInstance().runInEdt(sw, "Upload url " + url);

		});

		new FileDropDecorator().init(panneauCenter, (File[] files) -> {
			buzy.start(files.length);
			SwingWorker<Void, GedEntry<T>> sw = new SwingWorker<>() {
				@Override
				protected Void doInBackground() throws Exception {
					for(File f : files)
					{
						try {
							var entry = new GedEntry<>(FileTools.readFileAsBinary(f),classe, instance.getStoreId(),f.getName());
							MTG.getEnabledPlugin(MTGGedStorage.class).store(entry);
							publish(entry);

						} catch (Exception e) {
							logger.error("Error uploading {}",f,e);
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



	private void addEntry(GedEntry<T> c)  {

		if(c==null)
			return;

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
						var tmp = FileTools.createTempFile(e.getEntry().getName(), "."+e.getEntry().getExt());
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
					var ret = JOptionPane.showConfirmDialog(null, MTG.capitalize("DELETE"));

					if(ret==JOptionPane.YES_OPTION && MTG.getEnabledPlugin(MTGGedStorage.class).delete(c)) {
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
		panneauCenter.removeAll();
		panneauCenter.revalidate();
		panneauCenter.repaint();
		buzy.start();
		SwingWorker<Void, GedEntry<T>> sw = new SwingWorker<>()
		{
			protected Void doInBackground() throws Exception {

				try(Stream<Path> s = MTG.getEnabledPlugin(MTGGedStorage.class).listDirectory(p))
				{
					s.forEach(p->{
						try
						{
							if(!Files.isDirectory(p))
							{
								var ged = (GedEntry<T>) MTG.getEnabledPlugin(MTGGedStorage.class).read(p);
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
				buzy.end();
			}

		};
		ThreadManager.getInstance().runInEdt(sw, "loading ged elements for " + p);
	}


}
