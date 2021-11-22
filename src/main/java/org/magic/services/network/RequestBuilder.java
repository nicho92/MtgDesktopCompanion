package org.magic.services.network;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.jsoup.nodes.Document;

import com.google.gson.JsonElement;

public class RequestBuilder
{
	
	private String url;
	private METHOD method;
	private Map<String,String> headers;
	private Map<String,String> content;
	private URLToolsClient client;
	public enum METHOD { POST, GET,PUT}
	
	
	public RequestBuilder() {
		headers = new HashMap<>();
		content= new TreeMap<>();
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public METHOD getMethod() {
		return method;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> header) {
		this.headers = header;
	}

	public Map<String, String> getContent() {
		return content;
	}

	public void setContent(Map<String, String> content) {
		this.content = content;
	}

	@Override
	public String toString() {
		var builder = new StringBuilder();
		
		builder.append(method).append(" ").append(url).append("\n");
		
		if(!headers.isEmpty()) {
			builder.append("headers:\n");
			headers.entrySet().forEach(entry->builder.append(entry.getKey()).append(":").append(entry.getValue()).append("\n"));
		}
		
		
		if(!content.isEmpty()) {
			builder.append("body:\n");
			content.entrySet().forEach(entry->builder.append(entry.getKey()).append(":").append(entry.getValue()).append("\n"));
		}
		
		
		return builder.toString();
	}
	
	public static RequestBuilder build()
	{
		return new RequestBuilder();
	}
	
	public RequestBuilder method(METHOD m)
	{
		method=m;
		return this;
	}
	
	public RequestBuilder url(String u)
	{
		url=u;
		return this;
	}
	
	public RequestBuilder url(URI u)
	{
		url=u.toString();
		return this;
	}
	
	public RequestBuilder clearHeaders()
	{
		headers.clear();
		return this;
	}

	public RequestBuilder clearContents()
	{
		content.clear();
		return this;
	}
	
	public RequestBuilder addHeader(String k, String c)
	{
		headers.put(k, c);
		return this;
	}
	
	public RequestBuilder addContent(String k, String c)
	{
		content.put(k, c);
		return this;
	}

	public RequestBuilder setClient(URLToolsClient client) {
		this.client=client;
		return this;
	}
	
	public JsonElement toJson() throws IOException
	{
		return URLTools.toJson(execute());
	}
	
	public HttpResponse toResponse() throws IOException
	{
		execute();
		return client.getResponse();
		
	}
	
	public String execute() throws IOException
	{
		if(client!=null)
			return client.execute(this);
		
		throw new IOException("You must set a httpClient with .setClient()");
	}

	public RequestBuilder clean() {
		
		clearHeaders();
		clearContents();
		url("");
		method(null);
		return this;
	}

	public Document toHtml() throws IOException {
		return URLTools.toHtml(execute());
	}

	public URLToolsClient getClient() {
		return client;
		
	}

	public void download(File dest) throws IOException {
		FileUtils.copyInputStreamToFile(toResponse().getEntity().getContent(),dest);
	}

	public RequestBuilder addContent(String s) {
		content.put("", s);
		return this;
	}
	
}