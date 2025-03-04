package org.magic.api.dao.impl;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jooq.SQLDialect;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.extra.AbstractMagicSQLDAO;
import org.magic.services.MTGConstants;

public class DerbyDAO extends AbstractMagicSQLDAO {

	@Override
	public String getName() {
		return "Derby";
	}
	
	
	public DerbyDAO() {
		super();
		System.setProperty("derby.system.home", new File(MTGConstants.CONF_DIR,"logs").getAbsolutePath());
		
	}
	
	@Override
	public List<MTGCard> listCardsFromCollection(MTGCollection collection, MTGEdition me) throws SQLException {

		var ret = new ArrayList<MTGCard>();
		var sql = "SELECT distinct(idmc) FROM stocks WHERE qte > 0 AND collection= ?";

		if (me != null)
			sql = "SELECT distinct(idmc) FROM stocks WHERE qte > 0 AND collection= ? and idMe = ?";

		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(sql)) {
			pst.setString(1, collection.getName());
			if (me != null)
				pst.setString(2, me.getId());
			try (ResultSet rs = executeQuery(pst)) {
					while (rs.next()) 
					{
						try (var c2 = pool.getConnection(); var pst2 = c2.prepareStatement("SELECT mcard FROM stocks WHERE idmc = ? AND collection= ?")) {
							pst2.setString(1,rs.getString(1) );
							pst2.setString(2, collection.getName());
							var rs2 = executeQuery(pst2);
							
							if(rs2.next()) {
							var mc = readCard(rs2, MCARD);
							notify(mc);
							ret.add(mc);
							}
					}
				}
			}
		}
		return ret;	
	}
	

	@Override
	protected String getjdbcnamedb() {
		return "derby";
	}

	@Override
	protected String getjdbcUrl() {
		return "jdbc:derby:"+MTGConstants.DATA_DIR.getAbsolutePath()+"\\"+getString(DB_NAME)+";create="+!new File(getFile(SERVERNAME),getString(DB_NAME)).exists();
	}
	
	@Override
	protected String getdbSizeQuery() {
		return "SELECT T2.CONGLOMERATENAME, (T2.PAGESIZE * T2.NUMALLOCATEDPAGES) FROM SYS.SYSTABLES systabs, TABLE (SYSCS_DIAG.SPACE_TABLE(systabs.tablename)) AS T2 WHERE systabs.tabletype = 'T'   AND T2.CONGLOMERATENAME NOT LIKE 'IDX%' AND T2.CONGLOMERATENAME NOT LIKE 'SQL%'";
	}

	@Override
	protected SQLDialect getDialect() {
		return SQLDialect.DERBY;
	}
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
		m.get(DB_NAME).setDefaultValue("derbyDB");
		m.get(SERVERNAME).setDefaultValue(new File(MTGConstants.DATA_DIR,m.get(DB_NAME).getDefaultValue()).getAbsolutePath());
		return m;
	}
	

}
