package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.Icon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTreeTable;
import org.magic.api.beans.MagicCollection;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.models.conf.CollectionAnalyzerTreeTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class CollectionAnalyzerDashlet extends AbstractJDashlet {
	

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		MTGControler.getInstance().getEnabled(MTGDao.class).init();
		
	
		JFrame f = new JFrame();
		f.getContentPane().setLayout(new BorderLayout());
		JDesktopPane jif = new JDesktopPane();
		CollectionAnalyzerDashlet dash = new CollectionAnalyzerDashlet();
		dash.setVisible(true);
		jif.add(dash);
		
		f.getContentPane().add(jif, BorderLayout.CENTER);
		f.setSize(640, 480);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		
		
	}
	
	
	private JXTreeTable treeTable;
	private CollectionAnalyzerTreeTableModel model;
	
	public CollectionAnalyzerDashlet() {
		super();
		initGUI();
	}

	@Override
	public void initGUI() {
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		
		treeTable = new JXTreeTable();
		getContentPane().add(new JScrollPane(treeTable), BorderLayout.CENTER);
	}

	@Override
	public void init() {
		model = new CollectionAnalyzerTreeTableModel(new MagicCollection("Library"));
		treeTable.setTreeTableModel(model);
	}

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_DASHBOARD;
	}

	
	@Override
	public String getName() {
		return "Collection Analyser";
	}
}
