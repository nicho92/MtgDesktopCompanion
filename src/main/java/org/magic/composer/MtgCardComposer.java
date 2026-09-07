package org.magic.composer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import org.apache.logging.log4j.Logger;
import org.magic.composer.gui.CardCanvas;
import org.magic.composer.gui.LayerDetailPanel;
import org.magic.composer.gui.LayersListPanel;
import org.magic.composer.gui.LeftPanel;
import org.magic.composer.gui.TextLayerDialog;
import org.magic.composer.layer.BorderLayer;
import org.magic.composer.layer.FrameLayer;
import org.magic.composer.layer.IllustrationLayer;
import org.magic.composer.layer.TextLayer;
import org.magic.services.logging.MTGLogger;

public class MtgCardComposer extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final JTree tree;
    private final CardCanvas imageCanvas;
    private final LayersListPanel layerList;
    private final LayerDetailPanel inspector;
    private JSlider sldZoom;
    protected transient Logger logger = MTGLogger.getLogger(this.getClass());
    
    
    public MtgCardComposer(File rootDirectory) {

	if (rootDirectory == null || !rootDirectory.isDirectory()) {
	    throw new IllegalArgumentException("rootDirectory must be a valid directory");
	}

	setLayout(new BorderLayout());

	var rootNode = initTree(rootDirectory);

	
	var rootColors = new DefaultMutableTreeNode("borders");
		rootColors.add(new DefaultMutableTreeNode(Color.BLACK));
		rootColors.add(new DefaultMutableTreeNode(Color.WHITE));
		rootColors.add(new DefaultMutableTreeNode(Color.LIGHT_GRAY));
		rootColors.add(new DefaultMutableTreeNode(new Color(186, 142, 35)));
		
	rootNode.add(rootColors);
	
	
	var rootImage = new DefaultMutableTreeNode("images");
		rootImage.add(new DefaultMutableTreeNode("URL"));
	
		rootNode.add(rootImage);
	
	tree = new JTree(new DefaultTreeModel(rootNode));

	tree.setRootVisible(true);
	tree.setShowsRootHandles(true);
	tree.setCellRenderer((JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)->{

		    var node = (DefaultMutableTreeNode) value;
		    String text;
		    if (node.getUserObject() instanceof File file) {
			text = file.getName();
		    } else {
			text = node.getUserObject().toString();
		    }
		    return new DefaultTreeCellRenderer().getTreeCellRendererComponent(tree, text, selected, expanded, leaf, row, hasFocus);
	});
	
	imageCanvas = new CardCanvas();
	
	layerList = new LayersListPanel();
	layerList.setCanvas(imageCanvas);

	inspector = new LayerDetailPanel();
	
	
	imageCanvas.setLayerChangeListener(layer-> inspector.refresh(layer));
	
	
	var leftPanel = new LeftPanel(tree, layerList,inspector);
	leftPanel.setPreferredSize(new Dimension(280, 0));

	add(leftPanel, BorderLayout.WEST);
	add(new JScrollPane(imageCanvas), BorderLayout.CENTER);

	imageCanvas.setLayerSelectionListener(layer -> {
	    layerList.selectLayer(layer);
	});
	
	sldZoom = new JSlider(0,100,100);
	sldZoom.setMajorTickSpacing(10);
	sldZoom.setMinorTickSpacing(1);
	sldZoom.setPaintTicks(true);
	sldZoom.setPaintLabels(true);
	sldZoom.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			imageCanvas.setZoom((double)sldZoom.getValue()/100);
		
		}
	});
	
	sldZoom.setValue(20);
	
	add(sldZoom, BorderLayout.SOUTH);

	layerList.getList().addListSelectionListener(e -> {

	    if (e.getValueIsAdjusting()) {
		return;
	    }
	    var layer = layerList.getSelectedLayer();
	    imageCanvas.selectLayer(layer);
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

		if (object instanceof File file)
		{
	    		if(isImageFile(file))
	    		    imageCanvas.addLayer(new FrameLayer(file));
		
        		if(isFontFile(file))
        		{
        		    var flayer = new TextLayer("A Sample Text",file);
        		    var dialog = new TextLayerDialog(null, flayer);
        		    dialog.setVisible(true);
        		    imageCanvas.addLayer(flayer);
        		}
		}
		
		if (object instanceof Color c)
		{
		    imageCanvas.addLayer(new BorderLayer(c));
		}
		
		if (object.toString().equals("URL"))
		{
		    
		  var url = JOptionPane.showInputDialog("URL ?");
		    
		    try {
			imageCanvas.addLayer(new IllustrationLayer(URI.create(url).toURL()));
		    } catch (MalformedURLException e1) {
			logger.error(e1);
		    }
		}
		
		

		layerList.setLayers(imageCanvas.getLayers());
	    }
	});
    }

    // =====================================================================
    // Tree
    // =====================================================================

    private DefaultMutableTreeNode initTree(File file) {

	DefaultMutableTreeNode node = new DefaultMutableTreeNode(file);

	if (!file.isDirectory()) {
	    return node;
	}

	File[] children = file.listFiles();

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

	for (File child : children) {
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
    
    private boolean isFontFile(File file) {
	var name = file.getName().toLowerCase();
	return name.endsWith(".ttf");
    }
    

    // =====================================================================
    // Demo
    // =====================================================================

    public static void main(String[] args) {

	//var directry = "D:\\Téléchargements\\Full-Magic-Pack-main\\data";
	var directry="D:\\programmation\\GIT\\mtg-card-generator-main\\assets";
	
	SwingUtilities.invokeLater(() -> {

	    var frame = new JFrame("MTG Card Composer");
	    	 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    	frame.setContentPane(new MtgCardComposer(new File(directry)));
	    	 
	    	 
	    	 
	    	 frame.setSize(1200, 900);
	    	 frame.setLocationRelativeTo(null);
	    	 frame.setVisible(true);
	});
    }
}