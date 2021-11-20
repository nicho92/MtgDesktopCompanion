package org.magic.gui;

import static org.magic.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.text.html.HTMLEditorKit;

import org.magic.api.beans.MTGStory;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.renderer.MTGStoryListRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.providers.StoryProvider;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.UITools;
import org.magic.tools.URLTools;
public class StoriesGUI extends MTGUIComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AbstractBuzyIndicatorComponent lblLoading;
	private transient StoryProvider provider;
	private JList<MTGStory> listResult;
	private DefaultListModel<MTGStory> resultListModel;
	private JEditorPane editorPane;

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_STORY;
	}
	
	@Override
	public String getTitle() {
		return capitalize("HISTORY_MODULE");
	}
	
	
	public StoriesGUI() {
		provider = new StoryProvider(MTGControler.getInstance().getLocale());

		initGUI();
		initStories();
	}

	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		resultListModel = new DefaultListModel<>();

		listResult = new JList<>(resultListModel);
		listResult.setCellRenderer(new MTGStoryListRenderer());
		listResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listResult.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 1) {
					evt.consume();

					lblLoading.start();
					var sw = new SwingWorker<String, Void>()
							{

								@Override
								protected String doInBackground() throws Exception {
									return URLTools.extractHtml(listResult.getSelectedValue().getUrl().toString()).select("div#content-detail-page-of-an-article").html();
								}
								@Override
								protected void done() {
									
									try {
										editorPane.setText(get());
										
									} catch (InterruptedException e) {
										Thread.currentThread().interrupt();
									} catch (Exception e) {
										MTGControler.getInstance().notify(e);
									}
									
									lblLoading.end();
								}
								
								
							};
					ThreadManager.getInstance().runInEdt(sw, "Load story");
				} else {
					try {
						UITools.browse(listResult.getSelectedValue().getUrl().toURI().toASCIIString());
					} catch (Exception e) {
						MTGControler.getInstance().notify(e);
					}
				}
			}
		});

		

		var panel = new JPanel();
		add(panel, BorderLayout.NORTH);

		var btnLoadNext = UITools.createBindableJButton("Load next",MTGConstants.ICON_OPEN,KeyEvent.VK_N,"Storie loading");
		btnLoadNext.addActionListener(ae -> initStories());
		panel.add(btnLoadNext);

		lblLoading = AbstractBuzyIndicatorComponent.createLabelComponent();
		panel.add(lblLoading);

		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setContentType(URLTools.HEADER_HTML);

		var kit = new HTMLEditorKit();
		editorPane.setEditorKit(kit);
		var doc = kit.createDefaultDocument();
		editorPane.setDocument(doc);
		var splitPane = new JSplitPane();
		splitPane.setLeftComponent(new JScrollPane(listResult));
		splitPane.setRightComponent(new JScrollPane(editorPane));
		add(splitPane, BorderLayout.CENTER);
	}

	public void initStories() {
		
		SwingWorker<Void, MTGStory> sw = new SwingWorker<>() {
			
			@Override
			protected Void doInBackground(){
				try {
					for (MTGStory story : provider.next())
						resultListModel.addElement(story);
				} catch (IOException e) {
					logger.error(e);
				}
				
				return null;
			}
			
			@Override
			protected void done() {
				lblLoading.end();
			}
			
		};
		
		
		
		lblLoading.start();
		ThreadManager.getInstance().runInEdt(sw, "loading stories");
	}

}
