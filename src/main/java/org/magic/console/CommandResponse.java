package org.magic.console;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.vandermeer.asciitable.AsciiTable;

public class CommandResponse {
	
	private JsonElement element;
	private List<String> attributes;
	private Class classe;
	
	public boolean isList()
	{
		return element.isJsonArray();
	}
	
	public Class getClasse() {
		return classe;
	}
	
	public void setClasse(Class classe) {
		this.classe = classe;
	}
	
	
	public CommandResponse() {
		attributes=new ArrayList<>();
	}
	
	public CommandResponse(Class cls, List<String> attributes, JsonElement element)
	{
		this.classe=cls;
		this.attributes=attributes;
		this.element=element;
	}
	
	public JsonElement getElement() {
		return element;
	}
	public void setElement(JsonElement element) {
		this.element = element;
	}
	public List<String> getAttributes() {
		if(attributes==null)
		{
			try {
				return new ArrayList<>(BeanUtils.describe(classe.getConstructor().newInstance()).keySet());
			} catch (Exception e) {
				return new ArrayList<>();
			}
		}
		return attributes;
	}
	
	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}
	
	@Override
	public String toString() {
		return show();
	}
	
	public String show()
	{
		if(isList())
			return showList();
		else
			return showObject();
	}
	
	
	private String showObject() {
		AsciiTable at = new AsciiTable();
		at.getContext().setWidth(200);
		at.addRule();
		at.addRow("NAME","VALUE");
		at.addRule();
		JsonObject obj = getElement().getAsJsonObject();
		for(String k : getAttributes())
		{
			String val="";
			if(obj.get(k)!=null)
			{
				if(obj.get(k).isJsonPrimitive())
					val=(obj.get(k).getAsString());
				else
					val=(obj.get(k).toString());
			}
			
			at.addRow(k,val);
			at.addRule();
		}
		
		return at.render();
	}

	private String showList() {
		AsciiTable at = new AsciiTable();
		at.getContext().setWidth(200);
		at.addRule();
		at.addRow(getAttributes());
		at.addRule();
		for(int i=0;i<getElement().getAsJsonArray().size();i++)
		{
			JsonObject obj = getElement().getAsJsonArray().get(i).getAsJsonObject();
			List<String> values = new ArrayList<>();
			for(String k : getAttributes())
			{
				if(obj.get(k)!=null)
				{
					if(obj.get(k).isJsonPrimitive())
						values.add(obj.get(k).getAsString());
					else
						values.add(obj.get(k).toString());
				}
				else
					values.add("");
			}
			at.addRow(values);
		}
		return at.render();
	}
	
	
	
	

}
