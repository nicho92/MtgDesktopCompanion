package org.beta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.Wallpaper;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.tools.URLTools;
import org.magic.tools.URLToolsClient;

public class TestImgUr extends AbstractWallpaperProvider {

	public static void main(String[] args) throws IOException {
		
		
	}

	@Override
	public List<Wallpaper> search(String search) {
		
		
		List<Wallpaper> p = new ArrayList<>();
		URLToolsClient c = URLTools.newClient();
		Map<String,String> m = new HashMap<>();
		m.put("Authorization","Client-ID "+getString("CLIENTID"));
		
		try {
			String s= c.doGet("https://api.imgur.com/3/gallery/search/time/all/1?q=liliana", m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}


	@Override
	public String getName() {
		return "Imgur";
	}

	@Override
	public void initDefault() {
		setProperty("CLIENTID", "");
	}
}
