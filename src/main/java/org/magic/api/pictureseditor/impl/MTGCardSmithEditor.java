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
	private final String urlBuilder=BASE_URL+"/src/actions/m15card2";
	private final String urlPictureUpload=BASE_URL+"/src/actions/cards/upload";
	private final String urlAuthentication="https://shop.mtgcardsmith.com/my-account/?redirect_to="+BASE_URL;
	
	private MTGHttpClient client;
	private boolean connected;
	private Map<String,String> layout;
	
	@Override
	public BufferedImage getPicture(MTGCard mc, MTGEdition me) throws IOException {
		
		connect();
		
		var imgPath = uploadPicture(new File(mc.getUrl()));
		var build = RequestBuilder.build().url(urlBuilder+"?fromAjax=1&v=3").setClient(client).post()
				.addHeader("x-requested-with", "XMLHttpRequest")
				.addHeader(URLTools.REFERER, BASE_URL+"/mtg-card-maker/edit")
				.addHeader(URLTools.HOST, "mtgcardsmith.com")
				.addHeader(URLTools.ORIGIN, BASE_URL)
				.addHeader(URLTools.ACCEPT, "application/json, text/javascript, */*; q=0.01")
				.addHeader(URLTools.ACCEPT_ENCODING, "gzip, deflate, br, zstd")
				.addHeader(URLTools.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8")
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
				.addContent("title_color","#000000")
				.addContent("custom_mana",mc.getCost().toLowerCase())
				.addContent("watermark","")
				.addContent("frame_color[]",EnumColors.determine(mc.getColors()).toPrettyString().toLowerCase())
				.addContent("special_card_color","")
				.addContent("pos_art_x",mc.getCustomMetadata().get(AbstractPicturesEditorProvider.X)==null?"0":mc.getCustomMetadata().get(AbstractPicturesEditorProvider.X))
				.addContent("pos_art_y",mc.getCustomMetadata().get(AbstractPicturesEditorProvider.Y)==null?"0":mc.getCustomMetadata().get(AbstractPicturesEditorProvider.Y))
				.addContent("pos_art_s",mc.getCustomMetadata().get(AbstractPicturesEditorProvider.ZOOM)==null?"100":mc.getCustomMetadata().get(AbstractPicturesEditorProvider.ZOOM))
				.addContent("subtype_color","#000000")
				.addContent("type",mc.getSupertypes().stream().collect(Collectors.joining(" ")) + " " + mc.getTypes().stream().collect(Collectors.joining(" ")))
				.addContent("custom_type","")
				.addContent("subtype",mc.getSubtypes().stream().collect(Collectors.joining(" ")))
				.addContent("rarity",mc.getRarity().toPrettyString().toLowerCase())
				.addContent("set_icon","mtgcs1")
				.addContent("body_color","#000000")
				.addContent("text_size","large")
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
				.addContent("image_path",imgPath);
				
		
		if(mc.isCreature())
			build.addContent("showPT","true");

		
		if(mc.isLegendary())
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

		if(EnumColors.determine(mc.getColors())==EnumColors.UNCOLOR)
			build.addContent("frame_color[]", "colorless");


		
		if(EnumColors.determine(mc.getColors())==EnumColors.GOLD)
			build.addContent("frame_rare", "/moderator/tmp/custom_666ba6254de65.png");
		
		
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
		
		var ret = layout.get("normal-"+color);
		
		if(mc.isBorderLess())
			return layout.get("borderless-"+color);
		
		
		
		if(mc.isLand())
			ret = layout.get("normal-land");
		else
			ret = layout.get("normal-"+color);

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
		layout.put("normal-uncolor", "custom_666b64b05d996.jpg");
		layout.put("normal-land", "custom_666b64d958319.jpg");
		layout.put("normal-gold", "custom_666ba621b8c9d.png");
		
		layout.put("borderless-white", "custom_66637c40bf2a2.png");
		layout.put("borderless-blue", "custom_6663822aa3c55.png");
		layout.put("borderless-black", "custom_6663830c90efd.png");
		layout.put("borderless-red", "custom_6663833f965ba.png");
		layout.put("borderless-green", "custom_666388ec7e198.png");
		layout.put("borderless-gold", "custom_66638b4a98a6e.png");
		layout.put("borderless-uncolor","custom_66638ba07c65e.png");
		layout.put("borderless-land","custom_66638ca862a35.png");
	
	}
	
	

	private void connect() throws IOException
	{
		if(connected)
			return;
		
		if(getAuthenticator().getLogin().isEmpty() || getAuthenticator().getPassword().isEmpty())
		{
			throw new IOException("Please fill LOGIN/PASSWORD field in account panel");
		}
		
		
		var nonce = RequestBuilder.build().url("https://shop.mtgcardsmith.com/my-account").setClient(client).get().toHtml().getElementById("woocommerce-login-nonce").attr("value");
		
		RequestBuilder.build().url(urlAuthentication).setClient(client).post()
																							.addContent("username", getAuthenticator().getLogin())
																							.addContent("password", getAuthenticator().getPassword())
																							.addContent("_wp_http_referer", "/my-account/?redirect_to="+BASE_URL)
																							.addContent("login", "Log in")
																							.addContent("rememberme", "forever")
																							.addContent("woocommerce-login-nonce", nonce)
																							.addContent("redirect", BASE_URL)
																							.execute();
		try 
		{
			var c = RequestBuilder.build().clean().url(BASE_URL+"/account/profile").setClient(client).get().toHtml().select("a[Title=Home]").first().text();
			logger.info("logged as {}", c);
			connected=true;
		}
		catch(Exception e)
		{
			logger.error("can't connect");
			connected=false;
		}

		
	}
	
	private String uploadPicture(File f) throws IOException
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
												.addContent("slim[]", generateJsonData(f).toString())
												.toHtml();
		
		var imgPath = res.select("img.previewImg2").attr("src");
		
		logger.info("File {} uploaded at {}", f,imgPath);
		
		return imgPath ;
	}
	
	
	private JsonObject generateJsonData(File f) throws IOException
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
			content.get("output").getAsJsonObject().addProperty("width", img.getWidth());
			content.get("output").getAsJsonObject().addProperty("height", img.getHeight());
			content.get("output").getAsJsonObject().addProperty("image", "data:image/png;base64,"+CryptoUtils.toBase64(binary));			
			
			content.get("actions").getAsJsonObject().get("crop").getAsJsonObject().addProperty("width", img.getWidth());
			content.get("actions").getAsJsonObject().get("crop").getAsJsonObject().addProperty("height", img.getHeight());
			
			content.get("actions").getAsJsonObject().get("size").getAsJsonObject().addProperty("width", img.getWidth());
			content.get("actions").getAsJsonObject().get("size").getAsJsonObject().addProperty("height", img.getHeight());
			
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
