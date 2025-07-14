package org.magic.api.pictureseditor.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumExtraCardMetaData;
import org.magic.api.beans.enums.EnumFrameEffects;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractPicturesEditorProvider;
import org.magic.game.model.abilities.ActivatedAbilities;
import org.magic.game.model.abilities.LoyaltyAbilities;
import org.magic.game.model.abilities.SagaAbilities;
import org.magic.game.model.factories.AbilitiesFactory;
import org.magic.services.AccountsManager;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.URLTools;
import org.magic.services.tools.ImageTools;
public class MTGDesignPicturesProvider extends AbstractPicturesEditorProvider{


	private static final String CARD_ACCENT = "card-accent";
	private static final String CARD_TEMPLATE = "card-template";
	private static final String FALSE = "false";
	private static final String DESIGNER ="designer";
	private static final String BASE_URI="https://mtg.design";
	private MTGHttpClient httpclient;


	public MTGDesignPicturesProvider() {
		super();
		httpclient = URLTools.newClient();
	
	}
	
	@Override
	public MOD getMode() {
		return MOD.URI;
	}
	
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return AccountsManager.generateLoginPasswordsKeys();
	}
	

	private void connect() throws IOException {
		String u = BASE_URI+"/login";
	
		if(getAuthenticator().getLogin().isEmpty() || getAuthenticator().getPassword().isEmpty())
		{
			throw new IOException("Please fill LOGIN/PASSWORD field in account panel");
		}
		
		
		
		var p =  httpclient.doGet(u).getEntity();
		String token = URLTools.toHtml(EntityUtils.toString(p)).select("input[name=_token]").first().attr("value");
		List<NameValuePair> nvps = new ArrayList<>();
							nvps.add(new BasicNameValuePair("email", getAuthenticator().getLogin()));
							nvps.add(new BasicNameValuePair("password", getAuthenticator().getPassword()));
							nvps.add(new BasicNameValuePair("remember", "on"));
							nvps.add(new BasicNameValuePair("_token", token));

		
		var headers = new HashMap<String,String>();
		headers.put(URLTools.REFERER, u);
		headers.put(URLTools.UPGR_INSECURE_REQ, "1");
		headers.put(URLTools.ORIGIN, BASE_URI);

		HttpResponse resp = httpclient.doPost(u, new UrlEncodedFormEntity(nvps), headers);

		logger.debug("Connection : {}",resp.getStatusLine().getReasonPhrase());
		
		
	}


	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	@Override
	public BufferedImage getPicture(MTGCard mc, MTGEdition me) throws IOException {
		if(httpclient.getCookies().isEmpty())
			connect();
		
		
		if(mc.getEdition()==null)
		{
			if(me!=null)
				mc.getEditions().set(0, me);
			else
				mc.getEditions().set(0, new MTGEdition("Fake"));
		}

		var build = new URIBuilder();
		build.setScheme("https").setHost("mtg.design").setPath("render");
		
		
		mc.setFrameVersion("2015");
		
		if(me!=null)
		{
			build.addParameter("card-number", !mc.getNumber().isEmpty()?mc.getNumber():"1");
			build.addParameter("card-total", String.valueOf(me.getCardCount()));
			build.addParameter("card-set", me.getId());
			build.addParameter("language", "EN");
			build.addParameter("card-border", "black");
		}
		else
		{
			build.addParameter("card-number", "1");
			build.addParameter("card-total", "1");
			build.addParameter("card-set", "MTG");
			build.addParameter("language", "EN");
			build.addParameter("card-border", "black");
		}
		build.addParameter("card-title", mc.getName());
		build.addParameter("mana-cost", mc.getCost());

		if(!mc.getSupertypes().isEmpty())
			build.addParameter("super-type", String.join(" ", mc.getSupertypes()));

		if(!mc.getTypes().isEmpty())
			build.addParameter("type", String.join(" ", mc.getTypes()));

		if(!mc.getSubtypes().isEmpty())
			build.addParameter("sub-type", String.join(" ", mc.getSubtypes()));

		build.addParameter("text-size", mc.getCustomMetadata().getOrDefault(EnumExtraCardMetaData.SIZE,"30"));

		if(mc.getRarity()!=null)
			build.addParameter("rarity", mc.getRarity().name().substring(0,1).toUpperCase());
		else
			build.addParameter("rarity", "C");

		if(!mc.getArtist().isEmpty())
			build.addParameter("artist", mc.getArtist());

		if(!StringUtils.isEmpty(mc.getPower()))
			build.addParameter("power", mc.getPower());

		if(!StringUtils.isEmpty(mc.getToughness()))
			build.addParameter("toughness", mc.getToughness());

		if(mc.getLoyalty()!=null)
			build.addParameter("loyalty", String.valueOf(mc.getLoyalty()));

		if(mc.getUrl()!=null && mc.getUrl().startsWith("http"))
			build.addParameter("artwork", mc.getUrl());

		build.addParameter(DESIGNER, getString(DESIGNER));

		if(mc.isLand() && mc.getCustomMetadata().get(EnumExtraCardMetaData.ACCENT)!=null)
			build.addParameter("land-overlay", mc.getCustomMetadata().get(EnumExtraCardMetaData.ACCENT));
		else
			build.addParameter("land-overlay", "C");

		build.addParameter("watermark", "0");
		build.addParameter("set-symbol", "0");
		
		if(mc.getCustomMetadata().get(EnumExtraCardMetaData.CENTER)!=null &&  mc.getCustomMetadata().get(EnumExtraCardMetaData.CENTER).equalsIgnoreCase(TRUE) )
			build.addParameter("centered", TRUE);
		
		if(mc.getCustomMetadata().get(EnumExtraCardMetaData.FOIL)!=null &&  mc.getCustomMetadata().get(EnumExtraCardMetaData.FOIL).equalsIgnoreCase(TRUE) )
			build.addParameter("foil",TRUE);
		
		if(mc.getFrameEffects().contains(EnumFrameEffects.PROMO))
			build.addParameter("card-layout", "mgdpromo");
		else
			build.addParameter("card-layout", "regular");


		if(!mc.getText().isEmpty())
		{
			if(mc.isPlaneswalker())
			{

				var abs= AbilitiesFactory.getInstance().getLoyaltyAbilities(mc);
				build.addParameter("pw-size", String.valueOf(abs.size()));
				
				for(var i=0;i<abs.size();i++)
				{ 
					if(i==0)
					{
						build.addParameter("rules-text", String.valueOf(abs.get(i).getCost()).replace("+", "")+": "+ abs.get(i).getEffect() +'\u00a0');
					}
					else
					{
						build.addParameter("pw-text"+(i+1), abs.get(i).getCost()+": "+ abs.get(i).getEffect() +'\u00a0');
					}
					build.addParameter( (i==0)?"rules-text":"pw-text"+(i+1), abs.get(i).getCost()+": "+ abs.get(i).getEffect() +'\u00a0');
				}
			}
			else if(mc.isSaga())
			{
				build.addParameter("type", "Enchantment Saga");
				build.addParameter("saga-reminder", "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)");
				
				var abs = AbilitiesFactory.getInstance().getSagaAbilities(mc);
				build.addParameter("pw-size", String.valueOf(abs.size()));
				for(var i=0;i<abs.size();i++)
				{
					if(i==0)
						build.addParameter("rules-text", abs.get(i).getEffect().toString());
					else
						build.addParameter("pw-text"+(i+1), abs.get(i).getEffect().toString());
				}
			}
			else
			{
				build.addParameter("rules-text", mc.getText());
			}
				
				
		}

		
	
		
		if(!StringUtils.isEmpty(mc.getFlavor()))
			build.addParameter("flavor-text", mc.getFlavor());

		if(mc.getColors().size()==1)
		{
			build.addParameter(CARD_TEMPLATE,mc.getColors().get(0).getCode());
			build.addParameter(CARD_ACCENT, mc.getColors().get(0).getCode());
		}
		else if(mc.getColors().size()==2)
		{
			build.addParameter(CARD_TEMPLATE, "Gld");
			build.addParameter(CARD_ACCENT, getAccents(mc.getColors().get(0).getCode()+mc.getColors().get(1).getCode()));
		}
		else if(mc.getColors().size()>2)
		{
			build.addParameter(CARD_TEMPLATE, "Gld");
			build.addParameter(CARD_ACCENT, "Gld");
		}
		else
		{
			build.addParameter(CARD_TEMPLATE, "C");
			build.addParameter(CARD_ACCENT, "C");
		}

		if(mc.getCustomMetadata().get(EnumExtraCardMetaData.INDICATOR)!=null && mc.getCustomMetadata().get(EnumExtraCardMetaData.INDICATOR).equalsIgnoreCase(TRUE))
		{
			try {
			if(mc.getColors().size()==1)
				build.addParameter("color-indicator",mc.getColors().get(0).getCode());
			else
				build.addParameter("color-indicator", mc.getColors().get(0).getCode()+mc.getColors().get(1).getCode());
			}
			catch(Exception e)
			{
				logger.error(e);
			}
		}
		
		mc.getCustomMetadata().put(EnumExtraCardMetaData.PLUGIN_NAME, getName());

		build.addParameter("edit", FALSE);

		try {
			logger.debug("generate {}",build.build());
			var resp = httpclient.doGet(build.build().toASCIIString());
			logger.info("generate {}",resp.getStatusLine().getReasonPhrase());
			BufferedImage im = ImageTools.read(resp.getEntity().getContent());
			EntityUtils.consume(resp.getEntity());
			return im;
		} catch (Exception e) {
			logger.error("error generate : ",e);
			return null;
		}



	}
	
	
	private String getAccents(String code) {
		
		if(code.equals("WR"))
			return "RW";
		
		if(code.equals("WG"))
			return "GW";
		
		
		return code;
		
		
		
	}

	@Override
	public String getName() {
		return "MtgDesign";
	}


	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of(DESIGNER, new MTGProperty(System.getProperty("user.name"), "The name of the designer"));
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if(obj ==null)
			return false;

		return hashCode()==obj.hashCode();
	}

}
