package org.magic.services.providers;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.FontUIResource;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Logger;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.reflections.Reflections;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.jtattoo.plaf.AbstractLookAndFeel;

public class LookAndFeelProvider {

	private Logger logger = MTGLogger.getLogger(this.getClass());
	private List<LookAndFeelInfo> list;

	public LookAndFeelProvider() {
		list = new ArrayList<>();
	}

	public void setLookAndFeel(Component ui, LookAndFeelInfo lookAndFeel,boolean saving) {
		setLookAndFeel(ui, lookAndFeel.getClassName(),saving);
	}

	public void setUIFont() {
	    Enumeration<?> keys = UIManager.getDefaults().keys();
	    while (keys.hasMoreElements()) {
	        Object key = keys.nextElement();
	        Object value = UIManager.get(key);
	        if (value instanceof FontUIResource)
	            saveProperties(key, new FontUIResource(MTGControler.getInstance().getFont()));
	    }
	}

	public void setLookAndFeel(Component ui, String lookAndFeel,boolean saving) {
		try {

			if(saving)
				MTGControler.getInstance().setProperty("lookAndFeel", lookAndFeel);


			if(lookAndFeel==null || lookAndFeel.isEmpty())
				lookAndFeel=UIManager.getSystemLookAndFeelClassName();


			FlatLaf.registerCustomDefaultsSource(MTGConstants.DATA_DIR);

			logger.debug("loading look&feel custom file : {} : {}",new File(MTGConstants.DATA_DIR,"FlatLaf.properties").getAbsolutePath(),new File(MTGConstants.DATA_DIR,"FlatLaf.properties").exists());

			FlatAnimatedLafChange.showSnapshot();
			UIManager.setLookAndFeel(lookAndFeel);
			SwingUtilities.updateComponentTreeUI(ui);
			FlatAnimatedLafChange.hideSnapshotWithAnimation();

		} catch (Exception e) {
			logger.error("error setLookAndFeel",e);
		}
	}

	public LookAndFeelInfo[] getStandardLookAndFeel() {
		return UIManager.getInstalledLookAndFeels();

	}

	public LookAndFeelInfo[] getAllLookAndFeel() {
		return ArrayUtils.addAll(UIManager.getInstalledLookAndFeels(), getExtraLookAndFeel());
	}

	public LookAndFeelInfo[] getExtraLookAndFeel() {

		if (!list.isEmpty())
			return list.toArray(new LookAndFeelInfo[list.size()]);

		var classReflections = new Reflections("org.pushingpixels.substance.api.skin");
		list = new ArrayList<>();
		for (Class<? extends SubstanceLookAndFeel> c : classReflections.getSubTypesOf(SubstanceLookAndFeel.class)) {
			try {
				SubstanceLookAndFeel look = c.getConstructor().newInstance();
				list.add(new LookAndFeelInfo(look.getID(), c.getName()));
			} catch (Exception e) {
				logger.error("error for {}",c, e);
			}
		}

		classReflections = new Reflections("com.jtattoo.plaf");
		for (Class<? extends AbstractLookAndFeel> c : classReflections.getSubTypesOf(AbstractLookAndFeel.class)) {
			try {
				var look = c.getConstructor().newInstance();
				list.add(new LookAndFeelInfo("JTatoo " + look.getID(), c.getName()));
			} catch (Exception e) {
				logger.error("Loading {}",c, e);
			}
		}

		classReflections = new Reflections("com.formdev.flatlaf");
		for (Class<? extends FlatLaf> c : classReflections.getSubTypesOf(FlatLaf.class)) {
			try {
				FlatLaf look = c.getConstructor().newInstance();

				list.add(new LookAndFeelInfo("FlatLaf " + look.getID(), c.getName()));
			} catch (Exception e) {
				logger.trace("Loading {}:{}",c,e);
			}
		}
		return list.toArray(new LookAndFeelInfo[list.size()]);
	}


	public void saveProperties(Object key,Object value)
	{

		UIManager.put(key, value);
	}


	public void setFont(FontUIResource myFont) {
	    saveProperties("CheckBoxMenuItem.acceleratorFont", myFont);
	    saveProperties("Button.font", myFont);
	    saveProperties("ToggleButton.font", myFont);
	    saveProperties("RadioButton.font", myFont);
	    saveProperties("CheckBox.font", myFont);
	    saveProperties("ColorChooser.font", myFont);
	    saveProperties("ComboBox.font", myFont);
	    saveProperties("Label.font", myFont);
	    saveProperties("List.font", myFont);
	    saveProperties("MenuBar.font", myFont);
	    saveProperties("Menu.acceleratorFont", myFont);
	    saveProperties("RadioButtonMenuItem.acceleratorFont", myFont);
	    saveProperties("MenuItem.acceleratorFont", myFont);
	    saveProperties("MenuItem.font", myFont);
	    saveProperties("RadioButtonMenuItem.font", myFont);
	    saveProperties("CheckBoxMenuItem.font", myFont);
	    saveProperties("OptionPane.buttonFont", myFont);
	    saveProperties("OptionPane.messageFont", myFont);
	    saveProperties("Menu.font", myFont);
	    saveProperties("PopupMenu.font", myFont);
	    saveProperties("OptionPane.font", myFont);
	    saveProperties("Panel.font", myFont);
	    saveProperties("ProgressBar.font", myFont);
	    saveProperties("ScrollPane.font", myFont);
	    saveProperties("Viewport.font", myFont);
	    saveProperties("TabbedPane.font", myFont);
	    saveProperties("Slider.font", myFont);
	    saveProperties("Table.font", myFont);
	    saveProperties("TableHeader.font", myFont);
	    saveProperties("TextField.font", myFont);
	    saveProperties("Spinner.font", myFont);
	    saveProperties("TextArea.font", myFont);
	    saveProperties("TextPane.font", myFont);
	    saveProperties("EditorPane.font", myFont);
	    saveProperties("TabbedPane.smallFont", myFont);
	    saveProperties("TitledBorder.font", myFont);
	    saveProperties("ToolBar.font", myFont);
	    saveProperties("ToolTip.font", myFont);
	    saveProperties("Tree.font", myFont);
	    saveProperties("FormattedTextField.font", myFont);
	    saveProperties("IconButton.font", myFont);
	    saveProperties("InternalFrame.optionDialogTitleFont", myFont);
	    saveProperties("InternalFrame.paletteTitleFont", myFont);
	    saveProperties("InternalFrame.titleFont", myFont);
	}

}
