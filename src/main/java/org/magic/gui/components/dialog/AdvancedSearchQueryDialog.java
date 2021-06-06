package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.magic.api.criterias.MTGCrit;
import org.magic.gui.components.CriteriaComponent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class AdvancedSearchQueryDialog extends JDialog {
	
	
	private GridLayout layout;
	private JPanel pContent;
	private transient List<MTGCrit> crits;
	
	
	public AdvancedSearchQueryDialog() {
		setLayout(new BorderLayout(0, 0));
		
		crits = new ArrayList<>();
		
		layout = new GridLayout(1,1);
		
		var btnNewButton = new JButton(MTGConstants.ICON_NEW);
		var btnSearch = new JButton(MTGConstants.ICON_SEARCH);
		
		var bottom = new JPanel();
		
		bottom .add(btnNewButton);
		bottom.add(btnSearch);
		
		add(bottom, BorderLayout.SOUTH);
		
		pContent = new JPanel();
		add(pContent, BorderLayout.CENTER);
		pContent.setLayout(layout);
		
		
		
		btnNewButton.addActionListener(al->
			SwingUtilities.invokeLater(()->{
				
				var cc = new CriteriaComponent();
				
				var delete = new JButton("X");
				delete.addActionListener(el->{
					pContent.remove(cc);
					pContent.revalidate();
					pContent.repaint();

				});
				
				cc.addButton(delete,false);
				pContent.add(cc);
				layout.setRows(layout.getRows()+1);
				pContent.revalidate();
				pContent.repaint();
			})
		);
		
		
		btnSearch.addActionListener(al->{
			
			crits.clear();
			for(var i = 0 ; i<pContent.getComponentCount();i++)
			{
				if(pContent.getComponent(i) instanceof CriteriaComponent)
				{
					crits.add(((CriteriaComponent)pContent.getComponent(i)).getMTGCriteria());
				}
			}
			
			dispose();
		});
		
		setIconImage(MTGConstants.ICON_SEARCH_24.getImage());
		setTitle(MTGControler.getInstance().getLangService().get("ADVANCED_SEARCH"));
		setSize(new Dimension(875, 400));
		setModal(true);
		setLocationRelativeTo(null);
		btnNewButton.doClick();
	}
	
	public List<MTGCrit> getCrits() {
		return crits;
	}
	

	private static final long serialVersionUID = 1L;

}
