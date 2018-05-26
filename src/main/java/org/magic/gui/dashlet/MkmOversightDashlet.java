package org.magic.gui.dashlet;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.api.mkm.services.InsightService;
import org.jdesktop.swingx.JXTable;
import org.magic.api.exports.impl.MKMFileWantListExport;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.models.MkmInsightModel;

public class MkmOversightDashlet extends AbstractJDashlet {
	
	private transient InsightService service;
	private MkmInsightModel model;
	
	public MkmOversightDashlet() {
		super();
		setFrameIcon(new ImageIcon(MKMFileWantListExport.class.getResource("/icons/plugins/mkm.png")));
	}
	
	
	@Override
	public String getName() {
		return "Mkm Oversight";
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	@Override
	public void initGUI() 
	{
		getContentPane().setLayout(new BorderLayout(0, 0));
		model=new MkmInsightModel();
		JPanel panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);
		
		JComboBox<String> comboBox = new JComboBox<>();
		panneauHaut.add(comboBox);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JXTable table = new JXTable(model);
		scrollPane.setViewportView(table);

		setVisible(true);
	}

	@Override
	public void init() {
		service=new InsightService();
	}

}
