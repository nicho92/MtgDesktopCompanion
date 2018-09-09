package org.magic.api.pictureseditor.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.exports.impl.PDFExport;
import org.magic.api.interfaces.abstracts.AbstractPicturesEditorProvider;
import org.magic.game.model.abilities.LoyaltyAbilities;
import org.magic.game.model.factories.AbilitiesFactory;
import org.magic.services.MTGConstants;
import org.magic.tools.ColorParser;
import org.magic.tools.ImageUtils;
import org.magic.tools.URLTools;

import com.google.gson.JsonElement;

public class FunCardMakerPicturesProvider extends AbstractPicturesEditorProvider {

	
	private static final String GENERATE_URL ="http://funcardmaker.thaledric.fr/generate.php";
	private static final String UPLOAD_URL ="http://funcardmaker.thaledric.fr/upload.php";
	private static final String WEBSITE="funcardmaker.thaledric.fr";
	private BasicHttpContext httpContext;
	private BasicCookieStore cookieStore;
	private HttpClient httpclient;

	
	
	public FunCardMakerPicturesProvider() {
		super();
		cookieStore = new BasicCookieStore();
		httpContext = new BasicHttpContext();
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
	}
	
	@Override
	public MOD getMode() {
		return MOD.FILE;
	}
	
	private void connect()
	{
		httpclient = HttpClients.custom().setUserAgent(MTGConstants.USER_AGENT).setRedirectStrategy(new LaxRedirectStrategy()).build();
		
	}
	
	@Override
	public void setFoil(Boolean b) {
		// do nothing
		
	}

	@Override
	public void setTextSize(int size) {
		//do nothing
		
	}

	@Override
	public void setCenter(boolean center) {
		//do nothing
		
	}

	@Override
	public void setColorIndicator(boolean selected) {
		// do nothing
		
	}

	@Override
	public void setColorAccentuation(String c) {
		
		if(c.length()>1)
		{
			c=c.charAt(0)+"/"+c.charAt(1);
		}
		
		setProperty("HYBRIDE",c);
		
	}

	@Override
	public BufferedImage getPicture(MagicCard mc, MagicEdition me) throws IOException {
		if(httpclient==null)
			connect();
		
		HttpPost post = new HttpPost(GENERATE_URL);
		post.addHeader("Host", WEBSITE);
		post.addHeader("Origin", "http://"+WEBSITE);
		post.addHeader("Referer","http://"+WEBSITE);
		
		List<NameValuePair> nvps = new ArrayList<>();
						    nvps.add(new BasicNameValuePair("width", "791"));
						    nvps.add(new BasicNameValuePair("height", "1107"));
						    nvps.add(new BasicNameValuePair("fields[title]", mc.getName()));
						    nvps.add(new BasicNameValuePair("fields[type]", mc.getFullType()));
						    nvps.add(new BasicNameValuePair("fields[capa]", mc.getText()));
						    nvps.add(new BasicNameValuePair("fields[ta]", mc.getFlavor()));
						    nvps.add(new BasicNameValuePair("fields[illustrator]", mc.getArtist()));
						    nvps.add(new BasicNameValuePair("fields[copyright]",getString("COPYRIGHT")));
						    nvps.add(new BasicNameValuePair("fields[cm]",mc.getCost()));
						   
						    
						    if(mc.isPlaneswalker())
						    {
						    	List<LoyaltyAbilities> abs = AbilitiesFactory.getInstance().getLoyaltyAbilities(mc);
						    	nvps.add(new BasicNameValuePair("template", "modern-planeswalker"+abs.size()));
						    	nvps.add(new BasicNameValuePair("fields[loyalty-base]", String.valueOf(mc.getLoyalty())));
						    	for(int i=0;i<abs.size();i++)
						    	{
						    		nvps.add(new BasicNameValuePair("fields[capa"+(i+1)+"-cost]", abs.get(i).getCost().toString().trim()));
						    		nvps.add(new BasicNameValuePair("fields[capa"+(i+1)+"]", abs.get(i).getEffect().toString().trim()));
						    	}
						    }
						    else
						    {
						    	nvps.add(new BasicNameValuePair("template", getString("LAYOUT_OLD_MODERN").toLowerCase()+"-basic"));
						    }
						    
						    String colorBase;
						    
						    if(mc.isArtifact())
						    	colorBase="a";
						    else if(mc.isLand())
						    	colorBase="l"; 
						    else if(mc.getColors().isEmpty())
						    	colorBase="c";
						    else 
						    {
						    	String c = ColorParser.getCodeByName(mc.getColors(), false).toLowerCase();
						    	if(c.length()==1)
						    		colorBase=c;
						    	else
						    		colorBase="m";
						    }
						    
						    if(!getString("HYBRIDE").isEmpty())
						    	colorBase=getString("HYBRIDE").toLowerCase();
						    
						    
						    nvps.add(new BasicNameValuePair("fields[background-base]", colorBase));
						    nvps.add(new BasicNameValuePair("fields[background-texture]", colorBase));
						    
						    if(mc.isCreature())
						    	nvps.add(new BasicNameValuePair("fields[fe]",mc.getPower()+"/"+mc.getToughness()));
						    
						    if(!mc.getRarity().isEmpty())
						    	nvps.add(new BasicNameValuePair("fields[se-rarity]",mc.getRarity().substring(0,1).toLowerCase()));
							
						    if(mc.getImageName()!=null && !mc.getImageName().startsWith("http"))
						    {
						    	File f = new File(mc.getImageName());
						    	if(f.exists())
						    	{
						    		String filename=upload(f);
						    		nvps.add(new BasicNameValuePair("fields[illustration]",filename));
						    	}
						    }

						    post.setEntity(new UrlEncodedFormEntity(nvps));
						    				
						    logger.debug(post + " with " + nvps);
						    HttpResponse resp = httpclient.execute(post, httpContext);
						    String ret = EntityUtils.toString(resp.getEntity());
						    logger.trace("RESPONSE: "+ret);
						    
						    JsonElement el = URLTools.toJson(ret);
		
		return ImageUtils.readBase64(el.getAsJsonObject().get("image").getAsString());
	}

	
	private String upload(File f) throws IOException {
		if(httpclient==null)
			connect();
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("fcm-file-media", new FileBody(f, ContentType.DEFAULT_BINARY));
		builder.addTextBody("fcm-field-illuscrop-x", "0");
		builder.addTextBody("fcm-field-illuscrop-y", "0");
		builder.addTextBody("fcm-field-illuscrop-w", "46");
		builder.addTextBody("fcm-field-illuscrop-h", "7");
		builder.addTextBody("MAX_FILE_SIZE", "104857600");
		
		HttpEntity ent = builder.build();
		HttpPost upload = new HttpPost(UPLOAD_URL);
				upload.addHeader("Host", WEBSITE);
				upload.addHeader("Origin", "http://"+WEBSITE);
				upload.addHeader("Referer","http://"+WEBSITE);
				upload.addHeader("X-Requested-With","XMLHttpRequest");
					
				 upload.setEntity(ent);
				 HttpResponse resp = httpclient.execute(upload, httpContext);
				 logger.trace("Upload image " + resp.getStatusLine());
				 JsonElement response = URLTools.toJson(EntityUtils.toString(resp.getEntity()));
				 logger.trace("response:"+response);
				 
				 if(response.getAsJsonObject().get("error")!=null)
					 throw new IOException(response.getAsJsonObject().get("error").getAsString());
				
				 EntityUtils.consume(upload.getEntity());
				 return response.getAsJsonObject().get("filepath").getAsString();
				 
				 
	}

	@Override
	public void initDefault() {
		setProperty("COPYRIGHT", "(c)2018-Wizards of the coast");
		setProperty("LAYOUT_OLD_MODERN","modern");
		setProperty("HYBRIDE","");
	}
	
	@Override
	public String getName() {
		return "FunCardMaker";
	}
	
	@Override
	public String getVersion() {
		return "0.4.1 Alpha";
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(PDFExport.class.getResource("/icons/plugins/smf.png"));
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
}
