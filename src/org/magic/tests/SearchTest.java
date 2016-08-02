package org.magic.tests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import org.magic.api.dao.impl.MysqlDAO;
import org.magic.api.interfaces.MagicDAO;

public class SearchTest {

	
	public static void main(String[] args) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, SQLException {
		
		MagicDAO dao = new MysqlDAO();
		dao.backup(new File("c:/dump.sql"));
	}
	
	
}
