package org.magic.api.interfaces;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicEdition;

public interface DashBoard {

	public List<CardShake> getShakerFor(String gameFormat,String weekordaly) throws IOException;
	public List<CardShake> getShakeForEdition(MagicEdition edition) throws IOException;
	
	
	public String getName();
	public Date getUpdatedDate();
	void setProperties(String k, Object value);
	boolean isEnable();
	void enable(boolean t);
	
}
