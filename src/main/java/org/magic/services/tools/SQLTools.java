package org.magic.services.tools;

import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.conf.StatementType;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.magic.services.MTGConstants;
import org.magic.services.TransactionService;
import org.magic.services.logging.MTGLogger;


public class SQLTools {
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	
	private DSLContext ctx;
	
	
	public SQLTools(SQLDialect dialect) {
		System.setProperty("org.jooq.no-tips", "true");
		System.setProperty("org.jooq.no-logo", "true");
		
		var settings = new Settings()
				.withStatementType(StatementType.STATIC_STATEMENT)
			    .withRenderQuotedNames(RenderQuotedNames.EXPLICIT_DEFAULT_UNQUOTED) // Defaults to EXPLICIT_DEFAULT_QUOTED
			    .withRenderNameCase(RenderNameCase.LOWER_IF_UNQUOTED); 
		
		ctx = DSL.using(dialect,settings);
		
	}
		
	public String selectAll(String tableName)
	{
		return ctx.select(DSL.asterisk())
					  .from(DSL.table(tableName))
					  .getSQL();
	}
	
	public String insertDefaultCollections() {
		var d = ctx.insertInto(DSL.table("collections"),DSL.field("name"));
		
		for(String s : MTGConstants.getDefaultCollectionsNames())
			d.values(s);
		
		return d.getSQL();
		
		
	}
	
	
	
public String insertMainContact() {
		return ctx.insertInto(DSL.table("contacts"),DSL.field("contact_id"),DSL.field("contact_name"),DSL.field("contact_lastname"),DSL.field("contact_telephone"),DSL.field("contact_country"),DSL.field("contact_zipcode"),DSL.field("contact_city"),DSL.field("contact_address"),DSL.field("contact_website"),DSL.field("contact_email"),DSL.field("emailAccept"),DSL.field("contact_active"))
					.values(1,"MTG","Companion","123456789","FR","123456","Somewhere","In the middle of nowhere","https://www.mtgcompanion.org","mtgdesktopcompanion@gmail.com",true,true).getSQL();
	}
		
	
	
	public String createTableTechnicalAudit() { 
		return	ctx.createTableIfNotExists("technicalauditlog")
				.column("classname",SQLDataType.VARCHAR(30))
				.column("techObject",SQLDataType.JSON)
				.column("start",SQLDataType.TIMESTAMP)
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
				.column("name", SQLDataType.VARCHAR(30))
				.column("mcard",SQLDataType.JSON)
				.column("collection",SQLDataType.VARCHAR(30))
				.column("comments",SQLDataType.LONGVARCHAR)
				.column("conditions",SQLDataType.VARCHAR(30))
				.column("foil",SQLDataType.BOOLEAN)
				.column("signedcard",SQLDataType.BOOLEAN)
				.column("altered",SQLDataType.BOOLEAN)
				.column("etched",SQLDataType.BOOLEAN)
				.column("langage",SQLDataType.VARCHAR(20))
				.column("qte",SQLDataType.INTEGER)
				.column("price",SQLDataType.DECIMAL(10,2))
				.column("grading",SQLDataType.JSON)
				.column("tiersAppIds",SQLDataType.JSON)
				.primaryKey("idstock")
				.getSQL();
	}
	
	public String createTableCollections() { 
		return	ctx.createTableIfNotExists("collections")
				.column("name", SQLDataType.VARCHAR(30))
				.primaryKey("name")
				.getSQL();
	}
		
	public String createTableCards() { 
			return ctx.createTableIfNotExists("cards")
				.column("ID", SQLDataType.VARCHAR(50))
				.column("mcard", SQLDataType.JSON)
				.column("edition", SQLDataType.VARCHAR(5))
				.column("cardprovider",SQLDataType.VARCHAR(20))
				.column("collection",SQLDataType.VARCHAR(30))
				.column("dateUpdate",SQLDataType.TIMESTAMP)
				.primaryKey("ID","edition","collection")
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
				.column("temporaryToken",SQLDataType.VARCHAR(TransactionService.TOKENSIZE))
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
