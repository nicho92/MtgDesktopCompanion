package org.beta;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.CharSet;
import org.apache.commons.lang3.StringUtils;
import org.magic.services.MTGConstants;
import org.magic.tools.CardsPatterns;
import org.magic.tools.URLTools;

public class RulesParser {

	private Integer parse(String s)
	{
		try {
			return Integer.parseInt(s);
		}
		catch(Exception e)
		{
			return null;
		}
	}
	private RulesNode root;
	
	public RulesParser() {
		root=new RulesNode();
	}
	
	public void addNode(RulesNode node)
	{
			
		if(node.getLevel()==1)
			root.addChild(node);
		
		
	}
	
	public RulesNode getRoot()
	{
		return root;
	}
	
	
	public static void main(String[] args) throws IOException {
		RulesParser parser = new RulesParser();
		parser.read(URLTools.extractAsString(MTGConstants.URL_RULES_FILE, Charset.forName("ISO-8859-15")));
		
		parser.getRoot().getChildren().forEach(System.out::println);
		
	}
	
	
	public void read(String s){
		RulesNode rn = null;
		
		String[] tab = s.split("\n");
		int start = ArrayUtils.indexOf(tab, "Credits\r")+1;
	
		for(int i=start;i<tab.length;i++)
		{
			String line = tab[i];
			
			line = StringUtils.trimToEmpty(line);
			if(line.startsWith("Glossary"))
				break;
			
			if(!StringUtils.isAllBlank(line))
			{
				Matcher m = CardsPatterns.extract(line,CardsPatterns.RULES_LINE);
				
				if(m.find())
				{
					rn = new RulesNode();
					
					if(m.group(1)!=null)
						rn.setChapter(parse(m.group(1)));
				
					if(m.group(2)!=null)
						rn.setSubChapter(parse(m.group(2)));
				
					if(m.group(3)!=null)
						rn.setSubsubchapter(m.group(3).toCharArray()[0]);

					rn.addLine(line.replaceAll(CardsPatterns.RULES_LINE.getPattern(), ""));					
				}
				else
				{
					if(rn!=null)
						rn.addLine(line);
				}
			}
			else
			{
				if(rn!=null)
					addNode(rn);
			}
		}
	}
}

class RulesNode{
    private List<String> data;
    private List<RulesNode> children;
    private RulesNode parent = null;
    Integer chapter;
	Integer subChapter;
	Character subsubchapter;
    
    public RulesNode(int c1,int c2,char c3) {
    	this.chapter=c1;
		this.subChapter=c2;
		this.subsubchapter=c3;
		init();
	}
    
    public RulesNode() {
		init();
	}
    
    public int getLevel()
    {
    	int lev = 3;
    	
    	if(subsubchapter==null)
    		lev=2;
    	
    	if(subChapter==null)
    		lev=1;
    	
    	if(chapter==null)
    		lev=0;
    	
    	return lev;
    }
    
    private void init() {
		data=new ArrayList<>();
		children = new ArrayList<>();
	}
    
    public void setChapter(Integer chapter) {
		this.chapter = chapter;
	}
    
    public void setSubChapter(Integer subChapter) {
		this.subChapter = subChapter;
	}
    
    public void setSubsubchapter(Character subsubchapter) {
		this.subsubchapter = subsubchapter;
	}
    
    
    public void addChild(RulesNode child) {
        child.setParent(this);
        this.children.add(child);
    }

    public void addChildren(List<RulesNode> children) {
        for(RulesNode t : children) {
            t.setParent(this);
        }
        this.children.addAll(children);
    }

    public List<RulesNode> getChildren() {
        return children;
    }

    public List<String> getData() {
        return data;
    }

    public void addLine(String d) {
        data.add(d);
    }

    private void setParent(RulesNode parent) {
        this.parent = parent;
    }

    public RulesNode getParent() {
        return parent;
    }
    
    @Override
	public String toString() {
		StringBuilder temp = new StringBuilder();
		temp.append(chapter).append(".");
		
		if(subChapter!=null)
			temp.append(subChapter);
		
		if(subsubchapter!=null)
			temp.append(subsubchapter);
		
		return temp.toString();
	}
	
}


