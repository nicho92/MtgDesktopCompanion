package org.magic.gui.components.composer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.apache.logging.log4j.Logger;
import org.magic.api.interfaces.extra.Layer;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.BeanTools;

public class LayersListPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final DefaultListModel<Layer> model;
    private final JList<Layer> list;
    protected transient Logger logger = MTGLogger.getLogger(this.getClass());
    private CardCanvas canvas;
    private final JButton copyButton;

    public LayersListPanel() {

	setLayout(new BorderLayout());

	
	JLabel title = new JLabel("Layers");
	
	title.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

	add(title, BorderLayout.NORTH);

	model = new DefaultListModel<>();

	list = new JList<>(model);

	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	list.setCellRenderer(new LayerCellRenderer());

	add(new JScrollPane(list), BorderLayout.CENTER);

	var buttons = new JPanel();

	var upButton = new JButton(MTGConstants.ICON_UP);
	var downButton = new JButton(MTGConstants.ICON_DOWN);
	var deleteButton = new JButton(MTGConstants.ICON_SMALL_DELETE);
	copyButton = new JButton(MTGConstants.ICON_SMALL_COPY);
	
	
	copyButton.setEnabled(false);
	
	buttons.setLayout(new GridLayout(0, 4, 0, 0));

	buttons.add(upButton);
	buttons.add(downButton);
	buttons.add(deleteButton);
	buttons.add(copyButton);

	add(buttons, BorderLayout.SOUTH);

	upButton.addActionListener(_ -> moveSelectedUp());

	downButton.addActionListener(_ -> moveSelectedDown());

	deleteButton.addActionListener(_ -> removeSelected());

	
	copyButton.addActionListener(_->{
	
	    try {
		var copy = BeanTools.cloneBean(getSelectedLayer());
		copy.setName(getSelectedLayer().getName()+"(copy)");
		
		model.addElement(copy);
		canvas.addLayer(copy);
		
	    } catch (Exception e) {
		logger.error(e);
	    } 
	    
	    
	});
	
	
	list.addMouseListener(new MouseAdapter() {

	    @Override
	    public void mouseClicked(MouseEvent e) {

		if (e.getClickCount() != 2) {
		    return;
		}

		var layer = list.getSelectedValue();

		if (layer == null) {
		    return;
		}

		renameLayer(layer);
	    }
	});
    }

    // =====================================================================
    // Canvas
    // =====================================================================

    public void setCanvas(CardCanvas canvas) {
	this.canvas = canvas;
    }

    // =====================================================================
    // Synchronisation
    // =====================================================================

    public void setLayers(List<Layer> layers) {
	
	if(layers==null)
	{
	    model.clear();
	    return;
	}
	
	
	var selected = getSelectedLayer();

	model.clear();

	/*
	 * Le dernier layer est affiché en premier dans la liste car il est au-dessus.
	 */

	for (int i = layers.size() - 1; i >= 0; i--) {

	    model.addElement(layers.get(i));
	}

	if (selected != null) {

	    list.setSelectedValue(selected, true);
	}
    }

    public Layer getSelectedLayer() {

	return list.getSelectedValue();
    }

    public void selectLayer(Layer layer) {

	if (layer == null) {

	    list.clearSelection();

	    return;
	}

	copyButton.setEnabled(layer!=null);
	
	list.setSelectedValue(layer, true);
    }

    private void moveSelectedUp() {

	if (canvas == null) {
	    return;
	}

	var layer = getSelectedLayer();

	if (layer == null) {
	    return;
	}

	canvas.moveLayerUp(layer);

	setLayers(canvas.getLayers());

	selectLayer(layer);
    }

    private void moveSelectedDown() {

	if (canvas == null) {
	    return;
	}

	var layer = getSelectedLayer();

	if (layer == null) {
	    return;
	}

	canvas.moveLayerDown(layer);

	setLayers(canvas.getLayers());

	selectLayer(layer);
    }

    private void removeSelected() {

	if (canvas == null) {
	    return;
	}

	var layer = getSelectedLayer();

	if (layer == null) {
	    return;
	}

	canvas.removeLayer(layer);

	setLayers(canvas.getLayers());
    }

    private void renameLayer(Layer layer) {

	String name = JOptionPane.showInputDialog(this, "Layer name:", layer.getName());

	if (name == null) {
	    return;
	}

	name = name.trim();

	if (name.isEmpty()) {
	    return;
	}

	layer.setName(name);

	list.repaint();
    }

    public JList<Layer> getList() {
	return list;
    }

    private class LayerCellRenderer extends DefaultListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected,
		boolean focus) {

	    var layer = (Layer) value;
	    return super.getListCellRendererComponent(list, layer.getName(), index, selected, focus);
	}
    }
}