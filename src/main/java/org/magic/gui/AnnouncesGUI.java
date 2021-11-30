package org.magic.gui;

import static org.magic.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.Announce;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.ContactPanel;
import org.magic.gui.components.GedPanel;
import org.magic.gui.components.shops.StockItemPanel;
import org.magic.gui.models.AnnouncesTableModel;
import org.magic.gui.renderer.standard.DateTableCellEditorRenderer;
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
	private ContactPanel contactPanel;
	private JXTable tableAnnounces;
	
	public AnnouncesGUI() {
		setLayout(new BorderLayout(0, 0));
		
		modelAnnounces = new AnnouncesTableModel();
		gedPanel = new GedPanel<>();
		itemsPanel = new StockItemPanel();
		contactPanel = new ContactPanel(true);
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		var splitCentral = new JSplitPane();
		var tabbedPane = new JTabbedPane(SwingConstants.TOP);
		tableAnnounces = UITools.createNewTable(modelAnnounces);
		var panneauHaut = new JPanel();
		var btnNew = new JButton(MTGConstants.ICON_NEW);
		var btnSave = new JButton(MTGConstants.ICON_SAVE);
		var btnDelete = new JButton(MTGConstants.ICON_DELETE);
		
		splitCentral.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		add(splitCentral, BorderLayout.CENTER);
		add(panneauHaut, BorderLayout.NORTH);
		splitCentral.setRightComponent(tabbedPane);
		splitCentral.setLeftComponent(new JScrollPane(tableAnnounces));
		
		panneauHaut.add(btnNew);
		panneauHaut.add(btnSave);
		panneauHaut.add(btnDelete);
		panneauHaut.add(buzy);
		
		
		UITools.addTab(tabbedPane, itemsPanel);
		UITools.addTab(tabbedPane, contactPanel);
		UITools.addTab(tabbedPane, gedPanel);
		splitCentral.setDividerLocation(.5);
		
		
		tableAnnounces.setDefaultRenderer(Date.class, new DateTableCellEditorRenderer(true));
		tableAnnounces.getSelectionModel().addListSelectionListener(lsl->{
			
			if(!lsl.getValueIsAdjusting())
			{
				Announce a = UITools.getTableSelection(tableAnnounces,0);
				
				contactPanel.setContact(a.getContact());
				itemsPanel.initItems(a.getItems());
				gedPanel.init(Announce.class, a);
				
			}
			
			
		});
		

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
					tableAnnounces.packAll();
				
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
