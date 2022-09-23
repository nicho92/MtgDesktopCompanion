package org.magic.api.interfaces;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.criterias.MTGCrit;

public interface MTGQueryable  {
	public List<MagicCard> searchByCriteria(MagicCollection c, MTGCrit<?>... crits) throws IOException;
	public List<MagicCard> searchByCriteria(MagicCollection c, List<MTGCrit> crits) throws IOException;

}
