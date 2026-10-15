package org.magic.gui.components.deck;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
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
import org.magic.services.IAPrompts;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

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
	
	
	private JButton createActionButton(
	        String label,
	        int key,
	        String action,
	        Runnable callback) {

	    var button = UITools.createBindableJButton(label, null, key, action);

	    button.addActionListener(_ -> callback.run());
	    return button;
	}
	
	public DeckIAAssitantPanel() {
		setLayout(new BorderLayout());
		var panelS = new JPanel();
		chatPanel = new JChatPanel();
		txtAsk = new JTextField(40);	
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
	
		add(chatPanel,BorderLayout.CENTER);
		add(panelS, BorderLayout.SOUTH);
		
		panelS.add(txtAsk);
		panelS.add(buzy);
		
		
		panelS.add(createActionButton("Analyze", KeyEvent.VK_A,"analyze",() -> askAssistant(IAPrompts.evaluation(deck))));
		panelS.add(createActionButton("Replace", KeyEvent.VK_R,"replace",() -> askAssistant(IAPrompts.replacements(deck))));
		panelS.add(createActionButton("Manabase", KeyEvent.VK_M,"manabase",() -> askAssistant(IAPrompts.manabase(deck))));
		panelS.add(createActionButton("Synergy", KeyEvent.VK_S,"synergy",() -> askAssistant(IAPrompts.synergy(deck))));
		
		panelS.add(createActionButton("Combos", KeyEvent.VK_C,"combos",() -> askAssistant(IAPrompts.combos(deck))));
		panelS.add(createActionButton("Competitivity", KeyEvent.VK_V,"competitive",() -> askAssistant(IAPrompts.competitive(deck))));
		
		txtAsk.addActionListener(_->{
		    askAssistant(txtAsk.getText());
		   	
		});
	}
	
	private void askAssistant(String text) {
	    	
//	    	var msg = new TalkMessage(text);
//	    	msg.setAuthor(MTGControler.getInstance().getProfilPlayer());
//	    	chatPanel.chatAddMessage(msg);
	    	
	    	buzy.start();
		buzy.setText("Thinking");
		var sw = new SwingWorker<String, Void>() {
		    @Override
		    protected String doInBackground() throws Exception {
			return  ia.ask("Considering the following deck: " + deck.toStringList() + "." + System.lineSeparator()+ txtAsk.getText());
		    }

		    @Override
		    protected void done() {
			buzy.end();
			
			try {
			    
			    var msg = new TalkMessage(get());
			    msg.setAuthor(user);
			    chatPanel.chatAddMessage(msg);
			    txtAsk.setText("");
			} catch (InterruptedException e) {
			  Thread.currentThread().interrupt();
			} catch (ExecutionException e) {
			   logger.error(e);
			}
			
		    }
		    
		    
		    
		};
		
		ThreadManager.getInstance().runInEdt(sw,"asking assistant");
	    
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
