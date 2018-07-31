package org.magic.game.gui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import org.jdesktop.swingx.JXPanel;
import org.magic.game.gui.components.renderer.StackItemRenderer;
import org.magic.game.model.AbstractSpell;
import org.magic.game.model.GameManager;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public class StackPanel extends JXPanel implements Observer {
	
	private JList<AbstractSpell> listStack;
	private DefaultListModel<AbstractSpell> model;
	private JLabel lblCounter;
	private Timer timer;
	private static final int SECONDE=10;
	private long startTime=SECONDE;
	
	public StackPanel() {
		model = new DefaultListModel<>();
		
		timer = new Timer(1000,new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startTime=startTime-1;
				lblCounter.setText(String.valueOf(startTime));
				
				if(startTime==0)
				{
					timer.stop();
					if(model.size()>0) {
						lblCounter.setText("RESOLVING " + model.size() + " spell(s)");
						GameManager.getInstance().getStack().resolve();
						timer.start();
					}
					else
					{
						startTime=SECONDE;
						timer.restart();
					}
				}
			}
		});

		lblCounter = new JLabel(String.valueOf(startTime));
		JPanel panel = new JPanel();
		listStack = new JList<>(model);
		
		setLayout(new BorderLayout(0, 0));
		listStack.setCellRenderer(new StackItemRenderer());
		
		add(new JScrollPane(listStack ), BorderLayout.CENTER);
		add(panel, BorderLayout.NORTH);
		panel.add(lblCounter);
		
		timer.start();
		
	}
	
	public void updateStack()
	{
		model.clear();
		startTime=SECONDE;
		for(AbstractSpell spell : GameManager.getInstance().getStack().toList())
		{
			model.addElement(spell);
		}
	}

	@Override
	public void update(Observable o, Object ob) {
		updateStack();
	}
}




