package org.magic.test;

import java.io.IOException;

import javax.swing.JFrame;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import javax.swing.JEditorPane;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JScrollPane;

public class MTGoldFishParser extends JFrame {

	JEditorPane editorPaneStandard;
	JEditorPane editorPaneLegacy;
	JEditorPane editorPaneModern;
	
	static final Logger logger = LogManager.getLogger(MTGoldFishParser.class.getName());

	public static enum FORMAT { standard,legacy,vintage,modern};
	public static enum WIN_OR_LOSE { winners,losers};
	public static enum ONLINE_PAPER {online, paper};
	
	
	public MTGoldFishParser() throws IOException {
		getContentPane().setLayout(new GridLayout(2, 2, 0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane);
		
		editorPaneStandard = new JEditorPane();
		scrollPane.setViewportView(editorPaneStandard);
		editorPaneStandard.setContentType("text/html");
		editorPaneStandard.setEditable(false);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		getContentPane().add(scrollPane_1);
		editorPaneModern = new JEditorPane();
		scrollPane_1.setViewportView(editorPaneModern);
		editorPaneModern.setContentType("text/html");
		editorPaneModern.setEditable(false);
		
		
		JScrollPane scrollPane_2 = new JScrollPane();
		getContentPane().add(scrollPane_2);
		editorPaneLegacy = new JEditorPane();
		scrollPane_2.setViewportView(editorPaneLegacy);
		editorPaneLegacy.setContentType("text/html");
		editorPaneLegacy.setEditable(false);
			
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					editorPaneStandard.setText(parse(FORMAT.standard,"dod",WIN_OR_LOSE.winners,ONLINE_PAPER.paper)+parse(FORMAT.standard,"dod",WIN_OR_LOSE.losers,ONLINE_PAPER.paper));
					editorPaneModern.setText(parse(FORMAT.modern,"dod",WIN_OR_LOSE.winners,ONLINE_PAPER.paper)+parse(FORMAT.modern,"dod",WIN_OR_LOSE.losers,ONLINE_PAPER.paper));
					editorPaneLegacy.setText(parse(FORMAT.legacy,"dod",WIN_OR_LOSE.winners,ONLINE_PAPER.paper)+parse(FORMAT.legacy,"dod",WIN_OR_LOSE.losers,ONLINE_PAPER.paper));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}).start();
		
		setVisible(true);
	
	}
	
	

	public String parse(FORMAT format,String weekordaly,WIN_OR_LOSE winorloose,ONLINE_PAPER onpap) throws IOException
	{
		String url = "http://www.mtggoldfish.com/movers-details/"+onpap+"/"+format+"/"+winorloose+"/"+weekordaly;
		
		Document doc = Jsoup.connect(url)
							.userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
							.timeout(0)
							.get();
		
		Element table =null;
		try{
		
		table = doc.select("table").get(0).getElementsByTag("tbody").get(0);
		
		for(Element e : table.getElementsByTag("tr"))
		{
			String name = e.getElementsByTag("TD").get(3).text();
			String img = e.getElementsByTag("TD").get(3).getElementsByTag("a").get(0).attr("data-full-image");
			String ed = e.getElementsByTag("TD").get(2).getElementsByTag("img").get(0).attr("alt");
			String price= e.getElementsByTag("TD").get(4).text();
			String priceChange = e.getElementsByTag("TD").get(1).text();
			String percentChange = e.getElementsByTag("TD").get(5).text();
			String mouvement = (priceChange.startsWith("+")? "+": (priceChange.startsWith("-")? "-":""));
		
		}
		return table.html();
		
		
		}
		catch(IndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
		return "";
		
		
	}
	

	public String parseEdition(String edition,ONLINE_PAPER onpap) throws IOException
	{
		String urlEditionChecker = "http://www.mtggoldfish.com/index/"+edition+"#"+onpap;
		
		Document doc = Jsoup.connect(urlEditionChecker)
							.userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
							.timeout(0)
							.get();
		
		Element table =null;
		try{
		
		table = doc.select("table").get(1).getElementsByTag("tbody").get(0);
		
		for(Element e : table.getElementsByTag("tr"))
		{
			String name = e.getElementsByTag("TD").get(0).text();
			String img = e.getElementsByTag("TD").get(0).getElementsByTag("a").get(0).attr("data-full-image");
			String ed = e.getElementsByTag("TD").get(1).text();
			String rarity = e.getElementsByTag("TD").get(2).text();
			String price = e.getElementsByTag("TD").get(3).text();
			String priceDChange = e.getElementsByTag("TD").get(4).text();
			String percentDChange = e.getElementsByTag("TD").get(5).text();
			String priceWChange = e.getElementsByTag("TD").get(6).text();
			String percentWChange = e.getElementsByTag("TD").get(7).text();
			String dailyChange = (priceDChange.startsWith("+")? "+": (priceDChange.startsWith("-")? "-":""));
			String weeklyChange = (priceWChange.startsWith("+")? "+": (priceWChange.startsWith("-")? "-":""));
			
			//System.out.println(name + " " + priceDChange +" " + dailyChange);
			
			
		}
		return table.html();
		
		
		}
		catch(IndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
		return "";
		
		
	}
	
	
	
	
	
	public static void main(String[] args) throws IOException {
			new MTGoldFishParser();
	
	}
	
}
