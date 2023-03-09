package org.magic.gui.components;

import static org.magic.services.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;

import javax.swing.ImageIcon;
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

import org.apache.commons.io.FilenameUtils;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jfree.ui.ExtensionFileFilter;
import org.magic.api.beans.technical.PluginEntry;
import org.magic.api.interfaces.MTGScript;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.Chrono;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.UITools;
public class ScriptPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private RSyntaxTextArea editorPane;
	private JTextPane resultPane;
	private JComboBox<MTGScript> cboScript;
	private JCheckBox chkShowReturn ;
	private JLabel lblInfo;
	private File currentFile;
	private transient Future<?> f;


	@Override
	public String getTitle() {
		return "Script";
	}


	public ScriptPanel() {
		setLayout(new BorderLayout());
		editorPane = new RSyntaxTextArea();
		editorPane.setCodeFoldingEnabled(true);
		resultPane = new JTextPane();
		var splitPane = new JSplitPane();
		var paneHaut = new JPanel();
		var paneBas = new JPanel();
		var btnOpen = UITools.createBindableJButton(null, MTGConstants.ICON_OPEN, KeyEvent.VK_O,"open");
		var btnSaveButton = UITools.createBindableJButton(null, MTGConstants.ICON_SAVE, KeyEvent.VK_S,"save");
		var btnNewButton = UITools.createBindableJButton(null, MTGConstants.ICON_NEW, KeyEvent.VK_N,"new");
		var btnRun =  UITools.createBindableJButton(null, MTGConstants.PLAY_ICON, KeyEvent.VK_R,"run");
		var btnStop =  UITools.createBindableJButton(null, MTGConstants.ICON_DELETE, KeyEvent.VK_K,"stop");
		var btnClear =  UITools.createBindableJButton(null, MTGConstants.ICON_SMALL_CLEAR, KeyEvent.VK_C,"clear");

		lblInfo = new JLabel("Result");
		cboScript = UITools.createComboboxPlugins(MTGScript.class, true);
		chkShowReturn = new JCheckBox("Show return");

		setPreferredSize(new Dimension(800, 600));
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setLeftComponent(new RTextScrollPane(editorPane));
		splitPane.setRightComponent(new JScrollPane(resultPane));
		splitPane.setDividerLocation(0.5);
		splitPane.setResizeWeight(0.5);
		editorPane.setSyntaxEditingStyle(((MTGScript)cboScript.getSelectedItem()).getContentType());
		btnStop.setEnabled(false);

		paneHaut.add(cboScript);
		paneHaut.add(btnNewButton);
		paneHaut.add(btnOpen);
		paneHaut.add(btnSaveButton);
		paneHaut.add(chkShowReturn);
		paneHaut.add(btnRun);
		paneHaut.add(btnStop);
		add(paneHaut,BorderLayout.NORTH);
		add(splitPane,BorderLayout.CENTER);
		add(paneBas,BorderLayout.SOUTH);
		paneBas.setLayout(new BorderLayout(0, 0));
		paneBas.add(lblInfo, BorderLayout.WEST);

		paneBas.add(btnClear, BorderLayout.EAST);

		new AutoCompletion(createCompletionProvider()).install(editorPane);





		btnClear.addActionListener(al->resultPane.setText(""));

		btnNewButton.addActionListener(al->{
			currentFile=null;
			editorPane.setText("");
			resultPane.setText("");
		});

		cboScript.addItemListener(il->editorPane.setSyntaxEditingStyle(((MTGScript)cboScript.getSelectedItem()).getContentType()));


		btnStop.addActionListener(e->{
			if(f!=null)
			{
				f.cancel(true);
				logger.debug("Canceling {}",f);
			}
		});



		btnRun.addActionListener(al->{

			var c = new Chrono();
			c.start();
			btnRun.setEnabled(false);
			btnStop.setEnabled(true);


			f = ThreadManager.getInstance().submitThread(new MTGRunnable() {

				@Override
				protected void auditedRun() {
					try {

						lblInfo.setText("Running");
						MTGScript scripter = (MTGScript)cboScript.getSelectedItem();
						scripter.init();
						var writer = new StringWriter();
						scripter.setOutput(writer);
						Object ret = scripter.runContent(editorPane.getText());
						appendResult(writer.toString()+"\n");

						if(chkShowReturn.isSelected())
							appendResult("Return :" + ret+"\n");

					}
					catch (Exception e) {
						logger.error("error scriptinng",e);
						appendResult(e.getMessage()+"\n",Color.RED);
					}

					lblInfo.setText("Running time : " + c.stop() +"s.");
					btnRun.setEnabled(true);
					btnStop.setEnabled(false);

				}
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
				int res= JOptionPane.showConfirmDialog(this, capitalize("OVERRIDE") + " " + currentFile + " ?", capitalize("OVERRIDE"), JOptionPane.YES_NO_OPTION);
				if(res==JOptionPane.YES_OPTION)
					ret=JFileChooser.APPROVE_OPTION;
			}

			if(ret==JFileChooser.CANCEL_OPTION)
			{
				var choose = new JFileChooser(MTGConstants.DATA_DIR);
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
					FileTools.saveFile(currentFile, editorPane.getText());

					if(FilenameUtils.getExtension(currentFile.getName()).isEmpty())
					{
						var fext = new File(currentFile.getParentFile(),currentFile.getName()+"."+((MTGScript)cboScript.getSelectedItem()).getExtension());
						boolean bext = currentFile.renameTo(fext);
						logger.debug("No extenstion, renaname to {}:{}",fext,bext);
					}


					appendResult(currentFile.getAbsolutePath() + " is saved", Color.CYAN);
				} catch (IOException e) {
					MTGControler.getInstance().notify(e);
				}
			}
		});

		btnOpen.addActionListener(al-> {
			var choose = new JFileChooser(MTGConstants.DATA_DIR);

			choose.setFileFilter(new ExtensionFileFilter(cboScript.getSelectedItem().toString(), ((MTGScript)cboScript.getSelectedItem()).getExtension()));

			int ret = choose.showOpenDialog(this);
			if(ret==JFileChooser.APPROVE_OPTION)
			{
				try {
					currentFile = choose.getSelectedFile();
					editorPane.setText(FileTools.readFile(currentFile));
				} catch (IOException e) {
					MTGControler.getInstance().notify(e);
				}

			}
		});

	}

	private CompletionProvider createCompletionProvider() {
		var provider = new DefaultCompletionProvider();
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
		var sc = StyleContext.getDefaultStyleContext();
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
}
