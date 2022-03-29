package org.magic.api.beans;

import java.util.ArrayList;
import java.util.List;

import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.interfaces.MTGPlugin.PLUGINS;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class PluginEntry <T extends MTGPlugin>
{
	private String classpath;
	private String root;
	private String element;
	private boolean multiprovider;
	private List<T> plugins;
	private PLUGINS type;
	private Class<T> classeType;
	
	@Override
	public String toString() {
		return getClasspath()+ " " + getXpath();
	}
	
	public void setPlugins(List<T> plugins) {
		this.plugins = plugins;
	}
	

	public JsonObject toJson() {
		var obj = new JsonObject();
		obj.addProperty("TYPE", getType().name());
		obj.addProperty("MULTI", isMultiprovider());
		obj.addProperty("CLASSPATH", getClasspath());
		
		var arr = new JsonArray();
		getPlugins().forEach(p->arr.add(p.toJson()));
		
		
		obj.add("plugins", arr);
		
		return obj;
		
	}

	
	
	public List<T> getPlugins() {
		return plugins;
	}
	
	public Class<T> getParametrizedClass() {
		return classeType;
	}
	
	public PluginEntry (Class<T> classType,boolean multiprovider,String root, String element,String classpath,PLUGINS type)
	{
		this.classeType=classType;
		this.type=type;
		this.root=root;
		this.element=element;
		this.classpath=classpath;
		this.setMultiprovider(multiprovider);
		plugins = new ArrayList<>();
	}
	
	public PLUGINS getType() {
		return type;
	}
	
	public String getRoot() {
		return root;
	}
	
	public String getElement() {
		return element;
	}
	
	public String getClasspath() {
		return classpath;
	}
	
	public String getXpath() {
		return root+element;
	}
	
	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}

	public boolean isMultiprovider() {
		return multiprovider;
	}

	public void setMultiprovider(boolean multiprovider) {
		this.multiprovider = multiprovider;
	}

	
}