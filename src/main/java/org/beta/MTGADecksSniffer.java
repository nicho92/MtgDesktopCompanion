package org.beta;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.tools.RequestBuilder;
import org.magic.tools.URLTools;
import org.magic.tools.RequestBuilder.METHOD;

import com.google.gson.JsonElement;

public class MTGADecksSniffer extends AbstractDeckSniffer {

	static String url="https://mtgadecks.net/serverSide?&draw=2"
													 + "&columns[0][data]=0"
													 + "&columns[0][name]=deckname"
													 + "&columns[0][searchable]=false"
													 + "&columns[0][orderable]=true"
													 + "&columns[0][orderable]="
													 + "&columns[0][search][regex]=false"
													 + "&columns[1][data]=1"
													 + "&columns[1][name]=colors"
													 + "&columns[1][searchable]=false"
													 + "&columns[1][orderable]=false"
													 + "&columns[1][search][value]="
													 + "&columns[1][search][regex]=false"
													 + "&columns[2][data]=2"
													 + "&columns[2][name]=archetype"
													 + "&columns[2][searchable]=false"
													 + "&columns[2][orderable]=false"
													 + "&columns[2][search][value]="
													 + "&columns[2][search][regex]=false"
													 + "&columns[3][data]=3"
													 + "&columns[3][name]=rares"
													 + "&columns%5B3%5D%5Bsearchable%5D=false"
													 + "&columns%5B3%5D%5Borderable%5D=true"
													 + "&columns%5B3%5D%5Bsearch%5D%5Bvalue%5D="
													 + "&columns%5B3%5D%5Bsearch%5D%5Bregex%5D=false"
													 + "&columns%5B4%5D%5Bdata%5D=4&columns%5B4%5D%5Bname%5D=epics"
													 + "&columns%5B4%5D%5Bsearchable%5D=false"
													 + "&columns%5B4%5D%5Borderable%5D=true"
													 + "&columns%5B4%5D%5Bsearch%5D%5Bvalue%5D="
													 + "&columns%5B4%5D%5Bsearch%5D%5Bregex%5D=false"
													 + "&columns%5B5%5D%5Bdata%5D=5"
													 + "&columns%5B5%5D%5Bname%5D=votes"
													 + "&columns%5B5%5D%5Bsearchable%5D=false"
													 + "&columns%5B5%5D%5Borderable%5D=true"
													 + "&columns%5B5%5D%5Bsearch%5D%5Bvalue%5D="
													 + "&columns%5B5%5D%5Bsearch%5D%5Bregex%5D=false"
													 + "&columns%5B6%5D%5Bdata%5D=6"
													 + "&columns%5B6%5D%5Bname%5D=need"
													 + "&columns%5B6%5D%5Bsearchable%5D=false"
													 + "&columns%5B6%5D%5Borderable%5D=false"
													 + "&columns%5B6%5D%5Bsearch%5D%5Bvalue%5D="
													 + "&columns%5B6%5D%5Bsearch%5D%5Bregex%5D=false"
													 + "&columns%5B7%5D%5Bdata%5D=7"
													 + "&columns%5B7%5D%5Bname%5D=real_update"
													 + "&columns%5B7%5D%5Bsearchable%5D=false"
													 + "&columns%5B7%5D%5Borderable%5D=true"
													 + "&columns%5B7%5D%5Bsearch%5D%5Bvalue%5D="
													 + "&columns%5B7%5D%5Bsearch%5D%5Bregex%5D=false"
													 + "&order%5B0%5D%5Bcolumn%5D=7&order%5B0%5D%5Bdir%5D=desc"
													 + "&start=0"
													 + "&length=100"
													 + "&search%5Bvalue%5D="
													 + "&search%5Bregex%5D=false"
													 + "&_token=tx65GvRoyo7PTpdnsIvnAtYr5trygtJY6qovUtWc"
													 + "&data=_token%3Dtx65GvRoyo7PTpdnsIvnAtYr5trygtJY6qovUtWc%26title%3D%26rares%3D%26archetype%3D0%26epics%3D"
													 + "&_=1559923966947";
	
	
	public static void main(String[] args) throws IOException {
		JsonElement e = RequestBuilder.build().setClient(URLTools.newClient()).method(METHOD.GET).url(url).addHeader("x-requested-with", "XMLHttpRequest").toJson();
		
		System.out.println(e);
	} 
	
	
	@Override
	public String[] listFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RetrievableDeck> getDeckList() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
