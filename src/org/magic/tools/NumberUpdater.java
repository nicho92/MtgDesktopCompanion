package org.magic.tools;

import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.api.providers.impl.MtgjsonProvider;


//TEST
public class NumberUpdater {

	public static void main(String[] args) throws Exception {
		for(MagicCard mc : NumberUpdater.updateNumber("3ED", new MtgjsonProvider()))
		{
			System.out.println(mc.getNumber() +" " + mc.getName());
		}
		
	}
	
	
	public static List<MagicCard> updateNumber(String ed,MagicCardsProvider prov) throws Exception {
		
	
		String unavailableEds[] = {"LEA","LEB","ARN","2ED","ATQ","3ED","FEM","4ED","ICE","CHR","HML","ALL","RQS","MIR","MGB","ITP","5ED","POR","VAN","WTH","TMP","STH","PO2","ATH","BRB","S00","DDQ"};
		List<MagicCard> editionsCards = prov.searchCardByCriteria("set",ed,null);
		
		int index=1;
		for(MagicCard mc : editionsCards)
			if(mc.getColors().size()==1 && mc.getColors().contains("White"))
				mc.setNumber(String.valueOf(index++));
		
		for(MagicCard mc : editionsCards)
			if(mc.getColors().size()==1 && mc.getColors().contains("Blue"))
				mc.setNumber(String.valueOf(index++));
		
		for(MagicCard mc : editionsCards)
			if(mc.getColors().size()==1 && mc.getColors().contains("Black"))
				mc.setNumber(String.valueOf(index++));
		
		for(MagicCard mc : editionsCards)
			if(mc.getColors().size()==1 && mc.getColors().contains("Red"))
				mc.setNumber(String.valueOf(index++));
		
		for(MagicCard mc : editionsCards)
			if(mc.getColors().size()==1 && mc.getColors().contains("Green"))
				mc.setNumber(String.valueOf(index++));
		
		for(MagicCard mc : editionsCards)
			if(mc.getColors().size()>1)
				mc.setNumber(String.valueOf(index++));
		
		for(MagicCard mc : editionsCards)
			if(mc.getColors().size()==0 && mc.getTypes().get(0).equals("Artifact"))
				mc.setNumber(String.valueOf(index++));
		
		for(MagicCard mc : editionsCards)
			if(mc.getColors().size()==0 && mc.getTypes().get(0).equals("Land"))
				mc.setNumber(String.valueOf(index++));
		
		
		return editionsCards;
	}
}
