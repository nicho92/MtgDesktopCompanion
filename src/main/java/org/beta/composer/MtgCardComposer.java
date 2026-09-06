package org.beta.composer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import org.beta.composer.gui.CardCanvas;
import org.beta.composer.gui.LayerDetailPanel;
import org.beta.composer.gui.LayersListPanel;
import org.beta.composer.gui.LeftPanel;
import org.beta.composer.gui.TextLayerDialog;
import org.beta.composer.layer.BorderLayer;
import org.beta.composer.layer.ImageLayer;
import org.beta.composer.layer.TextLayer;

public class MtgCardComposer extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final JTree tree;
    private final CardCanvas imageCanvas;
    private final LayersListPanel layerList;
    private final LayerDetailPanel inspector;
    
    
    public MtgCardComposer(File rootDirectory) {

	if (rootDirectory == null || !rootDirectory.isDirectory()) {
	    throw new IllegalArgumentException("rootDirectory must be a valid directory");
	}

	setLayout(new BorderLayout());

	var rootNode = createTreeNode(rootDirectory);

	
	var rootColors = new DefaultMutableTreeNode("Borders");
		rootColors.add(new DefaultMutableTreeNode(Color.BLACK));
		rootColors.add(new DefaultMutableTreeNode(Color.WHITE));
		rootColors.add(new DefaultMutableTreeNode(Color.GRAY));
		rootColors.add(new DefaultMutableTreeNode(Color.YELLOW));
		
	rootNode.add(rootColors);
	
	
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
	imageCanvas.setZoom(0.20);
	
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
	    		    imageCanvas.addLayer(new ImageLayer(file));
		
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


		layerList.setLayers(imageCanvas.getLayers());
	    }
	});
    }

    // =====================================================================
    // Tree
    // =====================================================================

    private DefaultMutableTreeNode createTreeNode(File file) {

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
	    node.add(createTreeNode(child));
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

	SwingUtilities.invokeLater(() -> {

	    File directory = new File("D:\\programmation\\GIT\\mtg-card-generator-main\\assets");

	    JFrame frame = new JFrame("MTG Card Composer");

	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    frame.setContentPane(new MtgCardComposer(directory));

	    frame.setSize(1200, 900);

	    frame.setLocationRelativeTo(null);

	    frame.setVisible(true);
	});
    }
}