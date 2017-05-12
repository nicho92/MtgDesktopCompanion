package org.magic.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.KeyWord;

public class KeyWordManager {

	public static void main(String[] args) throws IOException {
		Document d = Jsoup.connect("https://en.wikipedia.org/wiki/List_of_Magic:_The_Gathering_keywords")
    		 	.userAgent("Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13")
    		 	.timeout(0)
				.get();
		
		
		Elements list = d.select("#bodyContent h3>span.mw-headline");
		List<KeyWord> map = new ArrayList<KeyWord>();
		
		
		for(Element e : list)
		{
			map.add(new KeyWord(e.text()));
		}
		
		list = d.select("#bodyContent p");
		for(Element e : list)
		{
			//System.out.println(e.text());
		}
		
		System.out.println(map);
		
	}
}




