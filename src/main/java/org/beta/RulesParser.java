package org.beta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.magic.tools.CardsPatterns;
import org.magic.tools.URLTools;

public class RulesParser {

	private static Integer parse(String s)
	{
		try {
			return Integer.parseInt(s);
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	
	public static void main(String[] args) throws IOException {
		String s = URLTools.extractAsString("http://media.wizards.com/2018/downloads/MagicCompRules%2020181005.txt","ISO-8859-15");

		
		String[] tab = s.split("\n");
		int start = ArrayUtils.indexOf(tab, "Credits\r")+1;
		
		RulesNode root = new RulesNode();
		
		
		for(int i=start;i<tab.length;i++)
		{
			String line = tab[i];
			
			if(line.startsWith("Glossary"))
				break;
			
			
			if(!StringUtils.isAllBlank(line))
			{
				Matcher m = CardsPatterns.extract(line,CardsPatterns.RULES_LINE);
				
				RulesNode rn = new RulesNode();
				
				
				if(m.find())
				{
					if(m.group(1)!=null)
					{
						rn.setChapter(parse(m.group(1)));
					}
				
					if(m.group(2)!=null)
					{
						rn.setSubChapter(parse(m.group(1)));
					}

					if(m.group(3)!=null)
						rn.setSubsubchapter(m.group(1));

					rn.addLine(line.replaceAll(CardsPatterns.RULES_LINE.getPattern(), ""));					
				}
				else
				{
					rn.addLine(line);
				}
				
				System.out.println(rn);
				
			}
		}
		
	}
}






class RulesNode
{
	List<String> lines;
	Integer chapter;
	Integer subChapter;
	String subsubchapter;
	List<RulesNode> childNode;
	
	public void setChapter(Integer chapter) {
		this.chapter = chapter;
	}
	
	public void setSubChapter(Integer subChapter) {
		this.subChapter = subChapter;
	}
	
	public void setSubsubchapter(String subsubchapter) {
		this.subsubchapter = subsubchapter;
	}
	
	
	public void addLine(String l)
	{
		lines.add(l);
	}
	
	public void addNode(RulesNode n)
	{
		childNode.add(n);
	}
	
	public RulesNode(int c1,int c2,String c3)
	{
		this.chapter=c1;
		this.subChapter=c2;
		this.subsubchapter=c3;
		init();
	}
	
	public RulesNode()
	{
		init();
	}
	
	private void init() {
		lines=new ArrayList<>();
		childNode = new ArrayList<>();
	}
	
	@Override
	public String toString() {
		return chapter +"/" + subsubchapter+"/"+subsubchapter + " " + lines.size();
	}
	
	
}

