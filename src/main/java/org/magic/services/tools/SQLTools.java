package org.magic.services.tools;

import static org.jooq.impl.DSL.asterisk;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.using;

import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.conf.StatementType;
import org.jooq.impl.SQLDataType;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;


public class SQLTools {
	private Logger logger = MTGLogger.getLogger(this.getClass());
	
	private DSLContext ctx;
	
	
	public SQLTools(SQLDialect dialect) {
		
		logger.debug("Init sql tools with dialect {}",dialect);
		
		System.setProperty("org.jooq.no-tips", "true");
		System.setProperty("org.jooq.no-logo", "true");
		
		var settings = new Settings()
				.withStatementType(StatementType.STATIC_STATEMENT)
			    .withRenderQuotedNames(RenderQuotedNames.EXPLICIT_DEFAULT_UNQUOTED) // Defaults to EXPLICIT_DEFAULT_QUOTED
			    .withRenderNameCase(RenderNameCase.LOWER_IF_UNQUOTED); 
		
		ctx = using(dialect,settings);
		
	}
		
	public String selectAll(String tableName)
	{
		return ctx.select(asterisk())
					  .from(table(tableName))
					  .getSQL();
	}
	
	public String insertDefaultCollections() {
		var d = ctx.insertInto(table("collections"),field("name"));
		for(String s : MTGConstants.DEFAULT_COLLECTIONS_NAMES)
			d.values(s);
		
		return d.getSQL();
	}
	
	public String createIndex(String table, String column)
	{
		return ctx.createIndex("idx_"+table+"_"+column).on(table, column).getSQL();
	}
	
	public String insertMainContact() {
		
		var c = MTGConstants.DEFAULT_CONTACT;
		
		return ctx.insertInto(table("contacts"),field("contact_id"),field("contact_name"),field("contact_lastname"),field("contact_telephone"),field("contact_country"),field("contact_zipcode"),field("contact_city"),field("contact_address"),field("contact_website"),field("contact_email"),field("emailAccept"),field("contact_active"))
					.values(c.getId(),c.getName(),c.getLastName(),c.getTelephone(),c.getCountry(),c.getZipCode(),c.getCity(),c.getAddress(),c.getWebsite(),c.getEmail(),true,true)
					.getSQL();
	}
		
	
	
	public String createTableTechnicalAudit() { 
		return ctx.createTableIfNotExists("technicalauditlog")
				.column("classname",SQLDataType.VARCHAR(30))
				.column("techObject",SQLDataType.JSON)
				.column("startTime",SQLDataType.TIMESTAMP)
				.getSQL();
	}
		
	public String createTableDecks() { 
		return	ctx.createTableIfNotExists("decks")
				.column("id",SQLDataType.INTEGER.identity(true))
				.column("description",SQLDataType.LONGVARCHAR)
				.column("name",SQLDataType.VARCHAR(250))				
				.column("dateCreation",SQLDataType.DATE)
				.column("dateUpdate",SQLDataType.DATE)
				.column("tags",SQLDataType.VARCHAR(250))
				.column("commander",SQLDataType.JSON)
				.column("main",SQLDataType.JSON)
				.column("sideboard",SQLDataType.JSON)
				.column("averagePrice",SQLDataType.DECIMAL(10,2))		
				.primaryKey("id")
				.getSQL();
	}
	
	public String createTableSealed() {  
			return ctx.createTableIfNotExists("sealed")
				.column("id",SQLDataType.INTEGER.identity(true))
				.column("edition",SQLDataType.VARCHAR(5))
				.column("qte",SQLDataType.INTEGER)
				.column("comment",SQLDataType.LONGVARCHAR)
				.column("lang",SQLDataType.VARCHAR(50))
				.column("typeProduct",SQLDataType.VARCHAR(25))
				.column("conditionProduct",SQLDataType.VARCHAR(25))
				.column("statut",SQLDataType.VARCHAR(10))
				.column("extra",SQLDataType.VARCHAR(10))
				.column("collection",SQLDataType.VARCHAR(30))
				.column("price",SQLDataType.DECIMAL(10,2))
				.column("tiersAppIds",SQLDataType.JSON)
				.column("numversion",SQLDataType.INTEGER)			
				.primaryKey("id")
				.getSQL();
	}
	
	public String createTableNews() { 
		return ctx.createTableIfNotExists("news")
					.column("id",SQLDataType.INTEGER.identity(true))
					.column("name",SQLDataType.VARCHAR(100))
					.column("url",SQLDataType.VARCHAR(255))
					.column("categorie",SQLDataType.VARCHAR(50))
					.column("typeNews",SQLDataType.VARCHAR(50))
					.primaryKey("id")
					.getSQL();
	}
	
	
	public String createTableAlerts() { 
		return 	ctx.createTableIfNotExists("alerts")
				.column("id",SQLDataType.VARCHAR(50))
				.column("mcard",SQLDataType.JSON)
				.column("amount",SQLDataType.DECIMAL(10,2))
				.column("foil",SQLDataType.BOOLEAN)
				.column("qte",SQLDataType.INTEGER)
				.primaryKey("id")
				.getSQL();
	}
	

	public String createTableStocks() { 
			return ctx.createTableIfNotExists("stocks")
				.column("idstock",SQLDataType.INTEGER.identity(true))
				.column("idmc",SQLDataType.VARCHAR(50))
				.column("idMe",SQLDataType.VARCHAR(5))
				.column("name", SQLDataType.VARCHAR(150))
				.column("mcard",SQLDataType.JSON)
				.column("collection",SQLDataType.VARCHAR(30))
				.column("comments",SQLDataType.LONGVARCHAR)
				.column("conditions",SQLDataType.VARCHAR(30))
				.column("foil",SQLDataType.BOOLEAN)
				.column("signedcard",SQLDataType.BOOLEAN)
				.column("altered",SQLDataType.BOOLEAN)
				.column("etched",SQLDataType.BOOLEAN)
				.column("digital",SQLDataType.BOOLEAN)
				.column("langage",SQLDataType.VARCHAR(20))
				.column("qte",SQLDataType.INTEGER)
				.column("price",SQLDataType.DECIMAL(10,2))
				.column("grading",SQLDataType.JSON)
				.column("tiersAppIds",SQLDataType.JSON)
				.column("dateUpdate",SQLDataType.TIMESTAMP)
				.primaryKey("idstock")
				.getSQL();
	}
	
	public String createTableCollections() { 
		return ctx.createTableIfNotExists("collections")
				.column("name", SQLDataType.VARCHAR(30))
				.primaryKey("name")
				.getSQL();
	}
		
	public String createTableContacts() { 
			return ctx.createTableIfNotExists("contacts")
				.column("contact_id", SQLDataType.INTEGER.identity(true))
				.column("contact_name", SQLDataType.VARCHAR(250))
				.column("contact_lastname",SQLDataType.VARCHAR(250))
				.column("contact_password",SQLDataType.VARCHAR(250))
				.column("contact_telephone",SQLDataType.VARCHAR(250))
				.column("contact_country",SQLDataType.VARCHAR(250))
				.column("contact_zipcode",SQLDataType.VARCHAR(10))
				.column("contact_city",SQLDataType.VARCHAR(50))
				.column("contact_address",SQLDataType.VARCHAR(250))
				.column("contact_website",SQLDataType.VARCHAR(250))
				.column("contact_email",SQLDataType.VARCHAR(100))
				.column("emailAccept",SQLDataType.BOOLEAN)
				.column("contact_active",SQLDataType.BOOLEAN)
				.column("temporaryToken",SQLDataType.VARCHAR(50))
				.primaryKey("contact_id")
				.unique("contact_email").getSQL();
	}
	
	
	public String createTableTransactions() { 
			return ctx.createTableIfNotExists("transactions")
				.column("id", SQLDataType.INTEGER.identity(true))
				.column("dateTransaction", SQLDataType.TIMESTAMP)
				.column("message", SQLDataType.VARCHAR(250))
				.column("stocksItem", SQLDataType.JSON)
				.column("statut", SQLDataType.VARCHAR(15))
				.column("transporter",SQLDataType.VARCHAR(50))
				.column("shippingPrice",SQLDataType.DECIMAL(10,3))
				.column("transporterShippingCode",SQLDataType.VARCHAR(50))
				.column("currency",SQLDataType.VARCHAR(5))
				.column("datePayment", SQLDataType.TIMESTAMP)
				.column("dateSend", SQLDataType.TIMESTAMP)
				.column("paymentProvider",SQLDataType.VARCHAR(50))
				.column("fk_idcontact",SQLDataType.INTEGER)
				.column("sourceShopId",SQLDataType.VARCHAR(250))
				.column("sourceShopName",SQLDataType.VARCHAR(250))
				.column("typeTransaction",SQLDataType.VARCHAR(15))
				.column("reduction",SQLDataType.DECIMAL(10,2))
				.primaryKey("id").getSQL();
	}
	
	
	public String createTableGed() { 
			return ctx.createTableIfNotExists("ged")
				.column("id", SQLDataType.INTEGER.identity(true))
				.column("creationDate", SQLDataType.TIMESTAMP)
				.column("className", SQLDataType.VARCHAR(250))
				.column("idInstance", SQLDataType.VARCHAR(250))
				.column("fileName", SQLDataType.VARCHAR(250))
				.column("fileContent",SQLDataType.LONGVARCHAR)
				.column("md5",SQLDataType.VARCHAR(35))
				.primaryKey("id").getSQL();
	}
	
	public String createTableAnnounces() { 
			return ctx.createTableIfNotExists("announces")
				.column("id", SQLDataType.INTEGER.identity(true))
				.column("creationDate", SQLDataType.TIMESTAMP)
				.column("startDate", SQLDataType.TIMESTAMP)
				.column("endDate", SQLDataType.TIMESTAMP)
				.column("title", SQLDataType.VARCHAR(150))
				.column("description",SQLDataType.LONGVARCHAR)
				.column("total",SQLDataType.DECIMAL(10,2))
				.column("currency", SQLDataType.VARCHAR(5))
				.column("stocksItem", SQLDataType.JSON)
				.column("typeAnnounce", SQLDataType.VARCHAR(10))
				.column("category", SQLDataType.VARCHAR(50))
				.column("percentReduction", SQLDataType.DECIMAL(10,2))
				.column("conditions", SQLDataType.VARCHAR(50))
				.column("statusAnnounce", SQLDataType.VARCHAR(25))
				.column("fk_idcontact", SQLDataType.INTEGER)
				.primaryKey("id").getSQL();
	}


}
