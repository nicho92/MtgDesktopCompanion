package org.magic.main;


import javax.swing.SwingUtilities;

import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.composer.MtgCardComposer;
import org.magic.services.MTGControler;

public class ComposerClient {


    // =====================================================================
    // Demo
    // =====================================================================

    public static void main(String[] args) throws Exception {
	MTGControler.getInstance().init();
	
	SwingUtilities.invokeLater(() -> {
	    var frame = MTGUIComponent.createJFrame(new MtgCardComposer(), true, true);
	    MTGControler.getInstance().getLafService().setLookAndFeel(frame,MTGControler.getInstance().get("lookAndFeel"), false);
	    	 frame.setSize(1200, 900);
	    	 frame.setLocationRelativeTo(null);
	    	 frame.setVisible(true);
	});
    }
}
