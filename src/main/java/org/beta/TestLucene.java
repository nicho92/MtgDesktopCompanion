package org.beta;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.magic.api.beans.MagicCard;
import org.magic.services.MTGControler;



public class TestLucene {

	private IndexWriter indexWriter;
	private Directory dir;
	
	
	public static void main(String[] args) throws IOException {
		TestLucene t = new TestLucene();
	
		if(t.open())
		{	
			//MTGControler.getInstance().getEnabledCardsProviders().init();
			//t.initIndex();
			
			t.search("name", "Counterspell");
			
			t.close();
		}
	}
	
	private void search(String fld,String value) throws IOException {
		 Query query = new TermQuery(new Term(fld,value));
			
		 IndexReader indexReader = DirectoryReader.open(dir);
		 IndexSearcher searcher = new IndexSearcher(indexReader);
		 TopDocs top = searcher.search(query, 10);
		 for(ScoreDoc d : top.scoreDocs)
		 {
			 System.out.println(searcher.doc(d.doc));
		 }
		 
		
	}

	private void initIndex() throws IOException {
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
		    	Analyzer analyzer = new StandardAnalyzer();
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
           		   doc.add(new StringField("name", mc.getName(), Field.Store.YES));
           		   doc.add(new StringField("cost", mc.getCost(), Field.Store.YES));
           		   doc.add(new StringField("text", mc.getText(), Field.Store.YES));
           		   doc.add(new StringField("type", mc.getFullType(), Field.Store.YES));
           		   doc.add(new StringField("flavor", mc.getFlavor(), Field.Store.YES));
	      		   doc.add(new IntPoint("cmc", mc.getCmc()));
          indexWriter.addDocument(doc);
 	 }
	    
	 
}
