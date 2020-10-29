package org.magic.api.indexer.impl;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.enums.MTGColor;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardsIndexer;
import org.magic.services.MTGConstants;


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
	public void initDefault() {
		setProperty(BOOST, "false");
		setProperty(MIN_TERM_FREQ, "1");
		setProperty(FIELDS,"cost,text,color,type,cmc");
		setProperty(MAX_RESULTS,"20");
		setProperty(DIRECTORY,Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(), "luceneIndex").toFile().getAbsolutePath());
	}
	
	public LuceneIndexer() {
		super();
		serializer=new JsonExport();
		analyzer = new StandardAnalyzer();
		
		if(!getFile(DIRECTORY).exists())
			logger.warn("Index is not initiated at "+getFile(DIRECTORY)+", please launch it from config panel");
	}
	
	public String[] listFields()
	{
		if(dir==null)
			open();
		
		try (IndexReader indexReader = DirectoryReader.open(dir))
		{
			Collection<String> fields = FieldInfos.getIndexedFields(indexReader);
			return fields.toArray(new String[fields.size()]);
		} catch (IOException e) {
			return new String[0];
		}
	}
	
	public List<MagicCard> listCards()
	{
		return search("*:*");
	}
	
	@Override
	public List<String> suggestCardName(String q)
	{
		
		StringBuilder query = new StringBuilder("");
		String[] split = q.split(" ");
		for(int i=0;i<split.length;i++)
		{
			query.append("name:").append(split[i]);
			
			if(i<split.length-1)
				query.append(" AND ");
			else
				query.append("*");
		}
		
		return search(query.toString().trim()).stream().map(MagicCard::getName).distinct().collect(Collectors.toList());
	}
	
	@Override
	public List<MagicCard> search(String q)
	{
		if(dir==null)
			open();
		
		List<MagicCard> ret = new ArrayList<>();
		
		try (IndexReader indexReader = DirectoryReader.open(dir))
		{
			 IndexSearcher searcher = new IndexSearcher(indexReader);
			 Query query = new QueryParser("name", analyzer).parse(q);
			 logger.trace(query);
			 
			 TotalHitCountCollector collector = new TotalHitCountCollector();
			 searcher.search(query,collector);
			 
			 TopDocs top= searcher.search(query, Math.max(1, collector.getTotalHits()));
			 
			 for(int i =0;i<top.totalHits.value;i++)
				 ret.add(serializer.fromJson(searcher.doc(top.scoreDocs[i].doc).get("data"),MagicCard.class));
			 
			 
		} catch (Exception e) {
			logger.error(e);
		}
		
		return ret;
	}
	
	@Override
	public String getVersion() {
		return Version.LATEST.toString();
	}
	
	public Map<String,Long> terms(String field)
	{
		if(dir==null)
			open();
		
		 Map<String,Long> map= new LinkedHashMap<>();
		
		 logger.debug("looking terms for "+ field);
		 
		 try {
			 IndexReader reader = DirectoryReader.open(dir);
			 		Terms terms = MultiTerms.getTerms(reader, field);
		            TermsEnum it = terms.iterator();
		            BytesRef term = it.next();
		            while (term != null) {
		               map.put(term.utf8ToString(), it.totalTermFreq());
		               term = it.next();
		            }
		            logger.debug("looking terms for "+ field +" " + map.size());
		} catch (Exception e) {
			logger.error("error ",e);
		}
		return map;
	}
	
	
	public Map<MagicCard,Float> similarity(MagicCard mc) throws IOException 
	{
		Map<MagicCard,Float> ret = new LinkedHashMap<>();
		
		if(mc==null)
			return ret;
		
		if(dir==null)
			open();
		
		logger.debug("search similar cards for " + mc);
		
		try (IndexReader indexReader = DirectoryReader.open(dir))
		{
			
		 IndexSearcher searcher = new IndexSearcher(indexReader);
		 Query query = new QueryParser("text", analyzer).parse("name:\""+mc.getName()+"\"");
		 logger.trace(query);
		 TopDocs top = searcher.search(query, 1);
		 
		 if(top.totalHits.value>0)
		 {
			 MoreLikeThis mlt = new MoreLikeThis(indexReader);
			  mlt.setFieldNames(getArray(FIELDS));
			  mlt.setAnalyzer(analyzer);
			  mlt.setMinTermFreq(getInt(MIN_TERM_FREQ));
			  mlt.setBoost(getBoolean(BOOST));
			  
			  
			  
			 ScoreDoc d = top.scoreDocs[0];
			 logger.trace("found doc id="+d.doc);
			 Query like = mlt.like(d.doc);
			 
			 logger.trace("mlt="+Arrays.asList(mlt.retrieveInterestingTerms(d.doc)));
			 logger.trace("Like query="+like);
			 TopDocs likes = searcher.search(like,getInt(MAX_RESULTS));
			 
			 for(ScoreDoc l : likes.scoreDocs)
				 ret.put(serializer.fromJson(searcher.doc(l.doc).get("data"),MagicCard.class),l.score);

			 logger.debug("found " + likes.scoreDocs.length + " results");
			 close();
			
		 }
		 else
		 {
			 logger.error("can't found "+mc);
		 }
		 
		} catch (ParseException e) {
			logger.error(e);
		}
		return ret;
		
	}

	
	public void initIndex() throws IOException {
		
		if(dir==null)
			open();
		
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		  				   iwc.setOpenMode(OpenMode.CREATE);
	    IndexWriter indexWriter = new IndexWriter(dir, iwc);
	    
		for(MagicCard mc : getEnabledPlugin(MTGCardsProvider.class).listAllCards())
		{
			try {
				indexWriter.addDocument(toDocuments(mc));
			} catch (IllegalArgumentException e) {
				logger.error("Error indexing " + mc + " " + mc.getCurrentSet(),e);
			}
		}
		
		indexWriter.commit();
		indexWriter.close();
	}
	
	public boolean open(){
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
	 
	public void close() throws IOException
	{
		dir.close();
		dir=null;
	}
	
	private Document toDocuments(MagicCard mc) {
          Document doc = new Document();
          			
          		FieldType fieldType = new FieldType();
		          		fieldType.setStored(true);
		          		fieldType.setStoreTermVectors(true);
		          		fieldType.setTokenized(true);
		          		fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
		          		
           		   doc.add(new Field("name", mc.getName(), fieldType));
           		   
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
           		  doc.add(new Field("set",mc.getCurrentSet().getId(),fieldType));
     		      doc.add(new StringField("data",serializer.toJson(mc),Field.Store.YES));
           		   
        		   
        		   
            	   for(MTGColor color:mc.getColors())
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
		return FileUtils.sizeOfDirectory(getFile(DIRECTORY));
	}

	@Override
	public Date getIndexDate() {
		if(getFile(DIRECTORY).exists())
			return new Date(getFile(DIRECTORY).lastModified());
		else
			return null;
	}


	 
}
