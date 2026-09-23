package org.magic.gui.components.hangman;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.magic.api.pictureseditor.impl.MTGCompanionComposerProvider;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.ImagePanel2;
import org.magic.gui.components.widgets.VirtualKeyboardPanel;
import org.magic.services.GuessGameService;
import org.magic.services.GuessGameService.GameStatus;


public class HangmanPanel extends MTGUIComponent {

    private static final long serialVersionUID = 1L;
    private final GuessGameService game = new GuessGameService();
    private final MTGCompanionComposerProvider provider = new MTGCompanionComposerProvider();

    
    // Écran de jeu
    private JLabel attemptsLabel;
    private JLabel messageLabel;
    private VirtualKeyboardPanel keyboardPanel;
    private ImagePanel2 drawingPanel;
    private JButton replayButton;

    public HangmanPanel() {
        buildGameScreen();
  
    }

    public void onStartGame() {
        try {
	    game.init();
	} catch (IOException e1) {
	   logger.error(e1);
	}
        resetGameScreen();
    }

    // --- Écran de jeu --------------------------------------------------------

    private void buildGameScreen() {
	setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        drawingPanel = new ImagePanel2();
        drawingPanel.setPreferredSize(new Dimension(420, 220));
        add(drawingPanel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createVerticalStrut(8));

        attemptsLabel = new JLabel(" ", SwingConstants.CENTER);
        attemptsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(attemptsLabel);

        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setFont(messageLabel.getFont().deriveFont(Font.BOLD, 15f));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(messageLabel);

        centerPanel.add(Box.createVerticalStrut(12));

        keyboardPanel = new VirtualKeyboardPanel(VirtualKeyboardPanel.Layout.ORDERED, true);
        keyboardPanel.setKeyListener(this::onLetterPressed);
        keyboardPanel.setAutoDisableOnPress(true);
        keyboardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(keyboardPanel);

        add(centerPanel, BorderLayout.CENTER);

        replayButton = new JButton("Restart");
        replayButton.addActionListener(_ -> onStartGame());
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(replayButton);
        add(bottomPanel, BorderLayout.SOUTH);

        
    }

    private void resetGameScreen() {
        keyboardPanel.resetKeys();
        messageLabel.setText(" ");
        replayButton.setText("Give Up");
        updateGameDisplay();
    }

    private void onLetterPressed(char letter) {
        game.suggest(letter);
        updateGameDisplay();
    }

    private void updateGameDisplay() {
        attemptsLabel.setText("Remaining : " + game.getAttemptsRemaining()+ " / " + game.getMaxAttemps());
       

        var status = game.getStatus();
        if (status == GameStatus.WIN) {
            messageLabel.setForeground(new Color(0, 128, 0));
            messageLabel.setText("Win !");
            endGame();
        } else if (status == GameStatus.LOST) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Lost");
            endGame();
        } else {
            messageLabel.setForeground(Color.DARK_GRAY);
            messageLabel.setText(" ");
            
            try {
    	    var img = provider.getPicture(game.getCurrent(), null);
    	    drawingPanel.setImg(img);
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
        }
        
       
	
    }

    private void endGame() {
        keyboardPanel.setAllKeysEnabled(false);
        replayButton.setText("Restart");
        drawingPanel.init(game.getResult());
        
    }
    
    @Override
    public String getTitle() {
	return "Card Guesser";
    }
}
