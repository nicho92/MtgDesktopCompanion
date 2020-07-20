package org.magic.gui.components;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.magic.api.criterias.MTGCrit;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

import groovy.transform.stc.FirstParam.Component;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdvancedSearchQueryPanel extends JDialog {
	
	
	private GridLayout layout;
	private JPanel pContent;
	private transient List<MTGCrit> crits;
	
	
	
	public AdvancedSearchQueryPanel() {
		setLayout(new BorderLayout(0, 0));
		
		crits = new ArrayList<>();
		
		layout = new GridLayout(1,1);
		
		JButton btnNewButton = new JButton(MTGConstants.ICON_NEW);
		JButton btnSearch = new JButton(MTGConstants.ICON_SEARCH);
		
		JPanel bottom = new JPanel();
		
		bottom .add(btnNewButton);
		bottom.add(btnSearch);
		
		add(bottom, BorderLayout.SOUTH);
		
		pContent = new JPanel();
		add(pContent, BorderLayout.CENTER);
		pContent.setLayout(layout);
		
		
		
		btnNewButton.addActionListener(al->
			SwingUtilities.invokeLater(()->{
				pContent.add(new CriteriaComponent());
				layout.setRows(layout.getRows()+1);
				pContent.revalidate();
				pContent.repaint();
			})
		);
		
		
		btnSearch.addActionListener(al->{
			
			crits.clear();
			for(int i = 0 ; i<pContent.getComponentCount();i++)
			{
				if(pContent.getComponent(i) instanceof CriteriaComponent)
				{
					crits.add(((CriteriaComponent)pContent.getComponent(i)).getMTGCriteria());
				}
			}
			
			dispose();
		});
		
		
		setModal(true);
	}
	
	public List<MTGCrit> getCrits() {
		return crits;
	}
	

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws SQLException {
		
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		MTGControler.getInstance().getEnabled(MTGDao.class).init();
		
		JFrame f = new JFrame();
		
		f.getContentPane().add(new AdvancedSearchQueryPanel());
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		

	}

}
