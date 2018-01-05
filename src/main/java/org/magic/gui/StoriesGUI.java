package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import org.jsoup.Jsoup;
import org.magic.api.beans.MTGStory;
import org.magic.gui.renderer.MTGStoryListRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.StoryProvider;
import org.magic.services.ThreadManager;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JSplitPane;

public class StoriesGUI extends JPanel {

	JLabel lblLoading;
	StoryProvider provider;
	JList<MTGStory> listResult;
	DefaultListModel<MTGStory> resultListModel;
	JEditorPane editorPane;
	
	public StoriesGUI() {
		provider = new StoryProvider(MTGControler.getInstance().getLocale());
		
		initGUI();
		initStories();
	}

	private void initGUI() {
		JScrollPane scrollList = new JScrollPane();
		JScrollPane scrollEditor = new JScrollPane();
		
		setLayout(new BorderLayout(0, 0));
		resultListModel= new DefaultListModel<MTGStory>();
		
		listResult = new JList<MTGStory>(resultListModel);
		listResult.setCellRenderer(new MTGStoryListRenderer());
		listResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listResult.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
		        if (evt.getClickCount() == 1) {
		        	evt.consume();

		        		ThreadManager.getInstance().execute(new Runnable() {
							
							@Override
							public void run() {
								lblLoading.setVisible(true);
					        	try {
					        		editorPane.setText(Jsoup.connect(listResult.getSelectedValue().getUrl().toString()).get().select("div#content-detail-page-of-an-article").html());
					        		
					        	} catch (Exception e) {
									JOptionPane.showMessageDialog(null, e.getMessage(),MTGControler.getInstance().getLangService().getCapitalize("ERROR"),JOptionPane.ERROR_MESSAGE);
								}
					        	lblLoading.setVisible(false);			
							}
						},"Load story");
		        		
		        	
		        }
		        else
		        {
		        	try {
						Desktop.getDesktop().browse(listResult.getSelectedValue().getUrl().toURI());
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, e.getMessage(),MTGControler.getInstance().getLangService().getCapitalize("ERROR"),JOptionPane.ERROR_MESSAGE);
					}
		        }
		    }
		});
		
		
		scrollList.setViewportView(listResult);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		
		JButton btnLoadNext = new JButton("Load Next");
		btnLoadNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				initStories();
			}
		});
		panel.add(btnLoadNext);
		
		lblLoading = new JLabel(MTGConstants.ICON_LOADING);
		lblLoading.setVisible(false);
		panel.add(lblLoading);
		
		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		
			HTMLEditorKit kit = new HTMLEditorKit();
			editorPane.setEditorKit(kit);
			Document doc = kit.createDefaultDocument();
			editorPane.setDocument(doc);
			scrollEditor.setViewportView(editorPane);
		
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setLeftComponent(scrollList);
		splitPane.setRightComponent(scrollEditor);
		add(splitPane, BorderLayout.CENTER);
	}
	
	public void initStories()
	{
		ThreadManager.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				lblLoading.setVisible(true);
				
					for(MTGStory story:provider.next())
						resultListModel.addElement(story);
					lblLoading.setVisible(false);
			}
		}, "loading stories");
	}
	
	
}

