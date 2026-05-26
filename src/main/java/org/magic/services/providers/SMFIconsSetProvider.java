package org.magic.services.providers;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumRarity;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.tools.MTG;

public class SMFIconsSetProvider {

	private HashMap<String, String> map;

	public SMFIconsSetProvider() {
		map = new HashMap<>();
		map.put("LEA", "base/alpha");
		map.put("LEB", "base/beta");
		map.put("2ED", "base/unlimited");
		map.put("3ED", "base/revised");
		map.put("4ED","base/4e");
		map.put("5ED","base/5e");
		map.put("6ED","base/6e");
		map.put("7ED","base/7e");
		map.put("8ED","base/8e");
		map.put("9ED","base/9e");
		map.put("10E","base/10e");
		map.put("M10","base/m10");
		map.put("M11","base/m11");
		map.put("M12","base/m12");
		map.put("M13","base/m13");
		map.put("M14","base/m14");
		map.put("M15","base/m15");
		map.put("ORI", "base/magic-origins");
		
		map.put("ARN", "first-extensions/arabian-nights");
		map.put("ATQ", "first-extensions/antiquities");
		map.put("LEG", "first-extensions/legends");
		map.put("DRK", "first-extensions/thedark");
		map.put("FEM", "first-extensions/fallenempires");
		map.put("HML", "first-extensions/homelands");
		
		map.put("ICE", "first-extensions/ice-age");
		map.put("ALL", "first-extensions/alliances");
		map.put("CSP", "first-extensions/coldsnap");
		 
		map.put("MIR", "mirage/mirage");
		map.put("VIS", "mirage/visions");
		map.put("WTH", "mirage/weatherlight");
				
		map.put("TMP", "tempest/tempest");
		map.put("STH", "tempest/stronghold");
		map.put("EXO", "tempest/exodus");
		
		map.put("USG", "urza/urzassaga");
		map.put("ULG", "urza/urzaslegacy");
		map.put("UDS", "urza/urzasdestiny");
		
		map.put("MMQ", "masques/mercadian-masques");
		map.put("NEM", "masques/nemesis");
		map.put("PCY", "masques/prophecy");
		
		map.put("INV", "invasion/invasion");
		map.put("PLS", "invasion/planeshift");
		map.put("APC", "invasion/apocalypse");
		
		map.put("ODY", "odyssey/odyssey");
		map.put("TOR", "odyssey/torment");
		map.put("JUD", "odyssey/judgment");
		
		map.put("ONS", "onslaught/onslaught");
		map.put("LGN", "onslaught/legions");
		map.put("SCG", "onslaught/scourge");
		
		map.put("MRD", "mirrodin/mirrodin");
		map.put("DST", "mirrodin/darksteel");
		map.put("5DN", "mirrodin/fifth-dawn");
	}
	
	public HashMap<String, String> getMap() {
		return map;
	}
	
	
	public List<MTGEdition> availableSets()
	{
		return map.keySet().stream().map(id->{
			try {
				return MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(id);
			} catch (IOException _) {
				return null;
			}
		}).filter(Objects::nonNull).sorted().toList();
	}
	
	public URI getSetFor(MTGEdition ed, EnumRarity r) {
		return getSetFor(ed.getId(), r);
	}
	
	public URI getSetFor(String setCode, EnumRarity r) {
		return URI.create("https://funcardmaker.thaledric.fr/resource/seThumb/"+map.get(setCode)+"-"+r.name().toLowerCase().charAt(0)+".png");
	}

	
}
