package org.magic.api.pictureseditor.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumCardsPatterns;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.enums.EnumExtraCardMetaData;
import org.magic.api.beans.enums.EnumFrameEffects;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractPicturesEditorProvider;
import org.magic.services.AccountsManager;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.CryptoUtils;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.ImageTools;

import com.google.gson.JsonObject;

public class MTGCardSmithEditor extends AbstractPicturesEditorProvider {

	private static final String BASE_URL = "https://mtgcardsmith.com";
	private static final String BASE_URL_DEVM = "https://devm.mtgcardsmith.com";
	
	private final String urlBuilder=BASE_URL_DEVM+"/src/actions/m15cardTest";
	private final String urlPictureUpload=BASE_URL+"/src/actions/cards/upload";
	private final String urlAuthentication=BASE_URL+"/my-account/";
	
	private MTGHttpClient client;
	private boolean connected;
	private Map<String,String> layout;
	
	
	
	private boolean isNormalLayout(MTGCard mc)
	{
		return !mc.isBorderLess() && !mc.getFrameEffects().contains(EnumFrameEffects.ETCHED) && !mc.isVehicule() && !mc.getFrameEffects().contains(EnumFrameEffects.ENCHANTMENT) && !mc.isSnow() && !mc.getFrameEffects().contains(EnumFrameEffects.DEVOID); 
	}
	
	@Override
	public BufferedImage getPicture(MTGCard mc, MTGEdition me) throws IOException {
		
		connect();
		
		var imgPath = uploadPicture(new File(mc.getUrl()),mc);
		
		int size = Integer.parseInt(mc.getCustomMetadata().getOrDefault(EnumExtraCardMetaData.SIZE,"30"));
		
		var build = RequestBuilder.build().url(urlBuilder+"?fromAjax=1&v=3").setClient(client).post()
				.addHeader("x-requested-with", "XMLHttpRequest")
				.addHeader(URLTools.REFERER, BASE_URL)
				.addHeader(URLTools.HOST, "devm.mtgcardsmith.com")
				.addHeader(URLTools.ORIGIN, BASE_URL)
				.addHeader(URLTools.ACCEPT, "application/json, text/javascript, */*; q=0.01")
				.addHeader(URLTools.ACCEPT_ENCODING, "gzip, deflate, br, zstd")
				.addHeader(URLTools.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8")
			//	.addContent("session_data_card", "{\"dimensions\":{\"width\":316,\"height\":232},\"frame\":\"m15\",\"image_path\":"+imgPath+"\",\"status\":\"new\"}")
				.addContent("slug","")
				.addContent("create_date","")
				.addContent("frame","/moderator/tmp/"+getFrame(mc))
				.addContent("pos_pt_f","")
				.addContent("pos_pt_ts","34")
				.addContent("pos_sub_f","beleren-bold_P1.01.ttf")
				.addContent("pos_sub_x","")
				.addContent("pos_sub_y","")
				.addContent("frame_name", EnumColors.determine(mc.getColors()).toPrettyString())
				.addContent("pos_cost_y","")
				.addContent("pos_desc_f","Mplantin.ttf")
				.addContent("pos_desc_x","")
				.addContent("pos_desc_y","")
				.addContent("pos_title_f","beleren-bold_P1.01.ttf")
				.addContent("pos_title_x","")
				.addContent("pos_title_y","")
				.addContent("pos_bottom_x","")
				.addContent("pos_bottom_y","")
				.addContent("pos_rarity_x","")
				.addContent("pos_rarity_y","")
				.addContent("pos_bottom2_x","")
				.addContent("pos_bottom2_y","")
				.addContent("pos_bottom3_x","")
				.addContent("pos_bottom3_y","")
				.addContent("frame_category","Standard Colors")
				.addContent("name",mc.getName())
				.addContent("title_color",mc.getCustomMetadata().getOrDefault(EnumExtraCardMetaData.TEXT_COLOR,"#000000"))
				.addContent("custom_mana",mc.getCost()!=null?mc.getCost().toLowerCase():"")
				.addContent("watermark",mc.getWatermarks())
				.addContent("frame_color[]",EnumColors.determine(mc.getColors()).toPrettyString().toLowerCase())
				.addContent("special_card_color","")
				.addContent("pos_art_x",mc.getCustomMetadata().getOrDefault(EnumExtraCardMetaData.X,"0"))
				.addContent("pos_art_y",mc.getCustomMetadata().getOrDefault(EnumExtraCardMetaData.Y,"0"))
				.addContent("pos_art_s",mc.getCustomMetadata().getOrDefault(EnumExtraCardMetaData.ZOOM,"100"))
				.addContent("subtype_color",mc.getCustomMetadata().getOrDefault(EnumExtraCardMetaData.TEXT_COLOR,"#000000"))
				.addContent("type",mc.getSupertypes().stream().collect(Collectors.joining(" ")) + " " + mc.getTypes().stream().collect(Collectors.joining(" ")))
				.addContent("custom_type","")
				.addContent("subtype",mc.getSubtypes().stream().collect(Collectors.joining(" ")))
				.addContent("rarity",mc.getRarity().toPrettyString().toLowerCase())
				.addContent("set_icon","mtgcs1")
				.addContent("body_color",mc.getCustomMetadata().getOrDefault(EnumExtraCardMetaData.TEXT_COLOR,"#000000"))
				.addContent("text_size",(size==18?"vsmall":size<=20?"small":size<=24?"large":"vlarge"))
				.addContent("description", minimize(mc.getText()) + (mc.getFlavor().isEmpty()?"":"\n<i>"+mc.getFlavor()+"</i>"))
				.addContent("power",mc.getPower())
				.addContent("toughness",mc.getToughness())
				.addContent("pos_pt_tx","0")
				.addContent("pos_pt_ty","0")
				.addContent("pt_color","#000000")
				.addContent("pos_pt_x","0")
				.addContent("pos_pt_y","0")
				.addContent("artist",mc.getArtist())
				.addContent("artist_color","#ffffff")
				.addContent("category","")
				.addContent("creator",getString("DESIGNER"))
				.addContent("set", mc.getEdition().getId())
				.addContent("image_path",imgPath);
				
		
		if(mc.isCreature())
			build.addContent("showPT","true");
		
		if(mc.isVehicule())
		{
			var c = EnumColors.determine(mc.getColors());
			
			build.removeContent("frame");
			build.addContent("special_card_color", "vehart01"+(c==EnumColors.GOLD?"m":c.getCode().toLowerCase()));
		}
		
		
		if(EnumColors.determine(mc.getColors())==EnumColors.UNCOLOR)
			build.addContent("frame_color[]", "colorless");
		
		if(EnumColors.determine(mc.getColors())==EnumColors.GOLD && isNormalLayout(mc))
			build.addContent("frame_rare", "/moderator/tmp/custom_666ba6254de65.png");
		
		
		if(mc.isLegendary() && isNormalLayout(mc))
		{
			build.removeContent("frame");
			var color = EnumColors.determine(mc.getColors());
			build.addContent("special_card_color", "lg"+(color==EnumColors.GOLD||color==EnumColors.UNCOLOR?"o":color.getCode().toLowerCase()));
			build.addContent("frame_category","Legendary Frames");
			
			if(mc.isLand())
			{
				build.addContent("frame_rare", "/moderator/tmp/custom_666ba6254de65.png");
				build.addContent("special_card_color", "lgc");
			}
		}

		
		logger.debug("sending {}", build);
		
		var res = build.execute();
	
		if(res.getStatusLine().getStatusCode()!=200)
			throw new IOException(res.getStatusLine().getReasonPhrase());
		
		return URLTools.extractAsImage(BASE_URL+URLTools.toJson(res.getEntity().getContent()).getAsJsonObject().get("success").getAsString());
	}
	
	private String minimize(String text) {
		if(text==null)
			return "";
		
		return Pattern.compile(EnumCardsPatterns.MANA_PATTERN.getPattern()).matcher(text).replaceAll(m -> {
            return m.group().toLowerCase();
        });
		
	}
	
	private String getFrame(MTGCard mc) {
		
		var color= EnumColors.determine(mc.getColors()).name().toLowerCase();
		var ret = layout.get("normal-"+(mc.isLand()?"land":color));
		
			
		if(mc.isBorderLess())
			return layout.get("borderless"+(mc.isLegendary()?"-lgd-":"-")+(mc.isLand()?"land":color));
		
		if(mc.getFrameEffects().contains(EnumFrameEffects.ETCHED))
			return layout.get("etched"+(mc.isLegendary()?"-lgd-":"-")+(mc.isLand()?"land":color));
		
		if(mc.getFrameEffects().contains(EnumFrameEffects.ENCHANTMENT))
			return layout.get("enchantment-"+color);
		
		if(mc.isSnow())
			return layout.get("snow"+(mc.isLegendary()?"-lgd-":"-")+(mc.isLand()?"land":color));
		
		if(mc.getFrameEffects().contains(EnumFrameEffects.DEVOID))
			return layout.get("devoid-"+color);
		
		
		return ret;
	}

	public MTGCardSmithEditor() {
		client = URLTools.newClient();
		initLayout();
	} 
	
	
	private void initLayout() {
		layout = new HashMap<>();
		
		layout.put("normal-white", "custom_666b5f5b0a7b2.jpg");
		layout.put("normal-blue", "custom_666b66c13041d.jpg");
		layout.put("normal-black", "custom_666b62e1659a8.jpg");
		layout.put("normal-red", "custom_666b637f49d3c.jpg");
		layout.put("normal-green", "custom_666b647f32017.jpg");
		layout.put("normal-gold", "custom_666ba621b8c9d.png");
		layout.put("normal-uncolor", "custom_666b64b05d996.jpg");
		layout.put("normal-land", "custom_666b64d958319.jpg");
		
		layout.put("borderless-white", "custom_66637c40bf2a2.png");
		layout.put("borderless-blue", "custom_6663822aa3c55.png");
		layout.put("borderless-black", "custom_6663830c90efd.png");
		layout.put("borderless-red", "custom_6663833f965ba.png");
		layout.put("borderless-green", "custom_666388ec7e198.png");
		layout.put("borderless-gold", "custom_66638b4a98a6e.png");
		layout.put("borderless-uncolor","custom_66638ba07c65e.png");
		layout.put("borderless-land","custom_66638ca862a35.png");
		
		layout.put("borderless-lgd-white", "custom_666389e5e46a1.png");
		layout.put("borderless-lgd-blue", "custom_666382be85178.png");
		layout.put("borderless-lgd-black", "custom_666383273f96e.png");
		layout.put("borderless-lgd-red", "custom_66638376b7b51.png");
		layout.put("borderless-lgd-green", "custom_666389293baab.png");
		layout.put("borderless-lgd-gold", "custom_66638b876689f.png");
		layout.put("borderless-lgd-uncolor","custom_66638bcd6c326.png");
		layout.put("borderless-lgd-land","custom_66638ce7c7962.png");
		
		layout.put("etched-white", "custom_6664ab5a731fd.png");
		layout.put("etched-blue", "custom_6664acc08fa47.png");
		layout.put("etched-black", "custom_6664acf6de3a1.png");
		layout.put("etched-red", "custom_6664ad7621e7e.png");
		layout.put("etched-green", "custom_6664ade1f1fe1.png");
		layout.put("etched-gold","custom_6664ae137d3bb.png");
		layout.put("etched-uncolor","custom_6664aeb38ff4d.png");
		layout.put("etched-land","custom_6664aefd6ecb4.png");
	
		layout.put("etched-lgd-white", "custom_6664ac46512e6.png");
		layout.put("etched-lgd-blue", "custom_6664ace1500bd.png");
		layout.put("etched-lgd-black", "custom_6664ad0e2d7fc.png");
		layout.put("etched-lgd-red", "custom_6664adcb0132a.png");
		layout.put("etched-lgd-green", "custom_6664adfe5c5f4.png");
		layout.put("etched-lgd-gold", "custom_6664ae37db2f3.png");
		layout.put("etched-lgd-uncolor","custom_6664aee666127.png");
		layout.put("etched-lgd-land","custom_6664af14ba437.png");
		
		layout.put("enchantment-white", "custom_666ddff883b56.png");
		layout.put("enchantment-blue", "custom_666de274cd91d.png");
		layout.put("enchantment-black", "custom_666de307afb9c.png");
		layout.put("enchantment-red", "custom_666de366c1d3c.png");
		layout.put("enchantment-green", "custom_666de3b4b75b6.png");
		layout.put("enchantment-gold", "custom_666de40a71e71.png");
		layout.put("enchantment-uncolor","custom_666de520b3ba8.png");
		layout.put("enchantment-land","custom_666de520b3ba8.png");
			
		layout.put("snow-white", "custom_66883e8de255c.png");
		layout.put("snow-blue", "custom_66883eebbb9da.png");
		layout.put("snow-black", "custom_66883f180fe4a.png");
		layout.put("snow-red", "custom_66883f42e7b07.png");
		layout.put("snow-green", "custom_66883f56a4e85.png");
		layout.put("snow-gold", "custom_66883f71996e5.png");
		layout.put("snow-uncolor","custom_66884ef81b4ca.png");
		layout.put("snow-land","custom_66884e9d9d083.png");

		layout.put("snow-lgd-white", "custom_668ae09ed1314.png");
		layout.put("snow-lgd-blue", "custom_668ae0d74b67d.png");
		layout.put("snow-lgd-black", "custom_668ae13e91f64.png");
		layout.put("snow-lgd-red", "custom_668ae157754ba.png");
		layout.put("snow-lgd-green", "custom_668ae16a06865.png");
		layout.put("snow-lgd-gold", "custom_668ae266e779e.png");
		layout.put("snow-lgd-uncolor","custom_668ae27de5d49.png");
		layout.put("snow-lgd-land","custom_668ae2998c618.png");

		layout.put("devoid-white", "custom_6668ca7255427.png");
		layout.put("devoid-blue", "custom_6663a74405cb0.png");
		layout.put("devoid-black", "custom_6663a7713b0d8.png");
		layout.put("devoid-red", "custom_6663a78537554.png");
		layout.put("devoid-green", "custom_6663a7b7e76af.png");
		layout.put("devoid-gold", "custom_6663a7cd57fda.png");
		layout.put("devoid-uncolor","custom_6663a7ea55f7d.png");
		layout.put("devoid-land","custom_6663a8122b4cc.png");
		
		
		
	}
	

	private void connect() throws IOException
	{
		if(connected)
			return;
		
		if(getAuthenticator().getLogin().isEmpty() || getAuthenticator().getPassword().isEmpty()){
			throw new IOException("Please fill LOGIN/PASSWORD field in account panel");
		}
		
		
		var nonce = RequestBuilder.build().url(urlAuthentication).setClient(client).get().toHtml().getElementById("woocommerce-login-nonce").attr("value");
		
		RequestBuilder.build().url(urlAuthentication).setClient(client).post()
																.addHeader(URLTools.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
																.addHeader(URLTools.ACCEPT_ENCODING, "gzip, deflate, br, zstd")
																.addHeader(URLTools.CONTENT_TYPE, "application/x-www-form-urlencoded")
																.addHeader("Pragma", "no-cache")
																.addHeader("Connection", "keep-alive")
																.addHeader(URLTools.HOST, "mtgcardsmith.com")
																.addHeader(URLTools.ORIGIN, BASE_URL)
																.addHeader(URLTools.REFERER, BASE_URL+"/my-account/")
																.addHeader("sec-ch-ua","Not)A;Brand\";v=\"8\",\"Chromium\";v=\"138\", \"Google Chrome\";v=\"138")
																.addHeader("sec-ch-ua-mobile","?0")
																.addHeader("sec-ch-ua-platform","Windows")
																.addHeader("sec-fetch-dest","document")
																.addHeader("sec-fetch-mode","navigate")
																.addHeader("sec-fetch-site","same-origin")
																.addHeader("sec-fetch-user","?1")
																.addHeader("upgrade-insecure-requests","1")
														
																.addContent("username", getAuthenticator().getLogin())
																.addContent("password", getAuthenticator().getPassword())
																.addContent("_wp_http_referer", "/my-account/")
																.addContent("login", "Log in")
																.addContent("rememberme", "forever")
																.addContent("woocommerce-login-nonce", nonce)
																
																.execute();
		
		try 
		{
			var c = RequestBuilder.build().clean().url(urlAuthentication).setClient(client).get().toHtml().select("div.woocommerce-MyAccount-content p strong").first().text();
			logger.info("logged as {}", c);
			connected=true;
		}
		catch(Exception e)
		{
			logger.error("can't connect");
			connected=false;
		}
	}
	
	private String uploadPicture(File f, MTGCard mc) throws IOException
	{
		var res = RequestBuilder.build().url(urlPictureUpload).setClient(client).post().addHeader(urlBuilder, urlAuthentication)
												.addHeader(URLTools.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
												.addHeader(URLTools.ACCEPT_ENCODING, "gzip, deflate, br, zstd")
												.addHeader(URLTools.CONTENT_TYPE, "application/x-www-form-urlencoded")
												.addHeader("Pragma", "no-cache")
												.addHeader("Connection", "keep-alive")
												.addHeader(URLTools.REFERER, BASE_URL+"/mtg-card-maker")
												.addHeader(URLTools.HOST, "mtgcardsmith.com")
												.addHeader(URLTools.ORIGIN, BASE_URL)
												.addContent("location", "mtg-card-maker")	 
												.addContent("status", "new")
												.addContent("slim[]", generateJsonData(f,mc.getCustomMetadata()).toString())
												.toHtml();
		
		var imgPath = res.select("img.previewImg2").attr("src");
		logger.info("File {} uploaded at {}", f,imgPath);
		
		return imgPath ;
	}
	
	
	private JsonObject generateJsonData(File f, Map<EnumExtraCardMetaData, String> map) throws IOException
	{
		
		var binary =  FileTools.readFileAsBinary(f);
		var img = ImageTools.read(binary);
		var content = URLTools.toJson("""
				{
				    "server": null,
				    "meta": {},
				    "input": {
				        "name": "InputName.png",
				        "type": "image/png",
				        "size": 0,
				        "width": 0,
				        "height": 0,
				        "field": null
				    },
				    "output": {
				        "name": "OutputName.png",
				        "type": "image/png",
				        "width": 0,
				        "height": 0,
				        "image": "Base64"
				    },
				    "actions": {
				        "rotation": 0,
				        "crop": {
				            "x": 0,
				            "y": 0,
				            "width": 0,
				            "height": 0,
				            "type": "manual"
				        },
				        "size": {
				            "width": 0,
				            "height": 0
				        }
				    }
				}
				""").getAsJsonObject();
			
			content.get("input").getAsJsonObject().addProperty("name", f.getName());
			content.get("input").getAsJsonObject().addProperty("size", f.length());
			content.get("input").getAsJsonObject().addProperty("width", img.getWidth());
			content.get("input").getAsJsonObject().addProperty("height", img.getHeight());
						
			content.get("output").getAsJsonObject().addProperty("name", f.getName());
			content.get("output").getAsJsonObject().addProperty("width", map.getOrDefault(EnumExtraCardMetaData.CROP_W, ""+img.getWidth()));
			content.get("output").getAsJsonObject().addProperty("height", map.getOrDefault(EnumExtraCardMetaData.CROP_H, ""+img.getHeight()));
			content.get("output").getAsJsonObject().addProperty("image", "data:image/png;base64,"+CryptoUtils.toBase64(binary));			
			
			content.get("actions").getAsJsonObject().get("crop").getAsJsonObject().addProperty("x", map.getOrDefault(EnumExtraCardMetaData.CROP_X, "0"));
			content.get("actions").getAsJsonObject().get("crop").getAsJsonObject().addProperty("y", map.getOrDefault(EnumExtraCardMetaData.CROP_Y, "0"));
			content.get("actions").getAsJsonObject().get("crop").getAsJsonObject().addProperty("width", map.getOrDefault(EnumExtraCardMetaData.CROP_W, ""+img.getWidth()));
			content.get("actions").getAsJsonObject().get("crop").getAsJsonObject().addProperty("height", map.getOrDefault(EnumExtraCardMetaData.CROP_H, ""+img.getHeight()));
			
			content.get("actions").getAsJsonObject().get("size").getAsJsonObject().addProperty("width", img.getWidth());
			content.get("actions").getAsJsonObject().get("size").getAsJsonObject().addProperty("height", img.getHeight());
			
			logger.debug("Upload data {}", content);
			
			return content;
			
	}
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return AccountsManager.generateLoginPasswordsKeys();
	}
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("DESIGNER", new MTGProperty(System.getProperty("user.name"), "The name of the designer"));
	}
	
	@Override
	public MOD getMode() {
		return MOD.FILE;
	}

	@Override
	public String getName() {
		return "MTGCardSmith";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
}
