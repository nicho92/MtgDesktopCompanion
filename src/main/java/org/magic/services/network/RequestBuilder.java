package org.magic.services.network;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.input.BoundedInputStream;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Document;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.Chrono;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.UITools;
import org.magic.services.tools.XMLTools;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class RequestBuilder
{

	private String url;
	private METHOD method;
	private Map<String,String> headers;
	private  Map<String,String> content;
	private MTGHttpClient client;
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

	public Map<String, String> getContent() {
		return content;
	}

	public String toContentString() throws IOException {
		return EntityUtils.toString(execute().getEntity());
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

	public RequestBuilder get()
	{
		method(METHOD.GET);
		return this;
	}
	
	public RequestBuilder post()
	{
		method(METHOD.POST);
		return this;
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

	public RequestBuilder addHeaders(Map<String, String> headers2) {

		headers2.entrySet().forEach(e->headers.put(e.getKey(),e.getValue()));
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

	public RequestBuilder setClient(MTGHttpClient client) {
		this.client=client;
		return this;
	}

	public JsonElement toJson()
	{
		try {
			return URLTools.toJson(toContentString());
		} catch (Exception e) {
			var je = new JsonObject();
			je.addProperty("error", e.getMessage());
			return je;
		}
	}


	public BufferedImage toImage() throws IOException {

		try(var stream = execute().getEntity().getContent()){
			return ImageTools.read(stream);
		}

	}


	public HttpResponse execute() throws IOException
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


	public org.w3c.dom.Document toXml() throws IOException {
		try {
			return XMLTools.createSecureXMLDocumentBuilder().parse(execute().getEntity().getContent());
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public Document toHtml() throws IOException {
		return URLTools.toHtml(toContentString());
	}

	public MTGHttpClient getClient() {
		return client;

	}

	public void download(File dest) throws IOException {
		
		var c = new Chrono();
		c.start();
		var stream = BoundedInputStream.builder().setInputStream(execute().getEntity().getContent()).get();
		FileTools.copyInputStreamToFile(stream,dest);
		var csi = c.stop();
		var size = UITools.humanReadableSize(stream.getCount());
		MTGLogger.getLogger(this.getClass()).info( "{} in {} s",size,csi);
		
	}

	public RequestBuilder addContent(String s) {
		content.put("", s);
		return this;
	}



}
