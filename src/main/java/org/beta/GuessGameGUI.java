package org.beta;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.magic.api.interfaces.MTGPictureEditor;
import org.magic.gui.components.ImagePanel2;
import org.magic.services.GuessGameService;
import org.magic.services.MTGControler;
import org.magic.services.tools.MTG;

public class GuessGameGUI extends JPanel{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static void main(String[] args) throws Exception {
	MTGControler.getInstance().init();
	var frame = new JFrame();
	     frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	     
	     frame.getContentPane().add(new GuessGameGUI());
	     
	     frame.setVisible(true);
	     
	
	

      }

    private GuessGameService game;
    private ImagePanel2 panelPictures;

    private JPanel createAlphabetPanel() {
	    var panel = new JPanel(new GridLayout(3, 12, 5, 5));

	    for (char letter = 'A'; letter <= 'Z'; letter++) {
	        var button = new JButton(String.valueOf(letter));

	        button.addActionListener(e -> {
	            char selectedLetter = e.getActionCommand().charAt(0);
	            game.suggest(selectedLetter);
	            try {
			refresh();
		    } catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		    }
	        });

	        panel.add(button);
	    }

	    return panel;
	}
    
    
    	public void refresh() throws IOException
    	{
    	    	var img = MTG.getPlugin("FunCardMaker",MTGPictureEditor.class).getPicture(game.getCurrent(), game.getCurrent().getEdition());
    		panelPictures.setImg(img);
    	}
    
	public GuessGameGUI() {
		setLayout(new BorderLayout());
		
		
		game = new GuessGameService();
		
		panelPictures = new ImagePanel2(false,false,true,false);
		var btnNewGame = new JButton("NEW GAME");
		var panelLeft = new JPanel();
		var panelHaut = new JPanel();
		
		panelPictures.setPreferredSize(new Dimension(500, 10));
		
		
		panelLeft.setLayout(new BorderLayout());
		panelLeft.add(createAlphabetPanel(),BorderLayout.SOUTH);
		
		add(panelPictures, BorderLayout.EAST);
		add(panelLeft, BorderLayout.CENTER);
		
		
		panelLeft.add(panelHaut, BorderLayout.NORTH);
		
		panelHaut.add(btnNewGame);
		
		btnNewGame.addActionListener(_->{
		    
		    try {
			game.init();
			refresh();
		    } catch (IOException e) {
			MTGControler.getInstance().notify(e);
		    }
		     
		});
		
		
	}

}
