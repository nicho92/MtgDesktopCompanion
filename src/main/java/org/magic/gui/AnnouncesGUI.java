package org.magic.gui;

import static org.magic.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.magic.api.beans.Announce;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.GedPanel;
import org.magic.gui.components.shops.StockItemPanel;
import org.magic.gui.models.AnnouncesTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.services.workers.AbstractObservableWorker;
import org.magic.tools.MTG;
import org.magic.tools.UITools;

public class AnnouncesGUI extends MTGUIComponent {
	
	private static final long serialVersionUID = 1L;
	private GedPanel<Announce> gedPanel;
	private StockItemPanel itemsPanel;
	private AnnouncesTableModel modelAnnounces;
	private AbstractBuzyIndicatorComponent buzy;
	
	public AnnouncesGUI() {
		setLayout(new BorderLayout(0, 0));
		
		modelAnnounces = new AnnouncesTableModel();
		gedPanel = new GedPanel<>();
		itemsPanel = new StockItemPanel();
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		var splitCentral = new JSplitPane();
		var tabbedPane = new JTabbedPane(SwingConstants.TOP);
		var tableAnnounces = UITools.createNewTable(modelAnnounces);
		var panneauHaut = new JPanel();
		var btnNewButton = new JButton(MTGConstants.ICON_NEW);
		var btnSaveButton = new JButton(MTGConstants.ICON_SAVE);
		
		splitCentral.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		
		add(splitCentral, BorderLayout.CENTER);
		add(panneauHaut, BorderLayout.NORTH);
		splitCentral.setRightComponent(tabbedPane);
		splitCentral.setLeftComponent(new JScrollPane(tableAnnounces));
		panneauHaut.add(btnNewButton);
		panneauHaut.add(btnSaveButton);
		panneauHaut.add(buzy);
		
		
		UITools.addTab(tabbedPane, itemsPanel);
		UITools.addTab(tabbedPane, gedPanel);
		
	}

	
	@Override
	public void onFirstShowing() {
			var sw = new AbstractObservableWorker<List<Announce>, Announce, MTGDao>(buzy,MTG.getEnabledPlugin(MTGDao.class)) {
				
				@Override
				protected List<Announce> doInBackground() throws Exception {
						return plug.listAnnounces();
				}
				
				@Override
				protected void notifyEnd() {
					modelAnnounces.init(getResult());
				}
			};
			
			ThreadManager.getInstance().runInEdt(sw, "loading announces");
			
	}
	
	
	@Override
	public String getTitle() {
		return capitalize("ANNOUNCES_MODULE");
	}
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_ANNOUNCES;
	}
	

}
