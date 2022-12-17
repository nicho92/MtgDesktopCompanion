package org.magic.gui.components;

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
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGTokensProvider;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.gui.renderer.MagicEditionsJLabelRenderer;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;

public class TokensTablePanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JXTable table;
	private MagicCardTableModel model;
	private MagicEdition currentEdition;
	private AbstractBuzyIndicatorComponent buzy;
	private transient AbstractObservableWorker<List<MagicCard>, MagicCard,MTGTokensProvider> sw;


	public TokensTablePanel() {
		setLayout(new BorderLayout(0, 0));

		var panneauHaut = new JPanel();
		model = new MagicCardTableModel();

		model.setDefaultHiddenComlumns(1,2,5,8,9,11,12,13,14,15);


		table = UITools.createNewTable(model);
		buzy=AbstractBuzyIndicatorComponent.createProgressComponent();

		table.getColumnModel().getColumn(6).setCellRenderer(new MagicEditionsJLabelRenderer());
		table.setColumnControlVisible(true);

		for(int i : model.defaultHiddenColumns())
			table.getColumnExt(model.getColumnName(i)).setVisible(false);

		UITools.initTableFilter(table);

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

	public MagicCard getSelectedCard()
	{
		if(table.getSelectedRow()>-1)
		{
			return UITools.getTableSelection(table, 0);
		}

		return null;
	}

	public List<MagicCard> getSelectedCards()
	{
		return UITools.getTableSelections(table,0);
	}


	public JXTable getTable() {
		return table;
	}

	public void init(MagicEdition ed)
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
			protected List<MagicCard> doInBackground() throws IOException {
				return plug.listTokensFor(currentEdition);
			}

			@Override
			protected void process(List<MagicCard> chunks) {
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
