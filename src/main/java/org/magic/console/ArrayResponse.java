package org.magic.console;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.google.gson.JsonElement;

import de.vandermeer.asciitable.AsciiTable;

public class ArrayResponse extends AbstractResponse {

	private JsonElement element;
	private List<String> attributes;
	private Class classe;
	
	
	
	public ArrayResponse(Class cls, List<String> attributes, JsonElement element)
	{
		this.classe=cls;
		this.attributes=attributes;
		this.element=element;
	}
	

	@Override
	public String show() {
		var at = new AsciiTable();
		at.getContext().setWidth(200);
		at.addRule();
		at.addRow(getAttributes());
		at.addRule();
		
		if(element.isJsonArray())
		{	
			for(var i=0;i<getElement().getAsJsonArray().size();i++)
			{
				var obj = getElement().getAsJsonArray().get(i).getAsJsonObject();
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
		}
		else
		{
			List<String> values = new ArrayList<>();
			for(String k : getAttributes())
			{
				if(element.getAsJsonObject().get(k)!=null)
				{
					if(element.getAsJsonObject().get(k).isJsonPrimitive())
						values.add(element.getAsJsonObject().get(k).getAsString());
					else
						values.add(element.getAsJsonObject().get(k).toString());
				}
				else
					values.add("");
			}
			at.addRow(values);
		}
		return at.render();
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
}
