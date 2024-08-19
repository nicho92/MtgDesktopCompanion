package org.magic.gui.components.card;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CancellationException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.interfaces.MTGTokensProvider;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;

public class TokensTablePanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JXTable table;
	private MagicCardTableModel model;
	private MTGEdition currentEdition;
	private AbstractBuzyIndicatorComponent buzy;
	private transient AbstractObservableWorker<List<MTGCard>, MTGCard,MTGTokensProvider> sw;


	public TokensTablePanel() {
		setLayout(new BorderLayout(0, 0));

		var panneauHaut = new JPanel();
		model = new MagicCardTableModel();

		model.setDefaultHiddenComlumns(1,2,5,8,9,11,12,13,14,15);


		table = UITools.createNewTable(model,true);
		buzy=AbstractBuzyIndicatorComponent.createProgressComponent();

		table.setColumnControlVisible(true);

		for(int i : model.defaultHiddenColumns())
			table.getColumnExt(model.getColumnName(i)).setVisible(false);


		panneauHaut.add(buzy);
		add(new JScrollPane(table), BorderLayout.CENTER);
		add(panneauHaut,BorderLayout.NORTH);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent componentEvent) {
				init(currentEdition);
			}

		});
	}

	@Override
	public void onVisible() {
		refresh();
	}

	public MTGCard getSelectedCard()
	{
		if(table.getSelectedRow()>-1)
		{
			return UITools.getTableSelection(table, 0);
		}

		return null;
	}

	public List<MTGCard> getSelectedCards()
	{
		return UITools.getTableSelections(table,0);
	}


	public JXTable getTable() {
		return table;
	}

	public void init(MTGEdition ed)
	{
		this.currentEdition=ed;
		if(isVisible())
			refresh();
	}

	private void refresh()
	{
		if(currentEdition==null)
			return;

		if(sw!=null && !sw.isDone())
		{
			sw.cancel(true);
		}

		sw = new AbstractObservableWorker<>(buzy,getEnabledPlugin(MTGTokensProvider.class),10) {
			@Override
			protected List<MTGCard> doInBackground() throws IOException {
				return plug.listTokensFor(currentEdition);
			}

			@Override
			protected void process(List<MTGCard> chunks) {
				super.process(chunks);
				model.addItems(chunks);
			}

			@Override
			protected void error(Exception e) {
				//do nothing
			}

			@Override
			protected void done() {
				try {
					super.done();
					model.init(get());
				}
				catch(InterruptedException|CancellationException ex)
				{
					Thread.currentThread().interrupt();
				}
				catch (Exception e) {
					logger.error(e);
				}
			}



		};

		ThreadManager.getInstance().runInEdt(sw, "loading edition "+currentEdition);
	}

	@Override
	public String getTitle() {
		return "Tokens";
	}



}
