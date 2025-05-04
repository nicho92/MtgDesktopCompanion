package org.magic.gui.components;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;

import org.magic.api.beans.MTGCard;
import org.magic.api.interfaces.MTGIA;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;

public class IASuggestionPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private AbstractBuzyIndicatorComponent buzy;
	private JTextPane textPane;
	private List<MTGCard> cards = new ArrayList<>();
	
	
	public IASuggestionPanel() {
		
		JPanel panneauHaut = new JPanel();
		 textPane = new JTextPane();
		 textPane.setEditable(false);
		JPanel panneauBas = new JPanel();
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		
		setLayout(new BorderLayout());
		panneauBas.setLayout(new BorderLayout(0, 0));
		
		
		add(panneauHaut, BorderLayout.NORTH);
		add(new JScrollPane(textPane), BorderLayout.CENTER);
		add(panneauBas, BorderLayout.SOUTH);
		panneauHaut.add(buzy);
		
	}
	
	
	@Override
	public void onVisible() {
		init(cards);
	}
	
	
	public void init(List<MTGCard> cards )
	{
		this.cards=cards;
		
		
		if(!isVisible() || cards.isEmpty())
			return;
		
		
		buzy.start();
		var sw = new SwingWorker<String, Void>() {

				@Override
				protected String doInBackground() throws Exception {
					var result = "";
					
					if(cards.size()==1)
						result =  MTG.getEnabledPlugin(MTGIA.class).describe(cards.get(0));
					else
						result =  MTG.getEnabledPlugin(MTGIA.class).suggestDeckWith(cards);
					
					return result;
				}

				@Override
				protected void done() {
					
					try {
						textPane.setText(get());
					} catch (InterruptedException _) {
						Thread.currentThread().interrupt();
					} catch (ExecutionException e) {
						logger.error(e);
						textPane.setText(e.getMessage());
					}
					buzy.end();
				}
				
				
			};
		
		if(!cards.isEmpty())	
			ThreadManager.getInstance().runInEdt(sw, "asking ia");
	}
	

	@Override
	public String getTitle() {
		return "IA";
	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_IA;
	}

}
