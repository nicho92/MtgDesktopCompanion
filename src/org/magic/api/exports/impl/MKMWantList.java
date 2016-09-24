package org.magic.api.exports.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MagicFactory;

public class MKMWantList extends AbstractCardExport {

	/**
	 * todo CSP : remove Magic the gathering -
	 * todo TSP : remove timeshifted label
	 * */
	
	
	public MKMWantList() {
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			save();
		}
	}
	
	@Override
	public MagicDeck importDeck(File f) throws Exception {
		throw new Exception(getName() + " can't generate deck");
	}

	@Override
	public void export(List<MagicCard> cards, File f) throws Exception {
		BufferedWriter bw;
		FileWriter out;
		out = new FileWriter(f);
		bw=new BufferedWriter(out);
		for (MagicCard mc : cards){
			StringBuffer temp = new StringBuffer();
			
			temp.append("1").append(" ").append(mc.getName()).append(" (").append(mc.getEditions().get(0).getSet()).append(")");
			bw.write(temp.toString()+"\n");
		}
		bw.close();
		out.close();
	}

	@Override
	public String getFileExtension() {
		return ".txt";
	}

	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		BufferedWriter bw;
		FileWriter out;
		out = new FileWriter(dest);
		bw=new BufferedWriter(out);
		for (MagicCard mc : deck.getMap().keySet()){
			StringBuffer temp = new StringBuffer();
			temp.append(deck.getMap().get(mc)).append(" ").append(mc.getName()).append(" (").append(mc.getEditions().get(0).getSet()).append(")");
			bw.write(temp.toString()+"\n");
		}
		for (MagicCard mc : deck.getMapSideBoard().keySet()){
			StringBuffer temp = new StringBuffer();
			temp.append(deck.getMapSideBoard().get(mc)).append(" ").append(mc.getName()).append(" (").append(mc.getEditions().get(0).getSet()).append(")");
			bw.write(temp.toString()+"\n");
		}
		bw.close();
		out.close();

	}

	@Override
	public String getName() {
		return "MKM Want List";
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(MKMWantList.class.getResource("/res/mkm.png"));
	}

}
