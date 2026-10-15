package org.magic.gui.components.deck;

import java.awt.BorderLayout;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.messages.TalkMessage;
import org.magic.api.interfaces.MTGAssistant;
import org.magic.api.interfaces.MTGIA;
import org.magic.game.model.beans.Player;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.network.JChatPanel;
import org.magic.main.IAVirtualUser;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;

public class DeckIAAssitantPanel extends MTGUIComponent {
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
	private MTGAssistant ia;
	private JTextField txtAsk;
	private JChatPanel chatPanel;
	private MTGDeck deck;
	private AbstractBuzyIndicatorComponent buzy;
	private Player user;
	
	
	
	@Override
	public void onFirstShowing() {
    		initAgent();
	}
	
	public DeckIAAssitantPanel() {
		setLayout(new BorderLayout());
		
		var panelN = new JPanel();
		var panelS = new JPanel();
		chatPanel = new JChatPanel();
		txtAsk = new JTextField(40);	
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		
		add(chatPanel,BorderLayout.CENTER);
		add(panelN, BorderLayout.NORTH);
		add(panelS, BorderLayout.SOUTH);
	
		panelS.add(txtAsk);
		panelN.add(buzy);
	
		
		
		txtAsk.addActionListener(_->{

		    var msg = new TalkMessage(txtAsk.getText());
		    msg.setAuthor(MTGControler.getInstance().getProfilPlayer());
		    
		    txtAsk.setText("");
		    
		    chatPanel.chatAddMessage(msg);
		    
		    var buff = new StringBuffer();
		    deck.getMain().entrySet().forEach(e->{
			    buff.append(e.getValue()).append(" ").append(e.getKey().getName()).append(System.lineSeparator());
		    });

			buzy.start();
			buzy.setText("Thinking");
			var sw = new SwingWorker<String, Void>() {
			    @Override
			    protected String doInBackground() throws Exception {
				return  ia.ask("Considering the following deck: " + buff + "." + System.lineSeparator()+ txtAsk.getText());
			    }

			    @Override
			    protected void done() {
				buzy.end();
				
				try {
				    
				    var msg = new TalkMessage(get());
				    msg.setAuthor(user);
				    
				    chatPanel.chatAddMessage(msg);
				} catch (InterruptedException e) {
				  Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
				   logger.error(e);
				}
				
			    }
			    
			    
			    
			};
			
			ThreadManager.getInstance().runInEdt(sw,"asking assistant");
		
		    
		});
	}
	
	private void initAgent() {
	    var plug = MTG.getEnabledPlugin(MTGIA.class);
	    ia = plug.toAssistant();
	    user = IAVirtualUser.toPlayer(MTG.getEnabledPlugin(MTGIA.class));
	
	}

	public void init(MTGDeck deck)
	{
	this.deck=deck;
	}

	@Override
	public String getTitle() {
	    return "Assistant";
	}
	
	@Override
	public ImageIcon getIcon() {
    		return MTGConstants.ICON_TAB_IA;
	}
	
	

}
