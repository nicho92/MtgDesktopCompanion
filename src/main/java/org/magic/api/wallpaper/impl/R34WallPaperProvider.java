package org.magic.api.wallpaper.impl;

import com.google.gson.JsonObject;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import org.magic.api.beans.MTGWallpaper;
import org.magic.api.interfaces.abstracts.extra.AbstractJsonWallpaperProvider;
import org.magic.services.network.RequestBuilder;

public class R34WallPaperProvider extends AbstractJsonWallpaperProvider {

	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("API_KEY", "USER_ID");
	}

	@Override
	protected int getResultsPerPage() {
		return 1000;
	}

	@Override
	protected RequestBuilder createQuery(String search, int pidStart) {

		var s = search.toLowerCase();

		if (search.indexOf("+") > -1) {
			var builder = new StringBuilder();
			for (var st : search.split("\\+"))
				builder.append(st.trim().toLowerCase().replace(" ", "_")).append(" ");

			s = builder.toString();
		} else {
			s = search.replace(" ", "_");
		}

		return RequestBuilder.build().newClient().url("https://api.rule34.xxx/index.php").get()
				.addContent("page", "dapi").addContent("tags", s)
				.addContent("limit", String.valueOf(getResultsPerPage()))
				.addContent(getPaginationKey(), String.valueOf(pidStart)).addContent("json", "1")
				.addContent("s", "post").addContent("q", "index")
				.addContent("api_key", getAuthenticator().get("API_KEY"))
				.addContent("user_id", getAuthenticator().get("USER_ID"));

	}

	@Override
	protected String getPaginationKey() {
		return "pid";
	}

	@Override
	public String getName() {
		return "Rule34";
	}

	@Override
	protected MTGWallpaper parse(JsonObject obj) {
		var wall = new MTGWallpaper();

		wall.setProvider(getName());
		wall.setAuthor(obj.get("owner").getAsString());
		wall.setMature(obj.get("rating").getAsString().equalsIgnoreCase("explicit"));
		wall.setName(obj.get("id").getAsString());
		wall.setUrl(URI.create(obj.get("file_url").getAsString()));
		wall.setUrlThumb(URI.create(obj.get("preview_url").getAsString()));
		wall.setFormat(obj.get("image").getAsString().substring(obj.get("image").getAsString().indexOf(".") + 1));
		wall.setPublishDate(new Date(obj.get("change").getAsLong() * 1000));
		Stream.of(obj.get("tags").getAsString().split(" ")).forEach(wall.getTags()::add);
		return wall;
	}

}
