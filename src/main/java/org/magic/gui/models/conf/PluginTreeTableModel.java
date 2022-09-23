package org.magic.gui.models.conf;

import static org.magic.tools.MTG.capitalize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.services.MTGControler;
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PluginTreeTableModel<T extends MTGPlugin> extends AbstractTreeTableModel {

	private String[] columnsNames = { capitalize("PROVIDERS"),
			capitalize("VALUE"), "Version",
			capitalize("ENABLED") };

	private static final int ENABLED_INDEX = 3;

	private MTGPlugin selectedProvider = null;
	private List<T> listElements;
	private boolean multipleSelection = false;

	public PluginTreeTableModel(boolean multipleSelection, List<T> listPlugins) {
		super(new Object());
		this.multipleSelection = multipleSelection;
		Collections.sort(listPlugins);
		listElements = listPlugins;
	}

	protected int getPosition(Entry<String, Object> k, Properties p) {
		for (var i = 0; i < p.keySet().size(); i++) {
			if (p.keySet().toArray()[i].toString().equals(k.getKey()))
				return i;
		}
		return -1;
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent instanceof MTGPlugin) {
			var dept = (T) parent;
			return dept.getProperties().entrySet().toArray()[index];
		}
		return new ArrayList<>(listElements).get(index);
	}


	@Override
	public int getChildCount(Object parent) {
		if (parent instanceof MTGPlugin) {
			var dept = (T) parent;
			return dept.getProperties().size();
		}
		return listElements.size();
	}

	@Override
	public void setValueAt(Object value, Object node, int column) {

		var strValue = String.valueOf(value);

		if (node instanceof MTGPlugin) {
			selectedProvider = (T) node;
			if (column == ENABLED_INDEX) {


				selectedProvider.enable(Boolean.parseBoolean(strValue));
				MTGControler.getInstance().setProperty(selectedProvider, selectedProvider.isEnable());

				if (!multipleSelection)
					listElements.stream().filter(p->p!=selectedProvider).forEach(p->{
						p.enable(false);
						MTGControler.getInstance().setProperty(p, p.isEnable());
					});


				if(listElements.stream().filter(MTGPlugin::isEnable).count()==0)
				{
					selectedProvider.enable(true);
					MTGControler.getInstance().setProperty(selectedProvider, selectedProvider.isEnable());
				}

			}
		}
		if (node instanceof Entry e && (column == 1)) {
			String k = (String) e.getKey();
			selectedProvider.setProperty(k, strValue);
			e.setValue(strValue);
		}
	}

	@Override
	public Object getValueAt(Object node, int column) {
		if (node instanceof MTGPlugin prov) {

			switch (column) {
			case 0:
				return prov;
			case 1:
				return prov.getStatut();
			case 2:
				return prov.getVersion();
			case 3:
				return prov.isEnable();
			default:
				return "";
			}
		} else if (node instanceof Entry) {
			Entry<String, String> emp = (Entry<String, String>) node;
			switch (column) {
			case 0:
				return emp.getKey();
			case 1:
				return emp.getValue();
			default:
				return "";
			}
		}
		return null;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		MTGPlugin dept = (MTGPlugin) parent;
		Entry k = (Entry) child;
		return getPosition(k, dept.getProperties());
	}

	@Override
	public int getColumnCount() {
		return columnsNames.length;
	}

	@Override
	public String getColumnName(int column) {
		return columnsNames[column];
	}

	@Override
	public boolean isLeaf(Object node) {
		return node instanceof Entry;
	}

	@Override
	public Class<?> getColumnClass(int column) {
		if (column == ENABLED_INDEX)
			return Boolean.class;

		return super.getColumnClass(column);
	}

	@Override
	public boolean isCellEditable(Object node, int column) {
		return (isLeaf(node) && column == 1) || (column == 3);
	}

	public void setSelectedNode(MTGPlugin pathComponent) {
		selectedProvider = pathComponent;
	}

}
