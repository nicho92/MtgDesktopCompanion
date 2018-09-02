package org.magic.api.indexer.impl;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.magic.api.beans.MagicCard;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.abstracts.AbstractCardsIndexer;
import org.magic.services.MTGControler;



public class LuceneIndexer extends AbstractCardsIndexer {

	private Directory dir;
	private Analyzer analyzer ;
	private JsonExport serializer;
	
	@Override
	public void initDefault() {
		setProperty("boost", "false");
		setProperty("minTermFreq", "1");
		setProperty("fields","cost,text,color,type,cmc");
		setProperty("maxResults","20");
	}
	
	public LuceneIndexer() {
		super();
		serializer=new JsonExport();
		analyzer = new StandardAnalyzer();
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
		 
		 if(top.totalHits>0)
		 {
			 MoreLikeThis mlt = new MoreLikeThis(indexReader);
			  mlt.setFieldNames(getArray("fields"));
			  mlt.setAnalyzer(analyzer);
			  mlt.setMinTermFreq(getInt("minTermFreq"));
			  mlt.setBoost(getBoolean("boost"));
			  
			  
			  
			 ScoreDoc d = top.scoreDocs[0];
			 logger.trace("found doc id="+d.doc);
			 Query like = mlt.like(d.doc);
			 
			 logger.trace("mlt="+Arrays.asList(mlt.retrieveInterestingTerms(d.doc)));
			 logger.trace("Like query="+like);
			 TopDocs likes = searcher.search(like,getInt("maxResults"));
			 
			 for(ScoreDoc l : likes.scoreDocs)
				 ret.put(serializer.fromJson(MagicCard.class, searcher.doc(l.doc).get("data")),l.score);

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
	    
		for(MagicCard mc : MTGControler.getInstance().getEnabledCardsProviders().searchCardByName("", null, false))
		{
			indexWriter.addDocument(toDocuments(mc));
		}
		
		indexWriter.commit();
		indexWriter.close();
	}
	
	public boolean open(){
	    try 
        {
	    	dir = FSDirectory.open(Paths.get(confdir.getAbsolutePath(),"luceneIndex"));
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
           		   doc.add(new Field("cost", mc.getCost(),fieldType));
           		   doc.add(new Field("text", mc.getText(), fieldType));
           		   doc.add(new Field("type", mc.getFullType(), fieldType));
           		   doc.add(new Field("set",mc.getCurrentSet().getId(),fieldType));
           		   doc.add(new StringField("data",serializer.toJson(mc),Field.Store.YES));
           		   doc.add(new StoredField("cmc",mc.getCmc()));
           		   
	      		   for(String color:mc.getColors())
	      		   {
	      			   doc.add(new Field("color", color, fieldType));
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

	 
}
