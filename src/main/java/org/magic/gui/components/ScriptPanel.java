package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.ScriptException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.apache.commons.io.FileUtils;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jfree.ui.ExtensionFileFilter;
import org.magic.api.beans.PluginEntry;
import org.magic.api.interfaces.MTGScript;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;
import org.magic.services.ThreadManager;
import org.magic.tools.Chrono;
import org.magic.tools.UITools;

public class ScriptPanel extends MTGUIComponent {
	
	private static final long serialVersionUID = 1L;
	private RSyntaxTextArea editorPane;
	private JTextPane resultPane;
	private JComboBox<MTGScript> cboScript;
	private JCheckBox chkShowReturn ;
	private JLabel lblInfo;
	private File currentFile;
	
	@Override
	public String getTitle() {
		return "Script";
	}
	
	
	public ScriptPanel() {
		setLayout(new BorderLayout());
		editorPane = new RSyntaxTextArea();
		editorPane.setCodeFoldingEnabled(true);
		resultPane = new JTextPane();
		JSplitPane splitPane = new JSplitPane();
		JPanel paneHaut = new JPanel();
		JPanel paneBas = new JPanel();
		JButton btnOpen = new JButton(MTGConstants.ICON_OPEN);
		JButton btnSaveButton = new JButton(MTGConstants.ICON_SAVE);
		JButton btnNewButton = new JButton(MTGConstants.ICON_NEW);
		JButton btnRun = new JButton(MTGConstants.PLAY_ICON);
		
		lblInfo = new JLabel("Result");
		cboScript = UITools.createCombobox(MTGScript.class, true);
		chkShowReturn = new JCheckBox("Show return");
		
		setPreferredSize(new Dimension(800, 600));
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setLeftComponent(new RTextScrollPane(editorPane));
		splitPane.setRightComponent(new JScrollPane(resultPane));
		splitPane.setDividerLocation(0.5);
		splitPane.setResizeWeight(0.5);
		editorPane.setSyntaxEditingStyle(((MTGScript)cboScript.getSelectedItem()).getContentType());
		
		paneHaut.add(cboScript);
		paneHaut.add(btnNewButton);
		paneHaut.add(btnOpen);
		paneHaut.add(btnSaveButton);
		paneHaut.add(chkShowReturn);
		paneHaut.add(btnRun);
		add(paneHaut,BorderLayout.NORTH);
		add(splitPane,BorderLayout.CENTER);
		add(paneBas,BorderLayout.SOUTH);
		paneBas.setLayout(new BorderLayout(0, 0));
		paneBas.add(lblInfo, BorderLayout.WEST);
		
		JButton btnClear = new JButton(MTGConstants.ICON_SMALL_CLEAR);
		paneBas.add(btnClear, BorderLayout.EAST);
		
		new AutoCompletion(createCompletionProvider()).install(editorPane);
		
		
		btnClear.addActionListener(al->resultPane.setText(""));
		
		btnNewButton.addActionListener(al->{
			currentFile=null;
			editorPane.setText("");
			resultPane.setText("");
		});
		
		
		cboScript.addItemListener(il->editorPane.setSyntaxEditingStyle(((MTGScript)cboScript.getSelectedItem()).getContentType()));
		
		
		btnRun.addActionListener(al->{
			
			Chrono c = new Chrono();
			c.start();
			btnRun.setEnabled(false);
			ThreadManager.getInstance().executeThread(()->{
				try {
					
					lblInfo.setText("Running");
					MTGScript scripter = (MTGScript)cboScript.getSelectedItem();
					scripter.init();
					StringWriter writer = new StringWriter();
					scripter.setOutput(writer);
					
					Object ret = scripter.runContent(editorPane.getText());
					
					appendResult(writer.toString()+"\n");
					
					if(chkShowReturn.isSelected())
						appendResult("Return :" + ret+"\n");
					
				} catch (Exception e) {
					appendResult(e.getMessage()+"\n",Color.RED);
				}
					
				lblInfo.setText("Running time : " + c.stop() +"ms");
				btnRun.setEnabled(true);
			}, "executing script");
		});
		
		
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent componentEvent) {
				splitPane.setDividerLocation(.45);
				splitPane.setDividerLocation(.5);
				removeComponentListener(this);
			}

		});
		
		
		btnSaveButton.addActionListener(al->{
			
			int ret=JFileChooser.CANCEL_OPTION;
			if(currentFile !=null)
			{
				int res= JOptionPane.showConfirmDialog(this, MTGControler.getInstance().getLangService().getCapitalize("OVERRIDE") + " " + currentFile + " ?", MTGControler.getInstance().getLangService().getCapitalize("OVERRIDE"), JOptionPane.YES_NO_OPTION);
				if(res==JOptionPane.YES_OPTION)
					ret=JFileChooser.APPROVE_OPTION;
			}
			
			if(ret==JFileChooser.CANCEL_OPTION)
			{
				JFileChooser choose = new JFileChooser(MTGConstants.DATA_DIR);
				choose.setFileFilter(new ExtensionFileFilter(cboScript.getSelectedItem().toString(), ((MTGScript)cboScript.getSelectedItem()).getExtension()));
				ret = choose.showSaveDialog(this);
				if(ret==JFileChooser.APPROVE_OPTION)
				{
					currentFile = choose.getSelectedFile();
				}
			}
			
			if(ret==JFileChooser.APPROVE_OPTION && currentFile!=null)
			{
				try {
					FileUtils.writeStringToFile(currentFile, editorPane.getText(), MTGConstants.DEFAULT_ENCODING);
					appendResult(currentFile.getAbsolutePath() + " is saved", Color.CYAN);
				} catch (IOException e) {
					MTGControler.getInstance().notify(e);
				}
			}
		});
		
		btnOpen.addActionListener(al-> {
			JFileChooser choose = new JFileChooser(MTGConstants.DATA_DIR);
			
			choose.setFileFilter(new ExtensionFileFilter(cboScript.getSelectedItem().toString(), ((MTGScript)cboScript.getSelectedItem()).getExtension()));
			
			int ret = choose.showOpenDialog(this);
			if(ret==JFileChooser.APPROVE_OPTION)
			{
				try {
					currentFile = choose.getSelectedFile();
					editorPane.setText(FileUtils.readFileToString(currentFile, MTGConstants.DEFAULT_ENCODING));
				} catch (IOException e) {
					MTGControler.getInstance().notify(e);
				}
				
			}
		});
		
	}
	
	private CompletionProvider createCompletionProvider() {
		DefaultCompletionProvider provider = new DefaultCompletionProvider();
		Set<String> sets = new HashSet<>();
		for (Entry<Class, PluginEntry> exp : PluginRegistry.inst().entrySet()) {
			PluginRegistry.inst().getStringMethod(exp.getKey()).forEach(sets::add);
		}
		sets.forEach(s->provider.addCompletion(new BasicCompletion(provider, s)));
		return provider;
	}

	private void appendResult(String msg)
	{
		appendResult(msg, defaultColor);
	}
	
	private Color defaultColor=StyleContext.getDefaultStyleContext().getForeground(SimpleAttributeSet.EMPTY);
	
	private void appendResult(String msg, Color c)
    {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
		resultPane.setCharacterAttributes(aset, false);
        int len = resultPane.getDocument().getLength();
        resultPane.setCaretPosition(len);
        resultPane.replaceSelection(msg);
        resultPane.setCaretPosition(resultPane.getDocument().getLength());
    }


	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_SCRIPT;
	}
	
	
	public static void main(String[] args) {
		ThreadManager.getInstance().invokeLater(()->MTGUIComponent.createJDialog(new ScriptPanel(), true, false).setVisible(true));
	}
	

}
