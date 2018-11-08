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
		RulesNode rn = null;
		
		String[] tab = s.split("\n");
		int start = ArrayUtils.indexOf(tab, "Credits\r")+1;
	
		for(int i=start;i<tab.length;i++)
		{
			String line = tab[i];
			
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
					rn.addLine(line);
				}
				
				System.out.println(rn +" " + rn.getData().size());
				
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


