package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.SystemColor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.magic.api.beans.MTGCombo;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGComboProvider;
import org.magic.game.gui.components.HandPanel;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;

public class ComboFinderPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private AbstractBuzyIndicatorComponent buzy;
	private MagicCard mc; 
	private DefaultListModel<MTGCombo> model;
	private SwingWorker<List<MTGCombo>, MTGCombo> sw;
	
	@Override
	public String getTitle() {
		return "Combo";
	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_COMBO;
	}

	
	
	@Override
	public void onVisible() {
		init(mc);
	}
	
	
	
	public ComboFinderPanel() {
		initGUI();
	}
	
	private void initGUI() {
		model = new DefaultListModel<>();
		setLayout(new BorderLayout(0, 0));
		JList<MTGCombo> list = new JList<>(model);
		JTextArea textArea = new JTextArea();
		JPanel panneauCenter = new JPanel();
		buzy = AbstractBuzyIndicatorComponent.createProgressComponent();
		JPanel panneauHaut = new JPanel();
		HandPanel panneauBas = new HandPanel();
		
		
		panneauCenter.setLayout(new BorderLayout());
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		
		
		panneauCenter.add(new JScrollPane(textArea), BorderLayout.CENTER);
		add(new JScrollPane(list), BorderLayout.WEST);
		panneauHaut.add(buzy,BorderLayout.NORTH);
		add(panneauCenter,BorderLayout.CENTER);
		panneauCenter.add(panneauHaut, BorderLayout.NORTH);
		
		panneauBas.enableDragging(false);
		panneauBas.setPreferredSize(MTGControler.getInstance().getCardsGameDimension());
		panneauBas.setThumbnailSize(MTGControler.getInstance().getCardsGameDimension());
		
		panneauBas.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		panneauCenter.add(panneauHaut, BorderLayout.NORTH);
		panneauCenter.add(panneauBas, BorderLayout.SOUTH);
		
		list.addListSelectionListener(le->{
			MTGCombo cb = list.getSelectedValue();
			if(cb!=null)
			{
				textArea.setText(cb.getComment());
				panneauBas.initThumbnails(cb.getCards(), false, false);
			}
		});
		
		
		list.setCellRenderer((JList<? extends MTGCombo> listCbo, MTGCombo cbo, int index, boolean isSelected,boolean cellHasFocus)->{
				JLabel l= new JLabel(cbo.getName(),cbo.getPlugin().getIcon(),SwingConstants.LEFT);
				l.setOpaque(true);
				
				if(isSelected)
					l.setBackground(SystemColor.textHighlight);
				else
					l.setBackground(SystemColor.menu);
				
				return l;
		});
		
		
	}

	public void init(MagicCard mc)
	{
		this.mc=mc;
		
		if(!isVisible())
			return;
		
		if(mc==null)
			return;
		
		if(sw!=null && !sw.isDone())
			sw.cancel(true);
		
		buzy.start();
		sw = new SwingWorker<>() {
			
			@Override
			protected List<MTGCombo> doInBackground() throws Exception 
			{
				model.removeAllElements();
				List<MTGCombo> ret = new ArrayList<>();
				for(MTGComboProvider plug : MTGControler.getInstance().listEnabled(MTGComboProvider.class))
				{
					plug.getComboWith(mc).forEach(cbo->{
						ret.add(cbo);
						publish(cbo);
					});
				}
				return ret;
			}
			
			@Override
			protected void process(List<MTGCombo> chunks) {
				model.addAll(chunks);
				buzy.progressSmooth(chunks.size());
			}
			
			@Override
			protected void done() {
				buzy.end();
			}
		};
		
		ThreadManager.getInstance().runInEdt(sw, "loading combos for " + mc);
		
	}

}
