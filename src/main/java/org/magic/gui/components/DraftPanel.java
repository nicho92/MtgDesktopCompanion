package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.SealedPack;
import org.magic.gui.models.SealedPackTableModel;
import org.magic.gui.renderer.IntegerCellEditor;
import org.magic.gui.renderer.MagicEditionListEditor;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class DraftPanel extends JPanel {
	
	private JPanel panelHaut;
	private JPanel panelBottom;
	private JButton btnSaveDeck;
	private JSplitPane panelCenter;
	
	private JButton btnAddBoosters;
	private JScrollPane scrollWest;
	private JTable table;
	private SealedPackTableModel model;
	
	public DraftPanel() {
		initGUI();
	}
	
	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		panelHaut = new JPanel();
		add(panelHaut, BorderLayout.NORTH);
		
		btnAddBoosters = new JButton(MTGConstants.ICON_NEW);
		btnAddBoosters.addActionListener(ae->addBooster());
		panelHaut.add(btnAddBoosters);
		
		panelBottom = new JPanel();
		add(panelBottom, BorderLayout.SOUTH);
		
		btnSaveDeck = new JButton(MTGConstants.ICON_SAVE);
		btnSaveDeck.addActionListener(e->save());
		
		JButton btnOpen = new JButton(MTGControler.getInstance().getLangService().getCapitalize("OPEN"));
		btnOpen.setEnabled(false);
		panelBottom.add(btnOpen);
		btnOpen.addActionListener(ae->open());
		panelBottom.add(btnSaveDeck);
		
		panelCenter = new JSplitPane();
		panelCenter.setResizeWeight(0.5);
		panelCenter.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panelCenter.addComponentListener(new ComponentAdapter() {
		      @Override
		      public void componentShown(ComponentEvent componentEvent) {
		    	panelCenter.setDividerLocation(.5);
		        removeComponentListener(this);
		      }
		    });
		add(panelCenter, BorderLayout.CENTER);
		model = new SealedPackTableModel();
		table = new JTable(model);
		table.getColumnModel().getColumn(1).setCellEditor(new IntegerCellEditor());
		//table.getColumnModel().getColumn(0).setCellEditor();
		
		scrollWest = new JScrollPane();
		scrollWest.setViewportView(table);
		
		add(scrollWest, BorderLayout.WEST);
		
	}
	
	private void addBooster() {
		model.add(new MagicEdition(), 0);
	}

	protected void open() {

	}
	

	protected void save() {
		// TODO Auto-generated method stub
		
	}


}
