package org.magic.gui.components;

import static org.magic.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.jdesktop.swingx.JXTree;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.tools.BeanTools;
import org.magic.tools.UITools;

public class ExportConfiguratorPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode();
	private DefaultTreeModel model = new DefaultTreeModel(root);
	private JTextField jtf;
	private JButton btnExport ;
	private JTextField txtSeparator;
	private JCheckBox chkseparator;



	public ExportConfiguratorPanel() {
		setLayout(new BorderLayout(0, 0));
		var tree = new JXTree(model);
		var panelBas = new JPanel();
		jtf = new JTextField(50);
		txtSeparator = new JTextField(10);
		chkseparator = new JCheckBox(capitalize("AUTO_ADD"));


		btnExport = UITools.createBindableJButton(null, MTGConstants.ICON_EXPORT, KeyEvent.VK_E, "export");
		add(new JScrollPane(tree), BorderLayout.WEST);
		add(new JScrollPane(jtf), BorderLayout.CENTER);

		add(panelBas, BorderLayout.SOUTH);

		panelBas.add(chkseparator);
		panelBas.add(txtSeparator);
		panelBas.add(btnExport);

		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					var selPath = tree.getPathForLocation(e.getX(), e.getY());

					if(selPath!=null)
					{
						jtf.setText(jtf.getText()+BeanTools.TOKEN_START+StringUtils.join(ArrayUtils.remove(selPath.getPath(), 0),".")+BeanTools.TOKEN_END);

						if(chkseparator.isSelected())
							jtf.setText(jtf.getText()+txtSeparator.getText());

						jtf.requestFocus();




					}

	            }
			}
			});

	}

	public void initTree(Object o)
	{
		BeanTools.describe(o).entrySet().forEach(e->{

			var entry = new DefaultMutableTreeNode(e.getKey());

			if(e.getValue()!=null && !(ClassUtils.isPrimitiveOrWrapper(e.getValue().getClass())) && !(e.getValue() instanceof String))
			{
				BeanTools.describe(e.getValue()).entrySet().forEach(f->{
					var entryEd = new DefaultMutableTreeNode(f.getKey());
					entry.add(entryEd);

					if(f.getValue()!=null && !(ClassUtils.isPrimitiveOrWrapper(f.getValue().getClass())) && !(f.getValue() instanceof String))
					{

						BeanTools.describe(f.getValue()).entrySet().forEach(g->{
							var entry2 = new DefaultMutableTreeNode(g.getKey());
							entryEd.add(entry2);
						});
					}
				});

				if(e.getValue() instanceof List)
				{
					List<?> t = (List<?>)e.getValue();

					if(!t.isEmpty())
						BeanTools.describe(t.get(0)).entrySet().forEach(f->{
							var entryEd = new DefaultMutableTreeNode(f.getKey());
							entry.add(entryEd);
						});

				}
			}
			root.add(entry);
		});
		model.reload();
	}

	public JButton getBtnExport() {
		return btnExport;
	}

	@Override
	public String getTitle() {
		return "Personnal Export";
	}

	public String getResult() {
		return jtf.getText();
	}

	public void setRegex(String string) {
		jtf.setText(string);

	}


}
