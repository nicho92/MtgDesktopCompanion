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
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.layer.BorderLayer;
import org.magic.api.beans.layer.FrameLayer;
import org.magic.api.beans.layer.IllustrationLayer;
import org.magic.api.beans.layer.ManaLayer;
import org.magic.api.beans.layer.ShapeLayer;
import org.magic.api.beans.layer.TextLayer;
import org.magic.api.beans.layer.enums.BorderColor;
import org.magic.api.beans.layer.enums.ShapeType;
import org.magic.api.beans.layer.enums.TextRole;
import org.magic.api.exports.impl.JsonExport;
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
    private AbstractBuzyIndicatorComponent buzy;
    private static File currentFile;
    ImagePanel2 paneRendering ;

    public MtgCardComposer() {
	setPreferredSize(new Dimension(1200, 900));
	UITools.loadFonts();
	MTGControler.getInstance().downloadComposerDatas();
	setLayout(new BorderLayout());
	
	
	paneRendering = new ImagePanel2(false, false, true, false);
	paneRendering.setPreferredSize(new Dimension(400,900));
	    
	
	paneRendering.setVisible(false);
	
	
	
	add(paneRendering,BorderLayout.EAST);
	
	var rootNode = initTree(MTGConstants.MTG_COMPOSER_DIR);

	var rootColors = new DefaultMutableTreeNode("Borders");
	for(var bc : BorderColor.values())
	    rootColors.add(new DefaultMutableTreeNode(bc));

	rootNode.add(rootColors);

	var textNodes = new DefaultMutableTreeNode("Text");
	for(var bc : TextRole.values())
	    textNodes.add(new DefaultMutableTreeNode(bc));

	rootNode.add(textNodes);


	var rootImage = new DefaultMutableTreeNode("Illustration");
	rootImage.add(new DefaultMutableTreeNode("URL"));
	rootImage.add(new DefaultMutableTreeNode("File"));

	rootNode.add(rootImage);

	var rootCost = new DefaultMutableTreeNode("Cost");
	rootCost.add(new DefaultMutableTreeNode("Cost"));

	rootNode.add(rootCost);	
	
	
	var rootShape = new DefaultMutableTreeNode("Shapes");
		for(var s : ShapeType.values())
		    rootShape.add(new DefaultMutableTreeNode(s));

	rootNode.add(rootShape);	
	

	tree = new JTree(new DefaultTreeModel(rootNode));

	tree.setRootVisible(true);
	tree.setShowsRootHandles(true);
	tree.setCellRenderer((JTree t, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)->{

	    var node = (DefaultMutableTreeNode) value;
	    String text;
	    if (node.getUserObject() instanceof File file) {
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


	var leftPanel = new LeftPanel(tree, layerList,inspector);

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


	var btnRender = new JButton(MTGConstants.ICON_TAB_ADMIN);
	var saveButton = new JButton(MTGConstants.ICON_SMALL_SAVE);
	var newButton = new JButton(MTGConstants.ICON_SMALL_NEW);
	var openButton = new JButton(MTGConstants.ICON_SMALL_OPEN);
	var exportButton = new JButton(MTGConstants.ICON_SMALL_EXPORT);
	var renderCardButton = new JButton(MTGConstants.ICON_TAB_CARD);
	
	
	buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
	
	add(UITools.createFlowPanel(newButton,openButton,saveButton,exportButton,btnRender,renderCardButton,buzy),BorderLayout.NORTH);

	
	
	var paneSouth = new JPanel();
	paneSouth.setLayout(new BorderLayout());
	paneSouth.add(sldZoom,BorderLayout.CENTER);

	add(paneSouth, BorderLayout.SOUTH);
	
	btnRender.addActionListener(_->{
	    var diag = new RenderingHintsDialog(null,canvas.getHints());
	    diag.setVisible(true);
	    var res = diag.getRenderingHints();
	    if(res!=null)
		canvas.setHints(res);
	});
	
	
	renderCardButton.addActionListener(_->{
	   
	   paneRendering.setVisible(true);
	    
	   var diag = new CardImporterDialog();
	   diag.setLocationRelativeTo(this);
	   diag.setVisible(true);
	   
		if (diag.hasSelected()) {
			var mc = diag.getSelectedItem();
			buzy.start();
			buzy.setText("Rendering " + mc.getName() + " with " + currentFile.getName());
			var sw = new SwingWorker<BufferedImage, Void>(){

			    @Override
			    protected BufferedImage doInBackground() throws Exception {
				mc.setUrl(new ScryFallPicturesProvider().generateUrl(mc, true));
				var renderer = new MTGCompanionComposerProvider();
				renderer.setTemplate(currentFile);
				return renderer.getPicture(mc, mc.getEdition());
			    }

			    @Override
			    protected void done() {
				    diag.dispose();
				    buzy.end();
				    
				    try {
					paneRendering.setImg(get());
				    } catch (InterruptedException _) {
					Thread.currentThread().interrupt();
				    }
				    catch(ExecutionException ex)
				    {
					logger.error(ex);
				    }
			    }
			};
			ThreadManager.getInstance().runInEdt(sw, "rendering results");
		}
	    
	});
	
	
	saveButton.addActionListener(_ -> {

	    var chose = new JFileChooser(currentFile);
	    chose.showSaveDialog(this);
	    
	    if(chose.getSelectedFile()==null)
		return;
	    
	    
	    try {
		var s = new JsonExport().toJson(canvas.getLayers());
		FileTools.saveFile(chose.getSelectedFile(), s);
		currentFile = chose.getSelectedFile();
		logger.info("{} saved",currentFile);
	    } catch (IOException e) {
		MTGControler.getInstance().notify(e);
	    }
	});

	exportButton.addActionListener(_ -> {

	    var chose = new JFileChooser();
	    chose.showSaveDialog(this);

	    try {
		ImageTools.saveImageInPng(canvas.getCardImage(), chose.getSelectedFile());
	    } catch (IOException e) {
		MTGControler.getInstance().notify(e);
	    }
	});
	newButton.addActionListener(_->{
	    
	    layerList.setLayers(null);
	    	canvas.clear();
		
	    
	});
	
	
	openButton.addActionListener(_ -> {
	    var chose = new JFileChooser(currentFile);
	    chose.showOpenDialog(this);
	    
	    
	    if(chose.getSelectedFile()==null)
		return;
	    
	    buzy.start();
	    
	    try {
		currentFile = chose.getSelectedFile();
		var s = FileTools.readFile(chose.getSelectedFile());
		var layers = new JsonExport().fromJsonList(s, Layer.class);
		
		
		var sw = new SwingWorker<Void, Void>() {

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
	    } catch (IOException e) {
		logger.error(e);
	    }
	});

	

	layerList.getList().addListSelectionListener(e -> {

	    if (e.getValueIsAdjusting()) {
		return;
	    }
	    var layer = layerList.getSelectedLayer();
	    canvas.selectLayer(layer);
	});

	tree.addMouseListener(new MouseAdapter() {

	    @Override
	    public void mouseClicked(MouseEvent e) {

		if (e.getClickCount() != 1) {
		    return;
		}

		var path = tree.getPathForLocation(e.getX(), e.getY());

		if (path == null) {
		    return;
		}

		var node = (DefaultMutableTreeNode) path.getLastPathComponent();
		var object = node.getUserObject();

		if (object instanceof File file && isImageFile(file))
		{
		    canvas.addLayer(new FrameLayer(file));
		}
		else if (object instanceof ShapeType shape) {
		    
		    var flayer = new ShapeLayer();
		    flayer.setShape(shape);
		    flayer.setName(shape.name());
		    canvas.addLayer(flayer);
		    
		    
		}
		else if (object instanceof TextRole role) {
		    var flayer = new TextLayer(role.name().toLowerCase(),role);
		    flayer.setColor(role.getColor());

		    var dialog = new TextLayerDialog(null, flayer);
		    dialog.setVisible(true);

		    if(dialog.isConfirmed())
			canvas.addLayer(flayer);
		} 
		else if (object instanceof BorderColor c) {

		    var border = new BorderLayer(c);

		    if(c==BorderColor.BORDERLESS)
		    {
			var url = JOptionPane.showInputDialog("URL");
			
			if(StringUtils.isEmpty(url))
			    return;
			
			border.setUri(URI.create(url));
			border.reload();
			border.fitImage();
		    }
		    canvas.addLayer(border);
		}
		else if (object.toString().equals("URL"))
		{

		    var url = JOptionPane.showInputDialog("URL ?");
		    try {
			canvas.addLayer(new IllustrationLayer(URI.create(url).toURL()));
		    } catch (MalformedURLException e1) {
			logger.error(e1);
		    }

		}
		else if (object.toString().equals("File"))
		{

		   var f = new JFileChooser(currentFile);
		   f.showOpenDialog(MtgCardComposer.this);
		   var file = f.getSelectedFile();
		   
		   if(file==null)
		       return;
		   
		    try {
			canvas.addLayer(new IllustrationLayer(file.toURI().toURL()));
		    } catch (MalformedURLException e1) {
			logger.error(e1);
		    }

		}
		else if (object.toString().equals("Cost"))
		{

		    ThreadManager.getInstance().invokeLater(new MTGRunnable() {

			@Override
			protected void auditedRun() {
			    var diag = new ManaCostDialog();
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

	if(!file.exists())
	    return null;


	var node = new DefaultMutableTreeNode(file);

	if (!file.isDirectory()) {
	    return node;
	}

	var children = file.listFiles();

	if (children == null) {
	    return node;
	}

	Arrays.sort(children, (a, b) -> {

	    if (a.isDirectory() && !b.isDirectory()) 
		return -1;

	    if (!a.isDirectory() && b.isDirectory()) 
		return 1;

	    return a.getName().compareToIgnoreCase(b.getName());
	});

	for (var child : children) {
	    node.add(initTree(child));
	}

	return node;
    }

    // =====================================================================
    // Type detection
    // =====================================================================

    private boolean isImageFile(File file) {
	var name = file.getName().toLowerCase();
	return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif") || name.endsWith(".bmp") || name.endsWith(".webp");
    }

    @Override
    public String getTitle() {
	return "Composer";
    }


}
