package org.magic.api.main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.dao.impl.MysqlDAO;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.providers.impl.Mtgjson4Provider;
import org.magic.services.MTGLogger;
import org.magic.tools.IDGenerator;

public class UpdateDBCards {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		MTGLogger.changeLevel("ERROR");
		MysqlDAO dao = new MysqlDAO();
		Mtgjson4Provider provider = new Mtgjson4Provider();
		provider.init();
		dao.init();
		JsonExport exp = new JsonExport();
		Connection con = dao.getCon();
		
		//NEED TO DO : alert and stock
	
		for(String ed : new String[] {"FBB"})
			updateCards(ed,con,provider,dao,exp);
		
	}

	private static void updateCards(String ed,Connection con, Mtgjson4Provider provider, MysqlDAO dao, JsonExport exp) throws SQLException {
		try (PreparedStatement pst = con.prepareStatement("SELECT * from cards where edition=? and cardprovider != ?",ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE)) {
			pst.setString(1, ed);
			pst.setString(2, provider.getName());
			ResultSet rs = pst.executeQuery();
			
			while(rs.next())
			{
				System.out.println("==============");
				MagicCard mc = exp.fromJson(rs.getString("mcard"),MagicCard.class);
				try {
					List<MagicCard> ret = provider.searchCardByCriteria("multiverseId", mc.getCurrentSet().getMultiverseid(), new MagicEdition(ed), true);
					if(ret.isEmpty())
					{
						System.out.println("NOT FOUND:" + mc + ":" + mc.getCurrentSet() + ":"+ mc.getCurrentSet().getNumber() +":"+mc.getCurrentSet().getMultiverseid() );
						
					}
					else if(ret.size()==1)
					{
						MagicCard founded = ret.get(0);
						System.out.println("    FOUND:" + founded + ":" + founded.getCurrentSet() + ":"+ founded.getCurrentSet().getNumber() +":"+founded.getCurrentSet().getMultiverseid() );
						rs.updateString("ID", IDGenerator.generate(founded));
						rs.updateString("cardprovider",provider.getName());
						rs.updateString("mcard",exp.toJsonElement(founded).toString());
						rs.updateString("edition",ed);
						rs.updateRow();
					}
					else if(ret.size()>1)
					{
						for(MagicCard founds : ret)
						{
							System.out.println("FOUND X:" + founds + ":"+mc.getCurrentSet().getNumber()+ ":" + founds.getCurrentSet() +":"+founds.getMultiverseid() + ":"+IDGenerator.generate(founds));
							
							rs.updateString("ID", IDGenerator.generate(founds));
							rs.updateString("cardprovider",provider.getName());
							rs.updateString("mcard",exp.toJsonElement(founds).toString());
							rs.updateRow();
						}
					}
					
					
					
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				
				
				
			}
		}
		
	}

	}
