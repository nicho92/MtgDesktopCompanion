package org.magic.api.pictureseditor.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.magic.api.ast.engine.OracleParser;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumExtraCardMetaData;
import org.magic.api.beans.enums.EnumSecurityStamp;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractPicturesEditorProvider;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.providers.SMFIconsSetProvider;
import org.magic.services.tools.ImageTools;

public class FunCardMakerPicturesProvider extends AbstractPicturesEditorProvider {

	private static final String LAYOUT_OLD_MODERN = "LAYOUT_OLD_MODERN";
	private static final String DOMAIN = "funcardmaker.thaledric.fr";
	private static final String WEBSITE = "https://" + DOMAIN;
	private static final String GENERATE_URL = WEBSITE + "/generate.php";
	private static final String UPLOAD_URL = WEBSITE + "/upload.php";

	private MTGHttpClient httpclient;

	@Override
	public MOD getMode() {
		return MOD.FILE;
	}

	private void connect() {
		httpclient = URLTools.newClient();

	}

	@Override
	public BufferedImage getPicture(MTGCard mc, MTGEdition me) throws IOException {
		if (httpclient == null)
			connect();

		RequestBuilder build = RequestBuilder.build();

		build.post().url(GENERATE_URL).setClient(httpclient).addContent("width", "791").addContent("height", "1107")
				.addContent("fields[title]", mc.getName()).addContent("fields[type]", mc.getFullType())
				.addContent("fields[capa]", mc.getText()).addContent("fields[ta]", mc.getFlavor())
				.addContent("fields[illustrator]", mc.getArtist())
				.addContent("fields[copyright]", getString("COPYRIGHT")).addContent("fields[cm]", mc.getCost());

		postEditing(mc);

		if (mc.isPlaneswalker()) {
			var abs = OracleParser.toFacade(mc.getName(), mc.getText()).getPlaneswalkerAbilities();
			build.addContent("template", "modern-planeswalker" + abs.size());
			build.addContent("fields[loyalty-base]", String.valueOf(mc.getLoyalty()));
			for (var i = 0; i < abs.size(); i++) {
				build.addContent("fields[capa" + (i + 1) + "-cost]", abs.get(i).loyalty());
				build.addContent("fields[capa" + (i + 1) + "]", abs.get(i).text());
			}
		} else {
			build.addContent("template", getString(LAYOUT_OLD_MODERN).toLowerCase() + "-basic");
		}

		String colorBase;

		if (mc.isArtifact())
			colorBase = "a";
		else if (mc.isLand())
			colorBase = "l";
		else if (mc.getColors().isEmpty())
			colorBase = "c";
		else {
			if (mc.getColors().size() == 1)
				colorBase = mc.getColors().get(0).getCode();
			else if (mc.getColors().size() > 1)
				colorBase = "m";
			else
				colorBase = mc.getColors().get(0).getCode();
		}

		if (mc.isHybride() || !mc.getCustomMetadata().getOrDefault(EnumExtraCardMetaData.ACCENT, "").isEmpty()) {
			if (mc.getCustomMetadata().get(EnumExtraCardMetaData.ACCENT).length() == 2) {
				colorBase = new StringBuilder()
						.append(mc.getCustomMetadata().get(EnumExtraCardMetaData.ACCENT).toCharArray()[0]).append("/")
						.append(mc.getCustomMetadata().get(EnumExtraCardMetaData.ACCENT).toCharArray()[1]).toString();
			} else {
				colorBase = mc.getCustomMetadata().get(EnumExtraCardMetaData.ACCENT);
			}
		}

		if (mc.isLand()) {
			if (mc.getCustomMetadata().get(EnumExtraCardMetaData.ACCENT).length() == 2) {
				colorBase = new StringBuilder("l")
						.append(mc.getCustomMetadata().get(EnumExtraCardMetaData.ACCENT).toCharArray()[0]).append("/l")
						.append(mc.getCustomMetadata().get(EnumExtraCardMetaData.ACCENT).toCharArray()[1]).toString();
			} else {
				colorBase = "l" + mc.getCustomMetadata().get(EnumExtraCardMetaData.ACCENT);
			}

		}

		build.addContent("fields[background-base]", colorBase.toLowerCase());
		build.addContent("fields[background-texture]", colorBase.toLowerCase());

		if (mc.isCreature() || mc.isVehicule())
			build.addContent("fields[fe]", mc.getPower() + "/" + mc.getToughness());

		if (mc.getRarity() != null)
			build.addContent("fields[se-rarity]", mc.getRarity().name().substring(0, 1).toLowerCase());

		if (mc.getUrl() != null) {
			var f = toFile(mc.getUrl(), mc.getName() + ".png");

			if (f.exists()) {
				String filename = upload(f);
				build.addContent("fields[illustration]", filename);
			}
		}

		if (Boolean.parseBoolean(mc.getCustomMetadata().getOrDefault(EnumExtraCardMetaData.SHOW_SET_ICON, "false")))
			build.addContent("fields[se-extension]", new SMFIconsSetProvider().getMap().get(me.getId()));

		mc.getCustomMetadata().put(EnumExtraCardMetaData.PLUGIN_NAME, getName());

		build.addHeader(URLTools.HOST, DOMAIN).addHeader(URLTools.ORIGIN, WEBSITE).addHeader(URLTools.REFERER, WEBSITE);

		logger.trace(build);

		String ret = httpclient.toString(httpclient.execute(build));
		logger.trace("RESPONSE: {}", ret);

		var el = URLTools.toJson(ret);

		return ImageTools.readBase64(el.getAsJsonObject().get("image").getAsString());
	}

	private void postEditing(MTGCard mc) {
		if (getString(LAYOUT_OLD_MODERN).equalsIgnoreCase("old"))
			mc.setFrameVersion("1993");
		else
			mc.setFrameVersion("2003");

		mc.setSecurityStamp(EnumSecurityStamp.NONE);

	}

	private String upload(File f) throws IOException {
		if (httpclient == null)
			connect();

		var builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("fcm-file-media", new FileBody(f, ContentType.DEFAULT_BINARY));
		builder.addTextBody("fcm-field-illuscrop-x", "0");
		builder.addTextBody("fcm-field-illuscrop-y", "0");
		builder.addTextBody("fcm-field-illuscrop-w", "46");
		builder.addTextBody("fcm-field-illuscrop-h", "7");
		builder.addTextBody("MAX_FILE_SIZE", "104857600");

		HttpEntity ent = builder.build();

		var response = URLTools.toJson(httpclient.toString(httpclient.doPost(UPLOAD_URL, ent,
				Map.of("Host", DOMAIN, "Origin", WEBSITE, "Referer", WEBSITE, "X-Requested-With", "XMLHttpRequest"))));
		logger.trace("response: {}", response);

		if (response.getAsJsonObject().get("error") != null)
			throw new IOException(response.getAsJsonObject().get("error").getAsString());

		return response.getAsJsonObject().get("filepath").getAsString();
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("COPYRIGHT", new MTGProperty("(c)2026-Wizards of the coast", "Bottom card information"),
				LAYOUT_OLD_MODERN, new MTGProperty("modern", "choose the layout of the card", "old", "modern"));
	}

	@Override
	public String getName() {
		return "FunCardMaker";
	}

	@Override
	public String getVersion() {
		return "0.4.1-alpha";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null)
			return false;

		return hashCode() == obj.hashCode();
	}

}
