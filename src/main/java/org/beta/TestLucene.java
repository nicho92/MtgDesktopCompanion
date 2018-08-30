package org.beta;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.NumericDocValuesField;
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
import org.magic.services.MTGControler;



public class TestLucene {

	private IndexWriter indexWriter;
	private Directory dir;
	private Analyzer analyzer ;
	
	public static void main(String[] args) throws IOException, ParseException {
		TestLucene t = new TestLucene();
	
		if(t.open())
		{	
			MTGControler.getInstance().getEnabledCardsProviders().init();
			t.initIndex();
			
			t.similarity();
			
			t.close();
		}
	}
	
	public void similarity() throws IOException, ParseException
	{
		 Query query = new QueryParser("name", analyzer).parse("name:'Woolly Thoctar'");
			
		 IndexReader indexReader = DirectoryReader.open(dir);
		 IndexSearcher searcher = new IndexSearcher(indexReader);
		 
		 MoreLikeThis mlt = new MoreLikeThis(indexReader);
		 			  mlt.setFieldNames(new String[] {"text","color"});
		 			  mlt.setAnalyzer(analyzer);
		 			  mlt.setMinTermFreq(1);
		 			 
		 TopDocs top = searcher.search(query, 5);
		 ScoreDoc d = top.scoreDocs[0];
		 
		 System.out.println("Found similarity for : " +d.doc);
		 Query like = mlt.like(d.doc);
		
		 
		 TopDocs likes = searcher.search(like,50);
		 
		 
		 for(ScoreDoc l : likes.scoreDocs)
			{
			 Document doc = searcher.doc(l.doc);
			 System.out.println(l.score +"% : " + doc.get("name") + " " + doc.get("text") +" " + doc.get("color"));
			}
		 
		
		
	}
	
	public void initIndex() throws IOException {
		for(MagicCard mc : MTGControler.getInstance().getEnabledCardsProviders().searchCardByCriteria("name", "", null, false))
			addDocuments(mc);

		commit();
	}

	public long commit() throws IOException
	 {
		 return indexWriter.commit();
	 }
	
	
	 public boolean open(){
		    try 
	        {
		    	dir = FSDirectory.open(Paths.get("d:\\index\\"));
		    	analyzer = new StandardAnalyzer();
	            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
	            				  iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
	            				  
	            indexWriter = new IndexWriter(dir, iwc);
	            return true;
	        } catch (Exception e) {
				e.printStackTrace();
				return false;
			}
	    }
	 
	 public void close() throws IOException
	 {
		 indexWriter.close();
		 dir.close();
	 }
	 
	 
	 public void addDocuments(MagicCard mc) throws IOException{
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
           		   doc.add(new NumericDocValuesField("cmc",mc.getCmc()));
           		   
	      		   for(String color:mc.getColors())
	      		   {
	      			   doc.add(new Field("color", color, fieldType));
	      		   }
	      		   
          indexWriter.addDocument(doc);
 	 }
	    
	 
}
