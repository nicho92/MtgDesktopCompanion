package org.beta.composer.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
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

import org.beta.composer.layer.Layer;
import org.magic.api.exports.impl.JsonExport;

public class LayersListPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final DefaultListModel<Layer> model;
    private final JList<Layer> list;

    private CardCanvas canvas;

    public LayersListPanel() {

	setLayout(new BorderLayout());

	// =================================================================
	// Title
	// =================================================================

	JLabel title = new JLabel("Layers");

	title.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

	add(title, BorderLayout.NORTH);

	// =================================================================
	// List
	// =================================================================

	model = new DefaultListModel<>();

	list = new JList<>(model);

	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	list.setCellRenderer(new LayerCellRenderer());

	add(new JScrollPane(list), BorderLayout.CENTER);

	// =================================================================
	// Buttons
	// =================================================================

	JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));

	JButton upButton = new JButton("↑");

	JButton downButton = new JButton("↓");

	JButton deleteButton = new JButton("✕");

	buttons.add(upButton);
	buttons.add(downButton);
	buttons.add(deleteButton);
	
	
	add(buttons, BorderLayout.SOUTH);

	// =================================================================
	// Actions
	// =================================================================

	upButton.addActionListener(_ -> moveSelectedUp());

	downButton.addActionListener(_ -> moveSelectedDown());

	deleteButton.addActionListener(_ -> removeSelected());
	
	// =================================================================
	// Double click -> rename
	// =================================================================

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

	list.setSelectedValue(layer, true);
    }

    // =====================================================================
    // Actions
    // =====================================================================

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

    // =====================================================================
    // Renderer
    // =====================================================================

    private static class LayerCellRenderer extends DefaultListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected,
		boolean focus) {

	    var layer = (Layer) value;

	    String prefix = layer.isVisible() ? "👁 " : "   ";

	    return super.getListCellRendererComponent(list, prefix + layer.getName(), index, selected, focus);
	}
    }
}