package org.magic.composer.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.magic.composer.gui.listeners.LayerChangeListener;
import org.magic.composer.gui.listeners.LayerSelectionListener;
import org.magic.composer.layer.Layer;

public class CardCanvas extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
   
    private final List<Layer> layers = new ArrayList<>();

    private Layer selectedLayer;

    private int dragOffsetX;
    private int dragOffsetY;

    private double zoom = 1.0;

    private LayerSelectionListener selectionListener;
    private LayerChangeListener layerChangeListener;

    private RenderingHints  hints;
    
    
    
    public CardCanvas() {

	setBackground(Color.LIGHT_GRAY);

	hints = new RenderingHints(Map.of(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON,
							    RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED, 
							    RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY, 
							    RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC));
		 
	
	var mouseHandler = new MouseAdapter() {

	    @Override
	    public void mousePressed(MouseEvent e) {

		selectLayerAt(toCanvasX(e.getX()), toCanvasY(e.getY()));
		
		notifyLayerChanged();
	    }

	    @Override
	    public void mouseDragged(MouseEvent e) {

		if (selectedLayer == null) {
		    return;
		}

		int mouseX = toCanvasX(e.getX());
		int mouseY = toCanvasY(e.getY());

		selectedLayer.setX(mouseX - dragOffsetX);
		selectedLayer.setY(mouseY - dragOffsetY);
		
		notifyLayerChanged();

		
		repaint();
	    }
	};

	addMouseListener(mouseHandler);
	addMouseMotionListener(mouseHandler);
    }

    // =====================================================================
    // Zoom
    // =====================================================================

    public double getZoom() {
	return zoom;
    }
    
    public void setHints(RenderingHints hints) {
	this.hints = hints;
    }
    
    public RenderingHints getHints() {
	return hints;
    }
    

    public void setZoom(double zoom) {
	if (zoom <= 0) {
	    throw new IllegalArgumentException("Zoom must be greater than 0");
	}

	this.zoom = zoom;

	revalidate();
	repaint();
    }

    // =====================================================================
    // Coordinate conversion
    // =====================================================================

    private int toCanvasX(int screenX) {

	return (int) (screenX / zoom);
    }

    private int toCanvasY(int screenY) {

	return (int) (screenY / zoom);
    }

    // =====================================================================
    // Selection listener
    // =====================================================================

    public void setLayerSelectionListener(LayerSelectionListener listener) {
	this.selectionListener = listener;
    }
    public void setLayerChangeListener(LayerChangeListener listener) {
	    this.layerChangeListener = listener;
    }
    
    private void notifyLayerChanged() {
	    if (layerChangeListener != null) {
	        layerChangeListener.layerChanged(selectedLayer);
	    }
	}
    
    // =====================================================================
    // Layers
    // =====================================================================

    public void addLayer(Layer layer) {
	layers.add(layer);
	selectedLayer = layer;

	notifyLayerSelection();
	repaint();
    }

    public List<Layer> getLayers() {

	return Collections.unmodifiableList(layers);
    }

    public Layer getSelectedLayer() {
	return selectedLayer;
    }

    public void selectLayer(Layer layer) {

	if (layer == null) {
	    selectedLayer = null;
	    repaint();
	    return;
	}

	if (!layers.contains(layer)) {
	    return;
	}
	selectedLayer = layer;
	repaint();
    }

    public void removeLayer(Layer layer) {

	if (layer == null) {
	    return;
	}

	int index = layers.indexOf(layer);

	if (index < 0) {
	    return;
	}

	layers.remove(index);

	if (selectedLayer == layer) {
	    if (!layers.isEmpty()) {
		selectedLayer = layers.get(Math.min(index, layers.size() - 1));
	    } else {
		selectedLayer = null;
	    }
	}

	notifyLayerSelection();
	repaint();
    }

    public void removeSelectedLayer() {
	removeLayer(selectedLayer);
    }

    // =====================================================================
    // Layer order
    // =====================================================================

    public void moveLayerUp(Layer layer) {

	if (layer == null) {
	    return;
	}

	int index = layers.indexOf(layer);

	if (index < 0 || index >= layers.size() - 1) {

	    return;
	}

	Collections.swap(layers, index, index + 1);

	selectedLayer = layer;

	repaint();
    }

    public void moveLayerDown(Layer layer) {

	if (layer == null) {
	    return;
	}

	int index = layers.indexOf(layer);

	if (index <= 0) {
	    return;
	}

	Collections.swap(layers, index, index - 1);

	selectedLayer = layer;

	repaint();
    }
    
   
    public BufferedImage getCardImage()
    {
	    int minX = getLayers().stream()
	            .mapToInt(Layer::getX)
	            .min()
	            .orElse(0);

	    int minY = getLayers().stream()
	            .mapToInt(Layer::getY)
	            .min()
	            .orElse(0);

	    int maxX = getLayers().stream()
	            .mapToInt(layer -> layer.getX() + layer.getWidth())
	            .max()
	            .orElse(0);

	    int maxY = getLayers().stream()
	            .mapToInt(layer -> layer.getY() + layer.getHeight())
	            .max()
	            .orElse(0);

	    int width = maxX - minX;
	    int height = maxY - minY;
	
	var awtImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	var g = awtImage.createGraphics();
	 g.translate(-minX, -minY);
	 paintCard(g);

	return awtImage;
	
	
    }
    


    // =====================================================================
    // Mouse selection
    // =====================================================================

    private void selectLayerAt(int x, int y) {

	/*
	 * On commence par le layer le plus haut.
	 */

	for (int i = layers.size() - 1; i >= 0; i--) {

	    Layer layer = layers.get(i);

	    if (!layer.isVisible()) {
		continue;
	    }

	    if (layer.contains(x, y)) {

		selectedLayer = layer;

		dragOffsetX = x - layer.getX();

		dragOffsetY = y - layer.getY();

		notifyLayerSelection();

		repaint();

		return;
	    }
	}

	selectedLayer = null;

	notifyLayerSelection();

	repaint();
    }

    private void notifyLayerSelection() {

	if (selectionListener != null) {

	    selectionListener.layerSelected(selectedLayer);
	}
    }
   

    // =====================================================================
    // Painting
    // =====================================================================

    @Override
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	var g2 = (Graphics2D) g.create();
	try {
	    
	    g2.setRenderingHints(hints);
	    g2.scale(zoom, zoom);

	    // -------------------------------------------------------------
	    // Layers
	    // -------------------------------------------------------------
	    paintCard(g2);
	   
	    
	    if (selectedLayer != null) {
		drawSelection(g2, selectedLayer);
	    }
	} finally {
	    g2.dispose();
	}
    }

    private void paintCard(Graphics2D g2) {
	 for (var layer : layers) {
		if (!layer.isVisible()) {
		    continue;
		}
		layer.paint(g2);
	    }
	
    }

    private void drawSelection(Graphics2D g2, Layer layer) {

	int x = layer.getX();
	int y = layer.getY();

	int width = layer.getWidth();
	int height = layer.getHeight();
	var oldStroke = g2.getStroke();
	
	g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[] { 6, 6 }, 0));
	g2.setColor(Color.WHITE);
	g2.drawRect(x, y, width, height);
	g2.setStroke(oldStroke);
    }

    public void clear() {
	layers.clear();
	notifyLayerChanged();
	repaint();
	
    }

}