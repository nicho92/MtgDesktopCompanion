package org.beta;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGConstants;

public class CardCastleExport extends AbstractCardExport {

	private String header="Count,Card Name,Set Name,Foil";
	private char separator=',';
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		StringBuilder build = new StringBuilder();
		build.append(header).append("\n");
		
		deck.getMap().entrySet().forEach(entry->{
			
			String name = entry.getKey().getName();
			
			if(name.contains(","))
				name="\""+name+"\"";
			
			
			build.append(entry.getValue()).append(separator);
			build.append(name).append(separator);
			build.append(entry.getKey().getCurrentSet().getSet()).append(separator);
			build.append("false").append("\n");
		});
		FileUtils.write(dest, build.toString(),MTGConstants.DEFAULT_ENCODING);
	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "CardCastle";
	}

}
