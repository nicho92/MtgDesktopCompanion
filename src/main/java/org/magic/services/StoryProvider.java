package org.magic.services;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.magic.api.beans.MTGStory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class StoryProvider {

	private Logger logger = MTGLogger.getLogger(this.getClass());
	private JsonParser parser;
	private Locale local;
	private int offset=0;
	private String baseURI="https://magic.wizards.com";
	//&fromDate=&toDate=&word=
	
	
	public StoryProvider(Locale local) {
		parser = new JsonParser();
		this.local=local;
	}
	

	private URLConnection getConnection(String url) throws IOException
	{
			logger.debug("get stream from " + url);
			HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
						  connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
						  connection.setInstanceFollowRedirects(true);
						  connection.connect();
						  
			return connection;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public List<MTGStory> next() throws IOException 
	{
		String url=baseURI+"/"+local.getLanguage()+"/section-articles-see-more-ajax?l="+local.getLanguage()+"&sort=DESC&f=13961&offset="+(offset++);
		List<MTGStory> list = new ArrayList<>();
		HttpURLConnection con = (HttpURLConnection) getConnection(url);
		JsonReader reader = null;
		try {
			reader = new JsonReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
		} catch (Exception e1) {
			logger.error("Error parsing URL",e1);
		}
		JsonElement el = parser.parse(reader);
		JsonArray arr = el.getAsJsonObject().get("data").getAsJsonArray();
		
		for(int i=0;i<arr.size();i++)
		{
			JsonElement e = arr.get(i);
			String finale = StringEscapeUtils.unescapeJava(e.toString());
			Document d = Jsoup.parse(finale);
			try {
			MTGStory story = new MTGStory();
					 story.setTitle(d.select("div.title h3").html());
					 story.setAuthor(StringEscapeUtils.unescapeHtml3(d.select("span.author").html()));
					 story.setDescription(StringEscapeUtils.unescapeHtml3(d.select("div.description").html()));
					 story.setUrl(new URL(baseURI+d.select("a").first().attr("href")));
					 story.setDate(d.select("span.date").text());
					 String bgImage=d.select("div.image").attr("style");
					 story.setIcon(loadPics(new URL(bgImage.substring(bgImage.indexOf("url(")+4,bgImage.indexOf(");")))));
				list.add(story);
			}
			catch(Exception e2)
			{
				logger.error("Error loading story "+finale,e2);
			}
		}
		
		
		return list;
	}
	
	private Image loadPics(URL url) {
		Image tmp;
		try {
			tmp = ImageIO.read(url).getScaledInstance(200, 110, Image.SCALE_SMOOTH);
			return tmp;
		} catch (IOException e) {
			logger.error("could not load" + url,e);
		}
		return null;

		
	}

}
