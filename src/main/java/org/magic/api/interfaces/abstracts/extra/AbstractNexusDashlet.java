package org.magic.api.interfaces.abstracts.extra;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.api.cardnexus.tools.Utils;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.services.tools.CardNexusTools;

public abstract class AbstractNexusDashlet extends AbstractJDashlet {

    private static final long serialVersionUID = 1L;
    
    @Override
    public void initGUI() {
	CardNexusTools.initConfig();
	setLayout(new BorderLayout());
	getContentPane().add(getNexusComponent());
	
	if (getProperties().size() > 0) {
		var r = new Rectangle((int) Double.parseDouble(getString("x")), (int) Double.parseDouble(getString("y")),
				(int) Double.parseDouble(getString("w")), (int) Double.parseDouble(getString("h")));

		setBounds(r);
	}

    }

    protected abstract  JComponent getNexusComponent() ;

    @Override
    public void init() {
	//do nothing
    }

    @Override
    public ImageIcon getDashletIcon() {
	return new ImageIcon(Utils.getNexusImage());
    }
    
    
    @Override
  	public String getCategory() {
  		return "CardNexus";
  	}

}
