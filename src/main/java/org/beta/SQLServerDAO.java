package org.beta;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.abstracts.AbstractSQLMagicDAO;
import org.postgresql.util.PGobject;

public class SQLServerDAO extends AbstractSQLMagicDAO {

	@Override
	public void backup(File dir) throws SQLException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return "SQLServer";
	}

	@Override
	protected String getAutoIncrementKeyWord() {
		return "IDENTITY";
	}

	@Override
	protected String getjdbcnamedb() {
		return "sqlserver";
	}

	@Override
	protected String cardStorage() {
		return "text";
	}

	@Override
	protected void storeCard(PreparedStatement pst, int position, MagicCard mc) throws SQLException {
		pst.setString(position, serialiser.toJsonElement(mc).toString());

	}

	@Override
	protected MagicCard readCard(ResultSet rs) throws SQLException {
		return serialiser.fromJson(((PGobject)rs.getObject("mcard")).getValue(), MagicCard.class);
	}

	@Override
	protected String createListStockSQL(MagicCard mc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getdbSizeQuery() {
		return "";
	}

}
