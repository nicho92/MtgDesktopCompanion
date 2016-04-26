package org.magic.api.interfaces;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicEdition;

public abstract class AbstractDashBoard {

	public static enum FORMAT { standard,legacy,vintage,modern};
	public static enum ONLINE_PAPER {online, paper};

	
	public abstract List<CardShake> getShakerFor(String gameFormat,String weekordaly) throws IOException;
	public abstract List<CardShake> getShakeForEdition(MagicEdition edition) throws IOException;
	
	
	public abstract String getName();
	public abstract void setSupportType(ONLINE_PAPER onlineOrPaper);
	
}
