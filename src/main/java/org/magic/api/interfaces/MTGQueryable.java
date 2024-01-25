package org.magic.api.interfaces;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCollection;
import org.magic.api.criterias.MTGCrit;

public interface MTGQueryable  {
	public List<MTGCard> searchByCriteria(MTGCollection c, MTGCrit<?>... crits) throws IOException;
	public List<MTGCard> searchByCriteria(MTGCollection c, List<MTGCrit> crits) throws IOException;

}
