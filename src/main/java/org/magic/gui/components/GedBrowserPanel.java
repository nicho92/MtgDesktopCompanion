package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.magic.api.beans.GedEntry;
import org.magic.api.interfaces.MTGGedStorage;
import org.magic.api.interfaces.MTGStorable;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.GedEntryTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.services.workers.AbstractObservableWorker;
import org.magic.tools.ImageTools;
import org.magic.tools.MTG;
import org.magic.tools.UITools;

public class GedBrowserPanel extends MTGUIComponent {
	
	private static final long serialVersionUID = 1L;
	private JTable table;
	private JComboBox<MTGGedStorage> cboGed;
	private GedEntryTableModel model;
	private AbstractBuzyIndicatorComponent buzy;
	//private ImagePanel imgPanel;
	private AbstractObservableWorker<List<GedEntry<MTGStorable>>, GedEntry<MTGStorable>, MTGGedStorage> sw;
	
	public GedBrowserPanel() {
		setLayout(new BorderLayout(0, 0));
		model = new GedEntryTableModel();
		cboGed = UITools.createCombobox(MTGGedStorage.class,true);
		var panneauHaut = new JPanel();
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		//imgPanel = new ImagePanel(true, false, true);
	//	imgPanel.setPreferredSize(new Dimension(250,0));
		cboGed.setSelectedItem(MTG.getEnabledPlugin(MTGGedStorage.class));
		cboGed.addItemListener(il->reload());
		table = UITools.createNewTable(model);
		UITools.initTableFilter(table);
		
		
		table.setDefaultRenderer(Long.class, (JTable t, Object value, boolean isSelected, boolean hasFocus,int row, int column)->{ 
				var lab = new DefaultTableCellRenderer();
				lab.setText(UITools.humanReadableSize((Long)value));
				return lab;
		});
		
		
//		table.getSelectionModel().addListSelectionListener(il->{
//			
//			if(!il.getValueIsAdjusting())
//			{
//				GedEntry<?> entry = UITools.getTableSelection(table, 0);
//				try {
//					imgPanel.setImg(ImageTools.read(entry.getContent()));
//				} catch (IOException e) {
//					logger.error(e);
//				}
//			}
//			
//		});
//		
		
		panneauHaut.add(cboGed);
		panneauHaut.add(buzy);
		add(panneauHaut, BorderLayout.NORTH);
		add(new JScrollPane(table),BorderLayout.CENTER);
//		add(imgPanel,BorderLayout.EAST);
		reload();
	}
	
	private void reload() {
		
		if(sw!=null && !sw.isDone())
			sw.cancel(true);
		
		
		sw = new AbstractObservableWorker<List<GedEntry<MTGStorable>>, GedEntry<MTGStorable>, MTGGedStorage>(buzy,(MTGGedStorage)cboGed.getSelectedItem()) {
					@Override
					protected List<GedEntry<MTGStorable>> doInBackground() throws Exception {
						return plug.listAll();
					}
					
					@Override
					protected void notifyEnd() {
						model.bind(getResult());
					}

				};
				
				ThreadManager.getInstance().runInEdt(sw, "Loading Geds files");
		
	}

	@Override
	public String getTitle() {
		return "GED";
	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_GED;
	}
	
	
	
}

