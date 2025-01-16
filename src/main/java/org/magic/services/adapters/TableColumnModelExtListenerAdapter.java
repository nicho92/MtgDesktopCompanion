package org.magic.services.adapters;

import java.beans.PropertyChangeEvent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;

import org.jdesktop.swingx.event.TableColumnModelExtListener;

public class TableColumnModelExtListenerAdapter implements TableColumnModelExtListener {

	@Override
	public void columnAdded(TableColumnModelEvent e) {
		// do nothing

	}

	@Override
	public void columnRemoved(TableColumnModelEvent e) {
		// do nothing

	}

	@Override
	public void columnMoved(TableColumnModelEvent e) {
		// do nothing

	}

	@Override
	public void columnMarginChanged(ChangeEvent e) {
		// do nothing

	}

	@Override
	public void columnSelectionChanged(ListSelectionEvent e) {
		// do nothing

	}

	@Override
	public void columnPropertyChange(PropertyChangeEvent event) {
		// do nothing

	}

}
