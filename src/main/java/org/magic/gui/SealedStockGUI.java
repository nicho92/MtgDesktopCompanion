package org.magic.gui;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.PackagesBrowserPanel;
import org.magic.gui.models.SealedPackTableModel;
import org.magic.gui.models.SealedStockModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class SealedStockGUI extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private PackagesBrowserPanel packagePanel;
	private SealedStockModel model;
	
	
	public SealedStockGUI() {
		
		model = new SealedStockModel();
		
		setLayout(new BorderLayout());
		
		packagePanel = new PackagesBrowserPanel(false);
		
		add(packagePanel,BorderLayout.WEST);
		
		add(new JScrollPane(new JXTable(model)),BorderLayout.CENTER);
		
		
	}
	
	@Override
	public String getTitle() {
		return MTGControler.getInstance().getLangService().getCapitalize("PACKAGES");
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_PACKAGE_SMALL;
	}
	
	@Override
	public void onFirstShowing() {
		packagePanel.initTree();
	}
}
