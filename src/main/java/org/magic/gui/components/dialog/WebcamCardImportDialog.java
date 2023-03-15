package org.magic.gui.components.dialog;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CancellationException;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListCellRenderer;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardRecognition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.AbstractDelegatedImporterDialog;
import org.magic.gui.abstracts.AbstractRecognitionArea;
import org.magic.gui.components.webcam.WebcamCanvas;
import org.magic.gui.components.widgets.JLangLabel;
import org.magic.gui.decorators.JListFilterDecorator;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.gui.renderer.MagicEditionsJLabelRenderer;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.providers.IconSetProvider;
import org.magic.services.recognition.MatchResult;
import org.magic.services.recognition.area.AutoDetectAreaStrat;
import org.magic.services.recognition.area.ManualAreaStrat;
import org.magic.services.recognition.area.RadiusAreaStrat;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;
import org.utils.webcam.WebcamUtils;

import com.github.sarxos.webcam.Webcam;

public class WebcamCardImportDialog extends AbstractDelegatedImporterDialog {


	private transient BufferedImage snapshotImage;

	private static final long serialVersionUID = 1L;
	private transient MTGCardRecognition strat;
	private WebcamCanvas webcamCanvas;
	private transient SwingWorker<Void, MatchResult> swWebcamReader;
	private boolean running=false;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private DefaultListModel<MagicEdition> listModel;
	private MagicCard currentCard;
	private AbstractBuzyIndicatorComponent buzy;
	private MagicCardTableModel modelCards;
	private JXTable tableResults;
	private boolean pause=false;


	@Override
	public String getTitle() {
		return "Card Detector";
	}

	@Override
	public void dispose() {
		webcamCanvas.close();
		running=false;
		logger.debug("Closing cam done");

		if(swWebcamReader!=null)
			swWebcamReader.cancel(true);

		super.dispose();
	}

	public WebcamCardImportDialog() {

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});


		setLayout(new BorderLayout(0, 0));

		var gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 59, 0, 0,0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0,0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0,Double.MIN_VALUE};


		buzy = AbstractBuzyIndicatorComponent.createProgressComponent();
		var panelControl = new JPanel();
		var cboWebcams = UITools.createCombobox(WebcamUtils.inst().listWebcam(),MTGConstants.ICON_WEBCAM);
		var cboAreaDetector = UITools.createCombobox(new AbstractRecognitionArea[] { new AutoDetectAreaStrat(),new ManualAreaStrat(), new RadiusAreaStrat()});
		var chkpause = new JCheckBox("Pause");
		var sldThreshold = new JSlider(0,100,40);
		var lblThreshHoldValue = new JLabel(String.valueOf(sldThreshold.getValue()));
		var thrsh = new JPanel();
		var controlWebcamPanel = new JPanel();
		var btnStarting = new JButton("Start",MTGConstants.PLAY_ICON);
		var panneauBas = new JPanel();
		var panneauBasButtons = new JPanel();
		var btnRemove = new JButton(MTGConstants.ICON_DELETE);
		var btnClose = new JButton(MTGConstants.ICON_IMPORT);
		var btnReloadCams = new JButton(MTGConstants.ICON_REFRESH);
		var btnAddCam = new JButton(MTGConstants.ICON_NEW);

		modelCards = new MagicCardTableModel();
		listModel = new DefaultListModel<>();
		strat = getEnabledPlugin(MTGCardRecognition.class);

		try {
			getEnabledPlugin(MTGCardsProvider.class).listEditions().stream().sorted().forEach(listModel::addElement);
		} catch (IOException e1) {
			logger.error(e1);
		}
		tableResults = UITools.createNewTable(modelCards);
		var listEds = new JList<>(listModel);
		var deco = JListFilterDecorator.decorate(listEds,(MagicEdition t, String u)->t.getSet().toLowerCase().contains(u.toLowerCase()));


		webcamCanvas = new WebcamCanvas((Webcam)cboWebcams.getSelectedItem(),(AbstractRecognitionArea)cboAreaDetector.getSelectedItem());



		panelControl.setLayout(gridBagLayout);
		panneauBasButtons.setLayout(new BoxLayout(panneauBasButtons, BoxLayout.Y_AXIS));
		panneauBas.setLayout(new BorderLayout());



		controlWebcamPanel.add(cboWebcams);
		controlWebcamPanel.add(btnReloadCams);
		controlWebcamPanel.add(btnAddCam);

		panelControl.add(controlWebcamPanel, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 0));

		panelControl.add(new JLangLabel("LOADING_EDITIONS",true), UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 1));
		panelControl.add(new JScrollPane(deco.getContentPanel()), UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 0, 2));
		panelControl.add(buzy, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 3));
		panelControl.add(cboAreaDetector, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 4));
		panelControl.add(thrsh, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 5));
		panelControl.add(btnStarting, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 6));



		thrsh.add(sldThreshold);
		thrsh.add(lblThreshHoldValue);
		thrsh.add(chkpause);
		panneauBasButtons.add(btnRemove);
		panneauBasButtons.add(btnClose);

		panneauBas.add(panneauBasButtons,BorderLayout.EAST);

		var scrollPane = new JScrollPane(tableResults);
		scrollPane.setPreferredSize(new Dimension(2, 150));
		panneauBas.add(scrollPane,BorderLayout.CENTER);

		getContentPane().add(panelControl, BorderLayout.EAST);
		getContentPane().add(panneauBas,BorderLayout.SOUTH);
		getContentPane().add(webcamCanvas, BorderLayout.CENTER);


		tableResults.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
		tableResults.getColumnModel().getColumn(6).setCellRenderer(new MagicEditionsJLabelRenderer());

		listEds.setCellRenderer(new ListCellRenderer<>() {
			JLabel l = new JLabel();
			@Override
			public Component getListCellRendererComponent(JList<? extends MagicEdition> list,MagicEdition value, int index, boolean isSelected, boolean cellHasFocus) {
				if (value != null)
				{
					ImageIcon ic= IconSetProvider.getInstance().get16(value.getId());

					l.setText(value.getSet());
					l.setIcon(ic);

					l.setOpaque(true);
					if (isSelected) {
						l.setBackground(list.getSelectionBackground());
						l.setForeground(list.getSelectionForeground());
					} else {
						l.setBackground(list.getBackground());
						l.setForeground(list.getForeground());
					}

					if(MTG.getEnabledPlugin(MTGCardRecognition.class).isSetCached(value))
						l.setBackground(Color.CYAN);

					if(MTG.getEnabledPlugin(MTGCardRecognition.class).isSetLoaded(value))
						l.setBackground(Color.GREEN);



				}

				return l;

			}
		});





		sldThreshold.addChangeListener(cl->lblThreshHoldValue.setText(String.valueOf(sldThreshold.getValue())));
		cboAreaDetector.addActionListener(il->webcamCanvas.setAreaStrat((AbstractRecognitionArea)cboAreaDetector.getSelectedItem()));
		btnClose.addActionListener(il->dispose());
		btnReloadCams.addActionListener(al->{
			((DefaultComboBoxModel<Webcam>)cboWebcams.getModel()).removeAllElements();
			((DefaultComboBoxModel<Webcam>)cboWebcams.getModel()).addAll(WebcamUtils.inst().listWebcam());
			cboWebcams.setSelectedItem(webcamCanvas.getWebcam());
		});

		btnAddCam.addActionListener(al->{
			var diag = new IPCamAddDialog();
						   diag.setModal(true);
						   diag.setVisible(true);
			if(diag.isHasNew())
				btnReloadCams.doClick();
		});




		chkpause.addChangeListener(l->pause=chkpause.isSelected());

		btnRemove.addActionListener(l->{
			List<MagicCard> cards = UITools.getTableSelections(tableResults,0);

			if(!cards.isEmpty())
				modelCards.removeItem(cards);

		});

		listEds.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if(me.getClickCount()==2)
				{
						MagicEdition ed = listEds.getSelectedValue();
						buzy.start(ed.getCardCount());
						pause=true;
						AbstractObservableWorker<Void, Void, MTGCardRecognition> work = new AbstractObservableWorker<>(buzy,strat,ed.getCardCount()) {
							@Override
							protected Void doInBackground() throws Exception {

								if(!plug.isSetLoaded(ed))
									plug.loadDatasForSet(ed);
								else
									plug.clear(ed);

								return null;
							}

							@Override
							protected void done() {
								super.done();
								plug.finalizeLoad();
								pause=false;
								listEds.updateUI();
							}
						};

						ThreadManager.getInstance().runInEdt(work, "building " + ed + " recognition");
				}

			}
		});


		btnStarting.addActionListener(al->{
			webcamCanvas.setWebcam((Webcam)cboWebcams.getSelectedItem());
			webcamCanvas.revalidate();
			ThreadManager.getInstance().runInEdt(swWebcamReader, "Webcam");
		});


		setModal(true);
		pack();

		swWebcamReader = new SwingWorker<>()
		{

			@Override
			protected Void doInBackground()
			{
				running=true;
				try {
					logger.info("start {} : {}" ,webcamCanvas.getWebcam() ,running);
					while(running)
					{
						webcamCanvas.draw();
						var img = webcamCanvas.lastDrawn();


						if(strat!=null && img!=null && !pause && !isCancelled())
						{
								var matches = webcamCanvas.getAreaRecognitionStrategy().recognize(img, strat,sldThreshold.getValue());
								MatchResult res = !matches.isEmpty() ? matches.get(0):null;
								if(res!=null)
								{
									publish(res);
								}
						}
					}
				}
				catch(Exception e)
				{
					logger.error("Error in webcam" ,e);
				}

				return null;
			}




			@Override
			protected void process(List<MatchResult> chunks) {
				addResult( chunks.get(0));
			}

			@Override
			protected void done() {

				try {
					logger.info("Stopping webcam {}",webcamCanvas.getWebcam());
					running=false;
					get();
				}
				catch(InterruptedException ex)
				{
					Thread.currentThread().interrupt();
				}
				catch(CancellationException ex)
				{
					logger.error("Cancelling");
				}
				catch (Exception e) {
					logger.error("Error Stopping webcam {}",webcamCanvas.getWebcam(),e);
				}

			}
		};



	}


	protected void addResult(MatchResult r) {
		try {

			if(currentCard==null || !currentCard.getName().equalsIgnoreCase(r.getName()))
			{
				logger.info("Looking for {} {}",r.getName(),r.getSetCode());
				currentCard = getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(r.getNumber(), new MagicEdition(r.getSetCode()));
				modelCards.addItem(currentCard);
				tableResults.scrollRectToVisible(tableResults.getCellRect(tableResults.getRowCount()-1, 0, true));
			}

		} catch (IOException e) {
			logger.error("Error loading card for result {}", r);
		}

	}

	public BufferedImage getSnappedImages()
	{

		return snapshotImage;
	}

	public List<MagicCard> getFindedCards()
	{
		return modelCards.getItems();
	}

	@Override
	public MagicDeck getSelectedDeck() {
		var d = new MagicDeck();
		d.setDescription("Imported from " + getTitle());
		d.setName(getName());
		getFindedCards().forEach(d::add);
		return d;
	}





}
