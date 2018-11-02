package org.magic.api.interfaces.abstracts;

import java.awt.Rectangle;
import java.io.File;
import java.util.Properties;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.magic.api.interfaces.MTGDashlet;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.utils.patterns.observer.Observer;

public abstract class AbstractJDashlet extends JInternalFrame implements MTGDashlet{

	private static final long serialVersionUID = 1L;
	public static final File confdir= new File(MTGConstants.CONF_DIR, "dashboards/dashlets");
	private Properties props;
	protected transient Logger logger = MTGLogger.getLogger(this.getClass());
	

	
	public AbstractJDashlet() {
		props = new Properties();
		
		if (!confdir.exists())
		{
			boolean ret = confdir.mkdirs();
			logger.debug(confdir + " doesn't exist, create it="+ret);
			
		}
	
		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
				AbstractJDashlet dash = (AbstractJDashlet) e.getInternalFrame();
				if (dash.getProperties().get("id") != null)
					FileUtils.deleteQuietly(new File(confdir, dash.getProperties().get("id") + ".conf"));
			}
		});
		
		
		setFrameIcon(getIcon());
		setTitle(getName());
		setResizable(true);
		setClosable(true);
		setIconifiable(true);
		setMaximizable(true);
		setBounds(new Rectangle(5,5, 536,346));
	}


	@Override
	public String getVersion() {
		return "1.0";
	}
	
	@Override
	public boolean isEnable() {
		return true;
	}
	
	public void setProperties(Properties p) {
		this.props = p;
	}

	public String getProperty(String k, String d) {
		return props.getProperty(k, d);
	}

	public void setProperty(Object k, Object v) {
		props.put(k, v);
	}
	
	@Override
	public String getString(String k) {
		return props.getProperty(k);
	}


	public Properties getProperties() {
		return props;
	}

	

	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public void setProperty(String k, Object value) {
		props.put(k, value);
		
	}

	

	@Override
	public void save() {
		// do nothing, managed in DashboardGUI
		
	}

	@Override
	public void load() {
		// do nothing, managed in DashboardGUI
		
	}

	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}

	@Override
	public PLUGINS getType() {
		return PLUGINS.DASHLET;
	}

	@Override
	public File getConfFile() {
		return null;
	}

	@Override
	public void initDefault() {
		// doNothing
		
	}


	@Override
	public void addObserver(Observer o) {
		// doNothing
		
	}

	@Override
	public void removeObservers() {
		// doNothing
		
	}

	@Override
	public void removeObserver(Observer o) {
		// doNothing
		
	}
}
