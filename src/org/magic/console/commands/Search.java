package org.magic.console.commands;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.magic.api.beans.MagicCard;
import org.magic.console.Command;
import org.magic.console.tables.ASCIITable;
import org.magic.console.tables.CollectionASCIITableAware;
import org.magic.console.tables.IASCIITableAware;
import org.magic.services.MagicFactory;


public class Search implements Command {

	Options opts;

	
	public Search() {
		opts = new Options();
		opts.addOption("a","attribute",false,"attribute to search");
		opts.addOption("v","value",false,"value to search");
	}
	
	@Override
	public void run(String[] array) throws Exception {
		CommandLine cl = parser.parse(opts, array);
		
		String att = "name",val="emrak";
		IASCIITableAware asciiTableAware = new CollectionASCIITableAware<MagicCard>(MagicFactory.getInstance().getEnabledProviders().searchCardByCriteria(att, val,null),"id","name", "editions[0].rarity");
		ASCIITable.getInstance(out).printTable(asciiTableAware);
	}

	@Override
	public void usage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "show", opts );
	}

	@Override
	public void quit() {
		// TODO Auto-generated method stub

	}

}
