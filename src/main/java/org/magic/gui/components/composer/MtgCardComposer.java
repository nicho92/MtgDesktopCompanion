package org.magic.gui.components.composer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.layer.BorderLayer;
import org.magic.api.beans.layer.FrameLayer;
import org.magic.api.beans.layer.IllustrationLayer;
import org.magic.api.beans.layer.ManaLayer;
import org.magic.api.beans.layer.SetIconLayer;
import org.magic.api.beans.layer.ShapeLayer;
import org.magic.api.beans.layer.TextLayer;
import org.magic.api.beans.layer.enums.BorderColor;
import org.magic.api.beans.layer.enums.ShapeType;
import org.magic.api.beans.layer.enums.TextRole;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.extra.Layer;
import org.magic.api.pictures.impl.ScryFallPicturesProvider;
import org.magic.api.pictureseditor.impl.MTGCompanionComposerProvider;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.ImagePanel2;
import org.magic.gui.components.dialog.RenderingHintsDialog;
import org.magic.gui.components.dialog.importer.CardImporterDialog;
import org.magic.gui.components.dialog.importer.ManaCostDialog;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class MtgCardComposer extends MTGUIComponent {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final JTree tree;
    private final CardCanvas canvas;
    private final LayersListPanel layerList;
    private final LayerDetailPanel inspector;
    private JSlider sldZoom;
    private final AbstractBuzyIndicatorComponent buzy;
    private static File currentFile;
    private final ImagePanel2 paneRendering ;

    public MtgCardComposer() {
	setPreferredSize(new Dimension(1200, 900));
	UITools.loadFonts();
	MTGControler.getInstance().downloadComposerDatas();
	setLayout(new BorderLayout());


	paneRendering = new ImagePanel2(false, false, true, false);
	paneRendering.setPreferredSize(new Dimension(400,900));


	paneRendering.setVisible(false);



	add(paneRendering,BorderLayout.EAST);

	final var rootNode = initTree(MTGConstants.MTG_COMPOSER_DIR);

	final var rootColors = new DefaultMutableTreeNode("Borders");
	for(final var bc : BorderColor.values()) {
	    rootColors.add(new DefaultMutableTreeNode(bc));
	}

	rootNode.add(rootColors);

	final var textNodes = new DefaultMutableTreeNode("Text");
	for(final var bc : TextRole.values()) {
	    textNodes.add(new DefaultMutableTreeNode(bc));
	}

	rootNode.add(textNodes);


	final var rootImage = new DefaultMutableTreeNode("Illustration");
	rootImage.add(new DefaultMutableTreeNode("URL"));
	rootImage.add(new DefaultMutableTreeNode("File"));

	rootNode.add(rootImage);

	final var rootCost = new DefaultMutableTreeNode("Cost");
	rootCost.add(new DefaultMutableTreeNode("Cost"));

	rootNode.add(rootCost);


	final var rootShape = new DefaultMutableTreeNode("Shapes");
	for(final var s : ShapeType.values()) {
	    rootShape.add(new DefaultMutableTreeNode(s));
	}

	rootNode.add(rootShape);


	final var rootEditions = new DefaultMutableTreeNode("Sets");
	try {
	    for(final var ed : MTG.getEnabledPlugin(MTGCardsProvider.class).listEditions().stream().sorted(Comparator.comparing(MTGEdition::getSet)).toList()) {
		rootEditions.add(new DefaultMutableTreeNode(ed));
	    }
	} catch (final IOException e) {
	    logger.error(e);
	}

	rootNode.add(rootEditions);


	tree = new JTree(new DefaultTreeModel(rootNode));
	tree.setRootVisible(true);
	tree.setShowsRootHandles(true);
	tree.setCellRenderer((JTree t, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)->{

	    final var node = (DefaultMutableTreeNode) value;
	    String text;
	    if (node.getUserObject() instanceof final File file) {
		text = file.getName();
	    } else {
		text = node.getUserObject().toString();
	    }
	    return new DefaultTreeCellRenderer().getTreeCellRendererComponent(t, text, selected, expanded, leaf, row, hasFocus);
	});

	canvas = new CardCanvas();

	layerList = new LayersListPanel();
	layerList.setCanvas(canvas);

	inspector = new LayerDetailPanel();
	inspector.setLayerChangeListener(_ -> canvas.repaint());


	canvas.setLayerChangeListener(layer-> inspector.refresh(layer));


	final var leftPanel = new LeftPanel(tree, layerList,inspector);

	add(leftPanel, BorderLayout.WEST);
	add(new JScrollPane(canvas), BorderLayout.CENTER);

	canvas.setLayerSelectionListener(layer -> {
	    layerList.selectLayer(layer);
	});

	sldZoom = new JSlider(0,100,100);
	sldZoom.setMajorTickSpacing(10);
	sldZoom.setMinorTickSpacing(1);
	sldZoom.setPaintTicks(true);
	sldZoom.setPaintLabels(true);
	sldZoom.addChangeListener(_->canvas.setZoom((double)sldZoom.getValue()/100));
	sldZoom.setValue(20);


	final var btnRender = new JButton(MTGConstants.ICON_TAB_ADMIN);
	final var saveButton = new JButton(MTGConstants.ICON_SMALL_SAVE);
	final var newButton = new JButton(MTGConstants.ICON_SMALL_NEW);
	final var openButton = new JButton(MTGConstants.ICON_SMALL_OPEN);
	final var exportButton = new JButton(MTGConstants.ICON_SMALL_EXPORT);
	final var renderCardButton = new JButton(MTGConstants.ICON_TAB_CARD);
	final var exportImageButton = new JButton(MTGConstants.ICON_TAB_PICTURE);

	buzy = AbstractBuzyIndicatorComponent.createLabelComponent();

	add(UITools.createFlowPanel(newButton,openButton,saveButton,exportButton,btnRender,renderCardButton,exportImageButton,buzy),BorderLayout.NORTH);


	final var paneSouth = new JPanel();
	paneSouth.setLayout(new BorderLayout());
	paneSouth.add(sldZoom,BorderLayout.CENTER);

	add(paneSouth, BorderLayout.SOUTH);

	btnRender.addActionListener(_->{
	    final var diag = new RenderingHintsDialog(null,canvas.getHints());
	    diag.setVisible(true);
	    final var res = diag.getRenderingHints();
	    if(res!=null) {
		canvas.setHints(res);
	    }
	});


	renderCardButton.addActionListener(_->{

	    paneRendering.setVisible(true);

	    final var diag = new CardImporterDialog();
	    diag.setLocationRelativeTo(this);
	    diag.setVisible(true);

	    if (diag.hasSelected()) {
		final var mc = diag.getSelectedItem();
		buzy.start();
		buzy.setText("Rendering " + mc.getName() + " with " + currentFile.getName());
		final var sw = new SwingWorker<BufferedImage, Void>(){

		    @Override
		    protected BufferedImage doInBackground() throws Exception {
			mc.setUrl(new ScryFallPicturesProvider().generateUrl(mc, true));
			final var renderer = new MTGCompanionComposerProvider();
			renderer.setTemplate(currentFile);
			return renderer.getPicture(mc, mc.getEdition());
		    }

		    @Override
		    protected void done() {
			diag.dispose();
			buzy.end();

			try {
			    paneRendering.setImg(get());
			} catch (final InterruptedException _) {
			    Thread.currentThread().interrupt();
			}
			catch(final ExecutionException ex)
			{
			    logger.error(ex);
			}
		    }
		};
		ThreadManager.getInstance().runInEdt(sw, "rendering results");
	    }

	});


	exportImageButton.addActionListener(_->{

	    final var chose = new JFileChooser(currentFile);
	    chose.showSaveDialog(this);

	    if(chose.getSelectedFile()==null) {
		return;
	    }

	    try {
		ImageTools.saveImageInPng(canvas.getCardImage(), chose.getSelectedFile());
	    } catch (final IOException e) {
		MTGControler.getInstance().notify(e);
	    }

	});


	saveButton.addActionListener(_ -> {

	    final var chose = new JFileChooser(currentFile);
	    chose.showSaveDialog(this);

	    if(chose.getSelectedFile()==null) {
		return;
	    }


	    try {
		final var s = new JsonExport().toJson(canvas.getLayers());
		FileTools.saveFile(chose.getSelectedFile(), s);
		currentFile = chose.getSelectedFile();
		logger.info("{} saved",currentFile);
	    } catch (final IOException e) {
		MTGControler.getInstance().notify(e);
	    }
	});

	exportButton.addActionListener(_ -> {

	    final var chose = new JFileChooser();
	    chose.showSaveDialog(this);

	    try {
		ImageTools.saveImageInPng(canvas.getCardImage(), chose.getSelectedFile());
	    } catch (final IOException e) {
		MTGControler.getInstance().notify(e);
	    }
	});
	newButton.addActionListener(_->{

	    layerList.setLayers(null);
	    canvas.clear();


	});


	openButton.addActionListener(_ -> {
	    final var chose = new JFileChooser(currentFile);
	    chose.showOpenDialog(this);


	    if(chose.getSelectedFile()==null) {
		return;
	    }

	    buzy.start();

	    try {
		currentFile = chose.getSelectedFile();
		final var s = FileTools.readFile(chose.getSelectedFile());
		final var layers = new JsonExport().fromJsonList(s, Layer.class);


		final var sw = new SwingWorker<Void, Void>() {

		    @Override
		    protected Void doInBackground() throws Exception {
			layers.forEach(Layer::reload);
			return null;
		    }
		    @Override
		    protected void done() {
			canvas.clear();
			layerList.setLayers(layers);
			layers.forEach(canvas::addLayer);
			buzy.end();
		    }
		};

		ThreadManager.getInstance().runInEdt(sw,"loading layers");
	    } catch (final IOException e) {
		logger.error(e);
	    }
	});



	layerList.getList().addListSelectionListener(e -> {

	    if (e.getValueIsAdjusting()) {
		return;
	    }
	    final var layer = layerList.getSelectedLayer();
	    canvas.selectLayer(layer);
	});

	tree.addMouseListener(new MouseAdapter() {

	    @Override
	    public void mouseClicked(MouseEvent e) {

		if (e.getClickCount() != 1) {
		    return;
		}

		final var path = tree.getPathForLocation(e.getX(), e.getY());

		if (path == null) {
		    return;
		}

		final var node = (DefaultMutableTreeNode) path.getLastPathComponent();
		final var object = node.getUserObject();

		if (object instanceof final File file && isImageFile(file))
		{
		    canvas.addLayer(new FrameLayer(file));
		}
		else if (object instanceof final ShapeType shape)
		{

		    final var flayer = new ShapeLayer();
		    flayer.setShape(shape);
		    flayer.setName(shape.name());
		    canvas.addLayer(flayer);


		}
		else if (object instanceof final TextRole role)
		{
		    final var flayer = new TextLayer(role.name().toLowerCase(),role);
		    flayer.setColor(role.getColor());

		    final var dialog = new TextLayerDialog(null, flayer);
		    dialog.setVisible(true);

		    if(dialog.isConfirmed()) {
			canvas.addLayer(flayer);
		    }
		}
		else if (object instanceof final BorderColor c)
		{

		    final var border = new BorderLayer(c);

		    if(c==BorderColor.BORDERLESS)
		    {
			final var url = JOptionPane.showInputDialog("URL");

			if(StringUtils.isEmpty(url)) {
			    return;
			}

			border.setUri(URI.create(url));
			border.reload();
			border.fitImage();
		    }
		    canvas.addLayer(border);
		}
		else if (object instanceof final MTGEdition ed)
		{
		    final var l = new SetIconLayer(ed.getId());
		    l.reload();

		    canvas.addLayer(l);
		}
		else if (object.toString().equals("URL"))
		{

		    final var url = JOptionPane.showInputDialog("URL ?");
		    try {
			canvas.addLayer(new IllustrationLayer(URI.create(url).toURL()));
		    } catch (final MalformedURLException e1) {
			logger.error(e1);
		    }

		}
		else if (object.toString().equals("File"))
		{

		    final var f = new JFileChooser(currentFile);
		    f.showOpenDialog(MtgCardComposer.this);
		    final var file = f.getSelectedFile();

		    if(file==null) {
			return;
		    }

		    try {
			canvas.addLayer(new IllustrationLayer(file.toURI().toURL()));
		    } catch (final MalformedURLException e1) {
			logger.error(e1);
		    }

		}
		else if (object.toString().equals("Cost"))
		{

		    ThreadManager.getInstance().invokeLater(new MTGRunnable() {

			@Override
			protected void auditedRun() {
			    final var diag = new ManaCostDialog();
			    diag.setVisible(true);
			    canvas.addLayer(new ManaLayer( diag.getSelectedItem()));

			}
		    },"run mana dialog");

		}
		layerList.setLayers(canvas.getLayers());
	    }
	});
    }

    // =====================================================================
    // Tree
    // =====================================================================

    private DefaultMutableTreeNode initTree(File file) {

	if(!file.exists()) {
	    return null;
	}


	final var node = new DefaultMutableTreeNode(file);

	if (!file.isDirectory()) {
	    return node;
	}

	final var children = file.listFiles();

	if (children == null) {
	    return node;
	}

	Arrays.sort(children, (a, b) -> {

	    if (a.isDirectory() && !b.isDirectory()) {
		return -1;
	    }

	    if (!a.isDirectory() && b.isDirectory()) {
		return 1;
	    }

	    return a.getName().compareToIgnoreCase(b.getName());
	});

	for (final var child : children) {
	    node.add(initTree(child));
	}

	return node;
    }

    // =====================================================================
    // Type detection
    // =====================================================================

    private boolean isImageFile(File file) {
	final var name = file.getName().toLowerCase();
	return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif") || name.endsWith(".bmp") || name.endsWith(".webp");
    }

    @Override
    public String getTitle() {
	return "Composer";
    }
}

class LeftPanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public LeftPanel(JTree tree, LayersListPanel layerList, LayerDetailPanel inspector) {

	setLayout(new BorderLayout());
	setPreferredSize(new Dimension(350, 0));

	final var treeScrollPane = new JScrollPane(tree);

	treeScrollPane.setBorder(BorderFactory.createTitledBorder("Resources"));

	final var treePanel = new JPanel(new BorderLayout());

	treePanel.add(treeScrollPane, BorderLayout.CENTER);

	final var layersPanel = new JPanel(new BorderLayout());
	layersPanel.setBorder(BorderFactory.createTitledBorder("Composition"));
	layersPanel.add(layerList, BorderLayout.CENTER);


	final var tabPane = new JTabbedPane();
	tabPane.addTab("Layers", layersPanel);
	tabPane.addTab("Inspect", inspector);


	// =================================================================
	// Split
	// =================================================================

	final var splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, treePanel, tabPane);

	splitPane.setResizeWeight(0.65);

	splitPane.setOneTouchExpandable(true);

	add(splitPane, BorderLayout.CENTER);
    }
}

