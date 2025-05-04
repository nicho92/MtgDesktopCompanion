package org.magic.api.indexer.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardsIndexer;
import org.magic.services.MTGConstants;
import org.magic.services.tools.FileTools;


public class LuceneIndexer extends AbstractCardsIndexer {

	private static final String DIRECTORY = "DIRECTORY";
	private static final String BOOST = "boost";
	private static final String MIN_TERM_FREQ = "minTermFreq";
	private static final String MAX_RESULTS = "maxResults";
	private static final String FIELDS = "fields";
	private Directory dir;
	private Analyzer analyzer ;
	private JsonExport serializer;

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		
			var m = super.getDefaultAttributes();
			m.put(BOOST, MTGProperty.newBooleanProperty("false", "enable Document level field boosting"));
			m.put(MIN_TERM_FREQ, MTGProperty.newIntegerProperty("1", "Sets the frequency below which terms will be ignored in the source doc", 1, -1));
			m.put(DIRECTORY, MTGProperty.newDirectoryProperty(Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(), "luceneIndex")));
			m.put(MAX_RESULTS, MTGProperty.newIntegerProperty("20", "set max return result of similar query", 1, -1));
			
			
			try {
				m.put(FIELDS, new MTGProperty("cost,text,scryfallId,numbercolor,type,cmc,rarity,extraLayout,rotatedCardName,borderless,showcase,extend,timeshifted,retro","indexed fields for cards. Separated by comma", BeanUtils.describe(new MTGCard()).keySet().stream().toArray(value -> new String[value])));
			} catch (Exception _) {
				
				m.put(FIELDS, new MTGProperty("cost,text,scryfallId,numbercolor,type,cmc,rarity,extraLayout,rotatedCardName,borderless,showcase,extend,timeshifted,retro","indexed fields for cards. Separated by comma"));
			}
			
			
			return m;
	}

	public LuceneIndexer() {
		super();
		serializer=new JsonExport();
		analyzer = new StandardAnalyzer();

		if(!getFile(DIRECTORY).exists())
			logger.warn("Index is not initiated at {}, please launch it from config panel",getFile(DIRECTORY));
	}

	@Override
	public String[] listFields()
	{
		if(dir==null)
			open();

		try (var indexReader = DirectoryReader.open(dir))
		{
			var fields = FieldInfos.getIndexedFields(indexReader);
			return fields.toArray(new String[fields.size()]);
		} catch (IOException _) {
			return new String[0];
		}
	}

	@Override
	public List<MTGCard> listCards()
	{
		return search("*:*");
	}

	@Override
	public List<String> suggestCardName(String q)
	{

		var query = new StringBuilder("");
		String[] split = q.split(" ");
		for(var i=0;i<split.length;i++)
		{
			query.append("name:").append(split[i]);

			if(i<split.length-1)
				query.append(" AND ");
			else
				query.append("*");
		}

		return search(query.toString().trim()).stream().map(MTGCard::getFullName).distinct().toList();
	}

	@Override
	public List<MTGCard> search(String q)
	{
		if(dir==null)
			open();

		var ret = new ArrayList<MTGCard>();

		try (var indexReader = DirectoryReader.open(dir))
		{
			var searcher = new IndexSearcher(indexReader);
			var query = new QueryParser("name", analyzer).parse(q);
			 logger.trace(query);

			 var count = searcher.count(query);
			 var top= searcher.search(query, Math.max(1, count));

			 logger.debug("found {} items for {}",top.totalHits,q);

			 for(var i =0;i<top.totalHits.value();i++)
				 ret.add(serializer.fromJson(searcher.storedFields().document(top.scoreDocs[i].doc).get("data"),MTGCard.class));


		} catch (Exception e) {
			logger.trace(e);
		}

		return ret;
	}

	@Override
	public String getVersion() {
		return Version.LATEST.toString();
	}

	@Override
	public Map<String,Long> terms(String field)
	{
		if(dir==null)
			open();

		 Map<String,Long> map= new LinkedHashMap<>();

		 logger.debug("looking terms for {}",field);

		 try {
			 var reader = DirectoryReader.open(dir);
			 		var terms = MultiTerms.getTerms(reader, field);
			 		var it = terms.iterator();
			 		var term = it.next();
		            while (term != null) {
		               map.put(term.utf8ToString(), it.totalTermFreq());
		               term = it.next();
		            }
		            logger.debug("looking terms for {} : {} ",field,map.size());
		} catch (Exception e) {
			logger.error("error ",e);
		}
		return map;
	}

	@Override
	public Map<MTGCard,Float> similarity(MTGCard mc) throws IOException
	{
		var ret = new LinkedHashMap<MTGCard,Float>();

		if(mc==null)
			return ret;

		if(dir==null)
			open();

		logger.debug("search similar cards for {}",mc);

		try (var indexReader = DirectoryReader.open(dir))
		{

		 var searcher = new IndexSearcher(indexReader);
		 var query = new QueryParser("text", analyzer).parse("name:\""+mc.getName()+"\"");
		 logger.trace(query);
		 var top = searcher.search(query, 1);

		 if(top.totalHits.value()>0)
		 {
			 var mlt = new MoreLikeThis(indexReader);
			  mlt.setFieldNames(getArray(FIELDS));
			  mlt.setAnalyzer(analyzer);
			  mlt.setMinTermFreq(getInt(MIN_TERM_FREQ));
			  mlt.setBoost(getBoolean(BOOST));



			 var scoreDoc = top.scoreDocs[0];
			 var like = mlt.like(scoreDoc.doc);
			 logger.trace("Like query={}",like);
			 var likes = searcher.search(like,getInt(MAX_RESULTS));

			 for(ScoreDoc l : likes.scoreDocs)
				 ret.put(serializer.fromJson(searcher.storedFields().document(l.doc).get("data"),MTGCard.class),l.score);

			 logger.debug("found {} results",likes.scoreDocs.length);
			 close();

		 }
		 else
		 {
			 logger.error("can't found {}",mc);
		 }

		} catch (ParseException e) {
			logger.error(e);
		}
		return ret;

	}


	@Override
	public void initIndex(boolean force) throws IOException {


		if(getFile(DIRECTORY).exists() && !force)
		{
			logger.info("Index already loaded");
			return;
		}

		logger.info("Loading cards indexing.... may takes few minutes...");

		if(dir==null)
			open();

		var iwc = new IndexWriterConfig(analyzer);
		  	iwc.setOpenMode(OpenMode.CREATE);
		var indexWriter = new IndexWriter(dir, iwc);

		for(MTGCard mc : getEnabledPlugin(MTGCardsProvider.class).listAllCards())
		{
			try {
				indexWriter.addDocument(toDocuments(mc));
			} catch (IllegalArgumentException e) {
				logger.error("Error indexing {} {}",mc,mc.getEdition(),e);
			}
		}

		indexWriter.commit();
		indexWriter.close();
	}


	private boolean open(){
	    try
        {
	    	dir = FSDirectory.open(getFile(DIRECTORY).toPath());
            return true;
        }
	    catch (Exception e) {
        	logger.error(e);
			return false;
		}
    }

	private void close() throws IOException
	{
		dir.close();
		dir=null;
	}

	private Document toDocuments(MTGCard mc) {
			var doc = new Document();
          	var fieldType = new FieldType();
		          		fieldType.setStored(true);
		          		fieldType.setStoreTermVectors(true);
		          		fieldType.setTokenized(true);
		          		fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);

		           doc.add(new Field("scryfallId",String.valueOf(mc.getScryfallId()),fieldType));
		           doc.add(new Field("name", mc.getName(), fieldType));
		           doc.add(new Field("number", mc.getNumber(), fieldType));
		           
		           
           		   if(mc.getCost()!=null)
           			   doc.add(new Field("cost", mc.getCost(),fieldType));
           		   else
           			   doc.add(new Field("cost", "",fieldType));

           		   if(mc.getText()!=null)
           			   doc.add(new Field("text", mc.getText(), fieldType));
           		   else
           			   doc.add(new Field("text", "", fieldType));


           		   if(mc.getCmc()!=null)
           			   doc.add(new StoredField("cmc",mc.getCmc()));

           		  doc.add(new Field("type", mc.getFullType(), fieldType));
           		  doc.add(new Field("set",mc.getEdition().getId(),fieldType));
     		      doc.add(new TextField("data",serializer.toJson(mc),Field.Store.YES));
     		      doc.add(new Field("rarity",mc.getRarity().toPrettyString(),fieldType));
	     		  doc.add(new Field("rotatedCardName", mc.getRotatedCard()!=null?mc.getRotatedCard().getName():"",fieldType));
	     		  doc.add(new Field("borderless",String.valueOf(mc.isBorderLess()),fieldType));
	     		  doc.add(new Field("showcase",String.valueOf(mc.isShowCase()),fieldType));
	     		  doc.add(new Field("extend",String.valueOf(mc.isExtendedArt()),fieldType));
	     		  doc.add(new Field("timeshifted",String.valueOf(mc.isTimeshifted()),fieldType));
	     		  doc.add(new Field("retro",String.valueOf(mc.isRetro()),fieldType));

			 	if(!mc.getExtra().isEmpty()) 
				{	
					for(var l:mc.getExtra())
	            	   		{
	            		   		doc.add(new Field("extraLayout", l.toString(), fieldType));
	            	   		}
				}
				else
				{
					 doc.add(new Field("extraLayout", "", fieldType));
				}
					


            	   for(EnumColors color:mc.getColors().stream().filter(c->c != null).toList())
            	   {
            		   doc.add(new Field("color", color.getCode(), fieldType));
            	   }



         return doc;
 	 }

	@Override
	public String getName() {
		return "Lucene";
	}

	@Override
	public PLUGINS getType() {
		return PLUGINS.INDEXER;
	}

	@Override
	public long size() {
		return FileTools.sizeOfDirectory(getFile(DIRECTORY));
	}

	@Override
	public Date getIndexDate() {
		if(getFile(DIRECTORY).exists())
			return new Date(getFile(DIRECTORY).lastModified());
		else
			return null;
	}



}
