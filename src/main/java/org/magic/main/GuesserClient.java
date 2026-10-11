package org.magic.main;

import java.sql.SQLException;

import javax.swing.SwingUtilities;

import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.hangman.HangmanPanel;
import org.magic.services.MTGControler;

public class GuesserClient {



    public static void main(String[] args) throws SQLException {
	
	MTGControler.getInstance().init();
	
	
	
        SwingUtilities.invokeLater(() -> {
            var f = MTGUIComponent.createJFrame(new HangmanPanel(), true, true);
            MTGControler.getInstance().getLafService().setLookAndFeel(f,MTGControler.getInstance().get("lookAndFeel"), false);
            f.setVisible(true);
        });
    }

    
}
