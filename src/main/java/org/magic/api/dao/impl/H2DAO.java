package org.magic.api.dao.impl;

import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.h2.tools.Server;
import org.jooq.SQLDialect;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGGrading;
import org.magic.api.beans.abstracts.AbstractAuditableItem;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.extra.AbstractMagicSQLDAO;
import org.magic.services.MTGConstants;
import org.magic.services.tools.FileTools;

import com.google.gson.JsonArray;

public class H2DAO extends AbstractMagicSQLDAO {

	private static final String MODE = "MODE";

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}
	
	@Override
	public void init() throws SQLException {
		super.init();
		
		if(getBoolean("WEB_ENABLE"))
			Server.createWebServer("-webPort", getString("WEB_PORT"), "-tcpAllowOthers").start();
	}

	private String parseJson(String json)
	{
		var str=  StringEscapeUtils.unescapeJson(json);
		return StringUtils.substring(str, 1, str.length()-1);
	}
	
	@Override
	protected MTGGrading readGrading(ResultSet rs) throws SQLException {
		return serialiser.fromJson(parseJson(rs.getString("grading")), MTGGrading.class);
	}
	
	@Override
	protected Map<MTGCard, Integer> readDeckBoard(ResultSet rs, String field) throws SQLException {

		Map<MTGCard, Integer> ret = new HashMap<>();
		serialiser.fromJson(parseJson(rs.getString(field)), JsonArray.class).forEach(je->{

			var mc = serialiser.fromJson(je.getAsJsonObject().get("card").toString(), MTGCard.class);
			Integer qte = je.getAsJsonObject().get("qty").getAsInt();

			ret.put(mc, qte);


		});

		return ret;
	}

	@Override
	protected <T extends AbstractAuditableItem> T readTechnical(ResultSet rs, Class<T> classe) throws SQLException
	{
		return serialiser.fromJson(parseJson(rs.getString("techobject")), classe);
	}
	
	
	
	@Override
	@SuppressWarnings({ "unchecked" })
	protected Map<String, String> readTiersApps(ResultSet rs) throws SQLException {
		return serialiser.fromJson(parseJson(rs.getString("tiersAppIds")), Map.class);
	}
	
	@Override
	protected MTGCard readCard(ResultSet rs,String field) throws SQLException {
		MTGCard mc=null;
		try{
			mc = serialiser.fromJson(parseJson(rs.getString(field)), MTGCard.class);
		}
		catch(NullPointerException _)
		{
			return null;
		}
		return mc;
	}

	@Override
	protected SQLDialect getDialect() {
		return SQLDialect.H2;
	}

	@Override
	protected String getjdbcUrl() {
		return "jdbc:"+getjdbcnamedb()+(getString(MODE).equals("file")?"/":":")+getString(DB_NAME)+";CASE_INSENSITIVE_IDENTIFIERS=TRUE"+(getString(MODE).equals("mem")?";DB_CLOSE_DELAY=-1":"");
	}
	
	@Override
	protected String getjdbcnamedb() {
		return "h2:"+getString(MODE)+ (getString(MODE).equals("file")?":"+getString(SERVERNAME):"");
	}

	@Override
	protected String getdbSizeQuery() {
		return null;
	}


	@Override
	public String getDBLocation() {
		return getString(SERVERNAME);
	}

	@Override
	public Map<String,Long> getDBSize() {
		var map = new HashMap<String,Long>();


		if(getString(MODE).equals("mem"))
			map.put("mem", 0L);

		if(getFile(SERVERNAME).exists())
			map.put("file",FileTools.sizeOfDirectory(getFile(SERVERNAME)));


		return map;
	}

	@Override
	public String getName() {
		return "h2";
	}
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = super.getDefaultAttributes();

		m.get(SERVERNAME).setDefaultValue(Paths.get(MTGConstants.DATA_DIR.getAbsolutePath()).toFile().getAbsolutePath());
		m.get(LOGIN).setDefaultValue("SA");
		m.put(MODE, new MTGProperty("file", "select storage mode. File will persiste data on drive, mem will store data in memory", "file","mem"));
		m.put("WEB_ENABLE",MTGProperty.newBooleanProperty("true", "set to true to enable web console"));
		m.put("WEB_PORT",  MTGProperty.newIntegerProperty("8082","set web console port if enable",1024,65535));
		return m;
	}

}
