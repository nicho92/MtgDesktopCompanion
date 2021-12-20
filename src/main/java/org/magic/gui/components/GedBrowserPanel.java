package org.magic.gui.components;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import org.magic.api.beans.GedEntry;
import org.magic.api.interfaces.MTGGedStorage;
import org.magic.api.interfaces.MTGStorable;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.GedEntryTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.services.workers.AbstractObservableWorker;
import org.magic.tools.MTG;
import org.magic.tools.UITools;

public class GedBrowserPanel extends MTGUIComponent {
	
	private static final long serialVersionUID = 1L;
	private JTable table;
	private JComboBox<MTGGedStorage> cboGed;
	private GedEntryTableModel model;
	private AbstractBuzyIndicatorComponent buzy;
	
	
	public GedBrowserPanel() {
		setLayout(new BorderLayout(0, 0));
		model = new GedEntryTableModel();
		cboGed = UITools.createCombobox(MTGGedStorage.class,true);
		var panneauHaut = new JPanel();
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		
		cboGed.setSelectedItem(MTG.getEnabledPlugin(MTGGedStorage.class));
		cboGed.addItemListener(il->reload());
		table = UITools.createNewTable(model);
		panneauHaut.add(cboGed);
		panneauHaut.add(buzy);
		add(panneauHaut, BorderLayout.NORTH);
		add(new JScrollPane(table),BorderLayout.CENTER);
		
		
		reload();
		
	}
	
	
	private void reload() {
		
		var sw = new AbstractObservableWorker<List<GedEntry<MTGStorable>>, GedEntry<MTGStorable>, MTGGedStorage>(buzy,(MTGGedStorage)cboGed.getSelectedItem()) {
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

