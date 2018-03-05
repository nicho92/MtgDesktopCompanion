package org.magic.api.interfaces.abstracts;

import java.io.File;

import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGConstants;

public abstract class AbstractMagicDAO extends AbstractMTGPlugin implements MTGDao {


	@Override
	public PLUGINS getType() {
		return PLUGINS.DAO;
	}
	
	public AbstractMagicDAO() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "dao");
		if(!confdir.exists())
			confdir.mkdir();
		load();
		
		if(!new File(confdir, getName()+".conf").exists()){
			initDefault();
			save();
		} 
	}
}
