package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.magic.api.beans.MTGStory;
import org.magic.api.exports.impl.MTGDesktopCompanionExport;
import org.magic.gui.renderer.MTGStoryListRenderer;
import org.magic.services.MTGControler;
import org.magic.services.StoryProvider;
import org.magic.services.ThreadManager;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;

public class StoriesGUI extends JPanel {
//
//	
//	public static void main(String[] args) {
//		JFrame f = new JFrame();
//		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		f.getContentPane().add(new StoriesGUI());
//		f.pack();
//		f.setVisible(true);
//		
//	}
	
	StoryProvider provider;
	JList<MTGStory> listResult;
	DefaultListModel<MTGStory> resultListModel;
	public StoriesGUI() {
		provider = new StoryProvider(MTGControler.getInstance().getLocale());
		
		initGUI();
		initStories();
	}

	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		resultListModel= new DefaultListModel<MTGStory>();
		listResult = new JList<MTGStory>(resultListModel);
		listResult.setCellRenderer(new MTGStoryListRenderer());
		listResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listResult.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
		        if (evt.getClickCount() == 2) {
		        	evt.consume();
		        	try {
						Desktop.getDesktop().browse(listResult.getSelectedValue().getUrl().toURI());
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, e.getMessage(),MTGControler.getInstance().getLangService().getCapitalize("ERROR"),JOptionPane.ERROR_MESSAGE);
					}
		            
		        } 		
		    }
		});
		
		
		scrollPane.setViewportView(listResult);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		
		JButton btnLoadNext = new JButton("Load Next");
		btnLoadNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				initStories();
			}
		});
		panel.add(btnLoadNext);
	}
	
	public void initStories()
	{
		ThreadManager.getInstance().execute(new Runnable() {
			@Override
			public void run() {
					for(MTGStory story:provider.next())
						resultListModel.addElement(story);
			}
		}, "loading stories");
	}
	
	
}

