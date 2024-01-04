package org.beta;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JFileChooser;

import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGControler;
import org.magic.services.TransactionService;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class TransactionImport {

	
	
	public static void main(String[] args) throws SQLException, IOException {
		
		MTGControler.getInstance().init();
		
		
		var choose = new JFileChooser();
		choose.showOpenDialog(null);
		File importFile = choose.getSelectedFile();
		
		var transaction = MTG.getEnabledPlugin(MTGDao.class).getTransaction(291L);
		var set = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById("3ED");
		var list = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByEdition(set);
		
		FileTools.readAllLines(importFile).forEach(s->{
			
			var line = s.split(";");
			var card = list.stream().filter(mc->mc.getCurrentSet().getNumber().equals(line[1])).findFirst().get();
			var stock = MTGControler.getInstance().getDefaultStock();
				
				stock.setProduct(card);
				stock.setQte(1);
				stock.setLanguage("French");
				stock.setPrice(UITools.parseDouble(line[2]));
				stock.setMagicCollection(new MagicCollection("Library"));
				stock.setCondition(EnumCondition.NEAR_MINT);
				
				transaction.getItems().add(stock);
				
				
				
		});
		TransactionService.saveTransaction(transaction, false);
		System.exit(0);
		
		
		
		
		
	}
}
