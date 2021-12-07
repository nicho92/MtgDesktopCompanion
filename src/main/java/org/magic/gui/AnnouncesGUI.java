package org.magic.gui;

import static org.magic.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.Announce;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.AnnounceDetailPanel;
import org.magic.gui.components.ContactPanel;
import org.magic.gui.components.GedPanel;
import org.magic.gui.components.shops.StockItemPanel;
import org.magic.gui.models.AnnouncesTableModel;
import org.magic.gui.renderer.standard.DateTableCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
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
	private AnnounceDetailPanel detailsPanel;
	
	public AnnouncesGUI() {
		setLayout(new BorderLayout(0, 0));
		
		modelAnnounces = new AnnouncesTableModel();
		gedPanel = new GedPanel<>();
		itemsPanel = new StockItemPanel();
		detailsPanel = new AnnounceDetailPanel();
		contactPanel = new ContactPanel(false);
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		var splitCentral = new JSplitPane();
		var tabbedPane = new JTabbedPane(SwingConstants.TOP);
		tableAnnounces = UITools.createNewTable(modelAnnounces);
		var panneauHaut = new JPanel();
		var btnNew = UITools.createBindableJButton("New",MTGConstants.ICON_NEW, KeyEvent.VK_N,"newAnnounce");
		var btnSave = UITools.createBindableJButton("Save",MTGConstants.ICON_SAVE, KeyEvent.VK_S,"saveAnnounce");
		var btnDelete = UITools.createBindableJButton("Delete",MTGConstants.ICON_DELETE, KeyEvent.VK_D,"deleteAnnounce");
		var btnUpdate = UITools.createBindableJButton("Update",MTGConstants.ICON_REFRESH, KeyEvent.VK_U,"updateAnnounce");
		
		splitCentral.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		add(splitCentral, BorderLayout.CENTER);
		add(panneauHaut, BorderLayout.NORTH);
		splitCentral.setRightComponent(tabbedPane);
		splitCentral.setLeftComponent(new JScrollPane(tableAnnounces));
		
		panneauHaut.add(btnUpdate);
		panneauHaut.add(btnNew);
		panneauHaut.add(btnSave);
		panneauHaut.add(btnDelete);
		panneauHaut.add(buzy);
		
		UITools.addTab(tabbedPane, detailsPanel);
		UITools.addTab(tabbedPane, itemsPanel);
		UITools.addTab(tabbedPane, contactPanel);
		UITools.addTab(tabbedPane, gedPanel);
		splitCentral.setDividerLocation(.5);
		
		
		btnSave.setEnabled(false);
		btnDelete.setEnabled(false);
		tableAnnounces.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		tableAnnounces.setDefaultRenderer(Date.class, new DateTableCellEditorRenderer(true));
		tableAnnounces.getSelectionModel().addListSelectionListener(lsl->{
			
			if(!lsl.getValueIsAdjusting())
			{
					Announce a = UITools.getTableSelection(tableAnnounces,0);
					
					if(a!=null)
					{
						contactPanel.setContact(a.getContact());
						itemsPanel.initItems(a.getItems());
						gedPanel.init(Announce.class, a);
						detailsPanel.setAnnounce(a);
					}
					btnDelete.setEnabled(a!=null);
					btnSave.setEnabled(a!=null);
			}
		});
		
		
		btnUpdate.addActionListener(al->load());
		
		btnNew.addActionListener(al->{
			var a = new Announce();
				a.setContact(MTGControler.getInstance().getWebConfig().getContact());
				detailsPanel.setAnnounce(a);
				itemsPanel.initItems(a.getItems());
				modelAnnounces.addItem(a);
		});
		
		btnSave.addActionListener(al->{
			
			Announce b = detailsPanel.getAnnounce();
				 b.setUpdated(true);
				 b.setItems(itemsPanel.getItems());
				 try {
					 b.setMainImage(gedPanel.listEntries().get(0));
				 }catch(Exception e)
				 {
					 logger.warn("no entries in documents");
				 }
							 
			var sw = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws SQLException {
					MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateAnnounce(b);
					return null;
				}
				
				@Override
				protected void done() {
					
					try {
						get();
					}
					catch(Exception e)
					{
						e.printStackTrace();
						MTGControler.getInstance().notify(e);
					}
					
					
					modelAnnounces.fireTableDataChanged();
				}
			};
				
			ThreadManager.getInstance().runInEdt(sw, "saving announce " + b);
			
							 
		});
		
		
		
		
		btnDelete.addActionListener(al->{
			
			Announce a = UITools.getTableSelection(tableAnnounces,0);
			
			int res = JOptionPane.showConfirmDialog(this, "Delete "  +  a + " ?");
			
			if(res==JOptionPane.YES_OPTION)
			{
				try {
					
					if(a.getId()>-1)
						MTG.getEnabledPlugin(MTGDao.class).deleteAnnounce(a);
					
					modelAnnounces.removeItem(a);
				} catch (SQLException e) {
					MTGControler.getInstance().notify(e);
				}
			}
		});
	}


	@Override
	public void onFirstShowing() {
			load();
	}
	
	
	private void load() {
		var sw = new AbstractObservableWorker<List<Announce>, Announce, MTGDao>(buzy,MTG.getEnabledPlugin(MTGDao.class)) {
			
			@Override
			protected List<Announce> doInBackground() throws Exception {
					return plug.listAnnounces(true);
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
